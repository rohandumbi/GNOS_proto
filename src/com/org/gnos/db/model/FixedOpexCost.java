package com.org.gnos.db.model;

import java.util.HashMap;

public class FixedOpexCost {
	protected int id;
	protected HashMap<Integer, Float> costData;
	
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

}
