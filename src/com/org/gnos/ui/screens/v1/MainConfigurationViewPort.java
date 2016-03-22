package com.org.gnos.ui.screens.v1;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.org.gnos.custom.controls.GnosScreen;
import com.org.gnos.custom.models.ProjectModel;
import com.org.gnos.events.GnosEvent;

public class MainConfigurationViewPort extends GnosScreen{

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private ProjectModel projectModel;
	private GnosScreen viewPort;
	private StackLayout stackLayout;
	private FieldDatatypeDefinitionScreen fieldDatatypeDefinitionScreen;
	private MapRequiredFieldsScreen mapRequiredFieldsScreen;
	private ExpressionDefinitionScreen expressionDefinitionScreen;
	private ModelDefinitionScreen modelDefinitionScreen;
	private ProcessRouteDefinitionScreen processRouteDefinitionScreen;
	private Shell dummyShell;
	
	//private Layout mainLayout;
	public MainConfigurationViewPort(Composite parent, int style, ProjectModel projectModel){
		super(parent, style);
		this.projectModel = projectModel;
		this.stackLayout = new StackLayout();
		this.setLayout(stackLayout);
		this.dummyShell = new Shell();
		//this.parent = parent;
		//this.loadFieldDatatypeDefinitionScreen();
		//this.setMinSize(this.viewPort.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		fieldDatatypeDefinitionScreen = new FieldDatatypeDefinitionScreen(this.dummyShell, SWT.NONE, this.projectModel);
		fieldDatatypeDefinitionScreen.registerEventListener(this);
		
		mapRequiredFieldsScreen = new MapRequiredFieldsScreen(this.dummyShell, SWT.NONE, this.projectModel);
		mapRequiredFieldsScreen.registerEventListener(this);
		
		expressionDefinitionScreen = new ExpressionDefinitionScreen(this.dummyShell, SWT.NONE, this.projectModel);
		expressionDefinitionScreen.registerEventListener(this);
		
		modelDefinitionScreen = new ModelDefinitionScreen(this.dummyShell, SWT.NONE, this.projectModel);
		modelDefinitionScreen.registerEventListener(this);
		
		processRouteDefinitionScreen = new ProcessRouteDefinitionScreen(this.dummyShell, SWT.NONE);
		processRouteDefinitionScreen.registerEventListener(this);
	}
	
	public void loadFieldDatatypeDefinitionScreen(){
		/*this.setLayout(new FillLayout());
		this.viewPort = new FieldDatatypeDefinitionScreen(this, SWT.NONE, this.projectModel);
		this.viewPort.registerEventListener(this);
		this.layout();*/
		/*fieldDatatypeDefinitionScreen = new FieldDatatypeDefinitionScreen(this, SWT.NONE, this.projectModel);
		fieldDatatypeDefinitionScreen.registerEventListener(this);*/
		if(this.viewPort != null){
			//this.viewPort.dispose();
			this.viewPort.setParent(dummyShell);
		}
		this.viewPort = fieldDatatypeDefinitionScreen;
		this.viewPort.setParent(this);
		this.stackLayout.topControl = this.viewPort;
		this.layout();
	}

	public void loadMapRequiredFieldsScreen(){
		/*this.viewPort.dispose();
		this.setLayout(new FillLayout());
		this.viewPort = new MapRequiredFieldsScreen(this, SWT.NONE, this.projectModel);
		this.viewPort.registerEventListener(this);
		this.layout();*/
		/*mapRequiredFieldsScreen = new MapRequiredFieldsScreen(this, SWT.NONE, this.projectModel);
		mapRequiredFieldsScreen.registerEventListener(this);*/
		//this.viewPort.dispose();
		this.viewPort.setParent(dummyShell);
		this.viewPort = mapRequiredFieldsScreen;
		this.viewPort.setParent(this);
		this.stackLayout.topControl = this.viewPort;
		this.layout();
	}

	public void loadExpressionDefinitionScreen(){
		/*this.viewPort.dispose();
		this.setLayout(new FillLayout());
		this.viewPort = new ExpressionDefinitionScreen(this, SWT.NONE, this.projectModel);
		this.viewPort.registerEventListener(this);
		this.layout();*/
		/*expressionDefinitionScreen = new ExpressionDefinitionScreen(this, SWT.NONE, this.projectModel);
		expressionDefinitionScreen.registerEventListener(this);*/
		//this.viewPort.dispose();
		this.viewPort.setParent(dummyShell);
		this.viewPort = expressionDefinitionScreen;
		this.viewPort.setParent(this);
		this.stackLayout.topControl = this.viewPort;
		this.layout();
	}

	public void loadModelDefinitionScreen(){
		/*this.viewPort.dispose();
		this.setLayout(new FillLayout());
		this.viewPort = new ModelDefinitionScreen(this, SWT.NONE);
		this.viewPort.registerEventListener(this);
		this.layout();*/
		/*modelDefinitionScreen = new ModelDefinitionScreen(this, SWT.NONE);
		modelDefinitionScreen.registerEventListener(this);*/
		//this.viewPort.dispose();
		this.viewPort.setParent(dummyShell);
		this.viewPort = modelDefinitionScreen;
		this.viewPort.setParent(this);
		this.stackLayout.topControl = modelDefinitionScreen;
		this.layout();
	}

	public void loadProcessRouteDefinitionScreen(){
		/*this.viewPort.dispose();
		this.setLayout(new FillLayout());
		this.viewPort = new ProcessRouteDefinitionScreen(this, SWT.NONE);
		this.viewPort.registerEventListener(this);
		this.layout();*/
		/*processRouteDefinitionScreen = new ProcessRouteDefinitionScreen(this, SWT.NONE);
		processRouteDefinitionScreen.registerEventListener(this);*/
		//this.viewPort.dispose();
		this.viewPort.setParent(dummyShell);
		this.viewPort = processRouteDefinitionScreen;
		this.viewPort.setParent(this);
		this.stackLayout.topControl = processRouteDefinitionScreen;
		this.layout();
	}

	private void datatypeDefinitionComplete(){
		loadMapRequiredFieldsScreen();
	}
	
	private void mappingRequiredFieldsComplete(){
		loadExpressionDefinitionScreen();
	}
	
	private void expressionDefinitionComplete(){
		loadModelDefinitionScreen();
	}
	
	private void modelDefinitionComplete(){
		loadProcessRouteDefinitionScreen();
	}
	
	private void processRouteDefinitionComplete(){
		//No-Op for the time being
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}


	@Override
	public void onGnosEventFired(GnosEvent e) {
		// TODO Auto-generated method stub
		if(e.eventName == "complete:datatype-defintion"){
			datatypeDefinitionComplete();
		}else if(e.eventName == "complete:map-required-fields"){
			mappingRequiredFieldsComplete();
		}else if(e.eventName == "complete:expression-defintion"){
			expressionDefinitionComplete();
		}else if(e.eventName == "complete:model-defintion"){
			modelDefinitionComplete();
		}else if(e.eventName == "complete:process-route-defintion"){
			processRouteDefinitionComplete();
		}
		this.triggerGnosEvent(e);
		
	}
	
	private void triggerGnosEvent(GnosEvent event){
		int j = listeners.size();
		int i = 0;
		for(i=0; i<j; i++){
			listeners.get(i).onGnosEventFired(event);
		}
	}

}
