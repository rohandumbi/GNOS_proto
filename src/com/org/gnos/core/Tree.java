package com.org.gnos.core;

import java.util.ArrayList;
import java.util.List;

public class Tree {

	private Node root;
	private List<Node> leafNodes = new ArrayList<Node>();

	public void addNode(Node node, Node parent) {
		if(parent == null){
			root = node;
		} else {
			parent.addChildren(node);
			leafNodes.remove(parent);
			node.setParent(parent);
		}
		leafNodes.add(node);
	}
	
	public List<Node> getLeafNodes() {
		return this.leafNodes;
	}
}
