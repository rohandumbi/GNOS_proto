package com.org.gnos.db.model;

import java.math.BigDecimal;
import java.util.HashMap;

public class TruckParameterCycleTime {
	private HashMap<String, BigDecimal> processData;
	private String stockPileName;
	
	public TruckParameterCycleTime(){
		this.processData = new HashMap<String, BigDecimal>();
	}
	
	public HashMap<String, BigDecimal> getProcessData() {
		return processData;
	}

	public void setProcessData(HashMap<String, BigDecimal> processData) {
		this.processData = processData;
	}

	public String getStockPileName() {
		return stockPileName;
	}
	public void setStockPileName(String stockPileName) {
		this.stockPileName = stockPileName;
	}
	
	
	
}
