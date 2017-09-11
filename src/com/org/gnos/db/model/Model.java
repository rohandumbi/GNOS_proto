package com.org.gnos.db.model;

public class Model {
	
	public static final short UNIT_FIELD = 1;
	public static final short UNIT_EXPRESSION = 2;
	
	private int id;
	private String name;
	private short unitType;
	private int fieldId;
	private int expressionId;
	private String condition;
	
	
	public Model(){
		this.id = -1;
	}
	
	public Model(String name) {
		super();
		this.id = -1;
		this.name = name;
	}
	
	public Model(int id, String name) {
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
	public void setName(String name) {
		this.name = name;
	}

	public int getExpressionId() {
		return expressionId;
	}

	public void setExpressionId(int expressionId) {
		this.expressionId = expressionId;
	}

	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}

	public int getFieldId() {
		return fieldId;
	}

	public void setFieldId(int fieldId) {
		this.fieldId = fieldId;
	}

	public short getUnitType() {
		return unitType;
	}

	public void setUnitType(short unitType) {
		this.unitType = unitType;
	}

	@Override
	public String toString() {
		return name + "|" + unitType + "|" + fieldId + "|" + expressionId + "|" + condition;
	}	

}
