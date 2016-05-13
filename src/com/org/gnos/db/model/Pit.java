package com.org.gnos.db.model;

public class Pit {
	private int pitNumber;
	private String pitName;
	
	public Pit(int pit_no, String pit_name){
		this.pitNumber = pit_no;
		this.pitName = pit_name;
	}
	public int getPitNumber() {
		return pitNumber;
	}
	public String getPitName() {
		return pitName;
	}
}
