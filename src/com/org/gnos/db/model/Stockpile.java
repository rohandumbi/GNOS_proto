package com.org.gnos.db.model;

public class Stockpile {
	private int id;
	private int stockpileType; // 0=External; 1=Internal
	private String name;
	private PitGroup associatedPitGroup;
	private int stockpileNumber;
	private boolean hasCapacity;
	private String expression;
	private int capacity;
	private boolean isReclaim;
	
	public Stockpile(String name, PitGroup pitGroup){
		this.id = -1;
		this.name = name;
		this.associatedPitGroup = pitGroup;
	}
	
	public Stockpile(){
		this.id = -1;
	}

	public String getName() {
		return name;
	}

	public PitGroup getAssociatedPitGroup() {
		return this.associatedPitGroup;
	}

	public int getStockpileNumber() {
		return stockpileNumber;
	}

	public void setStockpileNumber(int stockpileNumber) {
		this.stockpileNumber = stockpileNumber;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStockpileType() {
		return stockpileType;
	}

	public void setStockpileType(int stockpileType) {
		this.stockpileType = stockpileType;
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

	public boolean isReclaim() {
		return isReclaim;
	}

	public void setReclaim(boolean isReclaim) {
		this.isReclaim = isReclaim;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAssociatedPitGroup(PitGroup associatedPitGroup) {
		this.associatedPitGroup = associatedPitGroup;
	}
	
	
}
