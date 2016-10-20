package com.org.gnos.db.model;

import java.util.HashMap;

public class TruckParameterCycleTime {
	private int id;
	private HashMap<String, Integer> processData;
	private int projectId;
	private String stockPileName;
	
	public TruckParameterCycleTime(){
		this.processData = new HashMap<String, Integer>();
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public HashMap<String, Integer> getProcessData() {
		return processData;
	}

	public void setProcessData(HashMap<String, Integer> processData) {
		this.processData = processData;
	}
	public int getProjectId() {
		return projectId;
	}
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public String getStockPileName() {
		return stockPileName;
	}
	public void setStockPileName(String stockPileName) {
		this.stockPileName = stockPileName;
	}
	
	
	
}
