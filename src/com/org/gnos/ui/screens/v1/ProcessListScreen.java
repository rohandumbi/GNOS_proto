package com.org.gnos.ui.screens.v1;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.services.ProcessRoute;

public class ProcessListScreen extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private Composite lastProcess;
	
	public ProcessListScreen(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new FormLayout());

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	public void addProcess(ProcessRoute processRoute){
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new FormLayout());
		FormData fd_composite = new FormData();
		if(this.lastProcess != null){
			fd_composite.top = new FormAttachment(this.lastProcess, 10, SWT.BOTTOM);
			fd_composite.bottom = new FormAttachment(this.lastProcess, 38, SWT.BOTTOM);
		}else{
			fd_composite.top = new FormAttachment(0, 10);
			fd_composite.bottom = new FormAttachment(0, 38);
		}
		fd_composite.left = new FormAttachment(0, 10);
		fd_composite.right = new FormAttachment(100, -10);
		composite.setLayoutData(fd_composite);
		
		Label colorLabel = new Label(composite, SWT.NONE);
		colorLabel.setBackground(processRoute.getProcessRepresentativeColor());
		FormData fd_colorLabel = new FormData();
		
		//int offsetX = -colorLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).x / 2;
		int offsetYColorLabel = -colorLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y / 2;
		//fd_composite.left = new FormAttachment(50,offsetX);
		
		fd_colorLabel.left = new FormAttachment(0, 10);
		//fd_colorLabel.top = new FormAttachment(0, 10);
		fd_colorLabel.right = new FormAttachment(0, 40);
		fd_colorLabel.top = new FormAttachment(50, offsetYColorLabel);
		colorLabel.setLayoutData(fd_colorLabel);
		
		Label lblProcessName = new Label(composite, SWT.NONE);
		lblProcessName.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		lblProcessName.setText(processRoute.getName());
		FormData fd_lblProcessName = new FormData();
		fd_lblProcessName.right = new FormAttachment(0, 177);
		//fd_lblProcessName.top = new FormAttachment(0, 10);
		fd_lblProcessName.left = new FormAttachment(colorLabel, 5, SWT.RIGHT);
		//int offsetXLblProcessName = -lblProcessName.computeSize(SWT.DEFAULT, SWT.DEFAULT).x / 2;
		int offsetYLblProcessName = -lblProcessName.computeSize(SWT.DEFAULT, SWT.DEFAULT).y / 2;
		fd_lblProcessName.top = new FormAttachment(50, offsetYLblProcessName);
		//fd_lblProcessName.left = new FormAttachment(50, offsetXLblProcessName);
		lblProcessName.setLayoutData(fd_lblProcessName);
		
		this.lastProcess = composite;
		this.layout();
	}

}
