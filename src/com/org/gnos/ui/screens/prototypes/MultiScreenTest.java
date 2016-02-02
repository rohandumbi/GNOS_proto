package com.org.gnos.ui.screens.prototypes;

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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.org.gnos.utilities.SWTResourceManager;

public class MultiScreenTest extends ApplicationWindow {
	private Text text;
	private Text text_1;
	private Text text_2;

	/**
	 * Create the application window.
	 */
	public MultiScreenTest() {
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
		
		final Composite homeScreen = new Composite(container, SWT.NONE);
		homeScreen.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		homeScreen.setLayout(null);
		
		Button createNewProjectButton = new Button(homeScreen, SWT.NONE);
		/*createNewProjectButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				stackLayout.topControl = homeScreen;
				//homeScreen.setVisible(true);
				container.layout();
			}
		});*/
		createNewProjectButton.setGrayed(true);
		createNewProjectButton.setBounds(115, 38, 195, 50);
		createNewProjectButton.setText("Create New Project");
		
		Button oprnExisitingProjectButton = new Button(homeScreen, SWT.NONE);
		oprnExisitingProjectButton.setBounds(115, 110, 195, 50);
		oprnExisitingProjectButton.setText("Open Exisitn Project");
		
		final Composite createNewProjectScreen = new Composite(container, SWT.NONE);
		createNewProjectScreen.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		createNewProjectScreen.setLayout(null);
		
		Label lblNewLabel = new Label(createNewProjectScreen, SWT.NONE);
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblNewLabel.setBounds(44, 42, 91, 15);
		lblNewLabel.setText("Name:");
		
		text = new Text(createNewProjectScreen, SWT.BORDER);
		text.setBounds(44, 58, 331, 21);
		
		Label lblNewLabel_1 = new Label(createNewProjectScreen, SWT.NONE);
		lblNewLabel_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblNewLabel_1.setBounds(44, 96, 91, 15);
		lblNewLabel_1.setText("Description:");
		
		text_1 = new Text(createNewProjectScreen, SWT.BORDER);
		text_1.setBounds(44, 117, 331, 82);
		
		Button btnSubmit = new Button(createNewProjectScreen, SWT.NONE);
		/*btnSubmit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//fileUploadScreen.
				stackLayout.topControl = fileUploadScreen;
			}
		});*/
		btnSubmit.setBounds(300, 205, 75, 25);
		btnSubmit.setText("Submit");
		
		final Composite fileUploadScreen = new Composite(container, SWT.NONE);
		fileUploadScreen.setBackgroundMode(SWT.INHERIT_DEFAULT);
		fileUploadScreen.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		fileUploadScreen.setLayout(null);
		
		Label lblSelectFile = new Label(fileUploadScreen, SWT.NONE);
		lblSelectFile.setBounds(22, 28, 60, 15);
		lblSelectFile.setText("Select File:");
		
		text_2 = new Text(fileUploadScreen, SWT.BORDER);
		text_2.setBounds(88, 22, 264, 21);
		
		Button fileBrowserButton = new Button(fileUploadScreen, SWT.NONE);
		fileBrowserButton.setBounds(358, 23, 75, 20);
		fileBrowserButton.setText("Browse...");
		
		Button fileSubmitButton = new Button(fileUploadScreen, SWT.NONE);
		fileSubmitButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnSubmit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//fileUploadScreen.setVisible(true);
				stackLayout.topControl = fileUploadScreen;
				container.layout();
			}
		});
		createNewProjectButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				stackLayout.topControl = createNewProjectScreen;
				//homeScreen.setVisible(true);
				container.layout();
			}
		});
		fileSubmitButton.setBounds(120, 141, 199, 25);
		fileSubmitButton.setText("Submit");
		
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
