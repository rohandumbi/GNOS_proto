package com.org.gnos.db.model;

import java.util.HashSet;
import java.util.Set;

public class PitGroup {
	
	public static short CHILD_PIT = 1 ;
	public static short CHILD_PIT_GROUP = 2 ;
	
	private String name;
	private Set<String> listChildPits;
	private Set<String> listChildPitGroups;
	private int pitGroupNumber;
	
	public PitGroup(){
		this.listChildPits = new HashSet<String>();
		this.listChildPitGroups = new HashSet<String>();
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

	public Set<String> getListChildPits() {
		return listChildPits;
	}

	public Set<String> getListChildPitGroups() {
		return listChildPitGroups;
	}

	public int getPitGroupNumber() {
		return pitGroupNumber;
	}

	public void setPitGroupNumber(int pitGroupNumber) {
		this.pitGroupNumber = pitGroupNumber;
	}
	
	@Override
	public String toString() {
		String str = name ;
		str += "|";
		for (String child: listChildPits) {
			str += child +",";
		}
		str += "|";
		for (String child: listChildPitGroups) {
			str += child +",";
		}
		
		return str;
	}
}
