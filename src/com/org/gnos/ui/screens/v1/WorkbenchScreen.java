package com.org.gnos.ui.screens.v1;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.core.ScenarioConfigutration;
import com.org.gnos.custom.models.ProjectModel;
import com.org.gnos.equation.ObjectiveFunctionEquationGenerator;
import com.org.gnos.equation.ProcessConstraintEquationGenerator;
import com.org.gnos.events.GnosEvent;
import com.org.gnos.ui.custom.controls.GnosConfigurationStepLabel;
import com.org.gnos.ui.custom.controls.GnosScreen;


public class WorkbenchScreen extends GnosScreen {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private GnosConfigurationStepLabel gnosStepDefineFieldTypeLabel;
	private GnosConfigurationStepLabel gnosStepMapRequiredFieldsLabel;
	private GnosConfigurationStepLabel gnosStepDefineExpressionsLabel;
	private GnosConfigurationStepLabel gnosStepModelDefinitionLabel;
	private GnosConfigurationStepLabel gnosStepProcessRouteDefinitionLabel;
	private GnosConfigurationStepLabel gnosStepPitGroupDefinitionLabel;
	private GnosConfigurationStepLabel gnosStepOpexDefinitionLabel;
	private GnosConfigurationStepLabel gnosStepProcessConstraintsDefinitionLabel;
	private GnosConfigurationStepLabel gnosStepGradeConstraintsDefinitionLabel;
	private GnosConfigurationStepLabel gnosStepBenchConstraintsDefinitionLabel;
	private GnosConfigurationStepLabel gnosPitDependencyLabel;
	private ScrolledComposite scViewPortContainer;
	private MainConfigurationViewPort mainConfigurationViewPort;


	public CLabel labelWorkbenchHeader;
	private Button btnHome;
	private Button btnSave;
	private Button btnRun;
	
	public WorkbenchScreen(Composite parent, int style){
		super(parent, style);
		setFont(SWTResourceManager.getFont("Arial", 11, SWT.BOLD));
		setToolTipText("");
		setBackground(SWTResourceManager.getColor(255, 255, 255));
		setLayout(new FormLayout());
		
		btnHome = new Button(this, SWT.NONE);
		btnHome.setImage(SWTResourceManager.getImage(WorkbenchScreen.class, "/com/org/gnos/resources/home24.png"));
		final WorkbenchScreen me = this;
		btnHome.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				GnosEvent event = new GnosEvent(me, "open:homeScreen");
				fireChildEvent(event);
			}
		});
		btnHome.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		FormData fd_btnHome = new FormData();
		fd_btnHome.top = new FormAttachment(0);
		fd_btnHome.right = new FormAttachment(100, -10);
		btnHome.setLayoutData(fd_btnHome);
		
		btnSave = new Button(this, SWT.NONE);
		btnSave.setImage(SWTResourceManager.getImage(WorkbenchScreen.class, "/com/org/gnos/resources/save-disk.png"));
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				GnosEvent event = new GnosEvent(me, "save:clicked");
				fireChildEvent(event);
			}
		});
		btnSave.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		FormData fd_btnSave = new FormData();
		fd_btnSave.top = new FormAttachment(0);
		fd_btnSave.right = new FormAttachment(btnHome, -10, SWT.LEFT);
		btnSave.setLayoutData(fd_btnSave);
		
		btnRun = new Button(this, SWT.NONE);
		btnRun.setImage(SWTResourceManager.getImage(WorkbenchScreen.class, "/com/org/gnos/resources/run_24.png"));
		btnRun.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				GnosEvent event = new GnosEvent(me, "run:clicked");
				fireChildEvent(event);
			}
		});
		btnRun.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		FormData fd_btnRun = new FormData();
		fd_btnRun.top = new FormAttachment(0);
		fd_btnRun.right = new FormAttachment(btnSave, -10, SWT.LEFT);
		btnRun.setLayoutData(fd_btnRun);
		
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


		gnosStepDefineFieldTypeLabel = new GnosConfigurationStepLabel(this, SWT.NONE, "Define Field Type");
		FormData fd_gnosStepDefineFieldTypeLabel = new FormData();
		fd_gnosStepDefineFieldTypeLabel.bottom = new FormAttachment(labelWorkbenchHeader, 40, SWT.BOTTOM);
		fd_gnosStepDefineFieldTypeLabel.right = new FormAttachment(label, -6);
		fd_gnosStepDefineFieldTypeLabel.top = new FormAttachment(labelWorkbenchHeader, 6);
		fd_gnosStepDefineFieldTypeLabel.left = new FormAttachment(0, 10);
		gnosStepDefineFieldTypeLabel.setLayoutData(fd_gnosStepDefineFieldTypeLabel);
		gnosStepDefineFieldTypeLabel.setSelectedState();
		gnosStepDefineFieldTypeLabel.registerEventListener(this);


		gnosStepMapRequiredFieldsLabel = new GnosConfigurationStepLabel(this, SWT.NONE, "Map Required Fields");
		FormData fd_gnosStepMapRequiredFieldsLabel = new FormData();
		fd_gnosStepMapRequiredFieldsLabel.bottom = new FormAttachment(gnosStepDefineFieldTypeLabel, 40, SWT.BOTTOM);
		fd_gnosStepMapRequiredFieldsLabel.right = new FormAttachment(label, -6);
		fd_gnosStepMapRequiredFieldsLabel.top = new FormAttachment(gnosStepDefineFieldTypeLabel, 6);
		fd_gnosStepMapRequiredFieldsLabel.left = new FormAttachment(0, 10);
		gnosStepMapRequiredFieldsLabel.setLayoutData(fd_gnosStepMapRequiredFieldsLabel);
		//gnosStepMapRequiredFieldsLabel.setSelectedState();
		gnosStepMapRequiredFieldsLabel.registerEventListener(this);

		gnosStepDefineExpressionsLabel = new GnosConfigurationStepLabel(this, SWT.NONE, "Expression Definition");
		FormData fd_gnosStepDefineExpressionsLabel = new FormData();
		fd_gnosStepDefineExpressionsLabel.bottom = new FormAttachment(gnosStepMapRequiredFieldsLabel, 40, SWT.BOTTOM);
		fd_gnosStepDefineExpressionsLabel.right = new FormAttachment(label, -6);
		fd_gnosStepDefineExpressionsLabel.top = new FormAttachment(gnosStepMapRequiredFieldsLabel);
		fd_gnosStepDefineExpressionsLabel.left = new FormAttachment(0, 10);
		gnosStepDefineExpressionsLabel.setLayoutData(fd_gnosStepDefineExpressionsLabel);
		gnosStepDefineExpressionsLabel.registerEventListener(this);

		gnosStepModelDefinitionLabel = new GnosConfigurationStepLabel(this, SWT.NONE, "Model Definition");
		FormData fd_gnosStepModelDefinitionLabel = new FormData();
		fd_gnosStepModelDefinitionLabel.bottom = new FormAttachment(gnosStepDefineExpressionsLabel, 40, SWT.BOTTOM);
		fd_gnosStepModelDefinitionLabel.right = new FormAttachment(label, -6);
		fd_gnosStepModelDefinitionLabel.top = new FormAttachment(gnosStepDefineExpressionsLabel);
		fd_gnosStepModelDefinitionLabel.left = new FormAttachment(gnosStepMapRequiredFieldsLabel, 0, SWT.LEFT);
		gnosStepModelDefinitionLabel.setLayoutData(fd_gnosStepModelDefinitionLabel);
		gnosStepModelDefinitionLabel.registerEventListener(this);

		gnosStepProcessRouteDefinitionLabel = new GnosConfigurationStepLabel(this, SWT.NONE, "Process Route Definition");
		FormData fd_gnosStepProcessRouteDefinitionLabel = new FormData();
		fd_gnosStepProcessRouteDefinitionLabel.bottom = new FormAttachment(gnosStepModelDefinitionLabel, 40, SWT.BOTTOM);
		fd_gnosStepProcessRouteDefinitionLabel.right = new FormAttachment(label, -6);
		fd_gnosStepProcessRouteDefinitionLabel.top = new FormAttachment(gnosStepModelDefinitionLabel);
		fd_gnosStepProcessRouteDefinitionLabel.left = new FormAttachment(gnosStepMapRequiredFieldsLabel, 0, SWT.LEFT);
		gnosStepProcessRouteDefinitionLabel.setLayoutData(fd_gnosStepProcessRouteDefinitionLabel);
		gnosStepProcessRouteDefinitionLabel.registerEventListener(this);
		
		gnosStepPitGroupDefinitionLabel = new GnosConfigurationStepLabel(this, SWT.NONE, "PitGroup Dump and Stockpile");
		FormData fd_gnosStepPitGroupDefinitionLabel = new FormData();
		fd_gnosStepPitGroupDefinitionLabel.bottom = new FormAttachment(gnosStepProcessRouteDefinitionLabel, 40, SWT.BOTTOM);
		fd_gnosStepPitGroupDefinitionLabel.right = new FormAttachment(label, -6);
		fd_gnosStepPitGroupDefinitionLabel.top = new FormAttachment(gnosStepProcessRouteDefinitionLabel);
		fd_gnosStepPitGroupDefinitionLabel.left = new FormAttachment(gnosStepProcessRouteDefinitionLabel, 0, SWT.LEFT);
		gnosStepPitGroupDefinitionLabel.setLayoutData(fd_gnosStepPitGroupDefinitionLabel);
		gnosStepPitGroupDefinitionLabel.registerEventListener(this);
		
		
		gnosStepOpexDefinitionLabel = new GnosConfigurationStepLabel(this, SWT.NONE, "OPEX Definition");
		FormData fd_gnosStepOpexDefinitionLabel = new FormData();
		fd_gnosStepOpexDefinitionLabel.bottom = new FormAttachment(gnosStepPitGroupDefinitionLabel, 40, SWT.BOTTOM);
		fd_gnosStepOpexDefinitionLabel.right = new FormAttachment(label, -6);
		fd_gnosStepOpexDefinitionLabel.top = new FormAttachment(gnosStepPitGroupDefinitionLabel);
		fd_gnosStepOpexDefinitionLabel.left = new FormAttachment(gnosStepPitGroupDefinitionLabel, 0, SWT.LEFT);
		gnosStepOpexDefinitionLabel.setLayoutData(fd_gnosStepOpexDefinitionLabel);
		gnosStepOpexDefinitionLabel.registerEventListener(this);
		
		gnosStepProcessConstraintsDefinitionLabel = new GnosConfigurationStepLabel(this, SWT.NONE, "Process Constraint Definition");
		FormData fd_gnosStepProcessConstraintsDefinitionLabel = new FormData();
		fd_gnosStepProcessConstraintsDefinitionLabel.bottom = new FormAttachment(gnosStepOpexDefinitionLabel, 40, SWT.BOTTOM);
		fd_gnosStepProcessConstraintsDefinitionLabel.right = new FormAttachment(label, -6);
		fd_gnosStepProcessConstraintsDefinitionLabel.top = new FormAttachment(gnosStepOpexDefinitionLabel);
		fd_gnosStepProcessConstraintsDefinitionLabel.left = new FormAttachment(gnosStepOpexDefinitionLabel, 0, SWT.LEFT);
		gnosStepProcessConstraintsDefinitionLabel.setLayoutData(fd_gnosStepProcessConstraintsDefinitionLabel);
		gnosStepProcessConstraintsDefinitionLabel.registerEventListener(this);
		
		gnosStepGradeConstraintsDefinitionLabel = new GnosConfigurationStepLabel(this, SWT.NONE, "Grade Constraint Definition");
		FormData fd_gnosStepGradeConstraintsDefinitionLabel = new FormData();
		fd_gnosStepGradeConstraintsDefinitionLabel.bottom = new FormAttachment(gnosStepProcessConstraintsDefinitionLabel, 40, SWT.BOTTOM);
		fd_gnosStepGradeConstraintsDefinitionLabel.right = new FormAttachment(label, -6);
		fd_gnosStepGradeConstraintsDefinitionLabel.top = new FormAttachment(gnosStepProcessConstraintsDefinitionLabel);
		fd_gnosStepGradeConstraintsDefinitionLabel.left = new FormAttachment(gnosStepProcessConstraintsDefinitionLabel, 0, SWT.LEFT);
		gnosStepGradeConstraintsDefinitionLabel.setLayoutData(fd_gnosStepGradeConstraintsDefinitionLabel);
		gnosStepGradeConstraintsDefinitionLabel.registerEventListener(this);
		
		gnosStepBenchConstraintsDefinitionLabel = new GnosConfigurationStepLabel(this, SWT.NONE, "Bench Constraint Definition");
		FormData fd_gnosStepBenchConstraintsDefinitionLabel = new FormData();
		fd_gnosStepBenchConstraintsDefinitionLabel.bottom = new FormAttachment(gnosStepGradeConstraintsDefinitionLabel, 40, SWT.BOTTOM);
		fd_gnosStepBenchConstraintsDefinitionLabel.right = new FormAttachment(label, -6);
		fd_gnosStepBenchConstraintsDefinitionLabel.top = new FormAttachment(gnosStepGradeConstraintsDefinitionLabel);
		fd_gnosStepBenchConstraintsDefinitionLabel.left = new FormAttachment(gnosStepGradeConstraintsDefinitionLabel, 0, SWT.LEFT);
		gnosStepBenchConstraintsDefinitionLabel.setLayoutData(fd_gnosStepBenchConstraintsDefinitionLabel);
		gnosStepBenchConstraintsDefinitionLabel.registerEventListener(this);
		
		gnosPitDependencyLabel = new GnosConfigurationStepLabel(this, SWT.NONE, "Pit Dependency");
		FormData fd_gnosPitDependencyLabel = new FormData();
		fd_gnosPitDependencyLabel.bottom = new FormAttachment(gnosStepBenchConstraintsDefinitionLabel, 40, SWT.BOTTOM);
		fd_gnosPitDependencyLabel.right = new FormAttachment(label, -6);
		fd_gnosPitDependencyLabel.top = new FormAttachment(gnosStepBenchConstraintsDefinitionLabel);
		fd_gnosPitDependencyLabel.left = new FormAttachment(gnosStepBenchConstraintsDefinitionLabel, 0, SWT.LEFT);
		gnosPitDependencyLabel.setLayoutData(fd_gnosPitDependencyLabel);
		gnosPitDependencyLabel.registerEventListener(this);
		
		this.scViewPortContainer = new ScrolledComposite(this, SWT.V_SCROLL | SWT.NONE);
		FormData fd_scViewPortContainer = new FormData();
		fd_scViewPortContainer.right = new FormAttachment(labelWorkbenchHeader, -6, SWT.RIGHT);
		fd_scViewPortContainer.bottom = new FormAttachment(100, -6);
		fd_scViewPortContainer.top = new FormAttachment(labelWorkbenchHeader, 6);
		fd_scViewPortContainer.left = new FormAttachment(label, 6);

		this.mainConfigurationViewPort = new MainConfigurationViewPort(this.scViewPortContainer, SWT.BORDER);
		this.mainConfigurationViewPort.registerEventListener(this);
		this.mainConfigurationViewPort.loadFieldDatatypeDefinitionScreen();

		this.scViewPortContainer.setContent(this.mainConfigurationViewPort);
		this.scViewPortContainer.setExpandHorizontal(true);
		this.scViewPortContainer.setExpandVertical(true);
		this.scViewPortContainer.setLayout(new FillLayout());
		//this.scViewPortContainer.setMinSize(this.mainConfigurationViewPort.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scViewPortContainer.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				System.out.println("MVCP resized");
				Rectangle r = scViewPortContainer.getClientArea();
				//scViewPortContainer.setMinSize(mainConfigurationViewPort.computeSize(r.width, SWT.DEFAULT));
				scViewPortContainer.setMinSize(mainConfigurationViewPort.computeSize(r.width, SWT.DEFAULT, true));
			}
		});
		this.scViewPortContainer.setLayoutData(fd_scViewPortContainer);
		
	}

	public void setScrolledCompositeMinSize(){
		Rectangle r = scViewPortContainer.getClientArea();
		scViewPortContainer.setMinSize(mainConfigurationViewPort.computeSize(r.width, SWT.DEFAULT));
		scViewPortContainer.setOrigin(mainConfigurationViewPort.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	private void fireChildEvent(GnosEvent event){
		int j = listeners.size();
		int i = 0;
		for(i=0; i<j; i++){
			listeners.get(i).onGnosEventFired(event);
		}
	}


	private void selectStepLabel(GnosConfigurationStepLabel label) {
		gnosStepDefineFieldTypeLabel.setDeselectedState();
		gnosStepMapRequiredFieldsLabel.setDeselectedState();
		gnosStepDefineExpressionsLabel.setDeselectedState();
		gnosStepModelDefinitionLabel.setDeselectedState();
		gnosStepProcessRouteDefinitionLabel.setDeselectedState();
		gnosStepPitGroupDefinitionLabel.setDeselectedState();
		gnosStepOpexDefinitionLabel.setDeselectedState();
		gnosStepProcessConstraintsDefinitionLabel.setDeselectedState();
		gnosStepGradeConstraintsDefinitionLabel.setDeselectedState();
		gnosStepBenchConstraintsDefinitionLabel.setDeselectedState();
		gnosPitDependencyLabel.setDeselectedState();

		label.setSelectedState();
	}
	@Override
	public void onGnosEventFired(GnosEvent e) {
		System.out.println("Screen navigation done :"+e.eventName);
		if(e.eventName == "complete:datatype-defintion"){
			this.gnosStepDefineFieldTypeLabel.setDeselectedState();
			this.gnosStepMapRequiredFieldsLabel.setSelectedState();
		}else if(e.eventName == "complete:map-required-fields"){
			this.gnosStepMapRequiredFieldsLabel.setDeselectedState();
			this.gnosStepDefineExpressionsLabel.setSelectedState();
		}else if(e.eventName == "complete:expression-defintion"){
			this.gnosStepDefineExpressionsLabel.setDeselectedState();
			this.gnosStepModelDefinitionLabel.setSelectedState();
		}else if(e.eventName == "complete:model-defintion"){
			this.gnosStepModelDefinitionLabel.setDeselectedState();
			this.gnosStepProcessRouteDefinitionLabel.setSelectedState();
		}else if(e.eventName == "complete:process-route-defintion"){
			this.gnosStepProcessRouteDefinitionLabel.setDeselectedState();
			this.gnosStepPitGroupDefinitionLabel.setSelectedState();
		}
		
		if(e.eventName == "Define Field Type"){
			selectStepLabel(gnosStepDefineFieldTypeLabel);		
			mainConfigurationViewPort.loadFieldDatatypeDefinitionScreen();
			
		}else if(e.eventName == "Map Required Fields"){
			selectStepLabel(gnosStepMapRequiredFieldsLabel);			
			mainConfigurationViewPort.loadMapRequiredFieldsScreen();
			
		}else if(e.eventName == "Expression Definition"){
			selectStepLabel(gnosStepDefineExpressionsLabel);			
			mainConfigurationViewPort.loadExpressionDefinitionScreen();
			
		}else if(e.eventName == "Model Definition"){
			selectStepLabel(gnosStepModelDefinitionLabel);			
			mainConfigurationViewPort.loadModelDefinitionScreen();
			
		}else if(e.eventName == "Process Route Definition"){
			selectStepLabel(gnosStepProcessRouteDefinitionLabel);			
			mainConfigurationViewPort.loadProcessRouteDefinitionScreen();
		}else if(e.eventName == "PitGroup Dump and Stockpile"){
			selectStepLabel(gnosStepPitGroupDefinitionLabel);
			mainConfigurationViewPort.loadPitGroupDefinitionScreen();
		}else if(e.eventName == "OPEX Definition"){
			selectStepLabel(gnosStepOpexDefinitionLabel);
			mainConfigurationViewPort.loadOpexDefinitionScreen();
		}else if(e.eventName == "Process Constraint Definition"){
			selectStepLabel(gnosStepProcessConstraintsDefinitionLabel);
			mainConfigurationViewPort.loadProcessConstraintDefinitionScreen();
		}else if(e.eventName == "Grade Constraint Definition"){
			selectStepLabel(gnosStepGradeConstraintsDefinitionLabel);
			mainConfigurationViewPort.loadGradeConstraintDefinitionScreen();
		}else if(e.eventName == "Bench Constraint Definition"){
			selectStepLabel(gnosStepBenchConstraintsDefinitionLabel);
			mainConfigurationViewPort.loadBenchConstraintDefinitionScreen();
		}else if(e.eventName == "Pit Dependency"){
			selectStepLabel(gnosPitDependencyLabel);
			System.out.println("Pit Dependency to show up");
			mainConfigurationViewPort.loadPitDependencyScreen();
		}
		
		
		this.scViewPortContainer.setMinSize(this.mainConfigurationViewPort.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

}
