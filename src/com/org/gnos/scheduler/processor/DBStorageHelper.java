package com.org.gnos.scheduler.processor;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.core.Block;
import com.org.gnos.core.Pit;
import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Field;
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
				ps.setString(1, String.valueOf(record.getValue()));
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
					Block b = context.getBlocks().get(record.getBlockNo());
					double tonnesWt = context.getTonnesWtForBlock(b);				
					double quantityMined = record.getValue();
					double ratio = quantityMined/tonnesWt;
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
						total_TH = total_TH.add(context.getTruckHourRatio(b, process.getModel().getName()));
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
					total_TH  = total_TH.multiply(new BigDecimal(ratio));
					
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
						BigDecimal value = context.getProductValueForBlock(b, product);
						value = value.multiply(new BigDecimal(quantityMined));
						ips.setBigDecimal(index, value);
						
						index ++;
					}
					
					for(ProductJoin productJoin: productJoinList) {
						BigDecimal value = context.getProductJoinValueForBlock(b, productJoin);
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
	
	private void createReportTable() throws SQLException {
		int projectId = context.getProjectId();
		dropReportTable(projectId);
		
		StringBuffer sbuff_sql = new StringBuffer("insert into gnos_report_"+projectId+" (scenario_name, mode, origin_type, pit_no, sp_no, block_no, destination_type, destination, period, quantity_mined, ratio, total_th ");
		StringBuffer sbuff = new StringBuffer(" ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ");
		String  data_sql = "CREATE TABLE gnos_report_"+projectId+" ( " +
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

	private void dropReportTable(int projectId) throws SQLException {
		String  data_table_sql = "DROP TABLE IF EXISTS gnos_report_"+projectId+"; ";

		try (
				Statement stmt = conn.createStatement();
			)
		{
			stmt.executeUpdate(data_table_sql);
		} 
		
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
