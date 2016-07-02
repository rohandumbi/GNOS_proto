package com.org.gnos.ui.custom.controls;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Grade;
import com.org.gnos.db.model.ProcessConstraintData;

public class ProductDefinitionDialog extends Dialog {

	//private ProcessDefinitionFormScreen processDefinitionFormScreen;
	private String[] availableExpressionNames;
	private Label lblProductName;
	private Composite container;
	private Text textProductName;
	private Button btnAddExpression;
	private Button btnAddGrade;
	private ArrayList<Combo> listOfChildExpressionCombos;
	private Control presentRow;
	private String productName;
	private List<Expression> associatedExpressions;
	private ArrayList<Grade> associatedGrades;
	private ScrolledComposite scrollContainerExpressions;
	private ScrolledComposite scrollContainerGrades;
	private Composite expressionListContainerComposite;
	private Composite gradeListContainerComposite;
	private Composite presentGrid;
	
	
	public ProductDefinitionDialog(Shell parentShell, String[] availableExpressionNames) {
		super(parentShell);
		this.availableExpressionNames = availableExpressionNames;
		this.listOfChildExpressionCombos = new ArrayList<Combo>();
		this.associatedExpressions = new ArrayList<Expression>();
		this.associatedGrades = new ArrayList<Grade>();
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		this.container = (Composite) super.createDialogArea(parent);
		this.container.setLayout(new FormLayout());
		
		this.lblProductName = new Label(this.container, SWT.NONE);
		this.lblProductName.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblProductName = new FormData();
		fd_lblProductName.top = new FormAttachment(0, 10);
		fd_lblProductName.left = new FormAttachment(0, 10);
		this.lblProductName.setLayoutData(fd_lblProductName);
		this.lblProductName.setText("Product Name:");
		
		this.textProductName = new Text(this.container, SWT.BORDER);
		FormData fd_textProductName = new FormData();
		fd_textProductName.top = new FormAttachment(0, 10);
		fd_textProductName.left = new FormAttachment(this.lblProductName, 6);
		fd_textProductName.right = new FormAttachment(100, -10);
		this.textProductName.setLayoutData(fd_textProductName);
		
		this.btnAddExpression = new Button(container, SWT.NONE);
		this.btnAddExpression.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO add implementation for button click
				addExpressionRow();
			}
		});
		FormData fd_btnAddExpressions = new FormData();
		fd_btnAddExpressions.right = new FormAttachment(textProductName, 0, SWT.RIGHT);
		fd_btnAddExpressions.top = new FormAttachment(lblProductName, 20);
		fd_btnAddExpressions.left = new FormAttachment(0, 10);
		this.btnAddExpression.setLayoutData(fd_btnAddExpressions);
		this.btnAddExpression.setText("Add Expression");
		
		this.scrollContainerExpressions = new ScrolledComposite(this.container, SWT.BORDER | SWT.V_SCROLL);
		FormData fd_scrollContainer = new FormData(500,500);// temp hack else size of scrolled composite keeps on increasing
		fd_scrollContainer.top = new FormAttachment(this.btnAddExpression);
		fd_scrollContainer.bottom = new FormAttachment(50);
		fd_scrollContainer.right = new FormAttachment(100, -10);
		fd_scrollContainer.left = new FormAttachment(0, 10);
		
		this.scrollContainerExpressions.setExpandHorizontal(true);
		this.scrollContainerExpressions.setExpandVertical(true);
		this.scrollContainerExpressions.setLayoutData(fd_scrollContainer);
		
		this.expressionListContainerComposite = new Composite(this.scrollContainerExpressions, SWT.NONE);
		this.expressionListContainerComposite.setLayout(new FormLayout());
		this.expressionListContainerComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.scrollContainerExpressions.setContent(this.expressionListContainerComposite);
		
		this.btnAddGrade = new Button(container, SWT.NONE);
		this.btnAddGrade.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO add implementation for button click
				//addExpressionRow();
				Grade newGrade = new Grade();
				associatedGrades.add(newGrade);
				addGradeRow(newGrade);
			}
		});
		FormData fd_btnAddGrade = new FormData();
		fd_btnAddGrade.right = new FormAttachment(textProductName, 0, SWT.RIGHT);
		fd_btnAddGrade.top = new FormAttachment(this.scrollContainerExpressions, 20);
		fd_btnAddGrade.left = new FormAttachment(0, 10);
		this.btnAddGrade.setLayoutData(fd_btnAddGrade);
		this.btnAddGrade.setText("Add Grade");
		
		this.scrollContainerGrades = new ScrolledComposite(this.container, SWT.BORDER | SWT.V_SCROLL);
		FormData fd_scrollContainerGrades = new FormData(500,500);// temp hack else size of scrolled composite keeps on increasing
		fd_scrollContainerGrades.top = new FormAttachment(this.btnAddGrade);
		fd_scrollContainerGrades.bottom = new FormAttachment(100, -5);
		fd_scrollContainerGrades.right = new FormAttachment(100, -10);
		fd_scrollContainerGrades.left = new FormAttachment(0, 10);
		
		this.scrollContainerGrades.setExpandHorizontal(true);
		this.scrollContainerGrades.setExpandVertical(true);
		this.scrollContainerGrades.setLayoutData(fd_scrollContainerGrades);
		
		this.gradeListContainerComposite = new Composite(this.scrollContainerGrades, SWT.NONE);
		this.gradeListContainerComposite.setLayout(new FormLayout());
		this.gradeListContainerComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.scrollContainerGrades.setContent(this.gradeListContainerComposite);
		
		Rectangle r1 = this.scrollContainerExpressions.getClientArea();
		this.scrollContainerExpressions.setMinSize(this.scrollContainerExpressions.computeSize(SWT.DEFAULT, r1.height, true));
		
		Rectangle r2 = this.scrollContainerGrades.getClientArea();
		this.scrollContainerGrades.setMinSize(this.scrollContainerGrades.computeSize(SWT.DEFAULT, r2.height, true));
		
		container.getShell().setText("Product Definition");
		this.setDialogLocation();
		return this.container;
	}
	
	private String[] getGradeExpressionNames(){
		List<Expression> gradeExpressions = ProjectConfigutration.getInstance().getGradeExpressions();
		int gradeExpressionSize = gradeExpressions.size();
		String[] gradeExpressionNames = new String[gradeExpressionSize];
		for(int i=0; i<gradeExpressionSize; i++){
			gradeExpressionNames[i] = gradeExpressions.get(i).getName();
		}
		return gradeExpressionNames;
	}
	
	private void addGradeRow(final Grade grade) {
		final Composite compositeGradeRow = new Composite(this.gradeListContainerComposite, SWT.NONE);
		compositeGradeRow.setData(grade);
		compositeGradeRow.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		compositeGradeRow.setLayout(new FormLayout());
		FormData fd_compositeGradeRow = new FormData();
		if(this.presentGrid == null){
			fd_compositeGradeRow.top = new FormAttachment(0, 10);
		}else{
			fd_compositeGradeRow.top = new FormAttachment(this.presentGrid, 10);
		}
		fd_compositeGradeRow.left = new FormAttachment(0, 10);
		fd_compositeGradeRow.right = new FormAttachment(100, -10);
		compositeGradeRow.setLayoutData(fd_compositeGradeRow);
		
		Label lblGradeName = new Label(compositeGradeRow, SWT.NONE);
		lblGradeName.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		lblGradeName.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblGradeName.setText("Name:");
		FormData fd_lblGradeName = new FormData();
		fd_lblGradeName.left = new FormAttachment(0, 10);
		fd_lblGradeName.right = new FormAttachment(10, 10);
		lblGradeName.setLayoutData(fd_lblGradeName);
		
		final Text textGradeName = new Text(compositeGradeRow, SWT.BORDER);
		textGradeName.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		textGradeName.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_textGradeName = new FormData();
		fd_textGradeName.left = new FormAttachment(lblGradeName);
		fd_textGradeName.right = new FormAttachment(50, 10);
		textGradeName.setLayoutData(fd_textGradeName);

		textGradeName.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent event) {
				String name = textGradeName.getText();
				grade.setName(name);
			}
		});
		
		final Combo comboGradeExpressions = new Combo(compositeGradeRow, SWT.NONE);
		comboGradeExpressions.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		comboGradeExpressions.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		comboGradeExpressions.setItems(this.getGradeExpressionNames());
		FormData fd_comboGradeExpressions = new FormData();
		fd_comboGradeExpressions.left = new FormAttachment(textGradeName);
		fd_comboGradeExpressions.right = new FormAttachment(100);
		comboGradeExpressions.setLayoutData(fd_comboGradeExpressions);
		comboGradeExpressions.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String expressionName = comboGradeExpressions.getText();
				Expression expression = ProjectConfigutration.getInstance().getExpressionByName(expressionName);
				grade.setExpression(expression);
			}
		});
		
		this.presentGrid = compositeGradeRow;
		this.gradeListContainerComposite.layout();
		Rectangle r = this.scrollContainerGrades.getClientArea();
		this.scrollContainerGrades.setMinSize(this.scrollContainerGrades.computeSize((r.width - 20), SWT.DEFAULT, true));
	}
	
	private void addExpressionRow() {
		Label lblSelectExpression = new Label(this.expressionListContainerComposite, SWT.NONE);
		lblSelectExpression.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		lblSelectExpression.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_lblSelectExpression = new FormData();
		if(this.presentRow == null){
			fd_lblSelectExpression.top = new FormAttachment(0, 10);
		}else{
			fd_lblSelectExpression.top = new FormAttachment(this.presentRow, 10);
		}
		
		fd_lblSelectExpression.left = new FormAttachment(0, 10);
		lblSelectExpression.setLayoutData(fd_lblSelectExpression);
		lblSelectExpression.setText("Select Expression:");
		
		Combo comboExpression = new Combo(this.expressionListContainerComposite, SWT.NONE);
		comboExpression.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		comboExpression.setItems(availableExpressionNames);
		FormData fd_comboProcess = new FormData();
		fd_comboProcess.top = new FormAttachment(lblSelectExpression, 0, SWT.TOP);
		fd_comboProcess.left = new FormAttachment(lblSelectExpression, 6);
		fd_comboProcess.right = new FormAttachment(100, -10);
		comboExpression.setLayoutData(fd_comboProcess);
		
		this.expressionListContainerComposite.layout();
		this.presentRow = lblSelectExpression;
		this.listOfChildExpressionCombos.add(comboExpression);
		
		Rectangle r = this.scrollContainerExpressions.getClientArea();
		this.scrollContainerExpressions.setMinSize(this.scrollContainerExpressions.computeSize((r.width - 20), SWT.DEFAULT, true));
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Add", true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(735, 487);
	}

	@Override
	protected void okPressed() {
		System.out.println("OK Pressed");
		this.productName = textProductName.getText();
		for(Combo expressionCombo : listOfChildExpressionCombos){
			String expressionName = expressionCombo.getText();
			Expression expression = ProjectConfigutration.getInstance().getExpressionByName(expressionName);
			this.associatedExpressions.add(expression);
		}
		super.okPressed();
	}

	private void setDialogLocation(){
		Rectangle monitorArea = getShell().getDisplay().getPrimaryMonitor().getBounds();
		//Rectangle shellArea = getShell().getBounds();
		int x = monitorArea.x + (monitorArea.width - 980)/2;
		int y = monitorArea.y + (monitorArea.height - 650)/2;
		System.out.println("Process dialog X: "+ x);
		System.out.println("Process dialog Y: "+ y);
		getShell().setLocation(x,y);
	}
	
	
	public String getProductName() {
		return this.productName;
	}
	
	public List<Expression> getAssociatedExpressions() {
		return this.associatedExpressions;
	}
	
	public ArrayList<Grade> getAssociatedGrades() {
		return this.associatedGrades;
	}
	
	
}
