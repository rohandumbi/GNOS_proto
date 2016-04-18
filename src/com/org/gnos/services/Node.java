package com.org.gnos.services;

import java.util.ArrayList;

public class Node {
	private String identifier;
	private String parent;

	private ArrayList<String> children;
    private boolean saved;

    // Constructor
    public Node(String identifier) {
        this.identifier = identifier;
        children = new ArrayList<String>();
    }

    // Properties
    public String getIdentifier() {
        return identifier;
    }

    public ArrayList<String> getChildren() {
        return children;
    }

    // Public interface
    public void addChild(String identifier) {
        children.add(identifier);
    }

	public boolean isSaved() {
		return saved;
	}

	public void setSaved(boolean saved) {
		this.saved = saved;
	}
   
    public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	} 
    
}
