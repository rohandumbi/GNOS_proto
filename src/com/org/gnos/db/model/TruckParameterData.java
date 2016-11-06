package com.org.gnos.db.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class TruckParameterData {
	private Map<String, Integer> materialPayloadMap;
	private int fixedTime;
	
	public TruckParameterData(){
		this.materialPayloadMap = new LinkedHashMap<String, Integer>();
	}

	public Map<String, Integer> getMaterialPayloadMap() {
		return materialPayloadMap;
	}

	public void setMaterialPayloadMap(Map<String, Integer> materialPayloadMap) {
		this.materialPayloadMap = materialPayloadMap;
	}

	public int getFixedTime() {
		return fixedTime;
	}

	public void setFixedTime(int fixedTime) {
		this.fixedTime = fixedTime;
	}
	
}
