package com.org.gnos.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.org.gnos.db.model.Model;

public class Tree {

	private Node root;
	private List<Node> leafNodes = new ArrayList<Node>();
	private HashMap<String, Node> nodes = new HashMap<String, Node>();
	private int levels = 0;

	public Tree () {
		Model dummyModel = new Model("Block");
		root = new Node(dummyModel);
	}
	public void addNode(Node node, Node parent) {
		if(parent == null){
			parent = root;
		} 
		parent.addChildren(node);
		leafNodes.remove(parent);
		node.setParent(parent);	
		leafNodes.add(node);
		nodes.put(node.getIdentifier(), node);
		if(node.getLevel() > levels) {
			levels = node.getLevel();
		}
	}
	
	public List<Node> getLeafNodes() {
		return this.leafNodes;
	}
	
	public HashMap<String, Node> getNodes() {
		return this.nodes;
	}
	
	public Node getNodeByName(String name) {
		return this.nodes.get(name);
	}
	public Node getRoot() {
		return root;
	}
	public int getLevels() {
		return levels;
	}
}
