package com.org.gnos.ui.custom.controls;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.core.Expression;
import com.org.gnos.core.Field;

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
	private List<Expression> expressions;
	private String[] arithemeticOperatorsArray;
	private Composite parent;
	private List<String> presentExpressionNames;
	private Label firstSeparator;
	private Label secondSeparator;
	private Label thirdSeparator;
	private Label fourthSeparator;

	public ExpressionBuilderGrid(Composite parent, int style, List<Field> allSourceFields, List<Expression> expressions) {
		super(parent, style);
		this.parent = parent;
		this.allSourceFields = allSourceFields;
		this.allRows = new ArrayList<Composite>();
		this.expressions = expressions;
		this.arithemeticOperatorsArray = new String[]{"+", "-", "*", "/"};
		this.createContent(parent);
	}
	
	private void createContent(Composite parent){
		this.setLayout(new FormLayout());
		//this.createSourceFieldsComboItems();
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
		for(Expression expression: expressions){
			final Composite compositeRow = new Composite(this, SWT.BORDER);
			compositeRow.setEnabled(false);
			compositeRow.setData(expression);
			compositeRow.setLayout(new FormLayout());
			Color backgroundColor = SWTResourceManager.getColor(SWT.COLOR_WHITE);
			if((this.allRows != null) && (this.allRows.size()%2 != 0)){
				backgroundColor =  SWTResourceManager.getColor(245, 245, 245);
			}

			compositeRow.setBackground(backgroundColor);
			FormData fd_compositeRow = new FormData();
			fd_compositeRow.left = new FormAttachment(this.presentRow, 0, SWT.LEFT);
			fd_compositeRow.right = new FormAttachment(this.presentRow, 0, SWT.RIGHT);
			fd_compositeRow.top = new FormAttachment(this.presentRow);

			Button grade = new Button(compositeRow, SWT.CHECK);
			grade.setSelection(expression.isGrade());
			FormData fd_grade = new FormData();
			fd_grade.left = new FormAttachment(0, 10);
			fd_grade.top = new FormAttachment(compositeRow, 2, SWT.TOP);			
			grade.setLayoutData(fd_grade);

			Text expressionName = new Text(compositeRow, SWT.BORDER);
			expressionName.setText(expression.getName());
			FormData fd_expressionName = new FormData();
			fd_expressionName.left = new FormAttachment(5, 5);
			fd_expressionName.top = new FormAttachment(0);
			fd_expressionName.right = new FormAttachment(20, -5);
			expressionName.setLayoutData(fd_expressionName);

			Button buttonIsComplex = new Button(compositeRow, SWT.CHECK);
			buttonIsComplex.setSelection(expression.isComplex());
			FormData fd_buttonIsComplex = new FormData();
			fd_buttonIsComplex.left = new FormAttachment(24);
			//fd_buttonIsComplex.right = new FormAttachment(30, -5);
			fd_buttonIsComplex.top = new FormAttachment(0,2);
			buttonIsComplex.setLayoutData(fd_buttonIsComplex);

			Text textExpression = new Text(compositeRow, SWT.BORDER);
			textExpression.setText(expression.getExpr_str());
			FormData fd_textExpression = new FormData();
			fd_textExpression.right = new FormAttachment(62, -2);
			fd_textExpression.left = new FormAttachment(30, 2);
			textExpression.setLayoutData(fd_textExpression);
			
			Text textCondition = new Text(compositeRow, SWT.BORDER);
			if(expression.getCondition() != null)
				textCondition.setText(expression.getCondition());
			FormData fd_textCondition = new FormData();
			fd_textCondition.left = new FormAttachment(62, 2);
			fd_textCondition.right = new FormAttachment(100, -2);
			textCondition.setLayoutData(fd_textCondition);

			this.presentRow = compositeRow;
			this.allRows.add(compositeRow);
			compositeRow.setLayoutData(fd_compositeRow);
		}
	}
	
	private boolean isExpressionNameDuplicate(String expressionName){
		boolean isPresentInExpressionGrid = false;
		boolean isPresentInSavedGrid = false;
		for(String str: presentExpressionNames) {
		    if(str.trim().equalsIgnoreCase(expressionName.trim()))
		    	isPresentInExpressionGrid = true;
		}
		return isPresentInExpressionGrid||isPresentInSavedGrid;
	}

	private String[] getSourceFieldsComboItems(){
		int i = 0;
		int sourceFieldSize = this.allSourceFields.size();
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
	
	private void toggleExpressionType(Composite compositeRow, boolean isExpression){

		Composite expressionComposite = new Composite(compositeRow, SWT.NONE);
		expressionComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		//expressionComposite.setBackground(backgroundColor);
		FormData fd_expressionComposite = new FormData();
		fd_expressionComposite.right = new FormAttachment(62, -5);
		fd_expressionComposite.left = new FormAttachment(30, 5);
		expressionComposite.setLayoutData(fd_expressionComposite);
		String[] comboItems = this.getSourceFieldsComboItems();

		if(isExpression == true){
			final Combo comboLeftOperand = new Combo(expressionComposite, SWT.NONE);
			comboLeftOperand.setItems(comboItems);
			comboLeftOperand.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
			comboLeftOperand.setText("Field Value");
			comboLeftOperand.addListener(SWT.MouseDown, new Listener(){
				@Override
				public void handleEvent(Event event) {
					// TODO Auto-generated method stub
					//System.out.println("detected combo click");
					comboLeftOperand.removeAll();
					comboLeftOperand.setItems(getSourceFieldsComboItems());
					comboLeftOperand.getParent().layout();
					comboLeftOperand.setListVisible(true);
				}
			});

			Combo comboOperator = new Combo(expressionComposite, SWT.NONE);
			comboOperator.setItems(this.arithemeticOperatorsArray);
			comboOperator.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
			comboOperator.setText("Operator");

			final Combo comboRightOperand = new Combo(expressionComposite, SWT.NONE);
			comboRightOperand.setItems(comboItems);
			comboRightOperand.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
			comboRightOperand.setText("Field Value");
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

		}else{
			final Combo comboExpressionDefinition = new Combo(expressionComposite, SWT.NONE);
			comboExpressionDefinition.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
			comboExpressionDefinition.setItems(comboItems);
			comboExpressionDefinition.setText("Field Value");
			comboExpressionDefinition.addListener(SWT.MouseDown, new Listener(){
				@Override
				public void handleEvent(Event event) {
					// TODO Auto-generated method stub
					//System.out.println("detected combo click");
					comboExpressionDefinition.removeAll();
					comboExpressionDefinition.setItems(getSourceFieldsComboItems());
					comboExpressionDefinition.getParent().layout();
					comboExpressionDefinition.setListVisible(true);
				}
			});
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
		fd_compositeRow.right = new FormAttachment(this.presentRow, 0, SWT.RIGHT);
		fd_compositeRow.top = new FormAttachment(this.presentRow);

		Button grade = new Button(compositeRow, SWT.CHECK);
		FormData fd_grade = new FormData();
		fd_grade.left = new FormAttachment(0, 10);
		fd_grade.top = new FormAttachment(compositeRow, 2, SWT.TOP);
		grade.setLayoutData(fd_grade);

		Text expressionName = new Text(compositeRow, SWT.BORDER);
		//fd_grade.top = new FormAttachment(expressionName, 2, SWT.TOP);
		FormData fd_expressionName = new FormData();
		fd_expressionName.left = new FormAttachment(5, 5);
		fd_expressionName.top = new FormAttachment(0);
		fd_expressionName.right = new FormAttachment(20, -5);
		expressionName.setLayoutData(fd_expressionName);

		Button buttonIsComplex = new Button(compositeRow, SWT.CHECK);
		FormData fd_buttonIsComplex = new FormData();
		fd_buttonIsComplex.left = new FormAttachment(24);
		fd_buttonIsComplex.top = new FormAttachment(0,2);
		buttonIsComplex.setLayoutData(fd_buttonIsComplex);
		//final Composite me = this;
		this.toggleExpressionType(compositeRow, false);

		buttonIsComplex.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean isSelected = ((Button)e.getSource()).getSelection();
				System.out.println("Selection: " + isSelected);
				if(compositeRow.getChildren()[3] instanceof Text){ //temporary hack, need to identify in a better way
					compositeRow.getChildren()[4].dispose();
				}else{
					compositeRow.getChildren()[3].dispose();
				}
				toggleExpressionType(compositeRow, isSelected);
			}
		});
		
		Text textExpression = new Text(compositeRow, SWT.BORDER);
		FormData fd_textExpression = new FormData();
		fd_textExpression.left = new FormAttachment(62, 2);
		fd_textExpression.right = new FormAttachment(100, -2);
		textExpression.setLayoutData(fd_textExpression);

		this.presentRow = compositeRow;
		this.allRows.add(compositeRow);
		compositeRow.setLayoutData(fd_compositeRow);
	}

	public void resetAllRows(){
		for(Composite existingRow : this.allRows){
			existingRow.setEnabled(false);
		}
		this.allRows = new ArrayList<Composite>();
	}

	public List<Composite> getAllRowsComposite(){
		return this.allRows;
	}

	public List<Expression> getDefinedExpressions(){
		Control[] rowChildren = null;
		this.presentExpressionNames = new ArrayList<String>();
		for(int i = 0; i < allRows.size(); i++){
			Composite row = allRows.get(i);
			Expression expression= (Expression)row.getData();
			
			rowChildren = row.getChildren();
			boolean isGrade = false;
			boolean isComplex = false;
			String expressionName = null;
			String expressionValue = null;

			Control controlGrade = rowChildren[0];
			Control controlExpressionName = rowChildren[1];
			Control controlIsComplex = rowChildren[2];
			Control controlExpressionValue = rowChildren[3];
			Text textCondition = (Text)rowChildren[4];

			if(controlGrade instanceof Button){
				Button buttonControlGrade = (Button)controlGrade;
				isGrade = buttonControlGrade.getSelection();
			}

			if(controlIsComplex instanceof Button){
				Button buttonIsComplex = (Button)controlIsComplex;
				isComplex = buttonIsComplex.getSelection();
			}

			if(controlExpressionName instanceof Text){
				Text textControlExpressionName = (Text)controlExpressionName;
				expressionName = textControlExpressionName.getText();				
			}

			if(expressionName == null || expressionName == ""){
				continue;
			}
			
			if(isExpressionNameDuplicate(expressionName)){
				MessageDialog.openError(this.parent.getShell(), "GNOS Error", "Expression name: " + expressionName + " already exists. Please use a unique expression name.");
				return null;
			}else{
				presentExpressionNames.add(expressionName);
			}

			if(expression == null){
				expression = new Expression(expressionName);
				this.expressions.add(expression);
			}

			if(controlExpressionValue instanceof Composite){
				Composite compositeExpressionValue = (Composite)controlExpressionValue;
				if(isComplex == true){
					Combo leftOperand = (Combo)compositeExpressionValue.getChildren()[0];
					Combo operator = (Combo)compositeExpressionValue.getChildren()[1];
					Combo rightOperand = (Combo)compositeExpressionValue.getChildren()[2];

					String leftOperandValue = leftOperand.getText();
					String rightOperandValue = rightOperand.getText();
					String operatorValue = operator.getText();

					expression.setExpr_str(leftOperandValue + operatorValue + rightOperandValue);

				}else{
					Combo sourceField = (Combo)compositeExpressionValue.getChildren()[0];
					expressionValue = sourceField.getText();
					int index = -1;
					for (int j=0; j<this.allSourceFields.size(); j++) {
						String columnName = this.allSourceFields.get(j).getName();

						if (columnName.equalsIgnoreCase(expressionValue)) {
							index = j;
							break;
						}
					}
					if(index<0){
						MessageDialog.openError(this.parent.getShell(), "GNOS Error", "Please map a proper value for the expression: " + expressionName);
						return null;
					}
					expression.setExpr_str(expressionValue);
				}
			}

			expression.setGrade(isGrade);
			expression.setComplex(isComplex);
			String condition = textCondition.getText();
			expression.setCondition(condition);
/*			if(condition == null || condition == ""){
				condition = "[bin]==[bin]";
				System.out.println("Condition: " + condition);//temporary hack to set everything when no condition
			}
			boolean isConditionValid = expression.setCondition(condition);
			System.out.println("Condition: " + isConditionValid);
			if(!isConditionValid){
				MessageDialog.openError(this.parent.getShell(), "GNOS Error", "Conditions not defined properly.");
				return null;
			} */
		}
		return this.expressions;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
