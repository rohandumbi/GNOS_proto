package com.org.gnos.db.model;

import java.util.LinkedHashMap;

public class StockpileReclaimingCost extends FixedOpexCost{
	public StockpileReclaimingCost(){
		super();
		this.id = -1;
		this.costData = new LinkedHashMap<Integer, Float>();
	}

}
