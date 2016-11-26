package com.org.gnos.core;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.db.model.Process;

public class Block {

	private int id;
	private int blockNo;
	
	private Set<Process> processes;  // Keep track of processes block can go to. Should have a better way to do this.
	
	private Map<String, String> fields;
	private Map<String, String> computedFields;

	public Block() {
		fields = new HashMap<String, String>();
		computedFields = new HashMap<String, String>();
		processes = new HashSet<Process>();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBlockNo() {
		return blockNo;
	}

	public void setBlockNo(int blockNo) {
		this.blockNo = blockNo;
	}

	public int getPitNo() {
		return Integer.parseInt(this.computedFields.get("pit_no"));
	}

	public int getBenchNo() {		
		return Integer.parseInt(this.computedFields.get("bench_no"));
	}
	
	public BigDecimal getComputedField(String fieldName) {
		String val = this.computedFields.get(fieldName);
		if(val == null ) return new BigDecimal(0);
		else return new BigDecimal(val);
	}
	
	public String getField(String fieldName) {
		return this.fields.get(fieldName);
	}

	public void addField(String key,  String ratio) {
		this.fields.put(key, ratio);
	}	
	
	public void addComputedField(String key,  String ratio) {
		this.computedFields.put(key, ratio);
	}
	
	public Set<Process> getProcesses() {
		return processes;
	}

	public void addProcess(Process p){
		processes.add(p);
	}
	@Override
	public boolean equals(Object obj) {
		return (this.blockNo == ((Block)obj).blockNo);
	}
	
	@Override
	public int hashCode() {
		return new Integer(this.blockNo).hashCode();
	}
}
