package com.org.gnos.db.model;

public class Dump {
	private String name;
	private PitGroup associatedPitGroup;
	private int dumpNumber;
	
	public Dump(String name, PitGroup pitGroup){
		this.name = name;
		this.associatedPitGroup = pitGroup;
	}

	public String getName() {
		return name;
	}

	public PitGroup getAssociatedPitGroup() {
		return associatedPitGroup;
	}

	public int getDumpNumber() {
		return dumpNumber;
	}

	public void setDumpNumber(int dumpNumber) {
		this.dumpNumber = dumpNumber;
	}
	
	
}
