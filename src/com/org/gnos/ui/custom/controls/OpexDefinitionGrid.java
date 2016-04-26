package com.org.gnos.ui.custom.controls;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.core.Model;
import com.org.gnos.core.OpexData;
import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.services.TimePeriod;

public class OpexDefinitionGrid extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private Composite compositeGridHeader;
	private List<Composite> allRows;
	private String[] sourceFieldsComboItems;
	private Composite presentRow;
	private List<OpexData> opexDataList;
	private Composite parent;
	private List<String> presentmodelNames;
	private TimePeriod timePeriod;
	private Label firstSeparator;
	private Label secondSeparator;

	public OpexDefinitionGrid(Composite parent, int style, TimePeriod timePeriod) {
		super(parent, style);
		this.parent = parent;
		this.allRows = new ArrayList<Composite>();
		this.opexDataList = ProjectConfigutration.getInstance().getOpexDataList();
		this.timePeriod = timePeriod;
		this.createContent(parent);
	}
	
	private void createContent(Composite parent){
		this.setLayout(new FormLayout());
		this.createHeader();
		this.createRows();
	}
	
	private boolean isModelNameDuplicate(String modelName){
		boolean isPresentInModelGrid = false;
		for(String str: presentmodelNames) {
		    if(str.trim().equalsIgnoreCase(modelName.trim()))
		    	isPresentInModelGrid = true;
		}
		return isPresentInModelGrid;
	}

	
	private String[] getIdentifierComboItems(){
		
		List<Model> models = ProjectConfigutration.getInstance().getModels();
		this.sourceFieldsComboItems = new String[models.size()];
		for(int i=0; i<models.size(); i++){
			this.sourceFieldsComboItems[i] = models.get(i).getName();
		}
				
		return this.sourceFieldsComboItems;
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

		Label lblClassification = new Label(compositeGridHeader, SWT.NONE);
		lblClassification.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblClassification = new FormData();
		fd_lblClassification.top = new FormAttachment(0,2);
		fd_lblClassification.left = new FormAttachment(0, 10);
		lblClassification.setLayoutData(fd_lblClassification);
		lblClassification.setText("Classification");
		lblClassification.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		firstSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_firstSeparator = new FormData();
		fd_firstSeparator.left = new FormAttachment(lblClassification, 10);
		firstSeparator.setLayoutData(fd_firstSeparator);
		
		Label lblUse = new Label(compositeGridHeader, SWT.NONE);
		lblUse.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblUse = new FormData();
		fd_lblUse.top = new FormAttachment(lblClassification, 0, SWT.TOP);
		fd_lblUse.left = new FormAttachment(firstSeparator, 6);
		lblUse.setLayoutData(fd_lblUse);
		lblUse.setText("Use");
		lblUse.setBackground(SWTResourceManager.getColor(230, 230, 230));

		secondSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_secondSeparator = new FormData();
		fd_secondSeparator.left = new FormAttachment(lblUse, 10);
		secondSeparator.setLayoutData(fd_secondSeparator);

		Label lblIdentifier = new Label(compositeGridHeader, SWT.NONE);
		lblIdentifier.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblIdentifier = new FormData();
		fd_lblIdentifier.top = new FormAttachment(lblClassification, 0, SWT.TOP);
		fd_lblIdentifier.left = new FormAttachment(secondSeparator, 35);
		lblIdentifier.setLayoutData(fd_lblIdentifier);
		lblIdentifier.setText("Identifier");
		lblIdentifier.setBackground(SWTResourceManager.getColor(230, 230, 230));

		this.presentRow = this.compositeGridHeader;//referring to the header as the 1st row when there are no rows inserted yet
		this.addTimePeriodHeaderColumns(lblIdentifier);

	}
	
	private void addTimePeriodHeaderColumns(Control reference){
		Control previousColumn = reference;
		for(int i=0; i<this.timePeriod.getIncrements(); i++){
			Label separator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
			FormData fd_separator = new FormData();
			fd_separator.left = new FormAttachment(previousColumn, 25);
			separator.setLayoutData(fd_separator);
			
			Label lblYear = new Label(compositeGridHeader, SWT.NONE);
			lblYear.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
			FormData fd_lblYear = new FormData();
			fd_lblYear.left = new FormAttachment(separator, 25);
			fd_lblYear.top = new FormAttachment(0, 2);
			lblYear.setText(String.valueOf(this.timePeriod.getStartYear() + i));
			lblYear.setBackground(SWTResourceManager.getColor(230, 230, 230));
			lblYear.setLayoutData(fd_lblYear);
			
			previousColumn = lblYear;
		}
	}

	private void createRows() {
		
		for(OpexData od: this.opexDataList) {
			final Composite compositeRow = new Composite(this, SWT.BORDER);
			compositeRow.setData(od);
			compositeRow.setLayout(new FormLayout());
			Color backgroundColor = SWTResourceManager.getColor(SWT.COLOR_WHITE);
			if((this.allRows != null) && (this.allRows.size()%2 != 0)){
				backgroundColor =  SWTResourceManager.getColor(245, 245, 245);
			}
			compositeRow.setBackground(backgroundColor);
			
			FormData fd_compositeRow = new FormData();
			fd_compositeRow.left = new FormAttachment(this.presentRow, 0, SWT.LEFT);
			//fd_compositeRow.bottom = new FormAttachment(this.presentRow, 26, SWT.BOTTOM);
			fd_compositeRow.right = new FormAttachment(this.presentRow, 0, SWT.RIGHT);
			fd_compositeRow.top = new FormAttachment(this.presentRow);


			Combo comboClassification = new Combo(compositeRow, SWT.NONE);
			comboClassification.setItems(new String[]{"PCost", "Rev"});
			if(od.isRevenue()) {
				comboClassification.select(1);
			} else {
				comboClassification.select(0);
			}
			FormData fd_comboClassification = new FormData();
			fd_comboClassification.left = new FormAttachment(0, 2);
			fd_comboClassification.top = new FormAttachment(0);
			//fd_comboClassification.right = new FormAttachment(20, -5);
			comboClassification.setLayoutData(fd_comboClassification);
			
			Button btnUse = new Button(compositeRow, SWT.CHECK);
			btnUse.setSelection(od.isInUse());
			FormData fd_btnUse = new FormData();
			fd_btnUse.left = new FormAttachment(comboClassification, 10, SWT.RIGHT);
			fd_btnUse.top = new FormAttachment(0, 2);
			btnUse.setLayoutData(fd_btnUse);
			
			Combo comboIdentifier = new Combo(compositeRow, SWT.NONE);
			String[] items = this.getIdentifierComboItems();
			comboIdentifier.setItems(items);
			for(int i=0; i< items.length; i++){
				if(items[i].equals(od.getModel().getName())) {
					comboIdentifier.select(i);
					break;
				}
			}
			comboIdentifier.setText("Select Model");
			FormData fd_comboIdentifier = new FormData();
			fd_comboIdentifier.left = new FormAttachment(btnUse, 21);
			fd_comboIdentifier.right = new FormAttachment(btnUse, 135);
			fd_comboIdentifier.top = new FormAttachment(0);
			comboIdentifier.setLayoutData(fd_comboIdentifier);
			
			Control previousMember = comboIdentifier;
			Map<Integer, Integer> yearData = od.getCostData();
			Set keys = yearData.keySet();
			Iterator<Integer> it = keys.iterator();
			while(it.hasNext()){
				int value = yearData.get(it.next());
				Text yearlyValue = new Text(parent, SWT.BORDER);
				yearlyValue.setText(String.valueOf(value));
				FormData fd_yearlyValue = new FormData();
				/*
				 * Hacky calculation at the moment
				 */
				fd_yearlyValue.left = new FormAttachment(previousMember, 3);
				fd_yearlyValue.right = new FormAttachment(previousMember, 76, SWT.RIGHT);
				yearlyValue.setLayoutData(fd_yearlyValue);
				previousMember = yearlyValue;
			}
			
			this.presentRow = compositeRow;
			this.allRows.add(compositeRow);
			compositeRow.setLayoutData(fd_compositeRow);
			this.layout();
		}
	}
	
	public void addRow(){
		final Composite compositeRow = new Composite(this, SWT.BORDER);
		compositeRow.setLayout(new FormLayout());
		Color backgroundColor = SWTResourceManager.getColor(SWT.COLOR_WHITE);
		if((this.allRows != null) && (this.allRows.size()%2 != 0)){
			backgroundColor =  SWTResourceManager.getColor(245, 245, 245);
		}

		compositeRow.setBackground(backgroundColor);
		FormData fd_compositeRow = new FormData();
		fd_compositeRow.left = new FormAttachment(this.presentRow, 0, SWT.LEFT);
		//fd_compositeRow.bottom = new FormAttachment(this.presentRow, 26, SWT.BOTTOM);
		fd_compositeRow.right = new FormAttachment(this.presentRow, 0, SWT.RIGHT);
		fd_compositeRow.top = new FormAttachment(this.presentRow);


		Combo comboClassification = new Combo(compositeRow, SWT.NONE);
		comboClassification.setItems(new String[]{"PCost", "Rev"});
		comboClassification.setText("Select Type");
		FormData fd_comboClassification = new FormData();
		fd_comboClassification.left = new FormAttachment(0, 2);
		fd_comboClassification.top = new FormAttachment(0);
		//fd_comboClassification.right = new FormAttachment(20, -5);
		comboClassification.setLayoutData(fd_comboClassification);
		
		Button btnUse = new Button(compositeRow, SWT.CHECK);
		FormData fd_btnUse = new FormData();
		fd_btnUse.left = new FormAttachment(comboClassification, 10, SWT.RIGHT);
		fd_btnUse.top = new FormAttachment(0, 2);
		btnUse.setLayoutData(fd_btnUse);
		
		Combo comboIdentifier = new Combo(compositeRow, SWT.NONE);
		String[] items = this.getIdentifierComboItems();
		comboIdentifier.setItems(items);
		comboIdentifier.setText("Select Model");
		FormData fd_comboIdentifier = new FormData();
		fd_comboIdentifier.left = new FormAttachment(btnUse, 21);
		fd_comboIdentifier.right = new FormAttachment(btnUse, 135);
		fd_comboIdentifier.top = new FormAttachment(0);
		comboIdentifier.setLayoutData(fd_comboIdentifier);
		
		this.addTimePeriodRowMembers(compositeRow, comboIdentifier);
		
		this.presentRow = compositeRow;
		this.allRows.add(compositeRow);
		compositeRow.setLayoutData(fd_compositeRow);
		this.layout();
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
	
	public boolean saveOpexData(){
		ProjectConfigutration.getInstance().setOpexDataList(this.opexDataList);
		return true;
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
