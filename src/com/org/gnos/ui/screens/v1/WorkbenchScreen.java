package com.org.gnos.ui.screens.v1;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.org.gnos.customsontrols.GnosConfigurationStepLabel;

public class WorkbenchScreen extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CLabel labelWorkbenchHeader;
	public WorkbenchScreen(Composite parent, int style) {
		super(parent, style);
		setFont(SWTResourceManager.getFont("Arial", 11, SWT.BOLD));
		setToolTipText("");
		setBackground(SWTResourceManager.getColor(255, 255, 255));
		setLayout(new FormLayout());
		
		labelWorkbenchHeader = new CLabel(this, SWT.NONE);
		labelWorkbenchHeader.setImage(SWTResourceManager.getImage(WorkbenchScreen.class, "/com/org/gnos/resources/settings16.png"));
		labelWorkbenchHeader.setForeground(SWTResourceManager.getColor(255, 255, 255));
		labelWorkbenchHeader.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		labelWorkbenchHeader.setBackground(SWTResourceManager.getColor(0, 102, 204));
		FormData fd_labelWorkbenchHeader = new FormData();
		fd_labelWorkbenchHeader.bottom = new FormAttachment(0, 34);
		fd_labelWorkbenchHeader.right = new FormAttachment(100);
		fd_labelWorkbenchHeader.top = new FormAttachment(0);
		fd_labelWorkbenchHeader.left = new FormAttachment(0);
		labelWorkbenchHeader.setLayoutData(fd_labelWorkbenchHeader);
		labelWorkbenchHeader.setText("Project Import Configuration");
		
		Label label = new Label(this, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_label = new FormData();
		fd_label.left = new FormAttachment(0, 260);
		fd_label.top = new FormAttachment(labelWorkbenchHeader, 6);
		fd_label.bottom = new FormAttachment(100, 10);
		fd_label.right = new FormAttachment(0, 262);
		label.setLayoutData(fd_label);
		
		
		GnosConfigurationStepLabel compositeMapRequiredFields = new GnosConfigurationStepLabel(this, SWT.NONE, "Map Required Fields");
		FormData fd_compositeMapRequiredFields = new FormData();
		fd_compositeMapRequiredFields.bottom = new FormAttachment(labelWorkbenchHeader, 40, SWT.BOTTOM);
		fd_compositeMapRequiredFields.right = new FormAttachment(label, -6);
		fd_compositeMapRequiredFields.top = new FormAttachment(labelWorkbenchHeader, 6);
		fd_compositeMapRequiredFields.left = new FormAttachment(labelWorkbenchHeader, 10, SWT.LEFT);
		compositeMapRequiredFields.setLayoutData(fd_compositeMapRequiredFields);
		compositeMapRequiredFields.setSelectedState();
		
		GnosConfigurationStepLabel compositeDefineFields = new GnosConfigurationStepLabel(this, SWT.NONE, "Define System Fields");
		FormData fd_compositeDefineFields = new FormData();
		fd_compositeDefineFields.bottom = new FormAttachment(compositeMapRequiredFields, 40, SWT.BOTTOM);
		fd_compositeDefineFields.right = new FormAttachment(label, -6);
		fd_compositeDefineFields.top = new FormAttachment(compositeMapRequiredFields);
		fd_compositeDefineFields.left = new FormAttachment(0, 10);
		compositeDefineFields.setLayoutData(fd_compositeDefineFields);
		
		GnosConfigurationStepLabel compositeModelDefinition = new GnosConfigurationStepLabel(this, SWT.NONE, "Model Definition");
		FormData fd_compositeModelDefinition = new FormData();
		fd_compositeModelDefinition.bottom = new FormAttachment(compositeDefineFields, 40, SWT.BOTTOM);
		fd_compositeModelDefinition.right = new FormAttachment(label, -6);
		fd_compositeModelDefinition.top = new FormAttachment(compositeDefineFields);
		fd_compositeModelDefinition.left = new FormAttachment(compositeMapRequiredFields, 0, SWT.LEFT);
		compositeModelDefinition.setLayoutData(fd_compositeModelDefinition);
		
		GnosConfigurationStepLabel compositeProcessRouteDefinition = new GnosConfigurationStepLabel(this, SWT.NONE, "Process Route Definition");
		FormData fd_compositeProcessRouteDefinition = new FormData();
		fd_compositeProcessRouteDefinition.bottom = new FormAttachment(compositeDefineFields, 80, SWT.BOTTOM);
		fd_compositeProcessRouteDefinition.right = new FormAttachment(label, -6);
		fd_compositeProcessRouteDefinition.top = new FormAttachment(compositeDefineFields, 40);
		fd_compositeProcessRouteDefinition.left = new FormAttachment(compositeMapRequiredFields, 0, SWT.LEFT);
		compositeProcessRouteDefinition.setLayoutData(fd_compositeProcessRouteDefinition);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
