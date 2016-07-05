package com.org.gnos.db.model;

import java.util.LinkedHashMap;

public class GradeConstraintData {

	/*public static final int COEFFICIENT_EXPRESSION =1;
	public static final int COEFFICIENT_PRODUCT =2;
	public static final int COEFFICIENT_PRODUCT_JOIN =3;*/
	
	public static final int SELECTION_NONE = 0;
	public static final int SELECTION_PROCESS_JOIN = 1;
	public static final int SELECTION_PROCESS = 2;
	public static final int SELECTION_PIT = 3;
	public static final int SELECTION_PIT_GROUP = 4;
	
	private int id;
	private boolean inUse;
	private String productJoinName;
	private int selectedGradeIndex;
	private boolean isMax;
	private int selectionType;
	private String selectorName;
	private LinkedHashMap<Integer, Float> constraintData;
	
	public GradeConstraintData() {
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
	
	

	public String getProductJoinName() {
		return productJoinName;
	}

	public void setProductJoinName(String productJoinName) {
		this.productJoinName = productJoinName;
	}
	
	public String getSelectorName() {
		return selectorName;
	}

	public void setSelectorName(String selectorName) {
		this.selectorName = selectorName;
	}

	public int getSelectedGradeIndex() {
		return selectedGradeIndex;
	}

	public void setSelectedGradeIndex(int selectedGradeIndex) {
		this.selectedGradeIndex = selectedGradeIndex;
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
