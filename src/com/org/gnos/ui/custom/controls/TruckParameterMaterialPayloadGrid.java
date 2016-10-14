package com.org.gnos.ui.custom.controls;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.db.model.Expression;

public class TruckParameterMaterialPayloadGrid extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private List<Expression> allSourceFields;
	private Composite compositeGridHeader;
	private List<Composite> allRows;
	private Map<String, Integer> materialPayloadMap;
	private Composite presentRow;
	
	public TruckParameterMaterialPayloadGrid(Composite parent, int style) {
		super(parent, style);
		ProjectConfigutration projectInstance = ProjectConfigutration.getInstance();
		this.materialPayloadMap = projectInstance.getTruckParameterData().getMaterialPayloadMap();
		this.allSourceFields = projectInstance.getExpressions();
		this.allRows = new ArrayList<>();
		this.createContent(parent);
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
		lblRqrdFieldHeader.setText("MATERIAL");
		lblRqrdFieldHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));

		Label lblSourceFieldHeader = new Label(compositeGridHeader, SWT.NONE);
		lblSourceFieldHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));
		lblSourceFieldHeader.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		FormData fd_lblSourceFieldHeader = new FormData();
		fd_lblSourceFieldHeader.top = new FormAttachment(0, 2);
		fd_lblSourceFieldHeader.left = new FormAttachment(label, 10);
		lblSourceFieldHeader.setLayoutData(fd_lblSourceFieldHeader);
		lblSourceFieldHeader.setText("PAYLOAD");
		
		this.presentRow = compositeGridHeader;

	}
	
	public void addRow(){
		this.addRow("");
	}
	
	private void addRow(final String key){
		final Composite compositeRow = new Composite(this, SWT.BORDER);
		compositeRow.setLayout(new FormLayout());
		Color backgroundColor = SWTResourceManager.getColor(SWT.COLOR_WHITE);
		if((allRows.size())%2 != 0){
			backgroundColor =  SWTResourceManager.getColor(245, 245, 245);
		}
		compositeRow.setBackground(backgroundColor);
		FormData fd_compositeRow = new FormData();
		fd_compositeRow.bottom = new FormAttachment(presentRow, 30, SWT.BOTTOM);
		fd_compositeRow.top = new FormAttachment(presentRow);
		fd_compositeRow.right = new FormAttachment(presentRow, 0, SWT.RIGHT);
		fd_compositeRow.left = new FormAttachment(presentRow, 0, SWT.LEFT);
		compositeRow.setLayoutData(fd_compositeRow);

		final Combo comboSourceField = new Combo(compositeRow, SWT.NONE);
		comboSourceField.setItems(this.getSourceFieldComboItems());
		FormData fd_comboSourceField = new FormData();
		fd_comboSourceField.left = new FormAttachment(0, 10);
		fd_comboSourceField.top =  new FormAttachment(0);
		fd_comboSourceField.right = new FormAttachment(30);
		fd_comboSourceField.bottom = new FormAttachment(presentRow, 20, SWT.BOTTOM);
		comboSourceField.setLayoutData(fd_comboSourceField);
		if(!key.isEmpty()){
			comboSourceField.setText(key);
		}
		final Text textPayload = new Text(compositeRow, SWT.BORDER);
		FormData fd_textPayload = new FormData();
		fd_textPayload.left = new FormAttachment(50, 10);
		fd_textPayload.top =  new FormAttachment(0);
		fd_textPayload.right = new FormAttachment(70);
		//fd_textPayload.bottom = new FormAttachment(presentRow, 20, SWT.BOTTOM);
		textPayload.setLayoutData(fd_textPayload);
		textPayload.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		if(!key.isEmpty()){
			int payLoad = materialPayloadMap.get(key);
			if(payLoad > 0){
				textPayload.setText(String.valueOf(payLoad));
			}
		}
		
		comboSourceField.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String expressionName = comboSourceField.getText();
				if(!textPayload.getText().isEmpty() && !comboSourceField.getText().isEmpty()){
					materialPayloadMap.put(expressionName, Integer.valueOf(textPayload.getText()));
				}
			}
		});
		textPayload.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent event) {
				//TODO implement handler
				String text = textPayload.getText();
				if(!text.isEmpty() && !comboSourceField.getText().isEmpty()){
					materialPayloadMap.put(comboSourceField.getText(), Integer.valueOf(text));
				}/*else{
					truckParameterData.setFixedTime(0);
				}*/
			}
		});
		
		allRows.add(compositeRow);
		this.presentRow = compositeRow;
		this.layout();
	}

	private void createContent(Composite parent){
		this.setLayout(new FormLayout());
		this.createHeader();
		Set keys = materialPayloadMap.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			String key = it.next();
			addRow(key);
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
