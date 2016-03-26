package com.org.gnos.services;

public class Process {
	private ProcessNode start;
	private ProcessNode end;
	
	public boolean isEmpty(){
		return (start==null) && (end==null);
	}
	public Process(){
		super();
	}
	public Process(ProcessNode node){
		this.start = node;
		this.end = node;
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
	
}
