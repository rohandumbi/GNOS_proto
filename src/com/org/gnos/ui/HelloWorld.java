package com.org.gnos.ui;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class HelloWorld extends ApplicationWindow{

	public HelloWorld(){
		super(null);
	}
	
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, true));
        
		
		
		Label label = new Label(container, SWT.NONE);
		label.setText("Hello, world!");
		
		// Set the minimum size
        parent.getShell().setMinimumSize(1200, 700);
		//parent.getShell().setMaximized(false);
        parent.pack();
        return container;
	}
}
