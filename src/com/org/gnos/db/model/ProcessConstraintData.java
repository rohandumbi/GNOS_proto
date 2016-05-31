package com.org.gnos.db.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProcessConstraintData {

	private int id;
	private ProcessJoin processJoin;
	private Expression expression;
	private boolean inUse;
	private boolean isMax;
	private LinkedHashMap<Integer, Float> constraintData;
	
	public ProcessConstraintData() {
		super();
		this.id = -1;
		this.constraintData = new LinkedHashMap<Integer, Float>();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ProcessJoin getProcessJoin() {
		return processJoin;
	}

	public void setProcessJoin(ProcessJoin processJoin) {
		this.processJoin = processJoin;
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

	public boolean isMax() {
		return isMax;
	}

	public void setMax(boolean isMax) {
		this.isMax = isMax;
	}

	public LinkedHashMap<Integer, Float> getConstraintData() {
		return constraintData;
	}

	public void setConstraintData(LinkedHashMap<Integer, Float> constraintData) {
		this.constraintData = constraintData;
	}

	public void addYear(int year, float value) {
		this.constraintData.put(year, value);
	}
	
}
