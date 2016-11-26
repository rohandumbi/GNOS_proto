package com.org.gnos.db.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.org.gnos.core.Block;

public class Stockpile {
	private int id;
	private int type; // 0=External; 1=Internal
	private String name;
	private PitGroup associatedPitGroup;
	private Pit associatedPit;
	private int mappingType;
	private int stockpileNumber;
	private boolean hasCapacity;
	private String condition;
	private int capacity;
	private boolean isReclaim;
	
	private Set<Block> blocks = new HashSet<Block>();
	
	public Stockpile(String name, PitGroup pitGroup){
		this.id = -1;
		this.name = name;
		this.associatedPitGroup = pitGroup;
	}
	
	public Stockpile(){
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

	public int getStockpileNumber() {
		return stockpileNumber;
	}

	public void setStockpileNumber(int stockpileNumber) {
		this.stockpileNumber = stockpileNumber;
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

	public boolean isReclaim() {
		return isReclaim;
	}

	public void setReclaim(boolean isReclaim) {
		this.isReclaim = isReclaim;
	}

	public Set<Block> getBlocks() {
		return blocks;
	}
	public void setBlocks(Set<Block> blocks) {
		this.blocks = blocks;
	}
	public void addBlock(Block block) {
		this.blocks.add(block);
	}
}
