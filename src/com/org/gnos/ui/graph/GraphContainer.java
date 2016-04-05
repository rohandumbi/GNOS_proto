package com.org.gnos.ui.graph;

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

import com.org.gnos.services.ProcessNode;
import com.org.gnos.services.ProcessRoute;

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
		this.graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
		// Selection listener on graphConnect or GraphNode is not supported
		// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=236528
		this.graph.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println(e);
			}

		});
	}
	
	public void addProcessToGraph(ProcessRoute process){
		/*GraphNode node1 = new GraphNode(graph, SWT.NONE, "DUMMY_LEVEL_1");
		GraphNode node2 = new GraphNode(graph, SWT.NONE, "DUMMY_LEVEL_2");
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, this.rootNode,node1);
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, node1,node2);
		this.graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);*/
		if(!process.isEmpty()){
			ProcessNode presentProcessNode = process.getStart();
			GraphNode lastGraphNode = this.rootNode;
			do{
				GraphNode newGraphNode = new GraphNode(graph, SWT.NONE, presentProcessNode.getModel().getName());
				GraphConnection newGraphConnection = new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, lastGraphNode,newGraphNode);
				newGraphConnection.setLineColor(process.getProcessRepresentativeColor());
				graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
				lastGraphNode = newGraphNode;
				presentProcessNode = presentProcessNode.getNextNode();
			}while(presentProcessNode != null);
		}else{
			MessageDialog.openError(this.parent.getShell(), "GNOS Error", "Process defined is empty.");
		}
		//this.layout();
		
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}