package com.org.gnos.custom.controls;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.services.ColumnHeader;

public class MapRequiredFieldsGrid extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private String[] requiredFieldNames;
	private List<ColumnHeader> allSourceFields;
	private String[] dataTypes;
	private Composite compositeGridHeader;
	private List<Composite> allRows;
	private String[] sourceFieldsComboItems;
	
	public MapRequiredFieldsGrid(Composite parent, int style, String[] requiredFieldNames, List<ColumnHeader> allSourceFields, String[] dataTypes) {
		super(parent, style);
		this.requiredFieldNames = requiredFieldNames;
		this.allSourceFields = allSourceFields;
		this.dataTypes = dataTypes;
		this.createContent(parent);
	}
	private void createSourceFieldsComboItems(){
		int i = 0;
		int sourceFieldSize = this.allSourceFields.size();
		this.sourceFieldsComboItems = new String[sourceFieldSize];
		for(i=0; i<sourceFieldSize; i++){
			this.sourceFieldsComboItems[i] = this.allSourceFields.get(i).getName();
		}
	}
	
	
	private void createHeader(){
		compositeGridHeader = new Composite(this, SWT.BORDER);
		//compositeGridHeader.setBounds(0, 0, 749, 37);
		compositeGridHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));
		compositeGridHeader.setLayout(new FormLayout());
		FormData fd_compositeGridHeader = new FormData();
		fd_compositeGridHeader.bottom = new FormAttachment(0, 31);
		fd_compositeGridHeader.top = new FormAttachment(0);
		fd_compositeGridHeader.left = new FormAttachment(0);
		fd_compositeGridHeader.right = new FormAttachment(100);
		compositeGridHeader.setLayoutData(fd_compositeGridHeader);
		
		Label label = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_label = new FormData();
		fd_label.left = new FormAttachment(40);
		label.setLayoutData(fd_label);
		
		Label label_1 = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_label_1 = new FormData();
		fd_label_1.left = new FormAttachment(70);
		label_1.setLayoutData(fd_label_1);
		
		Label lblRqrdFieldHeader = new Label(compositeGridHeader, SWT.NONE);
		lblRqrdFieldHeader.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		FormData fd_lblRqrdFieldHeader = new FormData();
		fd_lblRqrdFieldHeader.top = new FormAttachment(0,2);
		fd_lblRqrdFieldHeader.left = new FormAttachment(0, 10);
		lblRqrdFieldHeader.setLayoutData(fd_lblRqrdFieldHeader);
		lblRqrdFieldHeader.setText("REQUIRED FIELD");
		lblRqrdFieldHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		Label lblSourceFieldHeader = new Label(compositeGridHeader, SWT.NONE);
		lblSourceFieldHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));
		lblSourceFieldHeader.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		FormData fd_lblSourceFieldHeader = new FormData();
		fd_lblSourceFieldHeader.top = new FormAttachment(0, 2);
		fd_lblSourceFieldHeader.left = new FormAttachment(label, 10);
		lblSourceFieldHeader.setLayoutData(fd_lblSourceFieldHeader);
		lblSourceFieldHeader.setText("SOURCE FIELD");
		
		Label lblDatatypeHeader = new Label(compositeGridHeader, SWT.NONE);
		lblDatatypeHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));
		lblDatatypeHeader.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		FormData fd_lblDatatypeHeader = new FormData();
		fd_lblDatatypeHeader.top = new FormAttachment(0,2);
		fd_lblDatatypeHeader.left = new FormAttachment(label_1, 10);
		lblDatatypeHeader.setLayoutData(fd_lblDatatypeHeader);
		lblDatatypeHeader.setText("DATATYPE");
	}
	private void createRows(){
		Composite presentRow = this.compositeGridHeader;//referring to the header as the 1st row when there are no rows inserted yet
		allRows = new ArrayList<Composite>();
		for(String requiredFieldName : this.requiredFieldNames){
			
			Composite compositeRow = new Composite(this, SWT.BORDER);
			compositeRow.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			
			compositeRow.setLayout(new FormLayout());
			FormData fd_compositeRow = new FormData();
			fd_compositeRow.bottom = new FormAttachment(presentRow, 25, SWT.BOTTOM);
			fd_compositeRow.top = new FormAttachment(presentRow);
			fd_compositeRow.right = new FormAttachment(presentRow, 0, SWT.RIGHT);
			fd_compositeRow.left = new FormAttachment(presentRow, 0, SWT.LEFT);
			
			Label lblRqrdFieldName = new Label(compositeRow, SWT.NONE);
			lblRqrdFieldName.setForeground(SWTResourceManager.getColor(0,191,255));
			lblRqrdFieldName.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			lblRqrdFieldName.setFont(SWTResourceManager.getFont("Arial", 9, SWT.BOLD));
			FormData fd_lblRqrdFieldName = new FormData();
			fd_lblRqrdFieldName.top = new FormAttachment(0);
			fd_lblRqrdFieldName.left = new FormAttachment(0, 10);
			lblRqrdFieldName.setLayoutData(fd_lblRqrdFieldName);
			lblRqrdFieldName.setText(requiredFieldName);
			
			Combo comboSourceField = new Combo(compositeRow, SWT.NONE);
			comboSourceField.setItems(this.sourceFieldsComboItems);
			FormData fd_comboSourceField = new FormData();
			fd_comboSourceField.left = new FormAttachment(40, 12);
			comboSourceField.setLayoutData(fd_comboSourceField);
			
			Combo comboDatatype = new Combo(compositeRow, SWT.NONE);
			comboDatatype.setItems(this.dataTypes);
			FormData fd_comboDatatype = new FormData();
			fd_comboDatatype.left = new FormAttachment(70, 12);
			comboDatatype.setLayoutData(fd_comboDatatype);
			
			compositeRow.setLayoutData(fd_compositeRow);
			allRows.add(compositeRow);
			presentRow = compositeRow;
		}
	}
	
	private void createContent(Composite parent){
		this.setLayout(new FormLayout());
		this.createSourceFieldsComboItems();
		this.createHeader();
		this.createRows();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
