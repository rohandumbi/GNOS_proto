package com.org.gnos.ui.screens.v1;

import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.org.gnos.db.dao.ProjectDAO;
import com.org.gnos.db.model.Project;
import com.org.gnos.events.GnosEvent;
import com.org.gnos.events.GnosEventWithAttributeMap;
import com.org.gnos.ui.custom.controls.GnosScreen;
import com.org.gnos.utilities.ClickBehavior;
import com.org.gnos.utilities.SWTResourceManager;

public class ProjectScreen extends GnosScreen {

	public ProjectScreen(Composite parent, int style) {
		super(parent, style);
		
		this.createContent();

	}

	private void createContent(){
		
		
		setLayout(new FormLayout());
		
		CLabel labelHome = new CLabel(this, SWT.NONE);
		labelHome.setForeground(SWTResourceManager.getColor(255, 255, 255));
		labelHome.setFont(org.eclipse.wb.swt.SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		labelHome.setBackground(SWTResourceManager.getColor(0, 102, 204));
		FormData fd_labelHome = new FormData();
		fd_labelHome.bottom = new FormAttachment(0, 34);
		fd_labelHome.right = new FormAttachment(100);
		fd_labelHome.top = new FormAttachment(0);
		fd_labelHome.left = new FormAttachment(0);
		labelHome.setLayoutData(fd_labelHome);
		labelHome.setText("  Home");
		
		Label labelVerticalSeparator = new Label(this, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_labelVerticalSeparator = new FormData();
		fd_labelVerticalSeparator.left = new FormAttachment(25);
		fd_labelVerticalSeparator.top = new FormAttachment(labelHome, 0, SWT.BOTTOM);
		fd_labelVerticalSeparator.bottom = new FormAttachment(100);
		//fd_labelVerticalSeparator.right = new FormAttachment(0, 262);
		labelVerticalSeparator.setLayoutData(fd_labelVerticalSeparator);
		
		CLabel labelList = new CLabel(this, SWT.NONE);
		labelList.setFont(org.eclipse.wb.swt.SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		FormData fd_labelList = new FormData();
		fd_labelList.top = new FormAttachment(labelHome, 6);
		fd_labelList.left = new FormAttachment(0, 10);
		labelList.setLayoutData(fd_labelList);
		labelList.setText("Available Projects:");
		

		List<Project> projects = ProjectDAO.getAll();
		final Object me = this;
		CLabel lastLabel = labelList;
		for(int i=0; i< projects.size(); i++){
			CLabel labelProject1 = new CLabel(this, SWT.NONE);
			labelProject1.setForeground(SWTResourceManager.getColor(255, 255, 255));
			labelProject1.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
			labelProject1.setBackground(SWTResourceManager.getColor(0, 102, 204));
			FormData fd_labelProject1 = new FormData();
			fd_labelProject1.bottom = new FormAttachment(lastLabel, 40, SWT.BOTTOM);
			fd_labelProject1.right = new FormAttachment(labelVerticalSeparator, -6);
			fd_labelProject1.top = new FormAttachment(lastLabel, 6);
			fd_labelProject1.left = new FormAttachment(0, 10);
			labelProject1.setLayoutData(fd_labelProject1);
			labelProject1.setText(projects.get(i).getName());
			final int projectId = projects.get(i).getId();
			labelProject1.addMouseListener(new ClickBehavior(new Runnable(){
				@Override
				public void run() {
					HashMap<String, String> attributes = new HashMap<String, String>();
					
					attributes.put("projectId", ""+projectId);
					GnosEventWithAttributeMap event = new GnosEventWithAttributeMap(me, "project:opened", attributes);

					fireChildEvent(event);
					System.out.println("Got the click...");
				}
			}));
			lastLabel = labelProject1;
		}

		ScrolledComposite scViewPortContainer = new ScrolledComposite(this, SWT.V_SCROLL | SWT.NONE);
		FormData fd_scViewPortContainer = new FormData();
		fd_scViewPortContainer.right = new FormAttachment(labelHome, -6, SWT.RIGHT);
		fd_scViewPortContainer.bottom = new FormAttachment(100, -6);
		fd_scViewPortContainer.top = new FormAttachment(labelHome, 6);
		fd_scViewPortContainer.left = new FormAttachment(labelVerticalSeparator, 6);

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
			e.eventName = "project:created";
			fireChildEvent(e);
		}
		
	}
}
