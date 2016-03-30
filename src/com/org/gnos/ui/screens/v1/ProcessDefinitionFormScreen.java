package com.org.gnos.ui.screens.v1;

import org.eclipse.swt.widgets.Composite;

import com.org.gnos.events.GnosEvent;
import com.org.gnos.ui.custom.controls.GnosScreen;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;

public class ProcessDefinitionFormScreen extends GnosScreen {
	private Text text;

	public ProcessDefinitionFormScreen(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		
		Label lblProcessName = new Label(this, SWT.NONE);
		FormData fd_lblProcessName = new FormData();
		fd_lblProcessName.top = new FormAttachment(0, 37);
		fd_lblProcessName.left = new FormAttachment(0, 44);
		lblProcessName.setLayoutData(fd_lblProcessName);
		lblProcessName.setText("Process Name:");
		
		text = new Text(this, SWT.BORDER);
		FormData fd_text = new FormData();
		fd_text.right = new FormAttachment(lblProcessName, 522, SWT.RIGHT);
		fd_text.top = new FormAttachment(0, 37);
		fd_text.left = new FormAttachment(lblProcessName, 6);
		text.setLayoutData(fd_text);
		
		Label lblStep = new Label(this, SWT.NONE);
		FormData fd_lblStep = new FormData();
		fd_lblStep.top = new FormAttachment(lblProcessName, 52);
		fd_lblStep.left = new FormAttachment(lblProcessName, 0, SWT.LEFT);
		lblStep.setLayoutData(fd_lblStep);
		lblStep.setText("Step 1:");
		
		Combo combo = new Combo(this, SWT.NONE);
		FormData fd_combo = new FormData();
		fd_combo.bottom = new FormAttachment(lblStep, 0, SWT.BOTTOM);
		fd_combo.left = new FormAttachment(text, 0, SWT.LEFT);
		combo.setLayoutData(fd_combo);
		
		Label lblStep_1 = new Label(this, SWT.NONE);
		FormData fd_lblStep_1 = new FormData();
		fd_lblStep_1.top = new FormAttachment(lblStep, 39);
		fd_lblStep_1.left = new FormAttachment(lblProcessName, 0, SWT.LEFT);
		lblStep_1.setLayoutData(fd_lblStep_1);
		lblStep_1.setText("Step 2:");
		
		Combo combo_1 = new Combo(this, SWT.NONE);
		FormData fd_combo_1 = new FormData();
		fd_combo_1.bottom = new FormAttachment(lblStep_1, 0, SWT.BOTTOM);
		fd_combo_1.left = new FormAttachment(text, 0, SWT.LEFT);
		combo_1.setLayoutData(fd_combo_1);
		
		Label lblStep_2 = new Label(this, SWT.NONE);
		FormData fd_lblStep_2 = new FormData();
		fd_lblStep_2.top = new FormAttachment(lblStep_1, 42);
		fd_lblStep_2.left = new FormAttachment(lblProcessName, 0, SWT.LEFT);
		lblStep_2.setLayoutData(fd_lblStep_2);
		lblStep_2.setText("Step 3:");
		
		Combo combo_2 = new Combo(this, SWT.NONE);
		FormData fd_combo_2 = new FormData();
		fd_combo_2.top = new FormAttachment(lblStep_2, 0, SWT.TOP);
		fd_combo_2.left = new FormAttachment(text, 0, SWT.LEFT);
		combo_2.setLayoutData(fd_combo_2);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onGnosEventFired(GnosEvent e) {
		// TODO Auto-generated method stub

	}
}
