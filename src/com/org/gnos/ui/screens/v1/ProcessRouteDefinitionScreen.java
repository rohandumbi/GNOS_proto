package com.org.gnos.ui.screens.v1;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;

import com.org.gnos.events.GnosEvent;
import com.org.gnos.events.interfaces.GnosEventListener;
import com.org.gnos.ui.custom.controls.GnosScreen;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;

public class ProcessRouteDefinitionScreen extends GnosScreen {


	private Composite parent;
	private Graph graph;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ProcessRouteDefinitionScreen(Composite parent, int style) {
		super(parent, style);

		/*Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		lblNewLabel.setBounds(10, 10, 228, 32);
		lblNewLabel.setText("Process Route Definition Screen");

		Button buttonProcessRouteDefined = new Button(this, SWT.NONE);
		buttonProcessRouteDefined.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO process route defined
				GnosEvent event = new GnosEvent(this, "complete:process-route-defintion");
				triggerGnosEvent(event);
			}
		});
		buttonProcessRouteDefined.setBounds(241, 10, 75, 25);
		buttonProcessRouteDefined.setText("Next");*/

		setForeground(SWTResourceManager.getColor(30, 144, 255));
		setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.parent = parent;
		//this.projectModel = projectModel;
		//this.allHeaders = this.getAllHeaders();
		this.createContent();

	}

	private void createContent(){
		setLayout(new FormLayout());
		Composite containerComposite = new Composite(this, SWT.NONE);
		containerComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		FormData fd_containerComposite = new FormData();
		fd_containerComposite.left = new FormAttachment(0, 6);
		fd_containerComposite.right = new FormAttachment(100, -6);
		fd_containerComposite.top = new FormAttachment(0, 6);
		fd_containerComposite.bottom = new FormAttachment(100, -6);
		containerComposite.setLayoutData(fd_containerComposite);

		graph = new Graph(containerComposite, SWT.NONE);
		// now a few nodes
		GraphNode node1 = new GraphNode(graph, SWT.NONE, "Jim");
		GraphNode node2 = new GraphNode(graph, SWT.NONE, "Jack");
		GraphNode node3 = new GraphNode(graph, SWT.NONE, "Joe");
		GraphNode node4 = new GraphNode(graph, SWT.NONE, "Bill");
		// Lets have a directed connection
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, node1,
				node2);
		// Lets have a dotted graph connection
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DOT, node2, node3);
		// Standard connection
		new GraphConnection(graph, SWT.NONE, node3, node1);
		// Change line color and line width
		GraphConnection graphConnection = new GraphConnection(graph, SWT.NONE,
				node1, node4);
		graphConnection.changeLineColor(parent.getDisplay().getSystemColor(SWT.COLOR_GREEN));
		// Also set a text
		graphConnection.setText("This is a text");
		graphConnection.setHighlightColor(parent.getDisplay().getSystemColor(SWT.COLOR_RED));
		graphConnection.setLineWidth(3);

		graph.setLayoutAlgorithm(new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
		// Selection listener on graphConnect or GraphNode is not supported
		// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=236528
		graph.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println(e);
			}

		});
		//this.parent.layout();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}


	@Override
	public void onGnosEventFired(GnosEvent e) {
		// TODO Auto-generated method stub

	}

	private void triggerGnosEvent(GnosEvent event){
		int j = listeners.size();
		int i = 0;
		for(i=0; i<j; i++){
			listeners.get(i).onGnosEventFired(event);
		}
	}

}
