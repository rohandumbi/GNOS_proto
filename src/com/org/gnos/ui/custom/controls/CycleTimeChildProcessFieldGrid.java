package com.org.gnos.ui.custom.controls;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
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

import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.db.model.Field;

public class CycleTimeChildProcessFieldGrid extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private String[] requiredFieldNames;
	private List<Field> allSourceFields;
	//private String[] dataTypes;
	private Composite compositeGridHeader;
	private List<Composite> allRows;
	private String[] sourceFieldsComboItems;
	private Composite parent;
	
	public CycleTimeChildProcessFieldGrid(Composite parent, int style) {
		super(parent, style);
		this.parent = parent;
		//this.requiredFieldNames = requiredFieldNames;
		//this.allSourceFields = allSourceFields;
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
		fd_label.left = new FormAttachment(50);
		label.setLayoutData(fd_label);

		/*Label label_1 = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_label_1 = new FormData();
		fd_label_1.left = new FormAttachment(70);
		label_1.setLayoutData(fd_label_1);*/

		Label lblRqrdFieldHeader = new Label(compositeGridHeader, SWT.NONE);
		lblRqrdFieldHeader.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		FormData fd_lblRqrdFieldHeader = new FormData();
		fd_lblRqrdFieldHeader.top = new FormAttachment(0,2);
		fd_lblRqrdFieldHeader.left = new FormAttachment(0, 10);
		lblRqrdFieldHeader.setLayoutData(fd_lblRqrdFieldHeader);
		lblRqrdFieldHeader.setText("CHILD PROCESS NAME");
		lblRqrdFieldHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));

		Label lblSourceFieldHeader = new Label(compositeGridHeader, SWT.NONE);
		lblSourceFieldHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));
		lblSourceFieldHeader.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		FormData fd_lblSourceFieldHeader = new FormData();
		fd_lblSourceFieldHeader.top = new FormAttachment(0, 2);
		fd_lblSourceFieldHeader.left = new FormAttachment(label, 10);
		lblSourceFieldHeader.setLayoutData(fd_lblSourceFieldHeader);
		lblSourceFieldHeader.setText("SOURCE FIELD");

		/*Label lblDatatypeHeader = new Label(compositeGridHeader, SWT.NONE);
		lblDatatypeHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));
		lblDatatypeHeader.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		FormData fd_lblDatatypeHeader = new FormData();
		fd_lblDatatypeHeader.top = new FormAttachment(0,2);
		fd_lblDatatypeHeader.left = new FormAttachment(label_1, 10);
		lblDatatypeHeader.setLayoutData(fd_lblDatatypeHeader);
		lblDatatypeHeader.setText("DATATYPE");*/
	}
	private void createRows(){
		Composite presentRow = this.compositeGridHeader;//referring to the header as the 1st row when there are no rows inserted yet
		allRows = new ArrayList<Composite>();
		Map<String, String> existingMapping = ProjectConfigutration.getInstance().getRequiredFieldMapping();
		int i=0;
		for(String requiredFieldName : this.requiredFieldNames){
			
			Composite compositeRow = new Composite(this, SWT.BORDER);
			Color backgroundColor = SWTResourceManager.getColor(SWT.COLOR_WHITE);
			if(i%2 != 0){
				backgroundColor =  SWTResourceManager.getColor(245, 245, 245);
			}
			compositeRow.setLayout(new FormLayout());
			compositeRow.setBackground(backgroundColor);
			FormData fd_compositeRow = new FormData();
			fd_compositeRow.bottom = new FormAttachment(presentRow, 25, SWT.BOTTOM);
			fd_compositeRow.top = new FormAttachment(presentRow);
			fd_compositeRow.right = new FormAttachment(presentRow, 0, SWT.RIGHT);
			fd_compositeRow.left = new FormAttachment(presentRow, 0, SWT.LEFT);

			Label lblRqrdFieldName = new Label(compositeRow, SWT.NONE);
			lblRqrdFieldName.setForeground(SWTResourceManager.getColor(0,191,255));
			lblRqrdFieldName.setBackground(backgroundColor);
			lblRqrdFieldName.setFont(SWTResourceManager.getFont("Arial", 9, SWT.BOLD));
			FormData fd_lblRqrdFieldName = new FormData();
			fd_lblRqrdFieldName.top = new FormAttachment(0);
			fd_lblRqrdFieldName.left = new FormAttachment(0, 10);
			lblRqrdFieldName.setLayoutData(fd_lblRqrdFieldName);
			lblRqrdFieldName.setText(requiredFieldName);

			Combo comboSourceField = new Combo(compositeRow, SWT.NONE);
			comboSourceField.setItems(this.sourceFieldsComboItems);
			if(existingMapping != null && existingMapping.size()> 0){
				String mappedField = existingMapping.get(requiredFieldName);
				for(int j=0; j< this.allSourceFields.size(); j++) {
					if(mappedField.equalsIgnoreCase(this.allSourceFields.get(j).getName())){
						comboSourceField.select(j);
						break;
					}
				}
			} else {
				comboSourceField.select(i);
			}
			
			FormData fd_comboSourceField = new FormData();
			fd_comboSourceField.left = new FormAttachment(50, 12);
			fd_comboSourceField.right = new FormAttachment(80);
			comboSourceField.setLayoutData(fd_comboSourceField);
			
			compositeRow.setLayoutData(fd_compositeRow);
			allRows.add(compositeRow);
			presentRow = compositeRow;
			i++;
		}
	}

	private void createContent(Composite parent){
		this.setLayout(new FormLayout());
		//this.createSourceFieldsComboItems();
		this.createHeader();
		//this.createRows();
		//this.setSourceFieldMapping(); //updating the default standard mappings initially
	}

	public boolean setSourceFieldMapping(){
		
		Map<String, String> requiredFieldMapping = new LinkedHashMap<String, String>();
		Control[] rowChildren = null;
		for(int i = 0; i < allRows.size(); i++){
			rowChildren = allRows.get(i).getChildren();
			
			String requiredFieldName = null;
			String sourceFieldName = null;
			//String datatypeName = null;
			
			Control compositeRequiredFieldName = rowChildren[0];
			Control compositeSourceFieldName = rowChildren[1];
			//Control compositeDatatype = rowChildren[2];
			
			if(compositeRequiredFieldName instanceof Label){
				Label labelRequiredFieldName = (Label)compositeRequiredFieldName;
				requiredFieldName = labelRequiredFieldName.getText();
			}
			if(compositeSourceFieldName instanceof Combo){
				Combo comboSourceFieldName = (Combo)compositeSourceFieldName;
				sourceFieldName = comboSourceFieldName.getText();
			}
			if(sourceFieldName == null || sourceFieldName == ""){
				MessageDialog.openError(this.parent.getShell(), "GNOS Error", "Please map a source field for the Required Field: " + requiredFieldName);
				return false;
			}
			requiredFieldMapping.put(requiredFieldName, sourceFieldName);
		}
		ProjectConfigutration.getInstance().setRequiredFieldMapping(requiredFieldMapping);
		return true;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
