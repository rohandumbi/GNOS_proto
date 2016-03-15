package com.org.gnos.ui.screens.v1;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
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
import com.org.gnos.custom.models.ProjectModel;
import com.org.gnos.events.GnosEvent;
import com.org.gnos.services.csv.ColumnHeader;

public class FieldDatatypeDefinitionScreen extends GnosScreen {

	private String[] allHeaders;
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
		System.out.println("Length of all columns: " + this.allHeaders.length);
		//this.requiredFields = this.getRequiredFieldsFromProperties();
		this.dataTypes = new String[]{"Number", "Text"/*, "Double"*/};
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
		//int offsetX = -buttonDatatypeDefinition.computeSize(SWT.DEFAULT, SWT.DEFAULT).x / 2;
		FormData fd_buttonDatatypeDefinition = new FormData();
		fd_buttonDatatypeDefinition.right = new FormAttachment(50);
		fd_buttonDatatypeDefinition.top = new FormAttachment(fieldDatatypeDefinitionGrid, 10, SWT.BOTTOM);
		fd_buttonDatatypeDefinition.left = new FormAttachment(50, -145);
		//fd_buttonMapRqrdFields.right = new FormAttachment(0, 282);
		buttonDatatypeDefinition.setLayoutData(fd_buttonDatatypeDefinition);
		buttonDatatypeDefinition.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO mapping complete
				//projectModel.setAllProjectFields(fieldDatatypeDefinitionGrid.getFieldDatatypes());
				fieldDatatypeDefinitionGrid.setFieldDatatypes();
				//System.out.println("After mapping datatype of 3rd row is: " + projectModel.getAllProjectFields().get(2).getDataType());
				GnosEvent event = new GnosEvent(this, "complete:datatype-defintion");
				triggerGnosEvent(event);
			}
		});

		/*
		 * Temporary Save button
		 */

		final Button buttonSave = new Button(this, SWT.NONE);
		buttonSave.setImage(null);
		buttonSave.setForeground(SWTResourceManager.getColor(0, 102, 204));
		buttonSave.setText("SAVE");
		//int offsetXbuttonSave = -buttonDatatypeDefinition.computeSize(SWT.DEFAULT, SWT.DEFAULT).x / 2;
		FormData fd_buttonSave = new FormData();
		fd_buttonSave.right = new FormAttachment(50, 145);
		fd_buttonSave.left = new FormAttachment(buttonDatatypeDefinition, 5, SWT.RIGHT);
		fd_buttonSave.top = new FormAttachment(fieldDatatypeDefinitionGrid, 10, SWT.BOTTOM);
		//fd_buttonMapRqrdFields.right = new FormAttachment(0, 282);
		buttonSave.setLayoutData(fd_buttonSave);
		buttonSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO mapping complete
				//projectModel.setAllProjectFields(fieldDatatypeDefinitionGrid.getFieldDatatypes());
				fieldDatatypeDefinitionGrid.setFieldDatatypes();
			}
		});
	}

	/*private void setFieldDatatypes(){
		fieldDatatypeDefinitionGrid.setFieldDatatypes();
	}*/

	private String[] getAllHeaders(){
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
