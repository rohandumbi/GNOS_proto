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
		
		
		GnosConfigurationStepLabel gnosStepMapRequiredFieldsLabel = new GnosConfigurationStepLabel(this, SWT.NONE, "Map Required Fields");
		FormData fd_gnosStepMapRequiredFieldsLabel = new FormData();
		fd_gnosStepMapRequiredFieldsLabel.bottom = new FormAttachment(labelWorkbenchHeader, 40, SWT.BOTTOM);
		fd_gnosStepMapRequiredFieldsLabel.right = new FormAttachment(label, -6);
		fd_gnosStepMapRequiredFieldsLabel.top = new FormAttachment(labelWorkbenchHeader, 6);
		fd_gnosStepMapRequiredFieldsLabel.left = new FormAttachment(labelWorkbenchHeader, 10, SWT.LEFT);
		gnosStepMapRequiredFieldsLabel.setLayoutData(fd_gnosStepMapRequiredFieldsLabel);
		gnosStepMapRequiredFieldsLabel.setSelectedState();
		
		GnosConfigurationStepLabel gnosStepDefineExpressionsLabel = new GnosConfigurationStepLabel(this, SWT.NONE, "Expression Definition");
		FormData fd_gnosStepDefineExpressionsLabel = new FormData();
		fd_gnosStepDefineExpressionsLabel.bottom = new FormAttachment(gnosStepMapRequiredFieldsLabel, 40, SWT.BOTTOM);
		fd_gnosStepDefineExpressionsLabel.right = new FormAttachment(label, -6);
		fd_gnosStepDefineExpressionsLabel.top = new FormAttachment(gnosStepMapRequiredFieldsLabel);
		fd_gnosStepDefineExpressionsLabel.left = new FormAttachment(0, 10);
		gnosStepDefineExpressionsLabel.setLayoutData(fd_gnosStepDefineExpressionsLabel);
		
		GnosConfigurationStepLabel gnosStepModelDefinitionLabel = new GnosConfigurationStepLabel(this, SWT.NONE, "Model Definition");
		FormData fd_gnosStepModelDefinitionLabel = new FormData();
		fd_gnosStepModelDefinitionLabel.bottom = new FormAttachment(gnosStepDefineExpressionsLabel, 40, SWT.BOTTOM);
		fd_gnosStepModelDefinitionLabel.right = new FormAttachment(label, -6);
		fd_gnosStepModelDefinitionLabel.top = new FormAttachment(gnosStepDefineExpressionsLabel);
		fd_gnosStepModelDefinitionLabel.left = new FormAttachment(gnosStepMapRequiredFieldsLabel, 0, SWT.LEFT);
		gnosStepModelDefinitionLabel.setLayoutData(fd_gnosStepModelDefinitionLabel);
		
		GnosConfigurationStepLabel gnosStepProcessRouteDefinitionLabel = new GnosConfigurationStepLabel(this, SWT.NONE, "Process Route Definition");
		FormData fd_gnosStepProcessRouteDefinitionLabel = new FormData();
		fd_gnosStepProcessRouteDefinitionLabel.bottom = new FormAttachment(gnosStepDefineExpressionsLabel, 80, SWT.BOTTOM);
		fd_gnosStepProcessRouteDefinitionLabel.right = new FormAttachment(label, -6);
		fd_gnosStepProcessRouteDefinitionLabel.top = new FormAttachment(gnosStepDefineExpressionsLabel, 40);
		fd_gnosStepProcessRouteDefinitionLabel.left = new FormAttachment(gnosStepMapRequiredFieldsLabel, 0, SWT.LEFT);
		gnosStepProcessRouteDefinitionLabel.setLayoutData(fd_gnosStepProcessRouteDefinitionLabel);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
