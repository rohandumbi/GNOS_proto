package com.org.gnos.ui.custom.controls;

import java.util.ArrayList;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.core.ScenarioConfigutration;
import com.org.gnos.db.model.Pit;
import com.org.gnos.db.model.PitBenchConstraintData;
import com.org.gnos.db.model.PitDependencyData;

public class PitDependencyGrid extends Composite {

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
	private Label fifthSeparator;
	private Label sixthSeparator;
	private Label seventhSeparator;
	private Label eigthSeparator;
	private List<PitDependencyData> pitDependencyDataList;

	public PitDependencyGrid(Composite parent, int style) {
		super(parent, style);
		this.allRows = new ArrayList<Composite>();
		this.pitDependencyDataList = ScenarioConfigutration.getInstance().getPitDependencyDataList();
		this.createContent(parent);
	}

	private void createContent(Composite parent){
		this.setLayout(new FormLayout());
		this.createHeader();
		for(PitDependencyData pitDependencyData : this.pitDependencyDataList){
			this.addRow(pitDependencyData);
		}
	}

	private String[] getPits(){

		ProjectConfigutration projectConfigutration = ProjectConfigutration.getInstance();
		List<Pit> pits = projectConfigutration.getPitList();
		String[] comboItems = new String[pits.size()];
		for(int i=0; i < pits.size(); i++){
			comboItems[i] = pits.get(i).getPitName();
		}
		return comboItems;
	}
	
	private String[] getBenchesForPitName(String pitName){

		ProjectConfigutration projectConfigutration = ProjectConfigutration.getInstance();
		List<String> benchNames = projectConfigutration.getBenchNamesAssociatedWithPit(pitName);
		String[] comboItems = new String[benchNames.size()];
		for(int i=0; i < benchNames.size(); i++){
			comboItems[i] = benchNames.get(i);
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
		
		Label lblFirstPit = new Label(compositeGridHeader, SWT.NONE);
		lblFirstPit.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblFirstPit = new FormData();
		fd_lblFirstPit.top = new FormAttachment(0,2);
		fd_lblFirstPit.left = new FormAttachment(firstSeparator, 35);
		lblFirstPit.setLayoutData(fd_lblFirstPit);
		lblFirstPit.setText("First Pit");
		lblFirstPit.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		secondSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_secondSeparator = new FormData();
		fd_secondSeparator.left = new FormAttachment(lblFirstPit, 35);
		secondSeparator.setLayoutData(fd_secondSeparator);
		
		Label lblFirstPitAssociatedBench = new Label(compositeGridHeader, SWT.NONE);
		lblFirstPitAssociatedBench.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblFirstPitAssociatedBench = new FormData();
		fd_lblFirstPitAssociatedBench.top = new FormAttachment(0,2);
		fd_lblFirstPitAssociatedBench.left = new FormAttachment(secondSeparator, 35);
		lblFirstPitAssociatedBench.setLayoutData(fd_lblFirstPitAssociatedBench);
		lblFirstPitAssociatedBench.setText("Bench Name");
		lblFirstPitAssociatedBench.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		thirdSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_thirdSeparator = new FormData();
		fd_thirdSeparator.left = new FormAttachment(lblFirstPitAssociatedBench, 35);
		thirdSeparator.setLayoutData(fd_thirdSeparator);
		
		Label lblDependentPit = new Label(compositeGridHeader, SWT.NONE);
		lblDependentPit.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblDependentPit = new FormData();
		fd_lblDependentPit.top = new FormAttachment(0,2);
		fd_lblDependentPit.left = new FormAttachment(thirdSeparator, 35);
		lblDependentPit.setLayoutData(fd_lblDependentPit);
		lblDependentPit.setText("Dependent Pit");
		lblDependentPit.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		fourthSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_fourthSeparator = new FormData();
		fd_fourthSeparator.left = new FormAttachment(lblDependentPit, 35);
		fourthSeparator.setLayoutData(fd_fourthSeparator);
		
		Label lblDependentPitAssociatedBench = new Label(compositeGridHeader, SWT.NONE);
		lblDependentPitAssociatedBench.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblDependentPitAssociatedBench = new FormData();
		fd_lblDependentPitAssociatedBench.top = new FormAttachment(0,2);
		fd_lblDependentPitAssociatedBench.left = new FormAttachment(fourthSeparator, 35);
		lblDependentPitAssociatedBench.setLayoutData(fd_lblDependentPitAssociatedBench);
		lblDependentPitAssociatedBench.setText("Bench Name");
		lblDependentPitAssociatedBench.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		fifthSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_fifthSeparator = new FormData();
		fd_fifthSeparator.left = new FormAttachment(lblDependentPitAssociatedBench, 35);
		fifthSeparator.setLayoutData(fd_fifthSeparator);
		
		Label lblMinLead = new Label(compositeGridHeader, SWT.NONE);
		lblMinLead.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblMinLead = new FormData();
		fd_lblMinLead.top = new FormAttachment(0,2);
		fd_lblMinLead.left = new FormAttachment(fifthSeparator, 20);
		lblMinLead.setLayoutData(fd_lblMinLead);
		lblMinLead.setText("Min Lead");
		lblMinLead.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		sixthSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_sixthSeparator = new FormData();
		fd_sixthSeparator.left = new FormAttachment(lblMinLead, 20);
		sixthSeparator.setLayoutData(fd_sixthSeparator);
		
		Label lblMaxLead = new Label(compositeGridHeader, SWT.NONE);
		lblMaxLead.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblMaxLead = new FormData();
		fd_lblMaxLead.top = new FormAttachment(0,2);
		fd_lblMaxLead.left = new FormAttachment(sixthSeparator, 20);
		lblMaxLead.setLayoutData(fd_lblMaxLead);
		lblMaxLead.setText("Max Lead");
		lblMaxLead.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		seventhSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_seventhSeparator = new FormData();
		fd_seventhSeparator.left = new FormAttachment(lblMaxLead, 20);
		seventhSeparator.setLayoutData(fd_seventhSeparator);
		
		Label lblDescription = new Label(compositeGridHeader, SWT.NONE);
		lblDescription.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblDescription = new FormData();
		fd_lblDescription.top = new FormAttachment(0,2);
		fd_lblDescription.left = new FormAttachment(seventhSeparator, 200);
		lblDescription.setLayoutData(fd_lblDescription);
		lblDescription.setText("Description");
		lblDescription.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		eigthSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_eigthSeparator = new FormData();
		fd_eigthSeparator.left = new FormAttachment(lblDescription, 200);
		eigthSeparator.setLayoutData(fd_eigthSeparator);
		
		
		
		this.presentRow = this.compositeGridHeader;//referring to the header as the 1st row when there are no rows inserted yet
		//this.addTimePeriodHeaderColumns(lblPit);

	}

	/*private void addTimePeriodHeaderColumns(Control reference){
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
	}*/
	
	public void addRow(final PitDependencyData pitDependencyData){
		final Composite compositeRow = new Composite(this, SWT.BORDER);
		compositeRow.setLayout(new FormLayout());
		Color backgroundColor = SWTResourceManager.getColor(SWT.COLOR_WHITE);
		compositeRow.setData(pitDependencyData);
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
		btnUse.setSelection(pitDependencyData.isInUse());
		btnUse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				System.out.println("Is button in use selected: " + btnUse.getSelection());
				pitDependencyData.setInUse(btnUse.getSelection());
			}
		});
		
		final Combo comboFirstPits = new Combo(compositeRow, SWT.NONE);
		comboFirstPits.setItems(this.getPits());
		String associatedFirstPitName  = pitDependencyData.getFirstPitName();
		FormData fd_comboFirstPits = new FormData();
		fd_comboFirstPits.left = new FormAttachment(btnUse, 18);
		fd_comboFirstPits.top = new FormAttachment(0);
		fd_comboFirstPits.right = new FormAttachment(0, 150);
		comboFirstPits.setLayoutData(fd_comboFirstPits);
		
		final Combo comboAssociatedFirstPitBenches = new Combo(compositeRow, SWT.NONE);
		String accociatedFirstPitBenchName = pitDependencyData.getFirstPitAssociatedBench();
		FormData fd_comboAssociatedFirstPitBenches = new FormData();
		fd_comboAssociatedFirstPitBenches.left = new FormAttachment(comboFirstPits, 2, SWT.RIGHT);
		fd_comboAssociatedFirstPitBenches.top = new FormAttachment(0);
		fd_comboAssociatedFirstPitBenches.right = new FormAttachment(comboFirstPits, 140, SWT.RIGHT);
		comboAssociatedFirstPitBenches.setLayoutData(fd_comboAssociatedFirstPitBenches);
		
		
		if(associatedFirstPitName != null){
			comboFirstPits.setText(associatedFirstPitName);
			comboAssociatedFirstPitBenches.setItems(this.getBenchesForPitName(associatedFirstPitName));
		}else{
			comboFirstPits.setText("Select Pit");
		}
		
		if(accociatedFirstPitBenchName != null){
			comboAssociatedFirstPitBenches.setText(accociatedFirstPitBenchName);
		}else{
			comboAssociatedFirstPitBenches.setText("Select First Pit");
		}
		
		comboFirstPits.addListener(SWT.MouseDown, new Listener(){
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				comboFirstPits.removeAll();
				comboFirstPits.setItems(getPits());
				comboFirstPits.getParent().layout();
				comboFirstPits.setListVisible(true);
			}
		});

		comboFirstPits.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectedPitName = comboFirstPits.getText();
				pitDependencyData.setFirstPitName(selectedPitName);
				comboAssociatedFirstPitBenches.setItems(getBenchesForPitName(selectedPitName));
			}
		});
		
		comboAssociatedFirstPitBenches.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectedBenchName = comboAssociatedFirstPitBenches.getText();
				pitDependencyData.setFirstPitAssociatedBench(selectedBenchName);
			}
		});
		
		final Combo comboDependentPits = new Combo(compositeRow, SWT.NONE);
		comboDependentPits.setItems(this.getPits());
		String associatedDependentPitName  = pitDependencyData.getDependentPitName();
		FormData fd_comboDependentPits = new FormData();
		fd_comboDependentPits.left = new FormAttachment(comboAssociatedFirstPitBenches, 2, SWT.RIGHT);
		fd_comboDependentPits.top = new FormAttachment(0);
		fd_comboDependentPits.right = new FormAttachment(comboAssociatedFirstPitBenches, 148, SWT.RIGHT);
		comboDependentPits.setLayoutData(fd_comboDependentPits);
		
		final Combo comboAssociatedDependentPitBenches = new Combo(compositeRow, SWT.NONE);
		String accociatedDependentPitBenchName = pitDependencyData.getDependentPitAssociatedBench();
		FormData fd_comboAssociatedDependentPitBenches = new FormData();
		fd_comboAssociatedDependentPitBenches.left = new FormAttachment(comboDependentPits, 2, SWT.RIGHT);
		fd_comboAssociatedDependentPitBenches.top = new FormAttachment(0);
		fd_comboAssociatedDependentPitBenches.right = new FormAttachment(comboDependentPits, 140, SWT.RIGHT);
		comboAssociatedDependentPitBenches.setLayoutData(fd_comboAssociatedDependentPitBenches);
		
		if(associatedDependentPitName != null){
			comboDependentPits.setText(associatedDependentPitName);
		}else{
			comboDependentPits.setText("Select Pit");
		}
		
		if(accociatedDependentPitBenchName != null){
			comboAssociatedDependentPitBenches.setText(accociatedDependentPitBenchName);
		}else{
			comboAssociatedDependentPitBenches.setText("Select Dependent Pit");
		}

		comboDependentPits.addListener(SWT.MouseDown, new Listener(){
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				comboDependentPits.removeAll();
				comboDependentPits.setItems(getPits());
				comboDependentPits.getParent().layout();
				comboDependentPits.setListVisible(true);
			}
		});

		comboDependentPits.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectedPitName = comboDependentPits.getText();
				pitDependencyData.setDependentPitName(selectedPitName);
				comboAssociatedDependentPitBenches.setItems(getBenchesForPitName(selectedPitName));
			}
		});
		
		comboAssociatedDependentPitBenches.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectedBenchName = comboAssociatedDependentPitBenches.getText();
				pitDependencyData.setDependentPitAssociatedBench(selectedBenchName);
			}
		});
		
		final Text textMinLead = new Text(compositeRow, SWT.BORDER);
		int associatedMinLead = pitDependencyData.getMinLead();
		if(associatedMinLead != -1){
			textMinLead.setText(String.valueOf(associatedMinLead));
		}
		FormData fd_textMinLead = new FormData();
		fd_textMinLead.left = new FormAttachment(comboAssociatedDependentPitBenches, 2, SWT.RIGHT);
		fd_textMinLead.top = new FormAttachment(0);
		fd_textMinLead.right = new FormAttachment(comboAssociatedDependentPitBenches, 92, SWT.RIGHT);
		textMinLead.setLayoutData(fd_textMinLead);
		textMinLead.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent event) {
				// Get the widget whose text was modified
				pitDependencyData.setMinLead(Integer.valueOf(textMinLead.getText()));
			}
		});
		
		final Text textMaxLead = new Text(compositeRow, SWT.BORDER);
		int associatedMaxLead = pitDependencyData.getMaxLead();
		if(associatedMaxLead != -1){
			textMaxLead.setText(String.valueOf(associatedMaxLead));
		}
		FormData fd_textMaxLead = new FormData();
		fd_textMaxLead.left = new FormAttachment(textMinLead, 2, SWT.RIGHT);
		fd_textMaxLead.top = new FormAttachment(0);
		fd_textMaxLead.right = new FormAttachment(textMinLead, 92, SWT.RIGHT);
		textMaxLead.setLayoutData(fd_textMaxLead);
		textMaxLead.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent event) {
				// Get the widget whose text was modified
				pitDependencyData.setMaxLead(Integer.valueOf(textMaxLead.getText()));
			}
		});
		
		final Text textDescription = new Text(compositeRow, SWT.BORDER);
		FormData fd_textDescription = new FormData();
		fd_textDescription.left = new FormAttachment(textMaxLead, 2, SWT.RIGHT);
		fd_textDescription.top = new FormAttachment(0);
		fd_textDescription.right = new FormAttachment(textMaxLead, 460, SWT.RIGHT);
		textDescription.setLayoutData(fd_textDescription);
		
		this.presentRow = compositeRow;
		this.allRows.add(compositeRow);
		compositeRow.setLayoutData(fd_compositeRow);
		this.layout();
		
		
	}

	public void addRow(){
		PitDependencyData pitDependencyData  = new PitDependencyData();
		this.pitDependencyDataList.add(pitDependencyData);
		this.addRow(pitDependencyData);
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
