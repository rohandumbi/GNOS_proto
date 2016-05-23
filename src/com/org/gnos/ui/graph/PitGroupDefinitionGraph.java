package com.org.gnos.ui.graph;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;

import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.Pit;
import com.org.gnos.db.model.PitGroup;
import com.org.gnos.db.model.Stockpile;

public class PitGroupDefinitionGraph extends Composite {

	private Graph graph;
	private HashMap<String, GraphNode> existingGroupNodeGraph;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PitGroupDefinitionGraph(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new FillLayout());
		this.graph = new Graph(this, SWT.NONE);
		this.existingGroupNodeGraph = new HashMap<String, GraphNode>();
	}
	
	public void addGroup(PitGroup pitGroup){
		GraphNode groupNode = new GraphNode(this.graph, SWT.NONE, "Group: " + pitGroup.getName());
		this.existingGroupNodeGraph.put(pitGroup.getName(), groupNode);
		for(Pit pit : pitGroup.getListChildPits()){
			GraphNode pitNode = new GraphNode(this.graph, SWT.NONE, "Pit: " + pit.getPitName());
			new GraphConnection(this.graph, ZestStyles.CONNECTIONS_DIRECTED, pitNode, groupNode);
		}
		this.graph.setLayoutAlgorithm(new HorizontalTreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
	}
	
	public void addDumpToGroup(Dump dump){
		GraphNode dumpNode = new GraphNode(this.graph, SWT.NONE, "Dump: " + dump.getName());
		GraphNode associatedPitGroupNode = this.existingGroupNodeGraph.get(dump.getAssociatedPitGroup().getName());
		new GraphConnection(this.graph, ZestStyles.CONNECTIONS_DOT, dumpNode, associatedPitGroupNode);
		this.graph.setLayoutAlgorithm(new HorizontalTreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
	}
	
	public void addStockpileToGroup(Stockpile stockpile){
		GraphNode stockPileNode = new GraphNode(this.graph, SWT.NONE, "Stockpile: " + stockpile.getName());
		GraphNode associatedPitGroupNode = this.existingGroupNodeGraph.get(stockpile.getAssociatedPitGroup().getName());
		new GraphConnection(this.graph, ZestStyles.CONNECTIONS_DOT, stockPileNode, associatedPitGroupNode);
		this.graph.setLayoutAlgorithm(new HorizontalTreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
	}

	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}