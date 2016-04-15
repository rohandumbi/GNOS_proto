package com.org.gnos.ui.screens.v1;

import java.util.ArrayList;

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

import com.org.gnos.events.GnosEvent;
import com.org.gnos.services.Model;
import com.org.gnos.services.Models;
import com.org.gnos.services.Tree;
import com.org.gnos.ui.custom.controls.GnosScreen;
import com.org.gnos.ui.custom.controls.ProcessNodeDefinitionDialog;

public class ProcessRouteDefinitionScreen_V2 extends GnosScreen {
	private String[] sourceFieldsComboItems;
	private List modelList;
	private ArrayList<String> listAddedModels;
	private Tree processTree;

	public ProcessRouteDefinitionScreen_V2(Composite parent, int style) {
		super(parent, style);
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		setLayout(new FormLayout());
		this.listAddedModels = new ArrayList<String>();
		this.listAddedModels.add("Block");
		
		Label labelSectionSeparator = new Label(this, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_labelSectionSeparator = new FormData();
		fd_labelSectionSeparator.top = new FormAttachment(0);
		fd_labelSectionSeparator.left = new FormAttachment(25);
		fd_labelSectionSeparator.bottom = new FormAttachment(100);
		labelSectionSeparator.setLayoutData(fd_labelSectionSeparator);
		
		Label lblAllModels = new Label(this, SWT.NONE);
		lblAllModels.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblAllModels.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		FormData fd_lblAllModels = new FormData();
		fd_lblAllModels.top = new FormAttachment(0, 10);
		fd_lblAllModels.left = new FormAttachment(0, 10);
		lblAllModels.setLayoutData(fd_lblAllModels);
		lblAllModels.setText("All Models");
		
		Label lblProcessDiagram = new Label(this, SWT.NONE);
		lblProcessDiagram.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblProcessDiagram.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		FormData fd_lblProcessDiagram = new FormData();
		fd_lblProcessDiagram.bottom = new FormAttachment(lblAllModels, 0, SWT.BOTTOM);
		fd_lblProcessDiagram.left = new FormAttachment(labelSectionSeparator, 10);
		lblProcessDiagram.setLayoutData(fd_lblProcessDiagram);
		lblProcessDiagram.setText("Generated Process Diagram");
		
		final Composite compositeModelList = new Composite(this, SWT.BORDER);
		compositeModelList.setLayout(new FillLayout(SWT.HORIZONTAL));
		FormData fd_compositeModelList = new FormData();
		fd_compositeModelList.top = new FormAttachment(lblAllModels, 10);
		fd_compositeModelList.bottom = new FormAttachment(100, -10);
		fd_compositeModelList.left = new FormAttachment(0, 10);
		fd_compositeModelList.right = new FormAttachment(labelSectionSeparator, -10);
		compositeModelList.setLayoutData(fd_compositeModelList);
		
		this.modelList = new List(compositeModelList, SWT.BORDER);
		modelList.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		this.modelList.setItems(this.getSourceFieldsComboItems());
		
		Composite compositeProcessDiagram = new ProcessDiagramScreen(this, SWT.BORDER);
		FormData fd_compositeProcessDiagram = new FormData();
		fd_compositeProcessDiagram.top = new FormAttachment(lblProcessDiagram, 10);
		fd_compositeProcessDiagram.left = new FormAttachment(labelSectionSeparator, 10);
		fd_compositeProcessDiagram.bottom = new FormAttachment(100, -10);
		fd_compositeProcessDiagram.right = new FormAttachment(100, -10);
		compositeProcessDiagram.setLayoutData(fd_compositeProcessDiagram);
		
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
				}
			}
		});
		btnAddModelToProcess.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		FormData fd_btnAddModelToProcess = new FormData();
		fd_btnAddModelToProcess.bottom = new FormAttachment(lblAllModels, 0, SWT.BOTTOM);
		fd_btnAddModelToProcess.left = new FormAttachment(lblAllModels, 20);
		btnAddModelToProcess.setLayoutData(fd_btnAddModelToProcess);
		btnAddModelToProcess.setText("Add to Process");
	}
	
	private String[] getSourceFieldsComboItems(){

		java.util.List<Model> models = Models.getAll();
		this.sourceFieldsComboItems = new String[models.size()];
		for(int i=0; i<models.size(); i++){
			this.sourceFieldsComboItems[i] = models.get(i).getName();
		}

		return this.sourceFieldsComboItems;
	}
	
	public void refreshModelList(){
		this.modelList.setItems(this.getSourceFieldsComboItems());
	}

	@Override
	public void onGnosEventFired(GnosEvent e) {
		// TODO Auto-generated method stub

	}
}
