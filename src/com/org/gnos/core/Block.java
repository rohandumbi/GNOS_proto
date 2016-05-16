package com.org.gnos.core;

import java.util.HashMap;
import java.util.Map;

public class Block {

	private int blockNo;
	private int pitNo;
	private int benchNo;
	
	private Map<String, String> fields;

	public Block() {
		fields = new HashMap<String, String>();
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
		return Float.parseFloat(this.fields.get(fieldName));
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
	
	@Override
	public boolean equals(Object obj) {
		return (this.blockNo == ((Block)obj).blockNo);
	}
	
	@Override
	public int hashCode() {
		return new Integer(this.blockNo).hashCode();
	}
}
