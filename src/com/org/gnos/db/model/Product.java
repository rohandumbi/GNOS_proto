package com.org.gnos.db.model;

import java.util.HashSet;
import java.util.Set;

public class Product {
	
	public static final short UNIT_FIELD = 1;
	public static final short UNIT_EXPRESSION = 2;
	
	private String name;
	private int modelId;
	private Set<Integer> fieldIdList;
	private Set<Integer> expressionIdList;
	
	public Product(){
		this.expressionIdList = new HashSet<Integer>();
		this.fieldIdList = new HashSet<Integer>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getModelId() {
		return modelId;
	}

	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

	public Set<Integer> getExpressionIdList() {
		return expressionIdList;
	}

	public void setExpressionIdList(Set<Integer> expressionIdList) {
		this.expressionIdList = expressionIdList;
	}

	public Set<Integer> getFieldIdList() {
		return fieldIdList;
	}

	public void setFieldIdList(Set<Integer> fieldIdList) {
		this.fieldIdList = fieldIdList;
	}

}
