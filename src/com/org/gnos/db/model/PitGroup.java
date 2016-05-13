package com.org.gnos.db.model;

import java.util.ArrayList;

public class PitGroup {
	private String name;
	private ArrayList<Pit> listChildPits;

	private ArrayList<PitGroup> listChildPitGroups;
	
	public PitGroup(String name){
		this.name = name;
		this.listChildPits = new ArrayList<Pit>();
		this.listChildPitGroups = new ArrayList<PitGroup>();
	}
	
	public void addPit(Pit newPit){
		this.listChildPits.add(newPit);
	}
	
	public void addPitGroup(PitGroup newPitGroup){
		this.listChildPitGroups.add(newPitGroup);
	}
	
	public String getName() {
		return name;
	}

	public ArrayList<Pit> getListChildPits() {
		return listChildPits;
	}

	public ArrayList<PitGroup> getListChildPitGroups() {
		return listChildPitGroups;
	}
}
