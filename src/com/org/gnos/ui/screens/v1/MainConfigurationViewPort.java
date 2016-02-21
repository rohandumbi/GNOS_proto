package com.org.gnos.ui.screens.v1;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import com.org.gnos.custom.controls.GnosScreen;
import com.org.gnos.custom.models.ProjectModel;
import com.org.gnos.events.GnosEvent;

public class MainConfigurationViewPort extends GnosScreen {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private ProjectModel projectModel;
	private GnosScreen viewPort;
	//private Layout mainLayout;
	public MainConfigurationViewPort(Composite parent, int style, ProjectModel projectModel){
		super(parent, style);
		this.projectModel = projectModel;
		//this.parent = parent;
		this.loadFieldDatatypeDefinitionScreen();
	}
	
	private void loadFieldDatatypeDefinitionScreen(){
		this.setLayout(new FillLayout());
		this.viewPort = new FieldDatatypeDefinitionScreen(this, SWT.NONE, this.projectModel);
		this.viewPort.registerEventListener(this);
		this.layout();
	}

	private void loadMapRequiredFieldsScreen(){
		this.viewPort.dispose();
		this.setLayout(new FillLayout());
		this.viewPort = new MapRequiredFieldsScreen(this, SWT.NONE, this.projectModel);
		this.viewPort.registerEventListener(this);
		this.layout();
	}

	private void loadExpressionDefinitionScreen(){
		this.viewPort.dispose();
		this.setLayout(new FillLayout());
		this.viewPort = new ExpressionDefinitionScreen(this, SWT.NONE, this.projectModel);
		this.viewPort.registerEventListener(this);
		this.layout();
	}

	private void loadModelDefinitionScreen(){
		this.viewPort.dispose();
		this.setLayout(new FillLayout());
		this.viewPort = new ModelDefinitionScreen(this, SWT.NONE);
		this.viewPort.registerEventListener(this);
		this.layout();
	}

	private void loadProcessRouteDefinitionScreen(){
		this.viewPort.dispose();
		this.setLayout(new FillLayout());
		this.viewPort = new ProcessRouteDefinitionScreen(this, SWT.NONE);
		this.viewPort.registerEventListener(this);
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
