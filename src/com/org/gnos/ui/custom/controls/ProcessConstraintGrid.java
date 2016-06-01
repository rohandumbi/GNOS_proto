package com.org.gnos.ui.custom.controls;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.core.ScenarioConfigutration;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.OpexData;
import com.org.gnos.db.model.Pit;
import com.org.gnos.db.model.PitGroup;
import com.org.gnos.db.model.Process;
import com.org.gnos.db.model.ProcessConstraintData;
import com.org.gnos.db.model.ProcessJoin;
import com.org.gnos.db.model.Product;
import com.org.gnos.db.model.ProductJoin;
import com.org.gnos.services.TimePeriod;

public class ProcessConstraintGrid extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private Composite compositeGridHeader;
	private List<Composite> allRows;
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

	private int expressionEndIndex = 0;
	private int productEndIndex = 0;
	private int productJoinEndIndex = 0;
	
	private int processEndIndex = 0;
	private int processJoinEndIndex = 0;
	private int pitEndIndex = 0;
	private int pitGroupEndIndex = 0;

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


	private String[] getCoefficientComboItems(){
		ProjectConfigutration projectConfigutration = ProjectConfigutration.getInstance();
		List<Expression> expressions = projectConfigutration.getNonGradeExpressions();
		List<Product> products = projectConfigutration.getProductList();
		List<ProductJoin> productJoins = projectConfigutration.getProductJoinList();
		this.expressionEndIndex = expressions.size() -1;
		this.productEndIndex = this.expressionEndIndex + products.size();
		this.productJoinEndIndex = this.productEndIndex + productJoins.size();
		String[] comboItems = new String[this.productJoinEndIndex+1];
		for(int i=0; i< expressions.size() ; i++){
			comboItems[i] = expressions.get(i).getName();
		}
		for(int i=0; i < products.size(); i++){
			comboItems[this.expressionEndIndex +i+1] = products.get(i).getName();
		}
		for(int i=0; i < productJoins.size(); i++){
			comboItems[this.productEndIndex+ i +1] = productJoins.get(i).getName();
		}
		return comboItems;
	}
	
	private String[] getSelectors(){
		
		ProjectConfigutration projectConfigutration = ProjectConfigutration.getInstance();
			
		List<ProcessJoin> processJoins = projectConfigutration.getProcessJoins();
		List<Process> processes = projectConfigutration.getProcessList();
		List<Pit> pits = projectConfigutration.getPitList();
		List<PitGroup> pitGroups = projectConfigutration.getPitGroupList();
		this.processJoinEndIndex = processJoins.size() -1;
		this.processEndIndex = this.processJoinEndIndex + processes.size();
		this.pitEndIndex = this.processEndIndex + pits.size();
		this.pitGroupEndIndex = this.pitEndIndex + pitGroups.size();
		String[] comboItems = new String[this.pitGroupEndIndex+1];
		for(int i=0; i < processJoins.size(); i++){
			comboItems[i] = processJoins.get(i).getName();
		}
		for(int i=0; i < processes.size(); i++){
			comboItems[this.processJoinEndIndex + i +1] = processes.get(i).getModel().getName();
		}
		for(int i=0; i < pits.size(); i++){
			comboItems[this.processEndIndex + i +1] = pits.get(i).getPitName();
		}
		for(int i=0; i < pitGroups.size(); i++){
			comboItems[this.pitEndIndex +i +1] = pitGroups.get(i).getName();
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
			String[] itemsComboExpression = this.getCoefficientComboItems();
			comboExpression.setItems(itemsComboExpression);
			comboExpression.addListener(SWT.MouseDown, new Listener(){
				@Override
				public void handleEvent(Event event) {
					// TODO Auto-generated method stub
					comboExpression.removeAll();
					comboExpression.setItems(getCoefficientComboItems());
					comboExpression.getParent().layout();
					comboExpression.setListVisible(true);
				}
			});
			int start = 0;
			int end = this.expressionEndIndex;
			if(pcd.getCoefficientType() == ProcessConstraintData.COEFFICIENT_PRODUCT) {
				start = this.expressionEndIndex +1 ;
				end = this.productEndIndex;
			} else if (pcd.getCoefficientType() == ProcessConstraintData.COEFFICIENT_PRODUCT_JOIN) {
				start = this.productEndIndex +1 ;
				end = this.productJoinEndIndex;
			}
			for(; start <= end; start++){
				if(itemsComboExpression[start].equals(pcd.getCoefficient_name())) {
					comboExpression.select(start);
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
			String[] itemsComboGroup = this.getSelectors();
			comboGroup.setItems(itemsComboGroup);
			comboGroup.addListener(SWT.MouseDown, new Listener(){
				@Override
				public void handleEvent(Event event) {
					// TODO Auto-generated method stub
					comboGroup.removeAll();
					comboGroup.setItems(getSelectors());
					comboGroup.getParent().layout();
					comboGroup.setListVisible(true);
				}
			});
			start = 0;
			end = this.processJoinEndIndex;
			if(pcd.getSelectionType() == ProcessConstraintData.SELECTION_PROCESS) {
				start = this.processJoinEndIndex +1 ;
				end = this.processEndIndex;
			} else if (pcd.getSelectionType() == ProcessConstraintData.SELECTION_PIT) {
				start = this.processEndIndex +1 ;
				end = this.pitEndIndex;
			} else if (pcd.getSelectionType() == ProcessConstraintData.SELECTION_PIT_GROUP) {
				start = this.pitEndIndex +1 ;
				end = this.pitGroupEndIndex;
			}
			for(; start <= end; start++){
				if(itemsComboGroup[start].equals(pcd.getSelector_name())) {
					comboGroup.select(start);
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
		comboExpression.setItems(this.getCoefficientComboItems());
		comboExpression.setText("Select Expression");
		comboExpression.addListener(SWT.MouseDown, new Listener(){
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				comboExpression.removeAll();
				comboExpression.setItems(getCoefficientComboItems());
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
		String[] items = this.getSelectors();
		comboGroup.setItems(items);
		comboGroup.setText("Select Group");
		comboGroup.addListener(SWT.MouseDown, new Listener(){
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				comboGroup.removeAll();
				comboGroup.setItems(getSelectors());
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
			String selectorName = comboGroup.getText();
			String coefficientName = comboExpression.getText();
			int coefficientSelectionIndex = comboExpression.getSelectionIndex();
			int selectorSelectionIndex = comboGroup.getSelectionIndex();
			
			ProcessConstraintData processConstraintData = null;
			
			if(rowConstraintData.getData() == null){
				//new row data, not update of previously saved rowOpexData.
				processConstraintData  = new ProcessConstraintData();
				this.processConstraintDataList.add(processConstraintData);
				rowConstraintData.setData(processConstraintData);
			}else{
				//update of previously saved rowOpexData.
				processConstraintData = (ProcessConstraintData)rowConstraintData.getData();
			}
			
			
			processConstraintData.setConstraintData(mapConstraintData);
			if(coefficientSelectionIndex <= this.expressionEndIndex) {
				processConstraintData.setCoefficientType(ProcessConstraintData.COEFFICIENT_EXPRESSION);
			} else if(coefficientSelectionIndex <= this.productEndIndex) {
				processConstraintData.setCoefficientType(ProcessConstraintData.COEFFICIENT_PRODUCT);
			} else {
				processConstraintData.setCoefficientType(ProcessConstraintData.COEFFICIENT_PRODUCT_JOIN);
			}		
			processConstraintData.setCoefficient_name(coefficientName);
			if(selectorSelectionIndex <= 0) {
				processConstraintData.setSelectionType(ProcessConstraintData.SELECTION_NONE);
			}
			else if(selectorSelectionIndex <= processJoinEndIndex ) {
				processConstraintData.setSelectionType(ProcessConstraintData.SELECTION_PROCESS_JOIN);
			} else if(selectorSelectionIndex <= processEndIndex ) {
				processConstraintData.setSelectionType(ProcessConstraintData.SELECTION_PROCESS);
			} else if(selectorSelectionIndex <= pitEndIndex ) {
				processConstraintData.setSelectionType(ProcessConstraintData.SELECTION_PIT);
			} else {
				processConstraintData.setSelectionType(ProcessConstraintData.SELECTION_PIT_GROUP);
			}

			processConstraintData.setSelector_name(selectorName);
			processConstraintData.setInUse(inUse);
			processConstraintData.setMax(isMax);
			
			
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
