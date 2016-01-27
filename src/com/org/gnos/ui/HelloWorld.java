package com.org.gnos.ui;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class HelloWorld extends ApplicationWindow{

	public HelloWorld(){
		super(null);
	}
	
	protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("GNOS - Adding a new dimension to mining");
    }
	
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		//container.setLayout(new GridLayout(1, true));
		container.setLayout(new FillLayout());
		Image backgroundImage = new Image (Display.getCurrent(), "resources/bg.jpg");
		container.setBackgroundImage(backgroundImage);
		container.setBackgroundMode(SWT.INHERIT_FORCE);
		container.setBounds(0, 0, backgroundImage.getBounds().width, backgroundImage.getBounds().height);
		
		
		Label label = new Label(container, SWT.NONE);
		label.setText("Hello, world!");
		
		// Set the minimum size
        parent.getShell().setMinimumSize(1200, 700);
		//parent.getShell().setMaximized(false);
        parent.pack();
        return container;
	}
}
