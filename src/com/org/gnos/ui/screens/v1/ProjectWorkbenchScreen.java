package com.org.gnos.ui.screens.v1;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

public class ProjectWorkbenchScreen extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ProjectWorkbenchScreen(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		
		Button buttonModelDefinition = new Button(this, SWT.NONE);
		buttonModelDefinition.setAlignment(SWT.LEFT);
		buttonModelDefinition.setImage(SWTResourceManager.getImage(ProjectWorkbenchScreen.class, "/com/org/gnos/resources/models_icon.png"));
		buttonModelDefinition.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		FormData fd_buttonModelDefinition = new FormData();
		fd_buttonModelDefinition.bottom = new FormAttachment(0, 46);
		fd_buttonModelDefinition.right = new FormAttachment(0, 179);
		fd_buttonModelDefinition.top = new FormAttachment(0, 10);
		fd_buttonModelDefinition.left = new FormAttachment(0, 10);
		buttonModelDefinition.setLayoutData(fd_buttonModelDefinition);
		buttonModelDefinition.setText("Model Definition");
		
		Button buttonProcessRoutes = new Button(this, SWT.NONE);
		buttonProcessRoutes.setImage(SWTResourceManager.getImage(ProjectWorkbenchScreen.class, "/com/org/gnos/resources/process_icon.png"));
		buttonProcessRoutes.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		FormData fd_buttonProcessRoutes = new FormData();
		fd_buttonProcessRoutes.right = new FormAttachment(buttonModelDefinition, 0, SWT.RIGHT);
		fd_buttonProcessRoutes.bottom = new FormAttachment(buttonModelDefinition, 38, SWT.BOTTOM);
		fd_buttonProcessRoutes.top = new FormAttachment(buttonModelDefinition, 2);
		fd_buttonProcessRoutes.left = new FormAttachment(0, 10);
		buttonProcessRoutes.setLayoutData(fd_buttonProcessRoutes);
		buttonProcessRoutes.setText("Process Routes");
		
		Composite compositeUserPallete = new Composite(this, SWT.BORDER);
		FormData fd_compositeUserPallete = new FormData();
		fd_compositeUserPallete.bottom = new FormAttachment(100,-10);
		fd_compositeUserPallete.right = new FormAttachment(100, -10);
		fd_compositeUserPallete.top = new FormAttachment(0, 10);
		fd_compositeUserPallete.left = new FormAttachment(buttonModelDefinition, 6);
		compositeUserPallete.setLayoutData(fd_compositeUserPallete);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
