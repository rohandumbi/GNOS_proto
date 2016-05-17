package com.org.gnos.db.model;

public class DiscountFactor {
	private int id;
	private float value;
	
	public DiscountFactor(){
		this.id = -1;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

}
