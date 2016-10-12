package com.org.gnos.db.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class CycleTimeData {
	private Map<String, String> fixedFieldMap;
	private Map<String, String> stockpileFieldMap;
	private Map<String, String> dumpFieldMap;
	private Map<String, String> childProcessFieldMap;
	
	public CycleTimeData(){
		fixedFieldMap = new LinkedHashMap<String, String>();
		stockpileFieldMap = new LinkedHashMap<String, String>();
		dumpFieldMap = new LinkedHashMap<String, String>();
		childProcessFieldMap = new LinkedHashMap<String, String>();
	}
	public Map<String, String> getFixedFieldMap() {
		return fixedFieldMap;
	}
	public void setFixedFieldMap(Map<String, String> fixedFieldMap) {
		this.fixedFieldMap = fixedFieldMap;
	}
	public Map<String, String> getStockpileFieldMap() {
		return stockpileFieldMap;
	}
	public void setStockpileFieldMap(Map<String, String> stockpileFieldMap) {
		this.stockpileFieldMap = stockpileFieldMap;
	}
	public Map<String, String> getDumpFieldMap() {
		return dumpFieldMap;
	}
	public void setDumpFieldMap(Map<String, String> dumpFieldMap) {
		this.dumpFieldMap = dumpFieldMap;
	}
	public Map<String, String> getChildProcessFieldMap() {
		return childProcessFieldMap;
	}
	public void setChildProcessFieldMap(Map<String, String> childProcessFieldMap) {
		this.childProcessFieldMap = childProcessFieldMap;
	}
	
}
