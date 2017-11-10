package com.org.gnos.scheduler.processor;

import java.math.BigDecimal;
import java.sql.Connection;
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
import com.org.gnos.core.Tree;
import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.CapexData;
import com.org.gnos.db.model.CapexInstance;
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Field;
import com.org.gnos.db.model.FixedOpexCost;
import com.org.gnos.db.model.Grade;
import com.org.gnos.db.model.Model;
import com.org.gnos.db.model.OpexData;
import com.org.gnos.db.model.PitGroup;
import com.org.gnos.db.model.Process;
import com.org.gnos.db.model.ProcessJoin;
import com.org.gnos.db.model.Product;
import com.org.gnos.db.model.ProductJoin;
import com.org.gnos.db.model.Stockpile;
import com.org.gnos.db.model.TruckParameterCycleTime;
import com.org.gnos.scheduler.equation.ExecutionContext;

public class DBStorageHelper implements IStorageHelper {

	protected Connection conn;
	protected ExecutionContext context;
	
	protected static String report_insert_sql ="";
	protected static String capex_report_insert_sql ="";
	
	@Override
	public void store(List<Record> records) {
		
		// Store for reports 
		storeInReports(records);
		int projectId = context.getProjectId();
		int scenarioId = context.getScenarioId();
		String insert_sql = "insert into gnos_result_"+projectId+"_"+scenarioId+" (origin_type, pit_no, block_no, sp_no, destination_type, destination) values (? , ?, ?, ?, ?, ?)";
		Map<Integer, PreparedStatement> stmts = new HashMap<Integer, PreparedStatement>();
		try ( PreparedStatement ips = conn.prepareStatement(insert_sql); ){
			boolean autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			for(Record record:records){
				processRecord(record);
				try {
					ips.setInt(1, record.getOriginType());
					if(record.getOriginType() == Record.ORIGIN_PIT) {
						ips.setInt(2, record.getPitNo());
						ips.setInt(3, record.getBlockNo());
						ips.setInt(4, -1);
					} else {
						ips.setInt(2, -1);
						ips.setInt(3, -1);
						ips.setInt(4, record.getOriginSpNo());
					}
					ips.setInt(5, record.getDestinationType());
					if(record.getDestinationType() == Record.DESTINATION_PROCESS) {
						ips.setInt(6, record.getProcessNo());
					} else if(record.getDestinationType() == Record.DESTINATION_SP) {
						ips.setInt(6, record.getDestSpNo());
					} else if(record.getDestinationType() == Record.DESTINATION_WASTE) {
						ips.setInt(6, record.getWasteNo());
					}
					
					ips.executeUpdate();
				} catch (SQLException e) {
					
				}
				PreparedStatement ps = stmts.get(record.getTimePeriod());
				if(ps == null) {
					ps = getUpdateStatement(record.getTimePeriod());
					stmts.put(record.getTimePeriod(), ps);
				}
				ps.setString(1, String.valueOf(context.getUnScaledValue(record.getValue())));
				if(record.getOriginType() == Record.ORIGIN_PIT) {
					ps.setInt(2, record.getPitNo());
					ps.setInt(3, record.getBlockNo());
					ps.setInt(4, -1);
				} else {
					ps.setInt(2, -1);
					ps.setInt(3, -1);
					ps.setInt(4, record.getOriginSpNo());
				}
				ps.setInt(5, record.getOriginType());
				ps.setInt(6, record.getDestinationType());
				if(record.getDestinationType() == Record.DESTINATION_PROCESS) {
					ps.setInt(7, record.getProcessNo());
				} else if(record.getDestinationType() == Record.DESTINATION_SP) {
					ps.setInt(7, record.getDestSpNo());
				} else if(record.getDestinationType() == Record.DESTINATION_WASTE) {
					ps.setInt(7, record.getWasteNo());
				}
				ps.executeUpdate();
				
			}
			conn.commit();
			conn.setAutoCommit(autoCommit);
			postProcess();			
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

	public void storeInReports(List<Record> records) {
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
					Block b = context.getBlockByNumber(record.getBlockNo());
					if(b == null) continue;
					double tonnesWt = context.getTonnesWtForBlock(b);				
					double quantityMined = context.getUnScaledValue(record.getValue());
					double ratio = quantityMined/tonnesWt;
					int year = context.getScenario().getStartYear() + record.getTimePeriod() - 1;
					BigDecimal total_TH = new BigDecimal(0);
					int index = 1;
					ips.setString(index++, context.getScenario().getName());
					ips.setInt(index++, 1); // 1- Global mode, 2 - SW mode
					ips.setInt(index++, record.getOriginType());
					ips.setInt(index++, record.getPitNo());
					ips.setInt(index++, record.getOriginSpNo());
					ips.setInt(index++, record.getBlockNo());
					ips.setInt(index++, record.getDestinationType());
					if(record.getDestinationType() == Record.DESTINATION_PROCESS) {
						Process process = context.getProcessByNumber(record.getProcessNo());
						if(record.getOriginType() == Record.ORIGIN_PIT ) {
							total_TH = total_TH.add(context.getTruckHourRatio(b, process.getModel().getName()));
						}						
						ips.setString(index++, process.getModel().getName());
					} else if(record.getDestinationType() == Record.DESTINATION_SP) {
						Stockpile sp = context.getStockpileFromNo(record.getDestSpNo());
						total_TH = total_TH.add(context.getTruckHourRatio(b, sp.getName()));
						ips.setString(index++, sp.getName());
					} else if(record.getDestinationType() == Record.DESTINATION_WASTE) {
						Dump dump = context.getDumpfromNo(record.getWasteNo());
						total_TH = total_TH.add(context.getTruckHourRatio(b, dump.getName()));
						ips.setString(index++, dump.getName());
					}
					ips.setDouble(index++, record.getTimePeriod());
					ips.setDouble(index++, quantityMined);
					ips.setDouble(index++, ratio);
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
							if(associatedFieldName == null || associatedFieldName.trim().length() == 0) {
								ips.setString(index, "0");
							} else {
								BigDecimal value = new BigDecimal(b.getField(f.getName())).multiply(new BigDecimal(b.getField(associatedFieldName)));
								value = value.multiply(new BigDecimal(ratio));
								ips.setString(index, value.toString());
							}
							
						} else if(f.getDataType() == Field.TYPE_UNIT){
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
							BigDecimal value = b.getComputedField(expression.getName());
							if(expression.getWeightedFieldType() == Expression.UNIT_EXPRESSION) {
								value = value.multiply(b.getComputedField(associatedFieldName));
							} else {
								value = value.multiply(new BigDecimal(b.getField(associatedFieldName)));
							}
							
							value = value.multiply(new BigDecimal(quantityMined));
							ips.setString(index, value.toString());
						} else {
							BigDecimal value = b.getComputedField(expression.getName());
							value = value.multiply(new BigDecimal(quantityMined));
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
					// Financial fields 
					
					double total_cost = 0;
					double total_revenue = 0;
					// Ore mining 
					double oreMiningCost = 0;
					if(record.getDestinationType() != Record.DESTINATION_WASTE && record.getOriginType() == Record.ORIGIN_PIT) {
						for(FixedOpexCost foc: context.getFixedOpexCostList()) {
							if(foc.getCostType() == FixedOpexCost.ORE_MINING_COST) {
								oreMiningCost = quantityMined * foc.getCostData().get(year).doubleValue();								
								break;
							}
						}				
					} 
					ips.setDouble(index++, -oreMiningCost);
					total_cost += oreMiningCost;
					// Waste mining
					double wasteMiningCost = 0;
					if(record.getDestinationType() == Record.DESTINATION_WASTE) {
						for(FixedOpexCost foc: context.getFixedOpexCostList()) {
							if(foc.getCostType() == FixedOpexCost.WASTE_MINING_COST) {
								wasteMiningCost = quantityMined * foc.getCostData().get(year).doubleValue();								
								break;
							}
						}				
					} 
					ips.setDouble(index++, -wasteMiningCost);
					total_cost += wasteMiningCost;
					// Stockpile cost
					double stockpilingCost = 0;
					if(record.getDestinationType() == Record.DESTINATION_SP) {
						for(FixedOpexCost foc: context.getFixedOpexCostList()) {
							if(foc.getCostType() == FixedOpexCost.STOCKPILING_COST) {
								stockpilingCost = quantityMined * foc.getCostData().get(year).doubleValue();								
								break;
							}
						}				
					} 
					ips.setDouble(index++, -stockpilingCost);
					total_cost += stockpilingCost;

					// Stockpile Reclaim 
					double stockpileReclaimingCost = 0;
					if(record.getOriginType() == Record.ORIGIN_SP) {
						for(FixedOpexCost foc: context.getFixedOpexCostList()) {
							if(foc.getCostType() == FixedOpexCost.STOCKPILE_RECLAIMING_COST) {
								stockpileReclaimingCost = quantityMined * foc.getCostData().get(year).doubleValue();								
								break;
							}
						}				
					}
					ips.setDouble(index++, -stockpileReclaimingCost);
					total_cost += stockpileReclaimingCost;
					//Truckhour cost
					double truckHourCost = 0;
					for(FixedOpexCost foc: context.getFixedOpexCostList()) {
						if(foc.getCostType() == FixedOpexCost.TRUCK_HOUR_COST) {
							truckHourCost = total_TH.doubleValue() * foc.getCostData().get(year).doubleValue();								
							break;
						}
					}
					ips.setDouble(index++, -truckHourCost);
					total_cost += truckHourCost;
					
					List<OpexData> opexDataList = context.getOpexDataList();
					for (OpexData opexData: opexDataList) {
						if(!opexData.isInUse()) continue;						
						if(opexData.isRevenue()) {
							double revenue = 0;	
							if(record.getDestinationType() == Record.DESTINATION_PROCESS) {
								boolean calRev = true;
								if(opexData.getModelId() != -1 && (opexData.getModelId() != process.getModel().getId())) {
									calRev = false;

								} else if(opexData.getProductJoinName() != null  && opexData.getProductJoinName().trim().length() > 0) {
									if(!isProcessAssociatedToProductJoin(process.getModel(), opexData.getProductJoinName())){
										calRev = false;
									}
								}
								if(calRev) {
									int unitId;
									if(opexData.getUnitType() == OpexData.UNIT_FIELD) {
										unitId = opexData.getFieldId();
									} else {
										unitId = opexData.getExpressionId();
									}
									BigDecimal expr_value = context.getUnitValueforBlock(b, unitId, opexData.getUnitType());
									revenue = expr_value.doubleValue()* quantityMined * opexData.getCostData().get(year).doubleValue();
								}
								
							}
							ips.setDouble(index++, revenue);
							total_revenue += revenue;
						} else {
							double pcost = 0;
							if(record.getDestinationType() == Record.DESTINATION_PROCESS) {
								if(opexData.getModelId() != -1 && (process.getModel().getId() == opexData.getModelId() || isChild(opexData.getModelId(), process.getModel()))) {
									pcost = opexData.getCostData().get(year).doubleValue() * quantityMined;									
								}
							}
							
							ips.setDouble(index++, -pcost);
							total_cost += pcost;
						}
					}

					ips.setDouble(index++, -total_cost);
					ips.setDouble(index++, total_revenue);
					ips.setDouble(index++, (total_revenue - total_cost));
					double discount_rate = context.getScenario().getDiscount()/100;
					double dcf = (total_revenue - total_cost) * (1 / Math.pow ((1 + discount_rate), record.getTimePeriod()));
					ips.setDouble(index++, dcf);
					
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
	
	@Override
	public void storeCapex(List<CapexRecord> capexRecords) {
		if(capexRecords == null || capexRecords.size() == 0) return;
		try ( PreparedStatement ips = conn.prepareStatement(capex_report_insert_sql); ){
			boolean autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			List<CapexData> cdList = context.getCapexDataList();
			
			for(CapexRecord cr: capexRecords) {
				int index = 1;
				int year = context.getScenario().getStartYear() + cr.getYear() - 1;
				ips.setInt(index++, year);
				List<Double> capexCosts = new ArrayList<Double>();
				double total_capex = 0;
				int ccount = 1;
				for(CapexData cd : cdList) {
					List<CapexInstance> ciList = cd.getListOfCapexInstances();
					int cicount = 1;
					for(CapexInstance ci : ciList) {
						double cost =0;
						if(ccount == cr.getCapexNo() && cicount == cr.getInstanceNo()) {
							ips.setInt(index++, cr.getValue());
							cost =  -cr.getValue() * ci.getCapexAmount();
						} else {
							ips.setInt(index++, 0);
						}
						total_capex += cost;
						capexCosts.add(cost);
						cicount++;
					}
					ccount++;
				}
				
				for(Double capexValue : capexCosts) {
					ips.setDouble(index++, capexValue);
				}
				ips.setDouble(index++, total_capex);
				double discount_rate = context.getScenario().getDiscount()/100;
				double dcf = total_capex * (1 / Math.pow ((1 + discount_rate), cr.getYear()));
				ips.setDouble(index++, dcf);
				
				ips.executeUpdate();
			}	
			conn.commit();
			conn.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
	public void processRecord(Record record){
		
	}
	
	public void postProcess(){
		
	}

	private void createTable() throws SQLException {
		int projectId = context.getProjectId();
		int scenarioId = context.getScenarioId();
		dropTable(projectId, scenarioId);
		String  data_sql = "CREATE TABLE gnos_result_"+projectId+"_"+scenarioId+" ( " +
				" origin_type TINYINT NOT NULL, " +
				" pit_no INT NOT NULL default -1," +
				" block_no INT  NOT NULL default -1," +
				" sp_no INT  NOT NULL default -1, " +
				" destination_type TINYINT NOT NULL, " +
				" destination INT, tonnage_wt VARCHAR(50), ";
		
		for(int i = 1; i<= context.getScenario().getTimePeriod(); i++){
			data_sql +=  "t"+i+" VARCHAR(50) , ";
		}
		data_sql += "  UNIQUE KEY (origin_type, pit_no, block_no, sp_no, destination_type, destination) );";
		
		System.out.println("Sql =>"+data_sql);
		try (
				Statement stmt = conn.createStatement();
			)
		{			
			stmt.executeUpdate(data_sql);
		} 
		
	}
	
	private void createCapexTable() throws SQLException {
		int projectId = context.getProjectId();
		int scenarioId = context.getScenarioId();
		dropCapexTable(projectId, scenarioId);
		StringBuffer sbuff_sql = new StringBuffer("insert into gnos_capex_report_"+projectId+"_"+scenarioId+" (period ");
		StringBuffer sbuff = new StringBuffer(" ( ?");
		String  data_sql = "CREATE TABLE gnos_capex_report_"+projectId+"_"+scenarioId+" ( period INT ";

		List<CapexData> cdList = context.getCapexDataList();
		List<String> columns = new ArrayList<String>();
		int ccount = 1;
		for(CapexData cd : cdList) {
			List<CapexInstance> ciList = cd.getListOfCapexInstances();
			int cicount = 1;
			for(CapexInstance ci : ciList) {
				String name = "capex"+ccount+"_int"+cicount;
				columns.add(name);
				data_sql +=  ","+ name +" int ";			
				sbuff_sql.append("," + name);
				sbuff.append(", ?");
				cicount++;
			}
			ccount++;
		}
		for(String column: columns) {
			String name = column +"_cost";
			data_sql +=  ","+ name +" double ";			
			sbuff_sql.append("," + name);
			sbuff.append(", ?");
		}
		
		String[] capex_heads = {"total_capex", "capex_dcf"};
		for(int i= 0; i< capex_heads.length; i ++){
			data_sql +=  ","+ capex_heads[i] +" double ";			
			sbuff_sql.append("," + capex_heads[i]);
			sbuff.append(", ?");
		}


		
		data_sql += " ); ";
		
		sbuff_sql.append(")");
		sbuff.append(")");
		capex_report_insert_sql = sbuff_sql.toString() + " values " + sbuff.toString();
		
		System.out.println("Sql =>"+data_sql);
		try (
				Statement stmt = conn.createStatement();
			)
		{			
			stmt.executeUpdate(data_sql);
		} 
		
	}
	
	private void dropCapexTable(int projectId, int scenarioId) throws SQLException {
		String  data_table_sql = "DROP TABLE IF EXISTS gnos_capex_report_"+projectId+"_"+scenarioId+"; ";

		try (
				Statement stmt = conn.createStatement();
			)
		{
			stmt.executeUpdate(data_table_sql);
		} 
		
		
	}

	private void createReportTable() throws SQLException {
		int projectId = context.getProjectId();
		int scenarioId = context.getScenarioId();
		
		dropReportTable(projectId, scenarioId);
		
		StringBuffer sbuff_sql = new StringBuffer("insert into gnos_report_"+projectId+"_"+scenarioId+" (scenario_name, mode, origin_type, pit_no, sp_no, block_no, destination_type, destination, period, quantity_mined, ratio, total_th ");
		StringBuffer sbuff = new StringBuffer(" ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ");
		String  data_sql = "CREATE TABLE gnos_report_"+projectId+"_"+scenarioId+" ( " +
				"scenario_name VARCHAR(50), " +
				" mode TINYINT NOT NULL, " +
				" origin_type TINYINT NOT NULL, " +			
				" pit_no INT NOT NULL default -1," +
				" sp_no INT NOT NULL default -1," +
				" block_no INT  NOT NULL default -1," +
				" destination_type TINYINT NOT NULL, " +
				" destination VARCHAR(50), " + 
				" period INT, " + 
				" quantity_mined VARCHAR(50) ," +
				" ratio double,  " +
				" total_th double ";
		
		
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
		
		for(Expression expression : context.getExpressions()){
			String name = expression.getName().replaceAll("\\s+", "_");
			if(expression.isGrade()) {
				name = name+"_u";
			}
			data_sql +=  ","+ name +" double ";			
			sbuff_sql.append("," + name);
			sbuff.append(", ?");
		}
		
		for(Product product : context.getProductList()){
			String name = product.getName().replaceAll("\\s+", "_");
			data_sql +=  ","+ name +" double ";			
			sbuff_sql.append("," + name);
			sbuff.append(", ?");
		}
		
		for(ProductJoin productJoin : context.getProductJoinList()){
			String name = productJoin.getName().replaceAll("\\s+", "_");
			data_sql +=  ","+ name +" double ";			
			sbuff_sql.append("," + name);
			sbuff.append(", ?");
		}
		
		for(ProcessJoin processJoin : context.getProcessJoinList()){
			String name = processJoin.getName().replaceAll("\\s+", "_");
			data_sql +=  ","+ name +" tinyint ";			
			sbuff_sql.append("," + name);
			sbuff.append(", ?");
		}
		
		for(PitGroup pitGroup : context.getPitGroups()){
			String name = pitGroup.getName().replaceAll("\\s+", "_");
			data_sql +=  ","+ name +" tinyint ";			
			sbuff_sql.append("," + name);
			sbuff.append(", ?");
		}
		
		for(Product product : context.getProductList()){
			String productName = product.getName().replaceAll("\\s+", "_");
			List<Grade> grades = context.getGradesForProduct(productName);
			for(Grade grade: grades ){
				String name = "`"+productName +"::"+grade.getName()+"`";
				data_sql +=  ","+ name +" double ";			
				sbuff_sql.append("," + name);
				sbuff.append(", ?");
			}			
		}
		
		for(ProductJoin productJoin : context.getProductJoinList()){
			Set<String> products = productJoin.getProductList();
			String productName = products.iterator().next().replaceAll("\\s+", "_");
			List<Grade> grades = context.getGradesForProduct(productName);
			for(Grade grade: grades ){
				String name = "`"+productJoin.getName() +"::"+grade.getName()+"`";
				data_sql +=  ","+ name +" double ";			
				sbuff_sql.append("," + name);
				sbuff.append(", ?");
			}			
		}
		
		for (int i = 1; i<= context.getProcessTree().getLevels(); i++) {
			String name = "process_level_"+i;
			data_sql +=  ","+ name +"  VARCHAR(50) ";			
			sbuff_sql.append("," + name);
			sbuff.append(", ?");
		}
		String[]  cost_heads =  { "ore_mining","waste_mining", "stockpile_cost", "stockpile_reclaim", "truckhour_cost" };
		
		for (int i = 0; i< cost_heads.length; i++) {
			data_sql +=  ","+ cost_heads[i] +"  double ";			
			sbuff_sql.append("," + cost_heads[i]);
			sbuff.append(", ?");
		}
		
		List<OpexData> opexDataList = context.getOpexDataList();
		for (OpexData opexData: opexDataList) {
			if(!opexData.isInUse()) continue;
			String name = opexData.isRevenue() ? "rev_" : "pcost_";
			if(opexData.getModelId() != -1) {
				name += context.getModelById(opexData.getModelId()).getName().toLowerCase();
			} else {
				name += opexData.getProductJoinName();
			}
			if(opexData.getUnitType() == OpexData.UNIT_FIELD) {
				name += "_"+ context.getFieldById(opexData.getFieldId()).getName().toLowerCase();
			} else if(opexData.getUnitType() == OpexData.UNIT_EXPRESSION) {
				name += "_"+ context.getExpressionById(opexData.getExpressionId()).getName().toLowerCase();
			}
			data_sql +=  ","+ name +"  double ";			
			sbuff_sql.append("," + name);
			sbuff.append(", ?");
		}
		
		String[] financial_heads = {"total_cost", "total_revenue", "cashflow", "dcf"};
		
		for (int i = 0; i< financial_heads.length; i++) {
			data_sql +=  ","+ financial_heads[i] +"  double ";			
			sbuff_sql.append("," + financial_heads[i]);
			sbuff.append(", ?");
		}

		
		data_sql += " ); ";
		
		sbuff_sql.append(")");
		sbuff.append(")");
		report_insert_sql = sbuff_sql.toString() + " values " + sbuff.toString();
		System.out.println("Sql =>"+data_sql);
		try (
				Statement stmt = conn.createStatement();
			)
		{			
			stmt.executeUpdate(data_sql);
		} 
		
	}
	
	protected void createStockpileInventory() throws SQLException  {
		
	}
	
	private void dropTable(int projectId, int scenarioId) throws SQLException {
		String  data_table_sql = "DROP TABLE IF EXISTS gnos_result_"+projectId+"_"+scenarioId+"; ";

		try (
				Statement stmt = conn.createStatement();
			)
		{
			stmt.executeUpdate(data_table_sql);
		} 
		
	}

	private void dropReportTable(int projectId, int scenarioId) throws SQLException {
		String  data_table_sql = "DROP TABLE IF EXISTS gnos_report_"+projectId+"_"+scenarioId+"; ";

		try (
				Statement stmt = conn.createStatement();
			)
		{
			stmt.executeUpdate(data_table_sql);
		} 
		
	}
	
	protected boolean isChild(int modelId, Model model) {
		Tree processTree = context.getProcessTree();
		Node processNode = processTree.getNodeByName(model.getName());
		
		return isChild(modelId, processNode);
	}
	
	protected boolean isChild(int modelId, Node node) {
		Node parent = node.getParent();
		if(parent == null) return false;
		if(parent.getData().getId() == modelId) {
			return true;
		}
		
		return isChild(modelId, parent);
	}
	
	
	protected boolean isProcessAssociatedToProductJoin(Model model, String productJoinName) {
		ProductJoin pj = context.getProductJoinFromName(productJoinName);
		Set<String> products = pj.getProductList();
		for(String productName: products) {
			Product p = context.getProductFromName(productName);
			if(p.getModelId() == model.getId()) {
				return true;
			}
		}
		Set<String> productJoins = pj.getProductJoinList();
		for(String pjName: productJoins) {
			if(isProcessAssociatedToProductJoin(model, pjName)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void setContext(ExecutionContext context) {
		this.context = context;
	}

	@Override
	public void start() {
		conn = DBManager.getConnection();
		try {
			createTable();
			createReportTable();
			createCapexTable();
			createStockpileInventory();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private PreparedStatement getUpdateStatement(int period) throws SQLException {
		int projectId = context.getProjectId();
		int scenarioId = context.getScenarioId();
		String update_sql = "update gnos_result_"+projectId+"_"+scenarioId+" set t"+period +"= ? where pit_no =? AND block_no=? AND sp_no = ? AND origin_type = ? AND destination_type = ? AND destination = ? ";
		PreparedStatement ps = conn.prepareStatement(update_sql);
		
		return ps;
	}
	@Override
	public void stop() {
		DBManager.releaseConnection(conn);
	}
}
