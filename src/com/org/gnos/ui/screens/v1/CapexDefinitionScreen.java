package com.org.gnos.ui.screens.v1;

import java.util.ArrayList;

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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.core.ScenarioConfigutration;
import com.org.gnos.db.model.CapexData;
import com.org.gnos.events.GnosEvent;
import com.org.gnos.ui.custom.controls.CapexDefinitionGrid;
import com.org.gnos.ui.custom.controls.GnosScreen;
import com.org.gnos.ui.custom.controls.PitDependencyGrid;

public class CapexDefinitionScreen extends GnosScreen {

	private Text textScenarioName;
	private ScrolledComposite scGridContainer;
	private PitDependencyGrid pitDependencyGrid;
	private Label labelScreenName;
	private String scenarioName;
	private Button btnAddConstraintRow;
	private Control referenceToAddNewGrid;
	private ArrayList<CapexData> capexDataList;
	private ArrayList<ScrolledComposite> allCapexGrids;
	private ArrayList<Button> allAddRowButtons;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CapexDefinitionScreen(Composite parent, int style) {
		super(parent, style);
		setForeground(SWTResourceManager.getColor(30, 144, 255));
		setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.allCapexGrids = new ArrayList<ScrolledComposite>();
		this.allAddRowButtons = new ArrayList<Button>();
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
		labelScreenName.setText("Capex Definition");
		
		Label labelScreenDescription = new Label(this, SWT.NONE);
		labelScreenDescription.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		labelScreenDescription.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_labelScreenDescription = new FormData();
		fd_labelScreenDescription.top = new FormAttachment(labelScreenName, 10, SWT.BOTTOM);
		fd_labelScreenDescription.left = new FormAttachment(0, 10);
		//fd_labelScreenDescription.right = new FormAttachment(0, 866);
		labelScreenDescription.setLayoutData(fd_labelScreenDescription);
		labelScreenDescription.setText("Define your Capital Expenditures.");
		
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
		
		Button btnAddCapex = new Button(this, SWT.NONE);
		btnAddCapex.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(scenarioName == null){
					return;
				}else{
					addCapexGrid();
				}
			}
		});
		FormData fd_btnAddCapex = new FormData();
		fd_btnAddCapex.right = new FormAttachment(textScenarioName, 103, SWT.RIGHT);
		fd_btnAddCapex.bottom = new FormAttachment(textScenarioName, 0, SWT.BOTTOM);
		fd_btnAddCapex.left = new FormAttachment(textScenarioName, 20);
		btnAddCapex.setLayoutData(fd_btnAddCapex);
		btnAddCapex.setText("Add Capex");
		if(this.scenarioName != null){
			textScenarioName.setText(this.scenarioName);
		}
		referenceToAddNewGrid = textScenarioName;
		
		//this.initializeGridContainer();
		//this.refreshGrid();
		
	}
	
	private void addCapexGrid(){
		CapexData capexData = new CapexData();
		this.capexDataList.add(capexData);
		this.addCapexGrid(capexData);
	}
	
	private void addCapexGrid(CapexData capexData){
		final ScrolledComposite scGridContainer = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		FormData fd_scGridContainer = new FormData(500,500);// temp hack else size of scrolled composite keeps on increasing
		fd_scGridContainer.top = new FormAttachment(referenceToAddNewGrid, 10, SWT.BOTTOM);
		fd_scGridContainer.left = new FormAttachment(labelScreenName, 0, SWT.LEFT);
		fd_scGridContainer.bottom = new FormAttachment(referenceToAddNewGrid, 200, SWT.BOTTOM);
		fd_scGridContainer.right = new FormAttachment(100, -35);
		
		scGridContainer.setExpandHorizontal(true);
		scGridContainer.setExpandVertical(true);
		scGridContainer.setLayoutData(fd_scGridContainer);
		Rectangle r = scGridContainer.getClientArea();
		scGridContainer.setMinSize(scGridContainer.computeSize(SWT.DEFAULT, r.height, true));
		
		final CapexDefinitionGrid capexGrid = new CapexDefinitionGrid(scGridContainer, SWT.NONE, capexData);
		scGridContainer.setContent(capexGrid);
		
		Button btnAddCapexInstance = new Button(this, SWT.NONE);
		this.allAddRowButtons.add(btnAddCapexInstance);
		btnAddCapexInstance.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				capexGrid.addRow();
				Rectangle r = capexGrid.getClientArea();
				int gridWidth = r.width;
				int scrollableHeight = scGridContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).y;
				Point point = new Point(gridWidth, scrollableHeight);
				scGridContainer.setMinSize(point);
			}
		});
		btnAddCapexInstance.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
		FormData fd_btnAddCapexInstance = new FormData();
		fd_btnAddCapexInstance.top = new FormAttachment(referenceToAddNewGrid, 10, SWT.BOTTOM);
		fd_btnAddCapexInstance.right = new FormAttachment(100, -5);
		btnAddCapexInstance.setLayoutData(fd_btnAddCapexInstance);
		btnAddCapexInstance.setText("+");
		this.allCapexGrids.add(scGridContainer);
		referenceToAddNewGrid = scGridContainer;
		this.layout();
	}
	
	public void refreshGrid(){
		this.scenarioName = ScenarioConfigutration.getInstance().getName();
		this.capexDataList = ScenarioConfigutration.getInstance().getCapexDataList();
		if(this.scenarioName != null){
			this.textScenarioName.setText(this.scenarioName);
		}
		for(ScrolledComposite capexGrid: this.allCapexGrids){
			capexGrid.dispose();
			for(Button button: this.allAddRowButtons){
				button.dispose();
			}
			referenceToAddNewGrid = textScenarioName;
		}
		for(CapexData capexData: this.capexDataList){
			this.addCapexGrid(capexData);
		}
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
