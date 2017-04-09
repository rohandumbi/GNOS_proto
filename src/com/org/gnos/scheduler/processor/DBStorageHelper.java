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
import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Field;
import com.org.gnos.db.model.Process;
import com.org.gnos.db.model.Stockpile;
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
		try ( PreparedStatement ips = conn.prepareStatement(report_insert_sql); ){
			boolean autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			for(Record record:records){
				try {
					Block b = context.getBlocks().get(record.getBlockNo());
					double tonnesWt = context.getTonnesWtForBlock(b);
					double ratio = record.getValue()/tonnesWt;
					ips.setString(1, context.getScenario().getName());
					ips.setInt(2, record.getOriginType());
					ips.setInt(3, record.getPitNo());
					ips.setInt(4, record.getOriginSpNo());
					ips.setInt(5, record.getBlockNo());
					ips.setInt(6, record.getDestinationType());
					if(record.getDestinationType() == Record.DESTINATION_PROCESS) {
						Process process = context.getProcessByNumber(record.getProcessNo());
						ips.setString(7, process.getModel().getName());
					} else if(record.getDestinationType() == Record.DESTINATION_SP) {
						Stockpile sp = context.getStockpileFromNo(record.getDestSpNo());
						ips.setString(7, sp.getName());
					} else if(record.getDestinationType() == Record.DESTINATION_WASTE) {
						Dump dump = context.getDumpfromNo(record.getWasteNo());
						ips.setString(7, dump.getName());
					}
					ips.setDouble(8, record.getTimePeriod());
					ips.setDouble(9, record.getValue());
					ips.setDouble(10, ratio);
					int index = 11;
					for(Field f: fields) {
						if(f.getDataType() == Field.TYPE_GRADE) {
							String associatedFieldName = f.getWeightedUnit();
							BigDecimal value = new BigDecimal(b.getField(f.getName())).multiply(new BigDecimal(b.getField(associatedFieldName)));
							value = value.multiply(new BigDecimal(ratio));
							ips.setString(index, value.toString());
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
		
		StringBuffer sbuff_sql = new StringBuffer("insert into gnos_report_"+projectId+" (scenario_name, origin_type, pit_no, sp_no, block_no, destination_type, destination, period, quantity_mined, ratio ");
		StringBuffer sbuff = new StringBuffer(" ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ");
		String  data_sql = "CREATE TABLE gnos_report_"+projectId+" ( " +
				"scenario_name VARCHAR(50), " +
				" origin_type TINYINT NOT NULL, " +			
				" pit_no INT NOT NULL default -1," +
				" sp_no INT NOT NULL default -1," +
				" block_no INT  NOT NULL default -1," +
				" destination_type TINYINT NOT NULL, " +
				" destination VARCHAR(50), " + 
				" period INT, " + 
				" quantity_mined VARCHAR(50) ," +
				" ratio double ";
		
		
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
			String name = expression.getName();
			if(expression.isGrade()) {
				name = name+"_u";
			}
			data_sql +=  ","+ name +" double ";			
			sbuff_sql.append("," + name);
			sbuff.append(", ?");
		}
		
		data_sql += " ); ";
		//data_sql += "  UNIQUE KEY (scenario_name, origin_type, pit_no, block_no, destination_type, destination) );";
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
