package com.org.gnos.ui.custom.controls;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.services.ProcessRoute;

public class ProcessNodeDefinitionDialog extends Dialog {

	//private ProcessDefinitionFormScreen processDefinitionFormScreen;
	private ProcessRoute definedProcessRoute;
	private List<String> availableParents;
	private String selectedParent;
	private Combo comboParent;
	
	public ProcessNodeDefinitionDialog(Shell parentShell, List<String> availableParents) {
		super(parentShell);
		this.availableParents = availableParents;
		// TODO Auto-generated constructor stub
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FormLayout());
		
		Label lblSelectParent = new Label(container, SWT.NONE);
		lblSelectParent.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		FormData fd_lblSelectParent = new FormData();
		fd_lblSelectParent.top = new FormAttachment(0, 10);
		fd_lblSelectParent.left = new FormAttachment(0, 10);
		lblSelectParent.setLayoutData(fd_lblSelectParent);
		lblSelectParent.setText("Select Parent:");
		
		comboParent = new Combo(container, SWT.NONE);
		comboParent.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		String[] comboParentItems = availableParents.toArray(new String[availableParents.size()]);
		comboParent.setItems(comboParentItems);
		FormData fd_comboParent = new FormData();
		fd_comboParent.top = new FormAttachment(0, 10);
		fd_comboParent.left = new FormAttachment(lblSelectParent, 6);
		fd_comboParent.right = new FormAttachment(100, -10);
		comboParent.setLayoutData(fd_comboParent);
		container.getShell().setText("Process Details");
		//this.processDefinitionFormScreen = new ProcessDefinitionFormScreen(container, SWT.NONE);
		this.setDialogLocation();
		return container;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Add", true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/*@Override
	protected Point getInitialSize() {
		return new Point(980, 650);
	}*/

	@Override
	protected void okPressed() {
		System.out.println("OK Pressed");
		//this.definedProcessRoute = this.processDefinitionFormScreen.getDefinedProcess();
		this.selectedParent = comboParent.getText();
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
	
	public String getParentName(){
		return this.selectedParent;
	}


}
