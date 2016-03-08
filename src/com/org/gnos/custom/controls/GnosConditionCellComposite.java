package com.org.gnos.custom.controls;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.services.Filter;
import com.org.gnos.services.csv.ColumnHeader;

public class GnosConditionCellComposite extends Composite {

	@SuppressWarnings("unused")
	private Composite parent;
	//private Text textConditionValue;
	private Composite lastCondition;
	private List<Composite> allConditions;
	private String[] allSourceFields;
	private List<Filter> allFilters;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public GnosConditionCellComposite(Composite parent, int style, String[] allSourceFields) {
		super(parent, style);
		this.parent = parent;
		this.allConditions = new ArrayList<Composite>();
		this.allFilters = new ArrayList<Filter>();
		this.allSourceFields = allSourceFields;
		this.setLayout(new FillLayout(SWT.VERTICAL));
		
		Button buttonAddCondition = new Button(this, SWT.NONE);
		buttonAddCondition.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO add handler
				addCondition();
			}
		});
		buttonAddCondition.setText("Add Condition");
	}
	
	private String[] getSourceFieldsComboItems(){
		int i = 0;
		int sourceFieldSize = this.allSourceFields.length;
		//this.sourceFieldsComboItems = new String[sourceFieldSize];
		/*this.numericSourceFields = new ArrayList<String>();
		for(i=0; i<sourceFieldSize; i++){
			if((this.allSourceFields.get(i).getDataType() == 2) || (this.allSourceFields.get(i).getDataType() == 3)){ //only double or integer fields are allowed in expression definition
				//this.sourceFieldsComboItems[i] = this.allSourceFields.get(i).getName();
				this.numericSourceFields.add(this.allSourceFields.get(i).getName());
			}
		}*/
		String[] sourceFieldsComboItems = new String[sourceFieldSize];
		for(i=0; i<sourceFieldSize; i++){
			sourceFieldsComboItems[i] = this.allSourceFields[i];
		}
		return sourceFieldsComboItems;
	}
	
	protected void addCondition(){
		lastCondition = new Composite(this, SWT.NONE);
		lastCondition.setLayout(new FormLayout());
		
		/*Combo comboConditionType = new Combo(lastCondition, SWT.NONE);
		FormData fd_comboConditionType = new FormData();
		fd_comboConditionType.right = new FormAttachment(0, 62);
		fd_comboConditionType.left = new FormAttachment(0);
		comboConditionType.setLayoutData(fd_comboConditionType);*/
		
		Combo comboField = new Combo(lastCondition, SWT.NONE);
		FormData fd_comboField = new FormData();
		fd_comboField.left = new FormAttachment(0);
		fd_comboField.right = new FormAttachment(40);
		comboField.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		comboField.setItems(this.getSourceFieldsComboItems());
		comboField.setText("Field");
		comboField.setLayoutData(fd_comboField);
		
		Combo comboOperator = new Combo(lastCondition, SWT.NONE);
		FormData fd_comboOperator = new FormData();
		fd_comboOperator.left = new FormAttachment(comboField);
		fd_comboOperator.right = new FormAttachment(60);
		comboField.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		comboOperator.setItems(new String[]{"=", "<>", ">", "<", "In"});
		comboOperator.setText("Operator");
		comboOperator.setLayoutData(fd_comboOperator);
		
		Text textConditionValue = new Text(lastCondition, SWT.BORDER);
		FormData fd_textConditionValue = new FormData();
		//fd_textConditionValue.top = new FormAttachment(comboConditionType, 0, SWT.TOP);
		fd_textConditionValue.left = new FormAttachment(comboOperator);
		fd_textConditionValue.right = new FormAttachment(100);
		textConditionValue.setText("Value");
		textConditionValue.setLayoutData(fd_textConditionValue);
		
		textConditionValue.addListener(SWT.MouseUp, new Listener() {

	        @Override
	        public void handleEvent(Event event) {
	            Text text = (Text) event.widget;
	            text.setText("");
	            /*String selection = text.getSelectionText();

	            if(selection.length() > 0)
	            {
	                System.out.println("Selected text: " + selection);
	            }*/
	        }
	    });
		
		allConditions.add(lastCondition);
		this.layout(true, true);
		
		/*
		 * Total hack of resizing parents.
		 */
		
		Composite parentExpressionRow = this.getParent();
		//parentExpressionRow.layout(true, true);
		
		Composite parentExpressionGrid = parentExpressionRow.getParent();
		//parentExpressionGrid.layout(true, true);
		
		Composite parentExpressionScreen = parentExpressionGrid.getParent();
		parentExpressionScreen.layout(true, true);
		
		/*Composite configurationViewport = parentExpressionScreen.getParent();
		configurationViewport.layout(true, true);
		
		Composite viewportContainer = configurationViewport.getParent();
		viewportContainer.layout(true, true);*/
	}
	

	public List<Filter> getExpressionFilters(){
		for(int i=0; i<this.allConditions.size(); i++){
			Control[] conditionComponents = this.allConditions.get(i).getChildren();
			Combo comboField = (Combo)conditionComponents[0];
			Combo comboOperator = (Combo)conditionComponents[1];
			Text textConditionValue = (Text)conditionComponents[2];
			if(comboField.getSelectionIndex() < 0 || comboOperator.getSelectionIndex()<0 || textConditionValue.getText()==null || textConditionValue.getText().equalsIgnoreCase("")){
				return null;
			}
			int filterId = i;
			int columnId = comboField.getSelectionIndex();
			int opType = comboOperator.getSelectionIndex();
			String value = textConditionValue.getText();
			Filter filter = new Filter(filterId, columnId, opType, value);
			this.allFilters.add(filter);
		}
		
		return this.allFilters;
	}


	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
