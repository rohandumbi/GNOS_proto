package com.org.gnos.core;

import java.util.HashMap;
import java.util.Map;

public class Block {

	private int id;
	private int blockNo;
	
	private Map<String, String> fields;
	private Map<String, String> computedFields;

	public Block() {
		fields = new HashMap<String, String>();
		computedFields = new HashMap<String, String>();
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
	
	public Float getComputedField(String fieldName) {
		String val = this.computedFields.get(fieldName);
		if(val == null ) return new Float(0);
		else return Float.parseFloat(val);
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
	
	@Override
	public boolean equals(Object obj) {
		return (this.blockNo == ((Block)obj).blockNo);
	}
	
	@Override
	public int hashCode() {
		return new Integer(this.blockNo).hashCode();
	}
}
