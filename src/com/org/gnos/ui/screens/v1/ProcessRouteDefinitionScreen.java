package com.org.gnos.ui.screens.v1;

import java.util.List;

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
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.events.GnosEvent;
import com.org.gnos.services.Model;
import com.org.gnos.services.Models;
import com.org.gnos.services.ProcessNode;
import com.org.gnos.services.ProcessRoute;
import com.org.gnos.services.ProcessRoutes;
import com.org.gnos.ui.custom.controls.GnosScreen;
import com.org.gnos.ui.custom.controls.ProcessDefinitionDialog;
import com.org.gnos.ui.graph.GraphContainer;

public class ProcessRouteDefinitionScreen extends GnosScreen {


	//private Composite parent;
	private GraphContainer graphContainerComposite;
	private Composite parent;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ProcessRouteDefinitionScreen(Composite parent, int style) {
		super(parent, style);

		/*Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		lblNewLabel.setBounds(10, 10, 228, 32);
		lblNewLabel.setText("Process Route Definition Screen");

		Button buttonProcessRouteDefined = new Button(this, SWT.NONE);
		buttonProcessRouteDefined.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO process route defined
				GnosEvent event = new GnosEvent(this, "complete:process-route-defintion");
				triggerGnosEvent(event);
			}
		});
		buttonProcessRouteDefined.setBounds(241, 10, 75, 25);
		buttonProcessRouteDefined.setText("Next");*/

		setForeground(SWTResourceManager.getColor(30, 144, 255));
		setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.parent = parent;
		//this.projectModel = projectModel;
		//this.allHeaders = this.getAllHeaders();
		this.createContent();

	}
	
	private ProcessRoute createDummyProcess(){
		ProcessRoute dummyProcess = new ProcessRoute("test_process");
		List<Model> allModels = Models.getAll();
		for(Model model: allModels){
			ProcessNode processNode = new ProcessNode();
			processNode.setModel(model);
			dummyProcess.addNode(processNode);
		}
		ProcessRoutes.add(dummyProcess);
		return dummyProcess;
	}

	private void createContent(){
		setLayout(new FormLayout());
		Button buttonAddProcess = new Button(this, SWT.NONE);
		buttonAddProcess.setText("ADD A PROCESS");
		FormData fd_buttonAddProcess = new FormData();
		fd_buttonAddProcess.top = new FormAttachment(0, 6);
		//fd_buttonNext.right = new FormAttachment(btnAddNewRow, -5, SWT.LEFT);
		fd_buttonAddProcess.left = new FormAttachment(0, 6);
		//fd_buttonMapRqrdFields.right = new FormAttachment(0, 282);
		buttonAddProcess.setLayoutData(fd_buttonAddProcess);
		
		this.graphContainerComposite = new GraphContainer(this, SWT.BORDER);
		this.graphContainerComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		FormData fd_containerComposite = new FormData();
		fd_containerComposite.left = new FormAttachment(25, 6);
		fd_containerComposite.right = new FormAttachment(100, -6);
		fd_containerComposite.top = new FormAttachment(0, 6);
		fd_containerComposite.bottom = new FormAttachment(100, -6);
		this.graphContainerComposite.setLayoutData(fd_containerComposite);

		buttonAddProcess.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO mapping complete
				//graphContainerComposite.addProcessToGraph();
				ProcessDefinitionDialog processDefinitionDialog = new ProcessDefinitionDialog(parent.getShell());
				//processDefinitionDialog.open();
				ProcessRoute newProcess = new ProcessRoute();
				if (Window.OK == processDefinitionDialog.open()) {
					newProcess = processDefinitionDialog.getDefinedProcessRoute();
				}
				
				//ProcessRoute newProcess = createDummyProcess();
				graphContainerComposite.addProcessToGraph(newProcess);
			}
		});
		
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}


	@Override
	public void onGnosEventFired(GnosEvent e) {
		// TODO Auto-generated method stub

	}

	private void triggerGnosEvent(GnosEvent event){
		int j = listeners.size();
		int i = 0;
		for(i=0; i<j; i++){
			listeners.get(i).onGnosEventFired(event);
		}
	}

}
