package com.org.gnos.custom.controls;

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

import com.org.gnos.services.Expression;
import com.org.gnos.services.Expressions;
import com.org.gnos.services.Model;
import com.org.gnos.services.Operation;

public class ModelDefinitionGrid extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private String[] allSourceFields;
	private Composite compositeGridHeader;
	private List<Composite> allRows;
	private List<String> numericSourceFields;
	private String[] sourceFieldsComboItems;
	private Composite presentRow;
	private List<Model> modelList;
	private String[] arithemeticOperatorsArray;
	private Composite parent;
	private List<String> presentmodelNames;

	public ModelDefinitionGrid(Composite parent, int style, String[] allSourceFields) {
		super(parent, style);
		this.parent = parent;
		//this.requiredFieldNames = requiredFieldNames;
		//this.parent = parent;
		this.allSourceFields = allSourceFields;
		this.allRows = new ArrayList<Composite>();
		//this.expressionList = new ArrayList<Expression>();
		//this.numericSourceFields = new ArrayList<String>();
		//this.dataTypes = dataTypes;
		this.arithemeticOperatorsArray = new String[]{"+", "-", "*", "/"};
		this.createContent(parent);
	}
	
	private boolean isModelNameDuplicate(String modelName){
		boolean isPresentInExpressionGrid = false;
		boolean isPresentInSavedGrid = false;
		for(String str: presentmodelNames) {
		    if(str.trim().equalsIgnoreCase(modelName.trim()))
		    	isPresentInExpressionGrid = true;
		}
		if(!isPresentInExpressionGrid){
			List<Expression> savedExpressions = Expressions.getAll();
			for(Expression expression : savedExpressions){
				if(expression.getName().trim().equalsIgnoreCase(modelName)){
					isPresentInSavedGrid = true;
				}
			}
		}
		return isPresentInExpressionGrid||isPresentInSavedGrid;
	}

	
	private String[] getSourceFieldsComboItems(){
		
		List<Expression> expressions = Expressions.getAll();
		this.sourceFieldsComboItems = new String[expressions.size()];
		for(int i=0; i<expressions.size(); i++){
			this.sourceFieldsComboItems[i] = expressions.get(i).getName();
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

		Label firstSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_firstSeparator = new FormData();
		fd_firstSeparator.left = new FormAttachment(33);
		firstSeparator.setLayoutData(fd_firstSeparator);

		Label secondSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_secondSeparator = new FormData();
		fd_secondSeparator.left = new FormAttachment(66);
		secondSeparator.setLayoutData(fd_secondSeparator);


		Label lblGradeHeader = new Label(compositeGridHeader, SWT.NONE);
		lblGradeHeader.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		FormData fd_lblGradeHeader = new FormData();
		fd_lblGradeHeader.top = new FormAttachment(0,2);
		fd_lblGradeHeader.left = new FormAttachment(0, 10);
		lblGradeHeader.setLayoutData(fd_lblGradeHeader);
		lblGradeHeader.setText("IDENTIFIER/NAME");
		lblGradeHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));

		Label lblExpressionNameHeader = new Label(compositeGridHeader, SWT.NONE);
		lblExpressionNameHeader.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		FormData fd_lblExpressionNameHeader = new FormData();
		fd_lblExpressionNameHeader.top = new FormAttachment(0,2);
		fd_lblExpressionNameHeader.left = new FormAttachment(firstSeparator, 10);
		lblExpressionNameHeader.setLayoutData(fd_lblExpressionNameHeader);
		lblExpressionNameHeader.setText("FIELD");
		lblExpressionNameHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));

		Label lblExpressionTypeHeader = new Label(compositeGridHeader, SWT.NONE);
		lblExpressionTypeHeader.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		FormData fd_lblExpressionTypeHeader = new FormData();
		fd_lblExpressionTypeHeader.top = new FormAttachment(0,2);
		fd_lblExpressionTypeHeader.left = new FormAttachment(secondSeparator, 10);
		lblExpressionTypeHeader.setLayoutData(fd_lblExpressionTypeHeader);
		lblExpressionTypeHeader.setText("CONDITION");
		lblExpressionTypeHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));

		this.presentRow = this.compositeGridHeader;//referring to the header as the 1st row when there are no rows inserted yet

	}


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


		Text modelName = new Text(compositeRow, SWT.BORDER);
		//fd_grade.top = new FormAttachment(expressionName, 2, SWT.TOP);
		FormData fd_modelName = new FormData();
		fd_modelName.left = new FormAttachment(0, 10);
		fd_modelName.top = new FormAttachment(0);
		fd_modelName.right = new FormAttachment(20, -5);
		modelName.setLayoutData(fd_modelName);
		
		final Combo comboModelDefinition = new Combo(compositeRow, SWT.NONE);
		FormData fd_comboExpressionDefinition = new FormData();
		fd_comboExpressionDefinition.right = new FormAttachment(60, -5);
		fd_comboExpressionDefinition.left = new FormAttachment(33, 5);
		comboModelDefinition.setLayoutData(fd_comboExpressionDefinition);
		comboModelDefinition.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		comboModelDefinition.setItems(getSourceFieldsComboItems());
		comboModelDefinition.setText("Field Value");
		comboModelDefinition.addListener(SWT.MouseDown, new Listener(){
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				//System.out.println("detected combo click");
				comboModelDefinition.removeAll();
				comboModelDefinition.setItems(getSourceFieldsComboItems());
				comboModelDefinition.getParent().layout();
				comboModelDefinition.setListVisible(true);
			}
		});

		Text textCondition = new Text(compositeRow, SWT.BORDER);
		FormData fd_textCondition = new FormData();
		fd_textCondition.left = new FormAttachment(66, 2);
		fd_textCondition.right = new FormAttachment(100, -2);
		textCondition.setLayoutData(fd_textCondition);

		this.presentRow = compositeRow;
		this.allRows.add(compositeRow);
		compositeRow.setLayoutData(fd_compositeRow);
	}

	private void createContent(Composite parent){
		this.setLayout(new FormLayout());
		//this.createSourceFieldsComboItems();
		this.createHeader();

	}

	public void resetAllRows(){
		for(Composite existingRow : this.allRows){
			existingRow.setEnabled(false);
		}
		this.allRows = new ArrayList<Composite>();
		this.presentRow = compositeGridHeader;
	}

	public List<Composite> getAllRowsComposite(){
		return this.allRows;
	}

	public List<Model> getDefinedModels(){
		Control[] rowChildren = null;
		//this.expressionList = new ArrayList<Expression>();
		this.modelList = new ArrayList<Model>();
		this.presentmodelNames = new ArrayList<String>();
		for(int i = 0; i < allRows.size(); i++){
			rowChildren = allRows.get(i).getChildren();
			String modelName = null;
			int modelValue = -1;
			String modelCondition = null;
			Model model = null;

			Text modelNameText = (Text)rowChildren[0];
			Combo modelValueCombo = (Combo)rowChildren[1];
			Text modelConditionText = (Text)rowChildren[2];
			
			modelName = modelNameText.getText();
			modelValue = modelValueCombo.getSelectionIndex();
			modelCondition = modelConditionText.getText();
			
			if(modelName == null || modelName == ""){
				MessageDialog.openError(this.parent.getShell(), "GNOS Error", "Please enter a valid name for model.");
				return null;
			}else if(modelValue<0){
				MessageDialog.openError(this.parent.getShell(), "GNOS Error", "Please enter a valid value for model " + modelName);
				return null;
			}
			if(modelCondition == null || modelCondition == ""){
				modelCondition = "[bin]==[bin]";
				System.out.println("Condition: " + modelCondition);//temporary hack to set everything when no condition
			}
			
			if(isModelNameDuplicate(modelName)){
				MessageDialog.openError(this.parent.getShell(), "GNOS Error", "Model name: " + modelName + " already exists. Please use a unique model name.");
				return null;
			}else{
				model = new Model(modelName);
				model.setValue(modelValue);
				boolean isConditionValid = model.setCondition(modelCondition);
				System.out.println("Condition: " + isConditionValid);
				if(!isConditionValid){
					MessageDialog.openError(this.parent.getShell(), "GNOS Error", "Conditions not defined properly.");
					return null;
				} 
				//model.setCondition(modelCondition);
				presentmodelNames.add(modelName);
				this.modelList.add(model);
			}
		}
			
		return this.modelList;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
