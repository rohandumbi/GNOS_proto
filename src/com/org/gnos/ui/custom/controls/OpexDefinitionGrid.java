package com.org.gnos.ui.custom.controls;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import com.org.gnos.db.model.Scenario;

public class OpexDefinitionGrid extends Composite {

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
	private List<OpexData> existingOpexDataList;
	private Composite parent;
	private List<String> presentmodelNames;
	private Scenario scenario;
	private Label firstSeparator;
	private Label secondSeparator;
	private Label thirdSeparator;
	private Label lblClassification;

	public OpexDefinitionGrid(Composite parent, int style, Scenario scenario) {
		super(parent, style);
		this.parent = parent;
		this.allRows = new ArrayList<Composite>();
		this.existingOpexDataList = ScenarioConfigutration.getInstance().getOpexDataList();
		this.scenario = scenario;
		this.createContent(parent);
	}

	private void createContent(Composite parent){
		this.setLayout(new FormLayout());
		this.createHeader();
		this.createRows();
	}


	private String[] getIdentifierComboItems(){
		//List<Model> models = ProjectConfigutration.getInstance().getModels();
		/*
		 * Allowing objective funtion calculation for leaf nodes only
		 */
		List<Model> models = new ArrayList<Model>();
		List<Node> nodes = ProjectConfigutration.getInstance().getProcessTree().getLeafNodes();
		for(Node node: nodes){
			models.add(node.getData());
		}
		this.sourceFieldsComboItems = new String[models.size()];
		for(int i=0; i<models.size(); i++){
			this.sourceFieldsComboItems[i] = models.get(i).getName();
		}

		return this.sourceFieldsComboItems;
	}

	private String[] getExpressionComboItems(){

		List<Expression> expressions = ProjectConfigutration.getInstance().getNonGradeExpressions();
		this.sourceExpressionComboItems = new String[expressions.size()];
		for(int i=0; i<expressions.size(); i++){
			this.sourceExpressionComboItems[i] = expressions.get(i).getName();
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

		thirdSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_thirdSeparator = new FormData();
		fd_thirdSeparator.left = new FormAttachment(lblIdentifier, 25);
		thirdSeparator.setLayoutData(fd_thirdSeparator);

		Label lblExpression = new Label(compositeGridHeader, SWT.NONE);
		lblExpression.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblExpression = new FormData();
		fd_lblExpression.top = new FormAttachment(lblClassification, 0, SWT.TOP);
		fd_lblExpression.left = new FormAttachment(thirdSeparator, 35);
		lblExpression.setLayoutData(fd_lblExpression);
		lblExpression.setText("Expression");
		lblExpression.setBackground(SWTResourceManager.getColor(230, 230, 230));


		this.presentRow = this.compositeGridHeader;//referring to the header as the 1st row when there are no rows inserted yet
		this.addTimePeriodHeaderColumns(lblExpression);

	}

	private void addTimePeriodHeaderColumns(Control reference){
		Control previousColumn = reference;
		for(int i=0; i<this.scenario.getTimePeriod(); i++){
			Label separator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
			FormData fd_separator = new FormData();
			fd_separator.left = new FormAttachment(previousColumn, 25);
			separator.setLayoutData(fd_separator);

			Label lblYear = new Label(compositeGridHeader, SWT.NONE);
			lblYear.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
			FormData fd_lblYear = new FormData();
			fd_lblYear.left = new FormAttachment(separator, 25);
			fd_lblYear.top = new FormAttachment(0, 2);
			lblYear.setText(String.valueOf(this.scenario.getStartYear() + i));
			lblYear.setBackground(SWTResourceManager.getColor(230, 230, 230));
			lblYear.setLayoutData(fd_lblYear);

			previousColumn = lblYear;
		}
	}

	private void createRows() {
		for(OpexData od: this.existingOpexDataList) {
			this.addRow(od);
		}
	}
	
	private void addRow(final OpexData opexData){
		final Composite compositeRow = new Composite(this, SWT.BORDER);
		boolean isNewOpexData = (opexData.getId() == -1);
		compositeRow.setData(opexData);
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


		final Combo comboClassification = new Combo(compositeRow, SWT.NONE);
		comboClassification.setItems(new String[]{"PCost", "Rev"});
		comboClassification.setText("Select Type");
		FormData fd_comboClassification = new FormData();
		fd_comboClassification.left = new FormAttachment(0, 2);
		fd_comboClassification.top = new FormAttachment(0);
		fd_comboClassification.right = new FormAttachment(0, 89);
		comboClassification.setLayoutData(fd_comboClassification);
		comboClassification.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean isRevenue = (comboClassification.getSelectionIndex() == 1);//0=cost; 1=revenue
				opexData.setRevenue(isRevenue);
			}
		});
		if(!isNewOpexData){
			if(opexData.isRevenue()) {
				comboClassification.select(1);
			} else {
				comboClassification.select(0);
			}
		}

		final Button btnUse = new Button(compositeRow, SWT.CHECK);
		FormData fd_btnUse = new FormData();
		fd_btnUse.left = new FormAttachment(comboClassification, 10, SWT.RIGHT);
		fd_btnUse.top = new FormAttachment(0, 2);
		btnUse.setLayoutData(fd_btnUse);
		btnUse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				System.out.println("Is button in use selected: " + btnUse.getSelection());
				opexData.setInUse(btnUse.getSelection());
			}
		});
		btnUse.setSelection(opexData.isInUse());

		final Combo comboIdentifier = new Combo(compositeRow, SWT.NONE);
		String[] items = this.getIdentifierComboItems();
		comboIdentifier.setItems(items);
		comboIdentifier.setText("Select Model");
		comboIdentifier.addListener(SWT.MouseDown, new Listener(){
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				//System.out.println("detected combo click");
				comboIdentifier.removeAll();
				comboIdentifier.setItems(getIdentifierComboItems());
				comboIdentifier.getParent().layout();
				comboIdentifier.setListVisible(true);
			}
		});
		comboIdentifier.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String modelName = comboIdentifier.getText();
				opexData.setModel(ProjectConfigutration.getInstance().getModelByName(modelName));;
			}
		});
		if(!isNewOpexData){
			String modelName = opexData.getModel().getName();
			comboIdentifier.setText(modelName);
		}
		
		FormData fd_comboIdentifier = new FormData();
		fd_comboIdentifier.left = new FormAttachment(btnUse, 21);
		fd_comboIdentifier.right = new FormAttachment(btnUse, 135);
		fd_comboIdentifier.top = new FormAttachment(0);
		comboIdentifier.setLayoutData(fd_comboIdentifier);


		final Combo comboExpression = new Combo(compositeRow, SWT.NONE);
		String[] expressionItems = this.getExpressionComboItems();
		comboExpression.setItems(expressionItems);
		comboExpression.setText("Select Expression");
		comboExpression.addListener(SWT.MouseDown, new Listener(){
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				//System.out.println("detected combo click");
				comboExpression.removeAll();
				comboExpression.setItems(getExpressionComboItems());
				comboExpression.getParent().layout();
				comboExpression.setListVisible(true);
			}
		});
		if(!isNewOpexData){
			if(!opexData.isRevenue()) {
				comboExpression.setEnabled(false);;
			} else {
				comboExpression.setText(opexData.getExpression().getName());
			}
		}
		comboExpression.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String expressionName = comboExpression.getText();
				opexData.setExpression(ProjectConfigutration.getInstance().getExpressionByName(expressionName));;
			}
		});
		FormData fd_comboExpression = new FormData();
		fd_comboExpression.left = new FormAttachment(comboIdentifier, 4);
		fd_comboExpression.right = new FormAttachment(comboIdentifier, 120, SWT.RIGHT);
		fd_comboExpression.top = new FormAttachment(0);
		comboExpression.setLayoutData(fd_comboExpression);
		
		comboClassification.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (comboClassification.getSelectionIndex() == 0) {
					comboExpression.setEnabled(false);
				}else {
					comboExpression.setEnabled(true);
				}
			}
		});

		this.addTimePeriodRowMembers(compositeRow, comboExpression);

		this.presentRow = compositeRow;
		this.allRows.add(compositeRow);
		compositeRow.setLayoutData(fd_compositeRow);
		this.layout();
	}

	public void addRow(){
		OpexData opexData = new OpexData();
		opexData.setScenarioId(this.scenario.getId());
		this.existingOpexDataList.add(opexData);
		this.addRow(opexData);
	}

	private void addTimePeriodRowMembers(final Composite parent, Control reference){
		Control previousMember = reference;
		final OpexData associatedOpexData = (OpexData)parent.getData();
		final Map<Integer, Float> constraintData = associatedOpexData.getCostData();
		boolean isNewOpexData = (associatedOpexData.getId() == -1);
		for(int i=0; i<this.scenario.getTimePeriod(); i++){
			Text yearlyValue = new Text(parent, SWT.BORDER);
			FormData fd_yearlyValue = new FormData();
			final int targetYear = this.scenario.getStartYear() + i;
			String value = String.valueOf(constraintData.get(targetYear));
			if(!isNewOpexData && value!=null){
				yearlyValue.setText(value);
			}
			yearlyValue.addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent event) {
					// Get the widget whose text was modified
					Text text = (Text) event.widget;
					constraintData.put(targetYear, Float.valueOf(text.getText()));
				}
			});
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
