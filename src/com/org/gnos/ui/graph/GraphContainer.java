package com.org.gnos.ui.graph;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import com.org.gnos.services.Node;
import com.org.gnos.services.ProcessNode;
import com.org.gnos.services.ProcessRoute;
import com.org.gnos.services.Tree;

public class GraphContainer extends Composite {

	private Composite parent;
	private Graph graph;
	private GraphNode rootNode;
	private GraphNode presentNode;
	private HashMap<String, Node> processNodes;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public GraphContainer(Composite parent, int style) {
		super(parent, style);
		this.parent = parent;
		
	}

	
	public void refreshTree(Tree processTree){
		if(this.graph != null){
			this.graph.dispose();
		}
		this.graph = new Graph(this, SWT.NONE);
		this.layout();
		//processTree.display("Block");
		this.processNodes = processTree.getNodes();
		this.graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
		// Selection listener on graphConnect or GraphNode is not supported
		// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=236528
		this.graph.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println(e);
			}

		});
		this.displayProcess("Block");
	}
	
	public void displayProcess(String identifier) {
		this.displayProcess(identifier, null);
    }

    
    public void displayProcess(String identifier, GraphNode parent){
    	ArrayList<String> children = processNodes.get(identifier).getChildren();
    	GraphNode graphNode = new GraphNode(this.graph, SWT.NONE, identifier);
    	if(parent != null){
    		new GraphConnection(this.graph, ZestStyles.CONNECTIONS_DIRECTED, parent, graphNode);
    	}
    	for (String child : children) {
            // Recursive call
            this.displayProcess(child, graphNode);
        }
    }
	
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}