package com.org.gnos.db.model;

import java.util.HashMap;

public class FixedOpexCost {
	protected int id;
	protected HashMap<Integer, Integer> costData;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public HashMap<Integer, Integer> getCostData() {
		return costData;
	}

	public void setCostData(HashMap<Integer, Integer> costData) {
		this.costData = costData;
	}

}
