package com.org.gnos.customsontrols;

import org.eclipse.swt.widgets.Composite;

import com.org.gnos.events.interfaces.GnosEventGenerator;
import com.org.gnos.events.interfaces.GnosEventListener;

public abstract class GnosScreen extends Composite implements GnosEventGenerator, GnosEventListener{

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public GnosScreen(Composite parent, int style) {
		super(parent, style);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
