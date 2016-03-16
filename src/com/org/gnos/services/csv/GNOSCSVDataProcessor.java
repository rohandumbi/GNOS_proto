package com.org.gnos.services.csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
	
	//private Map<String, String> requiredFieldMap = new HashMap<String, String>();
	private Map<String, String> requiredFieldMap = new LinkedHashMap<String, String>();
	//private Map<String, String> dataTypeMap = new HashMap<String, String>();
	private Map<String, String> dataTypeMap = new LinkedHashMap<String, String>();
	

	private Map<String, PitBenchMappingData> pitBenchMapping ;
	private List<String[]> computedData;
	private List<String> computedColumns;
	
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
		computedColumns = new ArrayList<String>();
		int tonnesWtIdx = -1;
		for(int j=0; j < columns.length;j++){
			if(columns[j].equalsIgnoreCase(requiredFieldMap.get("tonnes_wt"))){
				tonnesWtIdx = j;
				break;
			}
		}
		parsePitAndBenchData();
		List<Expression> expressions = Expressions.getAll();
		for(Expression expr: expressions){
			computedColumns.add(expr.getName());
			String[] dataArr = new String[data.size()];
			float value = 0;
			boolean isComplex = expr.isValueType();
			boolean isGrade = expr.isGrade();
			String conditionExpr = expr.getUpdatedCondition();
			
			for(int i=0; i < dataArr.length; i++) {
				String[] rowValues = data.get(i);
				boolean conditionsMet = true;
				try{
					if(conditionExpr != null){
						conditionsMet = GnosExpressionParser.evaluate(conditionExpr, rowValues, expr.getConditionColumns());
						//System.out.println("conditionsMet :"+conditionsMet);
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
	
	private void parsePitAndBenchData() {

		pitBenchMapping = new HashMap<String, PitBenchMappingData>();
		computedColumns.add("pit_no");
		computedColumns.add("bench_no");
		String pitNameAlias = requiredFieldMap.get("pit_name");
		String benchNameAlias = requiredFieldMap.get("bench_rl");
		int pitNameColIdx = -1;
		int benchColIdx = -1;
		for(int j=0; j < columns.length;j++){
			if(columns[j].equalsIgnoreCase(pitNameAlias)){
				pitNameColIdx = j;
			} else if(columns[j].equalsIgnoreCase(benchNameAlias)){
				benchColIdx = j;
			}
			if( pitNameColIdx >= 0 && benchColIdx >= 0 ){
				break;
			}
		}
		String[] pitNos = new String[data.size()];
		String[] benchNos = new String[data.size()];

		for(int i=0; i < data.size(); i++) {
			String[] rowValues = data.get(i);
			int pitNo = -1;
			PitBenchMappingData mappingData = pitBenchMapping.get(rowValues[pitNameColIdx]);
			if(mappingData != null){
				mappingData.addBenchData(Integer.parseInt(rowValues[benchColIdx]));
				pitNo = mappingData.pitNo;
			} else {
				pitNo = pitBenchMapping.size()+1;
				mappingData = new PitBenchMappingData(pitNo);
				mappingData.addBenchData(Integer.parseInt(rowValues[benchColIdx]));
				pitBenchMapping.put(rowValues[pitNameColIdx], mappingData);
			}
			pitNos[i] = String.valueOf(pitNo);
		}
		for(int i=0; i < data.size(); i++) {
			String[] rowValues = data.get(i);
			PitBenchMappingData mappingData = pitBenchMapping.get(rowValues[pitNameColIdx]);
			int benchNo = mappingData.benchValues.size() - mappingData.benchValues.indexOf(Integer.parseInt(rowValues[benchColIdx]));
			benchNos[i] = String.valueOf(benchNo);
		}
		computedData.add(pitNos);
		computedData.add(benchNos);
	}
	
	public void dumpToCsv() {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(new File("OUTPUT.CSV")));
			// Write header column
			StringBuilder sb = new StringBuilder();
			for(int i =0; i< columns.length; i++){
				sb.append(columns[i] +",");
			}
			for(int j =0; j< computedColumns.size(); j++){
				sb.append(computedColumns.get(j) +",");
			}
			bw.write(sb.substring(0, sb.length()-1)+"\n");
			
			// Write rows
			final int batchSize = 1000;
            int count = 0;
            for(int i=0; i < data.size() ; i++) {
            	sb = new StringBuilder();
            	String[] row = data.get(i);
            	int j=0;
            	for(; j < row.length ; j++) {
            		sb.append(row[j]+",");
            	}
            	for(int jj=0; jj< computedData.size(); jj++){
            		sb.append(computedData.get(jj)[i]+",");
            	}
            	bw.write(sb.substring(0, sb.length()-1)+"\n");
            	
            	if (++count % batchSize == 0) {
                    bw.flush();
                }
            }
            
		} catch (IOException ioe) {
			   ioe.printStackTrace();
		}
		finally
		{ 
		   try{
		      if(bw!=null){
		    	  bw.close();
		      }
		   }catch(Exception ex){
		       System.out.println("Error in closing the BufferedWriter"+ex);
		    }
		}
	}
	
	public void dumpToDB() {
		Connection conn = DBManager.getConnection();
		boolean autoCommit = true;
		PreparedStatement ps = null;
		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			createTable(conn);
			final int batchSize = 1000;
            int count = 0;
            StringBuffer  buff = new StringBuffer("insert into gnos_data values (");
            int columnCount = columns.length + computedColumns.size();
            for(int i=0; i < columnCount ; i++){
            	buff.append("?");
            	if(i < columnCount -1 ){
            		buff.append(",");
            	} else {
            		buff.append(")");
            	}
            }
            ps = conn.prepareStatement(buff.toString());
            for(int i=0; i < data.size() ; i++) {
            	String[] row = data.get(i);
            	int j=0;
            	for(; j < row.length ; j++) {
            		ps.setString(j+1, row[j]);
            	}
            	for(int jj=0; jj< computedData.size(); jj++){
            		ps.setString(j+jj+1, computedData.get(jj)[i]);
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
	
	private void dropTable(Connection conn) throws SQLException {
		String  sql = "DROP TABLE IF EXISTS gnos_data; ";
		
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
	
	private void createTable(Connection conn) throws SQLException {
		dropTable(conn);
		String  sql = "CREATE TABLE gnos_data ( ";
		
		for(int i =0; i< columns.length; i++){
			String columnName = columns[i].replaceAll("\\s+","_").toLowerCase();
			sql += columnName +" VARCHAR(50)";
			sql += ",";
		}
		for(int j =0; j< computedColumns.size(); j++){
			String columnName = computedColumns.get(j).replaceAll("\\s+","_").toLowerCase();
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

	

	class PitBenchMappingData{
		int pitNo = -1;
		List benchValues = new ArrayList();
		
		PitBenchMappingData(int pitNo) {
			this.pitNo = pitNo;
		}
		void addBenchData(Integer benchVal) {
			if(benchValues.indexOf(benchVal) == -1){
				benchValues.add(benchVal);
				Collections.sort(benchValues);
			}
		}
	}
}
