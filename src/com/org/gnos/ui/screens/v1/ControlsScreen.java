package com.org.gnos.ui.screens.v1;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;

public class ControlsScreen extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ControlsScreen(Composite parent, int style) {
		super(parent, style);
		
		Label lblControlsScreenWill = new Label(this, SWT.NONE);
		lblControlsScreenWill.setBounds(322, 21, 419, 127);
		lblControlsScreenWill.setText("CONTROLS SCREEN WILL COME HERE");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
