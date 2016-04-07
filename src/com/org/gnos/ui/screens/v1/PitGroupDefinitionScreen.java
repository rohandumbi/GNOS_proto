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

public class PitGroupDefinitionScreen extends GnosScreen {

	public PitGroupDefinitionScreen(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		
		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.NORMAL));
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("Pit Group Definition Screen will come here");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onGnosEventFired(GnosEvent e) {
		// TODO Auto-generated method stub

	}
}
