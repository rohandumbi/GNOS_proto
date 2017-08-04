package com.org.gnos.core;

import java.util.ArrayList;
import java.util.List;

import com.org.gnos.db.model.Model;

public class Node {

	private Node parent;
	private List<Node> childrens ;
	private Model data ;
	private boolean saved = false;
	private int level = 0;
	
	public Node(Model data) {
		this.data = data;
		this.childrens = new ArrayList<Node>();
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public Model getData() {
		return data;
	}

	public void setData(Model data) {
		this.data = data;
	}

	public List<Node> getChildrens() {
		return childrens;
	}
	
	public String getIdentifier() {
		return this.data.getName();
	}
	
	public void addChildren(Node node){
		this.childrens.add(node);
		node.setLevel(this.getLevel()+1);
	}

	public boolean isSaved() {
		return saved;
	}

	public void setSaved(boolean saved) {
		this.saved = saved;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	
}
