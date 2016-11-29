package com.org.gnos.db.model;

import java.math.BigDecimal;
import java.util.HashMap;

public class TruckParameterCycleTime {
	private int id;
	private HashMap<String, BigDecimal> processData;
	private int projectId;
	private String stockPileName;
	
	public TruckParameterCycleTime(){
		this.id = -1;
		this.processData = new HashMap<String, BigDecimal>();
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public HashMap<String, BigDecimal> getProcessData() {
		return processData;
	}

	public void setProcessData(HashMap<String, BigDecimal> processData) {
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
