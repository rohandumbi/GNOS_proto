package com.org.gnos.services;

import java.util.ArrayList;
import java.util.List;

public class Expression {
	
	int id;
	String name;
	boolean grade;
	boolean valueType;
	int value;
	Operation operation = null;
	List<Filter> filters = new ArrayList<Filter>();
	
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

	public List<Filter> getFilters() {
		return filters;
	}

	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}

	public void addFilter(Filter filter) {
		this.filters.add(filter);
	}
	
	@Override
	public String toString() {
		return "Expression [id=" + id + ", name=" + name + ", grade=" + grade
				+ "]";
	}	

}
