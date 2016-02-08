package com.org.gnos.services;

public class ColumnHeader {

	private String name;
	private boolean isRequired;
	private int dataType = 1; // 1- String 2 - int, 3 - double
	private String requiredFieldName;
	
	public ColumnHeader(String name) {
		this.name = name;
	}

	public boolean isRequired() {
		return isRequired;
	}

	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public String getRequiredFieldName() {
		return requiredFieldName;
	}

	public void setRequiredFieldName(String requiredFieldName) {
		this.requiredFieldName = requiredFieldName;
	}

	public String getName() {
		return name;
	}
	
}
