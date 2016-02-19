package com.org.gnos.services;

public class Expression {
	
	int id;
	String name;
	boolean valueType;
	int value;

	boolean grade;
	
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

	@Override
	public String toString() {
		return "Expression [id=" + id + ", name=" + name + ", grade=" + grade
				+ "]";
	}	

}
