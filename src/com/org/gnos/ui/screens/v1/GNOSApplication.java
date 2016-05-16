package com.org.gnos.ui.screens.v1;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.org.gnos.application.GNOSConfig;
import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.db.model.Field;
import com.org.gnos.events.GnosEvent;
import com.org.gnos.events.GnosEventWithAttributeMap;
import com.org.gnos.events.interfaces.GnosEventListener;
import com.org.gnos.services.EquationGenerator;
import com.org.gnos.services.csv.GNOSCSVDataProcessor;
import com.org.gnos.ui.custom.controls.GnosScreen;

public class GNOSApplication extends ApplicationWindow implements GnosEventListener{

	private Composite container;
	private StackLayout stackLayout;
	private GnosScreen viewPort;
	
	private GnosScreen projectScreen;
	private GnosScreen workbenchScreen;
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

		projectScreen = new ProjectScreen(container, SWT.NONE);
		projectScreen.registerEventListener(this);
		loadProjectScreen();
		
		getShell().setMinimumSize(814, 511);
		parent.pack();

		return container;
	}

	private void loadProjectScreen() {
		this.viewPort = projectScreen;
		this.viewPort.setParent(container);
		this.stackLayout.topControl = this.viewPort;
		container.layout();
	}
	
	private void loadWorkBenchScreen() {
		workbenchScreen = new WorkbenchScreen(container, SWT.NONE);
		workbenchScreen.registerEventListener(this);
		this.viewPort = workbenchScreen;
		this.viewPort.setParent(container);
		this.stackLayout.topControl = this.viewPort;
		container.layout();
	}
	
	private void loadCSVFile(String fileName) {
		ProjectConfigutration projectConfigutration = ProjectConfigutration.getInstance();
		gnosCsvDataProcessor = GNOSCSVDataProcessor.getInstance();
		gnosCsvDataProcessor.processCsv(fileName);
		gnosCsvDataProcessor.dumpToDB(projectConfigutration.getProjectId());
		String[] columns = gnosCsvDataProcessor.getHeaderColumns();
		List<Field> fields = new ArrayList<Field>();
		for(int i=0;i < columns.length; i++ ){
			Field  field = new Field(columns[i]);
			fields.add(field);
		}
		projectConfigutration.setFields(fields);
	}
	private void openProjectConfiguration(){
		loadWorkBenchScreen();
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
	 * Configure the shell.
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("GNOS - New age mining");
	}

	@Override
	protected void constrainShellSize() {
		super.constrainShellSize();
		getShell().setMaximized(true);
	}
	
	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(301, 142);
	}
	
	 /**
    *
    * @see org.eclipse.jface.window.Window#handleShellCloseEvent()
    */
	
   protected void handleShellCloseEvent() {
       /*boolean cancelled = saveAppIfDirty();
       if (cancelled) {
           setReturnCode(OK);
       } else {
           tidyUpOnExit();
           super.handleShellCloseEvent();
       }*/
	   tidyUpOnExit();
       super.handleShellCloseEvent();
   }
   
   private void tidyUpOnExit(){
	   //ProjectConfigutration.getInstance().save();
	   new EquationGenerator().generate();
   }
	
	

	@Override
	public void onGnosEventFired(GnosEvent e) {
		if(e.eventName == "project:created") {			
			GnosEventWithAttributeMap event = (GnosEventWithAttributeMap)e;
			String fileName = event.attributes.get("recordFileName");
			ProjectConfigutration.getInstance().setProjectId(Integer.parseInt(event.attributes.get("projectId")));
			loadCSVFile(fileName);
			openProjectConfiguration();
		} else if(e.eventName == "project:opened") {
			GnosEventWithAttributeMap event = (GnosEventWithAttributeMap)e;
			int projectId = Integer.parseInt(event.attributes.get("projectId"));
			ProjectConfigutration.getInstance().load(projectId);
			openProjectConfiguration();
		}else if(e.eventName == "open:homeScreen"){
			loadProjectScreen();
		}

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

}
