package com.org.gnos.ui.screens.v1;

import java.io.IOException;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.application.GNOSConfig;
import com.org.gnos.custom.controls.GnosScreen;
import com.org.gnos.custom.models.ProjectMetaDataModel;
import com.org.gnos.events.GnosEvent;
import com.org.gnos.events.interfaces.GnosEventListener;
import com.org.gnos.services.CSVProcessor;
import com.org.gnos.services.ColumnHeader;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Combo;

public class MapRequiredFieldsScreen extends GnosScreen {

	private List<ColumnHeader> allHeaders;
	private String[] requiredFields;
	private ProjectMetaDataModel projectMetaData;
	private CSVProcessor csvProcessor;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public MapRequiredFieldsScreen(Composite parent, int style, ProjectMetaDataModel projectMetaData) {
		super(parent, style);
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.projectMetaData = projectMetaData;
		this.allHeaders = this.getAllHeaders();
		System.out.println("Length of all columns: " + this.allHeaders.size());
		this.requiredFields = this.getRequiredFieldsFromProperties();
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
		Label lblNewLabel = new Label(this, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		lblNewLabel.setText("Map Required Fields Screen");
		
		Button buttonMapRqrdFields = new Button(this, SWT.NONE);
		fd_lblNewLabel.bottom = new FormAttachment(buttonMapRqrdFields, 0, SWT.BOTTOM);
		FormData fd_buttonMapRqrdFields = new FormData();
		fd_buttonMapRqrdFields.right = new FormAttachment(0, 282);
		fd_buttonMapRqrdFields.top = new FormAttachment(0, 10);
		fd_buttonMapRqrdFields.left = new FormAttachment(0, 207);
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
		
		Composite compositeGridHeader = new Composite(this, SWT.NONE);
		compositeGridHeader.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		compositeGridHeader.setLayout(new FillLayout(SWT.HORIZONTAL));
		FormData fd_compositeGridHeader = new FormData();
		fd_compositeGridHeader.bottom = new FormAttachment(lblNewLabel, 31, SWT.BOTTOM);
		fd_compositeGridHeader.top = new FormAttachment(lblNewLabel, 6);
		fd_compositeGridHeader.left = new FormAttachment(0, 10);
		fd_compositeGridHeader.right = new FormAttachment(100, -10);
		compositeGridHeader.setLayoutData(fd_compositeGridHeader);
		
		Label lblNewLabel_2 = new Label(compositeGridHeader, SWT.BORDER);
		lblNewLabel_2.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		lblNewLabel_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		lblNewLabel_2.setAlignment(SWT.CENTER);
		lblNewLabel_2.setText("Required Fields");
		
		Label lblNewLabel_1 = new Label(compositeGridHeader, SWT.BORDER);
		lblNewLabel_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		lblNewLabel_1.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		lblNewLabel_1.setAlignment(SWT.CENTER);
		lblNewLabel_1.setText("Mapped Field");
		
		Composite compositeRow1 = new Composite(this, SWT.NONE);
		compositeRow1.setBackground(SWTResourceManager.getColor(240, 248, 255));
		compositeRow1.setLayout(new FillLayout(SWT.HORIZONTAL));
		FormData fd_compositeRow1 = new FormData();
		fd_compositeRow1.right = new FormAttachment(compositeGridHeader, 0, SWT.RIGHT);
		//fd_compositeRow1.bottom = new FormAttachment(compositeGridHeader, 31, SWT.BOTTOM);
		fd_compositeRow1.top = new FormAttachment(compositeGridHeader);
		fd_compositeRow1.left = new FormAttachment(0, 10);
		compositeRow1.setLayoutData(fd_compositeRow1);
		
		Label lblNewLabel_3 = new Label(compositeRow1, SWT.NONE);
		lblNewLabel_3.setBackground(SWTResourceManager.getColor(240, 248, 255));
		lblNewLabel_3.setAlignment(SWT.CENTER);
		lblNewLabel_3.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		lblNewLabel_3.setText("Required Field 1");
		
		Combo combo = new Combo(compositeRow1, SWT.NONE);
		combo.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		
		Composite compositeRow2 = new Composite(this, SWT.NONE);
		compositeRow2.setBackground(SWTResourceManager.getColor(240, 248, 255));
		compositeRow2.setLayout(new FillLayout(SWT.HORIZONTAL));
		FormData fd_compositeRow2 = new FormData();
		fd_compositeRow2.right = new FormAttachment(compositeGridHeader, 0, SWT.RIGHT);
		fd_compositeRow2.top = new FormAttachment(compositeRow1, 0);
		fd_compositeRow2.left = new FormAttachment(0, 10);
		compositeRow2.setLayoutData(fd_compositeRow2);
		
		Label lblRequiredField = new Label(compositeRow2, SWT.NONE);
		lblRequiredField.setText("Required Field 2");
		lblRequiredField.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		lblRequiredField.setBackground(SWTResourceManager.getColor(173, 216, 230));
		lblRequiredField.setAlignment(SWT.CENTER);
		
		Combo combo_1 = new Combo(compositeRow2, SWT.NONE);
		combo_1.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		
		Composite compositeRow3 = new Composite(this, SWT.NONE);
		compositeRow3.setBackground(SWTResourceManager.getColor(240, 248, 255));
		compositeRow3.setLayout(new FillLayout(SWT.HORIZONTAL));
		FormData fd_compositeRow3 = new FormData();
		fd_compositeRow3.right = new FormAttachment(compositeGridHeader, 0, SWT.RIGHT);
		fd_compositeRow3.top = new FormAttachment(compositeRow2, 0);
		fd_compositeRow3.left = new FormAttachment(0, 10);
		compositeRow3.setLayoutData(fd_compositeRow3);
		
		Label lblRequiredField_1 = new Label(compositeRow3, SWT.NONE);
		lblRequiredField_1.setText("Required Field 3");
		lblRequiredField_1.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		lblRequiredField_1.setBackground(SWTResourceManager.getColor(240, 248, 255));
		lblRequiredField_1.setAlignment(SWT.CENTER);
		
		Combo combo_2 = new Combo(compositeRow3, SWT.NONE);
		combo_2.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
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
