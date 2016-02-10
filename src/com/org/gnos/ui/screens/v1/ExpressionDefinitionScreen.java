package com.org.gnos.ui.screens.v1;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.customsontrols.GnosScreen;
import com.org.gnos.events.GnosEvent;
import com.org.gnos.events.interfaces.GnosEventListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class ExpressionDefinitionScreen extends GnosScreen {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ExpressionDefinitionScreen(Composite parent, int style) {
		super(parent, style);
		
		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		lblNewLabel.setBounds(10, 10, 197, 70);
		lblNewLabel.setText("Expression Definition Screen");
		
		Button buttonExpressionDefined = new Button(this, SWT.NONE);
		buttonExpressionDefined.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO process definition complete
				GnosEvent event = new GnosEvent(this, "complete:expression-defintion");
				triggerGnosEvent(event);
			}
		});
		buttonExpressionDefined.setBounds(223, 10, 75, 25);
		buttonExpressionDefined.setText("Next");

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
