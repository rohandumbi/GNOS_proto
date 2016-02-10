package com.org.gnos.ui.screens.v1;

import java.io.IOException;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.custom.controls.GnosScreen;
import com.org.gnos.custom.models.ProjectMetaDataModel;
import com.org.gnos.events.GnosEvent;
import com.org.gnos.events.interfaces.GnosEventListener;
import com.org.gnos.services.CSVProcessor;
import com.org.gnos.services.ColumnHeader;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class MapRequiredFieldsScreen extends GnosScreen {

	private List<ColumnHeader> allHeaders;
	private List<String> requiredFields;
	private ProjectMetaDataModel projectMetaData;
	private CSVProcessor csvProcessor;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public MapRequiredFieldsScreen(Composite parent, int style, ProjectMetaDataModel projectMetaData) {
		super(parent, style);
		this.projectMetaData = projectMetaData;
		this.allHeaders = this.getAllHeaders();
		System.out.println("Length of all columns: " + this.allHeaders.size());
		this.requiredFields = this.getRequiredFieldsFromProperties();
		//System.out.println("Length of required columns: " + this.requiredFields.size());
		this.createContent();
	}
	
	private List<String> getRequiredFieldsFromProperties(){
		return null;
	}
	
	private List<ColumnHeader> getAllHeaders(){
		try {
			csvProcessor = new CSVProcessor(this.projectMetaData.get("recordFileName"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			return csvProcessor.getHeaderColumns();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private void createContent(){
		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		lblNewLabel.setBounds(10, 10, 193, 69);
		lblNewLabel.setText("Map Required Fields Screen");
		
		Button buttonMapRqrdFields = new Button(this, SWT.NONE);
		buttonMapRqrdFields.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO mapping complete
				GnosEvent event = new GnosEvent(this, "complete:map-required-fields");
				triggerGnosEvent(event);
			}
		});
		buttonMapRqrdFields.setBounds(207, 10, 75, 25);
		buttonMapRqrdFields.setText("Next");
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}


	@Override
	public void onGnosEventFired(GnosEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	private void triggerGnosEvent(GnosEvent event){
		int j = listeners.size();
		int i = 0;
		for(i=0; i<j; i++){
			listeners.get(i).onGnosEventFired(event);
		}
	}
}
