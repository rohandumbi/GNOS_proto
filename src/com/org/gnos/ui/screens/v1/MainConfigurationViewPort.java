package com.org.gnos.ui.screens.v1;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import com.org.gnos.customsontrols.GnosScreen;
import com.org.gnos.events.GnosEvent;
import com.org.gnos.events.interfaces.GnosEventListener;

public class MainConfigurationViewPort extends GnosScreen {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private Composite viewPort;
	private Composite parent;
	public MainConfigurationViewPort(Composite parent, int style) {
		super(parent, style);
		this.parent = parent;
		this.setLayout(new FillLayout());
	}
	
	public void loadMapRequiredFieldsScreen(){
		this.viewPort = new MapRequiredFieldsScreen(this, SWT.NONE);
	}
	
	public void loadExpressionDefinitionScreen(){
		this.viewPort = new ExpressionDefinitionScreen(this, SWT.NONE);
	}
	
	public void loadModelDefinitionScreen(){
		this.viewPort = new ModelDefinitionScreen(this, SWT.NONE);
	}
	
	public void loadProcessRouteDefinitionScreen(){
		this.viewPort = new ProcessRouteDefinitionScreen(this, SWT.NONE);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void registerEventListener(GnosEventListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGnosEventFired(GnosEvent e) {
		// TODO Auto-generated method stub
		
	}

}
