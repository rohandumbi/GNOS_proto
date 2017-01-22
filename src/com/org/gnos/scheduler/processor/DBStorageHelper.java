package com.org.gnos.scheduler.processor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.core.ScenarioConfigutration;
import com.org.gnos.db.DBManager;
import com.org.gnos.scheduler.equation.ExecutionContext;

public class DBStorageHelper implements IStorageHelper {

	protected Connection conn;
	protected ExecutionContext context;
	
	@Override
	public void store(List<Record> records, boolean hasMore) {
		int projectId = ProjectConfigutration.getInstance().getProjectId();
		int scenarioId = ScenarioConfigutration.getInstance().getScenarioId();
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

	public void processRecord(Record record){
		
	}

	public void postProcess(){
		
	}

	private void createTable() throws SQLException {
		int projectId = ProjectConfigutration.getInstance().getProjectId();
		int scenarioId = ScenarioConfigutration.getInstance().getScenarioId();
		dropTable(projectId, scenarioId);
		String  data_sql = "CREATE TABLE gnos_result_"+projectId+"_"+scenarioId+" ( " +
				" origin_type TINYINT NOT NULL, " +
				" pit_no INT NOT NULL default -1," +
				" block_no INT  NOT NULL default -1," +
				" sp_no INT  NOT NULL default -1, " +
				" destination_type TINYINT NOT NULL, " +
				" destination INT, tonnage_wt VARCHAR(50), ";
		
		for(int i = 1; i<= ScenarioConfigutration.getInstance().getTimePeriod(); i++){
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
	
	private void dropTable(int projectId, int scenarioId) throws SQLException {
		String  data_table_sql = "DROP TABLE IF EXISTS gnos_result_"+projectId+"_"+scenarioId+"; ";

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
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private PreparedStatement getUpdateStatement(int period) throws SQLException {
		int projectId = ProjectConfigutration.getInstance().getProjectId();
		int scenarioId = ScenarioConfigutration.getInstance().getScenarioId();
		String update_sql = "update gnos_result_"+projectId+"_"+scenarioId+" set t"+period +"= ? where pit_no =? AND block_no=? AND sp_no = ?";
		PreparedStatement ps = conn.prepareStatement(update_sql);
		
		return ps;
	}
	@Override
	public void stop() {
		DBManager.releaseConnection(conn);
	}
}
