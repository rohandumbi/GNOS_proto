package com.org.gnos.ui.screens.v1;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.application.GNOSConfig;
import com.org.gnos.custom.models.ProjectModel;
import com.org.gnos.events.GnosEvent;
import com.org.gnos.events.GnosEventWithAttributeMap;
import com.org.gnos.events.interfaces.GnosEventListener;
import com.org.gnos.services.csv.GNOSCSVDataProcessor;
import com.org.gnos.ui.custom.controls.GnosScreen;

public class GNOSApplication extends ApplicationWindow implements GnosEventListener{

	private Composite container;
	//private CTabFolder cTabFolder;
	//private HomeTabItem homeTabItem;
	//private ProjectTabItem projectTabItem;
	private StackLayout stackLayout;
	private GnosScreen viewPort;
	private Shell dummyShell;
	private ProjectScreen projectScreen;
	private WorkbenchScreen workbenchScreen;
	private GNOSCSVDataProcessor gnosCsvDataProcessor;
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
		this.stackLayout = new StackLayout();
		container.setLayout(stackLayout);
		this.dummyShell = new Shell();

		projectScreen = new ProjectScreen(this.dummyShell, SWT.NONE);
		projectScreen.registerEventListener(this);
		loadProjectScreen();

		
		Label label_1 = new Label(container, SWT.NONE);
		label_1.setLayoutData(new FormData());
		
		getShell().setMinimumSize(814, 511);
		parent.pack();

		return container;
	}

	private void loadProjectScreen() {
		if(this.viewPort != null){
			//this.viewPort.dispose();
			this.viewPort.setParent(dummyShell);
		}
		this.viewPort = projectScreen;
		this.viewPort.setParent(container);
		this.stackLayout.topControl = this.viewPort;
		container.layout();
	}
	
	private void loadWorkBenchScreen(ProjectModel projectModel) {
		workbenchScreen = new WorkbenchScreen(this.dummyShell, SWT.NONE, projectModel);
		workbenchScreen.registerEventListener(this);
		if(this.viewPort != null){
			//this.viewPort.dispose();
			this.viewPort.setParent(dummyShell);
		}
		this.viewPort = workbenchScreen;
		this.viewPort.setParent(container);
		this.stackLayout.topControl = this.viewPort;
		container.layout();
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
		return new Point(301, 142);
	}

	@Override
	public void onGnosEventFired(GnosEvent e) {
		// TODO Auto-generated method stub
		if(e.eventName == "homeTab:new-project-created"){
			GnosEventWithAttributeMap event = (GnosEventWithAttributeMap)e;
			openPitControlsTab(event);
		} else if(e.eventName == "createNewProjectScreen:upload-records-complete") {
			GnosEventWithAttributeMap event = (GnosEventWithAttributeMap)e;
			openPitControlsTab(event);
		}

	}

	@Override
	protected void constrainShellSize() {
		super.constrainShellSize();
		getShell().setMaximized( true );
	}

	private void openPitControlsTab(GnosEventWithAttributeMap event){
		System.out.println("Opening pit controls tab");
		ProjectModel projectModel = new ProjectModel(event.attributes);
		
		gnosCsvDataProcessor = GNOSCSVDataProcessor.getInstance();
		gnosCsvDataProcessor.processCsv(event.attributes.get("recordFileName"));
		
		projectModel.setAllProjectFields(gnosCsvDataProcessor.getHeaderColumns());
		loadWorkBenchScreen(projectModel);
		//container.setLayoutData(new WorkbenchScreen(container, SWT.NONE, projectModel));
		//projectTabItem = new ProjectTabItem(cTabFolder, SWT.CLOSE, projectModel);
		//projectTabItem.registerEventListener(this);
		//cTabFolder.setSelection(projectTabItem);

		//homeTabItem.createContent(cTabFolder);
	}

}
