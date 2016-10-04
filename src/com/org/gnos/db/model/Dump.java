package com.org.gnos.db.model;

public class Dump {
	private int id;
	private int dumpType; // 0=External; 1=Internal
	private String name;
	private PitGroup associatedPitGroup;
	private int dumpNumber;
	private boolean hasCapacity;
	private String expression;
	private int capacity;
	
	public Dump(String name, PitGroup pitGroup){
		this.id = -1;
		this.name = name;
		this.associatedPitGroup = pitGroup;
	}

	public Dump(){
		this.id = -1;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public int getDumpType() {
		return dumpType;
	}

	public void setDumpType(int dumpType) {
		this.dumpType = dumpType;
	}

	public boolean isHasCapacity() {
		return hasCapacity;
	}

	public void setHasCapacity(boolean hasCapacity) {
		this.hasCapacity = hasCapacity;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAssociatedPitGroup(PitGroup associatedPitGroup) {
		this.associatedPitGroup = associatedPitGroup;
	}
	
}
