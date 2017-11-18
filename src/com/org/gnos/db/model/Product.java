package com.org.gnos.db.model;

import java.util.HashSet;
import java.util.Set;

public class Product {
	
	public static final short UNIT_FIELD = 1;
	public static final short UNIT_EXPRESSION = 2;
	
	private String name;
	private String baseProduct;
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
	
	public String getBaseProduct() {
		return baseProduct;
	}

	public void setBaseProduct(String baseProduct) {
		this.baseProduct = baseProduct;
	}

	@Override
	public String toString() {
		String str = name + "|" + modelId;
		if(fieldIdList.size() > 0) {
			str += "|"+UNIT_FIELD +"|"+fieldIdList.iterator().next();
		} else if(expressionIdList.size() > 0) {
			str += "|"+UNIT_EXPRESSION +"|"+expressionIdList.iterator().next();
		}
		
		return str;
	}

	
}
