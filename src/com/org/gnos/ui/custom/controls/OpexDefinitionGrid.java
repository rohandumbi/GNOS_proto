package com.org.gnos.ui.custom.controls;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.core.Expression;
import com.org.gnos.core.Model;
import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.services.TimePeriod;

public class OpexDefinitionGrid extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private Composite compositeGridHeader;
	private List<Composite> allRows;
	private String[] sourceFieldsComboItems;
	private Composite presentRow;
	private List<Model> models;
	private Composite parent;
	private List<String> presentmodelNames;
	private TimePeriod timePeriod;
	private Label firstSeparator;
	private Label secondSeparator;

	public OpexDefinitionGrid(Composite parent, int style, TimePeriod timePeriod) {
		super(parent, style);
		this.parent = parent;
		this.allRows = new ArrayList<Composite>();
		//this.models = ProjectConfigutration.getInstance().getModels();
		this.timePeriod = timePeriod;
		this.createContent(parent);
	}
	
	private void createContent(Composite parent){
		this.setLayout(new FormLayout());
		this.createHeader();
		//this.createRows();
	}
	
	private boolean isModelNameDuplicate(String modelName){
		boolean isPresentInModelGrid = false;
		for(String str: presentmodelNames) {
		    if(str.trim().equalsIgnoreCase(modelName.trim()))
		    	isPresentInModelGrid = true;
		}
		return isPresentInModelGrid;
	}

	
	private String[] getIdentifierComboItems(){
		
		List<Model> models = ProjectConfigutration.getInstance().getModels();
		this.sourceFieldsComboItems = new String[models.size()];
		for(int i=0; i<models.size(); i++){
			this.sourceFieldsComboItems[i] = models.get(i).getName();
		}
				
		return this.sourceFieldsComboItems;
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

		Label lblClassification = new Label(compositeGridHeader, SWT.NONE);
		lblClassification.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblClassification = new FormData();
		fd_lblClassification.top = new FormAttachment(0,2);
		fd_lblClassification.left = new FormAttachment(0, 10);
		lblClassification.setLayoutData(fd_lblClassification);
		lblClassification.setText("Classification");
		lblClassification.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		firstSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_firstSeparator = new FormData();
		fd_firstSeparator.left = new FormAttachment(lblClassification, 10);
		firstSeparator.setLayoutData(fd_firstSeparator);
		
		Label lblUse = new Label(compositeGridHeader, SWT.NONE);
		lblUse.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblUse = new FormData();
		fd_lblUse.top = new FormAttachment(lblClassification, 0, SWT.TOP);
		fd_lblUse.left = new FormAttachment(firstSeparator, 6);
		lblUse.setLayoutData(fd_lblUse);
		lblUse.setText("Use");
		lblUse.setBackground(SWTResourceManager.getColor(230, 230, 230));

		secondSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_secondSeparator = new FormData();
		fd_secondSeparator.left = new FormAttachment(lblUse, 10);
		secondSeparator.setLayoutData(fd_secondSeparator);

		Label lblIdentifier = new Label(compositeGridHeader, SWT.NONE);
		lblIdentifier.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblIdentifier = new FormData();
		fd_lblIdentifier.top = new FormAttachment(lblClassification, 0, SWT.TOP);
		fd_lblIdentifier.left = new FormAttachment(secondSeparator, 35);
		lblIdentifier.setLayoutData(fd_lblIdentifier);
		lblIdentifier.setText("Identifier");
		lblIdentifier.setBackground(SWTResourceManager.getColor(230, 230, 230));

		this.presentRow = this.compositeGridHeader;//referring to the header as the 1st row when there are no rows inserted yet
		this.addTimePeriodHeaderColumns(lblIdentifier);

	}
	
	private void addTimePeriodHeaderColumns(Control reference){
		Control previousColumn = reference;
		for(int i=0; i<this.timePeriod.getIncrements(); i++){
			Label separator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
			FormData fd_separator = new FormData();
			fd_separator.left = new FormAttachment(previousColumn, 25);
			separator.setLayoutData(fd_separator);
			
			Label lblYear = new Label(compositeGridHeader, SWT.NONE);
			lblYear.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
			FormData fd_lblYear = new FormData();
			fd_lblYear.left = new FormAttachment(separator, 25);
			fd_lblYear.top = new FormAttachment(0, 2);
			lblYear.setText(String.valueOf(this.timePeriod.getStartYear() + i));
			lblYear.setBackground(SWTResourceManager.getColor(230, 230, 230));
			lblYear.setLayoutData(fd_lblYear);
			
			previousColumn = lblYear;
		}
	}

	/*private void createRows() {
		
		for(Model model: this.models) {
			final Composite compositeRow = new Composite(this, SWT.BORDER);
			compositeRow.setData(model);
			compositeRow.setLayout(new FormLayout());
			Color backgroundColor = SWTResourceManager.getColor(SWT.COLOR_WHITE);
			if((this.allRows != null) && (this.allRows.size()%2 != 0)){
				backgroundColor =  SWTResourceManager.getColor(245, 245, 245);
			}

			compositeRow.setBackground(backgroundColor);
			FormData fd_compositeRow = new FormData();
			fd_compositeRow.left = new FormAttachment(this.presentRow, 0, SWT.LEFT);
			//fd_compositeRow.bottom = new FormAttachment(this.presentRow, 26, SWT.BOTTOM);
			fd_compositeRow.right = new FormAttachment(this.presentRow, 0, SWT.RIGHT);
			fd_compositeRow.top = new FormAttachment(this.presentRow);


			Text modelName = new Text(compositeRow, SWT.BORDER);
			modelName.setText(model.getName());
			FormData fd_modelName = new FormData();
			fd_modelName.left = new FormAttachment(0, 10);
			fd_modelName.top = new FormAttachment(0);
			fd_modelName.right = new FormAttachment(20, -5);
			
			modelName.setLayoutData(fd_modelName);
			
			Text expressionName = new Text(compositeRow, SWT.BORDER);
			expressionName.setText(model.getExpression().getName());
			FormData fd_expressionDefinition = new FormData();
			fd_expressionDefinition.right = new FormAttachment(60, -5);
			fd_expressionDefinition.left = new FormAttachment(33, 5);
			expressionName.setLayoutData(fd_expressionDefinition);
			expressionName.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));


			Text textCondition = new Text(compositeRow, SWT.BORDER);
			if(model.getCondition() != null) textCondition.setText(model.getCondition());
			FormData fd_textCondition = new FormData();
			fd_textCondition.left = new FormAttachment(66, 2);
			fd_textCondition.right = new FormAttachment(100, -2);
			textCondition.setLayoutData(fd_textCondition);

			this.presentRow = compositeRow;
			this.allRows.add(compositeRow);
			compositeRow.setLayoutData(fd_compositeRow);
		}
	}*/
	
	public void addRow(){
		final Composite compositeRow = new Composite(this, SWT.BORDER);
		compositeRow.setLayout(new FormLayout());
		Color backgroundColor = SWTResourceManager.getColor(SWT.COLOR_WHITE);
		if((this.allRows != null) && (this.allRows.size()%2 != 0)){
			backgroundColor =  SWTResourceManager.getColor(245, 245, 245);
		}

		compositeRow.setBackground(backgroundColor);
		FormData fd_compositeRow = new FormData();
		fd_compositeRow.left = new FormAttachment(this.presentRow, 0, SWT.LEFT);
		//fd_compositeRow.bottom = new FormAttachment(this.presentRow, 26, SWT.BOTTOM);
		fd_compositeRow.right = new FormAttachment(this.presentRow, 0, SWT.RIGHT);
		fd_compositeRow.top = new FormAttachment(this.presentRow);


		Combo comboClassification = new Combo(compositeRow, SWT.NONE);
		comboClassification.setItems(new String[]{"PCost", "Rev"});
		comboClassification.setText("Select Type");
		FormData fd_comboClassification = new FormData();
		fd_comboClassification.left = new FormAttachment(0, 2);
		fd_comboClassification.top = new FormAttachment(0);
		//fd_comboClassification.right = new FormAttachment(20, -5);
		comboClassification.setLayoutData(fd_comboClassification);
		
		Button btnUse = new Button(compositeRow, SWT.CHECK);
		FormData fd_btnUse = new FormData();
		fd_btnUse.left = new FormAttachment(comboClassification, 10, SWT.RIGHT);
		fd_btnUse.top = new FormAttachment(0, 2);
		btnUse.setLayoutData(fd_btnUse);
		
		Combo comboIdentifier = new Combo(compositeRow, SWT.NONE);
		comboIdentifier.setItems(this.getIdentifierComboItems());
		comboIdentifier.setText("Select Model");
		FormData fd_comboIdentifier = new FormData();
		fd_comboIdentifier.left = new FormAttachment(btnUse, 21);
		fd_comboIdentifier.right = new FormAttachment(btnUse, 135);
		fd_comboIdentifier.top = new FormAttachment(0);
		comboIdentifier.setLayoutData(fd_comboIdentifier);
		
		this.addTimePeriodRowMembers(compositeRow, comboIdentifier);
		
		this.presentRow = compositeRow;
		this.allRows.add(compositeRow);
		compositeRow.setLayoutData(fd_compositeRow);
		this.layout();
	}
	
	private void addTimePeriodRowMembers(Composite parent, Control reference){
		Control previousMember = reference;
		for(int i=0; i<this.timePeriod.getIncrements(); i++){
			Text yearlyValue = new Text(parent, SWT.BORDER);
			FormData fd_yearlyValue = new FormData();
			/*
			 * Hacky calculation at the moment
			 */
			fd_yearlyValue.left = new FormAttachment(previousMember, 3);
			fd_yearlyValue.right = new FormAttachment(previousMember, 76, SWT.RIGHT);
			yearlyValue.setLayoutData(fd_yearlyValue);
			previousMember = yearlyValue;
		}
	}

	public void resetAllRows(){
		for(Composite existingRow : this.allRows){
			existingRow.setEnabled(false);
		}
		this.allRows = new ArrayList<Composite>();
		//this.presentRow = compositeGridHeader;
	}

	public List<Composite> getAllRowsComposite(){
		return this.allRows;
	}

	public List<Model> getDefinedModels(){
		Control[] rowChildren = null;
		this.presentmodelNames = new ArrayList<String>();
		List<Expression> expressions = ProjectConfigutration.getInstance().getExpressions();
		for(int i = 0; i < allRows.size(); i++){
			Composite row = allRows.get(i);
			rowChildren = row.getChildren();
			String modelName = null;
			String modelValue = null;
			String modelCondition = null;
			Model model = (Model)row.getData();

			Text modelNameText = (Text)rowChildren[0];
			Control modelValueComp = rowChildren[1];
			Text modelConditionText = (Text)rowChildren[2];
			
			modelName = modelNameText.getText();
			if(modelValueComp instanceof Text){
				modelValue = ((Text)modelValueComp).getText();
			} else {
				modelValue = ((Combo)modelValueComp).getText();
			}
			
			modelCondition = modelConditionText.getText();
			
			if(modelName == null || modelName == ""){
				MessageDialog.openError(this.parent.getShell(), "GNOS Error", "Please enter a valid name for model.");
				return null;
			}else if(modelValue == null){
				MessageDialog.openError(this.parent.getShell(), "GNOS Error", "Please enter a valid value for model " + modelName);
				return null;
			}
			
			if(isModelNameDuplicate(modelName)){
				MessageDialog.openError(this.parent.getShell(), "GNOS Error", "Model name: " + modelName + " already exists. Please use a unique model name.");
				return null;
			}else{
				if(model == null){
					model = new Model(modelName);
					this.models.add(model);
				}
				
				for(Expression expression: expressions) {
					if(expression.getName().equals(modelValue)){
						model.setExpression(expression);
						break;
					}
				}
				model.setCondition(modelCondition);
				presentmodelNames.add(modelName);
				
			}
		}
			
		return this.models;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
