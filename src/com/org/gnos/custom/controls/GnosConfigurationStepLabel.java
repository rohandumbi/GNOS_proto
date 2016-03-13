package com.org.gnos.custom.controls;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.events.GnosEvent;
import com.org.gnos.events.interfaces.GnosEventGenerator;
import com.org.gnos.events.interfaces.GnosEventListener;
import com.org.gnos.utilities.ClickBehavior;

public class GnosConfigurationStepLabel extends Composite implements GnosEventGenerator{

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
	protected ArrayList<GnosEventListener> listeners = new ArrayList<GnosEventListener>();

	public GnosConfigurationStepLabel(Composite parent, int style, String label) {
		super(parent, style);
		this.defaultLabelColor = SWTResourceManager.getColor(SWT.COLOR_WHITE);
		this.defaultTextColor = SWTResourceManager.getColor(SWT.COLOR_BLACK);
		this.selectedLabelColor = SWTResourceManager.getColor(240, 240, 240);
		this.selectedTextColor = SWTResourceManager.getColor(0, 191, 255);

		this.stepName = label;
		createContent(parent);
	}

	private void createContent(Composite parent){
		this.setLayout(new FormLayout());
		this.setBackground(this.defaultLabelColor);

		labelMapRequiredFields = new Label(this, SWT.NONE);
		labelMapRequiredFields.setBackground(this.defaultLabelColor);
		labelMapRequiredFields.setForeground(this.defaultTextColor);
		labelMapRequiredFields.setFont(SWTResourceManager.getFont("Arial", 11, SWT.NORMAL));
		labelMapRequiredFields.setText(this.stepName);

		FormData fd_labelMapRequiredFields = new FormData();
		fd_labelMapRequiredFields.bottom = new FormAttachment(100, -9);
		fd_labelMapRequiredFields.left = new FormAttachment(0,5);
		fd_labelMapRequiredFields.right = new FormAttachment(100);
		labelMapRequiredFields.setLayoutData(fd_labelMapRequiredFields);
		labelMapRequiredFields.addMouseListener(new ClickBehavior(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				GnosEvent event = new GnosEvent(this, stepName);
				fireChildEvent(event);
				System.out.println("Got the click...");
			}
		}));
	}

	public void setSelectedState(){
		this.setBackground(this.selectedLabelColor);
		labelMapRequiredFields.setFont(SWTResourceManager.getFont("Arial", 11, SWT.BOLD));
		labelMapRequiredFields.setForeground(this.selectedTextColor);
		labelMapRequiredFields.setBackground(this.selectedLabelColor);
	}

	public void setDeselectedState(){
		this.setBackground(this.defaultLabelColor);
		labelMapRequiredFields.setBackground(defaultLabelColor);
		labelMapRequiredFields.setFont(SWTResourceManager.getFont("Arial", 11, SWT.NORMAL));
		labelMapRequiredFields.setForeground(this.defaultTextColor);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void registerEventListener(GnosEventListener listener) {
		// TODO Auto-generated method stub
		listeners.add(listener);
	}
	
	private void fireChildEvent(GnosEvent event){
		int j = listeners.size();
		int i = 0;
		for(i=0; i<j; i++){
			listeners.get(i).onGnosEventFired(event);
		}
	}

}
