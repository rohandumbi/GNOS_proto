package com.org.gnos.db.model;

import java.util.LinkedHashMap;

public class OreMiningCost extends FixedOpexCost{
	public OreMiningCost(){
		super();
		this.id = -1;
		this.costData = new LinkedHashMap<Integer, Float>();

	}

}
