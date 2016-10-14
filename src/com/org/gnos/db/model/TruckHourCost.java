package com.org.gnos.db.model;

import java.util.LinkedHashMap;

public class TruckHourCost extends FixedOpexCost{
	public TruckHourCost(){
		super();
		this.id = -1;
		this.costData = new LinkedHashMap<Integer, Float>();

	}

}
