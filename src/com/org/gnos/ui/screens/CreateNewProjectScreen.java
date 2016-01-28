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

public final class CreateNewProjectScreen implements ChildScreenEventGenerator{

	private Composite createNewProjectScreen;
	private Composite parent;
	private ArrayList<ChildScreenEventListener> listeners = new ArrayList<ChildScreenEventListener>();
	
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
				ChildScreenEvent event = new ChildScreenEvent(this, "createNewProjectScreen:upload-records");
				fireChildEvent(event);
			}
		});
		finishNewProjectSetupButton.setBounds(300, 205, 75, 25);
		finishNewProjectSetupButton.setText("Finish");
		
	}
	
	public Composite render(){
		createContent();
		return createNewProjectScreen;
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
		// TODO Auto-generated method stub
		listeners.add(listener);
	}
}
