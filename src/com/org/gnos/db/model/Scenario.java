package com.org.gnos.db.model;

public class Scenario {

	private int id = -1;
	private String name;
	private double discount;
	private int startYear;
	private int timePeriod;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getDiscount() {
		return discount;
	}
	public void setDiscount(double discount) {
		this.discount = discount;
	}
	public int getStartYear() {
		return startYear;
	}
	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}
	public int getTimePeriod() {
		return timePeriod;
	}
	public void setTimePeriod(int timePeriod) {
		this.timePeriod = timePeriod;
	}
	@Override
	public String toString() {
		return name + "|" + discount + "|" + startYear + "|" + timePeriod;
	}
	
}
