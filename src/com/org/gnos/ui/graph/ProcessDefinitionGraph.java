package com.org.gnos.ui.graph;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import com.org.gnos.core.Node;
import com.org.gnos.core.Tree;

public class ProcessDefinitionGraph extends Composite {

	private Composite parent;
	private Graph graph;
	private GraphNode rootNode;
	private GraphNode presentNode;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ProcessDefinitionGraph(Composite parent, int style) {
		super(parent, style);
		this.parent = parent;
		
	}

	
	public void refreshTree(Tree processTree){
		if(this.graph != null){
			this.graph.dispose();
		}
		this.graph = new Graph(this, SWT.NONE);
		this.layout();
		this.graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
		// Selection listener on graphConnect or GraphNode is not supported
		// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=236528
		this.graph.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println(e);
			}

		});
		this.displayProcess(processTree.getRoot());
	}
	
	public void displayProcess(Node rootnode) {
		this.displayProcess(rootnode, null);
    }

    
    public void displayProcess(Node node, GraphNode parent){
    	List<Node> children = node.getChildrens();
    	GraphNode graphNode = new GraphNode(this.graph, SWT.NONE, node.getIdentifier());
    	if(parent != null){
    		new GraphConnection(this.graph, ZestStyles.CONNECTIONS_DIRECTED, parent, graphNode);
    	}
    	for (Node child : children) {
            // Recursive call
            this.displayProcess(child, graphNode);
        }
    }
	
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}