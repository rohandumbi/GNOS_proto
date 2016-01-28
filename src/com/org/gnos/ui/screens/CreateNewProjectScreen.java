package com.org.gnos.ui.screens;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.org.gnos.utilities.SWTResourceManager;

public final class CreateNewProjectScreen {

	private Composite createNewProjectScreen;
	private Composite parent;
	
	public CreateNewProjectScreen(Composite parent){
		this.parent = parent;
	}
	
	public void createContent(){
		//Basic layout setup
		createNewProjectScreen = new Composite(parent, SWT.NONE);
		createNewProjectScreen.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		createNewProjectScreen.setLayout(null);
		
		//Name label
		Label newProjectNameLabel = new Label(createNewProjectScreen, SWT.NONE);
		newProjectNameLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		newProjectNameLabel.setBounds(44, 42, 91, 15);
		newProjectNameLabel.setText("Name:");
		
		//Project Name text
		Text newProjectNameText = new Text(createNewProjectScreen, SWT.BORDER);
		newProjectNameText.setBounds(44, 58, 331, 21);
		
		//Project Description label
		Label projectDescriptionLabel = new Label(createNewProjectScreen, SWT.NONE);
		projectDescriptionLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		projectDescriptionLabel.setBounds(44, 96, 91, 15);
		projectDescriptionLabel.setText("Description:");
		
		//Project Description text
		Text newProjectDescriptionText = new Text(createNewProjectScreen, SWT.BORDER);
		newProjectDescriptionText.setBounds(44, 117, 331, 82);
		
		//Submit button
		Button finishNewProjectSetupButton = new Button(createNewProjectScreen, SWT.NONE);
		finishNewProjectSetupButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//TO DO
			}
		});
		finishNewProjectSetupButton.setBounds(300, 205, 75, 25);
		finishNewProjectSetupButton.setText("Finish");
		
	}
	
	public Composite render(){
		createContent();
		return createNewProjectScreen;
	}
}
