package com.org.gnos.services;

import java.util.ArrayList;
import java.util.List;

import com.org.gnos.services.csv.GNOSCSVDataProcessor;

public class Expression {
	
	int id;
	String name;
	boolean grade;
	boolean valueType;
	int value;
	Operation operation = null;
	String condition = null;
	List conditionColumns = new ArrayList<Integer>();
	
	public Expression(String name) {
		super();
		this.name = name;
	}
	
	public Expression(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isValueType() {
		return valueType;
	}

	public void setValueType(boolean valueType) {
		this.valueType = valueType;
	}
	
	public boolean isGrade() {
		return grade;
	}

	public void setGrade(boolean grade) {
		this.grade = grade;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	
	public String getCondition() {
		return condition;
	}

	public boolean setCondition(String condition) {
		if(condition == null || condition.trim().length() == 0) return false;
		String updatedConditionStr = "";
		String pattern = "(\\.?\\])";
		String[] splits = condition.split(pattern);
		String[] columns = GNOSCSVDataProcessor.getInstance().getHeaderColumns();
		for(int i=0; i<splits.length; i++){
			int index = splits[i].lastIndexOf("[");
			if(index != -1) {
				String columnName = splits[i].substring(index+1);
				boolean  matchFound = false;
				for(int j=0; j < columns.length ;j++){
					if(columnName.trim().equalsIgnoreCase(columns[j])){
						updatedConditionStr += splits[i].substring(0, index+1).trim() + j +"]";
						matchFound = true;
						conditionColumns.add(j);
					}
				}
				if(!matchFound) {
					conditionColumns = new ArrayList<Integer>();
					return false;
				}
			} else {
				updatedConditionStr += splits[i];
			}			
		}
		this.condition = updatedConditionStr;
		
		
		return true;
	}

	
	public List getConditionColumns() {
		return conditionColumns;
	}

	@Override
	public String toString() {
		return "Expression [id=" + id + ", name=" + name + ", grade=" + grade
				+ "]";
	}	

}
