package com.org.gnos.ui.screens.v1;

import java.io.IOException;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.application.GNOSConfig;
import com.org.gnos.custom.controls.GnosScreen;
import com.org.gnos.custom.controls.MapRequiredFieldsGrid;
import com.org.gnos.custom.models.ProjectMetaDataModel;
import com.org.gnos.events.GnosEvent;
import com.org.gnos.services.CSVProcessor;
import com.org.gnos.services.ColumnHeader;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

public class MapRequiredFieldsScreen_V2 extends GnosScreen {

	private List<ColumnHeader> allHeaders;
	private String[] requiredFields;
	private String[] dataTypes;
	private ProjectMetaDataModel projectMetaData;
	private CSVProcessor csvProcessor;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public MapRequiredFieldsScreen_V2(Composite parent, int style, ProjectMetaDataModel projectMetaData) {
		super(parent, style);
		setForeground(SWTResourceManager.getColor(30, 144, 255));
		setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.projectMetaData = projectMetaData;
		this.allHeaders = this.getAllHeaders();
		System.out.println("Length of all columns: " + this.allHeaders.size());
		this.requiredFields = this.getRequiredFieldsFromProperties();
		this.dataTypes = new String[]{"String", "Integer", "Double"};
		System.out.println("Length of required columns: " + this.requiredFields.length);
		this.createContent();
	}
	
	private String[] getRequiredFieldsFromProperties(){
		String[] requiredFields = GNOSConfig.get("fields.required").split("#");
		return requiredFields;
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
		setLayout(new FormLayout());
		Label labelScreenName = new Label(this, SWT.NONE);
		labelScreenName.setForeground(SWTResourceManager.getColor(0, 191, 255));
		labelScreenName.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_labelScreenName = new FormData();
		//fd_labelScreenName.bottom = new FormAttachment(100, -461);
		fd_labelScreenName.top = new FormAttachment(0, 10);
		fd_labelScreenName.left = new FormAttachment(0, 10);
		labelScreenName.setLayoutData(fd_labelScreenName);
		labelScreenName.setFont(SWTResourceManager.getFont("Arial", 9, SWT.BOLD));
		labelScreenName.setText("Required Field Mappings");
		
		Button buttonMapRqrdFields = new Button(this, SWT.NONE);
		FormData fd_buttonMapRqrdFields = new FormData();
		fd_buttonMapRqrdFields.top = new FormAttachment(labelScreenName, -5, SWT.TOP);
		fd_buttonMapRqrdFields.left = new FormAttachment(labelScreenName, 59);
		fd_buttonMapRqrdFields.right = new FormAttachment(0, 282);
		buttonMapRqrdFields.setLayoutData(fd_buttonMapRqrdFields);
		buttonMapRqrdFields.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO mapping complete
				GnosEvent event = new GnosEvent(this, "complete:map-required-fields");
				triggerGnosEvent(event);
			}
		});
		buttonMapRqrdFields.setText("Next");
		
		Label labelScreenDescription = new Label(this, SWT.NONE);
		labelScreenDescription.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		labelScreenDescription.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_labelScreenDescription = new FormData();
		fd_labelScreenDescription.top = new FormAttachment(labelScreenName, 10, SWT.BOTTOM);
		fd_labelScreenDescription.left = new FormAttachment(0, 10);
		fd_labelScreenDescription.right = new FormAttachment(0, 866);
		labelScreenDescription.setLayoutData(fd_labelScreenDescription);
		labelScreenDescription.setText("For each required field in the system map your custom field and also specify its datatype.");
		
		MapRequiredFieldsGrid mapRequiredFieldsGrid = new MapRequiredFieldsGrid(this, SWT.NONE, this.requiredFields, this.allHeaders, this.dataTypes);
		FormData fd_mapRequiredFieldsGrid = new FormData();
		fd_mapRequiredFieldsGrid.top = new FormAttachment(labelScreenDescription, 6);
		fd_mapRequiredFieldsGrid.left = new FormAttachment(0, 10);
		fd_mapRequiredFieldsGrid.right = new FormAttachment(100, -10);
		mapRequiredFieldsGrid.setLayoutData(fd_mapRequiredFieldsGrid);
			
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
