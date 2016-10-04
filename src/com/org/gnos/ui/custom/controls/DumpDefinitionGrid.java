package com.org.gnos.ui.custom.controls;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
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

import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.Pit;
import com.org.gnos.db.model.PitDependencyData;
import com.org.gnos.db.model.PitGroup;

public class DumpDefinitionGrid extends Composite {

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
	/*private Label seventhSeparator;
	private Label eigthSeparator;*/
	private List<Dump> dumpList;
	private final int FIRST_SEPARATOR_POSITION = 10;
	private final int SECOND_SEPARATOR_POSITION = 20;
	private final int THIRD_SEPARATOR_POSITION = 35;
	private final int FOURTH_SEPARATOR_POSITION = 55;
	private final int FIFTH_SEPARATOR_POSITION = 60;
	private final int SIXTH_SEPARATOR_POSITION = 70;

	public DumpDefinitionGrid(Composite parent, int style) {
		super(parent, style);
		this.allRows = new ArrayList<Composite>();
		//this.pitDependencyDataList = ScenarioConfigutration.getInstance().getPitDependencyDataList();
		this.dumpList = ProjectConfigutration.getInstance().getDumpList();
		this.createContent(parent);
	}

	private void createContent(Composite parent){
		this.setLayout(new FormLayout());
		this.createHeader();
		for(Dump dump: this.dumpList){
			this.addRow(dump);
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
	
	private String[] getPitGroups(){
		ProjectConfigutration projectConfigutration = ProjectConfigutration.getInstance();
		List<PitGroup> pits = projectConfigutration.getPitGroupList();
		String[] comboItems = new String[pits.size()];
		for(int i=0; i < pits.size(); i++){
			comboItems[i] = pits.get(i).getName();
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

		Label lblDumpType = new Label(compositeGridHeader, SWT.NONE);
		lblDumpType.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblDumpType = new FormData();
		fd_lblDumpType.top = new FormAttachment(0,2);
		fd_lblDumpType.left = new FormAttachment(0, 10);
		lblDumpType.setLayoutData(fd_lblDumpType);
		lblDumpType.setText("External/Inpit Dump");
		lblDumpType.setBackground(SWTResourceManager.getColor(230, 230, 230));

		firstSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_firstSeparator = new FormData();
		fd_firstSeparator.left = new FormAttachment(FIRST_SEPARATOR_POSITION);
		firstSeparator.setLayoutData(fd_firstSeparator);
		
		Label lblDumpName = new Label(compositeGridHeader, SWT.NONE);
		lblDumpName.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblDumpName = new FormData();
		fd_lblDumpName.top = new FormAttachment(0,2);
		fd_lblDumpName.left = new FormAttachment(firstSeparator, 35);
		lblDumpName.setLayoutData(fd_lblDumpName);
		lblDumpName.setText("Dump Name");
		lblDumpName.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		secondSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_secondSeparator = new FormData();
		fd_secondSeparator.left = new FormAttachment(SECOND_SEPARATOR_POSITION);
		secondSeparator.setLayoutData(fd_secondSeparator);
		
		Label lblPitGroup = new Label(compositeGridHeader, SWT.NONE);
		lblPitGroup.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblFirstPitAssociatedBench = new FormData();
		fd_lblFirstPitAssociatedBench.top = new FormAttachment(0,2);
		fd_lblFirstPitAssociatedBench.left = new FormAttachment(secondSeparator, 80);
		lblPitGroup.setLayoutData(fd_lblFirstPitAssociatedBench);
		lblPitGroup.setText("Pit/Pit Group");
		lblPitGroup.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		thirdSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_thirdSeparator = new FormData();
		fd_thirdSeparator.left = new FormAttachment(THIRD_SEPARATOR_POSITION);
		thirdSeparator.setLayoutData(fd_thirdSeparator);
		
		Label lblExpression = new Label(compositeGridHeader, SWT.NONE);
		lblExpression.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblExpression = new FormData();
		fd_lblExpression.top = new FormAttachment(0,2);
		fd_lblExpression.left = new FormAttachment(thirdSeparator, 110);
		lblExpression.setLayoutData(fd_lblExpression);
		lblExpression.setText("Expression");
		lblExpression.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		fourthSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_fourthSeparator = new FormData();
		fd_fourthSeparator.left = new FormAttachment(FOURTH_SEPARATOR_POSITION);
		fourthSeparator.setLayoutData(fd_fourthSeparator);
		
		Label lblHasCapacity = new Label(compositeGridHeader, SWT.NONE);
		lblHasCapacity.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblHasCapacity = new FormData();
		fd_lblHasCapacity.top = new FormAttachment(0,2);
		fd_lblHasCapacity.left = new FormAttachment(fourthSeparator, 2);
		lblHasCapacity.setLayoutData(fd_lblHasCapacity);
		lblHasCapacity.setText("has capacity");
		lblHasCapacity.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		fifthSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_fifthSeparator = new FormData();
		fd_fifthSeparator.left = new FormAttachment(FIFTH_SEPARATOR_POSITION);
		fifthSeparator.setLayoutData(fd_fifthSeparator);
		
		Label lblCapacity = new Label(compositeGridHeader, SWT.NONE);
		lblCapacity.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblCapacity = new FormData();
		fd_lblCapacity.top = new FormAttachment(0,2);
		fd_lblCapacity.left = new FormAttachment(fifthSeparator, 60);
		lblCapacity.setLayoutData(fd_lblCapacity);
		lblCapacity.setText("Capacity");
		lblCapacity.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		sixthSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_sixthSeparator = new FormData();
		fd_sixthSeparator.left = new FormAttachment(SIXTH_SEPARATOR_POSITION);
		sixthSeparator.setLayoutData(fd_sixthSeparator);
		
		/*Label lblReclaim = new Label(compositeGridHeader, SWT.NONE);
		lblReclaim.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblReclaim = new FormData();
		fd_lblReclaim.top = new FormAttachment(0,2);
		fd_lblReclaim.left = new FormAttachment(sixthSeparator, 5);
		lblReclaim.setLayoutData(fd_lblReclaim);
		lblReclaim.setText("reclaim");
		lblReclaim.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		seventhSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_seventhSeparator = new FormData();
		fd_seventhSeparator.left = new FormAttachment(lblReclaim, 5);
		seventhSeparator.setLayoutData(fd_seventhSeparator);*/
		
		this.presentRow = this.compositeGridHeader;//referring to the header as the 1st row when there are no rows inserted yet
	}
	
	private void createDumpNameWidget(Composite rowComposite,Composite dumpNameComposite, int type){
		for (Control control : dumpNameComposite.getChildren()) {
	        control.dispose();
	    }
		final Control dumpNameWidget;
		final Dump dump = (Dump)rowComposite.getData();
		String dumpName = dump.getName();
		if(type == 0){
			dumpNameWidget = new Text(dumpNameComposite, SWT.NONE);
			if(dumpName != null){
				((Text)dumpNameWidget).setText(dumpName);
			}
			((Text)dumpNameWidget).addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent event) {
					dump.setName(((Text)dumpNameWidget).getText());
				}
			});
		}else{
			dumpNameWidget = new Combo(dumpNameComposite, SWT.NONE);
			((Combo) dumpNameWidget).setItems(getPits());
			if(dumpName != null){
				((Combo)dumpNameWidget).setText(dumpName);
			}
			((Combo) dumpNameWidget).addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					dump.setName(((Combo)dumpNameWidget).getText());
				}
			});
		}
		dumpNameComposite.layout();
	}

	public void addRow(final Dump dump){
		final Composite compositeRow = new Composite(this, SWT.BORDER);
		compositeRow.setLayout(new FormLayout());
		Color backgroundColor = SWTResourceManager.getColor(SWT.COLOR_WHITE);
		compositeRow.setData(dump);
		if((this.allRows != null) && (this.allRows.size()%2 != 0)){
			backgroundColor =  SWTResourceManager.getColor(245, 245, 245);
		}
		compositeRow.setBackground(backgroundColor);
		FormData fd_compositeRow = new FormData();
		fd_compositeRow.left = new FormAttachment(this.presentRow, 0, SWT.LEFT);
		//fd_compositeRow.bottom = new FormAttachment(this.presentRow, 26, SWT.BOTTOM);
		fd_compositeRow.right = new FormAttachment(this.presentRow, 0, SWT.RIGHT);
		fd_compositeRow.top = new FormAttachment(this.presentRow, 0, SWT.BOTTOM);
		
		final Combo comboDumpType = new Combo(compositeRow, SWT.NONE);
		String[] accociatedTypes = new String[]{"External", "Internal"};
		FormData fd_comboDumpType = new FormData();
		fd_comboDumpType.left = new FormAttachment(0);
		fd_comboDumpType.top = new FormAttachment(0);
		fd_comboDumpType.right = new FormAttachment(FIRST_SEPARATOR_POSITION);
		comboDumpType.setLayoutData(fd_comboDumpType);
		comboDumpType.setItems(accociatedTypes);
		comboDumpType.select(dump.getDumpType());
		
		final Composite dumpNameComposite = new Composite(compositeRow, SWT.BORDER);
		FormData fd_dumpNameComposite = new FormData();
		fd_dumpNameComposite.left = new FormAttachment(FIRST_SEPARATOR_POSITION);
		fd_dumpNameComposite.top = new FormAttachment(0);
		fd_dumpNameComposite.right = new FormAttachment(SECOND_SEPARATOR_POSITION);
		fd_dumpNameComposite.bottom = new FormAttachment(comboDumpType, 0, SWT.BOTTOM);
		dumpNameComposite.setLayoutData(fd_dumpNameComposite);
		dumpNameComposite.setLayout(new FillLayout());
		
		createDumpNameWidget(compositeRow,dumpNameComposite, 0);
		
		comboDumpType.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				createDumpNameWidget(compositeRow, dumpNameComposite, comboDumpType.getSelectionIndex());
				dump.setDumpType(comboDumpType.getSelectionIndex());
			}
		});
		
		final Combo comboPitGroup = new Combo(compositeRow, SWT.NONE);
		FormData fd_comboPitGroup = new FormData();
		fd_comboPitGroup.left = new FormAttachment(SECOND_SEPARATOR_POSITION);
		fd_comboPitGroup.top = new FormAttachment(0);
		fd_comboPitGroup.right = new FormAttachment(THIRD_SEPARATOR_POSITION);
		comboPitGroup.setLayoutData(fd_comboPitGroup);
		comboPitGroup.setItems(getPitGroups());
		comboPitGroup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//TODO implement handler
				PitGroup selectedPitGroup = ProjectConfigutration.getInstance().getPitGroupfromName(comboPitGroup.getText());
				dump.setAssociatedPitGroup(selectedPitGroup);
			}
		});
		if(dump.getAssociatedPitGroup() != null){
			String pitGroupName = dump.getAssociatedPitGroup().getName();
			if(pitGroupName != null){
				comboPitGroup.setText(pitGroupName);
			}
		}
		
		final Text textExpression = new Text(compositeRow, SWT.BORDER);
		FormData fd_textExpression = new FormData();
		fd_textExpression.left = new FormAttachment(THIRD_SEPARATOR_POSITION);
		fd_textExpression.top = new FormAttachment(0);
		fd_textExpression.right = new FormAttachment(FOURTH_SEPARATOR_POSITION);
		textExpression.setLayoutData(fd_textExpression);
		textExpression.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent event) {
				//TODO implement handler
				dump.setExpression(textExpression.getText());
			}
		});
		String expression = dump.getExpression();
		if(expression != null){
			textExpression.setText(expression);
		}
		
		final Button btnHasCapacity = new Button(compositeRow, SWT.CHECK);
		FormData fd_btnHasCapacity = new FormData();
		fd_btnHasCapacity.left = new FormAttachment(textExpression, 40, SWT.RIGHT);
		fd_btnHasCapacity.top = new FormAttachment(0, 2);
		btnHasCapacity.setLayoutData(fd_btnHasCapacity);
		btnHasCapacity.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				dump.setHasCapacity(btnHasCapacity.getSelection());
			}
		});
		btnHasCapacity.setSelection(dump.isHasCapacity());
		
		final Text textCapacity = new Text(compositeRow, SWT.BORDER);
		FormData fd_textCapacity = new FormData();
		fd_textCapacity.left = new FormAttachment(FIFTH_SEPARATOR_POSITION);
		fd_textCapacity.top = new FormAttachment(0);
		fd_textCapacity.right = new FormAttachment(SIXTH_SEPARATOR_POSITION);
		textCapacity.setLayoutData(fd_textCapacity);
		textCapacity.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent event) {
				dump.setCapacity(Integer.parseInt(textCapacity.getText()));
			}
		});
		String capacity = String.valueOf(dump.getCapacity());
		if((capacity!=null) && !(capacity.equals(""))){
			textCapacity.setText(capacity);
		}
		
		//comboPitGroup.select(0);
		
		/*final Button btnUse = new Button(compositeRow, SWT.CHECK);
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
				pitDependencyData.setFirstPitName("");
				pitDependencyData.setFirstPitAssociatedBench("");
				updateRowDescription(compositeRow);
			}
		});

		comboFirstPits.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectedPitName = comboFirstPits.getText();
				pitDependencyData.setFirstPitName(selectedPitName);
				comboAssociatedFirstPitBenches.setItems(getBenchesForPitName(selectedPitName));
				updateRowDescription(compositeRow);
			}
		});
		
		comboAssociatedFirstPitBenches.addListener(SWT.MouseDown, new Listener(){
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				pitDependencyData.setFirstPitAssociatedBench("");
				updateRowDescription(compositeRow);
			}
		});
		
		comboAssociatedFirstPitBenches.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectedBenchName = comboAssociatedFirstPitBenches.getText();
				pitDependencyData.setFirstPitAssociatedBench(selectedBenchName);
				updateRowDescription(compositeRow);
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
				pitDependencyData.setDependentPitName("");
				pitDependencyData.setDependentPitAssociatedBench("");
				updateRowDescription(compositeRow);
			}
		});

		comboDependentPits.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectedPitName = comboDependentPits.getText();
				pitDependencyData.setDependentPitName(selectedPitName);
				comboAssociatedDependentPitBenches.setItems(getBenchesForPitName(selectedPitName));
				updateRowDescription(compositeRow);
				
			}
		});
		
		comboAssociatedDependentPitBenches.addListener(SWT.MouseDown, new Listener(){
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				pitDependencyData.setDependentPitAssociatedBench("");
				updateRowDescription(compositeRow);
			}
		});
		
		comboAssociatedDependentPitBenches.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectedBenchName = comboAssociatedDependentPitBenches.getText();
				pitDependencyData.setDependentPitAssociatedBench(selectedBenchName);
				updateRowDescription(compositeRow);
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
				String maxLead = textMinLead.getText();
				if(maxLead.equals("")){
					pitDependencyData.setMinLead(-1);
				}else{
					pitDependencyData.setMinLead(Integer.valueOf(textMinLead.getText()));
				}
				updateRowDescription(compositeRow);
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
				String maxLead = textMaxLead.getText();
				if(maxLead.equals("")){
					pitDependencyData.setMaxLead(-1);
				}else{
					pitDependencyData.setMaxLead(Integer.valueOf(textMaxLead.getText()));
				}
				updateRowDescription(compositeRow);
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
		this.updateRowDescription(compositeRow);*/
		this.presentRow = compositeRow;
		compositeRow.setLayoutData(fd_compositeRow);
		this.layout();
		
		
	}

	public void addRow(){
		Dump dump = new Dump();
		this.dumpList.add(dump);
		this.addRow(dump);
	}

	public List<Composite> getAllRowsComposite(){
		return this.allRows;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
