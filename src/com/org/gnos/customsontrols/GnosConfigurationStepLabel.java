package com.org.gnos.customsontrols;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

public class GnosConfigurationStepLabel extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private Color defaultLabelColor;
	private Color selectedLabelColor;
	private Color defaultTextColor;
	private Color selectedTextColor;
	private String stepName;
	private Label labelMapRequiredFields ;

	public GnosConfigurationStepLabel(Composite parent, int style, String label) {
		super(parent, style);
		this.defaultLabelColor = SWTResourceManager.getColor(SWT.COLOR_WHITE);
		this.defaultTextColor = SWTResourceManager.getColor(SWT.COLOR_BLACK);
		this.selectedLabelColor = SWTResourceManager.getColor(240, 240, 240);
		this.selectedTextColor = SWTResourceManager.getColor(0, 204, 255);

		this.stepName = label;
		createContent(parent);
	}

	private void createContent(Composite parent){
		this.setLayout(new FormLayout());
		this.setBackground(this.defaultLabelColor);

		labelMapRequiredFields = new Label(this, SWT.NONE);
		labelMapRequiredFields.setBackground(this.defaultLabelColor);
		labelMapRequiredFields.setForeground(this.defaultTextColor);
		labelMapRequiredFields.setFont(SWTResourceManager.getFont("Arial", 11, SWT.BOLD));
		labelMapRequiredFields.setText(this.stepName);

		FormData fd_labelMapRequiredFields = new FormData();
		fd_labelMapRequiredFields.bottom = new FormAttachment(100, -9);
		fd_labelMapRequiredFields.left = new FormAttachment(0,5);
		labelMapRequiredFields.setLayoutData(fd_labelMapRequiredFields);
	}

	public void setSelectedState(){
		this.setBackground(this.selectedLabelColor);
		labelMapRequiredFields.setForeground(this.selectedTextColor);
		labelMapRequiredFields.setBackground(this.selectedLabelColor);
	}

	public void setDeselectedState(){
		this.setBackground(this.defaultLabelColor);
		labelMapRequiredFields.setForeground(this.defaultTextColor);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
