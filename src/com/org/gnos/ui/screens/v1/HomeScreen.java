package com.org.gnos.ui.screens.v1;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.org.gnos.events.ChildScreenEvent;
import com.org.gnos.events.interfaces.ChildScreenEventGenerator;
import com.org.gnos.events.interfaces.ChildScreenEventListener;

public class HomeScreen extends Composite implements ChildScreenEventGenerator{

	private ArrayList<ChildScreenEventListener> listeners = new ArrayList<ChildScreenEventListener>();
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public HomeScreen(Composite parent, int style) {
		super(parent, style);
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		//setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		setLayout(new FormLayout());
		
		Composite compositeButtonGroup = new Composite(this, SWT.NONE);
		compositeButtonGroup.setLayout(new FormLayout());
		FormData fd_compositeButtonGroup = new FormData();
		
		/*
		fd_composite.top = new FormAttachment(0, 134);
		fd_composite.right = new FormAttachment(100, -196);
		fd_composite.bottom = new FormAttachment(0, 367);
		fd_composite.left = new FormAttachment(100, -584);
		composite.setLayoutData(fd_composite);*/
		
		Button buttonOpenExisting = new Button(compositeButtonGroup, SWT.NONE);
		buttonOpenExisting.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO open exisitng project action
			}
		});
		buttonOpenExisting.setFont(SWTResourceManager.getFont("Arial", 14, SWT.NORMAL));
		buttonOpenExisting.setImage(SWTResourceManager.getImage(HomeScreen.class, "/com/org/gnos/resources/openFile64.png"));
		FormData fd_buttonOpenExisting = new FormData();
		//fd_buttonOpenExisting.right = new FormAttachment(0, 345);
		fd_buttonOpenExisting.top = new FormAttachment(0);
		fd_buttonOpenExisting.left = new FormAttachment(0);
		buttonOpenExisting.setLayoutData(fd_buttonOpenExisting);
		buttonOpenExisting.setText("Open Existing Project");
		
		Button buttonCreateNew = new Button(compositeButtonGroup, SWT.NONE);
		buttonCreateNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO create new project action
				ChildScreenEvent event = new ChildScreenEvent(this, "homeScreen:create-new-project");
				fireChildEvent(event);
			}
		});
		buttonCreateNew.setText("Create New Project");
		buttonCreateNew.setImage(SWTResourceManager.getImage(HomeScreen.class, "/com/org/gnos/resources/addFile64.png"));
		buttonCreateNew.setFont(SWTResourceManager.getFont("Arial", 14, SWT.NORMAL));
		FormData fd_buttonCreateNew = new FormData();
		fd_buttonCreateNew.right = new FormAttachment(buttonOpenExisting, 0, SWT.RIGHT);
		fd_buttonCreateNew.top = new FormAttachment(buttonOpenExisting, 0, SWT.BOTTOM);
		fd_buttonCreateNew.left = new FormAttachment(buttonOpenExisting, 0, SWT.LEFT);
		buttonCreateNew.setLayoutData(fd_buttonCreateNew);
		
		
		int offsetX = -compositeButtonGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT).x / 2;
		int offsetY = -compositeButtonGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT).y / 2;
		fd_compositeButtonGroup.left = new FormAttachment(50,offsetX);
		fd_compositeButtonGroup.top = new FormAttachment(50,offsetY);
		compositeButtonGroup.setLayoutData(fd_compositeButtonGroup);
		
		Composite compositePageHeader = new Composite(this, SWT.NONE);
		FormData fd_compositePageHeader = new FormData();
		fd_compositePageHeader.bottom = new FormAttachment(0, 33);
		fd_compositePageHeader.top = new FormAttachment(0);
		fd_compositePageHeader.left = new FormAttachment(0);
		fd_compositePageHeader.right = new FormAttachment(0, 227);
		compositePageHeader.setLayoutData(fd_compositePageHeader);
		
		/*Label homeIcon = new Label(compositePageHeader, SWT.NONE);
		homeIcon.setImage(SWTResourceManager.getImage(HomeScreen.class, "/com/org/gnos/resources/home24.png"));
		homeIcon.setBounds(10, 0, 30, 33);*/
		
		/*Label labelHome = new Label(compositePageHeader, SWT.NONE);
		labelHome.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		labelHome.setBounds(46, 10, 55, 15);
		labelHome.setText("Home");*/

	}

	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	private void fireChildEvent(ChildScreenEvent event){
		int j = listeners.size();
		int i = 0;
		for(i=0; i<j; i++){
			listeners.get(i).onChildScreenEventFired(event);
		}
	}

	@Override
	public void registerEventListener(ChildScreenEventListener listener) {
		// TODO Auto-generated method stub
		listeners.add(listener);
	}
}
