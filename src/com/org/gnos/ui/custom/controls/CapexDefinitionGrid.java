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
import com.org.gnos.db.model.CapexData;
import com.org.gnos.db.model.CapexInstance;
import com.org.gnos.db.model.Pit;
import com.org.gnos.db.model.PitBenchConstraintData;
import com.org.gnos.db.model.PitDependencyData;
import com.org.gnos.db.model.PitGroup;
import com.org.gnos.db.model.Process;
import com.org.gnos.db.model.ProcessConstraintData;
import com.org.gnos.db.model.ProcessJoin;

public class CapexDefinitionGrid extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private Composite compositeGridHeader;
	private Composite capexInfo;
	private List<Composite> allRows;
	private Composite presentRow;
	private Label secondSeparator;
	private Label thirdSeparator;
	private Label fourthSeparator;
	private Text textCapexName;
	private CapexData capexData;
	private int processJoinEndIndex;
	private int processEndIndex;
	private int pitEndIndex;
	private int pitGroupEndIndex;

	public CapexDefinitionGrid(Composite parent, int style, CapexData capexData) {
		super(parent, style);
		this.allRows = new ArrayList<Composite>();
		this.capexData = capexData;
		this.createContent(parent);
	}

	private void createContent(Composite parent){
		this.setLayout(new FormLayout());
		this.addCapexInfo();
		this.createHeader();
		/*for(PitDependencyData pitDependencyData : this.pitDependencyDataList){
			this.addRow(pitDependencyData);
		}*/
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

	/*private String[] getPits(){

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
	}*/
	
	private void addCapexInfo(){
		capexInfo = new Composite(this, SWT.BORDER);
		capexInfo.setBackground(SWTResourceManager.getColor(230, 230, 230));
		capexInfo.setLayout(new FormLayout());
		FormData fd_capexInfo = new FormData();
		fd_capexInfo.bottom = new FormAttachment(0, 28);
		fd_capexInfo.top = new FormAttachment(0);
		fd_capexInfo.left = new FormAttachment(0);
		fd_capexInfo.right = new FormAttachment(0, 366);
		capexInfo.setLayoutData(fd_capexInfo);
		
		Label lblCapexName = new Label(capexInfo, SWT.NONE);
		lblCapexName.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblCapexName = new FormData();
		fd_lblCapexName.right = new FormAttachment(0, 91);
		fd_lblCapexName.left = new FormAttachment(0, 10);
		fd_lblCapexName.top = new FormAttachment(0,2);
		lblCapexName.setLayoutData(fd_lblCapexName);
		lblCapexName.setText("Capex Name:");
		lblCapexName.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		textCapexName = new Text(capexInfo, SWT.BORDER);
		FormData fd_textCapexName = new FormData();
		fd_textCapexName.left = new FormAttachment(lblCapexName, 10, SWT.RIGHT);
		fd_textCapexName.right = new FormAttachment(0, 300);
		textCapexName.setLayoutData(fd_textCapexName);
		String capexName = capexData.getName();
		if(capexName != null){
			textCapexName.setText(capexName);
		}
		textCapexName.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent event) {
				String capexName = textCapexName.getText();
				capexData.setName(capexName);
			}
		});
	}
	
	private void createHeader(){
		compositeGridHeader = new Composite(this, SWT.BORDER);
		compositeGridHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));
		compositeGridHeader.setLayout(new FormLayout());
		FormData fd_compositeGridHeader = new FormData();
		fd_compositeGridHeader.bottom = new FormAttachment(capexInfo, 38, SWT.BOTTOM);
		fd_compositeGridHeader.top = new FormAttachment(capexInfo, 5, SWT.BOTTOM);
		fd_compositeGridHeader.left = new FormAttachment(0);
		fd_compositeGridHeader.right = new FormAttachment(100);
		compositeGridHeader.setLayoutData(fd_compositeGridHeader);
		
		Label lblFirstPit = new Label(compositeGridHeader, SWT.NONE);
		lblFirstPit.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblFirstPit = new FormData();
		fd_lblFirstPit.right = new FormAttachment(0, 133);
		fd_lblFirstPit.left = new FormAttachment(0, 10);
		fd_lblFirstPit.top = new FormAttachment(0,2);
		lblFirstPit.setLayoutData(fd_lblFirstPit);
		lblFirstPit.setText("Capex Instance Name");
		lblFirstPit.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		secondSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_secondSeparator = new FormData();
		fd_secondSeparator.left = new FormAttachment(lblFirstPit, 17);
		secondSeparator.setLayoutData(fd_secondSeparator);
		
		Label lblFirstPitAssociatedBench = new Label(compositeGridHeader, SWT.NONE);
		lblFirstPitAssociatedBench.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblFirstPitAssociatedBench = new FormData();
		fd_lblFirstPitAssociatedBench.top = new FormAttachment(0,2);
		fd_lblFirstPitAssociatedBench.left = new FormAttachment(secondSeparator, 35);
		lblFirstPitAssociatedBench.setLayoutData(fd_lblFirstPitAssociatedBench);
		lblFirstPitAssociatedBench.setText("Process/Pit");
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
		lblDependentPit.setText("Capex($)");
		lblDependentPit.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		fourthSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_fourthSeparator = new FormData();
		fd_fourthSeparator.left = new FormAttachment(lblDependentPit, 35);
		fourthSeparator.setLayoutData(fd_fourthSeparator);
		
		Label lblMinLead = new Label(compositeGridHeader, SWT.NONE);
		lblMinLead.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblMinLead = new FormData();
		fd_lblMinLead.top = new FormAttachment(lblFirstPit, 0, SWT.TOP);
		fd_lblMinLead.left = new FormAttachment(fourthSeparator, 6);
		lblMinLead.setLayoutData(fd_lblMinLead);
		lblMinLead.setText("Expansion Capacity");
		lblMinLead.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		
		
		this.presentRow = this.compositeGridHeader;//referring to the header as the 1st row when there are no rows inserted yet
		
		Label label = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_label = new FormData();
		fd_label.left = new FormAttachment(lblMinLead, 6);
		label.setLayoutData(fd_label);

	}

	public void addRow(final CapexInstance capexInstance){
		final Composite compositeRow = new Composite(this, SWT.BORDER);
		compositeRow.setLayout(new FormLayout());
		Color backgroundColor = SWTResourceManager.getColor(SWT.COLOR_WHITE);
		compositeRow.setData(capexInstance);
		if((this.allRows != null) && (this.allRows.size()%2 != 0)){
			backgroundColor =  SWTResourceManager.getColor(245, 245, 245);
		}
		compositeRow.setBackground(backgroundColor);
		FormData fd_compositeRow = new FormData();
		fd_compositeRow.left = new FormAttachment(this.presentRow, 0, SWT.LEFT);
		fd_compositeRow.right = new FormAttachment(this.presentRow, 0, SWT.RIGHT);
		fd_compositeRow.top = new FormAttachment(this.presentRow);
		
		final Text textCapexInstanceName = new Text(compositeRow, SWT.BORDER);
		FormData fd_textCapexInstanceName = new FormData();
		fd_textCapexInstanceName.left = new FormAttachment(0, 5);
		fd_textCapexInstanceName.right = new FormAttachment(0, 148);
		textCapexInstanceName.setLayoutData(fd_textCapexInstanceName);
		String capexName = capexData.getName();
		if(capexName != null){
			textCapexInstanceName.setText(capexName);
		}
		textCapexInstanceName.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent event) {
				String capexName = textCapexInstanceName.getText();
				capexInstance.setName(capexName);
			}
		});
		
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

		comboGroup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectorName = comboGroup.getText();
				System.out.println("Group selected is: " + selectorName);
				int selectorSelectionIndex = comboGroup.getSelectionIndex();
				if(selectorSelectionIndex < 0) {
					capexInstance.setGroupingType(CapexInstance.SELECTION_NONE);
				}else if(selectorSelectionIndex <= processJoinEndIndex ) {
					capexInstance.setGroupingType(CapexInstance.SELECTION_PROCESS_JOIN);
				} else if(selectorSelectionIndex <= processEndIndex ) {
					capexInstance.setGroupingType(CapexInstance.SELECTION_PROCESS);
				} else if(selectorSelectionIndex <= pitEndIndex ) {
					capexInstance.setGroupingType(CapexInstance.SELECTION_PIT);
				} else {
					capexInstance.setGroupingType(CapexInstance.SELECTION_PIT_GROUP);
				}

				capexInstance.setGroupingName(selectorName);
			}
		});
		
		FormData fd_comboGroup = new FormData();
		fd_comboGroup.left = new FormAttachment(textCapexInstanceName, 5, SWT.RIGHT);
		fd_comboGroup.right = new FormAttachment(textCapexInstanceName, 130, SWT.RIGHT);
		fd_comboGroup.top = new FormAttachment(0);
		comboGroup.setLayoutData(fd_comboGroup);
		

		final Text textCapexAmount = new Text(compositeRow, SWT.BORDER);
		FormData fd_textCapexAmount = new FormData();
		fd_textCapexAmount.left = new FormAttachment(comboGroup, 5, SWT.RIGHT);
		fd_textCapexAmount.right = new FormAttachment(comboGroup, 120, SWT.RIGHT);
		textCapexAmount.setLayoutData(fd_textCapexAmount);
		Double capexAmount = capexInstance.getCapexAmount();
		if((capexAmount != null) && (capexAmount > 0)){
			textCapexAmount.setText(String.valueOf(capexAmount));
		}
		textCapexAmount.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent event) {
				String capexAmount = textCapexAmount.getText();
				capexInstance.setCapexAmount(Double.valueOf(capexAmount));
			}
		});
		
		final Text textExpansionCapacity = new Text(compositeRow, SWT.BORDER);
		FormData fd_textExpansionCapacity = new FormData();
		fd_textExpansionCapacity.left = new FormAttachment(textCapexAmount, 5, SWT.RIGHT);
		fd_textExpansionCapacity.right = new FormAttachment(textCapexAmount, 120, SWT.RIGHT);
		textExpansionCapacity.setLayoutData(fd_textExpansionCapacity);
		Double expansionCapacity = capexInstance.getExpansionCapacity();
		if((expansionCapacity != null) && (expansionCapacity > 0)){
			textExpansionCapacity.setText(String.valueOf(expansionCapacity));
		}
		textExpansionCapacity.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent event) {
				String expansionCapacity = textExpansionCapacity.getText();
				capexInstance.setExpansionCapacity(Double.valueOf(expansionCapacity));
			}
		});
		
		this.presentRow = compositeRow;
		this.allRows.add(compositeRow);
		compositeRow.setLayoutData(fd_compositeRow);
		this.layout();
	}

	public void addRow(){
		System.out.println("Should add a new row");
		CapexInstance capexInstance = new CapexInstance();
		this.addRow(capexInstance);
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
