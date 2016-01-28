package com.org.gnos.ui.screens;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.org.gnos.utilities.SWTResourceManager;

public class ScreenController extends ApplicationWindow {
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
		final Composite container = new Composite(parent, SWT.NONE);
		container.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		final StackLayout stackLayout = new StackLayout();
		container.setLayout(stackLayout);
		
		HomeScreen oHomeScreen = new HomeScreen(container);
		Composite homeScreen = oHomeScreen.render();
		
		CreateNewProjectScreen oCreateNewProjectScreen = new CreateNewProjectScreen(container);
		Composite createNewProjectScreen = oCreateNewProjectScreen.render();
		
		UploadRecords oUploadRecordsScreen = new UploadRecords(container);
		Composite uploadRecordsScreen = oUploadRecordsScreen.render();
		
		stackLayout.topControl = homeScreen;
		container.layout();

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
	 * Launch the application.
	 * @param args
	 */
	/*public static void main(String args[]) {
		try {
			MultiScreenTest window = new MultiScreenTest();
			window.setBlockOnOpen(true);
			window.open();
			Display.getCurrent().dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	/**
	 * Configure the shell.
	 * @param newShell
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("New Application");
	}

	/**
	 * Return the initial size of the window.
	 */
	protected Point getInitialSize() {
		return new Point(450, 300);
	}
}
