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

import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.services.ProcessRoute;

public class ImportCycleTimeDialog extends Dialog {

	//private ProcessDefinitionFormScreen processDefinitionFormScreen;
	private ProcessRoute definedProcessRoute;
	private String createdDumpName;
	private String associatedPitGroupName;
	
	private ScrolledComposite scGridContainer1;
	private ScrolledComposite scGridContainer2;
	private ScrolledComposite scGridContainer3;
	private ScrolledComposite scGridContainer4;
	
	private CycleTimeFixedFieldGrid cycleTimeFixedFieldGrid;
	private CycleTimeDumpFieldGrid cycleTimeDumpFieldGrid;
	private CycleTimeStockpileFieldGrid cycleTimeStockpileFieldGrid;
	private CycleTimeChildProcessFieldGrid cycleTimeChildProcessFieldGrid;
	//private Button btnAddDump;
	private Composite container;
	private Label labelScreenName;
	
	public ImportCycleTimeDialog(Shell parentShell, String[] pitGroupNames) {
		super(parentShell);
		//this.pitGroupNames = pitGroupNames;
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
		labelScreenName.setText("Cycle Time Data Mapping Dialog");
		
		/*Label labelScreenDescription = new Label(this.container, SWT.NONE);
		labelScreenDescription.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		labelScreenDescription.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_labelScreenDescription = new FormData();
		fd_labelScreenDescription.top = new FormAttachment(labelScreenName, 10, SWT.BOTTOM);
		fd_labelScreenDescription.left = new FormAttachment(labelScreenName, 0, SWT.LEFT);
		//fd_labelScreenDescription.right = new FormAttachment(0, 866);
		labelScreenDescription.setLayoutData(fd_labelScreenDescription);
		labelScreenDescription.setText("Add/Modify Dumps");*/
		
		this.initializeGridContainers();
		this.refreshGrids();
		
		this.container.getShell().setText("Dump Details");
		return this.container;
	}
	
	private void initializeGridContainers(){
		/*
		 * 1
		 */
		this.scGridContainer1 = new ScrolledComposite(this.container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		FormData fd_scGridContainer1 = new FormData(500,500);// temp hack else size of scrolled composite keeps on increasing
		fd_scGridContainer1.top = new FormAttachment(5);
		fd_scGridContainer1.left = new FormAttachment(labelScreenName, 0, SWT.LEFT);
		fd_scGridContainer1.bottom = new FormAttachment(25, -10);
		fd_scGridContainer1.right = new FormAttachment(100, -35);
		
		this.scGridContainer1.setExpandHorizontal(true);
		this.scGridContainer1.setExpandVertical(true);
		this.scGridContainer1.setLayoutData(fd_scGridContainer1);
		
		Rectangle r1 = this.scGridContainer1.getClientArea();
		this.scGridContainer1.setMinSize(this.scGridContainer1.computeSize(SWT.DEFAULT, r1.height, true));
		
		/*
		 * 2
		 */
		this.scGridContainer2 = new ScrolledComposite(this.container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		FormData fd_scGridContainer2 = new FormData(500,500);// temp hack else size of scrolled composite keeps on increasing
		fd_scGridContainer2.top = new FormAttachment(scGridContainer1, 5, SWT.BOTTOM);
		fd_scGridContainer2.left = new FormAttachment(labelScreenName, 0, SWT.LEFT);
		fd_scGridContainer2.bottom = new FormAttachment(50, -10);
		fd_scGridContainer2.right = new FormAttachment(100, -35);
		
		this.scGridContainer2.setExpandHorizontal(true);
		this.scGridContainer2.setExpandVertical(true);
		this.scGridContainer2.setLayoutData(fd_scGridContainer2);
		
		Rectangle r2 = this.scGridContainer1.getClientArea();
		this.scGridContainer1.setMinSize(this.scGridContainer1.computeSize(SWT.DEFAULT, r2.height, true));
		
		/*
		 * 3 
		 */
		this.scGridContainer3 = new ScrolledComposite(this.container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		FormData fd_scGridContainer3 = new FormData(500,500);// temp hack else size of scrolled composite keeps on increasing
		fd_scGridContainer3.top = new FormAttachment(scGridContainer2, 5, SWT.BOTTOM);
		fd_scGridContainer3.left = new FormAttachment(labelScreenName, 0, SWT.LEFT);
		fd_scGridContainer3.bottom = new FormAttachment(75, -10);
		fd_scGridContainer3.right = new FormAttachment(100, -35);
		
		this.scGridContainer3.setExpandHorizontal(true);
		this.scGridContainer3.setExpandVertical(true);
		this.scGridContainer3.setLayoutData(fd_scGridContainer3);
		
		Rectangle r3 = this.scGridContainer3.getClientArea();
		this.scGridContainer3.setMinSize(this.scGridContainer3.computeSize(SWT.DEFAULT, r3.height, true));
		
		/*
		 * 4
		 */
		this.scGridContainer4 = new ScrolledComposite(this.container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		FormData fd_scGridContainer4 = new FormData(500,500);// temp hack else size of scrolled composite keeps on increasing
		fd_scGridContainer4.top = new FormAttachment(scGridContainer3, 5, SWT.BOTTOM);
		fd_scGridContainer4.left = new FormAttachment(labelScreenName, 0, SWT.LEFT);
		fd_scGridContainer4.bottom = new FormAttachment(100, -10);
		fd_scGridContainer4.right = new FormAttachment(100, -35);
		
		this.scGridContainer4.setExpandHorizontal(true);
		this.scGridContainer4.setExpandVertical(true);
		this.scGridContainer4.setLayoutData(fd_scGridContainer4);
		
		Rectangle r4 = this.scGridContainer4.getClientArea();
		this.scGridContainer4.setMinSize(this.scGridContainer4.computeSize(SWT.DEFAULT, r4.height, true));
		
		
		/*this.btnAddDump = new Button(this.container, SWT.NONE);
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
		this.btnAddDump.setText("+");*/
		
	}
	
	public void refreshGrids(){
		if(this.cycleTimeFixedFieldGrid != null){
			this.cycleTimeFixedFieldGrid.dispose();
		}
		this.cycleTimeFixedFieldGrid = new CycleTimeFixedFieldGrid(scGridContainer1, SWT.None);
		this.scGridContainer1.setContent(this.cycleTimeFixedFieldGrid);
		
		if(this.cycleTimeDumpFieldGrid != null){
			this.cycleTimeDumpFieldGrid.dispose();
		}
		this.cycleTimeDumpFieldGrid = new CycleTimeDumpFieldGrid(scGridContainer2, SWT.None);
		this.scGridContainer2.setContent(this.cycleTimeDumpFieldGrid);
		
		if(this.cycleTimeStockpileFieldGrid != null){
			this.cycleTimeStockpileFieldGrid.dispose();
		}
		this.cycleTimeStockpileFieldGrid = new CycleTimeStockpileFieldGrid(scGridContainer3, SWT.None);
		this.scGridContainer3.setContent(this.cycleTimeStockpileFieldGrid);
		
		if(this.cycleTimeChildProcessFieldGrid != null){
			this.cycleTimeChildProcessFieldGrid.dispose();
		}
		this.cycleTimeChildProcessFieldGrid = new CycleTimeChildProcessFieldGrid(scGridContainer4, SWT.None);
		this.scGridContainer4.setContent(this.cycleTimeChildProcessFieldGrid);
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
