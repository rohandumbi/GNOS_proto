package com.org.gnos.ui.graph;

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
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;

public class GraphContainer extends Composite {

	private Composite parent;
	private Graph graph;
	private GraphNode rootNode;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public GraphContainer(Composite parent, int style) {
		super(parent, style);
		this.parent = parent;
		this.createContent();
	}

	private void createContent(){
		this.setLayout(new FillLayout(SWT.VERTICAL));
		this.graph = new Graph(this, SWT.NONE);
		this.rootNode = new GraphNode(graph, SWT.NONE, "BLOCK");
		this.graph.setLayoutAlgorithm(new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
		// Selection listener on graphConnect or GraphNode is not supported
		// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=236528
		this.graph.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println(e);
			}

		});
	}
	
	public void addProcessToGraph(){
		GraphNode node1 = new GraphNode(graph, SWT.NONE, "DUMMY_LEVEL_1");
		GraphNode node2 = new GraphNode(graph, SWT.NONE, "DUMMY_LEVEL_2");
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, this.rootNode,node1);
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, node1,node2);
		this.graph.setLayoutAlgorithm(new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
		this.layout();
		
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}