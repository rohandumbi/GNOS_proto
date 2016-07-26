package com.org.gnos.core;

import java.util.ArrayList;
import java.util.List;

public class Pit {

	private int pitNo;
	private String pitName;
	private List<Bench> benches;
	
	public Pit() {
		benches = new ArrayList<Bench>();
	}
	
	public int getPitNo() {
		return pitNo;
	}
	public void setPitNo(int pitNo) {
		this.pitNo = pitNo;
	}
	public String getPitName() {
		return pitName;
	}
	public void setPitName(String pitName) {
		this.pitName = pitName;
	}
	public List<Bench> getBenches() {
		return benches;
	}
	public void setBenches(List<Bench> benches) {
		this.benches = benches;
	}	
	public void addBench(Bench bench) {
		this.benches.add(bench);
	}
	public Bench getBench(int benchNo){
		for(Bench b:benches){
			if(b.getBenchNo() == benchNo){
				return b;
			}
		}
		return null;
	}
}
