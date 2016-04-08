package com.org.gnos.ui.screens.v1;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.custom.models.ProjectModel;
import com.org.gnos.events.GnosEvent;
import com.org.gnos.ui.custom.controls.GnosScreen;

public class MainConfigurationViewPort extends GnosScreen{

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private GnosScreen viewPort;
	private StackLayout stackLayout;
	private FieldDatatypeDefinitionScreen fieldDatatypeDefinitionScreen;
	private MapRequiredFieldsScreen mapRequiredFieldsScreen;
	private ExpressionDefinitionScreen expressionDefinitionScreen;
	private ModelDefinitionScreen modelDefinitionScreen;
	private ProcessRouteDefinitionScreen processRouteDefinitionScreen;
	private PitGroupDefinitionScreen pitGroupDefinitionScreen;
	private OpexDefinitionScreen opexDefinitionScreen;
	private GradeConstraintScreen gradeConstraintScreen;
	private BenchConstraintScreen benchConstraintScreen;
	private StockPileDefinitionScreen stockPileDefinitionScreen;
	
	private Shell dummyShell;
	
	//private Layout mainLayout;
	public MainConfigurationViewPort(Composite parent, int style){
		super(parent, style);
		this.stackLayout = new StackLayout();
		this.setLayout(stackLayout);
		this.dummyShell = new Shell();
		//this.parent = parent;
		//this.loadFieldDatatypeDefinitionScreen();
		//this.setMinSize(this.viewPort.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		fieldDatatypeDefinitionScreen = new FieldDatatypeDefinitionScreen(this.dummyShell, SWT.NONE);
		fieldDatatypeDefinitionScreen.registerEventListener(this);
		
		mapRequiredFieldsScreen = new MapRequiredFieldsScreen(this.dummyShell, SWT.NONE);
		mapRequiredFieldsScreen.registerEventListener(this);
		
		expressionDefinitionScreen = new ExpressionDefinitionScreen(this.dummyShell, SWT.NONE);
		expressionDefinitionScreen.registerEventListener(this);
		
		modelDefinitionScreen = new ModelDefinitionScreen(this.dummyShell, SWT.NONE);
		modelDefinitionScreen.registerEventListener(this);
		
		processRouteDefinitionScreen = new ProcessRouteDefinitionScreen(this.dummyShell, SWT.NONE);
		processRouteDefinitionScreen.registerEventListener(this);
		
		pitGroupDefinitionScreen = new PitGroupDefinitionScreen(this.dummyShell, SWT.NONE);
		pitGroupDefinitionScreen.registerEventListener(this);
		
		opexDefinitionScreen = new OpexDefinitionScreen(this.dummyShell, SWT.NONE);
		opexDefinitionScreen.registerEventListener(this);
		
		gradeConstraintScreen = new GradeConstraintScreen(this.dummyShell, SWT.NONE);
		gradeConstraintScreen.registerEventListener(this);
		
		benchConstraintScreen = new BenchConstraintScreen(this.dummyShell, SWT.NONE);
		benchConstraintScreen.registerEventListener(this);
		
		stockPileDefinitionScreen = new StockPileDefinitionScreen(this.dummyShell, SWT.NONE);
		stockPileDefinitionScreen.registerEventListener(this);
	}
	
	public void loadFieldDatatypeDefinitionScreen(){
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
		this.viewPort.setParent(dummyShell);
		this.viewPort = mapRequiredFieldsScreen;
		this.viewPort.setParent(this);
		this.stackLayout.topControl = this.viewPort;
		this.layout();
	}

	public void loadExpressionDefinitionScreen(){
		this.viewPort.setParent(dummyShell);
		this.viewPort = expressionDefinitionScreen;
		this.viewPort.setParent(this);
		this.stackLayout.topControl = this.viewPort;
		this.layout();
	}

	public void loadModelDefinitionScreen(){
		this.viewPort.setParent(dummyShell);
		this.viewPort = modelDefinitionScreen;
		this.viewPort.setParent(this);
		this.stackLayout.topControl = modelDefinitionScreen;
		this.layout();
	}

	public void loadProcessRouteDefinitionScreen(){
		this.viewPort.setParent(dummyShell);
		this.viewPort = processRouteDefinitionScreen;
		this.viewPort.setParent(this);
		this.stackLayout.topControl = processRouteDefinitionScreen;
		this.layout();
	}
	
	public void loadPitGroupDefinitionScreen(){
		this.viewPort.setParent(dummyShell);
		this.viewPort = pitGroupDefinitionScreen;
		this.viewPort.setParent(this);
		this.stackLayout.topControl = pitGroupDefinitionScreen;
		this.layout();
	}
	
	public void loadOpexDefinitionScreen(){
		this.viewPort.setParent(dummyShell);
		this.viewPort = opexDefinitionScreen;
		this.viewPort.setParent(this);
		this.stackLayout.topControl = opexDefinitionScreen;
		this.layout();
	}
	
	public void loadGradeConstraintDefinitionScreen(){
		this.viewPort.setParent(dummyShell);
		this.viewPort = gradeConstraintScreen;
		this.viewPort.setParent(this);
		this.stackLayout.topControl = gradeConstraintScreen;
		this.layout();
	}
	
	public void loadBenchConstraintDefinitionScreen(){
		this.viewPort.setParent(dummyShell);
		this.viewPort = benchConstraintScreen;
		this.viewPort.setParent(this);
		this.stackLayout.topControl = benchConstraintScreen;
		this.layout();
	}
	
	public void loadStockpileDefinitionScreen(){
		this.viewPort.setParent(dummyShell);
		this.viewPort = stockPileDefinitionScreen;
		this.viewPort.setParent(this);
		this.stackLayout.topControl = stockPileDefinitionScreen;
		this.layout();
	}
	
	private void datatypeDefinitionComplete(){
		ProjectConfigutration.getInstance().saveFieldData();
		loadMapRequiredFieldsScreen();
	}
	
	private void mappingRequiredFieldsComplete(){
		ProjectConfigutration.getInstance().saveRequiredFieldMappingData();
		loadExpressionDefinitionScreen();
	}
	
	private void expressionDefinitionComplete(){
		//ProjectConfigutration.getInstance().saveExpressionData();
		loadModelDefinitionScreen();
	}
	
	private void modelDefinitionComplete(){
		loadProcessRouteDefinitionScreen();
	}
	
	private void processRouteDefinitionComplete(){
		//No-Op for the time being
		loadPitGroupDefinitionScreen();
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
