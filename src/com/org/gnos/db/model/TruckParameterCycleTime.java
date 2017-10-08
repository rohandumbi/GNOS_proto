package com.org.gnos.db.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Set;

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

	@Override
	public String toString() {
		String str = stockPileName;
		Set<String> keys= processData.keySet();
		for(String key: keys) {
			str += "|"+key+","+processData.get(key);
		}
		return str;
	}
	
	
	
}
