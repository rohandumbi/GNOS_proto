package com.org.gnos.db.model;

public class Pit {
	private int pit_no;
	private String pit_name;
	
	public Pit(int pit_no, String pit_name){
		this.pit_no = pit_no;
		this.pit_name = pit_name;
	}
	public int getPit_no() {
		return pit_no;
	}
	public String getPit_name() {
		return pit_name;
	}
}
