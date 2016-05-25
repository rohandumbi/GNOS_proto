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
import com.org.gnos.db.model.Model;
import com.org.gnos.db.model.ProcessJoin;
import com.org.gnos.services.ProcessRoute;

public class ProcessJoinDefinitionDialog extends Dialog {

	//private ProcessDefinitionFormScreen processDefinitionFormScreen;
	private ProcessRoute definedProcessRoute;
	private List<String> availableProcesses;
	private Label lblProcessJoinName;
	private Composite container;
	private Text textProcessJoinName;
	private Button btnAddProcess;
	private ArrayList<Combo> listOfChildProcessCombos;
	private Control presentRow;
	private String processJoinName;
	private ProcessJoin createdProcessJoin;
	
	
	public ProcessJoinDefinitionDialog(Shell parentShell, List<String> availableProcesses) {
		super(parentShell);
		this.availableProcesses = availableProcesses;
		this.listOfChildProcessCombos = new ArrayList<Combo>();
		// TODO Auto-generated constructor stub
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		this.container = (Composite) super.createDialogArea(parent);
		this.container.setLayout(new FormLayout());
		
		this.lblProcessJoinName = new Label(this.container, SWT.NONE);
		this.lblProcessJoinName.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		FormData fd_lblProcessJoinName = new FormData();
		fd_lblProcessJoinName.top = new FormAttachment(0, 10);
		fd_lblProcessJoinName.left = new FormAttachment(0, 10);
		this.lblProcessJoinName.setLayoutData(fd_lblProcessJoinName);
		this.lblProcessJoinName.setText("Join Name:");
		
		this.textProcessJoinName = new Text(this.container, SWT.BORDER);
		FormData fd_textProcessJoinName = new FormData();
		fd_textProcessJoinName.top = new FormAttachment(0, 10);
		fd_textProcessJoinName.left = new FormAttachment(this.lblProcessJoinName, 6);
		fd_textProcessJoinName.right = new FormAttachment(100, -10);
		this.textProcessJoinName.setLayoutData(fd_textProcessJoinName);
		
		this.btnAddProcess = new Button(container, SWT.NONE);
		this.btnAddProcess.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO add implementation for button click
				addProcessDefinitionRow();
			}
		});
		FormData fd_btnAddProcess = new FormData();
		fd_btnAddProcess.right = new FormAttachment(textProcessJoinName, 0, SWT.RIGHT);
		fd_btnAddProcess.top = new FormAttachment(lblProcessJoinName, 20);
		fd_btnAddProcess.left = new FormAttachment(0, 10);
		this.btnAddProcess.setLayoutData(fd_btnAddProcess);
		this.btnAddProcess.setText("Add Process");
		
		this.presentRow = this.btnAddProcess;
		
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
		String[] comboParentItems = availableProcesses.toArray(new String[availableProcesses.size()]);
		comboProcess.setItems(comboParentItems);
		FormData fd_comboProcess = new FormData();
		fd_comboProcess.top = new FormAttachment(lblSelectProcess, 0, SWT.TOP);
		fd_comboProcess.left = new FormAttachment(lblSelectProcess, 6);
		fd_comboProcess.right = new FormAttachment(100, -10);
		comboProcess.setLayoutData(fd_comboProcess);
		
		this.container.layout();
		this.presentRow = lblSelectProcess;
		this.listOfChildProcessCombos.add(comboProcess);
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
		//this.selectedParent = comboParent.getText();
		this.processJoinName = textProcessJoinName.getText();
		this.createdProcessJoin = new ProcessJoin(processJoinName);
		for(Combo processCombo : listOfChildProcessCombos){
			String modelName = processCombo.getText();
			Model respectiveModel = ProjectConfigutration.getInstance().getModelByName(modelName);
			createdProcessJoin.addProcess(respectiveModel);
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
	
	
	public ProcessJoin getCreatedProcessJoin() {
		return createdProcessJoin;
	}
	
	public String getProcessJoinName() {
		return this.processJoinName;
	}
	
	
}
