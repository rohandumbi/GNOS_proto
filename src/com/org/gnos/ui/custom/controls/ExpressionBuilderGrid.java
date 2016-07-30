package com.org.gnos.ui.custom.controls;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Field;

public class ExpressionBuilderGrid extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private List<Field> allSourceFields;
	private Composite compositeGridHeader;
	private List<Composite> allRows;
	private List<String> numericSourceFields;
	private String[] sourceFieldsComboItems;
	private Composite presentRow;
	private List<Expression> existingExpressions;
	private String[] arithemeticOperatorsArray;
	private Label firstSeparator;
	private Label secondSeparator;
	private Label thirdSeparator;
	private Label fourthSeparator;

	public ExpressionBuilderGrid(Composite parent, int style) {
		super(parent, style);
		this.allSourceFields = ProjectConfigutration.getInstance().getFields();
		this.allRows = new ArrayList<Composite>();
		this.existingExpressions = ProjectConfigutration.getInstance().getExpressions();
		this.arithemeticOperatorsArray = new String[]{"+", "-", "*", "/"};
		this.createContent(parent);
	}
	
	private void createContent(Composite parent){
		this.setLayout(new FormLayout());
		this.createHeader();
		this.createRows();
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

		this.firstSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_firstSeparator = new FormData();
		fd_firstSeparator.left = new FormAttachment(5);
		this.firstSeparator.setLayoutData(fd_firstSeparator);

		this.secondSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_secondSeparator = new FormData();
		fd_secondSeparator.left = new FormAttachment(20);
		this.secondSeparator.setLayoutData(fd_secondSeparator);


		this.thirdSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_thirdSeparator = new FormData();
		fd_thirdSeparator.left = new FormAttachment(30);
		this.thirdSeparator.setLayoutData(fd_thirdSeparator);

		this.fourthSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_fourthSeparator = new FormData();
		fd_fourthSeparator.left = new FormAttachment(62);
		this.fourthSeparator.setLayoutData(fd_fourthSeparator);

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
		lblFiltersHeader.setText("CONDITIONS (Empty means everything)");
		this.presentRow = this.compositeGridHeader;//referring to the header as the 1st row when there are no rows inserted yet

	}

	private void createRows() {
		for(Expression expression: existingExpressions){
			this.addRow(expression);
		}
	}
	
	private String[] getSourceFieldsComboItems(){
		int i = 0;
		this.numericSourceFields = new ArrayList<String>();
		for(Field field: this.allSourceFields){
			if(field.getDataType() == Field.TYPE_NUMBER){
				this.numericSourceFields.add(field.getName());
			}		
		}
		this.sourceFieldsComboItems = new String[this.numericSourceFields.size()];
		for(i=0; i<this.numericSourceFields.size(); i++){
			this.sourceFieldsComboItems[i] = this.numericSourceFields.get(i);
		}
		return this.sourceFieldsComboItems;
	}
	
	/*
	 * returns a complex expression broken into an arraylist
	 * with 0th index containing the operator, 1st index containing the left operand
	 * and 2nd index containing right operand
	 */
	
	private List<String> getExpressionDefinitionComponents(String expressionDefinition){
		List<String> expressionComponents = new ArrayList<String>();
		String delim = null;
		if(expressionDefinition.indexOf('*') != -1){
			delim = "*";
		}else if(expressionDefinition.indexOf('/') != -1){
			delim = "/";
		}else if(expressionDefinition.indexOf('+') != -1){
			delim = "+";
		}else if(expressionDefinition.indexOf('-') != -1){
			delim = "-";
		}
		
		if(delim == null){
			expressionComponents.add(expressionDefinition);
			return expressionComponents;
		}else{
			expressionComponents.add(delim);
			String[] tokens = expressionDefinition.split("\\"+delim);
			for(String token : tokens){
				expressionComponents.add(token);
			}
		}
		return expressionComponents;
	}
	
	private void toggleExpressionType(Composite compositeRow){

		Composite expressionComposite = new Composite(compositeRow, SWT.NONE);
		final Expression associatedExpression = (Expression)compositeRow.getData();
		boolean isComplexExpression = associatedExpression.isComplex();
		expressionComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		String expressionDefinition = associatedExpression.getExprvalue();
		FormData fd_expressionComposite = new FormData();
		fd_expressionComposite.right = new FormAttachment(62, -5);
		fd_expressionComposite.left = new FormAttachment(30, 5);
		expressionComposite.setLayoutData(fd_expressionComposite);
		String[] comboItems = this.getSourceFieldsComboItems();
		
		if(isComplexExpression == true){
			String oper = null;
			String left = null;
			String right = null;
			if(expressionDefinition != null){
				List<String> expressionComponents = this.getExpressionDefinitionComponents(expressionDefinition);
				
				oper = expressionComponents.get(0);
				left = expressionComponents.get(1);
				right = expressionComponents.get(2);
			}
			
			final Combo comboLeftOperand = new Combo(expressionComposite, SWT.NONE);
			comboLeftOperand.setItems(comboItems);
			comboLeftOperand.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
			if(left != null){
				comboLeftOperand.setText(left);
			}else{
				comboLeftOperand.setText("Field Value");
			}
			comboLeftOperand.addListener(SWT.MouseDown, new Listener(){
				@Override
				public void handleEvent(Event event) {
					// TODO Auto-generated method stub
					comboLeftOperand.removeAll();
					comboLeftOperand.setItems(getSourceFieldsComboItems());
					comboLeftOperand.getParent().layout();
					comboLeftOperand.setListVisible(true);
				}
			});

			final Combo comboOperator = new Combo(expressionComposite, SWT.NONE);
			comboOperator.setItems(this.arithemeticOperatorsArray);
			comboOperator.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
			if(oper != null){
				comboOperator.setText(oper);
			}else{
				comboOperator.setText("Operator");
			}

			final Combo comboRightOperand = new Combo(expressionComposite, SWT.NONE);
			comboRightOperand.setItems(comboItems);
			comboRightOperand.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
			if(right != null){
				comboRightOperand.setText(right);
			}else{
				comboRightOperand.setText("Field Value");
			}
			comboRightOperand.addListener(SWT.MouseDown, new Listener(){
				@Override
				public void handleEvent(Event event) {
					// TODO Auto-generated method stub
					//System.out.println("detected combo click");
					comboRightOperand.removeAll();
					comboRightOperand.setItems(getSourceFieldsComboItems());
					comboRightOperand.getParent().layout();
					comboRightOperand.setListVisible(true);
				}
			});
			
			comboLeftOperand.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					String leftOperand = comboLeftOperand.getText();
					String operator = comboOperator.getText();
					String rightOperand = comboRightOperand.getText();
					String expressionValue = leftOperand + operator + rightOperand;
					associatedExpression.setExprvalue(expressionValue);
				}
			});
			comboOperator.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					String leftOperand = comboLeftOperand.getText();
					String operator = comboOperator.getText();
					String rightOperand = comboRightOperand.getText();
					String expressionValue = leftOperand + operator + rightOperand;
					associatedExpression.setExprvalue(expressionValue);
				}
			});
			comboRightOperand.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					String leftOperand = comboLeftOperand.getText();
					String operator = comboOperator.getText();
					String rightOperand = comboRightOperand.getText();
					String expressionValue = leftOperand + operator + rightOperand;
					associatedExpression.setExprvalue(expressionValue);
				}
			});

		}else{
			final Combo comboExpressionDefinition = new Combo(expressionComposite, SWT.NONE);
			comboExpressionDefinition.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
			comboExpressionDefinition.setItems(comboItems);
			if((expressionDefinition != null)){
				comboExpressionDefinition.setText(expressionDefinition);
			}else{
				comboExpressionDefinition.setText("Field Value");
			}
			comboExpressionDefinition.addListener(SWT.MouseDown, new Listener(){
				@Override
				public void handleEvent(Event event) {
					// TODO Auto-generated method stub
					comboExpressionDefinition.removeAll();
					comboExpressionDefinition.setItems(getSourceFieldsComboItems());
					comboExpressionDefinition.getParent().layout();
					comboExpressionDefinition.setListVisible(true);
				}
			});
			comboExpressionDefinition.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					String expressionValue = comboExpressionDefinition.getText();
					associatedExpression.setExprvalue(expressionValue);
				}
			});
		}
		compositeRow.layout();
	}
	
	private void addRow(final Expression expression){
		final Composite compositeRow = new Composite(this, SWT.BORDER);
		compositeRow.setLayout(new FormLayout());
		compositeRow.setData(expression);
		Color backgroundColor = SWTResourceManager.getColor(SWT.COLOR_WHITE);
		if((this.allRows != null) && (this.allRows.size()%2 != 0)){
			backgroundColor =  SWTResourceManager.getColor(245, 245, 245);
		}

		compositeRow.setBackground(backgroundColor);
		FormData fd_compositeRow = new FormData();
		fd_compositeRow.left = new FormAttachment(this.presentRow, 0, SWT.LEFT);
		fd_compositeRow.right = new FormAttachment(this.presentRow, 0, SWT.RIGHT);
		fd_compositeRow.top = new FormAttachment(this.presentRow);

		final Button grade = new Button(compositeRow, SWT.CHECK);
		FormData fd_grade = new FormData();
		fd_grade.left = new FormAttachment(0, 10);
		fd_grade.top = new FormAttachment(compositeRow, 2, SWT.TOP);
		grade.setLayoutData(fd_grade);
		grade.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				expression.setGrade(grade.getSelection());
			}
		});
		grade.setSelection(expression.isGrade());

		final Text textExpressionName = new Text(compositeRow, SWT.BORDER);
		//fd_grade.top = new FormAttachment(expressionName, 2, SWT.TOP);
		FormData fd_textExpressionName = new FormData();
		fd_textExpressionName.left = new FormAttachment(5, 5);
		fd_textExpressionName.top = new FormAttachment(0);
		fd_textExpressionName.right = new FormAttachment(20, -5);
		textExpressionName.setLayoutData(fd_textExpressionName);
		textExpressionName.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent event) {
				// Get the widget whose text was modified
				Text text = (Text) event.widget;
				String expressionName = text.getText();
				expression.setName(expressionName);
			}
		});
		String expressionName = expression.getName();
		if(expressionName != null){
			textExpressionName.setText(expressionName);
		}

		Button buttonIsComplex = new Button(compositeRow, SWT.CHECK);
		FormData fd_buttonIsComplex = new FormData();
		fd_buttonIsComplex.left = new FormAttachment(24);
		fd_buttonIsComplex.top = new FormAttachment(0,2);
		buttonIsComplex.setLayoutData(fd_buttonIsComplex);
		buttonIsComplex.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean isComplex = ((Button)e.getSource()).getSelection();
				System.out.println("Selection: " + isComplex);
				if(compositeRow.getChildren()[3] instanceof Text){ //temporary hack, need to identify in a better way
					compositeRow.getChildren()[4].dispose();
				}else{
					compositeRow.getChildren()[3].dispose();
				}
				expression.setComplex(isComplex);
				toggleExpressionType(compositeRow);
			}
		});
		buttonIsComplex.setSelection(expression.isComplex());
		
		this.toggleExpressionType(compositeRow);
		
		Text textCondition = new Text(compositeRow, SWT.BORDER);
		FormData fd_textCondition = new FormData();
		fd_textCondition.left = new FormAttachment(62, 2);
		fd_textCondition.right = new FormAttachment(100, -2);
		textCondition.setLayoutData(fd_textCondition);
		textCondition.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent event) {
				// Get the widget whose text was modified
				Text text = (Text) event.widget;
				String filter = text.getText();
				expression.setFilter(filter);
			}
		});
		String condition = expression.getFilter();
		if(condition != null){
			textCondition.setText(condition);
		}

		this.presentRow = compositeRow;
		this.allRows.add(compositeRow);
		compositeRow.setLayoutData(fd_compositeRow);
		this.layout();
	}

	public void addRow(){
		Expression newExpression = new Expression();
		this.existingExpressions.add(newExpression);
		this.addRow(newExpression);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
