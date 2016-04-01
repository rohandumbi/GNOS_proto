package com.org.gnos.ui.screens.v1;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.org.gnos.events.GnosEvent;
import com.org.gnos.events.GnosEventWithAttributeMap;
import com.org.gnos.events.interfaces.GnosEventListener;
import com.org.gnos.ui.custom.controls.GnosScreen;
import com.org.gnos.utilities.SWTResourceManager;

public class ProjectScreen extends GnosScreen {

	public ProjectScreen(Composite parent, int style) {
		super(parent, style);
		
		this.createContent();

	}

	private void createContent(){
		
		
		setLayout(new FormLayout());
		
		
		Label label = new Label(this, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_label = new FormData();
		fd_label.left = new FormAttachment(0, 260);
		fd_label.top = new FormAttachment(0);
		fd_label.bottom = new FormAttachment(100, 10);
		fd_label.right = new FormAttachment(0, 262);
		label.setLayoutData(fd_label);
		
		CLabel labelProject = new CLabel(this, SWT.NONE);
		labelProject.setForeground(SWTResourceManager.getColor(255, 255, 255));
		labelProject.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		labelProject.setBackground(SWTResourceManager.getColor(0, 102, 204));
		FormData fd_labelProject = new FormData();
		fd_labelProject.bottom = new FormAttachment(0, 34);
		fd_labelProject.right = new FormAttachment(100);
		fd_labelProject.top = new FormAttachment(0);
		fd_labelProject.left = new FormAttachment(0);
		labelProject.setLayoutData(fd_labelProject);
		labelProject.setText("Projects");
		

		
		CLabel lastLabel = labelProject;
		for(int i=0; i< 10; i++){
			CLabel labelProject1 = new CLabel(this, SWT.NONE);
			labelProject1.setForeground(SWTResourceManager.getColor(255, 255, 255));
			labelProject1.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
			labelProject1.setBackground(SWTResourceManager.getColor(0, 102, 204));
			FormData fd_labelProject1 = new FormData();
			fd_labelProject1.bottom = new FormAttachment(lastLabel, 40, SWT.BOTTOM);
			fd_labelProject1.right = new FormAttachment(label, -6);
			fd_labelProject1.top = new FormAttachment(lastLabel, 6);
			fd_labelProject1.left = new FormAttachment(0, 10);
			labelProject1.setLayoutData(fd_labelProject1);
			labelProject1.setText("Project "+(i+1));
			lastLabel = labelProject1;
		}

		ScrolledComposite scViewPortContainer = new ScrolledComposite(this, SWT.V_SCROLL | SWT.NONE);
		FormData fd_scViewPortContainer = new FormData();
		fd_scViewPortContainer.right = new FormAttachment(labelProject, -6, SWT.RIGHT);
		fd_scViewPortContainer.bottom = new FormAttachment(100, -6);
		fd_scViewPortContainer.top = new FormAttachment(labelProject, 6);
		fd_scViewPortContainer.left = new FormAttachment(label, 6);

		CreateNewProjectScreen newProjectScreen = new CreateNewProjectScreen(scViewPortContainer, SWT.BORDER);

		scViewPortContainer.setContent(newProjectScreen);
		scViewPortContainer.setExpandHorizontal(true);
		scViewPortContainer.setExpandVertical(true);
		scViewPortContainer.setLayout(new FillLayout());
		scViewPortContainer.setLayoutData(fd_scViewPortContainer);
		
		newProjectScreen.registerEventListener(this);
	}
	
	private void fireChildEvent(GnosEvent event){
		int j = listeners.size();
		int i = 0;
		for(i=0; i<j; i++){
			listeners.get(i).onGnosEventFired(event);
		}
	}
	
	@Override
	public void onGnosEventFired(GnosEvent e) {
		if(e.eventName == "createNewProjectScreen:upload-records-complete") {
			System.out.println("New project event fired");
			fireChildEvent(e);
		}
		
	}

}
