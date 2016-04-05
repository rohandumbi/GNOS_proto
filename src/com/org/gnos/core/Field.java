package com.org.gnos.core;

public class Field {

	public final static short TYPE_STRING = 0;
	public final static short TYPE_NUMBER = 1;
	
	private int id;
	private String name;
	private short dataType;
	
	public Field(String name) {
		super();
		this.name = name;
	}
	
	public Field(int id, String name, short dataType) {
		super();
		this.id = id;
		this.name = name;
		this.dataType = dataType;
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
	public void setName(String name) {
		this.name = name;
	}
	public short getDataType() {
		return dataType;
	}
	public void setDataType(short dataType) {
		if(dataType < 0 || dataType > 1) return ;
		
		this.dataType = dataType;
	}
	
	
}
