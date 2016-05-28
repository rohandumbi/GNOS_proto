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

import com.org.gnos.core.Node;
import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.core.Tree;
import com.org.gnos.db.model.Model;
import com.org.gnos.db.model.ProcessJoin;
import com.org.gnos.db.model.Product;
import com.org.gnos.events.GnosEvent;
import com.org.gnos.ui.custom.controls.GnosScreen;
import com.org.gnos.ui.custom.controls.ProcessJoinDefinitionDialog;
import com.org.gnos.ui.custom.controls.ProcessNodeDefinitionDialog;
import com.org.gnos.ui.graph.ProcessDefinitionGraph;

public class ProcessRouteDefinitionScreen extends GnosScreen {
	private String[] sourceFieldsComboItems;
	private List modelList;
	private List processJoinList;
	private ArrayList<String> listAddedModels;
	private Tree processTree;
	private ProcessDefinitionGraph compositeProcessDiagram;
	private java.util.List<ProcessJoin> listOfProcessJoins;
	private java.util.List<Product> listOfProducts;

	public ProcessRouteDefinitionScreen(Composite parent, int style) {
		super(parent, style);
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		setLayout(new FormLayout());
		this.listAddedModels = new ArrayList<String>();
		this.listAddedModels.add("Block");
		this.processTree = ProjectConfigutration.getInstance().getProcessTree();
		this.listOfProcessJoins = ProjectConfigutration.getInstance().getProcessJoins();
		this.listOfProducts = ProjectConfigutration.getInstance().getProductList();
		if(this.processTree == null) {
			this.processTree = new Tree();
			ProjectConfigutration.getInstance().setProcessTree(processTree);
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
		fd_labelScreenName.top = new FormAttachment(0, 10);
		fd_labelScreenName.left = new FormAttachment(0, 10);
		labelScreenName.setLayoutData(fd_labelScreenName);
		labelScreenName.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		labelScreenName.setText("Process Route Definition");
		
		Label labelScreenDescription = new Label(this, SWT.NONE);
		labelScreenDescription.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		labelScreenDescription.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_labelScreenDescription = new FormData();
		fd_labelScreenDescription.top = new FormAttachment(labelScreenName, 10, SWT.BOTTOM);
		fd_labelScreenDescription.left = new FormAttachment(0, 10);
		labelScreenDescription.setLayoutData(fd_labelScreenDescription);
		labelScreenDescription.setText("Design process flow using your defined models.");
	
		Label labelSectionSeparator = new Label(this, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_labelSectionSeparator = new FormData();
		fd_labelSectionSeparator.top = new FormAttachment(labelScreenDescription, 10, SWT.BOTTOM);
		fd_labelSectionSeparator.left = new FormAttachment(12);
		fd_labelSectionSeparator.bottom = new FormAttachment(100);
		labelSectionSeparator.setLayoutData(fd_labelSectionSeparator);

		Label lblAllModels = new Label(this, SWT.NONE);
		lblAllModels.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblAllModels.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		FormData fd_lblAllModels = new FormData();
		fd_lblAllModels.top = new FormAttachment(labelSectionSeparator, 0, SWT.TOP);
		fd_lblAllModels.left = new FormAttachment(0, 10);
		lblAllModels.setLayoutData(fd_lblAllModels);
		lblAllModels.setText("All Models :");
		
		Label lblAllProcessJoins = new Label(this, SWT.NONE);
		lblAllProcessJoins.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblAllProcessJoins.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		FormData fd_lblAllProcessJoins = new FormData();
		fd_lblAllProcessJoins.top = new FormAttachment(50);
		fd_lblAllProcessJoins.left = new FormAttachment(0, 10);
		lblAllProcessJoins.setLayoutData(fd_lblAllProcessJoins);
		lblAllProcessJoins.setText("All Joins :");
		
		Label lblProcessDiagram = new Label(this, SWT.NONE);
		lblProcessDiagram.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblProcessDiagram.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblProcessDiagram = new FormData();
		fd_lblProcessDiagram.top = new FormAttachment(labelSectionSeparator, 0, SWT.TOP);
		fd_lblProcessDiagram.left = new FormAttachment(labelSectionSeparator, 10);
		lblProcessDiagram.setLayoutData(fd_lblProcessDiagram);
		lblProcessDiagram.setText("Generated Process Diagram");
		
		
		/*
		 *Model List 
		 */
		final Composite compositeModelList = new Composite(this, SWT.BORDER);
		compositeModelList.setLayout(new FillLayout(SWT.HORIZONTAL));
		FormData fd_compositeModelList = new FormData();
		fd_compositeModelList.top = new FormAttachment(lblAllModels, 10);
		fd_compositeModelList.bottom = new FormAttachment(50, -10);
		fd_compositeModelList.left = new FormAttachment(0, 10);
		fd_compositeModelList.right = new FormAttachment(labelSectionSeparator, -10);
		compositeModelList.setLayoutData(fd_compositeModelList);
		this.modelList = new List(compositeModelList, SWT.BORDER);
		this.modelList.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		this.modelList.setItems(this.getModelNamesArray());
		
		/*
		 * Process Join List
		 */
		final Composite compositeJoinList = new Composite(this, SWT.BORDER);
		compositeJoinList.setLayout(new FillLayout(SWT.HORIZONTAL));
		FormData fd_compositeJoinList = new FormData();
		fd_compositeJoinList.top = new FormAttachment(lblAllProcessJoins, 10);
		fd_compositeJoinList.bottom = new FormAttachment(100, -10);
		fd_compositeJoinList.left = new FormAttachment(0, 10);
		fd_compositeJoinList.right = new FormAttachment(labelSectionSeparator, -10);
		compositeJoinList.setLayoutData(fd_compositeJoinList);
		this.processJoinList = new List(compositeJoinList, SWT.BORDER|SWT.MULTI|SWT.V_SCROLL);
		this.processJoinList.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		
		
		/*
		 * Graphical Diagram
		 */
		this.compositeProcessDiagram = new ProcessDefinitionGraph(this, SWT.BORDER);
		FormData fd_compositeProcessDiagram = new FormData();
		fd_compositeProcessDiagram.top = new FormAttachment(lblProcessDiagram, 10);
		fd_compositeProcessDiagram.left = new FormAttachment(labelSectionSeparator, 10);
		fd_compositeProcessDiagram.bottom = new FormAttachment(100, -10);
		fd_compositeProcessDiagram.right = new FormAttachment(100, -10);
		this.compositeProcessDiagram.setLayoutData(fd_compositeProcessDiagram);
		
		// Load graphical diagram with existing process tree, if any
		this.compositeProcessDiagram.refreshTree(processTree);
		
		// Load graphical diagram with existing process joins, if any
		if(this.listOfProcessJoins.size() > 0){
			for(ProcessJoin processJoin : this.listOfProcessJoins){
				this.compositeProcessDiagram.addProcessJoin(processJoin);
				this.processJoinList.add(processJoin.getName());
			}
		}
		
		// Load graphical diagram with existing products, if any
		if(this.listOfProducts.size() > 0){
			for(Product product : this.listOfProducts){
				this.compositeProcessDiagram.addProduct(product);;
			}
		}

		/*
		 * Add Model to Process Button
		 */
		Button btnAddModelToProcess = new Button(this, SWT.NONE);
		btnAddModelToProcess.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Add model to process implementation
				String selectedModelName = modelList.getSelection()[0];
				System.out.println("Selected model: " + selectedModelName);
				ProcessNodeDefinitionDialog processNodeDefintiDefinitionDialog = new ProcessNodeDefinitionDialog(getShell(), listAddedModels);
				if (Window.OK == processNodeDefintiDefinitionDialog.open()) {
					String parent = processNodeDefintiDefinitionDialog.getParentName();
					System.out.println("Model: " + selectedModelName + " has Parent: " + parent);
					Node node = new Node(ProjectConfigutration.getInstance().getModelByName(selectedModelName));
					Node parentNode = processTree.getNodeByName(parent);
					processTree.addNode(node, parentNode);
					listAddedModels.add(selectedModelName);
					compositeProcessDiagram.addProcess(node);
				}
			}
		});
		btnAddModelToProcess.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_btnAddModelToProcess = new FormData();
		fd_btnAddModelToProcess.bottom = new FormAttachment(lblAllModels, 5, SWT.BOTTOM);
		fd_btnAddModelToProcess.left = new FormAttachment(lblAllModels, 2);
		btnAddModelToProcess.setLayoutData(fd_btnAddModelToProcess);
		btnAddModelToProcess.setText("Add to Process");
		
		/*
		 * Button Create Process Join
		 */
		Button btnCreateProcessJoin = new Button(this, SWT.NONE);
		btnCreateProcessJoin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO create process join implementation
				ProcessJoinDefinitionDialog processJoinDefinitionDialog = new ProcessJoinDefinitionDialog(getShell(), listAddedModels);
				if (Window.OK == processJoinDefinitionDialog.open()) {
					String createdProcessJoinName = processJoinDefinitionDialog.getProcessJoinName();
					processJoinList.add(createdProcessJoinName);
					ProcessJoin createdProcessJoin = processJoinDefinitionDialog.getCreatedProcessJoin();
					ProjectConfigutration.getInstance().addProcessJoin(createdProcessJoin);
					compositeProcessDiagram.addProcessJoin(createdProcessJoin);
				}
			}
		});
		btnCreateProcessJoin.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_btnCreateProcessJoin = new FormData();
		fd_btnCreateProcessJoin.bottom = new FormAttachment(lblAllProcessJoins, 5, SWT.BOTTOM);
		fd_btnCreateProcessJoin.left = new FormAttachment(lblAllProcessJoins, 2);
		btnCreateProcessJoin.setLayoutData(fd_btnCreateProcessJoin);
		btnCreateProcessJoin.setText("Add New");
	}
	
	private String[] getModelNamesArray(){
		
		java.util.List<Model> models = ProjectConfigutration.getInstance().getModels();
		this.sourceFieldsComboItems = new String[models.size()];
		for(int i=0; i<models.size(); i++){
			this.sourceFieldsComboItems[i] = models.get(i).getName();
	}

		return this.sourceFieldsComboItems;
	}

	public void refreshModelList(){
		this.modelList.setItems(this.getModelNamesArray());
	}

	@Override
	public void onGnosEventFired(GnosEvent e) {
		// TODO Auto-generated method stub

	}
}
