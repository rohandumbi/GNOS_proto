package com.org.gnos.services.csv;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.org.gnos.db.DBManager;

public class CSVDataProcessor {

	private String fileName;
	private String[] columns = null;
	private List<String[]> data = new ArrayList<String[]>();
	
	
	public CSVDataProcessor(String fileName) {
		this.fileName = fileName;
		processCsv();
	}


	public CSVDataProcessor() {

	}
	
	public boolean processCsv(){
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
	

	public void reImportToDB(int projectId) {
		
		String computed_data_delete_sql = "delete from gnos_computed_data_"+projectId;
		String gnos_data_delete_sql = "delete from gnos_data_"+projectId;
		String computed_data_insert_sql = "insert into gnos_computed_data_"+projectId+ " (row_id) values (?)";
		
		boolean autoCommit = true;
		
		
		StringBuffer  buff = new StringBuffer("insert into gnos_data_"+projectId);
		StringBuffer values = new StringBuffer("");
        int columnCount = columns.length;
        for(int i=0; i <= columnCount ; i++){
        	values.append("? ");
        	if(i < columnCount ){
        		values.append(",");
        	}
        }
        buff.append(" values ("+values.toString() +") ");
        Connection conn = DBManager.getConnection();
		
		try (
				PreparedStatement ps = conn.prepareStatement(computed_data_delete_sql);
				PreparedStatement ps1 = conn.prepareStatement(gnos_data_delete_sql);
				PreparedStatement ps2 = conn.prepareStatement(computed_data_insert_sql);
				PreparedStatement ps3 = conn.prepareStatement(buff.toString());
		){
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
            ps.executeUpdate();
            ps1.executeUpdate();
            
			final int batchSize = 1000;
            int count = 0;
            for(int i=0; i < data.size() ; i++) {
            	String[] row = data.get(i);
            	
            	ps3.setInt(1, count + 1);
            	int j=1;
            	for(; j <=  row.length ; j++) {
            		ps3.setString(j+1, row[j-1]);
            	}
            	
            	ps3.executeUpdate();
				ps2.setInt(1, count + 1);
				ps2.executeUpdate();
            	if (++count % batchSize == 0) {
            		conn.commit();
                }
            }
            //ps.executeBatch();
            conn.commit();
            
		}  
		catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DBManager.releaseConnection(conn);
		}
		
	}
	
	public void dumpToDB(int projectId) {
		Connection conn = DBManager.getConnection();
		String computed_data_insert_sql = "insert into gnos_computed_data_"+projectId+ " (row_id) values (?)";
		boolean autoCommit = true;
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		ResultSet rs = null;
		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			final int batchSize = 1000;
            int count = 0;
            StringBuffer names = new StringBuffer("");
            StringBuffer values = new StringBuffer("");
            StringBuffer  buff = new StringBuffer("insert into gnos_data_"+projectId);
            int columnCount = columns.length;
            for(int i=0; i < columnCount ; i++){
            	names.append(columns[i].replaceAll("\\s+","_").toLowerCase());
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

	
	public String[] getHeaderColumns() {
		return columns;
	}
	
	public List<String[]> getData() {
		return data;
	}


	public void setColumns(String[] columns) {
		this.columns = columns;
	}

	public void setData(List<String[]> data) {
		this.data = data;
	}
		
}
