package com.org.gnos.services;

public class Filter {

	private int id;
	private int columnId;
	private int opType;
	private String value;
	
	
	public Filter(int id, int columnId, int opType, String value) {
		super();
		this.id = id;
		this.columnId = columnId;
		this.opType = opType;
		this.value = value;
	}
	
	public int getColumnId() {
		return columnId;
	}
	public void setColumnId(int columnId) {
		this.columnId = columnId;
	}
	public int getOpType() {
		return opType;
	}
	public void setOpType(int opType) {
		this.opType = opType;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getId() {
		return id;
	}
	
	
}
