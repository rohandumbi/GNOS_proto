package com.org.gnos.custom.controls;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.services.csv.ColumnHeader;

public class ExpressionBuilderGrid extends Composite {

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
	
	public ExpressionBuilderGrid(Composite parent, int style, List<ColumnHeader> allSourceFields) {
		super(parent, style);
		//this.requiredFieldNames = requiredFieldNames;
		this.allSourceFields = allSourceFields;
		//this.dataTypes = dataTypes;
		this.createContent(parent);
	}
	
	private int getDatatypeCode(String dataType){
		if(dataType.equalsIgnoreCase("String")){
			return 1;
		}else if(dataType.equalsIgnoreCase("Integer")){
			return 2;
		}else if(dataType.equalsIgnoreCase("Double")){
			return 3;
		}else{
			return 0;
		}
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

		Label firstSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_firstSeparator = new FormData();
		fd_firstSeparator.left = new FormAttachment(5);
		firstSeparator.setLayoutData(fd_firstSeparator);
		
		Label secondSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_secondSeparator = new FormData();
		fd_secondSeparator.left = new FormAttachment(40);
		secondSeparator.setLayoutData(fd_secondSeparator);

		Label thirdSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_thirdSeparator = new FormData();
		fd_thirdSeparator.left = new FormAttachment(70);
		thirdSeparator.setLayoutData(fd_thirdSeparator);
		
		Label lblGradeHeader = new Label(compositeGridHeader, SWT.NONE);
		lblGradeHeader.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		FormData fd_lblGradeHeader = new FormData();
		fd_lblGradeHeader.top = new FormAttachment(0,2);
		fd_lblGradeHeader.left = new FormAttachment(0);
		lblGradeHeader.setLayoutData(fd_lblGradeHeader);
		lblGradeHeader.setText("GRADE");
		lblGradeHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));

		Label lblExpressionNameHeader = new Label(compositeGridHeader, SWT.NONE);
		lblExpressionNameHeader.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		FormData fd_lblExpressionNameHeader = new FormData();
		fd_lblExpressionNameHeader.top = new FormAttachment(0,2);
		fd_lblExpressionNameHeader.left = new FormAttachment(firstSeparator, 10);
		lblExpressionNameHeader.setLayoutData(fd_lblExpressionNameHeader);
		lblExpressionNameHeader.setText("EXPRESSION NAME");
		lblExpressionNameHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));

		Label lblExpressionDefinitionHeader = new Label(compositeGridHeader, SWT.NONE);
		lblExpressionDefinitionHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));
		lblExpressionDefinitionHeader.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		FormData fd_lblExpressionDefinitionHeader = new FormData();
		fd_lblExpressionDefinitionHeader.top = new FormAttachment(0, 2);
		fd_lblExpressionDefinitionHeader.left = new FormAttachment(secondSeparator, 10);
		lblExpressionDefinitionHeader.setLayoutData(fd_lblExpressionDefinitionHeader);
		lblExpressionDefinitionHeader.setText("EXPRESSION DEFINITION");

		Label lblFiltersHeader = new Label(compositeGridHeader, SWT.NONE);
		lblFiltersHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));
		lblFiltersHeader.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		FormData fd_lblFiltersHeader = new FormData();
		fd_lblFiltersHeader.top = new FormAttachment(0,2);
		fd_lblFiltersHeader.left = new FormAttachment(thirdSeparator, 10);
		lblFiltersHeader.setLayoutData(fd_lblFiltersHeader);
		lblFiltersHeader.setText("FILTERS");
		
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
		//this.createRows();
	}

	public List<ColumnHeader> getMappedSourceFields(){
		Control[] rowChildren = null;
		for(int i = 0; i < allRows.size(); i++){
			rowChildren = allRows.get(i).getChildren();
			
			String requiredFieldName = null;
			String sourceFieldName = null;
			String datatypeName = null;
			
			Control compositeRequiredFieldName = rowChildren[0];
			Control compositeSourceFieldName = rowChildren[1];
			Control compositeDatatype = rowChildren[2];
			
			if(compositeRequiredFieldName instanceof Label){
				Label labelRequiredFieldName = (Label)compositeRequiredFieldName;
				requiredFieldName = labelRequiredFieldName.getText();
			}
			if(compositeSourceFieldName instanceof Combo){
				Combo comboSourceFieldName = (Combo)compositeSourceFieldName;
				sourceFieldName = comboSourceFieldName.getText();
			}
			if(compositeDatatype instanceof Combo){
				Combo comboDatatype = (Combo)compositeDatatype;
				datatypeName = comboDatatype.getText();
			}
			//System.out.println("ROW" + i +" data: " + requiredFieldName + "====" + sourceFieldName + "====" + datatypeName);
			this.upDateSourceFieldHeader(sourceFieldName, requiredFieldName, datatypeName);
		}

		return this.allSourceFields;
	}
	
	private void upDateSourceFieldHeader(String sourceFieldName, String requiredFieldName, String datatypeName){
		for( ColumnHeader sourceField : this.allSourceFields){
			if(sourceField.getName().equals(sourceFieldName)){
				sourceField.setRequiredFieldName(requiredFieldName);
				sourceField.setRequired(true);
				sourceField.setDataType(this.getDatatypeCode(datatypeName));
			}
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
