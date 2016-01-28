package com.org.gnos.ui.screens;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.org.gnos.utilities.SWTResourceManager;

public final class UploadRecords {
	private Composite uploadRecordsScreen;
	private Composite parent;
	
	public UploadRecords(Composite parent){
		this.parent = parent;
	}

	public void createContent(){
		uploadRecordsScreen = new Composite(parent, SWT.NONE);
		uploadRecordsScreen.setBackgroundMode(SWT.INHERIT_DEFAULT);
		uploadRecordsScreen.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		uploadRecordsScreen.setLayout(null);
		
		Label selectFileLabel = new Label(uploadRecordsScreen, SWT.NONE);
		selectFileLabel.setBounds(22, 28, 60, 15);
		selectFileLabel.setText("Select File:");
		
		Text chosenFileText = new Text(uploadRecordsScreen, SWT.BORDER);
		chosenFileText.setBounds(88, 22, 264, 21);
		
		Button fileBrowserButton = new Button(uploadRecordsScreen, SWT.NONE);
		fileBrowserButton.setBounds(358, 23, 75, 20);
		fileBrowserButton.setText("Browse...");
		
		Button fileSubmitButton = new Button(uploadRecordsScreen, SWT.NONE);
		fileSubmitButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//TO DO
			}
		});
	}
	
	public Composite render(){
		createContent();
		return uploadRecordsScreen;
	}
}
