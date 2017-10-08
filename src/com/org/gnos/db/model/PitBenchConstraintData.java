package com.org.gnos.db.model;

import java.util.LinkedHashMap;
import java.util.Set;

public class PitBenchConstraintData {
	private int id;
	private boolean inUse;
	private String pitName;
	private LinkedHashMap<Integer, Float> constraintData;
	
	public PitBenchConstraintData() {
		super();
		this.id = -1;
		this.constraintData = new LinkedHashMap<Integer, Float>();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isInUse() {
		return inUse;
	}

	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}

	public String getPitName() {
		return pitName;
	}

	public void setPitName(String pitName) {
		this.pitName = pitName;
	}

	public LinkedHashMap<Integer, Float> getConstraintData() {
		return constraintData;
	}

	public void setConstraintData(LinkedHashMap<Integer, Float> constraintData) {
		this.constraintData = constraintData;
	}

	public void addYear(int year, float value) {
		this.constraintData.put(year, value);
	}

	@Override
	public String toString() {
		String str = inUse + "|" + pitName;
		
		Set<Integer> keys= constraintData.keySet();
		for(int year: keys) {
			str += "|"+year+","+constraintData.get(year);
		}
		
		return str;
	}
	
}
