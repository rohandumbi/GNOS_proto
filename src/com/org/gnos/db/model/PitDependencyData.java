package com.org.gnos.db.model;


public class PitDependencyData {
	private int id;
	private boolean inUse;
	private String firstPitName;
	private String firstPitAssociatedBench;
	private String dependentPitName;
	private String dependentPitAssociatedBench;
	private int minLead;
	private int maxLead;
	
	public PitDependencyData() {
		super();
		this.id = -1;
		this.minLead = -1;
		this.maxLead = -1;
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

	public String getFirstPitAssociatedBench() {
		return firstPitAssociatedBench;
	}

	public void setFirstPitAssociatedBench(String firstPitAssociatedBench) {
		this.firstPitAssociatedBench = firstPitAssociatedBench;
	}

	public String getDependentPitName() {
		return dependentPitName;
	}

	public void setDependentPitName(String dependentPitName) {
		this.dependentPitName = dependentPitName;
	}

	public String getDependentPitAssociatedBench() {
		return dependentPitAssociatedBench;
	}
	
	public void setDependentPitAssociatedBench(String dependentPitAssociatedBench) {
		this.dependentPitAssociatedBench = dependentPitAssociatedBench;
	}

	public int getMinLead() {
		return minLead;
	}

	public void setMinLead(int minLead) {
		this.minLead = minLead;
	}

	public int getMaxLead() {
		return maxLead;
	}

	public void setMaxLead(int maxLead) {
		this.maxLead = maxLead;
	}

	@Override
	public String toString() {
		
		return inUse + "|" + escapeNull(firstPitName) + "|" + escapeNull(firstPitAssociatedBench) + "|" + escapeNull(dependentPitName) + "|"
				+ escapeNull(dependentPitAssociatedBench) + "|" + minLead + "|" + maxLead;
	}
	
	private String escapeNull(String s) {
		if(s == null) {
			return "";
		}
		return s;
	}
	
}
