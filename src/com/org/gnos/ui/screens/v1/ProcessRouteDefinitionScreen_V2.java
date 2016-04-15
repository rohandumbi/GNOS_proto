package com.org.gnos.ui.screens.v1;

import org.eclipse.swt.widgets.Composite;

import com.org.gnos.events.GnosEvent;
import com.org.gnos.ui.custom.controls.GnosScreen;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.wb.swt.SWTResourceManager;

public class ProcessRouteDefinitionScreen_V2 extends GnosScreen {

	public ProcessRouteDefinitionScreen_V2(Composite parent, int style) {
		super(parent, style);
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		setLayout(new FormLayout());
		
		Label labelSectionSeparator = new Label(this, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_labelSectionSeparator = new FormData();
		fd_labelSectionSeparator.top = new FormAttachment(0);
		fd_labelSectionSeparator.left = new FormAttachment(25);
		fd_labelSectionSeparator.bottom = new FormAttachment(100);
		labelSectionSeparator.setLayoutData(fd_labelSectionSeparator);
		
		Label lblAllModels = new Label(this, SWT.NONE);
		lblAllModels.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblAllModels.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		FormData fd_lblAllModels = new FormData();
		fd_lblAllModels.top = new FormAttachment(0, 10);
		fd_lblAllModels.left = new FormAttachment(0, 10);
		lblAllModels.setLayoutData(fd_lblAllModels);
		lblAllModels.setText("All Models");
		
		Label lblProcessDiagram = new Label(this, SWT.NONE);
		lblProcessDiagram.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblProcessDiagram.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		FormData fd_lblProcessDiagram = new FormData();
		fd_lblProcessDiagram.bottom = new FormAttachment(lblAllModels, 0, SWT.BOTTOM);
		fd_lblProcessDiagram.left = new FormAttachment(labelSectionSeparator, 10);
		lblProcessDiagram.setLayoutData(fd_lblProcessDiagram);
		lblProcessDiagram.setText("Generated Process Diagram");
		
		Composite compositeModelList = new Composite(this, SWT.BORDER);
		FormData fd_compositeModelList = new FormData();
		fd_compositeModelList.top = new FormAttachment(lblAllModels, 10);
		fd_compositeModelList.bottom = new FormAttachment(100, -10);
		fd_compositeModelList.left = new FormAttachment(0, 10);
		fd_compositeModelList.right = new FormAttachment(labelSectionSeparator, -10);
		compositeModelList.setLayoutData(fd_compositeModelList);
		
		Composite compositeProcessDiagram = new Composite(this, SWT.BORDER);
		FormData fd_compositeProcessDiagram = new FormData();
		fd_compositeProcessDiagram.top = new FormAttachment(lblProcessDiagram, 10);
		fd_compositeProcessDiagram.left = new FormAttachment(labelSectionSeparator, 10);
		fd_compositeProcessDiagram.bottom = new FormAttachment(100, -10);
		fd_compositeProcessDiagram.right = new FormAttachment(100, -10);
		compositeProcessDiagram.setLayoutData(fd_compositeProcessDiagram);
	}

	@Override
	public void onGnosEventFired(GnosEvent e) {
		// TODO Auto-generated method stub

	}
}
