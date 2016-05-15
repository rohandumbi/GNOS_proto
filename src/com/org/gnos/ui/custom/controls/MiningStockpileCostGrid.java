package com.org.gnos.ui.custom.controls;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
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
import com.org.gnos.db.model.OreMiningCost;
import com.org.gnos.db.model.StockpileReclaimingCost;
import com.org.gnos.db.model.StockpilingCost;
import com.org.gnos.db.model.WasteMiningCost;
import com.org.gnos.services.TimePeriod;

public class MiningStockpileCostGrid extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private Composite compositeGridHeader;
	private List<Composite> allRows;
	private Composite presentRow;
	private TimePeriod timePeriod;
	private Label firstSeparator;
	private Label lblClassification;
	private String[] costCategories = new String[]{"Ore mining cost", "Waste mining cost", "Stockpile cost", "Stockpile reclaiming cost"};

	public MiningStockpileCostGrid(Composite parent, int style, TimePeriod timePeriod) {
		super(parent, style);
		FixedOpexCost[] fixedOpexCostData = ProjectConfigutration.getInstance().getFixedCost();
		this.allRows = new ArrayList<Composite>();
		this.timePeriod = timePeriod;
		this.createContent(parent);
	}
	
	private void createContent(Composite parent){
		this.setLayout(new FormLayout());
		this.createHeader();
		//this.createRows();
		this.addRows();
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
		lblClassification.setText("Category");
		lblClassification.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		firstSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_firstSeparator = new FormData();
		fd_firstSeparator.left = new FormAttachment(lblClassification, 60);
		firstSeparator.setLayoutData(fd_firstSeparator);
		
		this.presentRow = this.compositeGridHeader;//referring to the header as the 1st row when there are no rows inserted yet
		this.addTimePeriodHeaderColumns(firstSeparator);

	}
	
	private void addTimePeriodHeaderColumns(Control reference){
		Control previousColumn = reference;
		for(int i=0; i<this.timePeriod.getIncrements(); i++){
			Label lblYear;
			if(i != 0){
				Label separator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
				FormData fd_separator = new FormData();
				fd_separator.left = new FormAttachment(previousColumn, 25);
				separator.setLayoutData(fd_separator);
				
				lblYear = new Label(compositeGridHeader, SWT.NONE);
				lblYear.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
				FormData fd_lblYear = new FormData();
				fd_lblYear.left = new FormAttachment(separator, 25);
				fd_lblYear.top = new FormAttachment(0, 2);
				lblYear.setText(String.valueOf(this.timePeriod.getStartYear() + i));
				lblYear.setBackground(SWTResourceManager.getColor(230, 230, 230));
				lblYear.setLayoutData(fd_lblYear);
			}else{
				
				lblYear = new Label(compositeGridHeader, SWT.NONE);
				lblYear.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
				FormData fd_lblYear = new FormData();
				fd_lblYear.left = new FormAttachment(reference, 25);
				fd_lblYear.top = new FormAttachment(0, 2);
				lblYear.setText(String.valueOf(this.timePeriod.getStartYear() + i));
				lblYear.setBackground(SWTResourceManager.getColor(230, 230, 230));
				lblYear.setLayoutData(fd_lblYear);
			}
			
			previousColumn = lblYear;
		}
	}
	
	private void addRows(){
		for(int i=0; i<4; i++){
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
		}
	}
	
	
	
	private void addTimePeriodRowMembers(Composite parent, Control reference){
		Control previousMember = reference;
		for(int i=0; i<this.timePeriod.getIncrements(); i++){
			Text yearlyValue = new Text(parent, SWT.BORDER);
			FormData fd_yearlyValue = new FormData();
			/*
			 * Hacky calculation at the moment
			 */
			fd_yearlyValue.left = new FormAttachment(previousMember, 3);
			fd_yearlyValue.right = new FormAttachment(previousMember, 76, SWT.RIGHT);
			yearlyValue.setLayoutData(fd_yearlyValue);
			previousMember = yearlyValue;
		}
	}
	
	public FixedOpexCost[] getCostData(){
		//this.opexDataList = new ArrayList<OpexData>();
		int i = 0;
		FixedOpexCost[] fixedOpexCostArray = new FixedOpexCost[4];
		FixedOpexCost fixedOpexCost = null;
		for(Composite rowCostData : this.allRows){
			Control[] rowChildren = rowCostData.getChildren();
			
			if(i==0){
				fixedOpexCost = new OreMiningCost();
			}else if(i==1){
				fixedOpexCost = new WasteMiningCost();
			}else if(i==2){
				fixedOpexCost = new StockpilingCost();
			}else if(i==3){
				fixedOpexCost = new StockpileReclaimingCost();
			}
			
			Map<Integer, Integer> mapCostData = new LinkedHashMap<Integer, Integer>();
			for(int j=0; j<this.timePeriod.getIncrements(); j++){
				mapCostData.put((this.timePeriod.getStartYear() + j), Integer.valueOf(((Text)rowChildren[1+j]).getText())); // cost input data starts from 1st indexed row child.
			}
			
			fixedOpexCost.setCostData(mapCostData);
			fixedOpexCostArray[i] = fixedOpexCost;
			
			/*if(rowOpexData.getData() == null){
				//new row data, not update of previously saved rowOpexData.
				System.out.println("\n Model: " + modelName + " inUse " + inUse + " isRevenue " + isRevenue);
				opexData = new OpexData(model);
				this.opexDataList.add(opexData);
			}else{
				//update of previously saved rowOpexData.
				opexData = (OpexData)rowOpexData.getData();
				opexData.setModel(model);
			}*/
			
			
			i++;
		}
		//ProjectConfigutration.getInstance().setOpexDataList(this.opexDataList);
		return fixedOpexCostArray;
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
