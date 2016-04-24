package com.org.gnos.services.csv;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.db.DBManager;
import com.org.gnos.services.csv.CopyOfGNOSCSVDataProcessor.PitBenchMappingData;

public class GNOSCSVDataProcessor {

	private final static GNOSCSVDataProcessor instance = new GNOSCSVDataProcessor();
	private String[] columns = null;
	private List<String[]> data = new ArrayList<String[]>();
	
	public boolean processCsv(String fileName){
		
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
		Connection conn = DBManager.getConnection();
		boolean autoCommit = true;
		PreparedStatement ps = null;
		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			createTable(projectId,conn);
			final int batchSize = 1000;
            int count = 0;
            StringBuffer names = new StringBuffer("");
            StringBuffer values = new StringBuffer("");
            StringBuffer  buff = new StringBuffer("insert into gnos_data_"+projectId);
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
            	ps.addBatch();
            	
            	if (++count % batchSize == 0) {
                    ps.executeBatch();
                }
            }
            ps.executeBatch();
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
		String  sql = "DROP TABLE IF EXISTS gnos_data_"+projectId+"; ";
		
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} finally {
			if(stmt != null){
				stmt.close();
			}
		}
		
	}
	
	private void createTable(int projectId, Connection conn) throws SQLException {
		dropTable(projectId, conn);
		String  sql = "CREATE TABLE gnos_data_"+projectId+" (id INT NOT NULL AUTO_INCREMENT, ";
		
		for(int i =0; i< columns.length; i++){
			String columnName = columns[i].replaceAll("\\s+","_").toLowerCase();
			sql += columnName +" VARCHAR(50)";
			sql += ", ";
		}
		sql += "pit_no INT, bench_no INT, PRIMARY KEY ( id ) );";
		System.out.println("Sql =>"+sql);
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} finally {
			if(stmt != null){
				stmt.close();
			}
		}
		
	}
	
	public String[] getHeaderColumns() {
		return columns;
	}
	
	public List<String[]> getData() {
		return data;
	}



	public static GNOSCSVDataProcessor getInstance() {
		return instance;
	}
	
}
