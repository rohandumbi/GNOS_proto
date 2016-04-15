package com.org.gnos.core;

import java.util.ArrayList;
import java.util.List;

import com.org.gnos.services.Operation;
import com.org.gnos.services.csv.GNOSCSVDataProcessor;

public class Expression {
	
	private int id;
	private String name;
	private boolean grade;
	private boolean isComplex;
	private String field_left;
	private String field_right;
	private short operator;
	private String condition = null;
	private String updatedCondition = null;
	private List conditionColumns = new ArrayList<Integer>();
	
	public Expression(String name) {
		super();
		this.name = name;
	}
	
	public Expression(int id, String name) {
		super();
		this.id = id;
		this.name = name;
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
	
	public boolean isComplex() {
		return isComplex;
	}

	public void setComplex(boolean isComplex) {
		this.isComplex = isComplex;
	}

	public boolean isGrade() {
		return grade;
	}

	public void setGrade(boolean grade) {
		this.grade = grade;
	}
	
	public String getField_left() {
		return field_left;
	}

	public void setField_left(String field_left) {
		this.field_left = field_left;
	}

	public String getField_right() {
		return field_right;
	}

	public void setField_right(String field_right) {
		this.field_right = field_right;
	}

	public short getOperator() {
		return operator;
	}

	public void setOperator(short operator) {
		this.operator = operator;
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
		this.updatedCondition = updatedConditionStr;
		this.condition = condition;
		
		
		return true;
	}

	
	
	public String getUpdatedCondition() {
		return updatedCondition;
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
