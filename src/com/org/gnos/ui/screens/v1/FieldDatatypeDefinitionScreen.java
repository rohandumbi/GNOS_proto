package com.org.gnos.ui.screens.v1;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.custom.controls.FieldDatatypeDefinitionGrid;
import com.org.gnos.custom.controls.GnosScreen;
import com.org.gnos.custom.controls.MapRequiredFieldsGrid;
import com.org.gnos.custom.models.ProjectModel;
import com.org.gnos.events.GnosEvent;
import com.org.gnos.services.csv.ColumnHeader;

public class FieldDatatypeDefinitionScreen extends GnosScreen {

	private List<ColumnHeader> allHeaders;
	private String[] dataTypes;
	private ProjectModel projectModel;
	private FieldDatatypeDefinitionGrid fieldDatatypeDefinitionGrid;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public FieldDatatypeDefinitionScreen(Composite parent, int style, ProjectModel projectModel) {
		super(parent, style);
		setForeground(SWTResourceManager.getColor(30, 144, 255));
		setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.projectModel = projectModel;
		this.allHeaders = this.getAllHeaders();
		System.out.println("Length of all columns: " + this.allHeaders.size());
		//this.requiredFields = this.getRequiredFieldsFromProperties();
		this.dataTypes = new String[]{"String", "Integer", "Double"};
		this.createContent();

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
		labelScreenName.setText("Source Field Datatype Mappings");
		
		Label labelScreenDescription = new Label(this, SWT.NONE);
		labelScreenDescription.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		labelScreenDescription.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_labelScreenDescription = new FormData();
		fd_labelScreenDescription.top = new FormAttachment(labelScreenName, 10, SWT.BOTTOM);
		fd_labelScreenDescription.left = new FormAttachment(0, 10);
		fd_labelScreenDescription.right = new FormAttachment(0, 866);
		labelScreenDescription.setLayoutData(fd_labelScreenDescription);
		labelScreenDescription.setText("For each field in the system map the datatype.");
		
		fieldDatatypeDefinitionGrid = new FieldDatatypeDefinitionGrid(this, SWT.NONE, this.allHeaders, this.dataTypes);
		FormData fd_fieldDatatypeDefinitionGrid = new FormData();
		fd_fieldDatatypeDefinitionGrid.top = new FormAttachment(labelScreenDescription, 6);
		fd_fieldDatatypeDefinitionGrid.left = new FormAttachment(0, 10);
		fd_fieldDatatypeDefinitionGrid.right = new FormAttachment(100, -10);
		fieldDatatypeDefinitionGrid.setLayoutData(fd_fieldDatatypeDefinitionGrid);
		
		Button buttonDatatypeDefinition = new Button(this, SWT.NONE);
		buttonDatatypeDefinition.setText("NEXT");
		int offsetX = -buttonDatatypeDefinition.computeSize(SWT.DEFAULT, SWT.DEFAULT).x / 2;
		FormData fd_buttonDatatypeDefinition = new FormData();
		fd_buttonDatatypeDefinition.top = new FormAttachment(fieldDatatypeDefinitionGrid, 10, SWT.BOTTOM);
		fd_buttonDatatypeDefinition.left = new FormAttachment(50, offsetX);
		//fd_buttonMapRqrdFields.right = new FormAttachment(0, 282);
		buttonDatatypeDefinition.setLayoutData(fd_buttonDatatypeDefinition);
		buttonDatatypeDefinition.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO mapping complete
				//updateHeadersWithRequiredFieldsMapping();
				GnosEvent event = new GnosEvent(this, "complete:datatype-defintion");
				triggerGnosEvent(event);
			}
		});
	}
	
	private List<ColumnHeader> getAllHeaders(){
		return this.projectModel.getAllProjectFields();
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
