package com.org.gnos.ui.custom.controls;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.core.ScenarioConfigutration;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.GradeConstraintData;
import com.org.gnos.db.model.Pit;
import com.org.gnos.db.model.PitGroup;
import com.org.gnos.db.model.Process;
import com.org.gnos.db.model.ProcessJoin;
import com.org.gnos.db.model.Product;
import com.org.gnos.db.model.ProductJoin;

public class GradeConstraintGrid extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private Composite compositeGridHeader;
	private List<Composite> allRows;
	private Composite presentRow;
	private Label firstSeparator;
	private Label secondSeparator;
	private Label thirdSeparator;
	private Label fourthSeparator;
	private Label lblClassification;
	private List<GradeConstraintData> gradeConstraintDataList;
	private int startYear;
	private int timePeriod;

	private int expressionEndIndex = 0;
	private int productEndIndex = 0;
	private int productJoinEndIndex = 0;

	private int processEndIndex = 0;
	private int processJoinEndIndex = 0;
	private int pitEndIndex = 0;
	private int pitGroupEndIndex = 0;

	public GradeConstraintGrid(Composite parent, int style) {
		super(parent, style);
		this.allRows = new ArrayList<Composite>();
		this.timePeriod = ScenarioConfigutration.getInstance().getTimePeriod();
		this.startYear = ScenarioConfigutration.getInstance().getStartYear();
		this.gradeConstraintDataList = ScenarioConfigutration.getInstance().getGradeConstraintDataList();
		this.createContent(parent);
	}

	private void createContent(Composite parent){
		this.setLayout(new FormLayout());
		this.createHeader();
		//this.createRows();
		for(GradeConstraintData gradeConstraintData : this.gradeConstraintDataList){
			this.addRow(gradeConstraintData);
		}
	}


	/*private String[] getCoefficientComboItems(){
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
	}*/

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

		Label lblUse = new Label(compositeGridHeader, SWT.NONE);
		lblUse.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblUse = new FormData();
		fd_lblUse.top = new FormAttachment(0,2);
		fd_lblUse.left = new FormAttachment(0, 10);
		lblUse.setLayoutData(fd_lblUse);
		lblUse.setText("Use");
		lblUse.setBackground(SWTResourceManager.getColor(230, 230, 230));

		firstSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_firstSeparator = new FormData();
		fd_firstSeparator.left = new FormAttachment(lblUse, 10);
		firstSeparator.setLayoutData(fd_firstSeparator);
		
		Label lblProductJoin = new Label(compositeGridHeader, SWT.NONE);
		lblProductJoin.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblProductJoin = new FormData();
		fd_lblProductJoin.top = new FormAttachment(0,2);
		fd_lblProductJoin.left = new FormAttachment(firstSeparator, 35);
		lblProductJoin.setLayoutData(fd_lblProductJoin);
		lblProductJoin.setText("Product Join");
		lblProductJoin.setBackground(SWTResourceManager.getColor(230, 230, 230));

		secondSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_secondSeparator = new FormData();
		fd_secondSeparator.left = new FormAttachment(lblProductJoin, 35);
		secondSeparator.setLayoutData(fd_secondSeparator);
		
		Label lblGrades = new Label(compositeGridHeader, SWT.NONE);
		lblGrades.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblGrades = new FormData();
		fd_lblGrades.top = new FormAttachment(0,2);
		fd_lblGrades.left = new FormAttachment(secondSeparator, 55);
		lblGrades.setLayoutData(fd_lblGrades);
		lblGrades.setText("Grade");
		lblGrades.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		thirdSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_thirdSeparator = new FormData();
		fd_thirdSeparator.left = new FormAttachment(lblGrades, 50);
		thirdSeparator.setLayoutData(fd_thirdSeparator);

		Label lblGrouping = new Label(compositeGridHeader, SWT.NONE);
		lblGrouping.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblGrouping = new FormData();
		fd_lblGrouping.top = new FormAttachment(0,2);
		fd_lblGrouping.left = new FormAttachment(thirdSeparator, 35);
		lblGrouping.setLayoutData(fd_lblGrouping);
		lblGrouping.setText("Grouping");
		lblGrouping.setBackground(SWTResourceManager.getColor(230, 230, 230));

		fourthSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_fourthSeparator = new FormData();
		fd_fourthSeparator.left = new FormAttachment(lblGrouping, 30);
		fourthSeparator.setLayoutData(fd_fourthSeparator);

		Label lblMaxMin = new Label(compositeGridHeader, SWT.NONE);
		lblMaxMin.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblMaxMin = new FormData();
		fd_lblMaxMin.top = new FormAttachment(0,2);
		fd_lblMaxMin.left = new FormAttachment(fourthSeparator, 30);
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
	
	private String[] getProductJoinsWithGrades(){
		List<ProductJoin> listOfProductJoinsWithGrades = ProjectConfigutration.getInstance().getProductJoinWithGrades();
		String[] namesOfProductJoinsWithGrades = new String[listOfProductJoinsWithGrades.size()];
		for(int i=0; i<listOfProductJoinsWithGrades.size(); i++){
			namesOfProductJoinsWithGrades[i] = listOfProductJoinsWithGrades.get(i).getName();
		}
		return namesOfProductJoinsWithGrades;
	}
	
	public void addRow(final GradeConstraintData gradeConstraintData){
		final Composite compositeRow = new Composite(this, SWT.BORDER);
		compositeRow.setLayout(new FormLayout());
		Color backgroundColor = SWTResourceManager.getColor(SWT.COLOR_WHITE);
		compositeRow.setData(gradeConstraintData);
		if((this.allRows != null) && (this.allRows.size()%2 != 0)){
			backgroundColor =  SWTResourceManager.getColor(245, 245, 245);
		}

		compositeRow.setBackground(backgroundColor);
		FormData fd_compositeRow = new FormData();
		fd_compositeRow.left = new FormAttachment(this.presentRow, 0, SWT.LEFT);
		//fd_compositeRow.bottom = new FormAttachment(this.presentRow, 26, SWT.BOTTOM);
		fd_compositeRow.right = new FormAttachment(this.presentRow, 0, SWT.RIGHT);
		fd_compositeRow.top = new FormAttachment(this.presentRow);
		
		final Button btnUse = new Button(compositeRow, SWT.CHECK);
		FormData fd_btnUse = new FormData();
		fd_btnUse.left = new FormAttachment(0,10);
		fd_btnUse.top = new FormAttachment(0, 2);
		btnUse.setLayoutData(fd_btnUse);
		btnUse.setSelection(gradeConstraintData.isInUse());
		btnUse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				System.out.println("Is button in use selected: " + btnUse.getSelection());
				gradeConstraintData.setInUse(btnUse.getSelection());
			}
		});
		
		final Combo comboProductJoins = new Combo(compositeRow, SWT.NONE);
		comboProductJoins.setItems(this.getProductJoinsWithGrades());
		String associatedProductJoinName  = gradeConstraintData.getProductJoinName();
		if(associatedProductJoinName != null){
			comboProductJoins.setText(associatedProductJoinName);
		}else{
			comboProductJoins.setText("Select Join");
		}
		
		
		final Combo comboAvailableGrades = new Combo(compositeRow, SWT.NONE);
		String associatedGradeName = gradeConstraintData.getSelectedGradeName();
		if(associatedGradeName != null){
			ProductJoin selectedProductJoin = ProjectConfigutration.getInstance().getProductJoinByName(associatedProductJoinName);
			comboAvailableGrades.setItems(selectedProductJoin.getGradeNames().toArray(new String[0]));
			comboAvailableGrades.setText(associatedGradeName);
		}else{
			comboAvailableGrades.setText("Select Join");
		}
		
		comboProductJoins.addListener(SWT.MouseDown, new Listener(){
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				comboProductJoins.removeAll();
				comboProductJoins.setItems(getProductJoinsWithGrades());
				comboProductJoins.getParent().layout();
				comboProductJoins.setListVisible(true);
			}
		});

		comboProductJoins.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectedProductJoinName = comboProductJoins.getText();
				gradeConstraintData.setProductJoinName(selectedProductJoinName);
				ProductJoin selectedProductJoin = ProjectConfigutration.getInstance().getProductJoinByName(selectedProductJoinName);
				comboAvailableGrades.setItems(selectedProductJoin.getGradeNames().toArray(new String[0]));
				comboAvailableGrades.select(0);
				gradeConstraintData.setSelectedGradeName(comboAvailableGrades.getText());
			}
		});
		
		comboAvailableGrades.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				gradeConstraintData.setSelectedGradeIndex(comboAvailableGrades.getSelectionIndex());
				gradeConstraintData.setSelectedGradeName(comboAvailableGrades.getText());
			}
		});

		FormData fd_comboProductJoins = new FormData();
		fd_comboProductJoins.left = new FormAttachment(btnUse, 18);
		fd_comboProductJoins.top = new FormAttachment(0);
		fd_comboProductJoins.right = new FormAttachment(0, 175);
		comboProductJoins.setLayoutData(fd_comboProductJoins);
		
		FormData fd_comboAvailableGrades = new FormData();
		fd_comboAvailableGrades.left = new FormAttachment(comboProductJoins, 10, SWT.RIGHT);
		fd_comboAvailableGrades.top = new FormAttachment(0);
		fd_comboAvailableGrades.right = new FormAttachment(comboProductJoins, 140, SWT.RIGHT);
		comboAvailableGrades.setLayoutData(fd_comboAvailableGrades);
		
		final Combo comboGroup = new Combo(compositeRow, SWT.NONE);
		String[] availableGroupingNames = this.getSelectors();
		comboGroup.setItems(availableGroupingNames);
		
		String associatedSelectorName = gradeConstraintData.getSelectorName();
		if(associatedSelectorName != null){
			comboGroup.setText(associatedSelectorName);
		}else{
			comboGroup.setText("Select Group");
		}
		comboGroup.addListener(SWT.MouseDown, new Listener(){
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				comboGroup.removeAll();
				comboGroup.setItems(getSelectors());
				comboGroup.getParent().layout();
				comboGroup.setListVisible(true);
				gradeConstraintData.setSelectionType(GradeConstraintData.SELECTION_NONE);
			}
		});

		comboGroup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectorName = comboGroup.getText();
				System.out.println("Group selected is: " + selectorName);
				int selectorSelectionIndex = comboGroup.getSelectionIndex();
				if(selectorSelectionIndex < 0) {
					gradeConstraintData.setSelectionType(GradeConstraintData.SELECTION_NONE);
				}else if(selectorSelectionIndex <= processJoinEndIndex ) {
					gradeConstraintData.setSelectionType(GradeConstraintData.SELECTION_PROCESS_JOIN);
				} else if(selectorSelectionIndex <= processEndIndex ) {
					gradeConstraintData.setSelectionType(GradeConstraintData.SELECTION_PROCESS);
				} else if(selectorSelectionIndex <= pitEndIndex ) {
					gradeConstraintData.setSelectionType(GradeConstraintData.SELECTION_PIT);
				} else {
					gradeConstraintData.setSelectionType(GradeConstraintData.SELECTION_PIT_GROUP);
				}

				gradeConstraintData.setSelectorName(selectorName);
			}
		});

		FormData fd_comboGroup = new FormData();
		fd_comboGroup.left = new FormAttachment(comboAvailableGrades, 2, SWT.RIGHT);
		fd_comboGroup.right = new FormAttachment(comboAvailableGrades, 115, SWT.RIGHT);
		fd_comboGroup.top = new FormAttachment(0);
		comboGroup.setLayoutData(fd_comboGroup);
		
		final Combo comboMaxMin = new Combo(compositeRow, SWT.NONE);
		comboMaxMin.setItems(new String[]{"Max", "Min"});
		if(gradeConstraintData.isMax() == true){
			comboMaxMin.select(0);
		}else{
			comboMaxMin.select(1);
		}
		FormData fd_comboMaxMin = new FormData();
		fd_comboMaxMin.left = new FormAttachment(comboGroup, 4);
		fd_comboMaxMin.right = new FormAttachment(comboGroup, 108, SWT.RIGHT);
		fd_comboMaxMin.top = new FormAttachment(0);
		comboMaxMin.setLayoutData(fd_comboMaxMin);

		comboMaxMin.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				System.out.println("Is equation for max value: " + (comboMaxMin.getSelectionIndex() == 0));
				boolean isMax = (comboMaxMin.getSelectionIndex() == 0);//0=max; 1=min
				gradeConstraintData.setMax(isMax);
			}
		});

		this.addTimePeriodRowMembers(compositeRow, comboMaxMin);

		this.presentRow = compositeRow;
		this.allRows.add(compositeRow);
		compositeRow.setLayoutData(fd_compositeRow);
		this.layout();
		
		
	}

	public void addRow(){
		GradeConstraintData gradeConstraintData  = new GradeConstraintData();
		this.gradeConstraintDataList.add(gradeConstraintData);
		this.addRow(gradeConstraintData);
	}

	private void addTimePeriodRowMembers(final Composite parent, Control reference){
		Control previousMember = reference;
		final GradeConstraintData gradeConstraintData = (GradeConstraintData)parent.getData();
		for(int i=0; i<this.timePeriod; i++){
			Text yearlyValue = new Text(parent, SWT.BORDER);
			final int targetYear = this.startYear + i;
			if(gradeConstraintData.getConstraintData().get(targetYear) != null){
				yearlyValue.setText(String.valueOf(gradeConstraintData.getConstraintData().get(targetYear)));
			}
			yearlyValue.addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent event) {
					// Get the widget whose text was modified
					Text text = (Text) event.widget;
					System.out.println("Input value for the " + targetYear + " year is " + text.getText());
					//GradeConstraintData gradeConstraintData = (GradeConstraintData)parent.getData();
					LinkedHashMap<Integer, Float> constraintData = gradeConstraintData.getConstraintData();
					constraintData.put(targetYear, Float.valueOf(text.getText()));
				}
			});
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
