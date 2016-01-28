package com.org.gnos.ui.screens;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.org.gnos.utilities.SWTResourceManager;

public final class HomeScreen {

	private Composite homeScreen;
	private Composite parent;

	public HomeScreen(Composite parent){
		this.parent = parent;
	}

	public void createContent(){
		//Basic layout setup
		homeScreen = new Composite(parent, SWT.NONE);
		homeScreen.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		homeScreen.setLayout(null);
		
		//Create new project button
		Button createNewProjectButton = new Button(homeScreen, SWT.NONE);
		createNewProjectButton.setGrayed(true);
		createNewProjectButton.setBounds(115, 38, 195, 50);
		createNewProjectButton.setText("Create New Project");
		createNewProjectButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//TO DO
			}
		});
		
		//Open existing project button
		Button openExisitingProjectButton = new Button(homeScreen, SWT.NONE);
		openExisitingProjectButton.setBounds(115, 110, 195, 50);
		openExisitingProjectButton.setText("Open Existing Project");
		openExisitingProjectButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//TO DO
			}
		});
	}

	public Composite render(){
		createContent();
		return homeScreen;
	}
}
