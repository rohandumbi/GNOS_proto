package com.org.gnos.ui.screens.v1;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.custom.controls.GnosScreen;
import com.org.gnos.events.GnosEvent;
import com.org.gnos.events.interfaces.GnosEventListener;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class ProcessRouteDefinitionScreen extends GnosScreen {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ProcessRouteDefinitionScreen(Composite parent, int style) {
		super(parent, style);
		
		Label lblNewLabel = new Label(this, SWT.NONE);
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
		buttonProcessRouteDefined.setText("Next");

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
