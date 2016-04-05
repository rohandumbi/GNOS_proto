package com.org.gnos.ui.custom.controls;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.core.Field;
import com.org.gnos.services.csv.GNOSCSVDataProcessor;

public class FieldDatatypeDefinitionGrid extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private List<Field> allSourceFields;
	private String[] dataTypes;
	private Composite compositeGridHeader;
	private List<Composite> allRows;
	
	public FieldDatatypeDefinitionGrid(Composite parent, int style, List<Field> allSourceFields, String[] dataTypes) {
		super(parent, style);
		this.allSourceFields = allSourceFields;
		this.dataTypes = dataTypes;
		this.createContent(parent);
	}
	
	private short getDatatypeCode(String dataType){
		short code = -1;
		if(dataType.equalsIgnoreCase("Number")){
			code = Field.TYPE_NUMBER;
		}else if(dataType.equalsIgnoreCase("Text")){
			code = Field.TYPE_STRING;
		} 
		return code;
	}

	private void createHeader(){
		compositeGridHeader = new Composite(this, SWT.BORDER);
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
		fd_label.left = new FormAttachment(50);
		label.setLayoutData(fd_label);

		Label lblSourceFieldHeader = new Label(compositeGridHeader, SWT.NONE);
		lblSourceFieldHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));
		lblSourceFieldHeader.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		FormData fd_lblSourceFieldHeader = new FormData();
		fd_lblSourceFieldHeader.top = new FormAttachment(0, 2);
		fd_lblSourceFieldHeader.left = new FormAttachment(0, 10);
		lblSourceFieldHeader.setLayoutData(fd_lblSourceFieldHeader);
		lblSourceFieldHeader.setText("SOURCE FIELD");

		Label lblDatatypeHeader = new Label(compositeGridHeader, SWT.NONE);
		lblDatatypeHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));
		lblDatatypeHeader.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		FormData fd_lblDatatypeHeader = new FormData();
		fd_lblDatatypeHeader.top = new FormAttachment(0,2);
		fd_lblDatatypeHeader.left = new FormAttachment(label, 10);
		lblDatatypeHeader.setLayoutData(fd_lblDatatypeHeader);
		lblDatatypeHeader.setText("DATATYPE");
	}
	private void createRows(){
		Composite presentRow = this.compositeGridHeader;//referring to the header as the 1st row when there are no rows inserted yet
		allRows = new ArrayList<Composite>();
		int i=0;
		for(Field field : this.allSourceFields){
			Composite compositeRow = new Composite(this, SWT.BORDER);
			Color backgroundColor = SWTResourceManager.getColor(SWT.COLOR_WHITE);
			if(i%2 != 0){
				backgroundColor =  SWTResourceManager.getColor(245, 245, 245);
			}
			
			compositeRow.setData(field);
			compositeRow.setLayout(new FormLayout());
			compositeRow.setBackground(backgroundColor);
			FormData fd_compositeRow = new FormData();
			fd_compositeRow.bottom = new FormAttachment(presentRow, 25, SWT.BOTTOM);
			fd_compositeRow.top = new FormAttachment(presentRow);
			fd_compositeRow.right = new FormAttachment(presentRow, 0, SWT.RIGHT);
			fd_compositeRow.left = new FormAttachment(presentRow, 0, SWT.LEFT);
			
			//String columnHeaderName = sourceField.getName();
			
			Label lblSourceFieldName = new Label(compositeRow, SWT.NONE);
			lblSourceFieldName.setForeground(SWTResourceManager.getColor(0,191,255));
			lblSourceFieldName.setBackground(backgroundColor);
			lblSourceFieldName.setFont(SWTResourceManager.getFont("Arial", 9, SWT.BOLD));
			FormData fd_lblSourceFieldName = new FormData();
			fd_lblSourceFieldName.top = new FormAttachment(0);
			fd_lblSourceFieldName.left = new FormAttachment(0, 10);
			lblSourceFieldName.setLayoutData(fd_lblSourceFieldName);
			lblSourceFieldName.setText(field.getName());
			
			Combo comboDatatype = new Combo(compositeRow, SWT.NONE);
			comboDatatype.setItems(this.dataTypes);
			if(field.getDataType() == Field.TYPE_STRING){
				comboDatatype.select(1);
			} else {
				comboDatatype.select(0);
			}
			
			FormData fd_comboDatatype = new FormData();
			fd_comboDatatype.left = new FormAttachment(50, 12);
			fd_comboDatatype.right = new FormAttachment(80);
			comboDatatype.setLayoutData(fd_comboDatatype);

			compositeRow.setLayoutData(fd_compositeRow);
			allRows.add(compositeRow);
			presentRow = compositeRow;
			i++;
		}
	}

	private void createContent(Composite parent){
		this.setLayout(new FormLayout());
		this.createHeader();
		this.createRows();
		this.setFieldDatatypes();//setting the data type mapping with default standards
	}
	
	
	public void setFieldDatatypes(){
		Control[] rowChildren = null;
		for(int i = 0; i < allRows.size(); i++){
			rowChildren = allRows.get(i).getChildren();
			Control compositeFieldName = rowChildren[0];
			Control compositeDatatype = rowChildren[1];
			
			String fieldName = null;
			String datatypeName = null;
			Field field = (Field)allRows.get(i).getData();
			if(compositeFieldName instanceof Label){
				Label labelFieldName = (Label)compositeFieldName;
				fieldName = labelFieldName.getText();
			}
			if(compositeDatatype instanceof Combo){
				Combo comboDatatype = (Combo)compositeDatatype;
				datatypeName = comboDatatype.getText();
			}
			field.setDataType(this.getDatatypeCode(datatypeName));
		}
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
