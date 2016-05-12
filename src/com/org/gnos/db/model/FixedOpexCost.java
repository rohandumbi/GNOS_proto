package com.org.gnos.db.model;

import java.util.Map;

public class FixedOpexCost {
	protected int id;
	protected Map<Integer, Integer> costData;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public Map<Integer, Integer> getCostData() {
		return costData;
	}

	public void setCostData(Map<Integer, Integer> costData) {
		this.costData = costData;
	}

}
