package com.org.gnos.ui.custom.controls;

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

import com.org.gnos.services.ProcessRoute;

public class DumpCreationDialog extends Dialog {

	//private ProcessDefinitionFormScreen processDefinitionFormScreen;
	private ProcessRoute definedProcessRoute;
	private Text textDumpName;
	private String createdDumpName;
	private String associatedPitGroupName;
	private Combo comboPitGroup;
	private String[] pitGroupNames;
	
	private ScrolledComposite scGridContainer;
	private DumpDefinitionGrid dumpDefinitionGrid;
	private Button btnAddDump;
	private Composite container;
	private Label labelScreenName;
	
	public DumpCreationDialog(Shell parentShell, String[] pitGroupNames) {
		super(parentShell);
		this.pitGroupNames = pitGroupNames;
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		this.container = (Composite) super.createDialogArea(parent);
		this.container.setLayout(new FormLayout());
		this.container.setForeground(SWTResourceManager.getColor(0, 191, 255));
		
		labelScreenName = new Label(this.container, SWT.NONE);
		labelScreenName.setForeground(SWTResourceManager.getColor(0, 191, 255));
		labelScreenName.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_labelScreenName = new FormData();
		fd_labelScreenName.top = new FormAttachment(0, 10);
		fd_labelScreenName.left = new FormAttachment(0, 10);
		labelScreenName.setLayoutData(fd_labelScreenName);
		labelScreenName.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		labelScreenName.setText("Grid Definition Dialog");
		
		Label labelScreenDescription = new Label(this.container, SWT.NONE);
		labelScreenDescription.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		labelScreenDescription.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_labelScreenDescription = new FormData();
		fd_labelScreenDescription.top = new FormAttachment(labelScreenName, 10, SWT.BOTTOM);
		fd_labelScreenDescription.left = new FormAttachment(labelScreenName, 0, SWT.LEFT);
		//fd_labelScreenDescription.right = new FormAttachment(0, 866);
		labelScreenDescription.setLayoutData(fd_labelScreenDescription);
		labelScreenDescription.setText("Add/Modify Dumps");
		
		this.initializeGridContainer();
		this.refreshGrid();
		
		this.container.getShell().setText("Dump Details");
		return this.container;
	}
	
	private void initializeGridContainer(){
		this.scGridContainer = new ScrolledComposite(this.container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		FormData fd_scGridContainer = new FormData(500,500);// temp hack else size of scrolled composite keeps on increasing
		fd_scGridContainer.top = new FormAttachment(10);
		fd_scGridContainer.left = new FormAttachment(labelScreenName, 0, SWT.LEFT);
		fd_scGridContainer.bottom = new FormAttachment(100, -10);
		fd_scGridContainer.right = new FormAttachment(100, -35);
		
		this.scGridContainer.setExpandHorizontal(true);
		this.scGridContainer.setExpandVertical(true);
		this.scGridContainer.setLayoutData(fd_scGridContainer);
		
		Rectangle r = this.scGridContainer.getClientArea();
		this.scGridContainer.setMinSize(this.scGridContainer.computeSize(SWT.DEFAULT, r.height, true));
		
		
		this.btnAddDump = new Button(this.container, SWT.NONE);
		this.btnAddDump.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TO DO implement row add
				dumpDefinitionGrid.addRow();
				Rectangle r = dumpDefinitionGrid.getClientArea();
				int gridWidth = r.width;
				
				int scrollableHeight = scGridContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).y;
				Point point = new Point(gridWidth, scrollableHeight);
				scGridContainer.setMinSize(point);
			}
		});
		this.btnAddDump.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
		FormData fd_btnAddRow = new FormData();
		fd_btnAddRow.top = new FormAttachment(scGridContainer, 0, SWT.TOP);
		fd_btnAddRow.right = new FormAttachment(100, -5);
		this.btnAddDump.setLayoutData(fd_btnAddRow);
		this.btnAddDump.setText("+");
		
	}
	
	public void refreshGrid(){
		if(this.dumpDefinitionGrid != null){
			this.dumpDefinitionGrid.dispose();
		}
		this.dumpDefinitionGrid = new DumpDefinitionGrid(scGridContainer, SWT.None);
		this.scGridContainer.setContent(this.dumpDefinitionGrid);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Add", true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected Point getInitialSize() {
		Rectangle monitorArea = getShell().getDisplay().getPrimaryMonitor().getBounds();
		int x = monitorArea.width - 100;
		int y = monitorArea.height -100;
		return new Point(x, y);
	}

	@Override
	protected void okPressed() {
		super.okPressed();
	}

	public ProcessRoute getDefinedProcessRoute(){
		return this.definedProcessRoute;
	}
	
	public String getCreatedDumpName(){
		return this.createdDumpName;
	}
	public String getAssociatedPitGroupName(){
		return this.associatedPitGroupName;
	}
}
