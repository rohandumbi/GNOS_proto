package com.org.gnos.db.model;

import java.util.ArrayList;

public class CapexData {
	private int id;
	private int scenarioId;
	private String name;
	private ArrayList<CapexInstance> listOfCapexInstances;
	
	public CapexData(){
		this.id = -1;
		this.scenarioId = -1;
		this.listOfCapexInstances = new ArrayList<CapexInstance>();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getScenarioId() {
		return scenarioId;
	}

	public void setScenarioId(int scenarioId) {
		this.scenarioId = scenarioId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<CapexInstance> getListOfCapexInstances() {
		return listOfCapexInstances;
	}

	public void setListOfCapexInstances(
			ArrayList<CapexInstance> listOfCapexInstances) {
		this.listOfCapexInstances = listOfCapexInstances;
	}
	
	public void addCapexInstance(CapexInstance capexInstance){
		this.listOfCapexInstances.add(capexInstance);
	}
}
