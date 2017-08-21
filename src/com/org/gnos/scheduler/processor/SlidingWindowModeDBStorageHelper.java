package com.org.gnos.scheduler.processor;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.core.Block;
import com.org.gnos.core.Node;
import com.org.gnos.core.Pit;
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Field;
import com.org.gnos.db.model.Grade;
import com.org.gnos.db.model.PitGroup;
import com.org.gnos.db.model.Process;
import com.org.gnos.db.model.ProcessJoin;
import com.org.gnos.db.model.Product;
import com.org.gnos.db.model.ProductJoin;
import com.org.gnos.db.model.Stockpile;
import com.org.gnos.db.model.TruckParameterCycleTime;
import com.org.gnos.scheduler.equation.SPBlock;
import com.org.gnos.scheduler.equation.SlidingWindowExecutionContext;

public class SlidingWindowModeDBStorageHelper extends DBStorageHelper {

	private static String stockpile_inventory_sql = "";
	@Override
	public void processRecord(Record record){
		SlidingWindowExecutionContext swctx = (SlidingWindowExecutionContext)context;
		double tonnesWt = context.getUnScaledValue(record.getValue());
		if(record.getOriginType() == Record.ORIGIN_PIT) {
			swctx.addMinedTonnesWeightForBlock(record.getBlockNo(), tonnesWt);
		} else {
			swctx.reclaimTonnesWeightForStockpile(record.getOriginSpNo(), tonnesWt);
		}
		if(record.getDestinationType() == Record.DESTINATION_SP) {
			swctx.addTonnesWeightForStockpile(record.getDestSpNo(), record.getBlockNo(), tonnesWt);
		}
	}
	
	@Override
	public void postProcess() {
		SlidingWindowExecutionContext swctx = (SlidingWindowExecutionContext)context;
		swctx.processStockpiles();
		swctx.finalizeStockpiles(this);
	}
	
	@Override
	public void storeInReports(List<Record> records) {
		SlidingWindowExecutionContext swctx = (SlidingWindowExecutionContext)context;
		Map<Integer, PreparedStatement> stmts = new HashMap<Integer, PreparedStatement>();
		List<Field> fields = context.getFields();
		List<Expression> expressions = context.getExpressions();
		List<Product> productList = context.getProductList();
		List<ProductJoin> productJoinList = context.getProductJoinList();
		try ( PreparedStatement ips = conn.prepareStatement(report_insert_sql); ){
			boolean autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			for(Record record:records){
				try {
					int index = 1;
					BigDecimal total_TH = new BigDecimal(0);
					ips.setString(index++, context.getScenario().getName());
					ips.setInt(index++, 2); // 1- Global mode, 2 - SW mode
					ips.setInt(index++, record.getOriginType());
					ips.setInt(index++, record.getPitNo());
					ips.setInt(index++, record.getOriginSpNo());
					ips.setInt(index++, record.getBlockNo());
					ips.setInt(index++, record.getDestinationType());
					if(record.getDestinationType() == Record.DESTINATION_PROCESS) {
						Process process = context.getProcessByNumber(record.getProcessNo());
						ips.setString(index++, process.getModel().getName());
					} else if(record.getDestinationType() == Record.DESTINATION_SP) {
						Stockpile sp = context.getStockpileFromNo(record.getDestSpNo());
						ips.setString(index++, sp.getName());
					} else if(record.getDestinationType() == Record.DESTINATION_WASTE) {
						Dump dump = context.getDumpfromNo(record.getWasteNo());
						ips.setString(index++, dump.getName());
					}
					ips.setInt(index++, record.getTimePeriod());
					if(record.getOriginType() == Record.ORIGIN_PIT) {
						//System.out.println("Block No"+record.getBlockNo());
						Block b = context.getBlockByNumber(record.getBlockNo());
						double tonnesWt = context.getTonnesWtForBlock(b);
						double quantityMined = context.getUnScaledValue(record.getValue());
						double ratio = quantityMined/tonnesWt;
						ips.setDouble(index++, quantityMined);
						ips.setDouble(index++, ratio);
						
						if(record.getDestinationType() == Record.DESTINATION_PROCESS) {
							Process process = context.getProcessByNumber(record.getProcessNo());
							total_TH = total_TH.add(context.getTruckHourRatio(b, process.getModel().getName()));
						} else if(record.getDestinationType() == Record.DESTINATION_SP) {
							Stockpile sp = context.getStockpileFromNo(record.getDestSpNo());
							total_TH = total_TH.add(context.getTruckHourRatio(b, sp.getName()));
						} else if(record.getDestinationType() == Record.DESTINATION_WASTE) {
							Dump dump = context.getDumpfromNo(record.getWasteNo());
							total_TH = total_TH.add(context.getTruckHourRatio(b, dump.getName()));
						}
						if(record.getOriginType() == Record.ORIGIN_SP && record.getDestinationType() == Record.DESTINATION_PROCESS) {
							Stockpile sp = context.getStockpileFromNo(record.getOriginSpNo());
							Process process = context.getProcessByNumber(record.getProcessNo());
							TruckParameterCycleTime cycleTime =  context.getTruckParamCycleTimeByStockpileName(sp.getName());
							int payload = context. getBlockPayloadMapping().get(b.getId());
							if(payload > 0) {
								BigDecimal ct = new BigDecimal(0);
								if(cycleTime.getProcessData() != null){
									ct = cycleTime.getProcessData().get(process.getModel().getName()).add(context.getFixedTime());
								} 
								if(ct != null) {
									double th_ratio_val =  ct.doubleValue() /( payload* 60);
									total_TH = total_TH.add(new BigDecimal(th_ratio_val));
								}
							}
							 
							
						}
						total_TH  = total_TH.multiply(new BigDecimal(quantityMined));
						
						ips.setBigDecimal(index++, total_TH);

											
						for(Field f: fields) {
							if(f.getDataType() == Field.TYPE_GRADE) {
								String associatedFieldName = f.getWeightedUnit();
								BigDecimal value = new BigDecimal(b.getField(f.getName())).multiply(new BigDecimal(b.getField(associatedFieldName)));
								value = value.multiply(new BigDecimal(ratio));
								ips.setString(index, value.toString());
							}  else if(f.getDataType() == Field.TYPE_UNIT){
								BigDecimal value = new BigDecimal(b.getField(f.getName()));
								value = value.multiply(new BigDecimal(ratio));
								ips.setString(index, value.toString());
							} else {
								ips.setString(index, b.getField(f.getName()));
							}						
							
							index ++;
						}
						for(Expression expression: expressions) {
							if(expression.isGrade()) {
								String associatedFieldName = expression.getWeightedField();
								BigDecimal value = b.getComputedField(expression.getName()).multiply(new BigDecimal(b.getField(associatedFieldName)));
								value = value.multiply(new BigDecimal(ratio));
								ips.setString(index, value.toString());
							} else {
								BigDecimal value = b.getComputedField(expression.getName());
								value = value.multiply(new BigDecimal(ratio));
								ips.setString(index, value.toString());
							}				
							
							index ++;
						} 
						
						for(Product product: productList) {
							boolean associatedToProcess = false;
							if(record.getDestinationType() == Record.DESTINATION_PROCESS) {
								Process process = context.getProcessByNumber(record.getProcessNo());
								if(process.getModel().getId() == product.getModelId()) {
									associatedToProcess = true;
								}
							}
							if(associatedToProcess) {
								BigDecimal value = context.getProductValueForBlock(b, product);
								value = value.multiply(new BigDecimal(quantityMined));
								ips.setBigDecimal(index, value);
							} else {
								ips.setBigDecimal(index, new BigDecimal(0));
							}
							
							
							index ++;
						}
						
						for(ProductJoin productJoin: productJoinList) {
							Process process = null;
							if(record.getDestinationType() == Record.DESTINATION_PROCESS) {
								process = context.getProcessByNumber(record.getProcessNo());
							}
							BigDecimal value =  new BigDecimal(0);
							for(String productName :productJoin.getProductList()) {
								Product p = context.getProductFromName(productName);
								if(process !=null && process.getModel().getId() == p.getModelId()) {
									value = value.add(context.getProductValueForBlock(b, p));
								}
								
							}
							value = value.multiply(new BigDecimal(quantityMined));
							ips.setBigDecimal(index, value);					
							index ++;
						}		
						
						for(ProcessJoin processJoin : context.getProcessJoinList()){
							boolean present = false;
							if(record.getDestinationType() == Record.DESTINATION_PROCESS) {
								Process process = context.getProcessByNumber(record.getProcessNo());
								for(int processNo :processJoin.getChildProcessList()) {
									
									if(processNo == process.getModel().getId()) {
										present = true;
										break;
									}
								}							
							} 
							if(present) {
								ips.setInt(index, 1);
							} else {
								ips.setInt(index, 0);
							}
							index ++;
						}
						for(PitGroup pitGroup : context.getPitGroups()){
							boolean present = false;
							if(record.getOriginType() == Record.ORIGIN_PIT) {
								Pit pit = context.getPits().get(record.getPitNo());
								for(String pitName :pitGroup.getListChildPits()) {							
									if(pitName.equals(pit.getPitName())) {
										present = true;
										break;
									}
								}
							}
							if(present) {
								ips.setInt(index, 1);
							} else {
								ips.setInt(index, 0);
							}
							index ++;
						}
						
						
						for(Product product : context.getProductList()){
							List<Grade> grades = context.getGradesForProduct(product.getName());
							for(Grade grade: grades) {
								BigDecimal value = new BigDecimal(0);
								boolean associatedToProcess = false;
								if(record.getDestinationType() == Record.DESTINATION_PROCESS) {
									Process process = context.getProcessByNumber(record.getProcessNo());
									if(process.getModel().getId() == product.getModelId()) {
										associatedToProcess = true;
									}
								}
								if(associatedToProcess) {
									if(grade.getType() == Grade.GRADE_FIELD) {
										for(Field f: fields) {
											if(f.getName().equals(grade.getMappedName())) {
												value = new BigDecimal(b.getField(f.getName())).multiply(new BigDecimal(b.getField(f.getWeightedUnit())));
												value = value.multiply(new BigDecimal(ratio));									
												break;
											}
										}
									} else if(grade.getType() == Grade.GRADE_EXPRESSION) {
										for(Expression expression: expressions) {
											if(expression.getName().equals(grade.getMappedName())) {
												String associatedFieldName = expression.getWeightedField();
												value = b.getComputedField(expression.getName());
												if(expression.getWeightedFieldType() == Expression.UNIT_EXPRESSION) {
													value = value.multiply(b.getComputedField(associatedFieldName));
												} else {
													value = value.multiply(new BigDecimal(b.getField(associatedFieldName)));
												}
												
												value = value.multiply(new BigDecimal(quantityMined));
												break;
											}
										}
									}
								}							
								ips.setString(index, value.toString());
								index ++;
							}
						}
						
						for(ProductJoin productJoin : context.getProductJoinList()){
							List<Product> products = new ArrayList<Product>();
							Set<String> productNames = productJoin.getProductList();
							Iterator<String> it = productNames.iterator();
							while(it.hasNext()) {
								String productName = it.next();
								products.add(context.getProductFromName(productName));
							}
							if(products.size() > 0) {
								List<Grade> grades = context.getGradesForProduct(products.get(0).getName());
								for(int i = 0; i < grades.size(); i++) {
									BigDecimal gradevalue = new BigDecimal(0);
									for(Product product: products) {
										boolean associatedToProcess = false;
										if(record.getDestinationType() == Record.DESTINATION_PROCESS) {
											Process process = context.getProcessByNumber(record.getProcessNo());
											if(process.getModel().getId() == product.getModelId()) {
												associatedToProcess = true;
											}
										}
										if(associatedToProcess) {
											List<Grade> productGrades = context.getGradesForProduct(product.getName());
											if(productGrades.size() < i+1 ) continue;
											Grade grade = productGrades.get(i);
											if(grade.getType() == Grade.GRADE_FIELD) {
												for(Field f: fields) {
													if(f.getName().equals(grade.getMappedName())) {
														String associatedFieldName = f.getWeightedUnit();											
														BigDecimal value = new BigDecimal(b.getField(f.getName())).multiply(new BigDecimal(b.getField(associatedFieldName)));
														value = value.multiply(new BigDecimal(ratio));
														gradevalue = gradevalue.add(value);									
														break;
													}
												}
											} else if(grade.getType() == Grade.GRADE_EXPRESSION) {
												for(Expression expression: expressions) {
													if(expression.getName().equals(grade.getMappedName())) {
														String associatedFieldName = expression.getWeightedField();
														BigDecimal value = b.getComputedField(expression.getName());
														if(expression.getWeightedFieldType() == Expression.UNIT_EXPRESSION) {
															value = value.multiply(b.getComputedField(associatedFieldName));
														} else {
															value = value.multiply(new BigDecimal(b.getField(associatedFieldName)));
														}												
														value = value.multiply(new BigDecimal(quantityMined));
														gradevalue = gradevalue.add(value);									
														break;
													}
												}
											}
										}									
									}
									
									ips.setString(index, gradevalue.toString());
									index ++;
								}
								
							}
						}
						
					} else {
						SPBlock spb = swctx.getSPBlock(record.getOriginSpNo());
						Stockpile sp = context.getStockpileFromNo(record.getOriginSpNo());
						double ratio = 0;
						double quantityMined = context.getUnScaledValue(record.getValue());
						if(spb.getLasttonnesWt() > 0) {
							ratio = quantityMined/spb.getLasttonnesWt();
						}
						ips.setDouble(index++, quantityMined);
						ips.setDouble(index++, ratio);
						
						if(record.getDestinationType() == Record.DESTINATION_PROCESS) {
							Process process = context.getProcessByNumber(record.getProcessNo());
							total_TH = total_TH.add(((SlidingWindowExecutionContext)context).getTruckHourRatio(spb, sp.getName(), process.getModel().getName()));
						} 
						if(record.getOriginType() == Record.ORIGIN_SP && record.getDestinationType() == Record.DESTINATION_PROCESS) {
							
							Process process = context.getProcessByNumber(record.getProcessNo());
							TruckParameterCycleTime cycleTime =  context.getTruckParamCycleTimeByStockpileName(sp.getName());
							int payload = spb.getPayload();
							if(payload > 0) {
								BigDecimal ct = new BigDecimal(0);
								if(cycleTime.getProcessData() != null){
									ct = cycleTime.getProcessData().get(process.getModel().getName()).add(context.getFixedTime());
								} 
								if(ct != null) {
									double th_ratio_val =  ct.doubleValue() /( payload* 60);
									total_TH = total_TH.add(new BigDecimal(th_ratio_val));
								}
							}
							 
							
						}
						total_TH  = total_TH.multiply(new BigDecimal(quantityMined));
						
						ips.setBigDecimal(index++, total_TH);
						
						for(Field f: fields) {
							if(f.getDataType() == Field.TYPE_GRADE) {
								String associatedFieldName = f.getWeightedUnit();
								BigDecimal value = spb.getField(f.getName()).multiply(spb.getField(associatedFieldName));
								ips.setString(index, value.toString());
							} else if(f.getDataType() == Field.TYPE_UNIT){
								BigDecimal value = spb.getField(f.getName());
								value = value.multiply(new BigDecimal(ratio));
								ips.setString(index, value.toString());
							} else {
								ips.setString(index, spb.getField(f.getName()).toString());
							}					
							
							index ++;
						}
						
						for(Expression expression: expressions) {
							if(expression.isGrade()) {
								String associatedFieldName = expression.getWeightedField();
								BigDecimal value = spb.getComputedField(expression.getName()).multiply(spb.getField(associatedFieldName));
								value = value.multiply(new BigDecimal(ratio));
								ips.setString(index, value.toString());
							} else {
								BigDecimal value = spb.getComputedField(expression.getName());
								value = value.multiply(new BigDecimal(ratio));
								ips.setString(index, value.toString());
							}				
							
							index ++;
						}
						
						for(Product product: productList) {
							boolean associatedToProcess = false;
							if(record.getDestinationType() == Record.DESTINATION_PROCESS) {
								Process process = context.getProcessByNumber(record.getProcessNo());
								if(process.getModel().getId() == product.getModelId()) {
									associatedToProcess = true;
								}
							}
							if(associatedToProcess) {
								BigDecimal value = ((SlidingWindowExecutionContext)context).getProductValueForBlock(spb, product);
								value = value.multiply(new BigDecimal(quantityMined));
								ips.setBigDecimal(index, value);
							} else {
								ips.setBigDecimal(index, new BigDecimal(0));
							}
							
							
							index ++;
						}
						
						for(ProductJoin productJoin: productJoinList) {
							Process process = null;
							if(record.getDestinationType() == Record.DESTINATION_PROCESS) {
								process = context.getProcessByNumber(record.getProcessNo());
							}
							BigDecimal value =  new BigDecimal(0);
							for(String productName :productJoin.getProductList()) {
								Product p = context.getProductFromName(productName);
								if(process !=null && process.getModel().getId() == p.getModelId()) {
									value = value.add(((SlidingWindowExecutionContext)context).getProductValueForBlock(spb, p));
								}
								
							}
							value = value.multiply(new BigDecimal(quantityMined));
							ips.setBigDecimal(index, value);					
							index ++;
						}				
						for(ProcessJoin processJoin : context.getProcessJoinList()){
							boolean present = false;
							if(record.getDestinationType() == Record.DESTINATION_PROCESS) {
								Process process = context.getProcessByNumber(record.getProcessNo());
								for(int processNo :processJoin.getChildProcessList()) {
									
									if(processNo == process.getModel().getId()) {
										present = true;
										break;
									}
								}							
							} 
							if(present) {
								ips.setInt(index, 1);
							} else {
								ips.setInt(index, 0);
							}
							index ++;
						}
						for(PitGroup pitGroup : context.getPitGroups()){
							boolean present = false;
							if(record.getOriginType() == Record.ORIGIN_PIT) {
								Pit pit = context.getPits().get(record.getPitNo());
								for(String pitName :pitGroup.getListChildPits()) {							
									if(pitName.equals(pit.getPitName())) {
										present = true;
										break;
									}
								}
							}
							if(present) {
								ips.setInt(index, 1);
							} else {
								ips.setInt(index, 0);
							}
							index ++;
						}
						
						for(Product product : context.getProductList()){
							List<Grade> grades = context.getGradesForProduct(product.getName());
							for(Grade grade: grades) {
								BigDecimal value = new BigDecimal(0);
								boolean associatedToProcess = false;
								if(record.getDestinationType() == Record.DESTINATION_PROCESS) {
									Process process = context.getProcessByNumber(record.getProcessNo());
									if(process.getModel().getId() == product.getModelId()) {
										associatedToProcess = true;
									}
								}
								if(associatedToProcess) {
									if(grade.getType() == Grade.GRADE_FIELD) {
										for(Field f: fields) {
											if(f.getName().equals(grade.getMappedName())) {
												value = spb.getField(f.getName()).multiply(spb.getField(f.getWeightedUnit()));
												value = value.multiply(new BigDecimal(ratio));									
												break;
											}
										}
									} else if(grade.getType() == Grade.GRADE_EXPRESSION) {
										for(Expression expression: expressions) {
											if(expression.getName().equals(grade.getMappedName())) {
												String associatedFieldName = expression.getWeightedField();
												value = spb.getComputedField(expression.getName());
												if(expression.getWeightedFieldType() == Expression.UNIT_EXPRESSION) {
													value = value.multiply(spb.getComputedField(associatedFieldName));
												} else {
													value = value.multiply(spb.getField(associatedFieldName));
												}
												
												value = value.multiply(new BigDecimal(quantityMined));
												break;
											}
										}
									}
								}							
								ips.setString(index, value.toString());
								index ++;
							}
						}
						
						for(ProductJoin productJoin : context.getProductJoinList()){
							List<Product> products = new ArrayList<Product>();
							Set<String> productNames = productJoin.getProductList();
							Iterator<String> it = productNames.iterator();
							while(it.hasNext()) {
								String productName = it.next();
								products.add(context.getProductFromName(productName));
							}
							if(products.size() > 0) {
								List<Grade> grades = context.getGradesForProduct(products.get(0).getName());
								for(int i = 0; i < grades.size(); i++) {
									BigDecimal gradevalue = new BigDecimal(0);
									for(Product product: products) {
										boolean associatedToProcess = false;
										if(record.getDestinationType() == Record.DESTINATION_PROCESS) {
											Process process = context.getProcessByNumber(record.getProcessNo());
											if(process.getModel().getId() == product.getModelId()) {
												associatedToProcess = true;
											}
										}
										if(associatedToProcess) {
											List<Grade> productGrades = context.getGradesForProduct(product.getName());
											if(productGrades.size() < i+1 ) continue;
											Grade grade = productGrades.get(i);
											if(grade.getType() == Grade.GRADE_FIELD) {
												for(Field f: fields) {
													if(f.getName().equals(grade.getMappedName())) {
														String associatedFieldName = f.getWeightedUnit();											
														BigDecimal value = spb.getField(f.getName()).multiply(spb.getField(associatedFieldName));
														value = value.multiply(new BigDecimal(ratio));
														gradevalue = gradevalue.add(value);									
														break;
													}
												}
											} else if(grade.getType() == Grade.GRADE_EXPRESSION) {
												for(Expression expression: expressions) {
													if(expression.getName().equals(grade.getMappedName())) {
														String associatedFieldName = expression.getWeightedField();
														BigDecimal value = spb.getComputedField(expression.getName());
														if(expression.getWeightedFieldType() == Expression.UNIT_EXPRESSION) {
															value = value.multiply(spb.getComputedField(associatedFieldName));
														} else {
															value = value.multiply(spb.getField(associatedFieldName));
														}												
														value = value.multiply(new BigDecimal(quantityMined));
														gradevalue = gradevalue.add(value);									
														break;
													}
												}
											}
										}									
									}
									
									ips.setString(index, gradevalue.toString());
									index ++;
								}
								
							}
						}
						
					}
					String processName = "";
					Process process = null;
					if(record.getDestinationType() == Record.DESTINATION_PROCESS) {
						process = context.getProcessByNumber(record.getProcessNo());
					}
					Node lastNode = null;
					int levels = context.getProcessTree().getLevels();
					for (int i = levels; i >= 1; i-- ) {
						
						if(process != null) {
							Node node = null;
							if(lastNode == null) {
								node = context.getProcessTree().getNodeByName(process.getModel().getName());						
							} else {
								node = lastNode.getParent();
							}
							lastNode = node;
							processName = node.getData().getName();
							
						}
						
						
						ips.setString(index, processName);
						index ++;
					}
					
					ips.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			}
			conn.commit();
			conn.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Set<Integer> keys = stmts.keySet();
			try {
				for(Integer key: keys){
					(stmts.get(key)).close();
				}
				
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void processStockpileInventory(int spNo, SPBlock spb) {
		SlidingWindowExecutionContext swctx = (SlidingWindowExecutionContext)context;
		List<Field> fields = context.getFields();
		try ( PreparedStatement ips = conn.prepareStatement(stockpile_inventory_sql); ){
			boolean autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			try {
				
				ips.setString(1, context.getScenario().getName());
				ips.setInt(2, spNo);
				ips.setInt(3, swctx.getCurrPeriod());
				ips.setDouble(4, spb.getTonnesWt());
				ips.setDouble(5, spb.getReclaimedTonnesWt());
				
				int index = 6;
									
				for(Field f: fields) {
					if(f.getDataType() == Field.TYPE_GRADE) {
						String associatedFieldName = f.getWeightedUnit();
						BigDecimal value = spb.getField(f.getName()).multiply(spb.getField(associatedFieldName));
						ips.setString(index, value.toString());
					} else if(f.getDataType() == Field.TYPE_UNIT){
						BigDecimal value = spb.getField(f.getName());
						ips.setString(index, value.toString());
					} else {
						ips.setString(index, spb.getField(f.getName()).toString());
					}					
					
					index ++;
				}
						
				ips.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				/*spb.setLasttonnesWt(spb.getTonnesWt());
				spb.setReclaimedTonnesWt(0);*/

			}
				
			conn.commit();
			conn.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void createStockpileInventory() throws SQLException {
		int projectId = context.getProjectId();
		dropStockpileInventory(projectId);
		
		StringBuffer sbuff_sql = new StringBuffer("insert into gnos_stockpile_inventory_"+projectId+" (scenario_name, sp_no, period, sp_tonnes_wt, rc_tonnes_wt ");
		StringBuffer sbuff = new StringBuffer(" ( ?, ?, ?, ?, ?");
		String  data_sql = "CREATE TABLE gnos_stockpile_inventory_"+projectId+" ( " +
				"scenario_name VARCHAR(50), " +
				" sp_no INT NOT NULL default -1," +
				" period INT NOT NULL default -1," +
				" sp_tonnes_wt double , " +
				" rc_tonnes_wt double ";
		
		
		for(Field f : context.getFields()){
			String name = f.getName();
			if(f.getDataType() == Field.TYPE_GRADE) {
				name = name+"_u";
			}
			if(f.getDataType() == Field.TYPE_GRADE || f.getDataType() == Field.TYPE_UNIT) {
				data_sql +=  ","+ name +" double ";
			} else {
				data_sql +=  ","+ name +" VARCHAR(50) ";
			}
			
			sbuff_sql.append("," + name);
			sbuff.append(", ?");
		}
		data_sql += " ); ";
		sbuff_sql.append(")");
		sbuff.append(")");
		stockpile_inventory_sql = sbuff_sql.toString() + " values " + sbuff.toString();
		System.out.println("Sql =>"+data_sql);
		try (
				Statement stmt = conn.createStatement();
			)
		{			
			stmt.executeUpdate(data_sql);
		} 		
	}
	
	
	private void dropStockpileInventory(int projectId) throws SQLException {
		String  data_table_sql = "DROP TABLE IF EXISTS gnos_stockpile_inventory_"+projectId+"; ";

		try (
				Statement stmt = conn.createStatement();
			)
		{
			stmt.executeUpdate(data_table_sql);
		} 
		
	}
}
