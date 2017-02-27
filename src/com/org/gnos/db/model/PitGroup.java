package com.org.gnos.db.model;

import java.util.ArrayList;

public class PitGroup {
	private int id;
	private String name;
	private ArrayList<Integer> listChildPits;
	private ArrayList<Integer> listChildPitGroups;
	
	public PitGroup(){
		this.id = -1;
		this.listChildPits = new ArrayList<Integer>();
		this.listChildPitGroups = new ArrayList<Integer>();
	}
	
	public PitGroup(String name){
		this.id = -1;
		this.name = name;
		this.listChildPits = new ArrayList<Integer>();
		this.listChildPitGroups = new ArrayList<Integer>();
	}
	
	public void addPit(Integer newPit){
		if(newPit == null) return;
		this.listChildPits.add(newPit);
	}
	
	public void addPitGroup(Integer newPitGroup){
		if(newPitGroup == null) return;
		this.listChildPitGroups.add(newPitGroup);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public ArrayList<Integer> getListChildPits() {
		return listChildPits;
	}

	public ArrayList<Integer> getListChildPitGroups() {
		return listChildPitGroups;
	}
}
