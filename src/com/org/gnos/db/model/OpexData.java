package com.org.gnos.db.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class OpexData {

	private int id;
	private Model model;
	private Expression expression;
	private boolean inUse;
	private boolean isRevenue;
	private Map<Integer, Integer> costData;
	
	public OpexData(Model model) {
		super();
		this.id = -1;
		this.model = model;
		this.costData = new LinkedHashMap<Integer, Integer>();
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Model getModel() {
		return model;
	}
	public void setModel(Model model) {
		this.model = model;
	}
	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}
	public boolean isInUse() {
		return inUse;
	}
	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}
	public boolean isRevenue() {
		return isRevenue;
	}
	public void setRevenue(boolean isRevenue) {
		this.isRevenue = isRevenue;
	}
	public Map<Integer, Integer> getCostData() {
		return costData;
	}
	public void setCostData(Map<Integer, Integer> costData) {
		this.costData = costData;
	}
	
	public void addYear(int year, int value) {
		this.costData.put(year, value);
	}
	
}
