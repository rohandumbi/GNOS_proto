package com.org.gnos.ui.screens;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.org.gnos.events.ChildScreenEvent;
import com.org.gnos.events.interfaces.ChildScreenEventGenerator;
import com.org.gnos.events.interfaces.ChildScreenEventListener;
import com.org.gnos.utilities.SWTResourceManager;

public final class UploadRecordsScreen implements ChildScreenEventGenerator{
	private Composite uploadRecordsScreen;
	private Composite parent;
	private ArrayList<ChildScreenEventListener> listeners = new ArrayList<ChildScreenEventListener>();
	
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
				ChildScreenEvent event = new ChildScreenEvent(this, "uploadScreen:upload-records");
				fireChildEvent(event);
			}
		});
	}
	
	public Composite render(){
		createContent();
		return uploadRecordsScreen;
	}
	private void fireChildEvent(ChildScreenEvent event){
		int j = listeners.size();
		int i = 0;
		for(i=0; i<j; i++){
			listeners.get(i).onChildScreenEventFired(event);
		}
	}

	@Override
	public void registerEventListener(ChildScreenEventListener listener) {
		listeners.add(listener);
	}
}
