package com.org.gnos.ui.screens.v1;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.Pit;
import com.org.gnos.db.model.PitGroup;
import com.org.gnos.db.model.Stockpile;
import com.org.gnos.events.GnosEvent;
import com.org.gnos.ui.custom.controls.DumpCreationDialog;
import com.org.gnos.ui.custom.controls.GnosScreen;
import com.org.gnos.ui.custom.controls.GroupCreationDialog;
import com.org.gnos.ui.custom.controls.ImportCycleTimeDialog;
import com.org.gnos.ui.custom.controls.StockpileCreationDialog;
import com.org.gnos.ui.custom.controls.TruckParameterDialog;
import com.org.gnos.ui.graph.PitGroupDefinitionGraph;

public class PitGroupDumpStockpileDefinitionScreen extends GnosScreen {
	private String[] sourcePitItems;
	private String[] sourcePitGroupItems;
	private List pitList;
	private List groupList;
	private List dumpList;
	private List stockpileList;
	
	private ArrayList<String> listAddedModels;
	private PitGroupDefinitionGraph compositeGroupDiagram;
	private java.util.List<Pit> listOfPits;
	private java.util.List<PitGroup> listOfPitGroups;
	private java.util.List<Stockpile> listOfStockpiles;
	private java.util.List<Dump> listOfDumps;

	public PitGroupDumpStockpileDefinitionScreen(Composite parent, int style) {
		super(parent, style);
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		setLayout(new FormLayout());
		
		ProjectConfigutration projectConfigutration = ProjectConfigutration.getInstance();
		this.listOfPits = projectConfigutration.getPitList();
		this.listOfPitGroups = projectConfigutration.getPitGroupList();
		this.listOfStockpiles = projectConfigutration.getStockPileList();
		this.listOfDumps = projectConfigutration.getDumpList();
		
		this.listAddedModels = new ArrayList<String>();
		
		Label labelScreenName = new Label(this, SWT.NONE);
		labelScreenName.setForeground(SWTResourceManager.getColor(0, 191, 255));
		labelScreenName.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_labelScreenName = new FormData();
		fd_labelScreenName.top = new FormAttachment(0, 10);
		fd_labelScreenName.left = new FormAttachment(0, 10);
		labelScreenName.setLayoutData(fd_labelScreenName);
		labelScreenName.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		labelScreenName.setText("PitGroup Dump and Stockpile Definition");
		
		Label labelScreenDescription = new Label(this, SWT.NONE);
		labelScreenDescription.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		labelScreenDescription.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_labelScreenDescription = new FormData();
		fd_labelScreenDescription.top = new FormAttachment(labelScreenName, 10, SWT.BOTTOM);
		fd_labelScreenDescription.left = new FormAttachment(0, 10);
		labelScreenDescription.setLayoutData(fd_labelScreenDescription);
		labelScreenDescription.setText("Define your pit gourps, dumps and stockpiles");
	
		Label labelFirstSeparator = new Label(this, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_labelFirstSeparator = new FormData();
		fd_labelFirstSeparator.top = new FormAttachment(labelScreenDescription, 10, SWT.BOTTOM);
		fd_labelFirstSeparator.left = new FormAttachment(12);
		fd_labelFirstSeparator.bottom = new FormAttachment(100);
		labelFirstSeparator.setLayoutData(fd_labelFirstSeparator);
		
		Label labelSecondSeparator = new Label(this, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_labelSecondSeparator = new FormData();
		fd_labelSecondSeparator.top = new FormAttachment(labelScreenDescription, 10, SWT.BOTTOM);
		fd_labelSecondSeparator.left = new FormAttachment(85);
		fd_labelSecondSeparator.bottom = new FormAttachment(100);
		labelSecondSeparator.setLayoutData(fd_labelSecondSeparator);

		
		Label lblAllPits = new Label(this, SWT.NONE);
		lblAllPits.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblAllPits.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		FormData fd_lblAllModels = new FormData();
		fd_lblAllModels.top = new FormAttachment(labelFirstSeparator, 0, SWT.TOP);
		fd_lblAllModels.left = new FormAttachment(0, 10);
		lblAllPits.setLayoutData(fd_lblAllModels);
		lblAllPits.setText("All Pits:");
		
		Label lblAllGroups = new Label(this, SWT.NONE);
		lblAllGroups.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblAllGroups.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		FormData fd_lblAllGroups = new FormData();
		fd_lblAllGroups.top = new FormAttachment(50);
		fd_lblAllGroups.left = new FormAttachment(0, 10);
		lblAllGroups.setLayoutData(fd_lblAllGroups);
		lblAllGroups.setText("All Pit Groups:");
		
		Label lblDiagram = new Label(this, SWT.NONE);
		lblDiagram.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblDiagram.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblDiagram = new FormData();
		//fd_lblProcessDiagram.bottom = new FormAttachment(lblAllModels, 0, SWT.BOTTOM);
		fd_lblDiagram.top = new FormAttachment(labelFirstSeparator, 0, SWT.TOP);
		fd_lblDiagram.left = new FormAttachment(labelFirstSeparator, 10);
		//fd_lblDiagram.right = new FormAttachment(labelSecondSeparator);
		lblDiagram.setLayoutData(fd_lblDiagram);
		lblDiagram.setText("Generated Grouping Diagram");
		
		/*
		 * Import cycle time button
		 */
		Button btnImportCycleTime = new Button(this, SWT.NONE);
		btnImportCycleTime.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ImportCycleTimeDialog dialog = new ImportCycleTimeDialog(getShell());
				if (Window.OK == dialog.open()) {
					/*for(Dump dump : listOfDumps){
						if(dump.getId() == -1){//unsaved dumps have value -1
							compositeGroupDiagram.addDumpToGroup(dump);
							dumpList.add(dump.getName());
						}
					}*/
				}
			}
		});
		btnImportCycleTime.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_btnImportCycleTime = new FormData();
		fd_btnImportCycleTime.top = new FormAttachment(lblDiagram, 0, SWT.TOP);
		fd_btnImportCycleTime.left = new FormAttachment(lblDiagram, 5);
		btnImportCycleTime.setLayoutData(fd_btnImportCycleTime);
		btnImportCycleTime.setText("Import Cycle Time");
		
		/*
		 * Define track params button
		 */
		Button btnDefineTruckParams = new Button(this, SWT.NONE);
		btnDefineTruckParams.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TruckParameterDialog dialog = new TruckParameterDialog(getShell());
				if (Window.OK == dialog.open()) {
					//TODO implementation
				}
			}
		});
		btnImportCycleTime.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_btnDefineTruckParams = new FormData();
		fd_btnDefineTruckParams.top = new FormAttachment(lblDiagram, 0, SWT.TOP);
		fd_btnDefineTruckParams.left = new FormAttachment(btnImportCycleTime, 5);
		btnDefineTruckParams.setLayoutData(fd_btnDefineTruckParams);
		btnDefineTruckParams.setText("Define Truck Params");
		
		Label lblAllDumps = new Label(this, SWT.NONE);
		lblAllDumps.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblAllDumps.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		FormData fd_lblAllDumps = new FormData();
		fd_lblAllDumps.top = new FormAttachment(labelSecondSeparator, 0, SWT.TOP);
		fd_lblAllDumps.left = new FormAttachment(labelSecondSeparator, 10);
		lblAllDumps.setLayoutData(fd_lblAllDumps);
		lblAllDumps.setText("All Dumps:");
		
		Label lblAllStockpiles = new Label(this, SWT.NONE);
		lblAllStockpiles.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblAllStockpiles.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		FormData fd_lblAllStockpiles = new FormData();
		fd_lblAllStockpiles.top = new FormAttachment(50);
		fd_lblAllStockpiles.left = new FormAttachment(labelSecondSeparator, 10);
		lblAllStockpiles.setLayoutData(fd_lblAllStockpiles);
		lblAllStockpiles.setText("All Stockpiles:");
		
		/*
		 * Pit List
		 */
		final Composite compositePitList = new Composite(this, SWT.BORDER);
		compositePitList.setLayout(new FillLayout(SWT.HORIZONTAL));
		FormData fd_compositePitList = new FormData();
		fd_compositePitList.top = new FormAttachment(lblAllPits, 10);
		fd_compositePitList.bottom = new FormAttachment(50, -10);
		fd_compositePitList.left = new FormAttachment(0, 10);
		fd_compositePitList.right = new FormAttachment(labelFirstSeparator, -10);
		compositePitList.setLayoutData(fd_compositePitList);
		this.pitList = new List(compositePitList, SWT.BORDER|SWT.MULTI|SWT.V_SCROLL);
		this.pitList.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		this.pitList.setItems(this.getSourcePitItems());
		
		/*
		 * Group List
		 */
		final Composite compositeGroupList = new Composite(this, SWT.BORDER);
		compositeGroupList.setLayout(new FillLayout(SWT.HORIZONTAL));
		FormData fd_compositeGroupList = new FormData();
		fd_compositeGroupList.top = new FormAttachment(lblAllGroups, 10);
		fd_compositeGroupList.bottom = new FormAttachment(100, -10);
		fd_compositeGroupList.left = new FormAttachment(0, 10);
		fd_compositeGroupList.right = new FormAttachment(labelFirstSeparator, -10);
		compositeGroupList.setLayoutData(fd_compositeGroupList);
		this.groupList = new List(compositeGroupList, SWT.BORDER|SWT.MULTI|SWT.V_SCROLL);
		this.groupList.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		
		/*
		 * Dump List
		 */
		final Composite compositeDumpList = new Composite(this, SWT.BORDER);
		compositeDumpList.setLayout(new FillLayout(SWT.HORIZONTAL));
		FormData fd_compositeDumpList = new FormData();
		fd_compositeDumpList.top = new FormAttachment(lblAllDumps, 10);
		fd_compositeDumpList.bottom = new FormAttachment(50, -10);
		fd_compositeDumpList.left = new FormAttachment(labelSecondSeparator, 10);
		fd_compositeDumpList.right = new FormAttachment(100, -10);
		compositeDumpList.setLayoutData(fd_compositeDumpList);
		this.dumpList = new List(compositeDumpList, SWT.BORDER|SWT.MULTI|SWT.V_SCROLL);
		this.dumpList.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		
		
		/*
		 * Stockpile List
		 */
		final Composite compositeStockpileList = new Composite(this, SWT.BORDER);
		compositeStockpileList.setLayout(new FillLayout(SWT.HORIZONTAL));
		FormData fd_compositeStockpileList = new FormData();
		fd_compositeStockpileList.top = new FormAttachment(lblAllStockpiles, 10);
		fd_compositeStockpileList.bottom = new FormAttachment(100, -10);
		fd_compositeStockpileList.left = new FormAttachment(labelSecondSeparator, 10);
		fd_compositeStockpileList.right = new FormAttachment(100, -10);
		compositeStockpileList.setLayoutData(fd_compositeStockpileList);
		this.stockpileList = new List(compositeStockpileList, SWT.BORDER|SWT.MULTI|SWT.V_SCROLL);
		this.stockpileList.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		
		
		/*
		 * Graphical Diagram
		 */
		this.compositeGroupDiagram = new PitGroupDefinitionGraph(this, SWT.BORDER);
		FormData fd_compositeGroupDiagram = new FormData();
		fd_compositeGroupDiagram.top = new FormAttachment(lblDiagram, 10);
		fd_compositeGroupDiagram.left = new FormAttachment(labelFirstSeparator, 10);
		fd_compositeGroupDiagram.bottom = new FormAttachment(100, -10);
		fd_compositeGroupDiagram.right = new FormAttachment(labelSecondSeparator, -10);
		this.compositeGroupDiagram.setLayoutData(fd_compositeGroupDiagram);
		

		/*
		 * Add pit to group button
		 */
		Button btnAddPitToGroup = new Button(this, SWT.NONE);
		btnAddPitToGroup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Add model to process implementation
				
				//TODO add pit to group handler
				GroupCreationDialog dialog = new GroupCreationDialog(getShell());
				if (Window.OK == dialog.open()) {
					String groupName = dialog.getCreatedGroupName();
					PitGroup pitGroup = new PitGroup(groupName);
					String[] selectedPitNames = pitList.getSelection();
					for(String selectedPitName: selectedPitNames){
						pitGroup.addPit(getPitByNameFromPitList(selectedPitName));
					}
					listOfPitGroups.add(pitGroup);
					groupList.add(groupName);
					compositeGroupDiagram.addGroup(pitGroup);
					
				}
			}
		});
		btnAddPitToGroup.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_btnAddPitToGroup = new FormData();
		fd_btnAddPitToGroup.bottom = new FormAttachment(lblAllPits, 5, SWT.BOTTOM);
		fd_btnAddPitToGroup.left = new FormAttachment(lblAllPits, 2);
		btnAddPitToGroup.setLayoutData(fd_btnAddPitToGroup);
		btnAddPitToGroup.setText("Add");
		
		/*
		 * Add group to group button
		 */
		/*Button btnAddGroupToGroup = new Button(this, SWT.NONE);
		btnAddGroupToGroup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO add group to group handler
			}
		});
		btnAddGroupToGroup.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_btnAddGroupToGroup = new FormData();
		fd_btnAddGroupToGroup.bottom = new FormAttachment(lblAllGroups, 5, SWT.BOTTOM);
		fd_btnAddGroupToGroup.left = new FormAttachment(lblAllGroups, 2);
		btnAddGroupToGroup.setLayoutData(fd_btnAddGroupToGroup);
		btnAddGroupToGroup.setText("Add");*/
		
		/*
		 * Add dump to group button
		 */
		Button btnAddDumpToGroup = new Button(this, SWT.NONE);
		btnAddDumpToGroup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO add group to group handler
				DumpCreationDialog dialog = new DumpCreationDialog(getShell(), getSourcePitGroupItems());
				if (Window.OK == dialog.open()) {
					for(Dump dump : listOfDumps){
						if((dump.getId() == -1) && !isDumpAlreadyPresent(dump.getName())){//unsaved dumps have value -1
							compositeGroupDiagram.addDumpToGroup(dump);
							dumpList.add(dump.getName());
						}
					}
				}
			}
		});
		btnAddDumpToGroup.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_btnAddDumpToGroup = new FormData();
		fd_btnAddDumpToGroup.bottom = new FormAttachment(lblAllDumps, 5, SWT.BOTTOM);
		fd_btnAddDumpToGroup.left = new FormAttachment(lblAllDumps, 2);
		btnAddDumpToGroup.setLayoutData(fd_btnAddDumpToGroup);
		btnAddDumpToGroup.setText("Add/Update");
		
		/*
		 * Add stockpile to group button
		 */
		Button btnAddStockpileToGroup = new Button(this, SWT.NONE);
		btnAddStockpileToGroup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO add stockpile to group handler
				StockpileCreationDialog dialog = new StockpileCreationDialog(getShell(), getSourcePitGroupItems());
				if (Window.OK == dialog.open()) {
					for(Stockpile stockpile : listOfStockpiles){
						if((stockpile.getId() == -1) && !isStockpileAlreadyPresent(stockpile.getName())){//unsaved dumps have value -1
							compositeGroupDiagram.addStockpileToGroup(stockpile);
							stockpileList.add(stockpile.getName());
						}
					}
				}
			}
		});
		btnAddStockpileToGroup.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_btnAddStockpileToGroup = new FormData();
		fd_btnAddStockpileToGroup.bottom = new FormAttachment(lblAllStockpiles, 5, SWT.BOTTOM);
		fd_btnAddStockpileToGroup.left = new FormAttachment(lblAllStockpiles, 2);
		btnAddStockpileToGroup.setLayoutData(fd_btnAddStockpileToGroup);
		btnAddStockpileToGroup.setText("Add/Update");
		
		//Add existing pit groups to diagram and list
		for(PitGroup pitGroup: this.listOfPitGroups){
			this.compositeGroupDiagram.addGroup(pitGroup);
			this.groupList.add(pitGroup.getName());
		}
		
		//Add existing dumps to diagram and list
		for(Dump dump : this.listOfDumps){
			this.compositeGroupDiagram.addDumpToGroup(dump);
			this.dumpList.add(dump.getName());
		}
		
		//Add existing stockpiles to diagram and list
		for(Stockpile stockPile :  this.listOfStockpiles){
			this.compositeGroupDiagram.addStockpileToGroup(stockPile);
			this.stockpileList.add(stockPile.getName());
		}
	}
	
	private boolean isDumpAlreadyPresent(String dumpName){
		boolean isPresent = false;
		String[] exisitingDumpNames = dumpList.getItems();
		isPresent = Arrays.asList(exisitingDumpNames).contains(dumpName);
		return isPresent;
	}
	
	private boolean isStockpileAlreadyPresent(String stockpileName){
		boolean isPresent = false;
		String[] exisitingStockpileNames = stockpileList.getItems();
		isPresent = Arrays.asList(exisitingStockpileNames).contains(stockpileName);
		return isPresent;
	}
	
	private String[] getSourcePitItems(){
		
		this.sourcePitItems = new String[listOfPits.size()];
		for(int i=0; i<listOfPits.size(); i++){
			this.sourcePitItems[i] = listOfPits.get(i).getPitName();
	}

		return this.sourcePitItems;
	}
	
	private String[] getSourcePitGroupItems(){
		
		this.sourcePitGroupItems = new String[listOfPitGroups.size()];
		for(int i=0; i<listOfPitGroups.size(); i++){
			this.sourcePitGroupItems[i] = listOfPitGroups.get(i).getName();
	}

		return this.sourcePitGroupItems;
	}
	
	private Pit getPitByNameFromPitList(String pitName){
		Pit desiredPit = null;
		for(Pit pit: this.listOfPits){
			if(pit.getPitName().equals(pitName)){
				desiredPit = pit;
				break;
			}
		}
		return desiredPit;
	}
	
	private PitGroup getPitGroupByNameFromPitGroupList(String pitGroupName){
		PitGroup desiredPitGroup = null;
		for(PitGroup pitGroup: this.listOfPitGroups){
			if(pitGroup.getName().equals(pitGroupName)){
				desiredPitGroup = pitGroup;
				break;
			}
		}
		return desiredPitGroup;
	}

	public void refreshModelList(){
		this.pitList.setItems(this.getSourcePitItems());
	}

	@Override
	public void onGnosEventFired(GnosEvent e) {
		// TODO Auto-generated method stub

	}
}
