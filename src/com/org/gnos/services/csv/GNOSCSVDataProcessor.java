package com.org.gnos.services.csv;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.org.gnos.db.DBManager;
import com.org.gnos.services.Expression;
import com.org.gnos.services.Expressions;
import com.org.gnos.services.Filter;
import com.org.gnos.services.Operation;

public class GNOSCSVDataProcessor {

	private final static GNOSCSVDataProcessor instance = new GNOSCSVDataProcessor();
	private String[] columns = null;
	private List<String[]> data = new ArrayList<String[]>();
	
	private Map<String, String> requiredFieldMap = new HashMap<String, String>();
	private Map<String, String> dataTypeMap = new HashMap<String, String>();
	
	//private List<String[]> computedData = new ArrayList<String[]>();
	private List<String[]> computedData;
	
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
	
	public void compute() {
		computedData = new ArrayList<String[]>();
		int tonnesWtIdx = -1;
		for(int j=0; j < columns.length;j++){
			if(columns[j].equalsIgnoreCase(requiredFieldMap.get("tonnes_wt"))){
				tonnesWtIdx = j;
				break;
			}
		}
		List<Expression> expressions = Expressions.getAll();
		for(Expression expr: expressions){
			System.out.println("Expr name = "+expr.getName());
			String[] dataArr = new String[data.size()];
			float value = 0;
			boolean isComplex = expr.isValueType();
			boolean isGrade = expr.isGrade();
			List<Filter> filters= expr.getFilters();
			
			for(int i=0; i < dataArr.length; i++) {
				String[] rowValues = data.get(i);
				boolean conditionsMet = true;
				try{
					
					for (Filter filter: filters){
						String conditionValue = filter.getValue();
						String valueToCheck = rowValues[filter.getColumnId()];

						switch(filter.getOpType()){
							case 0: if(!(valueToCheck.equalsIgnoreCase(conditionValue))){
										conditionsMet = false;
									}
									break;
							case 1: if(valueToCheck.equalsIgnoreCase(conditionValue)){
										conditionsMet = false;
									}
									break;
							case 2: if(!(conditionValue.toLowerCase().contains(valueToCheck.toLowerCase()))){
										conditionsMet = false;
									}
									break;
							case 3: if(!(Float.parseFloat(valueToCheck) > Float.parseFloat(conditionValue))){
										conditionsMet = false;
									}
									break;							
							case 4: if(!(Float.parseFloat(valueToCheck) < Float.parseFloat(conditionValue))){
										conditionsMet = false;
									}
									break;
						}
						System.out.println("conditionValue :"+conditionValue+ " conditionValue: "+valueToCheck +" conditionsMet: " +conditionsMet);
					}
					if(conditionsMet){						
						if(isComplex) {
							Operation operation = expr.getOperation();
							float leftOperand = Float.parseFloat(rowValues[operation.getOperand_left()]);
							float rightOperand = Float.parseFloat(rowValues[operation.getOperand_right()]);
							switch(operation.getOperator()) {
								case 0: value = leftOperand + rightOperand; break;
								case 1: value = leftOperand - rightOperand; break;
								case 2: value = leftOperand * rightOperand; break;
								case 3: value = leftOperand / rightOperand; break;
							}
						} else {
							value = Float.parseFloat(rowValues[expr.getValue()]);
						}
						if(!isGrade) {
							value = ( value / Float.parseFloat(rowValues[tonnesWtIdx]));
						}

					} else {
						value = 0;
					}
				} catch(Exception e){
					value = 0;
					e.printStackTrace();
				}
				dataArr[i] = String.valueOf(value);
			
			}
			computedData.add(dataArr);
			
		}
	}
	
	public void dumpToCsv() {
		for(String[] row: data){
			for(int i=0; i < row.length; i++ ){
				System.out.print(row[i]+ ",");
			}
			System.out.println("");
		}
		
	}
	
	public void dumpToDB() {
		Connection conn = DBManager.getConnection();
		boolean autoCommit = true;
		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			createTable(conn);
			final int batchSize = 1000;
            int count = 0;
            
		}  
		catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		finally {
			try {
				conn.setAutoCommit(autoCommit);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			DBManager.releaseConnection(conn);
		}

		
	}
	
	private void createTable(Connection conn) throws SQLException {
		String  sql = "CREATE TABLE gnos_data ( ";
		
		for(int i =0; i< columns.length; i++){
			String columnName = columns[i].replaceAll("\\s+","_").toLowerCase();
			sql += columnName +" VARCHAR(50)";
			sql += ",";
		}
		List<Expression> expressions = Expressions.getAll();
		for(Expression expr: expressions){
			String columnName = expr.getName().replaceAll("\\s+","_").toLowerCase();
			sql += columnName +" VARCHAR(50)";
			sql += ",";
		}
		sql = sql.substring(0, sql.length() -1);
		sql += ");";
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
	
	public void addRequiredFieldMapping(String requiredField, String mappedTo) {
		requiredFieldMap.put(requiredField, mappedTo);
	}
	
	public void addDataTypeMapping(String sourceFieldName, String datatypeName) {
		dataTypeMap.put(sourceFieldName, datatypeName);
	}
	
	public Map getDataTypeMapping() {
		return dataTypeMap;
	}
	
	public String[] getHeaderColumns() {
		return columns;
	}
	
	public static GNOSCSVDataProcessor getInstance() {
		return instance;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GNOSCSVDataProcessor processor = GNOSCSVDataProcessor.getInstance();
		System.out.println("Start Time: "+ new Date());
		processor.processCsv("C:\\Arpan\\Workspace\\personal\\workspace\\GNOS_proto\\data\\input_data.csv");
		System.out.println("End Time: "+ new Date());
	}

	

}
