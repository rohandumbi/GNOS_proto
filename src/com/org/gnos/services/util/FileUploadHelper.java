package com.org.gnos.services.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.org.gnos.core.GNOSConfig;
import com.org.gnos.db.DBManager;
import com.org.gnos.db.dao.ExpressionDAO;
import com.org.gnos.db.dao.FieldDAO;
import com.org.gnos.db.dao.RequiredFieldDAO;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Field;
import com.org.gnos.db.model.RequiredField;
import com.org.gnos.services.ExpressionProcessor;
import com.org.gnos.services.csv.CSVDataProcessor;

public class FileUploadHelper {

	private List<CSVDataProcessor> processors;
	private String[] headerColumns; 
	
	public FileUploadHelper(List<String> fileNames) {
		if(fileNames != null && fileNames.size() > 0) {
			processors = new ArrayList<CSVDataProcessor>();
			for(String fileName: fileNames) {
				CSVDataProcessor processor = new CSVDataProcessor(fileName);
				processors.add(processor);
			}
		}
	}

	public boolean verifyHeaders() {

		if(processors != null && processors.size() > 0) {
			if(headerColumns == null) {
				headerColumns = processors.get(0).getHeaderColumns();
			}
			for(int i = 1; i < processors.size(); i++) {
				String[] columns = processors.get(i).getHeaderColumns();
				if(headerColumns.length > columns.length) return false;
				for(int j=0; j< headerColumns.length; j++) {
					if(!headerColumns[j].equals(columns[j])){
						return false;
					}
				}
			}
		}
		return true;
	}

	public boolean storeFields(int projectId) {
		if(processors == null || processors.size() == 0) return true;
		FieldDAO fdao = new FieldDAO();
		RequiredFieldDAO rfdao = new RequiredFieldDAO();
		String[] requiredFields = GNOSConfig.get("fields.required").split("#");
		String lastUnitField = null;
		for(int i=0;i < headerColumns.length; i++ ){
			Field  field = new Field(headerColumns[i]);
			short dataType = findDataType(processors.get(0).getData().get(0)[i]);
			
			if(i < requiredFields.length && dataType != Field.TYPE_TEXT) {
				dataType = Field.TYPE_NUMERIC;
			}
			field.setDataType(dataType);
			if(dataType == Field.TYPE_UNIT) {
				lastUnitField = headerColumns[i];
			} else if(dataType == Field.TYPE_GRADE && lastUnitField != null){
				field.setWeightedUnit(lastUnitField);
			}
			fdao.create(field, projectId);
		}
		for(int i=0; i< requiredFields.length; i++) {
			RequiredField requiredField = new RequiredField();
			requiredField.setFieldName(requiredFields[i]);
			requiredField.setMappedFieldname(headerColumns[i]);
			rfdao.create(requiredField, projectId);
		}
		
		return true;
	}
	public boolean loadData(int projectId, boolean append) {
		try {
			if(!append) {
				createTable(projectId);
			}			
			for(CSVDataProcessor processor: processors) {
				processor.dumpToDB(projectId);
			}
			updateExpressions(projectId, append);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return true;
	}

	private void updateExpressions(int projectId, boolean append) {
		List<Expression> expressions = new ExpressionDAO().getAll(projectId);
		ExpressionProcessor processor = new ExpressionProcessor();
		processor.setExpressions(expressions);
		
		if(!append) {
			processor.store(projectId);
		} else {
			processor.processExpressions(projectId);
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
	
	private void createTable(int projectId) throws SQLException {
		Connection conn = DBManager.getConnection();
		dropTable(projectId, conn);
		String  data_sql = "CREATE TABLE gnos_data_"+projectId+" (id INT NOT NULL AUTO_INCREMENT, ";
		
		for(int i =0; i< headerColumns.length; i++){
			String columnName = headerColumns[i].replaceAll("\\s+","_").toLowerCase();
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
}
