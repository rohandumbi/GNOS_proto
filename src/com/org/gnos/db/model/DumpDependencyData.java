package com.org.gnos.db.model;


public class DumpDependencyData {
	private int id;
	private boolean inUse;
	private String firstPitName;
	private String firstPitGroupName;
	private String firstDumpName;
	private String dependentDumpName;

	
	public DumpDependencyData() {
		super();
		this.id = -1;
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

	public String getFirstPitName() {
		return firstPitName;
	}

	public void setFirstPitName(String firstPitName) {
		this.firstPitName = firstPitName;
	}
	
	public String getFirstPitGroupName() {
		return firstPitGroupName;
	}

	public void setFirstPitGroupName(String firstPitGroupName) {
		this.firstPitGroupName = firstPitGroupName;
	}

	public String getFirstDumpName() {
		return firstDumpName;
	}

	public void setFirstDumpName(String firstDumpName) {
		this.firstDumpName = firstDumpName;
	}

	public String getDependentDumpName() {
		return dependentDumpName;
	}

	public void setDependentDumpName(String dependentDumpName) {
		this.dependentDumpName = dependentDumpName;
	}

	
	
}
