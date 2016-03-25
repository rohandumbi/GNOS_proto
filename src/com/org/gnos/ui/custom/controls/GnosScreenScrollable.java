package com.org.gnos.ui.custom.controls;

import java.util.ArrayList;

import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;

import com.org.gnos.events.interfaces.GnosEventGenerator;
import com.org.gnos.events.interfaces.GnosEventListener;

public abstract class GnosScreenScrollable extends ScrolledComposite implements GnosEventGenerator, GnosEventListener{

	protected ArrayList<GnosEventListener> listeners = new ArrayList<GnosEventListener>();
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public GnosScreenScrollable(Composite parent, int style) {
		super(parent, style);
	}
	
	@Override
	public void registerEventListener(GnosEventListener listener) {
		// TODO Auto-generated method stub
		listeners.add(listener);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
