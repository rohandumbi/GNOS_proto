package com.org.gnos.db.model;

import java.util.ArrayList;
import java.util.List;

import com.org.gnos.core.Block;

public class Process {

	private int processNo;
	private Model model;
	private List<Block> blocks = new ArrayList<Block>();
	
	public int getProcessNo() {
		return processNo;
	}
	public void setProcessNo(int processNo) {
		this.processNo = processNo;
	}
	public Model getModel() {
		return model;
	}
	public void setModel(Model model) {
		this.model = model;
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
	
	@Override
	public boolean equals(Object obj) {
		return (this.processNo == ((Process)obj).processNo);
	}
	
	@Override
	public int hashCode() {
		return new Integer(this.processNo).hashCode();
	}
}

