package com.org.gnos.db.model;

public class ProcessTreeNodeState {
	
	private String nodeName;
	private float xLoc;
	private float yLoc;
	
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public float getxLoc() {
		return xLoc;
	}
	public void setxLoc(float xLoc) {
		this.xLoc = xLoc;
	}
	public float getyLoc() {
		return yLoc;
	}
	public void setyLoc(float yLoc) {
		this.yLoc = yLoc;
	}
	
	@Override
	public String toString() {
		return nodeName + "|" + xLoc + "|" + yLoc;
	}	
	
}
