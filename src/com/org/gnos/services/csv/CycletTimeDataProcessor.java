package com.org.gnos.services.csv;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.org.gnos.db.DBManager;

public class CycletTimeDataProcessor {

	private final static CycletTimeDataProcessor instance = new CycletTimeDataProcessor();
	private String[] columns = null;
	private List<String[]> data = new ArrayList<String[]>();
	
	public boolean processCsv(String fileName){
		System.out.println("CSV Processing started");
		CSVReader reader = null;
		
		try {
			boolean isFirstRow = true;
			reader = new CSVReader(fileName);
			String[] row = reader.readLine();
			while (row != null) {
				if(isFirstRow){
					columns = row;
					isFirstRow = false;
				} else {
					data.add(row);
				}
				
				row = reader.readLine();		
			}		

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
	

	
	public void dumpToDB(int projectId) {
		if(columns.length == 0 ) return;
		Connection conn = DBManager.getConnection();
		boolean autoCommit = true;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			createTable(projectId,conn);
			final int batchSize = 1000;
            int count = 0;
            StringBuffer names = new StringBuffer("");
            StringBuffer values = new StringBuffer("");
            StringBuffer  buff = new StringBuffer("insert into gnos_cycle_time_data_"+projectId);
            int columnCount = columns.length;
            for(int i=0; i < columnCount ; i++){
            	names.append(columns[i]);
            	values.append("? ");
            	if(i < columnCount -1 ){
            		names.append(",");
            		values.append(",");
            	}
            }
            buff.append(" ("+names.toString() +") ");
            buff.append(" values ("+values.toString() +") ");
            ps = conn.prepareStatement(buff.toString());
            for(int i=0; i < data.size() ; i++) {
            	String[] row = data.get(i);
            	int j=0;
            	for(; j < row.length ; j++) {
            		ps.setString(j+1, row[j]);
            	}
            	ps.executeUpdate();
            	if (++count % batchSize == 0) {
            		conn.commit();
                }
            }
            //ps.executeBatch();
            conn.commit();
            
		}  
		catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		finally {
			try {
				conn.setAutoCommit(autoCommit);
				if(ps!= null){
					ps.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			DBManager.releaseConnection(conn);
		}

		
	}
	
	private void dropTable(int projectId, Connection conn) throws SQLException {
		String  cycle_time_data_table_sql = "DROP TABLE IF EXISTS gnos_cycle_time_data_"+projectId+"; ";

		try (
				Statement stmt = conn.createStatement();
			)
		{
			stmt.executeUpdate(cycle_time_data_table_sql);
		} 
		
	}
	
	private void createTable(int projectId, Connection conn) throws SQLException {
		dropTable(projectId, conn);		
		String  data_sql = "CREATE TABLE gnos_cycle_time_data_"+projectId+" (id INT NOT NULL AUTO_INCREMENT, ";
		
		for(int i =0; i< columns.length; i++){
			String columnName = columns[i].replaceAll("\\s+","_").toLowerCase();
			data_sql += columnName +" VARCHAR(50)";
			data_sql += ", ";
		}
		data_sql += " PRIMARY KEY ( id ) );";
		
	}
	
	public String[] getHeaderColumns() {
		return columns;
	}
	
	public List<String[]> getData() {
		return data;
	}

	public static CycletTimeDataProcessor getInstance() {
		return instance;
	}
	
}
