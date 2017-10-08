package com.org.gnos.db.model;

import java.util.HashSet;
import java.util.Set;

public class ProcessJoin {
	private String name;
	private Set<Integer> childProcessList;

	public ProcessJoin(){
		this.childProcessList = new HashSet<Integer>();
	}
	
	public void addProcess(Integer modelId){
		this.childProcessList.add(modelId);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Integer> getChildProcessList() {
		return childProcessList;
	}

	public void setChildProcessList(Set<Integer> childProcessList) {
		this.childProcessList = childProcessList;
	}

	@Override
	public String toString() {
		String str = name ;
		
		for(Integer childProcess: childProcessList) {
			str += "|" +childProcess;
		}
		return str;
	}
	

}
