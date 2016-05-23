package com.org.gnos.ui.screens.v1;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import com.org.gnos.core.Tree;
import com.org.gnos.ui.graph.ProcessDefinitionGraph;

public class ProcessDiagramScreen extends Composite{
	private ProcessDefinitionGraph graphContainer;

	public ProcessDiagramScreen(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		this.graphContainer = new ProcessDefinitionGraph(this, SWT.BORDER);
		this.graphContainer.setLayout(new FillLayout(SWT.HORIZONTAL));
		// TODO Auto-generated constructor stub
	}
	
	public void refresh(Tree processTree){
		this.graphContainer.refreshTree(processTree);
	}

}
