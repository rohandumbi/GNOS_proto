package com.org.gnos.ui.screens.v1;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
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

public class GNOSApplication extends ApplicationWindow implements GnosEventListener{

	private Composite container;
	//private CTabFolder cTabFolder;
	//private HomeTabItem homeTabItem;
	//private ProjectTabItem projectTabItem;
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
		container.setLayout(new FormLayout());
		
		CLabel labelProject = new CLabel(container, SWT.NONE);
		labelProject.setForeground(SWTResourceManager.getColor(255, 255, 255));
		labelProject.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		labelProject.setBackground(SWTResourceManager.getColor(0, 102, 204));
		FormData fd_labelProject = new FormData();
		fd_labelProject.bottom = new FormAttachment(0, 34);
		fd_labelProject.right = new FormAttachment(100);
		fd_labelProject.top = new FormAttachment(0);
		fd_labelProject.left = new FormAttachment(0);
		labelProject.setLayoutData(fd_labelProject);
		labelProject.setText("Projects");
		
		Label label = new Label(container, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_label = new FormData();
		fd_label.left = new FormAttachment(0, 260);
		fd_label.top = new FormAttachment(labelProject, 6);
		fd_label.bottom = new FormAttachment(100, 10);
		fd_label.right = new FormAttachment(0, 262);
		label.setLayoutData(fd_label);
		
		CLabel lastLabel = labelProject;
		for(int i=0; i< 10; i++){
			CLabel labelProject1 = new CLabel(container, SWT.NONE);
			labelProject1.setForeground(SWTResourceManager.getColor(255, 255, 255));
			labelProject1.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
			labelProject1.setBackground(SWTResourceManager.getColor(0, 102, 204));
			FormData fd_labelProject1 = new FormData();
			fd_labelProject1.bottom = new FormAttachment(lastLabel, 40, SWT.BOTTOM);
			fd_labelProject1.right = new FormAttachment(label, -6);
			fd_labelProject1.top = new FormAttachment(lastLabel, 6);
			fd_labelProject1.left = new FormAttachment(0, 10);
			labelProject1.setLayoutData(fd_labelProject1);
			labelProject1.setText("Project "+(i+1));
			lastLabel = labelProject1;
		}

		ScrolledComposite scViewPortContainer = new ScrolledComposite(container, SWT.V_SCROLL | SWT.NONE);
		FormData fd_scViewPortContainer = new FormData();
		fd_scViewPortContainer.right = new FormAttachment(labelProject, -6, SWT.RIGHT);
		fd_scViewPortContainer.bottom = new FormAttachment(100, -6);
		fd_scViewPortContainer.top = new FormAttachment(labelProject, 6);
		fd_scViewPortContainer.left = new FormAttachment(label, 6);

		CreateNewProjectScreen newProjectScreen = new CreateNewProjectScreen(scViewPortContainer, SWT.BORDER);

		scViewPortContainer.setContent(newProjectScreen);
		scViewPortContainer.setExpandHorizontal(true);
		scViewPortContainer.setExpandVertical(true);
		scViewPortContainer.setLayout(new FillLayout());
		scViewPortContainer.setLayoutData(fd_scViewPortContainer);
		
		newProjectScreen.registerEventListener(this);
		//cTabFolder = new CTabFolder(container, SWT.NONE);
		//cTabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
/*		FormData fd_tabFolder = new FormData();
		fd_tabFolder.bottom = new FormAttachment(100, -10);
		fd_tabFolder.left = new FormAttachment(0, 10);
		fd_tabFolder.top = new FormAttachment(0, 23);
		fd_tabFolder.right = new FormAttachment(100, -10);*/
		//tabFolder.setLayoutData(fd_tabFolder);
		//cTabFolder.setLayoutData(fd_tabFolder);

		//homeTabItem = new HomeTabItem(cTabFolder, SWT.NONE);
		//homeTabItem.registerEventListener(this);

		
		Label label_1 = new Label(container, SWT.NONE);
		label_1.setLayoutData(new FormData());
		
		getShell().setMinimumSize(814, 511);
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
		return new Point(301, 142);
	}

	@Override
	public void onGnosEventFired(GnosEvent e) {
		// TODO Auto-generated method stub
		if(e.eventName == "homeTab:new-project-created"){
			GnosEventWithAttributeMap event = (GnosEventWithAttributeMap)e;
			openPitControlsTab(event);
		} else if(e.eventName == "createNewProjectScreen:upload-records-complete") {
			System.out.println("New project event fired");
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
		
		container.setLayoutData(new WorkbenchScreen(container, SWT.NONE, projectModel));
		//projectTabItem = new ProjectTabItem(cTabFolder, SWT.CLOSE, projectModel);
		//projectTabItem.registerEventListener(this);
		//cTabFolder.setSelection(projectTabItem);

		//homeTabItem.createContent(cTabFolder);
	}

}
