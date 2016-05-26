package com.org.gnos.db.model;

import java.util.ArrayList;

public class ProcessJoin {
	private int id;
	private String name;
	private ArrayList<Model> listChildProcesses;

	public ProcessJoin(String name){
		super();
		this.id = -1;
		this.name = name;
		this.listChildProcesses = new ArrayList<Model>();
	}
	
	public void addProcess(Model newModel){
		this.listChildProcesses.add(newModel);
	}
	
	public String getName() {
		return name;
	}

	public ArrayList<Model> getlistChildProcesses() {
		return listChildProcesses;
	}

}
