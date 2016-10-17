package com.org.gnos.ui.custom.controls;

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.core.ScenarioConfigutration;
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.DumpDependencyData;
import com.org.gnos.db.model.Pit;

public class DumpDependencyGrid extends Composite {

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
	private List<DumpDependencyData> dumpDependencyDataList;
	
	private static final int FIRST_SEPARATOR_POSITION = 4;
	private static final int SECOND_SEPARATOR_POSITION = 14;
	private static final int THIRD_SEPARATOR_POSITION = 24;
	private static final int FOURTH_SEPARATOR_POSITION = 40;
	private static final int FIFTH_SEPARATOR_POSITION = 70;

	public DumpDependencyGrid(Composite parent, int style) {
		super(parent, style);
		this.allRows = new ArrayList<Composite>();
		this.dumpDependencyDataList = ScenarioConfigutration.getInstance().getDumpDependencyDataList();
		this.createContent(parent);
	}

	private void createContent(Composite parent){
		this.setLayout(new FormLayout());
		this.createHeader();
		for(DumpDependencyData dumpDependencyData : this.dumpDependencyDataList){
			this.addRow(dumpDependencyData);
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
	
	private String[] getDumps(){

		ProjectConfigutration projectConfigutration = ProjectConfigutration.getInstance();
		List<Dump> dumps = projectConfigutration.getDumpList();
		String[] comboItems = new String[dumps.size()];
		for(int i=0; i < dumps.size(); i++){
			comboItems[i] = dumps.get(i).getName();
		}
		return comboItems;
	}
	
	private String getDescription(String firstPit, String firstPitAssociatedBench, String dependentPit, String dependentPitPitAssociatedBench, String minLead, String maxLead){
		String description = null;
		if(firstPitAssociatedBench == null){
			firstPitAssociatedBench = "";
		}
		if(dependentPitPitAssociatedBench == null){
			dependentPitPitAssociatedBench = "";
		}
		if((firstPit==null)||(dependentPit==null)||(firstPit.equals(""))||(dependentPit.equals(""))){
			return "";
		}else if((minLead.equals("-1")) && (maxLead.equals("-1"))){
			description = firstPit + "/" + firstPitAssociatedBench + " will be totally mined before " + dependentPit + "/" + dependentPitPitAssociatedBench + " is started.";
		}else if(maxLead.equals("-1")){
			description = firstPit + "/" + firstPitAssociatedBench + " will be mined atleast " + minLead + " benches ahead of " + dependentPit + "/" + dependentPitPitAssociatedBench;
		}else if(minLead.equals("-1")){
			description = firstPit + "/" + firstPitAssociatedBench + " will be mined atmost " + maxLead + " benches ahead of " + dependentPit + "/" + dependentPitPitAssociatedBench;
		}else if(!(minLead.equals("-1")) && !(maxLead.equals("-1"))){
			description = firstPit + "/" + firstPitAssociatedBench + " will be mined atleast " + minLead + " benches ahead of " + dependentPit + "/" + dependentPitPitAssociatedBench + " AND " + firstPit + "/" + firstPitAssociatedBench + " will be mined atmost " + maxLead + " benches ahead of " + dependentPit + "/" + dependentPitPitAssociatedBench;
		}
		return description;
	}
	
	private void updateRowDescription(Composite rowComposite){
		/*PitDependencyData pitDependencyData = (PitDependencyData)rowComposite.getData();
		String firstPit = pitDependencyData.getFirstPitName();
		String firstPitAssociatedBench = pitDependencyData.getFirstPitAssociatedBench();
		String dependentPit = pitDependencyData.getDependentPitName();
		String dependentPitAssociatedBench = pitDependencyData.getDependentPitAssociatedBench();
		String minLead = String.valueOf(pitDependencyData.getMinLead());
		String maxLead = String.valueOf(pitDependencyData.getMaxLead());
		
		String description = getDescription(firstPit, firstPitAssociatedBench, dependentPit, dependentPitAssociatedBench, minLead, maxLead);
		Text textDescription = (Text)rowComposite.getChildren()[7];
		if(description != null){
			textDescription.setText(description);
		}*/
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
		fd_firstSeparator.left = new FormAttachment(FIRST_SEPARATOR_POSITION);
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
		fd_secondSeparator.left = new FormAttachment(SECOND_SEPARATOR_POSITION);
		secondSeparator.setLayoutData(fd_secondSeparator);
		
		Label lblFirstDump = new Label(compositeGridHeader, SWT.NONE);
		lblFirstDump.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblFirstDump = new FormData();
		fd_lblFirstDump.top = new FormAttachment(0,2);
		fd_lblFirstDump.left = new FormAttachment(secondSeparator, 50);
		lblFirstDump.setLayoutData(fd_lblFirstDump);
		lblFirstDump.setText("First Dump");
		lblFirstDump.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		thirdSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_thirdSeparator = new FormData();
		fd_thirdSeparator.left = new FormAttachment(THIRD_SEPARATOR_POSITION);
		thirdSeparator.setLayoutData(fd_thirdSeparator);
		
		Label lblDependentDump = new Label(compositeGridHeader, SWT.NONE);
		lblDependentDump.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblDependentDump = new FormData();
		fd_lblDependentDump.top = new FormAttachment(0,2);
		fd_lblDependentDump.left = new FormAttachment(thirdSeparator, 35);
		lblDependentDump.setLayoutData(fd_lblDependentDump);
		lblDependentDump.setText("Dependent Dump");
		lblDependentDump.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		fourthSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_fourthSeparator = new FormData();
		fd_fourthSeparator.left = new FormAttachment(FOURTH_SEPARATOR_POSITION);
		fourthSeparator.setLayoutData(fd_fourthSeparator);
		
		Label lblDescription = new Label(compositeGridHeader, SWT.NONE);
		lblDescription.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblDescription = new FormData();
		fd_lblDescription.top = new FormAttachment(0,2);
		fd_lblDescription.left = new FormAttachment(fourthSeparator, 200);
		lblDescription.setLayoutData(fd_lblDescription);
		lblDescription.setText("Description");
		lblDescription.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		fifthSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_fifthSeparator = new FormData();
		fd_fifthSeparator.left = new FormAttachment(FIFTH_SEPARATOR_POSITION);
		fifthSeparator.setLayoutData(fd_fifthSeparator);
		
		/*Label lblMinLead = new Label(compositeGridHeader, SWT.NONE);
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
		eigthSeparator.setLayoutData(fd_eigthSeparator);*/
		
		
		
		this.presentRow = this.compositeGridHeader;//referring to the header as the 1st row when there are no rows inserted yet

	}

	public void addRow(final DumpDependencyData dumpDependencyData){
		final Composite compositeRow = new Composite(this, SWT.BORDER);
		compositeRow.setLayout(new FormLayout());
		Color backgroundColor = SWTResourceManager.getColor(SWT.COLOR_WHITE);
		compositeRow.setData(dumpDependencyData);
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
		btnUse.setSelection(dumpDependencyData.isInUse());
		btnUse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				System.out.println("Is button in use selected: " + btnUse.getSelection());
				dumpDependencyData.setInUse(btnUse.getSelection());
			}
		});
		
		final Combo comboFirstPits = new Combo(compositeRow, SWT.NONE);
		comboFirstPits.setItems(this.getPits());
		String firstPitName  = dumpDependencyData.getFirstPitName();
		FormData fd_comboFirstPits = new FormData();
		fd_comboFirstPits.left = new FormAttachment(FIRST_SEPARATOR_POSITION, 2);
		fd_comboFirstPits.top = new FormAttachment(0);
		fd_comboFirstPits.right = new FormAttachment(SECOND_SEPARATOR_POSITION, -2);
		comboFirstPits.setLayoutData(fd_comboFirstPits);
		
		final Combo comboFirstDumps = new Combo(compositeRow, SWT.NONE);
		comboFirstPits.setItems(this.getDumps());
		String firstDumpNameName = dumpDependencyData.getFirstDumpName();
		FormData fd_comboFirstDumps = new FormData();
		fd_comboFirstDumps.left = new FormAttachment(SECOND_SEPARATOR_POSITION, 2);
		fd_comboFirstDumps.top = new FormAttachment(0);
		fd_comboFirstDumps.right = new FormAttachment(THIRD_SEPARATOR_POSITION, -2);
		comboFirstDumps.setLayoutData(fd_comboFirstDumps);
		
		
		if(firstPitName != null){
			comboFirstPits.setText(firstPitName);
			//comboAssociatedFirstPitBenches.setItems(this.getBenchesForPitName(associatedFirstPitName));
		}else{
			comboFirstPits.setText("Select Pit");
		}
		
		if(firstDumpNameName != null){
			comboFirstDumps.setText(firstDumpNameName);
		}else{
			comboFirstDumps.setText("Select First Dump");
		}
		
		comboFirstPits.addListener(SWT.MouseDown, new Listener(){
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				comboFirstPits.removeAll();
				comboFirstPits.setItems(getPits());
				comboFirstPits.getParent().layout();
				comboFirstPits.setListVisible(true);
				//dumpDependencyData.setFirstPitName("");
				//dumpDependencyData.setFirstDumpName(null);
				//updateRowDescription(compositeRow);
			}
		});

		comboFirstPits.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectedPitName = comboFirstPits.getText();
				dumpDependencyData.setFirstPitName(selectedPitName);
				dumpDependencyData.setFirstDumpName(null);
				comboFirstDumps.setText("");
				//comboAssociatedFirstPitBenches.setItems(getBenchesForPitName(selectedPitName));
				updateRowDescription(compositeRow);
			}
		});
		
		comboFirstDumps.addListener(SWT.MouseDown, new Listener(){
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				comboFirstDumps.removeAll();
				comboFirstDumps.setItems(getDumps());
				comboFirstDumps.getParent().layout();
				comboFirstDumps.setListVisible(true);
				//dumpDependencyData.setFirstPitName("");
				//dumpDependencyData.setFirstDumpName(null);
				//updateRowDescription(compositeRow);
			}
		});
		
		comboFirstDumps.addListener(SWT.MouseDown, new Listener(){
			@Override
			public void handleEvent(Event event) {
				String selectedDumpName = comboFirstDumps.getText();
				dumpDependencyData.setFirstPitName(null);
				dumpDependencyData.setFirstDumpName(selectedDumpName);
				comboFirstPits.setText("");
				//comboAssociatedFirstPitBenches.setItems(getBenchesForPitName(selectedPitName));
				updateRowDescription(compositeRow);
			}
		});
		
		/*comboAssociatedFirstPitBenches.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectedBenchName = comboAssociatedFirstPitBenches.getText();
				pitDependencyData.setFirstPitAssociatedBench(selectedBenchName);
				updateRowDescription(compositeRow);
			}
		});*/
		
		final Combo comboDependentDumps = new Combo(compositeRow, SWT.NONE);
		comboDependentDumps.setItems(this.getPits());
		String dependentDumpName  = dumpDependencyData.getDependentDumpName();
		FormData fd_comboDependentDumps = new FormData();
		fd_comboDependentDumps.left = new FormAttachment(THIRD_SEPARATOR_POSITION, 2);
		fd_comboDependentDumps.top = new FormAttachment(0);
		fd_comboDependentDumps.right = new FormAttachment(FOURTH_SEPARATOR_POSITION, -2);
		comboDependentDumps.setLayoutData(fd_comboDependentDumps);
		if(dependentDumpName != null){
			comboDependentDumps.setText(dependentDumpName);
		}else{
			comboDependentDumps.setText("Select Dependent Dump");
		}
		
		final Text textDescription = new Text(compositeRow, SWT.BORDER);
		FormData fd_textDescription = new FormData();
		fd_textDescription.left = new FormAttachment(FOURTH_SEPARATOR_POSITION, 2);
		fd_textDescription.top = new FormAttachment(0);
		fd_textDescription.right = new FormAttachment(FIFTH_SEPARATOR_POSITION, -2);
		textDescription.setLayoutData(fd_textDescription);
		
		this.presentRow = compositeRow;
		this.allRows.add(compositeRow);
		compositeRow.setLayoutData(fd_compositeRow);
		this.updateRowDescription(compositeRow);
		this.layout();
		
		
	}

	public void addRow(){
		DumpDependencyData dumpDependencyData  = new DumpDependencyData();
		this.dumpDependencyDataList.add(dumpDependencyData);
		this.addRow(dumpDependencyData);
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
