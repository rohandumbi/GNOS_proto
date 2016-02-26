package com.org.gnos.custom.controls;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.services.Expression;
import com.org.gnos.services.csv.ColumnHeader;

public class ExpressionBuilderGrid extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private List<ColumnHeader> allSourceFields;
	private Composite compositeGridHeader;
	private List<Composite> allRows;
	private List<String> numericSourceFields;
	private String[] sourceFieldsComboItems;
	private Composite presentRow;
	private List<Expression> expressionList;
	private String[] arithemeticOperatorsArray;
	
	public ExpressionBuilderGrid(Composite parent, int style, List<ColumnHeader> allSourceFields) {
		super(parent, style);
		//this.requiredFieldNames = requiredFieldNames;
		//this.parent = parent;
		this.allSourceFields = allSourceFields;
		this.allRows = new ArrayList<Composite>();
		this.expressionList = new ArrayList<Expression>();
		//this.numericSourceFields = new ArrayList<String>();
		//this.dataTypes = dataTypes;
		this.arithemeticOperatorsArray = new String[]{"+", "-", "*", "/"};
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
	private String[] getSourceFieldsComboItems(){
		int i = 0;
		int sourceFieldSize = this.allSourceFields.size();
		//this.sourceFieldsComboItems = new String[sourceFieldSize];
		this.numericSourceFields = new ArrayList<String>();
		for(i=0; i<sourceFieldSize; i++){
			if((this.allSourceFields.get(i).getDataType() == 2) || (this.allSourceFields.get(i).getDataType() == 3)){ //only double or integer fields are allowed in expression definition
				//this.sourceFieldsComboItems[i] = this.allSourceFields.get(i).getName();
				this.numericSourceFields.add(this.allSourceFields.get(i).getName());
			}
		}
		this.sourceFieldsComboItems = new String[this.numericSourceFields.size()];
		for(i=0; i<this.numericSourceFields.size(); i++){
			this.sourceFieldsComboItems[i] = this.numericSourceFields.get(i);
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
		fd_firstSeparator.left = new FormAttachment(5);
		firstSeparator.setLayoutData(fd_firstSeparator);
		
		Label secondSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_secondSeparator = new FormData();
		fd_secondSeparator.left = new FormAttachment(30);
		secondSeparator.setLayoutData(fd_secondSeparator);

		
		Label thirdSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_thirdSeparator = new FormData();
		fd_thirdSeparator.left = new FormAttachment(40);
		thirdSeparator.setLayoutData(fd_thirdSeparator);
		
		Label fourthSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_fourthSeparator = new FormData();
		fd_fourthSeparator.left = new FormAttachment(70);
		fourthSeparator.setLayoutData(fd_fourthSeparator);
		
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
		
		Label lblExpressionTypeHeader = new Label(compositeGridHeader, SWT.NONE);
		lblExpressionTypeHeader.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		FormData fd_lblExpressionTypeHeader = new FormData();
		fd_lblExpressionTypeHeader.top = new FormAttachment(0,2);
		fd_lblExpressionTypeHeader.left = new FormAttachment(secondSeparator, 10);
		lblExpressionTypeHeader.setLayoutData(fd_lblExpressionTypeHeader);
		lblExpressionTypeHeader.setText("IS COMPLEX");
		lblExpressionTypeHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));

		Label lblExpressionDefinitionHeader = new Label(compositeGridHeader, SWT.NONE);
		lblExpressionDefinitionHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));
		lblExpressionDefinitionHeader.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		FormData fd_lblExpressionDefinitionHeader = new FormData();
		fd_lblExpressionDefinitionHeader.top = new FormAttachment(0, 2);
		fd_lblExpressionDefinitionHeader.left = new FormAttachment(thirdSeparator, 10);
		lblExpressionDefinitionHeader.setLayoutData(fd_lblExpressionDefinitionHeader);
		lblExpressionDefinitionHeader.setText("EXPRESSION DEFINITION");

		Label lblFiltersHeader = new Label(compositeGridHeader, SWT.NONE);
		lblFiltersHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));
		lblFiltersHeader.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		FormData fd_lblFiltersHeader = new FormData();
		fd_lblFiltersHeader.top = new FormAttachment(0,2);
		fd_lblFiltersHeader.left = new FormAttachment(fourthSeparator, 10);
		lblFiltersHeader.setLayoutData(fd_lblFiltersHeader);
		lblFiltersHeader.setText("FILTERS");
		this.presentRow = this.compositeGridHeader;//referring to the header as the 1st row when there are no rows inserted yet
		
	}
	
	private void toggleExpressionType(Composite compositeRow, boolean isExpression){
		
		Composite expressionComposite = new Composite(compositeRow, SWT.NONE);
		expressionComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		//expressionComposite.setBackground(backgroundColor);
		FormData fd_expressionComposite = new FormData();
		fd_expressionComposite.right = new FormAttachment(70, -5);
		fd_expressionComposite.left = new FormAttachment(40, 5);
		expressionComposite.setLayoutData(fd_expressionComposite);
		String[] comboItems = this.getSourceFieldsComboItems();
		
		if(isExpression == true){
			Combo comboLeftOperand = new Combo(expressionComposite, SWT.NONE);
			comboLeftOperand.setItems(comboItems);
			comboLeftOperand.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
			comboLeftOperand.setText("Field Value");
			
			Combo comboOperator = new Combo(expressionComposite, SWT.NONE);
			comboOperator.setItems(this.arithemeticOperatorsArray);
			comboOperator.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
			comboOperator.setText("Operator");
			
			Combo comboRightOperand = new Combo(expressionComposite, SWT.NONE);
			comboRightOperand.setItems(comboItems);
			comboRightOperand.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
			comboRightOperand.setText("Field Value");
			
		}else{
			Combo comboExpressionDefinition = new Combo(expressionComposite, SWT.NONE);
			comboExpressionDefinition.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
			comboExpressionDefinition.setItems(comboItems);
			comboExpressionDefinition.setText("Field Value");
		}
		compositeRow.layout();
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
		fd_compositeRow.bottom = new FormAttachment(this.presentRow, 26, SWT.BOTTOM);
		fd_compositeRow.right = new FormAttachment(this.presentRow, 0, SWT.RIGHT);
		fd_compositeRow.top = new FormAttachment(this.presentRow);
		
		Button grade = new Button(compositeRow, SWT.CHECK);
		FormData fd_grade = new FormData();
		fd_grade.left = new FormAttachment(0, 10);
		grade.setLayoutData(fd_grade);
		
		Text expressionName = new Text(compositeRow, SWT.BORDER);
		fd_grade.top = new FormAttachment(expressionName, 2, SWT.TOP);
		FormData fd_expressionName = new FormData();
		fd_expressionName.left = new FormAttachment(5, 5);
		fd_expressionName.top = new FormAttachment(0);
		fd_expressionName.right = new FormAttachment(30, -5);
		expressionName.setLayoutData(fd_expressionName);
		
		/*Composite expressionComposite = new Composite(compositeRow, SWT.NONE);
		expressionComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		expressionComposite.setBackground(backgroundColor);
		FormData fd_expressionComposite = new FormData();
		fd_expressionComposite.right = new FormAttachment(70, -5);
		fd_expressionComposite.left = new FormAttachment(40, 5);
		expressionComposite.setLayoutData(fd_expressionComposite);*/
		
		Button buttonIsComplex = new Button(compositeRow, SWT.CHECK);
		FormData fd_buttonIsComplex = new FormData();
		fd_buttonIsComplex.left = new FormAttachment(34);
		fd_buttonIsComplex.top = new FormAttachment(0,2);
		buttonIsComplex.setLayoutData(fd_buttonIsComplex);
		//final Composite me = this;
		
		buttonIsComplex.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean isSelected = ((Button)e.getSource()).getSelection();
				System.out.println("Selection: " + isSelected);
				compositeRow.getChildren()[3].dispose();//temporary hack, need to identify in a better way
				toggleExpressionType(compositeRow, isSelected);
			}
		});
		
		this.toggleExpressionType(compositeRow, false);
		
		this.presentRow = compositeRow;
		this.allRows.add(compositeRow);
		compositeRow.setLayoutData(fd_compositeRow);
	}

	private void createContent(Composite parent){
		this.setLayout(new FormLayout());
		//this.createSourceFieldsComboItems();
		this.createHeader();
		
	}
	
	public List<Expression> getDefinedExpressions(){
		Control[] rowChildren = null;
		for(int i = 0; i < allRows.size(); i++){
			rowChildren = allRows.get(i).getChildren();
			boolean isGrade = false;
			String expressionName = null;
			String expressionValue = null;
			
			Control controlGrade = rowChildren[0];
			Control controlExpressionName = rowChildren[1];
			Control controlExpressionValue = rowChildren[3];
			
			if(controlGrade instanceof Button){
				Button buttonControlGrade = (Button)controlGrade;
				isGrade = buttonControlGrade.getSelection();
			}
			
			if(controlExpressionName instanceof Text){
				Text textControlExpressionName = (Text)controlExpressionName;
				expressionName = textControlExpressionName.getText();
			}
			
			
			
			if(controlExpressionValue instanceof Composite){
				Composite compositeExpressionValue = (Composite)controlExpressionValue;
				Control[] controlExpression = compositeExpressionValue.getChildren();
				
				if(controlExpression.length == 1){
					Combo leftOperand = (Combo)controlExpression[0];
					expressionValue = leftOperand.getText();
				}else{
					Combo leftOperand = (Combo)controlExpression[0];
					Combo operator = (Combo)controlExpression[1];
					Combo rightOperand = (Combo)controlExpression[2];
				}
				
				//Combo comboDatatype = (Combo)compositeDatatype;
				//expressionValue = leftOperand.getText();
			}
			
			Expression expression = new Expression(expressionName);
			int index = -1;
			for (int j=0; j<this.allSourceFields.size(); j++) {
			    ColumnHeader columnHeader = this.allSourceFields.get(j);
				
				if (columnHeader.getName().equalsIgnoreCase(expressionValue)) {
			        index = j;
			        break;
			    }
			}
			expression.setValue(index);
			expression.setGrade(isGrade);
			
			this.expressionList.add(expression);
		}
		return this.expressionList;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
