package com.org.gnos.db.model;

import java.util.ArrayList;

public class ProcessJoin {
	private String name;
	private ArrayList<Model> listChildProcesses;

	public ProcessJoin(String name){
		this.name = name;
		this.listChildProcesses = new ArrayList<Model>();
	}
	
	public void addProcess(Model newModel){
		this.listChildProcesses.add(newModel);
	}
	
	
	
	public String getName() {
		return name;
	}

	public ArrayList<Model> getListChildPits() {
		return listChildProcesses;
	}

}
