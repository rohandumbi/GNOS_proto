package com.org.gnos.db.model;

import java.util.ArrayList;

public class PitGroup {
	
	public static short CHILD_PIT = 1 ;
	public static short CHILD_PIT_GROUP = 2 ;
	
	private String name;
	private ArrayList<String> listChildPits;
	private ArrayList<String> listChildPitGroups;
	
	public PitGroup(){
		this.listChildPits = new ArrayList<String>();
		this.listChildPitGroups = new ArrayList<String>();
	}
	
	public PitGroup(String name){
		this.name = name;
		this.listChildPits = new ArrayList<String>();
		this.listChildPitGroups = new ArrayList<String>();
	}
	
	public void addPit(String newPit){
		if(newPit == null) return;
		this.listChildPits.add(newPit);
	}
	
	public void addPitGroup(String newPitGroup){
		if(newPitGroup == null) return;
		this.listChildPitGroups.add(newPitGroup);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<String> getListChildPits() {
		return listChildPits;
	}

	public ArrayList<String> getListChildPitGroups() {
		return listChildPitGroups;
	}
}
