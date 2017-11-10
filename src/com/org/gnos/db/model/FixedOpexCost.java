package com.org.gnos.db.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

public class FixedOpexCost {
	
	public static int ORE_MINING_COST = 0;
	public static int WASTE_MINING_COST = 1;
	public static int STOCKPILING_COST = 2;
	public static int STOCKPILE_RECLAIMING_COST = 3;
	public static int TRUCK_HOUR_COST = 4;
	
	public static int SELECTOR_PIT = 1;
	public static int SELECTOR_PIT_GROUP = 2;
	public static int SELECTOR_STOCKPILE = 3;
	
	private int id;
	private int costType;
	private String selectorName;
	private int selectionType;
	private boolean inUse;
	private boolean isDefault;
	protected HashMap<Integer, BigDecimal> costData;
	
	public FixedOpexCost() {
		this.id = -1;
		this.costData = new LinkedHashMap<Integer, BigDecimal>();
	}
	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getCostType() {
		return costType;
	}


	public void setCostType(int costType) {
		this.costType = costType;
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


	public boolean isInUse() {
		return inUse;
	}


	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}


	public boolean isDefault() {
		return isDefault;
	}


	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}


	public HashMap<Integer, BigDecimal> getCostData() {
		return costData;
	}

	public void setCostData(HashMap<Integer, BigDecimal> costData) {
		this.costData = costData;
	}
	
	public void addCostData(int year, BigDecimal value) {
		this.costData.put(year, value);
	}


	@Override
	public String toString() {
		
		String str =  String.valueOf(costType) + "|" + selectorName + "|" + selectionType + "|" + inUse + "|" + isDefault ;
		Set<Integer> keys= costData.keySet();
		for(int year: keys) {
			str += "|"+year+","+costData.get(year);
		}
		return str;
	}
	
}
