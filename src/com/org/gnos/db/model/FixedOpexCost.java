package com.org.gnos.db.model;

import java.math.BigDecimal;
import java.util.HashMap;

public class FixedOpexCost {
	protected int id;
	protected HashMap<Integer, BigDecimal> costData;
	protected int scenarioId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public HashMap<Integer, BigDecimal> getCostData() {
		return costData;
	}

	public void setCostData(HashMap<Integer, BigDecimal> costData) {
		this.costData = costData;
	}
	
	public int getScenarioId() {
		return scenarioId;
	}
	
	public void setScenarioId(int scenarioId) {
		this.scenarioId = scenarioId;
	}
	
}
