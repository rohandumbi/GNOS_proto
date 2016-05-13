package com.org.gnos.ui.screens.v1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
import com.org.gnos.db.model.Model;
import com.org.gnos.events.GnosEvent;
import com.org.gnos.services.Node;
import com.org.gnos.services.Tree;
import com.org.gnos.ui.custom.controls.GnosScreen;
import com.org.gnos.ui.custom.controls.ProcessNodeDefinitionDialog;

public class PitGroupDumpStockpileDefinitionScreen extends GnosScreen {
	private String[] sourceFieldsComboItems;
	private List pitList;
	private List groupList;
	private List dumpList;
	private List stockpileList;
	
	private ArrayList<String> listAddedModels;
	private Tree processTree;
	private ProcessDiagramScreen compositeProcessDiagram;

	public PitGroupDumpStockpileDefinitionScreen(Composite parent, int style) {
		super(parent, style);
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		setLayout(new FormLayout());
		this.listAddedModels = new ArrayList<String>();
		

		this.processTree = ProjectConfigutration.getInstance().getProcessTree();
		if(this.processTree == null) {
			this.processTree = new Tree();
			Node rootNode = new Node("Block");
			rootNode.setSaved(true);
			this.processTree.getNodes().put("Block", rootNode);
			ProjectConfigutration.getInstance().setProcessTree(processTree);
			this.listAddedModels.add("Block");
		} else {
			Map<String, Node> nodes = this.processTree.getNodes();
			Set<String> keys = nodes.keySet();
			Iterator<String> it = keys.iterator();
			
			while (it.hasNext()) {
				String key = it.next();
				this.listAddedModels.add(key);
			}
		}
		
		Label labelScreenName = new Label(this, SWT.NONE);
		labelScreenName.setForeground(SWTResourceManager.getColor(0, 191, 255));
		labelScreenName.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_labelScreenName = new FormData();
		//fd_labelScreenName.bottom = new FormAttachment(100, -461);
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
		//fd_labelScreenDescription.right = new FormAttachment(0, 866);
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
		fd_labelSecondSeparator.left = new FormAttachment(88);
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
		lblAllGroups.setText("All Groups:");
		
		Label lblProcessDiagram = new Label(this, SWT.NONE);
		lblProcessDiagram.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblProcessDiagram.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblProcessDiagram = new FormData();
		//fd_lblProcessDiagram.bottom = new FormAttachment(lblAllModels, 0, SWT.BOTTOM);
		fd_lblProcessDiagram.top = new FormAttachment(labelFirstSeparator, 0, SWT.TOP);
		fd_lblProcessDiagram.left = new FormAttachment(labelFirstSeparator, 10);
		lblProcessDiagram.setLayoutData(fd_lblProcessDiagram);
		lblProcessDiagram.setText("Generated Grouping Diagram");
		
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
		this.pitList = new List(compositePitList, SWT.BORDER);
		this.pitList.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		//this.pitList.setItems(this.getSourceFieldsComboItems());
		this.pitList.setItems(new String[]{"pit 1","pit 2","pit 3","pit 4","pit 5","pit 6","pit 7","pit 8","pit 9","pit 10","pit 11","pit 12","pit 13","pit 14","pit 15","pit 16","pit 17","pit 18",});
		
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
		this.groupList = new List(compositeGroupList, SWT.BORDER);
		this.groupList.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		//this.pitList.setItems(this.getSourceFieldsComboItems());
		//this.pitList.setItems(new String[]{"dump 1","dump 2","dump 3","dump 4","dump 5","dump 6","dump 7","dump 8","dump 9","dump 10"});
		
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
		this.dumpList = new List(compositeDumpList, SWT.BORDER);
		this.dumpList.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		//this.pitList.setItems(this.getSourceFieldsComboItems());
		this.dumpList.setItems(new String[]{"dump 1","dump 2","dump 3","dump 4","dump 5","dump 6","dump 7","dump 8","dump 9","dump 10"});
		
		
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
		this.stockpileList = new List(compositeStockpileList, SWT.BORDER);
		this.stockpileList.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		//this.pitList.setItems(this.getSourceFieldsComboItems());
		this.stockpileList.setItems(new String[]{"stockpile 1","stockpile 2","stockpile 3","stockpile 4","stockpile 5","stockpile 6","stockpile 7","stockpile 8","stockpile 9","stockpile 10"});;
		
		
		/*
		 * Graphical Diagram
		 */
		this.compositeProcessDiagram = new ProcessDiagramScreen(this, SWT.BORDER);
		FormData fd_compositeProcessDiagram = new FormData();
		fd_compositeProcessDiagram.top = new FormAttachment(lblProcessDiagram, 10);
		fd_compositeProcessDiagram.left = new FormAttachment(labelFirstSeparator, 10);
		fd_compositeProcessDiagram.bottom = new FormAttachment(100, -10);
		fd_compositeProcessDiagram.right = new FormAttachment(labelSecondSeparator, -10);
		this.compositeProcessDiagram.setLayoutData(fd_compositeProcessDiagram);
		

		this.compositeProcessDiagram.refresh(processTree);

		/*
		 * Add pit to group button
		 */
		Button btnAddPitToGroup = new Button(this, SWT.NONE);
		btnAddPitToGroup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Add model to process implementation
				
				//TODO add pit to group handler
				
				
				/*String selectedModelName = pitList.getSelection()[0];
				System.out.println("Selected model: " + selectedModelName);
				ProcessNodeDefinitionDialog processNodeDefintiDefinitionDialog = new ProcessNodeDefinitionDialog(getShell(), listAddedModels);
				if (Window.OK == processNodeDefintiDefinitionDialog.open()) {
					String parent = processNodeDefintiDefinitionDialog.getParentName();
					System.out.println("Model: " + selectedModelName + " has Parent: " + parent);
					processTree.addNode(selectedModelName, parent);
					listAddedModels.add(selectedModelName);
					compositeProcessDiagram.refresh(processTree);
				}*/
				/*
				 * Test line to test tree DS
				 */
				processTree.display("Block");
			}
		});
		btnAddPitToGroup.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_btnAddPitToGroup = new FormData();
		fd_btnAddPitToGroup.bottom = new FormAttachment(lblAllPits, 0, SWT.BOTTOM);
		fd_btnAddPitToGroup.left = new FormAttachment(lblAllPits, 10);
		btnAddPitToGroup.setLayoutData(fd_btnAddPitToGroup);
		btnAddPitToGroup.setText("Add to Group");
		
		/*
		 * Add group to group button
		 */
		Button btnAddGroupToGroup = new Button(this, SWT.NONE);
		btnAddGroupToGroup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO add group to group handler
			}
		});
		btnAddGroupToGroup.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_btnAddGroupToGroup = new FormData();
		fd_btnAddGroupToGroup.bottom = new FormAttachment(lblAllGroups, 0, SWT.BOTTOM);
		fd_btnAddGroupToGroup.left = new FormAttachment(lblAllGroups, 10);
		btnAddGroupToGroup.setLayoutData(fd_btnAddGroupToGroup);
		btnAddGroupToGroup.setText("Add to Group");
		
		/*
		 * Add dump to group button
		 */
		Button btnAddDumpToGroup = new Button(this, SWT.NONE);
		btnAddDumpToGroup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO add group to group handler
			}
		});
		btnAddDumpToGroup.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_btnAddDumpToGroup = new FormData();
		fd_btnAddDumpToGroup.bottom = new FormAttachment(lblAllDumps, 0, SWT.BOTTOM);
		fd_btnAddDumpToGroup.left = new FormAttachment(lblAllDumps, 10);
		btnAddDumpToGroup.setLayoutData(fd_btnAddDumpToGroup);
		btnAddDumpToGroup.setText("Add to Group");
		
		/*
		 * Add stockpile to group button
		 */
		Button btnAddStockpileToGroup = new Button(this, SWT.NONE);
		btnAddStockpileToGroup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO add stockpile to group handler
			}
		});
		btnAddStockpileToGroup.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_btnAddStockpileToGroup = new FormData();
		fd_btnAddStockpileToGroup.bottom = new FormAttachment(lblAllStockpiles, 0, SWT.BOTTOM);
		fd_btnAddStockpileToGroup.left = new FormAttachment(lblAllStockpiles, 10);
		btnAddStockpileToGroup.setLayoutData(fd_btnAddStockpileToGroup);
		btnAddStockpileToGroup.setText("Add to Group");
	}
	
	private String[] getSourceFieldsComboItems(){
		
		java.util.List<Model> models = ProjectConfigutration.getInstance().getModels();
		this.sourceFieldsComboItems = new String[models.size()];
		for(int i=0; i<models.size(); i++){
			this.sourceFieldsComboItems[i] = models.get(i).getName();
	}

		return this.sourceFieldsComboItems;
	}

	public void refreshModelList(){
		this.pitList.setItems(this.getSourceFieldsComboItems());
	}

	@Override
	public void onGnosEventFired(GnosEvent e) {
		// TODO Auto-generated method stub

	}
}
