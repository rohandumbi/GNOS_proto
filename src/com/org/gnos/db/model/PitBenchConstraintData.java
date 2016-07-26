package com.org.gnos.db.model;

import java.util.LinkedHashMap;

public class PitBenchConstraintData {
	private int id;
	private boolean inUse;
	private String pitName;
	private LinkedHashMap<Integer, Integer> constraintData;
	
	public PitBenchConstraintData() {
		super();
		this.id = -1;
		this.constraintData = new LinkedHashMap<Integer, Integer>();
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

	public LinkedHashMap<Integer, Integer> getConstraintData() {
		return constraintData;
	}

	public void setConstraintData(LinkedHashMap<Integer, Integer> constraintData) {
		this.constraintData = constraintData;
	}

	public void addYear(int year, int value) {
		this.constraintData.put(year, value);
	}
	
}
