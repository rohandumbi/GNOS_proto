package com.org.gnos.application;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.StackLayout;

import com.org.gnos.ui.screens.v1.HomeScreen;

public class GNOSApplicationWindow {

	protected Shell shell;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			GNOSApplicationWindow window = new GNOSApplicationWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(834, 525);
		shell.setText("SWT Application");
		shell.setLayout(new FormLayout());
		
		TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
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
		@SuppressWarnings("unused")
		Composite createNewPageScreen = new Composite(homeComposite, SWT.NONE);
		Composite homeScreen = new HomeScreen(homeComposite, SWT.NONE);
		
		homeTabLayout.topControl = homeScreen;
		homeComposite.layout();

	}
}
