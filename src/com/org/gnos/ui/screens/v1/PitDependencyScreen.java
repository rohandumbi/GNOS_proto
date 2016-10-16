package com.org.gnos.ui.screens.v1;

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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.core.ScenarioConfigutration;
import com.org.gnos.events.GnosEvent;
import com.org.gnos.ui.custom.controls.DumpDependencyGrid;
import com.org.gnos.ui.custom.controls.GnosScreen;
import com.org.gnos.ui.custom.controls.PitDependencyGrid;

public class PitDependencyScreen extends GnosScreen {

	private Text textScenarioName;
	private ScrolledComposite scGridContainer1;
	private ScrolledComposite scGridContainer2;
	private PitDependencyGrid pitDependencyGrid;
	private DumpDependencyGrid dumpDependencyGrid;
	private Label labelScreenName;
	private String scenarioName;
	private Button btnAddConstraintRow;
	private Button btnAddDumpRow;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PitDependencyScreen(Composite parent, int style) {
		super(parent, style);
		setForeground(SWTResourceManager.getColor(30, 144, 255));
		setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		//this.timePeriod = ScenarioConfigutration.getInstance().getTimePeriod();
		//this.startYear = ScenarioConfigutration.getInstance().getStartYear();
		this.scenarioName = ScenarioConfigutration.getInstance().getName();
		this.createContent();

	}

	private void createContent(){
		setLayout(new FormLayout());
		labelScreenName = new Label(this, SWT.NONE);
		labelScreenName.setForeground(SWTResourceManager.getColor(0, 191, 255));
		labelScreenName.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_labelScreenName = new FormData();
		fd_labelScreenName.top = new FormAttachment(0, 10);
		fd_labelScreenName.left = new FormAttachment(0, 10);
		labelScreenName.setLayoutData(fd_labelScreenName);
		labelScreenName.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		labelScreenName.setText("Pit and Dump Dependency Definition");
		
		Label labelScreenDescription = new Label(this, SWT.NONE);
		labelScreenDescription.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		labelScreenDescription.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_labelScreenDescription = new FormData();
		fd_labelScreenDescription.top = new FormAttachment(labelScreenName, 10, SWT.BOTTOM);
		fd_labelScreenDescription.left = new FormAttachment(0, 10);
		//fd_labelScreenDescription.right = new FormAttachment(0, 866);
		labelScreenDescription.setLayoutData(fd_labelScreenDescription);
		labelScreenDescription.setText("Define your pit and dump dependencies");
		
		Label lblScenarioName = new Label(this, SWT.NONE);
		lblScenarioName.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_lblScenarioName = new FormData();
		fd_lblScenarioName.top = new FormAttachment(labelScreenDescription, 10);
		fd_lblScenarioName.left = new FormAttachment(labelScreenDescription, 0, SWT.LEFT);
		lblScenarioName.setLayoutData(fd_lblScenarioName);
		lblScenarioName.setText("Scenario In Use:");
		
		textScenarioName = new Text(this, SWT.BORDER);
		textScenarioName.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_textStartYear = new FormData();
		fd_textStartYear.top = new FormAttachment(lblScenarioName, -2, SWT.TOP);
		fd_textStartYear.left = new FormAttachment(lblScenarioName, 8);
		fd_textStartYear.right = new FormAttachment(lblScenarioName, 88, SWT.RIGHT);
		textScenarioName.setLayoutData(fd_textStartYear);
		textScenarioName.setEditable(false);
		if(this.scenarioName != null){
			textScenarioName.setText(this.scenarioName);
		}
		
		this.initializeGridContainers();
		this.refreshGrids();
		
	}
	
	private void initializeGridContainers(){
		/*
		 * 1 
		 */
		this.scGridContainer1 = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		FormData fd_scGridContainer1 = new FormData(500,500);// temp hack else size of scrolled composite keeps on increasing
		fd_scGridContainer1.top = new FormAttachment(textScenarioName, 10, SWT.BOTTOM);
		fd_scGridContainer1.left = new FormAttachment(labelScreenName, 0, SWT.LEFT);
		fd_scGridContainer1.bottom = new FormAttachment(40, -10);
		fd_scGridContainer1.right = new FormAttachment(100, -35);
		
		this.scGridContainer1.setExpandHorizontal(true);
		this.scGridContainer1.setExpandVertical(true);
		this.scGridContainer1.setLayoutData(fd_scGridContainer1);
		
		Rectangle r = this.scGridContainer1.getClientArea();
		this.scGridContainer1.setMinSize(this.scGridContainer1.computeSize(SWT.DEFAULT, r.height, true));
		
		
		this.btnAddConstraintRow = new Button(this, SWT.NONE);
		this.btnAddConstraintRow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TO DO implement row add
				pitDependencyGrid.addRow();
				Rectangle r = pitDependencyGrid.getClientArea();
				int gridWidth = r.width;
				
				int scrollableHeight = scGridContainer1.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).y;
				Point point = new Point(gridWidth, scrollableHeight);
				scGridContainer1.setMinSize(point);
			}
		});
		this.btnAddConstraintRow.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
		FormData fd_btnAddRow = new FormData();
		fd_btnAddRow.top = new FormAttachment(textScenarioName, 10, SWT.BOTTOM);
		fd_btnAddRow.right = new FormAttachment(100, -5);
		this.btnAddConstraintRow.setLayoutData(fd_btnAddRow);
		this.btnAddConstraintRow.setText("+");
		
		/*
		 * 2
		 */
		this.scGridContainer2 = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		FormData fd_scGridContainer2 = new FormData(500,500);// temp hack else size of scrolled composite keeps on increasing
		fd_scGridContainer2.top = new FormAttachment(scGridContainer1, 10, SWT.BOTTOM);
		fd_scGridContainer2.left = new FormAttachment(scGridContainer1, 0, SWT.LEFT);
		fd_scGridContainer2.bottom = new FormAttachment(80, -10);
		fd_scGridContainer2.right = new FormAttachment(100, -35);
		
		this.scGridContainer2.setExpandHorizontal(true);
		this.scGridContainer2.setExpandVertical(true);
		this.scGridContainer2.setLayoutData(fd_scGridContainer2);
		
		Rectangle r1 = this.scGridContainer2.getClientArea();
		this.scGridContainer2.setMinSize(this.scGridContainer1.computeSize(SWT.DEFAULT, r1.height, true));
		
		
		this.btnAddDumpRow = new Button(this, SWT.NONE);
		this.btnAddDumpRow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TO DO implement row add
				dumpDependencyGrid.addRow();
				Rectangle r = dumpDependencyGrid.getClientArea();
				int gridWidth = r.width;
				
				int scrollableHeight = scGridContainer2.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).y;
				Point point = new Point(gridWidth, scrollableHeight);
				scGridContainer2.setMinSize(point);
			}
		});
		this.btnAddDumpRow.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
		FormData fd_btnAddDumpRow = new FormData();
		fd_btnAddDumpRow.top = new FormAttachment(scGridContainer2, 0, SWT.TOP);
		fd_btnAddDumpRow.right = new FormAttachment(100, -5);
		this.btnAddDumpRow.setLayoutData(fd_btnAddDumpRow);
		this.btnAddDumpRow.setText("+");
		
	}
	
	public void refreshGrids(){
		this.scenarioName = ScenarioConfigutration.getInstance().getName();
		if(this.scenarioName != null){
			this.textScenarioName.setText(this.scenarioName);
		}
		
		if(this.pitDependencyGrid != null){
			this.pitDependencyGrid.dispose();
		}
		this.pitDependencyGrid = new PitDependencyGrid(scGridContainer1, SWT.None);
		this.scGridContainer1.setContent(this.pitDependencyGrid);
		
		if(this.dumpDependencyGrid != null){
			this.dumpDependencyGrid.dispose();
		}
		this.dumpDependencyGrid = new DumpDependencyGrid(scGridContainer2, SWT.None);
		this.scGridContainer2.setContent(this.dumpDependencyGrid);
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void onGnosEventFired(GnosEvent e) {
		// TODO Auto-generated method stub
		
	}

}
