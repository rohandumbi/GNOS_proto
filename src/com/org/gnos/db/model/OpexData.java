package com.org.gnos.db.model;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class OpexData {

	public static final short UNIT_FIELD = 1;
	public static final short UNIT_EXPRESSION = 2;
	
	private int id;
	private int modelId;
	private short unitType;
	private int fieldId;
	private int expressionId;
	private boolean inUse;
	private boolean isRevenue;
	private LinkedHashMap<Integer, BigDecimal> costData;
	
	public OpexData(){
		super();
		this.id = -1;
		this.costData = new LinkedHashMap<Integer, BigDecimal>();
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public int getModelId() {
		return modelId;
	}

	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

	public int getExpressionId() {
		return expressionId;
	}

	public void setExpressionId(int expressionId) {
		this.expressionId = expressionId;
	}

	public boolean isInUse() {
		return inUse;
	}
	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}
	public boolean isRevenue() {
		return isRevenue;
	}
	public void setRevenue(boolean isRevenue) {
		this.isRevenue = isRevenue;
	}
	public Map<Integer, BigDecimal> getCostData() {
		return costData;
	}
	
	public void addYear(int year, BigDecimal value) {
		this.costData.put(year, value);
	}

	public short getUnitType() {
		return unitType;
	}

	public void setUnitType(short unitType) {
		this.unitType = unitType;
	}

	public int getFieldId() {
		return fieldId;
	}

	public void setFieldId(int fieldId) {
		this.fieldId = fieldId;
	}
	
}
