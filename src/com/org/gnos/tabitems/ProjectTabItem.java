package com.org.gnos.tabitems;


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;

import com.org.gnos.custom.models.ProjectModel;
import com.org.gnos.events.GnosEvent;
import com.org.gnos.events.interfaces.GnosEventGenerator;
import com.org.gnos.events.interfaces.GnosEventListener;
import com.org.gnos.ui.screens.v1.WorkbenchScreen;
import com.org.gnos.utilities.SWTResourceManager;

public class ProjectTabItem extends GnosCTabItem implements GnosEventGenerator,GnosEventListener{

	public String projectName;
	private ProjectModel projectModel;
	
	public ProjectTabItem(CTabFolder parent, int style, ProjectModel projectModel) {
		super(parent, style);
		// TODO Auto-generated constructor stub
		this.projectModel = projectModel;
		this.projectName = this.projectModel.get("projectName");
		createContent(parent);
	}

	@Override
	public void createContent(CTabFolder parent) {
		// TODO Auto-generated method stub
		this.setImage(SWTResourceManager.getImage(ProjectTabItem.class, "/com/org/gnos/resources/controls24.png"));
		this.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		this.setText(this.projectName.toUpperCase());
		//this.setControl(new ProjectWorkbenchScreen(parent, SWT.NONE));
		this.setControl(new WorkbenchScreen(parent, SWT.NONE, this.projectModel));
	}
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void onGnosEventFired(GnosEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerEventListener(GnosEventListener listener) {
		// TODO Auto-generated method stub
		
	}

}
