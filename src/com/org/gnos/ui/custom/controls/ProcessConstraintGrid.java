package com.org.gnos.ui.custom.controls;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.core.Node;
import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.core.ScenarioConfigutration;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Model;
import com.org.gnos.db.model.OpexData;
import com.org.gnos.db.model.ProcessConstraintData;
import com.org.gnos.db.model.ProcessJoin;
import com.org.gnos.services.TimePeriod;

public class ProcessConstraintGrid extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private Composite compositeGridHeader;
	private List<Composite> allRows;
	private String[] sourceFieldsComboItems;
	private String[] sourceExpressionComboItems;
	private Composite presentRow;
	private Composite parent;
	private List<String> presentmodelNames;
	private Label firstSeparator;
	private Label secondSeparator;
	private Label thirdSeparator;
	private Label lblClassification;
	private List<ProcessConstraintData> processConstraintDataList;
	private int startYear;
	private int timePeriod;

	public ProcessConstraintGrid(Composite parent, int style) {
		super(parent, style);
		this.parent = parent;
		this.allRows = new ArrayList<Composite>();
		this.timePeriod = ScenarioConfigutration.getInstance().getTimePeriod();
		this.startYear = ScenarioConfigutration.getInstance().getStartYear();
		this.processConstraintDataList = ScenarioConfigutration.getInstance().getProcessConstraintDataList();
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


	private String[] getNonGradeExpressionComboItems(){

		List<Expression> expressions = ProjectConfigutration.getInstance().getNonGradeExpressions();
		this.sourceExpressionComboItems = new String[expressions.size()];
		for(int i=0; i<expressions.size(); i++){
			this.sourceExpressionComboItems[i] = expressions.get(i).getName();
		}

		return this.sourceExpressionComboItems;
	}
	
	private String[] getProcessJoins(){
		List<ProcessJoin> processJoins = ProjectConfigutration.getInstance().getProcessJoins();
		this.sourceExpressionComboItems = new String[processJoins.size()];
		for(int i=0; i<processJoins.size(); i++){
			this.sourceExpressionComboItems[i] = processJoins.get(i).getName();
		}
		return this.sourceExpressionComboItems;
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
		fd_lblClassification.left = new FormAttachment(0, 30);
		lblClassification.setLayoutData(fd_lblClassification);
		lblClassification.setText("Expression");
		lblClassification.setBackground(SWTResourceManager.getColor(230, 230, 230));

		firstSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_firstSeparator = new FormData();
		fd_firstSeparator.left = new FormAttachment(lblClassification, 30);
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

		Label lblGrouping = new Label(compositeGridHeader, SWT.NONE);
		lblGrouping.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblGrouping = new FormData();
		fd_lblGrouping.top = new FormAttachment(lblClassification, 0, SWT.TOP);
		fd_lblGrouping.left = new FormAttachment(secondSeparator, 35);
		lblGrouping.setLayoutData(fd_lblGrouping);
		lblGrouping.setText("Grouping");
		lblGrouping.setBackground(SWTResourceManager.getColor(230, 230, 230));

		thirdSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_thirdSeparator = new FormData();
		fd_thirdSeparator.left = new FormAttachment(lblGrouping, 30);
		thirdSeparator.setLayoutData(fd_thirdSeparator);

		Label lblMaxMin = new Label(compositeGridHeader, SWT.NONE);
		lblMaxMin.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblMaxMin = new FormData();
		fd_lblMaxMin.top = new FormAttachment(lblClassification, 0, SWT.TOP);
		fd_lblMaxMin.left = new FormAttachment(thirdSeparator, 35);
		lblMaxMin.setLayoutData(fd_lblMaxMin);
		lblMaxMin.setText("Max/Min");
		lblMaxMin.setBackground(SWTResourceManager.getColor(230, 230, 230));


		this.presentRow = this.compositeGridHeader;//referring to the header as the 1st row when there are no rows inserted yet
		this.addTimePeriodHeaderColumns(lblMaxMin);

	}

	private void addTimePeriodHeaderColumns(Control reference){
		Control previousColumn = reference;
		for(int i=0; i<this.timePeriod; i++){
			Label separator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
			FormData fd_separator = new FormData();
			fd_separator.left = new FormAttachment(previousColumn, 25);
			separator.setLayoutData(fd_separator);

			Label lblYear = new Label(compositeGridHeader, SWT.NONE);
			lblYear.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
			FormData fd_lblYear = new FormData();
			fd_lblYear.left = new FormAttachment(separator, 25);
			fd_lblYear.top = new FormAttachment(0, 2);
			lblYear.setText(String.valueOf(this.startYear + i));
			lblYear.setBackground(SWTResourceManager.getColor(230, 230, 230));
			lblYear.setLayoutData(fd_lblYear);

			previousColumn = lblYear;
		}
	}

	private void createRows() {
		
		for(ProcessConstraintData pcd : this.processConstraintDataList) {
			final Composite compositeRow = new Composite(this, SWT.BORDER);
			compositeRow.setData(pcd);
			compositeRow.setLayout(new FormLayout());
			Color backgroundColor = SWTResourceManager.getColor(SWT.COLOR_WHITE);
			if((this.allRows != null) && (this.allRows.size()%2 != 0)){
				backgroundColor =  SWTResourceManager.getColor(245, 245, 245);
			}
			compositeRow.setBackground(backgroundColor);
			FormData fd_compositeRow = new FormData();
			fd_compositeRow.left = new FormAttachment(this.presentRow, 0, SWT.LEFT);
			fd_compositeRow.right = new FormAttachment(this.presentRow, 0, SWT.RIGHT);
			fd_compositeRow.top = new FormAttachment(this.presentRow);
			
			final Combo comboExpression = new Combo(compositeRow, SWT.NONE);
			String[] itemsComboExpression = this.getNonGradeExpressionComboItems();
			comboExpression.setItems(itemsComboExpression);
			comboExpression.addListener(SWT.MouseDown, new Listener(){
				@Override
				public void handleEvent(Event event) {
					// TODO Auto-generated method stub
					comboExpression.removeAll();
					comboExpression.setItems(getNonGradeExpressionComboItems());
					comboExpression.getParent().layout();
					comboExpression.setListVisible(true);
				}
			});
			for(int i=0; i< itemsComboExpression.length; i++){
				if(itemsComboExpression[i].equals(pcd.getExpression().getName())) {
					comboExpression.select(i);
					break;
				}
			}
			FormData fd_comboExpression = new FormData();
			fd_comboExpression.left = new FormAttachment(0, 2);
			fd_comboExpression.top = new FormAttachment(0);
			fd_comboExpression.right = new FormAttachment(0, 115);
			comboExpression.setLayoutData(fd_comboExpression);
			
			Button btnUse = new Button(compositeRow, SWT.CHECK);
			btnUse.setSelection(pcd.isInUse());
			FormData fd_btnUse = new FormData();
			fd_btnUse.left = new FormAttachment(comboExpression, 12, SWT.RIGHT);
			fd_btnUse.top = new FormAttachment(0, 2);
			btnUse.setLayoutData(fd_btnUse);
			
			final Combo comboGroup = new Combo(compositeRow, SWT.NONE);
			String[] itemsComboGroup = this.getProcessJoins();
			comboGroup.setItems(itemsComboGroup);
			comboGroup.addListener(SWT.MouseDown, new Listener(){
				@Override
				public void handleEvent(Event event) {
					// TODO Auto-generated method stub
					comboGroup.removeAll();
					comboGroup.setItems(getProcessJoins());
					comboGroup.getParent().layout();
					comboGroup.setListVisible(true);
				}
			});
			for(int i=0; i< itemsComboGroup.length; i++){
				if(itemsComboGroup[i].equals(pcd.getProcessJoin().getName())) {
					comboGroup.select(i);
					break;
				}
			}
			FormData fd_comboGroup = new FormData();
			fd_comboGroup.left = new FormAttachment(btnUse, 18);
			fd_comboGroup.right = new FormAttachment(btnUse, 135);
			fd_comboGroup.top = new FormAttachment(0);
			comboGroup.setLayoutData(fd_comboGroup);
			
			final Combo comboMaxMin = new Combo(compositeRow, SWT.NONE);
			comboMaxMin.setItems(new String[]{"Max", "Min"});
			if(pcd.isMax()){
				comboMaxMin.select(0);
			}else{
				comboMaxMin.select(1);
			}
			FormData fd_comboMaxMin = new FormData();
			fd_comboMaxMin.left = new FormAttachment(comboGroup, 8);
			fd_comboMaxMin.right = new FormAttachment(comboGroup, 116, SWT.RIGHT);
			fd_comboMaxMin.top = new FormAttachment(0);
			comboMaxMin.setLayoutData(fd_comboMaxMin);
			
			Control previousMember = comboMaxMin;
			Map<Integer, Float> yearData = pcd.getConstraintData();
			Set keys = yearData.keySet();
			Iterator<Integer> it = keys.iterator();
			while(it.hasNext()){
				float value = yearData.get(it.next());
				Text yearlyValue = new Text(compositeRow, SWT.BORDER);
				yearlyValue.setText(String.valueOf(value));
				FormData fd_yearlyValue = new FormData();
				
				 // Hacky calculation at the moment
				 
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


		final Combo comboExpression = new Combo(compositeRow, SWT.NONE);
		comboExpression.setItems(this.getNonGradeExpressionComboItems());
		comboExpression.setText("Select Expression");
		comboExpression.addListener(SWT.MouseDown, new Listener(){
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				comboExpression.removeAll();
				comboExpression.setItems(getNonGradeExpressionComboItems());
				comboExpression.getParent().layout();
				comboExpression.setListVisible(true);
			}
		});
		FormData fd_comboExpression = new FormData();
		fd_comboExpression.left = new FormAttachment(0, 2);
		fd_comboExpression.top = new FormAttachment(0);
		fd_comboExpression.right = new FormAttachment(0, 115);
		comboExpression.setLayoutData(fd_comboExpression);

		Button btnUse = new Button(compositeRow, SWT.CHECK);
		FormData fd_btnUse = new FormData();
		fd_btnUse.left = new FormAttachment(comboExpression, 12, SWT.RIGHT);
		fd_btnUse.top = new FormAttachment(0, 2);
		btnUse.setLayoutData(fd_btnUse);

		final Combo comboGroup = new Combo(compositeRow, SWT.NONE);
		String[] items = this.getProcessJoins();
		comboGroup.setItems(items);
		comboGroup.setText("Select Group");
		comboGroup.addListener(SWT.MouseDown, new Listener(){
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				comboGroup.removeAll();
				comboGroup.setItems(getProcessJoins());
				comboGroup.getParent().layout();
				comboGroup.setListVisible(true);
			}
		});
		FormData fd_comboGroup = new FormData();
		fd_comboGroup.left = new FormAttachment(btnUse, 18);
		fd_comboGroup.right = new FormAttachment(btnUse, 135);
		fd_comboGroup.top = new FormAttachment(0);
		comboGroup.setLayoutData(fd_comboGroup);


		final Combo comboMaxMin = new Combo(compositeRow, SWT.NONE);
		comboMaxMin.setItems(new String[]{"Max", "Min"});
		comboMaxMin.setText("Select Max/Min");
		FormData fd_comboMaxMin = new FormData();
		fd_comboMaxMin.left = new FormAttachment(comboGroup, 8);
		fd_comboMaxMin.right = new FormAttachment(comboGroup, 116, SWT.RIGHT);
		fd_comboMaxMin.top = new FormAttachment(0);
		comboMaxMin.setLayoutData(fd_comboMaxMin);

		this.addTimePeriodRowMembers(compositeRow, comboMaxMin);

		this.presentRow = compositeRow;
		this.allRows.add(compositeRow);
		compositeRow.setLayoutData(fd_compositeRow);
		this.layout();
	}

	private void addTimePeriodRowMembers(Composite parent, Control reference){
		Control previousMember = reference;
		for(int i=0; i<this.timePeriod; i++){
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

	public boolean saveProcessConstraintData(){
		for(Composite rowConstraintData: this.allRows){
			Control[] rowChildren = rowConstraintData.getChildren();
			Combo comboExpression = (Combo)rowChildren[0];
			Button isInUse = (Button)rowChildren[1];
			Combo comboGroup = (Combo)rowChildren[2];
			Combo comboMaxMin = (Combo)rowChildren[3];
			LinkedHashMap<Integer, Float> mapConstraintData = new LinkedHashMap<Integer, Float>();
			for(int j=0; j<this.timePeriod; j++){
				mapConstraintData.put((this.startYear + j), Float.valueOf(((Text)rowChildren[4+j]).getText())); // cost input data starts from 4th indexed row child.
			}
			boolean inUse = isInUse.getSelection();
			boolean isMax = (comboMaxMin.getSelectionIndex() == 0);//0=max; 1=min
			String processJoinName = comboGroup.getText();
			String expressionName = comboExpression.getText();
			
			ProcessJoin processJoin = ProjectConfigutration.getInstance().getProcessJoinByName(processJoinName);
			Expression expression = ProjectConfigutration.getInstance().getExpressionByName(expressionName);
			ProcessConstraintData processConstraintData = null;
			
			if(rowConstraintData.getData() == null){
				//new row data, not update of previously saved rowOpexData.
				processConstraintData  = new ProcessConstraintData();
				this.processConstraintDataList.add(processConstraintData);
			}else{
				//update of previously saved rowOpexData.
				processConstraintData = (ProcessConstraintData)rowConstraintData.getData();
			}
			
			
			processConstraintData.setConstraintData(mapConstraintData);
			processConstraintData.setExpression(expression);
			processConstraintData.setInUse(inUse);
			processConstraintData.setMax(isMax);
			processConstraintData.setProcessJoin(processJoin);
			
			//ProjectConfigutration.getInstance().addProcesssConstraintData(processConstraintData);
			
		}
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
