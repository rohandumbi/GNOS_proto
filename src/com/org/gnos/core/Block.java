package com.org.gnos.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Block {

	private int id;
	private int blockNo;
	private int pitNo;
	private int benchNo;
	private List<Integer> processNumbers;
	
	private Map<String, String> fields;

	public Block() {
		fields = new HashMap<String, String>();
		processNumbers = new ArrayList();
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
		return Integer.parseInt(this.fields.get("pit_no"));
	}

	public int getBenchNo() {		
		return Integer.parseInt(this.fields.get("bench_no"));
	}
	
	public Float getRatioField(String fieldName) {
		String val = this.fields.get(fieldName);
		if(val == null ) return new Float(0);
		else return Float.parseFloat(val);
	}
	
	public Map<String, String> getFields() {
		return fields;
	}

	public void setFields(Map<String, String> fields) {
		this.fields = fields;
	}

	public void addField(String key,  String ratio) {
		this.fields.put(key, ratio);
	}
	
	
	public void addProcess(int processNo) {
		this.processNumbers.add(processNo);
	}
	
	public List<String> getProcessVariables() {
		List<String> variables = new ArrayList();
		for(int processNumber: processNumbers){
			String variable = "p"+this.getPitNo()+"x"+this.getBlockNo()+"p"+processNumber;
			variables.add(variable);
		}
		
		return variables;
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
