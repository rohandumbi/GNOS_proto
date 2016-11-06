package com.org.gnos.db.model;

public class Dump {
	private int id;
	private int type; // 0=External; 1=Internal
	private String name;
	private PitGroup associatedPitGroup;
	private Pit associatedPit;
	private int mappingType; // 0 - Pit, 1 - PitGroup
	private int dumpNumber;
	private boolean hasCapacity;
	private String condition;
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PitGroup getAssociatedPitGroup() {
		return associatedPitGroup;
	}

	public void setAssociatedPitGroup(PitGroup associatedPitGroup) {
		this.associatedPitGroup = associatedPitGroup;
	}

	public Pit getAssociatedPit() {
		return associatedPit;
	}

	public void setAssociatedPit(Pit associatedPit) {
		this.associatedPit = associatedPit;
	}

	public int getMappingType() {
		return mappingType;
	}

	public void setMappingType(int mappingType) {
		this.mappingType = mappingType;
	}

	public int getDumpNumber() {
		return dumpNumber;
	}

	public void setDumpNumber(int dumpNumber) {
		this.dumpNumber = dumpNumber;
	}

	public boolean isHasCapacity() {
		return hasCapacity;
	}

	public void setHasCapacity(boolean hasCapacity) {
		this.hasCapacity = hasCapacity;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
}
