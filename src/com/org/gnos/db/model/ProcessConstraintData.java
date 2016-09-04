package com.org.gnos.db.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProcessConstraintData {
	
	public static final int COEFFICIENT_NONE =0;
	public static final int COEFFICIENT_EXPRESSION =1;
	public static final int COEFFICIENT_PRODUCT =2;
	public static final int COEFFICIENT_PRODUCT_JOIN =3;
	
	public static final int SELECTION_NONE = 0;
	public static final int SELECTION_PROCESS_JOIN = 1;
	public static final int SELECTION_PROCESS = 2;
	public static final int SELECTION_PIT = 3;
	public static final int SELECTION_PIT_GROUP = 4;
	
	private int id;
	private String coefficient_name;
	
	private String selector_name;
	private boolean inUse;
	private boolean isMax;
	private int coefficientType;
	private int selectionType;
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
	
	public String getCoefficient_name() {
		return coefficient_name;
	}

	public void setCoefficient_name(String coefficient_name) {
		this.coefficient_name = coefficient_name;
	}

	public String getSelector_name() {
		return selector_name;
	}

	public void setSelector_name(String selector_name) {
		this.selector_name = selector_name;
	}

	public int getCoefficientType() {
		return coefficientType;
	}

	public void setCoefficientType(int coefficientType) {
		this.coefficientType = coefficientType;
	}

	public int getSelectionType() {
		return selectionType;
	}

	public void setSelectionType(int selectionType) {
		this.selectionType = selectionType;
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
