package com.org.gnos.ui.custom.controls;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
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

import com.org.gnos.core.Expression;
import com.org.gnos.core.Field;

public class SavedExpressionsGrid extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private Composite compositeGridHeader;
	private List<Composite> allRows;
	private Composite presentRow;
	private List<Expression> expressions;
	private Composite parent;
	
	public SavedExpressionsGrid(Composite parent, int style, List<Expression> expressions, List<Field> fields) {
		super(parent, style);
		this.parent = parent;
		this.expressions = expressions;
		this.allRows = new ArrayList<Composite>();
		this.createContent(parent);
	}

	private void createContent(Composite parent){
		this.setLayout(new FormLayout());
		this.createHeader();
		//this.createRows();
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
	public void createRows() {
		for(Expression expression: expressions) {
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
			//this.toggleExpressionType(compositeRow, false);

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
					//toggleExpressionType(compositeRow, isSelected);
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
	}
	
	public void addRows(List<Composite> compositeSavedExpressionCollection){
		Composite controlExpressionValue = null;
		String expressionValue = null;
		for(int i=0; i<compositeSavedExpressionCollection.size(); i++){
			Composite compositeSavedExpression = compositeSavedExpressionCollection.get(i);
			Control[] rowChildren = compositeSavedExpression.getChildren();
			if(rowChildren[3] instanceof Text){ //temporary hack, need to identify in a better way
				controlExpressionValue = (Composite)rowChildren[4];
				//textCondition = (Text)rowChildren[3];
			}else{
				controlExpressionValue = (Composite)rowChildren[3];
				//textCondition = (Text)rowChildren[4];
			}
			
			if(controlExpressionValue.getChildren().length > 1){//complex expression
				Control[] expressionChildren = controlExpressionValue.getChildren();
				Combo leftOperand = (Combo)expressionChildren[0];
				Combo operator = (Combo)expressionChildren[1];
				Combo rightOperand = (Combo)expressionChildren[2];
				
				String sLeftOperand = leftOperand.getText();
				String sOperator = operator.getText();
				String sRightOperand = rightOperand.getText();
				
				expressionValue = sLeftOperand + sOperator + sRightOperand;
				
			}else{//simple expression
				Control[] expressionChildren = controlExpressionValue.getChildren();
				Combo operand = (Combo)expressionChildren[0];
				expressionValue = operand.getText();
			}
			System.out.println("Expression Value is:  " + expressionValue);
			controlExpressionValue.dispose();
			//controlExpressionValue.getParent().layout();
			//controlExpressionValue = null;
			Text textExpression = new Text(compositeSavedExpression, SWT.BORDER);
			textExpression.setText(expressionValue);
			FormData fd_textExpression = new FormData();
			fd_textExpression.right = new FormAttachment(62, -5);
			fd_textExpression.left = new FormAttachment(30, 5);
			textExpression.setLayoutData(fd_textExpression);
			
			compositeSavedExpression.setParent(this);
			FormData fd_compositeRow = new FormData();
			fd_compositeRow.left = new FormAttachment(this.presentRow, 0, SWT.LEFT);
			//fd_compositeRow.bottom = new FormAttachment(this.presentRow, 26, SWT.BOTTOM);
			fd_compositeRow.right = new FormAttachment(this.presentRow, 0, SWT.RIGHT);
			fd_compositeRow.top = new FormAttachment(this.presentRow);
			compositeSavedExpression.setLayoutData(fd_compositeRow);
			this.presentRow = compositeSavedExpression;
		}
	}


	
	public void resetAllRows(){
		for(Composite existingRow : this.allRows){
			existingRow.setEnabled(false);
		}
		this.allRows = new ArrayList<Composite>();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
