package com.org.gnos.db.model;

public class ProcessTreeNode {
	
	private int modelId;
	private int parentModelId;
	
	public int getModelId() {
		return modelId;
	}
	public void setModelId(int modelId) {
		this.modelId = modelId;
	}
	public int getParentModelId() {
		return parentModelId;
	}
	public void setParentModelId(int parentModelId) {
		this.parentModelId = parentModelId;
	}	

}
