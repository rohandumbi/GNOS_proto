package com.org.gnos.ui.custom.controls;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Product;
import com.org.gnos.services.ProcessRoute;

public class ProductPropertiesDialog extends Dialog {

	//private ProcessDefinitionFormScreen processDefinitionFormScreen;
	private ProcessRoute definedProcessRoute;
	private String selectedParent;
	private Text textGroupName;
	private String createdGroupName;
	private Product product;
	
	public ProductPropertiesDialog(Shell parentShell, Product product) {
		super(parentShell);
		this.product = product;
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FormLayout());
		
		Label lblGroupName = new Label(container, SWT.NONE);
		lblGroupName.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblSelectParent = new FormData();
		fd_lblSelectParent.top = new FormAttachment(0, 10);
		fd_lblSelectParent.left = new FormAttachment(0, 10);
		lblGroupName.setLayoutData(fd_lblSelectParent);
		lblGroupName.setText("Product Name:");
		
		this.textGroupName = new Text(container, SWT.BORDER);
		this.textGroupName.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_textGroupName = new FormData();
		fd_textGroupName.top = new FormAttachment(0, 10);
		fd_textGroupName.left = new FormAttachment(lblGroupName, 6);
		fd_textGroupName.right = new FormAttachment(100, -10);
		this.textGroupName.setLayoutData(fd_textGroupName);
		this.textGroupName.setText(this.product.getName());
		this.textGroupName.setEditable(false);
		
		Label lblExpressionList = new Label(container, SWT.NONE);
		lblExpressionList.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblExpressionList = new FormData();
		fd_lblExpressionList.top = new FormAttachment(textGroupName, 10);
		fd_lblExpressionList.left = new FormAttachment(0, 10);
		lblExpressionList.setLayoutData(fd_lblExpressionList);
		lblExpressionList.setText("Contained Expressions:");
		
		List listExpressions = new List(container, SWT.BORDER);
		FormData fd_listExpressions = new FormData();
		fd_listExpressions.bottom = new FormAttachment(100, -10);
		fd_listExpressions.top = new FormAttachment(lblExpressionList, 15);
		fd_listExpressions.right = new FormAttachment(textGroupName, 0, SWT.RIGHT);
		fd_listExpressions.left = new FormAttachment(0, 10);
		listExpressions.setLayoutData(fd_listExpressions);
		
		for(Expression expression : this.product.getListOfExpressions()){
			listExpressions.add(expression.getName());
		}
		
		container.getShell().setText("Product Details");
		//this.processDefinitionFormScreen = new ProcessDefinitionFormScreen(container, SWT.NONE);
		this.setDialogLocation();
		return container;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "OK", true);
		//createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(490, 325);
	}

	@Override
	protected void okPressed() {
		System.out.println("OK Pressed");
		//this.definedProcessRoute = this.processDefinitionFormScreen.getDefinedProcess();
		this.createdGroupName = textGroupName.getText();
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
	
	public ProcessRoute getDefinedProcessRoute(){
		return this.definedProcessRoute;
	}
	
	public String getCreatedGroupName(){
		return this.createdGroupName;
	}
}
