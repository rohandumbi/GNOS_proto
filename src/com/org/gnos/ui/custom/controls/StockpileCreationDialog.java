package com.org.gnos.ui.custom.controls;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.services.ProcessRoute;

public class StockpileCreationDialog extends Dialog {

	//private ProcessDefinitionFormScreen processDefinitionFormScreen;
	private ProcessRoute definedProcessRoute;
	private Text textStockpileName;
	private String createdStockpileName;
	private String associatedPitGroupName;
	private Combo comboPitGroup;
	private String[] pitGroupNames;
	
	public StockpileCreationDialog(Shell parentShell, String[] pitGroupNames) {
		super(parentShell);
		this.pitGroupNames = pitGroupNames;
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FormLayout());
		
		Label lblStockpileName = new Label(container, SWT.NONE);
		lblStockpileName.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		FormData fd_lblStockpileName = new FormData();
		fd_lblStockpileName.top = new FormAttachment(0, 10);
		fd_lblStockpileName.left = new FormAttachment(0, 10);
		lblStockpileName.setLayoutData(fd_lblStockpileName);
		lblStockpileName.setText("Stockpile Name:");
		
		this.textStockpileName = new Text(container, SWT.BORDER);
		this.textStockpileName.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		FormData fd_textStockpileName = new FormData();
		fd_textStockpileName.top = new FormAttachment(0, 10);
		fd_textStockpileName.left = new FormAttachment(lblStockpileName, 6);
		fd_textStockpileName.right = new FormAttachment(100, -10);
		this.textStockpileName.setLayoutData(fd_textStockpileName);
		
		Label lblPitGroupName = new Label(container, SWT.NONE);
		lblPitGroupName.setText("PitGroup Name:");
		lblPitGroupName.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		FormData fd_lblPitGroupName = new FormData();
		fd_lblPitGroupName.top = new FormAttachment(lblStockpileName, 12);
		fd_lblPitGroupName.left = new FormAttachment(lblStockpileName, 0, SWT.LEFT);
		lblPitGroupName.setLayoutData(fd_lblPitGroupName);
		
		this.comboPitGroup = new Combo(container, SWT.NONE);
		FormData fd_comboPitGroup = new FormData();
		fd_comboPitGroup.left = new FormAttachment(textStockpileName, 0, SWT.LEFT);
		fd_comboPitGroup.top = new FormAttachment(textStockpileName, 12);
		fd_comboPitGroup.right = new FormAttachment(100, -10);
		this.comboPitGroup.setLayoutData(fd_comboPitGroup);
		this.comboPitGroup.setItems(pitGroupNames);
		
		
		
		container.getShell().setText("Stockpile Details");
		//this.processDefinitionFormScreen = new ProcessDefinitionFormScreen(container, SWT.NONE);
		this.setDialogLocation();
		return container;
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
		//this.definedProcessRoute = this.processDefinitionFormScreen.getDefinedProcess();
		this.createdStockpileName = textStockpileName.getText();
		this.associatedPitGroupName = comboPitGroup.getText();
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
	
	public String getCreatedStockpilepName(){
		return this.createdStockpileName;
	}
	public String getAssociatedPitGroupName(){
		return this.associatedPitGroupName;
	}
}
