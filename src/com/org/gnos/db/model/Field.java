package com.org.gnos.db.model;

public class Field {

	public final static short TYPE_TEXT = 1;
	public final static short TYPE_NUMERIC = 2;
	public final static short TYPE_UNIT = 3;
	public final static short TYPE_GRADE = 4;
	
	private int id;
	private String name;
	private short dataType;
	
	public Field(){
		super();
		this.id = -1;
		this.dataType = TYPE_TEXT;
	}
	public Field(String name) {
		super();
		this.id = -1;
		this.name = name;
		this.dataType = TYPE_TEXT;
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
