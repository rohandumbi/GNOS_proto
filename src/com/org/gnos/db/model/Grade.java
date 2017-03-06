package com.org.gnos.db.model;

public class Grade {

	public static final short GRADE_FIELD = 1;
	public static final short GRADE_EXPRESSION = 2;
	
	private int id;
	private String productName;
	private String name;
	private short type;
	private String mappedName;
	
	public Grade(){
		super();
		this.id = -1;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public String getMappedName() {
		return mappedName;
	}

	public void setMappedName(String mappedName) {
		this.mappedName = mappedName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
