package com.org.gnos.ui.screens.prototypes;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.org.gnos.events.BasicScreenEvent;
import com.org.gnos.events.interfaces.ChildScreenEventGenerator;
import com.org.gnos.events.interfaces.ChildScreenEventListener;
import com.org.gnos.utilities.SWTResourceManager;

public final class HomeScreen implements ChildScreenEventGenerator{

	private Composite homeScreen;
	private Composite parent;
	private ArrayList<ChildScreenEventListener> listeners = new ArrayList<ChildScreenEventListener>();

	public HomeScreen(Composite parent){
		this.parent = parent;
	}

	public void createContent(){
		//Basic layout setup
		homeScreen = new Composite(parent, SWT.NONE);
		//homeScreen.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		homeScreen.setLayout(null);
		
		//Create new project button
		Button createNewProjectButton = new Button(homeScreen, SWT.NONE);
		createNewProjectButton.setGrayed(true);
		createNewProjectButton.setBounds(115, 38, 195, 50);
		createNewProjectButton.setText("Create New Project");
		createNewProjectButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				BasicScreenEvent event = new BasicScreenEvent(this, "homeScreen:create-new-project");
				fireChildEvent(event);
			}
		});
		
		//Open existing project button
		Button openExisitingProjectButton = new Button(homeScreen, SWT.NONE);
		openExisitingProjectButton.setBounds(115, 110, 195, 50);
		openExisitingProjectButton.setText("Open Existing Project");
		openExisitingProjectButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				BasicScreenEvent event = new BasicScreenEvent(this, "homeScreen:open-existing-project");
				fireChildEvent(event);
			}
		});
	}

	public Composite render(){
		createContent();
		return homeScreen;
	}
	
	private void fireChildEvent(BasicScreenEvent event){
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
