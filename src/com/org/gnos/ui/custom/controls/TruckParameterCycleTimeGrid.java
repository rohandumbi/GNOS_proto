package com.org.gnos.ui.custom.controls;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.db.model.FixedOpexCost;
import com.org.gnos.db.model.Stockpile;
import com.org.gnos.db.model.TruckParameterCycleTime;

public class TruckParameterCycleTimeGrid extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private Composite compositeGridHeader;
	private List<Composite> allRows;
	private Composite presentRow;
	//private Scenario scenario;
	private Label firstSeparator;
	private Label lblClassification;
	//private String[] costCategories = new String[]{"Ore mining cost", "Waste mining cost", "Stockpile cost", "Stockpile reclaiming cost", "Truck hour cost"};
	private FixedOpexCost[] existingFixedOpexCost;
	private String[] processNames;
	
	private ProjectConfigutration projectInstance;
	private static final int FIRST_SEPARATOR_POSITION = 10;
	private ArrayList<TruckParameterCycleTime> truckParameterCycleTimeList;

	public TruckParameterCycleTimeGrid(Composite parent, int style) {
		super(parent, style);
		this.projectInstance = ProjectConfigutration.getInstance();
		this.allRows = new ArrayList<Composite>();
		this.processNames = getProcesses();
		this.truckParameterCycleTimeList = projectInstance.getTruckParameterCycleTimeList();
		this.createContent(parent);
	}
	
	private void createContent(Composite parent){
		this.setLayout(new FormLayout());
		this.createHeader();
		//this.createRows();
		this.addRows();
	}
	
	private String[] getProcesses(){
		int i = 0;
		//int sourceFieldSize = this.allSourceFields.size();
		List<com.org.gnos.db.model.Process> processes = this.projectInstance.getProcessList();
		int processListSize = processes.size();
		String[] processNames = new String[processListSize];
		for(i=0; i<processListSize; i++){
			processNames[i] = processes.get(i).getModel().getName();
		}
		return processNames;
	}
	
	private String[] getReclaimStockpiles(){
		int i = 0;
		//int sourceFieldSize = this.allSourceFields.size();
		List<Stockpile> stockpiles = this.projectInstance.getStockPileList();
		List<Stockpile> reclaimStockpiles = new ArrayList<Stockpile>();
		for(Stockpile stockpile : stockpiles){
			if(stockpile.isReclaim() == true){
				reclaimStockpiles.add(stockpile);
			}
		}
		int reclaimStockpilesSize = reclaimStockpiles.size();
		String[] reclaimStockpileNames = new String[reclaimStockpilesSize];
		for(i=0; i<reclaimStockpilesSize; i++){
			reclaimStockpileNames[i] = reclaimStockpiles.get(i).getName();
		}
		return reclaimStockpileNames;
	}
	
	private void createHeader(){
		compositeGridHeader = new Composite(this, SWT.BORDER);
		compositeGridHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));
		compositeGridHeader.setLayout(new FormLayout());
		FormData fd_compositeGridHeader = new FormData();
		fd_compositeGridHeader.bottom = new FormAttachment(0, 31);
		fd_compositeGridHeader.top = new FormAttachment(0);
		fd_compositeGridHeader.left = new FormAttachment(0);
		fd_compositeGridHeader.right = new FormAttachment(100);
		compositeGridHeader.setLayoutData(fd_compositeGridHeader);

		lblClassification = new Label(compositeGridHeader, SWT.NONE);
		lblClassification.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblClassification = new FormData();
		fd_lblClassification.top = new FormAttachment(0,2);
		fd_lblClassification.left = new FormAttachment(0, 60);
		lblClassification.setLayoutData(fd_lblClassification);
		lblClassification.setText("Stockpile");
		lblClassification.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		firstSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_firstSeparator = new FormData();
		fd_firstSeparator.left = new FormAttachment(FIRST_SEPARATOR_POSITION);
		firstSeparator.setLayoutData(fd_firstSeparator);
		
		this.presentRow = this.compositeGridHeader;//referring to the header as the 1st row when there are no rows inserted yet
		this.addProcessHeaderColumns(firstSeparator);

	}
	
	private void addProcessHeaderColumns(Control reference){
		Control previousColumn = reference;
		
		for(int i=0; i<this.processNames.length; i++){
			Label lblYear;
			Label separator;
			if(i != 0){
				separator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
				FormData fd_separator = new FormData();
				fd_separator.left = new FormAttachment(previousColumn, 118);
				separator.setLayoutData(fd_separator);
				
				lblYear = new Label(compositeGridHeader, SWT.NONE);
				lblYear.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
				FormData fd_lblYear = new FormData();
				fd_lblYear.left = new FormAttachment(separator, 25);
				fd_lblYear.top = new FormAttachment(0, 2);
				lblYear.setText(processNames[i]);
				lblYear.setBackground(SWTResourceManager.getColor(230, 230, 230));
				lblYear.setLayoutData(fd_lblYear);
			}else{
				lblYear = new Label(compositeGridHeader, SWT.NONE);
				lblYear.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
				FormData fd_lblYear = new FormData();
				fd_lblYear.left = new FormAttachment(reference, 25);
				fd_lblYear.top = new FormAttachment(0, 2);
				lblYear.setText(processNames[i]);
				lblYear.setBackground(SWTResourceManager.getColor(230, 230, 230));
				lblYear.setLayoutData(fd_lblYear);
				separator = (Label)reference;
			}
			
			previousColumn = separator;
		}
	}
	
	private void addRows(){
		String[] reclaimStockpileNames = getReclaimStockpiles();
		TruckParameterCycleTime truckParameterCycleTime;
		for(int i=0; i<reclaimStockpileNames.length; i++){
			truckParameterCycleTime = projectInstance.getTruckParamCycleTimeByStockpileName(reclaimStockpileNames[i]);
			if(truckParameterCycleTime == null){
				truckParameterCycleTime = new TruckParameterCycleTime();
				truckParameterCycleTime.setStockPileName(reclaimStockpileNames[i]);
				truckParameterCycleTimeList.add(i, truckParameterCycleTime);
			}
			Color backgroundColor = SWTResourceManager.getColor(SWT.COLOR_WHITE);
			if((i%2 != 0)){
				backgroundColor =  SWTResourceManager.getColor(245, 245, 245);
			}
			Composite compositeRow = new Composite(this, SWT.BORDER);
			compositeRow.setLayout(new FormLayout());
			compositeRow.setBackground(backgroundColor);
			FormData fd_compositeRow = new FormData();
			fd_compositeRow.left = new FormAttachment(this.presentRow, 0, SWT.LEFT);
			fd_compositeRow.bottom = new FormAttachment(this.presentRow, 26, SWT.BOTTOM);
			fd_compositeRow.right = new FormAttachment(this.presentRow, 0, SWT.RIGHT);
			fd_compositeRow.top = new FormAttachment(this.presentRow);
			compositeRow.setLayoutData(fd_compositeRow);
			
			Label labelCategory = new Label(compositeRow, SWT.NONE);
			labelCategory.setBackground(backgroundColor);
			labelCategory.setText(reclaimStockpileNames[i]);
			FormData fd_labelCategory = new FormData();
			fd_labelCategory.left = new FormAttachment(0, 60);
			fd_labelCategory.top = new FormAttachment(0);
			//fd_labelCategory.right = new FormAttachment(0, 168);
			labelCategory.setLayoutData(fd_labelCategory);
			
			compositeRow.setData(truckParameterCycleTimeList.get(i));
			
			this.addProcessRowMembers(compositeRow, labelCategory);
			
			this.allRows.add(compositeRow);
			this.presentRow = compositeRow;
			this.layout();
		}
		/*FixedOpexCost fixedOpexCost = null;
		for(int i=0; i<5; i++){
			Color backgroundColor = SWTResourceManager.getColor(SWT.COLOR_WHITE);
			if((i%2 != 0)){
				backgroundColor =  SWTResourceManager.getColor(245, 245, 245);
			}
			Composite compositeRow = new Composite(this, SWT.BORDER);
			
			if(this.existingFixedOpexCost[i] == null){
				if(i==0){
					fixedOpexCost = new OreMiningCost();
				}else if(i==1){
					fixedOpexCost = new WasteMiningCost();
				}else if(i==2){
					fixedOpexCost = new StockpilingCost();
				}else if(i==3){
					fixedOpexCost = new StockpileReclaimingCost();
				}else if(i==4){
					fixedOpexCost = new TruckHourCost();
				}
				fixedOpexCost.setScenarioId(scenario.getId());
				this.existingFixedOpexCost[i] = fixedOpexCost;
			}
			
			compositeRow.setData(this.existingFixedOpexCost[i]);
			compositeRow.setLayout(new FormLayout());
			compositeRow.setBackground(backgroundColor);
			FormData fd_compositeRow = new FormData();
			fd_compositeRow.left = new FormAttachment(this.presentRow, 0, SWT.LEFT);
			fd_compositeRow.bottom = new FormAttachment(this.presentRow, 26, SWT.BOTTOM);
			fd_compositeRow.right = new FormAttachment(this.presentRow, 0, SWT.RIGHT);
			fd_compositeRow.top = new FormAttachment(this.presentRow);
			compositeRow.setLayoutData(fd_compositeRow);
			
			Label labelCategory = new Label(compositeRow, SWT.NONE);
			labelCategory.setBackground(backgroundColor);
			labelCategory.setText(this.costCategories[i]);
			FormData fd_labelCategory = new FormData();
			fd_labelCategory.left = new FormAttachment(0);
			fd_labelCategory.top = new FormAttachment(0);
			fd_labelCategory.right = new FormAttachment(0, 168);
			labelCategory.setLayoutData(fd_labelCategory);
			
			this.addTimePeriodRowMembers(compositeRow, labelCategory);
			
			this.allRows.add(compositeRow);
			this.presentRow = compositeRow;
			this.layout();
		}*/
	}
	
	
	private void addProcessRowMembers(Composite parent, Control reference){
		Control previousMember = reference;
		//FixedOpexCost associatedFixedCost = (FixedOpexCost)parent.getData();
		//final Map<Integer, Float> associatedFixedCostData = associatedFixedCost.getCostData();
		
		TruckParameterCycleTime truckParameterCycleTime = (TruckParameterCycleTime)parent.getData();
		final Map<String, BigDecimal> associatedProcessData = truckParameterCycleTime.getProcessData();
		for(int i=0; i<this.processNames.length; i++){
			Text yearlyValue = new Text(parent, SWT.BORDER);
			FormData fd_yearlyValue = new FormData();
			//fd_yearlyValue.right = new FormAttachment(previousMember, 76, SWT.RIGHT);
			if(i==0){
				fd_yearlyValue.left = new FormAttachment(FIRST_SEPARATOR_POSITION);
				fd_yearlyValue.right = new FormAttachment(FIRST_SEPARATOR_POSITION, 120);
			}else{
				fd_yearlyValue.left = new FormAttachment(previousMember, 0);
				fd_yearlyValue.right = new FormAttachment(previousMember, 120, SWT.RIGHT);
			}
			yearlyValue.setLayoutData(fd_yearlyValue);
			final String processName = this.processNames[i];
			if(associatedProcessData != null){
				BigDecimal value = associatedProcessData.get(processName);
				if(value != null){
					yearlyValue.setText(value.toString());
				}
			}
			/*if(value != null){
				yearlyValue.setText(Float.toString(value));
			}*/
			
			//Text yearlyValue = new Text(parent, SWT.BORDER);
			//final int targetYear = this.scenario.getStartYear() + i;
			//Float value = associatedFixedCostData.get(targetYear);
			/*if(value != null){
				yearlyValue.setText(Float.toString(value));
			}*/
			yearlyValue.addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent event) {
					// Get the widget whose text was modified
					Text text = (Text) event.widget;
					//System.out.println("Input value for the " + targetYear + " year is " + text.getText());
					associatedProcessData.put(processName, new BigDecimal(text.getText()));
				}
			});
			/*
			 * Hacky calculation at the moment
			 */
			previousMember = yearlyValue;
		}
	}
	
	
	public void resetAllRows(){
		for(Composite existingRow : this.allRows){
			existingRow.setEnabled(false);
		}
		this.allRows = new ArrayList<Composite>();
	}

	public List<Composite> getAllRowsComposite(){
		return this.allRows;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
