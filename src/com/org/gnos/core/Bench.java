package com.org.gnos.core;

import java.util.ArrayList;
import java.util.List;

public class Bench {

	int benchNo;
	String benchName;
	List<Block> blocks;
	
	public Bench() {
		blocks = new ArrayList<Block>();
	}
	
	public int getBenchNo() {
		return benchNo;
	}
	public void setBenchNo(int benchNo) {
		this.benchNo = benchNo;
	}
	public String getBenchName() {
		return benchName;
	}
	public void setBenchName(String benchName) {
		this.benchName = benchName;
	}
	public List<Block> getBlocks() {
		return blocks;
	}
	public void setBlocks(List<Block> blocks) {
		this.blocks = blocks;
	}
	public void addBlock(Block block) {
		this.blocks.add(block);
	}
}
