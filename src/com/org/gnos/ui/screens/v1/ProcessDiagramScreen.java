package com.org.gnos.ui.screens.v1;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;

import com.org.gnos.services.Tree;
import com.org.gnos.ui.graph.GraphContainer;

public class ProcessDiagramScreen extends Composite{
	private GraphContainer graphContainer;

	public ProcessDiagramScreen(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		this.graphContainer = new GraphContainer(this, SWT.BORDER);
		this.graphContainer.setLayout(new FillLayout(SWT.HORIZONTAL));
		// TODO Auto-generated constructor stub
	}
	
	public void refresh(Tree processTree){
		this.graphContainer.refreshTree(processTree);
	}

}
