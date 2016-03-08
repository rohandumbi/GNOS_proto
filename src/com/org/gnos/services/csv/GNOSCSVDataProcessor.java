package com.org.gnos.services.csv;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	private List<String[]> computedData = new ArrayList<String[]>();
	
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
			//List<Filter> filters= expr.getFilters();
			for(int i=0; i < dataArr.length; i++) {
				System.out.println("Row id "+i +" : Value :"+value);
				String[] rowValues = data.get(i);
				try{
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
					if(isGrade) {
						value = ( value / Float.parseFloat(rowValues[tonnesWtIdx]));
					}
				} catch(Exception e){
					e.printStackTrace();
				}
			
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
