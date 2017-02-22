package com.org.gnos.db.model;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class OpexData {

	private int id;
	private int scenarioId;
	private int modelId;
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

	public int getScenarioId() {
		return scenarioId;
	}

	public void setScenarioId(int scenarioId) {
		this.scenarioId = scenarioId;
	}
	
}
