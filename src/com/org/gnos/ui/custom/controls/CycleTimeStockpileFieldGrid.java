package com.org.gnos.ui.custom.controls;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.Field;
import com.org.gnos.db.model.Stockpile;

public class CycleTimeStockpileFieldGrid extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private List<Field> allSourceFields;
	private Composite compositeGridHeader;
	private List<Composite> allRows;
	private Map<String, String> stockpileFieldMap;
	private Composite presentRow;
	private List<Stockpile> stockpileList;
	
	public CycleTimeStockpileFieldGrid(Composite parent, int style) {
		super(parent, style);
		ProjectConfigutration projectInstance = ProjectConfigutration.getInstance();
		this.stockpileFieldMap = projectInstance.getCycleTimeData().getStockpileFieldMap();
		this.allSourceFields = projectInstance.getFields();
		this.stockpileList = projectInstance.getStockPileList();
		this.allRows = new ArrayList<>();
		this.createContent(parent);
	}
	
	private String[] getStockpileFields(){
		int i = 0;
		int sourceFieldSize = this.stockpileList.size();
		String[] dumps = new String[sourceFieldSize];
		for(i=0; i<sourceFieldSize; i++){
			dumps[i] = this.stockpileList.get(i).getName();
		}
		return dumps;
	}
	
	private String[] getSourceFieldComboItems(){
		int i = 0;
		int sourceFieldSize = this.allSourceFields.size();
		String[] comboItems = new String[sourceFieldSize];
		for(i=0; i<sourceFieldSize; i++){
			comboItems[i] = this.allSourceFields.get(i).getName();
		}
		return comboItems;
	}
	
	private void createHeader(){
		compositeGridHeader = new Composite(this, SWT.BORDER);
		//compositeGridHeader.setBounds(0, 0, 749, 37);
		compositeGridHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));
		compositeGridHeader.setLayout(new FormLayout());
		FormData fd_compositeGridHeader = new FormData();
		fd_compositeGridHeader.bottom = new FormAttachment(0, 31);
		fd_compositeGridHeader.top = new FormAttachment(0);
		fd_compositeGridHeader.left = new FormAttachment(0);
		fd_compositeGridHeader.right = new FormAttachment(100);
		compositeGridHeader.setLayoutData(fd_compositeGridHeader);

		Label label = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_label = new FormData();
		fd_label.left = new FormAttachment(50);
		label.setLayoutData(fd_label);

		Label lblRqrdFieldHeader = new Label(compositeGridHeader, SWT.NONE);
		lblRqrdFieldHeader.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		FormData fd_lblRqrdFieldHeader = new FormData();
		fd_lblRqrdFieldHeader.top = new FormAttachment(0,2);
		fd_lblRqrdFieldHeader.left = new FormAttachment(0, 10);
		lblRqrdFieldHeader.setLayoutData(fd_lblRqrdFieldHeader);
		lblRqrdFieldHeader.setText("DUMP NAME");
		lblRqrdFieldHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));

		Label lblSourceFieldHeader = new Label(compositeGridHeader, SWT.NONE);
		lblSourceFieldHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));
		lblSourceFieldHeader.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		FormData fd_lblSourceFieldHeader = new FormData();
		fd_lblSourceFieldHeader.top = new FormAttachment(0, 2);
		fd_lblSourceFieldHeader.left = new FormAttachment(label, 10);
		lblSourceFieldHeader.setLayoutData(fd_lblSourceFieldHeader);
		lblSourceFieldHeader.setText("SOURCE FIELD");
		
		this.presentRow = compositeGridHeader;
	}
	
	
	private void addRow(final String key){
		Composite compositeRow = new Composite(this, SWT.BORDER);
		compositeRow.setLayout(new FormLayout());
		Color backgroundColor = SWTResourceManager.getColor(SWT.COLOR_WHITE);
		if((allRows.size())%2 != 0){
			backgroundColor =  SWTResourceManager.getColor(245, 245, 245);
		}
		compositeRow.setBackground(backgroundColor);
		FormData fd_compositeRow = new FormData();
		fd_compositeRow.bottom = new FormAttachment(presentRow, 25, SWT.BOTTOM);
		fd_compositeRow.top = new FormAttachment(presentRow);
		fd_compositeRow.right = new FormAttachment(presentRow, 0, SWT.RIGHT);
		fd_compositeRow.left = new FormAttachment(presentRow, 0, SWT.LEFT);
		compositeRow.setLayoutData(fd_compositeRow);

		Label lblRqrdFieldName = new Label(compositeRow, SWT.NONE);
		lblRqrdFieldName.setBackground(backgroundColor);
		lblRqrdFieldName.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		FormData fd_lblRqrdFieldName = new FormData();
		fd_lblRqrdFieldName.top = new FormAttachment(0);
		fd_lblRqrdFieldName.left = new FormAttachment(0, 10);
		lblRqrdFieldName.setLayoutData(fd_lblRqrdFieldName);
		lblRqrdFieldName.setText(key);
		
		final Combo comboSourceField = new Combo(compositeRow, SWT.NONE);
		comboSourceField.setItems(this.getSourceFieldComboItems());
		FormData fd_comboSourceField = new FormData();
		fd_comboSourceField.left = new FormAttachment(50, 12);
		fd_comboSourceField.right = new FormAttachment(80);
		fd_comboSourceField.bottom = new FormAttachment(presentRow, 20, SWT.BOTTOM);
		comboSourceField.setLayoutData(fd_comboSourceField);
		comboSourceField.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String mappedFieldName = comboSourceField.getText();
				stockpileFieldMap.put(key, mappedFieldName);
			}
		});
		String existingMapping = stockpileFieldMap.get(key);
		if(existingMapping != null){
			comboSourceField.setText(existingMapping);
		}else{
			comboSourceField.setText("Mapping Not Done");
		}
		
		allRows.add(compositeRow);
		this.presentRow = compositeRow;
	}
	
	private void createContent(Composite parent){
		this.setLayout(new FormLayout());
		this.createHeader();
		for(String field: this.getStockpileFields()){
			this.addRow(field);
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
