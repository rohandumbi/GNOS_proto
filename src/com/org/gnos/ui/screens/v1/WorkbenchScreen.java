package com.org.gnos.ui.screens.v1;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.custom.controls.GnosConfigurationStepLabel;
import com.org.gnos.custom.controls.GnosScreen;
import com.org.gnos.custom.models.ProjectModel;
import com.org.gnos.events.GnosEvent;


public class WorkbenchScreen extends GnosScreen {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private GnosConfigurationStepLabel gnosStepMapRequiredFieldsLabel;
	private GnosConfigurationStepLabel gnosStepDefineExpressionsLabel;
	private GnosConfigurationStepLabel gnosStepModelDefinitionLabel;
	private GnosConfigurationStepLabel gnosStepProcessRouteDefinitionLabel;
	private ProjectModel projectModel;
	
	
	public CLabel labelWorkbenchHeader;
	public WorkbenchScreen(Composite parent, int style, ProjectModel projectModel){
		super(parent, style);
		this.projectModel = projectModel;
		setFont(SWTResourceManager.getFont("Arial", 11, SWT.BOLD));
		setToolTipText("");
		setBackground(SWTResourceManager.getColor(255, 255, 255));
		setLayout(new FormLayout());
		
		labelWorkbenchHeader = new CLabel(this, SWT.NONE);
		labelWorkbenchHeader.setImage(SWTResourceManager.getImage(WorkbenchScreen.class, "/com/org/gnos/resources/settings16.png"));
		labelWorkbenchHeader.setForeground(SWTResourceManager.getColor(255, 255, 255));
		labelWorkbenchHeader.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		labelWorkbenchHeader.setBackground(SWTResourceManager.getColor(0, 102, 204));
		FormData fd_labelWorkbenchHeader = new FormData();
		fd_labelWorkbenchHeader.bottom = new FormAttachment(0, 34);
		fd_labelWorkbenchHeader.right = new FormAttachment(100);
		fd_labelWorkbenchHeader.top = new FormAttachment(0);
		fd_labelWorkbenchHeader.left = new FormAttachment(0);
		labelWorkbenchHeader.setLayoutData(fd_labelWorkbenchHeader);
		labelWorkbenchHeader.setText("Project Import Configuration");
		
		Label label = new Label(this, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_label = new FormData();
		fd_label.left = new FormAttachment(0, 260);
		fd_label.top = new FormAttachment(labelWorkbenchHeader, 6);
		fd_label.bottom = new FormAttachment(100, 10);
		fd_label.right = new FormAttachment(0, 262);
		label.setLayoutData(fd_label);
		
		
		gnosStepMapRequiredFieldsLabel = new GnosConfigurationStepLabel(this, SWT.NONE, "Map Required Fields");
		FormData fd_gnosStepMapRequiredFieldsLabel = new FormData();
		fd_gnosStepMapRequiredFieldsLabel.bottom = new FormAttachment(labelWorkbenchHeader, 40, SWT.BOTTOM);
		fd_gnosStepMapRequiredFieldsLabel.right = new FormAttachment(label, -6);
		fd_gnosStepMapRequiredFieldsLabel.top = new FormAttachment(labelWorkbenchHeader, 6);
		fd_gnosStepMapRequiredFieldsLabel.left = new FormAttachment(labelWorkbenchHeader, 10, SWT.LEFT);
		gnosStepMapRequiredFieldsLabel.setLayoutData(fd_gnosStepMapRequiredFieldsLabel);
		gnosStepMapRequiredFieldsLabel.setSelectedState();
		
		gnosStepDefineExpressionsLabel = new GnosConfigurationStepLabel(this, SWT.NONE, "Expression Definition");
		FormData fd_gnosStepDefineExpressionsLabel = new FormData();
		fd_gnosStepDefineExpressionsLabel.bottom = new FormAttachment(gnosStepMapRequiredFieldsLabel, 40, SWT.BOTTOM);
		fd_gnosStepDefineExpressionsLabel.right = new FormAttachment(label, -6);
		fd_gnosStepDefineExpressionsLabel.top = new FormAttachment(gnosStepMapRequiredFieldsLabel);
		fd_gnosStepDefineExpressionsLabel.left = new FormAttachment(0, 10);
		gnosStepDefineExpressionsLabel.setLayoutData(fd_gnosStepDefineExpressionsLabel);
		
		gnosStepModelDefinitionLabel = new GnosConfigurationStepLabel(this, SWT.NONE, "Model Definition");
		FormData fd_gnosStepModelDefinitionLabel = new FormData();
		fd_gnosStepModelDefinitionLabel.bottom = new FormAttachment(gnosStepDefineExpressionsLabel, 40, SWT.BOTTOM);
		fd_gnosStepModelDefinitionLabel.right = new FormAttachment(label, -6);
		fd_gnosStepModelDefinitionLabel.top = new FormAttachment(gnosStepDefineExpressionsLabel);
		fd_gnosStepModelDefinitionLabel.left = new FormAttachment(gnosStepMapRequiredFieldsLabel, 0, SWT.LEFT);
		gnosStepModelDefinitionLabel.setLayoutData(fd_gnosStepModelDefinitionLabel);
		
		gnosStepProcessRouteDefinitionLabel = new GnosConfigurationStepLabel(this, SWT.NONE, "Process Route Definition");
		FormData fd_gnosStepProcessRouteDefinitionLabel = new FormData();
		fd_gnosStepProcessRouteDefinitionLabel.bottom = new FormAttachment(gnosStepDefineExpressionsLabel, 80, SWT.BOTTOM);
		fd_gnosStepProcessRouteDefinitionLabel.right = new FormAttachment(label, -6);
		fd_gnosStepProcessRouteDefinitionLabel.top = new FormAttachment(gnosStepDefineExpressionsLabel, 40);
		fd_gnosStepProcessRouteDefinitionLabel.left = new FormAttachment(gnosStepMapRequiredFieldsLabel, 0, SWT.LEFT);
		gnosStepProcessRouteDefinitionLabel.setLayoutData(fd_gnosStepProcessRouteDefinitionLabel);
		
		MainConfigurationViewPort mainConfigurationViewport = new MainConfigurationViewPort(this, SWT.BORDER, this.projectModel);
		mainConfigurationViewport.registerEventListener(this);
		
		FormData fd_mainConfigurationViewport = new FormData();
		fd_mainConfigurationViewport.right = new FormAttachment(labelWorkbenchHeader, -6, SWT.RIGHT);
		fd_mainConfigurationViewport.bottom = new FormAttachment(100, -6);
		fd_mainConfigurationViewport.top = new FormAttachment(labelWorkbenchHeader, 6);
		fd_mainConfigurationViewport.left = new FormAttachment(label, 6);
		mainConfigurationViewport.setLayoutData(fd_mainConfigurationViewport);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}


	@Override
	public void onGnosEventFired(GnosEvent e) {
		// TODO Auto-generated method stub
		if(e.eventName == "complete:map-required-fields"){
			//mappingRequiredFieldsComplete();
			this.gnosStepMapRequiredFieldsLabel.setDeselectedState();
			this.gnosStepDefineExpressionsLabel.setSelectedState();
		}else if(e.eventName == "complete:expression-defintion"){
			//expressionDefinitionComplete();
			this.gnosStepDefineExpressionsLabel.setDeselectedState();
			this.gnosStepModelDefinitionLabel.setSelectedState();
		}else if(e.eventName == "complete:model-defintion"){
			//modelDefinitionComplete();
			this.gnosStepModelDefinitionLabel.setDeselectedState();
			this.gnosStepProcessRouteDefinitionLabel.setSelectedState();
		}else if(e.eventName == "complete:process-route-defintion"){
			//processRouteDefinitionComplete();
			this.gnosStepProcessRouteDefinitionLabel.setDeselectedState();
		}
	}
	
}
