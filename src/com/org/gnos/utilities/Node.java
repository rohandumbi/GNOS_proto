package com.org.gnos.utilities;

import java.util.ArrayList;
import java.util.List;

//http://stackoverflow.com/questions/21718669/how-to-create-own-tree-in-java

public class Node {
	private List<Node> children = null;
    private String value;

    public Node(String value)
    {
        this.children = new ArrayList<>();
        this.value = value;
    }

    public void addChild(Node child)
    {
        children.add(child);
    }
}
