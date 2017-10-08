package com.org.gnos.db.model;

import java.util.LinkedHashMap;
import java.util.Set;

public class GradeConstraintData {


	
	public static final int SELECTION_NONE = 0;
	public static final int SELECTION_PROCESS_JOIN = 1;
	public static final int SELECTION_PROCESS = 2;
	public static final int SELECTION_PIT = 3;
	public static final int SELECTION_PIT_GROUP = 4;
	
	private int id;
	private boolean inUse;
	private String productJoinName;
	private String selectedGradeName;
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

	public String getSelectedGradeName() {
		return selectedGradeName;
	}

	public void setSelectedGradeName(String selectedGradeName) {
		this.selectedGradeName = selectedGradeName;
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

	@Override
	public String toString() {
		String str = inUse + "|" + productJoinName + "|" + selectedGradeName + "|" + isMax + "|" + selectionType + "|"
				+ selectorName;
		Set<Integer> keys= constraintData.keySet();
		for(int year: keys) {
			str += "|"+year+","+constraintData.get(year);
		}
		
		return str;
	}
	
	
}
