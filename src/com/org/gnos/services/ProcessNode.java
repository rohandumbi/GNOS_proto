package com.org.gnos.services;

public class ProcessNode {
	private Model model;
	private int id;
	private int value;//model index preferably
	private ProcessNode nextNode;
	
	public Model getModel() {
		return model;
	}
	public void setModel(Model model) {
		this.model = model;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public ProcessNode getNextNode() {
		return nextNode;
	}
	public void setNextNode(ProcessNode nextNode) {
		this.nextNode = nextNode;
	}
	
}
