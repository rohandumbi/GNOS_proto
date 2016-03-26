package com.org.gnos.services;

public class ProcessRoute {
	private ProcessNode start;
	private ProcessNode end;
	private int processId;
	
	public boolean isEmpty(){
		return (start==null) && (end==null);
	}
	public ProcessRoute(){
		super();
	}
	public ProcessRoute(ProcessNode node){
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
