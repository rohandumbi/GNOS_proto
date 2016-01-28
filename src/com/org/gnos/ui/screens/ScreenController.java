package com.org.gnos.ui.screens;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.org.gnos.events.ChildScreenEvent;
import com.org.gnos.events.interfaces.ChildScreenEventListener;
import com.org.gnos.utilities.SWTResourceManager;

public class ScreenController extends ApplicationWindow implements ChildScreenEventListener{

	private StackLayout stackLayout;
	private HomeScreen oHomeScreen;
	private CreateNewProjectScreen oCreateNewProjectScreen;
	private UploadRecordsScreen oUploadRecordsScreen;
	private Composite container;
	private Composite homeScreen;
	private Composite createNewProjectScreen;
	private Composite uploadRecordsScreen;

	/**
	 * Create the application window.
	 */
	public ScreenController() {
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
	protected Control createContents(Composite parent) {
		parent.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		container = new Composite(parent, SWT.NONE);
		//container.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		stackLayout = new StackLayout();
		container.setLayout(stackLayout);

		Image backgroundImage = new Image (Display.getCurrent(), "resources/bg.jpg");
		container.setBackgroundImage(backgroundImage);
		container.setBackgroundMode(SWT.INHERIT_FORCE);
		container.setBounds(0, 0, backgroundImage.getBounds().width, backgroundImage.getBounds().height);

		oHomeScreen = new HomeScreen(container);
		oHomeScreen.registerEventListener(this);
		homeScreen = oHomeScreen.render();

		oCreateNewProjectScreen = new CreateNewProjectScreen(container);
		oCreateNewProjectScreen.registerEventListener(this);
		createNewProjectScreen = oCreateNewProjectScreen.render();

		oUploadRecordsScreen = new UploadRecordsScreen(container);
		oUploadRecordsScreen.registerEventListener(this);
		uploadRecordsScreen = oUploadRecordsScreen.render();

		stackLayout.topControl = homeScreen;
		container.layout();

		parent.getShell().setMinimumSize(1200, 700);
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
	protected MenuManager createMenuManager() {
		MenuManager menuManager = new MenuManager("menu");
		//return menuManager;
		return null;
	}

	/**
	 * Create the toolbar manager.
	 * @return the toolbar manager
	 */
	protected ToolBarManager createToolBarManager(int style) {
		return null;
	}

	/**
	 * Create the status line manager.
	 * @return the status line manager
	 */
	protected StatusLineManager createStatusLineManager() {
		StatusLineManager statusLineManager = new StatusLineManager();
		return statusLineManager;
	}


	/**
	 * Configure the shell.
	 * @param newShell
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("GNOS - New Age Mining Software");
	}

	/**
	 * Return the initial size of the window.
	 */
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

	@Override
	public void onChildScreenEventFired(ChildScreenEvent e) {
		// TODO Auto-generated method stub
		if(e.eventName == "homeScreen:create-new-project"){
			stackLayout.topControl = createNewProjectScreen;
			container.layout();
		}else if(e.eventName == "createNewProjectScreen:upload-records"){
			stackLayout.topControl = uploadRecordsScreen;
			container.layout();
		}else if(e.eventName == "uploadScreen:upload-records"){
			//TODO
			System.out.println("Upload file to DB after processing");
		}
	}
}
