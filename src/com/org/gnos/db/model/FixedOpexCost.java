package com.org.gnos.db.model;

import java.util.HashMap;

public class FixedOpexCost {
	protected int id;
	protected HashMap<Integer, Float> costData;
	protected int scenarioId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public HashMap<Integer, Float> getCostData() {
		return costData;
	}

	public void setCostData(HashMap<Integer, Float> costData) {
		this.costData = costData;
	}
	
	public int getScenarioId() {
		return scenarioId;
	}
	
	public void setScenarioId(int scenarioId) {
		this.scenarioId = scenarioId;
	}
	
}
