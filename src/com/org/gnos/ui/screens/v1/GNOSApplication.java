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

public class GNOSApplication extends ApplicationWindow {

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
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FormLayout());
		
		TabFolder tabFolder = new TabFolder(container, SWT.NONE);
		FormData fd_tabFolder = new FormData();
		fd_tabFolder.bottom = new FormAttachment(100, -10);
		fd_tabFolder.left = new FormAttachment(0, 10);
		fd_tabFolder.top = new FormAttachment(0, 23);
		fd_tabFolder.right = new FormAttachment(100, -10);
		tabFolder.setLayoutData(fd_tabFolder);
		
		TabItem tbtmHome = new TabItem(tabFolder, SWT.NONE);
		tbtmHome.setImage(SWTResourceManager.getImage(GNOSApplicationWindow.class, "/com/org/gnos/resources/home24.png"));
		tbtmHome.setText("HOME");
		
		Composite homeComposite = new Composite(tabFolder, SWT.NONE);
		tbtmHome.setControl(homeComposite);
		StackLayout homeTabLayout = new StackLayout();
		homeComposite.setLayout(homeTabLayout);
		
		//Composite homeScreen = new Composite(homeComposite, SWT.NONE);
		//Composite createNewPageScreen = new Composite(homeComposite, SWT.NONE);
		Composite createNewPageScreen = new CreateNewProjectScreen(homeComposite, SWT.NONE);
		Composite homeScreen = new HomeScreen(homeComposite, SWT.NONE);
		
		homeTabLayout.topControl = createNewPageScreen;
		homeComposite.layout();
		
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
		newShell.setText("New Application");
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(814, 511);
	}

}
