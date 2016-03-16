package com.org.gnos.custom.controls;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.org.gnos.services.Expression;
import com.org.gnos.services.Operation;
import com.org.gnos.services.csv.GNOSCSVDataProcessor;

public class ExpressionBuilderGrid extends Composite {

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
	private List<Expression> expressionList;
	private String[] arithemeticOperatorsArray;
	private Composite parent;

	public ExpressionBuilderGrid(Composite parent, int style, String[] allSourceFields) {
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

	/*private int getDatatypeCode(String dataType){
		if(dataType.equalsIgnoreCase("String")){
			return 1;
		}else if(dataType.equalsIgnoreCase("Integer")){
			return 2;
		}else if(dataType.equalsIgnoreCase("Double")){
			return 3;
		}else{
			return 0;
		}
	}*/
	private String[] getSourceFieldsComboItems(){
		int i = 0;
		int sourceFieldSize = this.allSourceFields.length;
		this.numericSourceFields = new ArrayList<String>();
		Map dataTypeMap = GNOSCSVDataProcessor.getInstance().getDataTypeMapping();
		//Set<String> keys = dataTypeMap.entrySet();
		Set<String> keys = dataTypeMap.keySet();
		Iterator<String> it = keys.iterator();
		while(it.hasNext()){
			String key = it.next();
			String value = (String)dataTypeMap.get(key);

			if(!"Text".equalsIgnoreCase(value)){
				this.numericSourceFields.add(key);
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
		fd_secondSeparator.left = new FormAttachment(20);
		secondSeparator.setLayoutData(fd_secondSeparator);


		Label thirdSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_thirdSeparator = new FormData();
		fd_thirdSeparator.left = new FormAttachment(30);
		thirdSeparator.setLayoutData(fd_thirdSeparator);

		Label fourthSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_fourthSeparator = new FormData();
		fd_fourthSeparator.left = new FormAttachment(62);
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
		lblFiltersHeader.setText("CONDITIONS (Empty means everything)");
		this.presentRow = this.compositeGridHeader;//referring to the header as the 1st row when there are no rows inserted yet

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
		//fd_compositeRow.bottom = new FormAttachment(this.presentRow, 26, SWT.BOTTOM);
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

		/*Composite expressionComposite = new Composite(compositeRow, SWT.NONE);
		expressionComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		expressionComposite.setBackground(backgroundColor);
		FormData fd_expressionComposite = new FormData();
		fd_expressionComposite.right = new FormAttachment(70, -5);
		fd_expressionComposite.left = new FormAttachment(40, 5);
		expressionComposite.setLayoutData(fd_expressionComposite);*/

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
				if(compositeRow.getChildren()[3] instanceof GnosConditionCellComposite){ //temporary hack, need to identify in a better way
					compositeRow.getChildren()[4].dispose();
				}else{
					compositeRow.getChildren()[3].dispose();
				}
				toggleExpressionType(compositeRow, isSelected);
			}
		});

		/*GnosConditionCellComposite gnosExpressionComposite = new GnosConditionCellComposite(compositeRow, SWT.NONE, this.allSourceFields);
		FormData fd_gnosExpressionComposite = new FormData();
		fd_gnosExpressionComposite.left = new FormAttachment(62, 2);
		fd_gnosExpressionComposite.right = new FormAttachment(100);
		gnosExpressionComposite.setLayoutData(fd_gnosExpressionComposite);*/

		Text textExpression = new Text(compositeRow, SWT.BORDER);
		FormData fd_textExpression = new FormData();
		fd_textExpression.left = new FormAttachment(62, 2);
		fd_textExpression.right = new FormAttachment(100, -2);
		textExpression.setLayoutData(fd_textExpression);

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

	public List<Expression> getDefinedExpressions(){
		Control[] rowChildren = null;
		this.expressionList = new ArrayList<Expression>();
		for(int i = 0; i < allRows.size(); i++){
			rowChildren = allRows.get(i).getChildren();
			boolean isGrade = false;
			boolean isComplex = false;
			String expressionName = null;
			String expressionValue = null;

			Control controlGrade = rowChildren[0];
			Control controlExpressionName = rowChildren[1];
			Control controlIsComplex = rowChildren[2];

			Expression expression= null;

			Control controlExpressionValue = null;
			//GnosConditionCellComposite conditionComposite = null;

			Text textCondition = null;

			if(rowChildren[3] instanceof Text){ //temporary hack, need to identify in a better way
				controlExpressionValue = rowChildren[4];
				textCondition = (Text)rowChildren[3];
			}else{
				controlExpressionValue = rowChildren[3];
				textCondition = (Text)rowChildren[4];
			}

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
				expression = new Expression(expressionName);
			}

			if(expressionName == null || expressionName == ""){
				MessageDialog.openError(this.parent.getShell(), "GNOS Error", "Please enter a valid name for expression.");
				return null;
			}



			if(controlExpressionValue instanceof Composite){
				Composite compositeExpressionValue = (Composite)controlExpressionValue;
				if(isComplex == true){
					//expression.setValueType(isComplex);
					Combo leftOperand = (Combo)compositeExpressionValue.getChildren()[0];
					Combo operator = (Combo)compositeExpressionValue.getChildren()[1];
					Combo rightOperand = (Combo)compositeExpressionValue.getChildren()[2];

					String leftOperandValue = leftOperand.getText();
					String rightOperandValue = rightOperand.getText();

					int leftOperandIndex = -1;
					int rightOperandIndex = -1;

					/*
					 * Can't directly take the selection index as the column id because,
					 * in the field combo only numeric fields are present, whereas in the columns
					 * table all types of columns are present. Hence searching for the correct index
					 * of left/right operand by comparing the text value with all column names.
					 * Some complexity may be reduceable. Later!!!
					 */
					for (int j=0; j<this.allSourceFields.length; j++) {
						String columnName = this.allSourceFields[j];

						if (columnName.equalsIgnoreCase(leftOperandValue)) {
							leftOperandIndex = j;
							break;
						}
					}

					for (int k=0; k<this.allSourceFields.length; k++) {
						String columnName = this.allSourceFields[k];

						if (columnName.equalsIgnoreCase(rightOperandValue)) {
							rightOperandIndex = k;
							break;
						}
					}

					int operatorIndex = operator.getSelectionIndex();
					if(leftOperandIndex<0 || rightOperandIndex<0 || rightOperandIndex<0){
						MessageDialog.openError(this.parent.getShell(), "GNOS Error", "Expression value not properly defined: " + expressionName);
						return null;
					}

					Operation operation = new Operation();
					operation.setOperand_left(leftOperandIndex);
					operation.setOperand_right(rightOperandIndex);
					operation.setOperator(operatorIndex);

					expression.setValue(-1);
					expression.setOperation(operation);

				}else{
					//expression.setValueType(isComplex);
					Combo sourceField = (Combo)compositeExpressionValue.getChildren()[0];
					expressionValue = sourceField.getText();
					int index = -1;
					for (int j=0; j<this.allSourceFields.length; j++) {
						String columnName = this.allSourceFields[j];

						if (columnName.equalsIgnoreCase(expressionValue)) {
							index = j;
							break;
						}
					}
					if(index<0){
						MessageDialog.openError(this.parent.getShell(), "GNOS Error", "Please map a proper value for the expression: " + expressionName);
						return null;
					}
					expression.setValue(index);
					expression.setOperation(null);
				}
			}

			expression.setGrade(isGrade);
			expression.setValueType(isComplex);
			//List<Filter> filters = conditionComposite.getExpressionFilters();
			String condition = textCondition.getText();
			/*if(condition == null){
				MessageDialog.openError(this.parent.getShell(), "GNOS Error", "Conditions not defined properly.");
				return null;
			}else{
				//expression.setCondition("[bin]>30 AND [bin] < 70");
				boolean isConditionValid = expression.setCondition(condition);
				if(!isConditionValid){
					MessageDialog.openError(this.parent.getShell(), "GNOS Error", "Conditions not defined properly.");
					return null;
				}

			}*/
			if(condition == null || condition == ""){
				//condition = "[" + "bin" + "]" + "==" + "[" + "bin" + "]";//temporary hack to set everything when no condition
				condition = "[bin]==[bin]";
				System.out.println("Condition: " + condition);
			}
			boolean isConditionValid = expression.setCondition(condition);
			System.out.println("Condition: " + isConditionValid);
			if(!isConditionValid){
				MessageDialog.openError(this.parent.getShell(), "GNOS Error", "Conditions not defined properly.");
				return null;
			} 

			this.expressionList.add(expression);
		}
		return this.expressionList;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
