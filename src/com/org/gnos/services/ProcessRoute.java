package com.org.gnos.services;

import org.eclipse.swt.graphics.Color;

public class ProcessRoute {
	private ProcessNode start;
	private ProcessNode end;
	private int processId;
	private String name;
	private Color processRepresentativeColor;
	

	public boolean isEmpty(){
		return (start==null) && (end==null);
	}
	
	public ProcessRoute(){
		super();
	}
	
	public ProcessRoute(String name){
		super();
		this.name = name;
	}
	public ProcessRoute(ProcessNode node){
		this.start = node;
		this.end = node;
	}
	
	public String getName() {
		return name;
	}
	/*public void setName(String name) {
		this.name = name;
	}*/
	public Color getProcessRepresentativeColor() {
		return processRepresentativeColor;
	}

	public void setProcessRepresentativeColor(Color processRepresentativeColor) {
		this.processRepresentativeColor = processRepresentativeColor;
	}
	public void addNode(ProcessNode node){
		if(this.isEmpty()){
			this.start = node;
			this.end = node;
		}else{
			this.end.setNextNode(node);
			this.end = node;
		}
	}
	public int getProcessId() {
		return processId;
	}
	public void setProcessId(int processId) {
		this.processId = processId;
	}
	public ProcessNode getStart(){
		return start;
	}
	
}
