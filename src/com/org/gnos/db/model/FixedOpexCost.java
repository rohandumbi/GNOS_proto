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
	
	protected int costHead;
	protected HashMap<Integer, BigDecimal> costData;
	
	public FixedOpexCost() {
		this.costData = new LinkedHashMap<Integer, BigDecimal>();
	}
	
	
	public int getCostHead() {
		return costHead;
	}

	public void setCostHead(int costHead) {
		this.costHead = costHead;
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
		String str = String.valueOf(costHead);
		Set<Integer> keys= costData.keySet();
		for(int year: keys) {
			str += "|"+year+","+costData.get(year);
		}
		return str;
	}
	
}
