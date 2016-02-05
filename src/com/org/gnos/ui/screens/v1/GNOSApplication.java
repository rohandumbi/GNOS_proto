package com.org.gnos.ui.screens.v1;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.application.GNOSApplicationWindow;
import com.org.gnos.application.GNOSConfig;
import com.org.gnos.events.GnosEvent;
import com.org.gnos.events.GnosEventWithAttributeMap;
import com.org.gnos.events.interfaces.GnosEventListener;
import com.org.gnos.tabitems.HomeTabItem;
import com.org.gnos.tabitems.ProjectTabItem;

public class GNOSApplication extends ApplicationWindow implements GnosEventListener{

	private Composite container;
	private TabFolder tabFolder;
	private HomeTabItem homeTabItem;
	private ProjectTabItem projectTabItem;
	/**
	 * Create the application window.
	 */
	public GNOSApplication() {
		super(null);
		createActions();
		addToolBar(SWT.FLAT | SWT.WRAP);
		addMenuBar();
		addStatusLine();
	}

	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setLayout(new FormLayout());
		tabFolder = new TabFolder(container, SWT.NONE);
		FormData fd_tabFolder = new FormData();
		fd_tabFolder.bottom = new FormAttachment(100, -10);
		fd_tabFolder.left = new FormAttachment(0, 10);
		fd_tabFolder.top = new FormAttachment(0, 23);
		fd_tabFolder.right = new FormAttachment(100, -10);
		tabFolder.setLayoutData(fd_tabFolder);
		
		homeTabItem = new HomeTabItem(tabFolder, SWT.NONE);
		homeTabItem.registerEventListener(this);
		
		parent.getShell().setMinimumSize(814, 511);
		//parent.getShell().setMaximized(false);
		parent.pack();

		return container;
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Create the menu manager.
	 * @return the menu manager
	 */
	@Override
	protected MenuManager createMenuManager() {
		return null;
	}

	/**
	 * Create the toolbar manager.
	 * @return the toolbar manager
	 */
	@Override
	protected ToolBarManager createToolBarManager(int style) {
		return null;
	}

	/**
	 * Create the status line manager.
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager() {
		StatusLineManager statusLineManager = new StatusLineManager();
		return statusLineManager;
	}

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			GNOSConfig.load();
			GNOSApplication window = new GNOSApplication();
			window.setBlockOnOpen(true);
			window.open();
			Display.getCurrent().dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Configure the shell.
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("GNOS - New age mining");
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(814, 511);
	}

	@Override
	public void onGnosEventFired(GnosEvent e) {
		// TODO Auto-generated method stub
		if(e.eventName == "homeTab:new-project-created"){
			GnosEventWithAttributeMap event = (GnosEventWithAttributeMap)e;
			openPitControlsTab(event);
		}
		
	}
	
	private void openPitControlsTab(GnosEventWithAttributeMap event){
		System.out.println("Opening pit controls tab");
		projectTabItem = new ProjectTabItem(tabFolder, SWT.NONE, event.attributes.get("projectName"));
		projectTabItem.registerEventListener(this);
		tabFolder.setSelection(projectTabItem);
	}

}
