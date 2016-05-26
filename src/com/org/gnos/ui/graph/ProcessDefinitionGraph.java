package com.org.gnos.ui.graph;

import java.util.HashMap;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.core.widgets.internal.GraphLabel;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import com.org.gnos.core.Node;
import com.org.gnos.core.Tree;
import com.org.gnos.db.model.Model;
import com.org.gnos.db.model.ProcessJoin;

public class ProcessDefinitionGraph extends Composite {

	private Composite parent;
	private Graph graph;
	private GraphNode rootNode;
	private GraphNode presentNode;
	private HashMap<String, GraphNode> existingProcessGraphNodes;
	private HashMap<String, GraphNode> existingProcessJoinGraphNodes;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ProcessDefinitionGraph(Composite parent, int style) {
		super(parent, style);
		this.parent = parent;
		this.setLayout(new FillLayout());
		this.existingProcessGraphNodes = new HashMap<String, GraphNode>();
		this.existingProcessJoinGraphNodes = new HashMap<String, GraphNode>();
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
				System.out.println(e.getSource());
			}

		});
		this.graph.addMenuDetectListener(new MenuDetectListener()
	    {
	        @Override
	        public void menuDetected(MenuDetectEvent e)
	        {
	            Point point = graph.toControl(e.x, e.y);
	            IFigure fig = graph.getFigureAt(point.x, point.y);

	            if (fig != null)
	            {
	                Menu menu = new Menu(getShell(), SWT.POP_UP);
	                MenuItem exit = new MenuItem(menu, SWT.NONE);
	                exit.setText("Add a product to " + ((GraphLabel) fig).getText());
	                menu.setVisible(true);
	            }
	            else
	            {
	                Menu menu = new Menu(getShell(), SWT.POP_UP);
	                MenuItem exit = new MenuItem(menu, SWT.NONE);
	                exit.setText("Nothing here...");
	                menu.setVisible(true);
	            }
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
		this.existingProcessGraphNodes.put(node.getIdentifier(), graphNode);
		if(parent != null){
			new GraphConnection(this.graph, ZestStyles.CONNECTIONS_DIRECTED, parent, graphNode);
		}

		for (Node child : children) {
			// Recursive call
			this.displayProcess(child, graphNode);
		}
	}

	public void addProcess(Node node){
		GraphNode processNode = new GraphNode(this.graph, SWT.NONE, node.getIdentifier());
		GraphNode parentNode = this.existingProcessGraphNodes.get(node.getParent().getIdentifier());
		this.existingProcessGraphNodes.put(node.getIdentifier(), processNode);
		new GraphConnection(this.graph, ZestStyles.CONNECTIONS_DIRECTED, parentNode, processNode);
		this.graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
	}
	
	public void addProcessJoin(ProcessJoin processJoin) {
		GraphNode processJoinNode = new GraphNode(this.graph, SWT.NONE, "Process Join: " + processJoin.getName());
		this.existingProcessJoinGraphNodes.put(processJoin.getName(), processJoinNode);
		for(Model model : processJoin.getlistChildProcesses()){
			GraphNode processNode = this.existingProcessGraphNodes.get(model.getName());
			new GraphConnection(this.graph, ZestStyles.CONNECTIONS_DIRECTED, processJoinNode, processNode);
		}
		this.graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}