package com.org.gnos.ui.custom.controls;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
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

public class ProductDefinitionDialog extends Dialog {

	//private ProcessDefinitionFormScreen processDefinitionFormScreen;
	private String[] availableExpressionNames;
	private Label lblProductName;
	private Composite container;
	private Text textProductName;
	private Button btnAddExpression;
	private ArrayList<Combo> listOfChildExpressionCombos;
	private Control presentRow;
	private String productName;
	private List<Expression> associatedExpressions;
	
	
	public ProductDefinitionDialog(Shell parentShell, String[] availableExpressionNames) {
		super(parentShell);
		this.availableExpressionNames = availableExpressionNames;
		this.listOfChildExpressionCombos = new ArrayList<Combo>();
		this.associatedExpressions = new ArrayList<Expression>();
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		this.container = (Composite) super.createDialogArea(parent);
		this.container.setLayout(new FormLayout());
		
		this.lblProductName = new Label(this.container, SWT.NONE);
		this.lblProductName.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
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
				addProcessDefinitionRow();
			}
		});
		FormData fd_btnAddExpressions = new FormData();
		fd_btnAddExpressions.right = new FormAttachment(textProductName, 0, SWT.RIGHT);
		fd_btnAddExpressions.top = new FormAttachment(lblProductName, 20);
		fd_btnAddExpressions.left = new FormAttachment(0, 10);
		this.btnAddExpression.setLayoutData(fd_btnAddExpressions);
		this.btnAddExpression.setText("Add Expression");
		
		this.presentRow = this.btnAddExpression;
		
		container.getShell().setText("Process Details");
		this.setDialogLocation();
		return this.container;
	}
	
	private void addProcessDefinitionRow() {
		Label lblSelectProcess = new Label(container, SWT.NONE);
		lblSelectProcess.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		FormData fd_lblSelectProcess = new FormData();
		fd_lblSelectProcess.top = new FormAttachment(this.presentRow, 10);
		fd_lblSelectProcess.left = new FormAttachment(0, 10);
		lblSelectProcess.setLayoutData(fd_lblSelectProcess);
		lblSelectProcess.setText("Select Process:");
		
		Combo comboProcess = new Combo(container, SWT.NONE);
		comboProcess.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		comboProcess.setItems(availableExpressionNames);
		FormData fd_comboProcess = new FormData();
		fd_comboProcess.top = new FormAttachment(lblSelectProcess, 0, SWT.TOP);
		fd_comboProcess.left = new FormAttachment(lblSelectProcess, 6);
		fd_comboProcess.right = new FormAttachment(100, -10);
		comboProcess.setLayoutData(fd_comboProcess);
		
		this.container.layout();
		this.presentRow = lblSelectProcess;
		this.listOfChildExpressionCombos.add(comboProcess);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Add", true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(490, 325);
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
	
	
}
