package com.org.gnos.ui.custom.controls;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Model;

public class ModelDefinitionGrid extends Composite {

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

	public ModelDefinitionGrid(Composite parent, int style) {
		super(parent, style);
		this.parent = parent;
		this.allRows = new ArrayList<Composite>();
		this.models = ProjectConfigutration.getInstance().getModels();
		this.createContent(parent);
	}
	
	private void createContent(Composite parent){
		this.setLayout(new FormLayout());
		this.createHeader();
		this.createRows();
	}
	
	private boolean isModelNameDuplicate(String modelName){
		boolean isPresentInModelGrid = false;
		for(String str: presentmodelNames) {
		    if(str.trim().equalsIgnoreCase(modelName.trim()))
		    	isPresentInModelGrid = true;
		}
		return isPresentInModelGrid;
	}

	
	private String[] getExpressionComboItems(){
		
		List<Expression> expressions = ProjectConfigutration.getInstance().getExpressions();
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

	private void createRows() {
		
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
		comboModelDefinition.setItems(getExpressionComboItems());
		comboModelDefinition.setText("Field Value");
		comboModelDefinition.addListener(SWT.MouseDown, new Listener(){
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				//System.out.println("detected combo click");
				comboModelDefinition.removeAll();
				comboModelDefinition.setItems(getExpressionComboItems());
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
