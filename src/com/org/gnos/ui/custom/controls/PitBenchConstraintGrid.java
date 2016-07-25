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
import com.org.gnos.db.model.Pit;
import com.org.gnos.db.model.PitBenchConstraintData;

public class PitBenchConstraintGrid extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private Composite compositeGridHeader;
	private List<Composite> allRows;
	private Composite presentRow;
	private Label firstSeparator;
	private List<PitBenchConstraintData> pitBenchConstraintDataList;
	private int startYear;
	private int timePeriod;

	public PitBenchConstraintGrid(Composite parent, int style) {
		super(parent, style);
		this.allRows = new ArrayList<Composite>();
		this.timePeriod = ScenarioConfigutration.getInstance().getTimePeriod();
		this.startYear = ScenarioConfigutration.getInstance().getStartYear();
		this.pitBenchConstraintDataList = ScenarioConfigutration.getInstance().getPitBenchConstraintDataList();
		this.createContent(parent);
	}

	private void createContent(Composite parent){
		this.setLayout(new FormLayout());
		this.createHeader();
		for(PitBenchConstraintData pitBenchConstraintData : this.pitBenchConstraintDataList){
			this.addRow(pitBenchConstraintData);
		}
		if(this.pitBenchConstraintDataList.size() == 0){
			this.addRow();
		}
	}

	private String[] getPits(){

		ProjectConfigutration projectConfigutration = ProjectConfigutration.getInstance();
		List<Pit> pits = projectConfigutration.getPitList();
		//List<PitGroup> pitGroups = projectConfigutration.getPitGroupList();
		
		String[] comboItems = new String[pits.size()];
		for(int i=0; i < pits.size(); i++){
			comboItems[i] = pits.get(i).getPitName();
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
		
		Label lblPit = new Label(compositeGridHeader, SWT.NONE);
		lblPit.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblPit = new FormData();
		fd_lblPit.top = new FormAttachment(0,2);
		fd_lblPit.left = new FormAttachment(firstSeparator, 35);
		lblPit.setLayoutData(fd_lblPit);
		lblPit.setText("Pit Name");
		lblPit.setBackground(SWTResourceManager.getColor(230, 230, 230));
		this.presentRow = this.compositeGridHeader;//referring to the header as the 1st row when there are no rows inserted yet
		this.addTimePeriodHeaderColumns(lblPit);

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
	
	public void addRow(final PitBenchConstraintData pitBenchConstraintData){
		final Composite compositeRow = new Composite(this, SWT.BORDER);
		compositeRow.setLayout(new FormLayout());
		Color backgroundColor = SWTResourceManager.getColor(SWT.COLOR_WHITE);
		compositeRow.setData(pitBenchConstraintData);
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
		btnUse.setSelection(pitBenchConstraintData.isInUse());
		btnUse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				System.out.println("Is button in use selected: " + btnUse.getSelection());
				pitBenchConstraintData.setInUse(btnUse.getSelection());
			}
		});
		
		final Combo comboPits = new Combo(compositeRow, SWT.NONE);
		comboPits.setItems(this.getPits());
		String associatedPitName  = pitBenchConstraintData.getPitName();
		if(associatedPitName != null){
			comboPits.setText(associatedPitName);
		}else{
			comboPits.setText("Select Pit");
		}
		if(this.allRows.size() == 0){//if first row add default data
			comboPits.setText("Default");
			comboPits.setEnabled(false);
			pitBenchConstraintData.setPitName("Default");
		}else{
			comboPits.addListener(SWT.MouseDown, new Listener(){
				@Override
				public void handleEvent(Event event) {
					// TODO Auto-generated method stub
					comboPits.removeAll();
					comboPits.setItems(getPits());
					comboPits.getParent().layout();
					comboPits.setListVisible(true);
				}
			});

			comboPits.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					String selectedPitName = comboPits.getText();
					pitBenchConstraintData.setPitName(selectedPitName);
				}
			});
		}

		FormData fd_comboPits = new FormData();
		fd_comboPits.left = new FormAttachment(btnUse, 18);
		fd_comboPits.top = new FormAttachment(0);
		fd_comboPits.right = new FormAttachment(0, 150);
		comboPits.setLayoutData(fd_comboPits);
		
		this.addTimePeriodRowMembers(compositeRow, comboPits);
		this.presentRow = compositeRow;
		this.allRows.add(compositeRow);
		compositeRow.setLayoutData(fd_compositeRow);
		this.layout();
		
		
	}

	public void addRow(){
		PitBenchConstraintData pitBenchConstraintData  = new PitBenchConstraintData();
		this.pitBenchConstraintDataList.add(pitBenchConstraintData);
		this.addRow(pitBenchConstraintData);
	}

	private void addTimePeriodRowMembers(final Composite parent, Control reference){
		Control previousMember = reference;
		final PitBenchConstraintData pitBenchConstraintData = (PitBenchConstraintData)parent.getData();
		for(int i=0; i<this.timePeriod; i++){
			Text yearlyValue = new Text(parent, SWT.BORDER);
			final int targetYear = this.startYear + i;
			if(pitBenchConstraintData.getConstraintData().get(targetYear) != null){
				yearlyValue.setText(String.valueOf(pitBenchConstraintData.getConstraintData().get(targetYear)));
			}
			yearlyValue.addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent event) {
					// Get the widget whose text was modified
					Text text = (Text) event.widget;
					System.out.println("Input value for the " + targetYear + " year is " + text.getText());
					//GradeConstraintData gradeConstraintData = (GradeConstraintData)parent.getData();
					LinkedHashMap<Integer, Integer> constraintData = pitBenchConstraintData.getConstraintData();
					constraintData.put(targetYear, Integer.valueOf(text.getText()));
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
