package com.org.gnos.ui.screens.v1;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;

public class MainConfigurationViewPort extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public MainConfigurationViewPort(Composite parent, int style) {
		super(parent, style);
		
		Label lblSoonToCome = new Label(this, SWT.NONE);
		lblSoonToCome.setBounds(10, 10, 106, 15);
		lblSoonToCome.setText("Soon to come.....");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
