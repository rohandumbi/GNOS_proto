package com.org.gnos.services;

public class TimePeriod {

	private int startYear;
	private int increments;
	
	public TimePeriod(int startYear, int increments){
		this.startYear = startYear;
		this.increments = increments;
	}
	
	public TimePeriod(){
		
	}
	
	public int getStartYear() {
		return startYear;
	}
	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}
	public int getIncrements() {
		return increments;
	}
	public void setIncrements(int increments) {
		this.increments = increments;
	}
}
