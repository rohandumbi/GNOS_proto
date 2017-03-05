package com.org.gnos.db.model;

import java.util.HashSet;
import java.util.Set;

public class Product {
	private String name;
	private int modelId; 
	private Set<Integer> expressionIdList;
	
	public Product(){
		this.expressionIdList = new HashSet<Integer>();
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

}
