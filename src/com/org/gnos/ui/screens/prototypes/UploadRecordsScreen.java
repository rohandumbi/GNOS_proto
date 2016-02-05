package com.org.gnos.ui.screens.prototypes;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.org.gnos.events.GnosEvent;
import com.org.gnos.events.interfaces.GnosEventGenerator;
import com.org.gnos.events.interfaces.GnosEventListener;
import com.org.gnos.utilities.SWTResourceManager;

public final class UploadRecordsScreen implements GnosEventGenerator{
	private Composite uploadRecordsScreen;
	private Composite parent;
	private ArrayList<GnosEventListener> listeners = new ArrayList<GnosEventListener>();
	
	public UploadRecordsScreen(Composite parent){
		this.parent = parent;
	}

	public void createContent(){
		uploadRecordsScreen = new Composite(parent, SWT.NONE);
		uploadRecordsScreen.setBackgroundMode(SWT.INHERIT_DEFAULT);
		//uploadRecordsScreen.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
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
		fileSubmitButton.setBounds(120, 141, 199, 25);
		fileSubmitButton.setText("Submit");
		fileSubmitButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				GnosEvent event = new GnosEvent(this, "uploadScreen:upload-records");
				fireChildEvent(event);
			}
		});
	}
	
	public Composite render(){
		createContent();
		return uploadRecordsScreen;
	}
	private void fireChildEvent(GnosEvent event){
		int j = listeners.size();
		int i = 0;
		for(i=0; i<j; i++){
			listeners.get(i).onGnosEventFired(event);
		}
	}

	@Override
	public void registerEventListener(GnosEventListener listener) {
		listeners.add(listener);
	}
}
