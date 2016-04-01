package com.org.gnos.ui.screens.v1;

import java.util.ArrayList;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.org.gnos.events.GnosEvent;
import com.org.gnos.events.interfaces.GnosEventGenerator;
import com.org.gnos.events.interfaces.GnosEventListener;

public class HomeScreen extends Composite implements GnosEventGenerator{

	private ArrayList<GnosEventListener> listeners = new ArrayList<GnosEventListener>();
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public HomeScreen(Composite parent, int style) {
		super(parent, style);
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		
		
		Composite compositeButtonGroup = new Composite(this, SWT.NONE);
		compositeButtonGroup.setLayout(new FormLayout());
		
		

	}

	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	private void fireChildEvent(GnosEvent event){
		int j = listeners.size();
		int i = 0;
		for(i=0; i<j; i++){
			listeners.get(i).onGnosEventFired(event);
		}
	}

	@Override
	public void registerEventListener(GnosEventListener listener) {
		// TODO Auto-generated method stub
		listeners.add(listener);
	}
}
