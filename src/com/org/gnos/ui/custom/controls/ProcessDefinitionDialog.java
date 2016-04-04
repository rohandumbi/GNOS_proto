package com.org.gnos.ui.custom.controls;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.org.gnos.ui.screens.v1.ProcessDefinitionFormScreen;

public class ProcessDefinitionDialog extends Dialog {


	public ProcessDefinitionDialog(Shell parentShell) {
		super(parentShell);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.getShell().setText("Process Definition Dialog");
		container.setLayout(new FillLayout());
		ProcessDefinitionFormScreen processDefinitionFormScreen = new ProcessDefinitionFormScreen(container, SWT.NONE);
		this.setDialogLocation();
		return container;
	}

	// override method to use "Login" as label for the OK button
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Create Process", true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(980, 650);
	}

	@Override
	protected void okPressed() {
		System.out.println("OK Pressed");
		super.okPressed();
	}

	/*@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Process Definition Dialog");
	}*/

	private void setDialogLocation(){
		Rectangle monitorArea = getShell().getDisplay().getPrimaryMonitor().getBounds();
		//Rectangle shellArea = getShell().getBounds();
		int x = monitorArea.x + (monitorArea.width - 980)/2;
		int y = monitorArea.y + (monitorArea.height - 650)/2;
		System.out.println("Process dialog X: "+ x);
		System.out.println("Process dialog Y: "+ y);
		getShell().setLocation(x,y);
	}


}
