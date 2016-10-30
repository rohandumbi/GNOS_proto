package com.org.gnos.db.model;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

public class OreMiningCost extends FixedOpexCost{
	public OreMiningCost(){
		super();
		this.id = -1;
		this.costData = new LinkedHashMap<Integer, BigDecimal>();

	}

}
