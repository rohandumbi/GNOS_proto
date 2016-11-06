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
import com.org.gnos.ui.custom.controls.GnosScreen;
import com.org.gnos.ui.custom.controls.ProcessConstraintGrid;

public class ProcessConstraintDefinitionScreen extends GnosScreen {

	private Text textScenarioName;
	private ScrolledComposite scGridContainer;
	private ProcessConstraintGrid processConstraintGrid;
	private Label labelScreenName;
	private String scenarioName;
	private Button btnAddConstraintRow;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ProcessConstraintDefinitionScreen(Composite parent, int style) {
		super(parent, style);
		setForeground(SWTResourceManager.getColor(30, 144, 255));
		setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
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
		labelScreenName.setText("Process Constraint Definition");
		
		Label labelScreenDescription = new Label(this, SWT.NONE);
		labelScreenDescription.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		labelScreenDescription.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_labelScreenDescription = new FormData();
		fd_labelScreenDescription.top = new FormAttachment(labelScreenName, 10, SWT.BOTTOM);
		fd_labelScreenDescription.left = new FormAttachment(0, 10);
		//fd_labelScreenDescription.right = new FormAttachment(0, 866);
		labelScreenDescription.setLayoutData(fd_labelScreenDescription);
		labelScreenDescription.setText("Define your process contraints.");
		
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
		
		this.initializeGridContainer();
		this.refreshGrid();
		
	}
	
	private void initializeGridContainer(){
		this.scGridContainer = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		FormData fd_scGridContainer = new FormData(500,500);// temp hack else size of scrolled composite keeps on increasing
		fd_scGridContainer.top = new FormAttachment(textScenarioName, 10, SWT.BOTTOM);
		fd_scGridContainer.left = new FormAttachment(labelScreenName, 0, SWT.LEFT);
		fd_scGridContainer.bottom = new FormAttachment(100, -10);
		//fd_scGridContainer.bottom = new FormAttachment(50);
		fd_scGridContainer.right = new FormAttachment(100, -35);
		
		//processConstraintGrid = new ProcessConstraintGrid(scGridContainer, SWT.None);
		//scGridContainer.setContent(processConstraintGrid);
		
		this.scGridContainer.setExpandHorizontal(true);
		this.scGridContainer.setExpandVertical(true);
		this.scGridContainer.setLayoutData(fd_scGridContainer);
		
		Rectangle r = this.scGridContainer.getClientArea();
		this.scGridContainer.setMinSize(this.scGridContainer.computeSize(SWT.DEFAULT, r.height, true));
		
		
		this.btnAddConstraintRow = new Button(this, SWT.NONE);
		this.btnAddConstraintRow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TO DO implement row add
				processConstraintGrid.addRow();
				Rectangle r = processConstraintGrid.getClientArea();
				int gridWidth = r.width;
				
				int scrollableHeight = scGridContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).y;
				Point point = new Point(gridWidth, scrollableHeight);
				scGridContainer.setMinSize(point);
			}
		});
		this.btnAddConstraintRow.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
		FormData fd_btnAddRow = new FormData();
		fd_btnAddRow.top = new FormAttachment(textScenarioName, 10, SWT.BOTTOM);
		fd_btnAddRow.right = new FormAttachment(100, -5);
		this.btnAddConstraintRow.setLayoutData(fd_btnAddRow);
		this.btnAddConstraintRow.setText("+");
		
	}
	
	public void refreshGrid(){
		this.scenarioName = ScenarioConfigutration.getInstance().getName();
		if(this.scenarioName != null){
			this.textScenarioName.setText(this.scenarioName);
		}
		
		if(this.processConstraintGrid != null){
			this.processConstraintGrid.dispose();
		}
		this.processConstraintGrid = new ProcessConstraintGrid(scGridContainer, SWT.None);
		this.scGridContainer.setContent(this.processConstraintGrid);
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
