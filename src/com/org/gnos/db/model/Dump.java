package com.org.gnos.db.model;

import java.util.HashSet;
import java.util.Set;

import com.org.gnos.core.Block;

public class Dump {
	private int id;
	private int type; // 0=External; 1=Internal
	private String name;
	private String mappedTo;
	private int mappingType; // 0 - Pit, 1 - PitGroup
	private int dumpNumber;
	private boolean hasCapacity;
	private String condition;
	private int capacity;
	private Set<Block> blocks = new HashSet<Block>();
	
	public Dump(String name){
		this.id = -1;
		this.name = name;
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


	public String getMappedTo() {
		return mappedTo;
	}

	public void setMappedTo(String mappedTo) {
		this.mappedTo = mappedTo;
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
	
	public Set<Block> getBlocks() {
		return blocks;
	}
	public void setBlocks(Set<Block> blocks) {
		this.blocks = blocks;
	}
	
	public void addBlock(Block block) {
		this.blocks.add(block);
	}

	@Override
	public String toString() {
		return type + "|" + name + "|" + mappedTo + "|" + mappingType + "|" + hasCapacity + "|" + condition + "|"
				+ capacity;
	}
	
	
}
