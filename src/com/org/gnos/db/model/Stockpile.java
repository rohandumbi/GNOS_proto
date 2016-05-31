package com.org.gnos.db.model;

public class Stockpile {
	private String name;
	private PitGroup associatedPitGroup;
	private int stockpileNumber;
	
	public Stockpile(String name, PitGroup pitGroup){
		this.name = name;
		this.associatedPitGroup = pitGroup;
	}

	public String getName() {
		return name;
	}

	public PitGroup getAssociatedPitGroup() {
		return associatedPitGroup;
	}

	public int getStockpileNumber() {
		return stockpileNumber;
	}

	public void setStockpileNumber(int stockpileNumber) {
		this.stockpileNumber = stockpileNumber;
	}
	
	
}
