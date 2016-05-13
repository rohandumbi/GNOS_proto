package com.org.gnos.services.csv;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.org.gnos.db.DBManager;

public class GNOSCSVDataProcessor {

	private final static GNOSCSVDataProcessor instance = new GNOSCSVDataProcessor();
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
		Connection conn = DBManager.getConnection();
		String computed_data_insert_sql = "insert into gnos_computed_data_"+projectId+ " (block_no) values (?)";
		boolean autoCommit = true;
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		ResultSet rs = null;
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
            ps = conn.prepareStatement(buff.toString(), Statement.RETURN_GENERATED_KEYS);
            ps1 = conn.prepareStatement(computed_data_insert_sql);
            for(int i=0; i < data.size() ; i++) {
            	String[] row = data.get(i);
            	int j=0;
            	for(; j < row.length ; j++) {
            		ps.setString(j+1, row[j]);
            	}
            	ps.executeUpdate();
				rs = ps.getGeneratedKeys();    
				rs.next();  
				ps1.setInt(1, rs.getInt(1));
				ps1.executeUpdate();
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
		String  data_table_sql = "DROP TABLE IF EXISTS gnos_data_"+projectId+"; ";
		String  computed_data_table_sql = "DROP TABLE IF EXISTS gnos_computed_data_"+projectId+"; ";

		try (
				Statement stmt = conn.createStatement();
			)
		{
			stmt.executeUpdate(data_table_sql);
			stmt.executeUpdate(computed_data_table_sql);
		} 
		
	}
	
	private void createTable(int projectId, Connection conn) throws SQLException {
		dropTable(projectId, conn);
		String  data_sql = "CREATE TABLE gnos_data_"+projectId+" (id INT NOT NULL AUTO_INCREMENT, ";
		
		for(int i =0; i< columns.length; i++){
			String columnName = columns[i].replaceAll("\\s+","_").toLowerCase();
			data_sql += columnName +" VARCHAR(50)";
			data_sql += ", ";
		}
		data_sql += " PRIMARY KEY ( id ) );";
		
		String  computed_data_sql = "CREATE TABLE gnos_computed_data_"+projectId+" (block_no INT NOT NULL, pit_no INT, bench_no INT, PRIMARY KEY ( block_no )) ";
		System.out.println("Sql =>"+data_sql);
		try (
				Statement stmt = conn.createStatement();
			)
		{			
			stmt.executeUpdate(data_sql);
			stmt.executeUpdate(computed_data_sql);
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
