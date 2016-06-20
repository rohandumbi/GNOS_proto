package com.org.gnos.ui.custom.controls;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
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
	private ScrolledComposite scrollContainer;
	private Composite expressionListContainerComposite;
	
	
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
		
		this.scrollContainer = new ScrolledComposite(this.container, SWT.BORDER | SWT.V_SCROLL);
		FormData fd_scrollContainer = new FormData(500,500);// temp hack else size of scrolled composite keeps on increasing
		fd_scrollContainer.top = new FormAttachment(this.btnAddExpression);
		fd_scrollContainer.bottom = new FormAttachment(100, -5);
		fd_scrollContainer.right = new FormAttachment(100, -10);
		fd_scrollContainer.left = new FormAttachment(0, 10);
		
		this.scrollContainer.setExpandHorizontal(true);
		this.scrollContainer.setExpandVertical(true);
		this.scrollContainer.setLayoutData(fd_scrollContainer);
		
		this.expressionListContainerComposite = new Composite(this.scrollContainer, SWT.NONE);
		this.expressionListContainerComposite.setLayout(new FormLayout());
		this.expressionListContainerComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.scrollContainer.setContent(this.expressionListContainerComposite);
		
		Rectangle r = this.scrollContainer.getClientArea();
		this.scrollContainer.setMinSize(this.scrollContainer.computeSize(SWT.DEFAULT, r.height, true));
		
		container.getShell().setText("Product Definition");
		this.setDialogLocation();
		return this.container;
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
		
		Rectangle r = this.scrollContainer.getClientArea();
		this.scrollContainer.setMinSize(this.scrollContainer.computeSize((r.width - 20), SWT.DEFAULT, true));
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
	
	
}
