package com.org.gnos.tabitems;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.events.GnosEvent;
import com.org.gnos.events.GnosEventWithAttributeMap;
import com.org.gnos.events.interfaces.GnosEventGenerator;
import com.org.gnos.events.interfaces.GnosEventListener;
import com.org.gnos.ui.screens.v1.CreateNewProjectScreen;
import com.org.gnos.ui.screens.v1.HomeScreen;

public class HomeTabItem extends GnosCTabItem implements GnosEventGenerator, GnosEventListener{

	private ArrayList<GnosEventListener> listeners = new ArrayList<GnosEventListener>();
	private Composite mainComposite;
	private StackLayout mainLayout;
	private HomeScreen homeScreen;
	private CreateNewProjectScreen createNewProjectScreen;
	
	public HomeTabItem(CTabFolder parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
		createContent(parent);
	}
	
	@Override
	public void createContent(CTabFolder parent){
		this.setImage(SWTResourceManager.getImage(HomeTabItem.class, "/com/org/gnos/resources/home24.png"));
		this.setText("HOME");
		this.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		mainComposite = new Composite(parent, SWT.NONE);
		mainLayout = new StackLayout();
		mainComposite.setLayout(mainLayout);
		this.setControl(mainComposite);
		
		homeScreen = new HomeScreen(mainComposite, SWT.NONE);
		homeScreen.registerEventListener(this);
		
		createNewProjectScreen = new CreateNewProjectScreen(mainComposite, SWT.NONE);
		createNewProjectScreen.registerEventListener(this);
		
		mainLayout.topControl = homeScreen;
		mainComposite.layout();
	}
	
	@Override
	public void registerEventListener(GnosEventListener listener) {
		// TODO Auto-generated method stub
		listeners.add(listener);
		
	}
	@Override
	public void onGnosEventFired(GnosEvent e) {
		// TODO Auto-generated method stub
		if(e.eventName == "homeScreen:create-new-project"){
			mainLayout.topControl = createNewProjectScreen;
			mainComposite.layout();
		}else if(e.eventName == "createNewProjectScreen:upload-records-complete"){
			GnosEventWithAttributeMap event = (GnosEventWithAttributeMap)e;
			event.eventName = "homeTab:new-project-created";
			triggerEvent(event);
		}
	}
	
	private void triggerEvent(GnosEvent event){
		int j = listeners.size();
		int i = 0;
		for(i=0; i<j; i++){
			listeners.get(i).onGnosEventFired(event);
		}
	}
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
