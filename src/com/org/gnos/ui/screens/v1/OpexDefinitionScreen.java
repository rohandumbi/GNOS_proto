package com.org.gnos.ui.screens.v1;


import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.core.ScenarioConfigutration;
import com.org.gnos.db.dao.ScenarioDAO;
import com.org.gnos.db.model.FixedOpexCost;
import com.org.gnos.db.model.Scenario;
import com.org.gnos.events.GnosEvent;
import com.org.gnos.ui.custom.controls.GnosScreen;
import com.org.gnos.ui.custom.controls.MiningStockpileCostGrid;
import com.org.gnos.ui.custom.controls.OpexDefinitionGrid;

public class OpexDefinitionScreen extends GnosScreen {

	private Text textStartYear;
	private Text textDiscountFactor;
	private Text textNumberOfIncrements;
	private Text textScenarioName;
	private ScrolledComposite scOpexGridContainer;
	private ScrolledComposite scFixedCostGridContainer;
	private OpexDefinitionGrid opexGrid;
	private MiningStockpileCostGrid fixedCostGrid;
	private Button btnAddOpexRow;
	private Button btnSaveOpex;
	private Button saveFixedCost;
	private Label labelScreenName;
	private List<Scenario> scenarioList;
	private ScenarioDAO scenarioDAO;
	private Text textScenarioInUse;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public OpexDefinitionScreen(Composite parent, int style) {
		super(parent, style);
		setForeground(SWTResourceManager.getColor(30, 144, 255));
		setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.scenarioDAO = new ScenarioDAO();
		this.scenarioList = new ArrayList<Scenario>();

		/*
		 * If there is an existing Opex data for the project
		 */
		this.createContent();

	}
	
	private String[] getExistingScenarioList() {
		this.scenarioList = this.scenarioDAO.getAll();
		String[] exisitngScenarioNames = new String[this.scenarioList.size()];
		for(int i=0; i<this.scenarioList.size(); i++){
			exisitngScenarioNames[i] = this.scenarioList.get(i).getName();
		}
		return exisitngScenarioNames;
	}

	private void createContent(){
		setLayout(new FormLayout());
		labelScreenName = new Label(this, SWT.NONE);
		labelScreenName.setForeground(SWTResourceManager.getColor(0, 191, 255));
		labelScreenName.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_labelScreenName = new FormData();
		//fd_labelScreenName.bottom = new FormAttachment(100, -461);
		fd_labelScreenName.top = new FormAttachment(0, 10);
		fd_labelScreenName.left = new FormAttachment(0, 10);
		labelScreenName.setLayoutData(fd_labelScreenName);
		labelScreenName.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		labelScreenName.setText("OPEX Definition");
		
		Label labelScreenDescription = new Label(this, SWT.NONE);
		labelScreenDescription.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		labelScreenDescription.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_labelScreenDescription = new FormData();
		fd_labelScreenDescription.top = new FormAttachment(labelScreenName, 10, SWT.BOTTOM);
		fd_labelScreenDescription.left = new FormAttachment(0, 10);
		//fd_labelScreenDescription.right = new FormAttachment(0, 866);
		labelScreenDescription.setLayoutData(fd_labelScreenDescription);
		labelScreenDescription.setText("Select an existing scenario or define a new one.");

		Label lblSelectExistingScenario = new Label(this, SWT.NONE);
		lblSelectExistingScenario.setForeground(SWTResourceManager.getColor(0, 191, 255));
		lblSelectExistingScenario.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_lblSelectExistingScenario = new FormData();
		fd_lblSelectExistingScenario.top = new FormAttachment(labelScreenDescription, 10);
		fd_lblSelectExistingScenario.left = new FormAttachment(labelScreenDescription, 0, SWT.LEFT);
		lblSelectExistingScenario.setLayoutData(fd_lblSelectExistingScenario);
		lblSelectExistingScenario.setText("EXISTING SCENARIOS:");

		final Combo comboExistingScenarios = new Combo(this, SWT.NONE);
		FormData fd_comboExistingScenarios = new FormData();
		fd_comboExistingScenarios.left = new FormAttachment(lblSelectExistingScenario, 8, SWT.RIGHT);
		fd_comboExistingScenarios.top = new FormAttachment(lblSelectExistingScenario, -2, SWT.TOP);
		fd_comboExistingScenarios.right = new FormAttachment(lblSelectExistingScenario, 88, SWT.RIGHT);
		comboExistingScenarios.setLayoutData(fd_comboExistingScenarios);
		comboExistingScenarios.setItems(getExistingScenarioList());
		comboExistingScenarios.addListener(SWT.MouseDown, new Listener(){
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				comboExistingScenarios.removeAll();
				comboExistingScenarios.setItems(getExistingScenarioList());
				comboExistingScenarios.getParent().layout();
				comboExistingScenarios.setListVisible(true);
			}
		});
		comboExistingScenarios.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleScenarioSelection(comboExistingScenarios.getSelectionIndex());
			}
		});

		Label lblCreateNew = new Label(this, SWT.NONE);
		lblCreateNew.setForeground(SWTResourceManager.getColor(0, 191, 255));
		lblCreateNew.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_lblCreateNew = new FormData();
		fd_lblCreateNew.top = new FormAttachment(comboExistingScenarios, 10);
		fd_lblCreateNew.left = new FormAttachment(lblSelectExistingScenario, 0, SWT.LEFT);
		lblCreateNew.setLayoutData(fd_lblCreateNew);
		lblCreateNew.setText("CREATE NEW");
		
		Label lblScenarioName = new Label(this, SWT.NONE);
		lblScenarioName.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_lblScenarioName = new FormData();
		fd_lblScenarioName.top = new FormAttachment(comboExistingScenarios, 10);
		fd_lblScenarioName.left = new FormAttachment(lblCreateNew, 8, SWT.RIGHT);
		lblScenarioName.setLayoutData(fd_lblScenarioName);
		lblScenarioName.setText("Name:");

		textScenarioName = new Text(this, SWT.BORDER);
		FormData fd_textScenarioName = new FormData();
		fd_textScenarioName.top = new FormAttachment(lblScenarioName, -2, SWT.TOP);
		fd_textScenarioName.left = new FormAttachment(lblScenarioName, 8);
		fd_textScenarioName.right = new FormAttachment(lblScenarioName, 88, SWT.RIGHT);
		textScenarioName.setLayoutData(fd_textScenarioName);
		
		Label lblStartYear = new Label(this, SWT.NONE);
		lblStartYear.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_lblStartYear = new FormData();
		fd_lblStartYear.top = new FormAttachment(comboExistingScenarios, 10);
		fd_lblStartYear.left = new FormAttachment(textScenarioName, 8, SWT.RIGHT);
		lblStartYear.setLayoutData(fd_lblStartYear);
		lblStartYear.setText("Start Year:");
		
		textStartYear = new Text(this, SWT.BORDER);
		FormData fd_textStartYear = new FormData();
		fd_textStartYear.top = new FormAttachment(lblStartYear, -2, SWT.TOP);
		fd_textStartYear.left = new FormAttachment(lblStartYear, 8);
		fd_textStartYear.right = new FormAttachment(lblStartYear, 58, SWT.RIGHT);
		textStartYear.setLayoutData(fd_textStartYear);
		
		Label lblDiscountFactor = new Label(this, SWT.NONE);
		lblDiscountFactor.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_lblDiscountFactor = new FormData();
		fd_lblDiscountFactor.top = new FormAttachment(lblStartYear, 0, SWT.TOP);
		fd_lblDiscountFactor.left = new FormAttachment(textStartYear, 10);
		lblDiscountFactor.setLayoutData(fd_lblDiscountFactor);
		lblDiscountFactor.setText("Discount Factor:");
		
		textDiscountFactor = new Text(this, SWT.BORDER);
		textDiscountFactor.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_textDiscountFactor = new FormData();
		fd_textDiscountFactor.top = new FormAttachment(textStartYear, -2, SWT.TOP);
		fd_textDiscountFactor.left = new FormAttachment(lblDiscountFactor, 8);
		fd_textDiscountFactor.right = new FormAttachment(lblDiscountFactor, 58, SWT.RIGHT);
		textDiscountFactor.setLayoutData(fd_textDiscountFactor);
		
		Label lblNumberOfIncrements = new Label(this, SWT.NONE);
		lblNumberOfIncrements.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_lblNumberOfIncrements = new FormData();
		fd_lblNumberOfIncrements.top = new FormAttachment(lblStartYear, 0, SWT.TOP);
		fd_lblNumberOfIncrements.left = new FormAttachment(textDiscountFactor, 10);
		lblNumberOfIncrements.setLayoutData(fd_lblNumberOfIncrements);
		lblNumberOfIncrements.setText("Number of Increments:");
		
		textNumberOfIncrements = new Text(this, SWT.BORDER);
		textNumberOfIncrements.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_textNumberOfIncrements = new FormData();
		fd_textNumberOfIncrements.top = new FormAttachment(textStartYear, -2, SWT.TOP);
		fd_textNumberOfIncrements.left = new FormAttachment(lblNumberOfIncrements, 8);
		fd_textNumberOfIncrements.right = new FormAttachment(lblNumberOfIncrements, 58, SWT.RIGHT);
		textNumberOfIncrements.setLayoutData(fd_textNumberOfIncrements);
		
		Button btnAddTimePeriod = new Button(this, SWT.NONE);
		btnAddTimePeriod.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String scenarioName = textScenarioName.getText();
				int startingYear = Integer.parseInt(textStartYear.getText());
				int numberOfIncrements = Integer.parseInt(textNumberOfIncrements.getText());
				String discountFactorValue = textDiscountFactor.getText();
				float discountFactor;
				if((discountFactorValue.equals("")) || (discountFactorValue == null)){
					discountFactor = 1;
				}else{
					discountFactor = Float.parseFloat(discountFactorValue);
				}
				
				Scenario scenario = new Scenario();
				scenario.setName(scenarioName);
				scenario.setDiscount(discountFactor);
				scenario.setStartYear(startingYear);
				scenario.setTimePeriod(numberOfIncrements);
				
				boolean isScenarioCreationSuccessful = scenarioDAO.create(scenario);
				if(isScenarioCreationSuccessful){
					ScenarioConfigutration.getInstance().load(scenario.getId());
					textScenarioInUse.setText(scenario.getName());
					refreshOpexGrid(scenario);
					refreshFixedCostGrid(scenario);
					GnosEvent event = new GnosEvent(this, "selected:new-scenario");
					triggerGnosEvent(event);//this event needs to be thrown as new scenario is being loaded
				}
			}
		});
		FormData fd_btnAddTimePeriod = new FormData();
		fd_btnAddTimePeriod.top = new FormAttachment(textNumberOfIncrements, 0, SWT.TOP);
		fd_btnAddTimePeriod.left = new FormAttachment(textNumberOfIncrements, 6);
		btnAddTimePeriod.setLayoutData(fd_btnAddTimePeriod);
		fd_btnAddTimePeriod.bottom = new FormAttachment(textNumberOfIncrements, 0, SWT.BOTTOM);
		btnAddTimePeriod.setText("Create");
		
		Label lblScenarioInUse = new Label(this, SWT.NONE);
		lblScenarioInUse.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_lblScenarioInUse = new FormData();
		fd_lblScenarioInUse.top = new FormAttachment(lblStartYear, 0, SWT.TOP);
		fd_lblScenarioInUse.left = new FormAttachment(btnAddTimePeriod, 10);
		lblScenarioInUse.setLayoutData(fd_lblScenarioInUse);
		lblScenarioInUse.setText("Scenario In Use:");
		
		textScenarioInUse = new Text(this, SWT.BORDER);
		textScenarioInUse.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_textScenarioInUse = new FormData();
		fd_textScenarioInUse.top = new FormAttachment(lblScenarioInUse, -2, SWT.TOP);
		fd_textScenarioInUse.left = new FormAttachment(lblScenarioInUse, 8);
		fd_textScenarioInUse.right = new FormAttachment(lblScenarioInUse, 88, SWT.RIGHT);
		textScenarioInUse.setLayoutData(fd_textScenarioInUse);
		textScenarioInUse.setEditable(false);
		
		this.initializeOpexGridContainer();
		this.initializeFixedCostGridContainer();
		
		
	}
	
	private void initializeFixedCostGridContainer(){
		this.scFixedCostGridContainer = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		FormData fd_scFixedCostGridContainer = new FormData(500,500);// temp hack else size of scrolled composite keeps on increasing
		fd_scFixedCostGridContainer.top = new FormAttachment(this.scOpexGridContainer, 10, SWT.BOTTOM);
		fd_scFixedCostGridContainer.left = new FormAttachment(labelScreenName, 0, SWT.LEFT);
		fd_scFixedCostGridContainer.bottom = new FormAttachment(this.scOpexGridContainer, 300, SWT.BOTTOM);
		//fd_scFixedCostGridContainer.bottom = new FormAttachment(70);
		fd_scFixedCostGridContainer.right = new FormAttachment(100, -35);
		
		this.scFixedCostGridContainer.setExpandHorizontal(true);
		this.scFixedCostGridContainer.setExpandVertical(true);
		this.scFixedCostGridContainer.setLayoutData(fd_scFixedCostGridContainer);
		
		Rectangle r = this.scFixedCostGridContainer.getClientArea();
		this.scFixedCostGridContainer.setMinSize(this.scFixedCostGridContainer.computeSize(SWT.DEFAULT, r.height, true));
	}
	
	private void refreshFixedCostGrid(Scenario scenario){
		if(this.fixedCostGrid != null){
			this.fixedCostGrid.dispose();
		}
		this.fixedCostGrid = new MiningStockpileCostGrid(this.scFixedCostGridContainer, SWT.None, scenario);
		this.scFixedCostGridContainer.setContent(this.fixedCostGrid);
	}
	
	private void initializeOpexGridContainer(){
		this.scOpexGridContainer = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		FormData fd_scGridContainer = new FormData(500,500);// temp hack else size of scrolled composite keeps on increasing
		fd_scGridContainer.top = new FormAttachment(textStartYear, 10, SWT.BOTTOM);
		fd_scGridContainer.left = new FormAttachment(labelScreenName, 0, SWT.LEFT);
		fd_scGridContainer.bottom = new FormAttachment(100, -250);
		//fd_scGridContainer.bottom = new FormAttachment(50);
		fd_scGridContainer.right = new FormAttachment(100, -35);
		this.scOpexGridContainer.setExpandHorizontal(true);
		this.scOpexGridContainer.setExpandVertical(true);
		this.scOpexGridContainer.setLayoutData(fd_scGridContainer);
		
		Rectangle r = this.scOpexGridContainer.getClientArea();
		this.scOpexGridContainer.setMinSize(this.scOpexGridContainer.computeSize(SWT.DEFAULT, r.height, true));
		
		this.btnAddOpexRow = new Button(this, SWT.NONE);
		this.btnAddOpexRow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TO DO implement row add
				if(opexGrid != null){
					opexGrid.addRow();
					Rectangle r = opexGrid.getClientArea();
					int gridWidth = r.width;
					
					int scrollableHeight = scOpexGridContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).y;
					Point point = new Point(gridWidth, scrollableHeight);
					scOpexGridContainer.setMinSize(point);
				}
			}
		});
		this.btnAddOpexRow.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
		FormData fd_btnAddRow = new FormData();
		fd_btnAddRow.top = new FormAttachment(textStartYear, 10, SWT.BOTTOM);
		fd_btnAddRow.right = new FormAttachment(100, -5);
		this.btnAddOpexRow.setLayoutData(fd_btnAddRow);
		this.btnAddOpexRow.setText("+");
		
	}
	
	private void refreshOpexGrid(Scenario scenario){
		if(this.opexGrid != null){
			this.opexGrid.dispose();
		}
		this.opexGrid = new OpexDefinitionGrid(this.scOpexGridContainer, SWT.None, scenario);
		this.scOpexGridContainer.setContent(this.opexGrid);
		Rectangle r = opexGrid.getClientArea();
		int gridWidth = r.width;
		
		int scrollableHeight = scOpexGridContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).y;
		Point point = new Point(gridWidth, scrollableHeight);
		scOpexGridContainer.setMinSize(point);
	}

	private void handleScenarioSelection(int index){
		System.out.println("Selected scenario index is: " + index);
		Scenario scenario = this.scenarioList.get(index);
		ScenarioConfigutration.getInstance().load(scenario.getId());
		this.textScenarioInUse.setText(scenario.getName());
		this.refreshOpexGrid(scenario);
		this.refreshFixedCostGrid(scenario);
		GnosEvent event = new GnosEvent(this, "selected:new-scenario");
		triggerGnosEvent(event);
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void onGnosEventFired(GnosEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	private void triggerGnosEvent(GnosEvent event){
		int j = listeners.size();
		int i = 0;
		for(i=0; i<j; i++){
			listeners.get(i).onGnosEventFired(event);
		}
	}
}
