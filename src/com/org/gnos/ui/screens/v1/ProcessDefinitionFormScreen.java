package com.org.gnos.ui.screens.v1;

import org.eclipse.swt.widgets.Composite;

import com.org.gnos.events.GnosEvent;
import com.org.gnos.ui.custom.controls.GnosScreen;

import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;

public class ProcessDefinitionFormScreen extends GnosScreen {
	private Text textProcessName;
	private Label labelVerticalSeparator;
	private Composite lastProcessStep;
	private Composite modelListContainerComposite;
	private ScrolledComposite scViewportContainer;

	public ProcessDefinitionFormScreen(Composite parent, int style) {
		super(parent, style);
		setBackground(SWTResourceManager.getColor(240, 248, 255));
		setLayout(new FormLayout());
		
		Label lblProcessName = new Label(this, SWT.NONE);
		lblProcessName.setBackground(SWTResourceManager.getColor(240, 248, 255));
		lblProcessName.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		FormData fd_lblProcessName = new FormData();
		fd_lblProcessName.top = new FormAttachment(0, 37);
		fd_lblProcessName.left = new FormAttachment(0, 44);
		lblProcessName.setLayoutData(fd_lblProcessName);
		lblProcessName.setText("Process Name:");
		
		textProcessName = new Text(this, SWT.BORDER);
		FormData fd_textProcessName = new FormData();
		fd_textProcessName.right = new FormAttachment(lblProcessName, 522, SWT.RIGHT);
		fd_textProcessName.top = new FormAttachment(0, 37);
		fd_textProcessName.left = new FormAttachment(lblProcessName, 6);
		textProcessName.setLayoutData(fd_textProcessName);
		
		Label lblChooseColor = new Label(this, SWT.NONE);
		lblChooseColor.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		lblChooseColor.setBackground(SWTResourceManager.getColor(240, 248, 255));
		FormData fd_lblChooseColor = new FormData();
		//fd_lblChooseColor.bottom = new FormAttachment(lblStep, -89);
		fd_lblChooseColor.left = new FormAttachment(lblProcessName, 0, SWT.LEFT);
		fd_lblChooseColor.top = new FormAttachment(lblProcessName, 20, SWT.BOTTOM);
		lblChooseColor.setLayoutData(fd_lblChooseColor);
		lblChooseColor.setText("Choose Color:");
		
		Button btnNewButton = new Button(this, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addModelStep();
			}
		});
		btnNewButton.setAlignment(SWT.LEFT);
		btnNewButton.setImage(SWTResourceManager.getImage(ProcessDefinitionFormScreen.class, "/com/org/gnos/resources/Add_blue_24.png"));
		FormData fd_btnNewButton = new FormData();
		fd_btnNewButton.bottom = new FormAttachment(lblChooseColor, 51, SWT.BOTTOM);
		fd_btnNewButton.right = new FormAttachment(0, 173);
		fd_btnNewButton.top = new FormAttachment(lblChooseColor, 17);
		fd_btnNewButton.left = new FormAttachment(0, 44);
		btnNewButton.setLayoutData(fd_btnNewButton);
		btnNewButton.setText("Add Model Step");
		
		this.labelVerticalSeparator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		FormData fd_labelVerticalSeparator = new FormData();
		fd_labelVerticalSeparator.bottom = new FormAttachment(btnNewButton, 22, SWT.BOTTOM);
		fd_labelVerticalSeparator.right = new FormAttachment(100);
		fd_labelVerticalSeparator.top = new FormAttachment(btnNewButton, 20);
		fd_labelVerticalSeparator.left = new FormAttachment(0);
		this.labelVerticalSeparator.setLayoutData(fd_labelVerticalSeparator);
		
		this.scViewportContainer = new ScrolledComposite(this, SWT.V_SCROLL | SWT.NONE);
		FormData fd_scViewPortContainer = new FormData();
		fd_scViewPortContainer.top = new FormAttachment(this.labelVerticalSeparator, 10, SWT.BOTTOM);
		fd_scViewPortContainer.bottom = new FormAttachment(100);
		fd_scViewPortContainer.left = new FormAttachment(0);
		fd_scViewPortContainer.right = new FormAttachment(100);
		
		this.modelListContainerComposite = new Composite(this.scViewportContainer, SWT.NONE);
		this.modelListContainerComposite.setLayout(new FormLayout());
		this.modelListContainerComposite.setBackground(SWTResourceManager.getColor(240, 248, 255));
		
		FormData fd_modelListContainerComposite = new FormData();
		fd_modelListContainerComposite.top = new FormAttachment(this.labelVerticalSeparator, 10, SWT.BOTTOM);
		fd_modelListContainerComposite.bottom = new FormAttachment(100);
		fd_modelListContainerComposite.left = new FormAttachment(0);
		fd_modelListContainerComposite.right = new FormAttachment(100);
		this.modelListContainerComposite .setLayoutData(fd_modelListContainerComposite);
		
		this.scViewportContainer.setContent(this.modelListContainerComposite);
		this.scViewportContainer.setExpandHorizontal(true);
		this.scViewportContainer.setExpandVertical(true);
		this.scViewportContainer.setLayout(new FillLayout());
		//this.scViewPortContainer.setMinSize(this.mainConfigurationViewPort.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scViewportContainer.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				System.out.println("MVCP resized");
				Rectangle r = scViewportContainer.getClientArea();
				//scViewPortContainer.setMinSize(mainConfigurationViewPort.computeSize(r.width, SWT.DEFAULT));
				scViewportContainer.setMinSize(modelListContainerComposite.computeSize(r.width, SWT.DEFAULT, true));
			}
		});
		this.scViewportContainer.setLayoutData(fd_scViewPortContainer);
		
		
		
		// TODO Auto-generated constructor stub
	}
	
	private void addModelStep(){
		Composite processStep = new Composite(this.modelListContainerComposite, SWT.None);
		processStep.setBackground(SWTResourceManager.getColor(240, 248, 255));
		processStep.setLayout(new FormLayout());
		FormData fd_processStep = new FormData();
		fd_processStep.left =  new FormAttachment(0);
		fd_processStep.right =  new FormAttachment(100);
		if(this.lastProcessStep != null){
			fd_processStep.top = new FormAttachment(this.lastProcessStep, 10, SWT.BOTTOM);
		}else{
			fd_processStep.top = new FormAttachment(this.labelVerticalSeparator, 22, SWT.BOTTOM);
			/*fd_processStep.top = new FormAttachment(0);*/
		}
		processStep.setLayoutData(fd_processStep);
		
		Label lblStep = new Label(processStep, SWT.NONE);
		lblStep.setText("Step Name:");
		lblStep.setBackground(SWTResourceManager.getColor(240, 248, 255));
		lblStep.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		FormData fd_lblStep = new FormData();
		fd_lblStep.left = new FormAttachment(0, 44);
		lblStep.setLayoutData(fd_lblStep);
		
		Combo comboStep = new Combo(processStep, SWT.NONE);
		FormData fd_comboStep = new FormData();
		//fd_comboStep.top = new FormAttachment(lblStep, 2, SWT.TOP);
		fd_comboStep.left = new FormAttachment(lblStep, 5, SWT.RIGHT);
		fd_comboStep.right = new FormAttachment(50);
		comboStep.setLayoutData(fd_comboStep);
		
		this.lastProcessStep = processStep;
		this.modelListContainerComposite.layout();
		
		Rectangle r = this.scViewportContainer.getClientArea();
		this.scViewportContainer.setMinSize(this.modelListContainerComposite.computeSize(r.width, SWT.DEFAULT));
		this.scViewportContainer.setOrigin(this.modelListContainerComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		//this.layout();
	}

	@Override
	public void onGnosEventFired(GnosEvent e) {
		// TODO Auto-generated method stub

	}
}
