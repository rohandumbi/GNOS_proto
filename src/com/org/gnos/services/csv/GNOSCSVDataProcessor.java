package com.org.gnos.services.csv;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.org.gnos.core.GNOSConfig;
import com.org.gnos.db.DBManager;
import com.org.gnos.db.dao.FieldDAO;
import com.org.gnos.db.dao.RequiredFieldDAO;
import com.org.gnos.db.model.Field;
import com.org.gnos.db.model.RequiredField;

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
			createTable(projectId,conn);
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
	
	
	
	public void storeFields(int projectId){
		FieldDAO fdao = new FieldDAO();
		RequiredFieldDAO rfdao = new RequiredFieldDAO();
		String[] requiredFields = GNOSConfig.get("fields.required").split("#");
		String lastUnitField = null;
		for(int i=0;i < columns.length; i++ ){
			Field  field = new Field(columns[i]);
			short dataType = findDataType(data.get(0)[i]);
			
			if(i < requiredFields.length && dataType != Field.TYPE_TEXT) {
				dataType = Field.TYPE_NUMERIC;
			}
			field.setDataType(dataType);
			if(dataType == Field.TYPE_UNIT) {
				lastUnitField = columns[i];
			} else if(dataType == Field.TYPE_GRADE && lastUnitField != null){
				field.setWeightedUnit(lastUnitField);
			}
			fdao.create(field, projectId);
		}
		for(int i=0; i< requiredFields.length; i++) {
			RequiredField requiredField = new RequiredField();
			requiredField.setFieldName(requiredFields[i]);
			requiredField.setMappedFieldname(columns[i]);
			rfdao.create(requiredField, projectId);
		}
	}
	
	private short findDataType(String str) {
		short dataType = Field.TYPE_TEXT;
		try{
			Double data = Double.parseDouble(str);
			if(data < 100) {
				dataType = Field.TYPE_GRADE;
			} else {
				dataType = Field.TYPE_UNIT;
			}
		} catch (NumberFormatException e) {
			
		}
		return dataType;
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
		
		String  computed_data_sql = "CREATE TABLE gnos_computed_data_"+projectId+" (row_id INT NOT NULL, block_no INT, pit_no INT, bench_no INT, PRIMARY KEY ( row_id )) ";
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
