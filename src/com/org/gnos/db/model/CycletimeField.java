package com.org.gnos.db.model;

public class CycletimeField {

	
	private int id;
	private String name;
	
	public CycletimeField(){
		super();
		this.id = -1;
	}
	public CycletimeField(String name) {
		super();
		this.id = -1;
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
	public void setName(String name) {
		this.name = name;
	}
}
