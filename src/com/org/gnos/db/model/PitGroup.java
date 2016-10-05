package com.org.gnos.db.model;

import java.util.ArrayList;

public class PitGroup {
	private int id;
	private String name;
	private ArrayList<Pit> listChildPits;
	private ArrayList<PitGroup> listChildPitGroups;
	
	public PitGroup(String name){
		this.id = -1;
		this.name = name;
		this.listChildPits = new ArrayList<Pit>();
		this.listChildPitGroups = new ArrayList<PitGroup>();
	}
	
	public void addPit(Pit newPit){
		if(newPit == null) return;
		this.listChildPits.add(newPit);
	}
	
	public void addPitGroup(PitGroup newPitGroup){
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

	public ArrayList<Pit> getListChildPits() {
		return listChildPits;
	}

	public ArrayList<PitGroup> getListChildPitGroups() {
		return listChildPitGroups;
	}
}
