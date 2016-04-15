package com.org.gnos.ui.screens.v1;

import org.eclipse.swt.widgets.Composite;

import com.org.gnos.events.GnosEvent;
import com.org.gnos.ui.custom.controls.GnosScreen;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class PitGroupDefinitionScreen extends GnosScreen {
	private Text textPitGroupName;
	// Strings to use as list items
	private static final String[] PITITEMS = {"PIT DUMMY", 
		"PIT DUMMY", "PIT DUMMY", "PIT DUMMY",
		"PIT DUMMY", "PIT DUMMY", "PIT DUMMY"};
	
	private static final String[] GROUPITEMS = {"GROUP DUMMY", 
		"GROUP DUMMY", "GROUP DUMMY", "GROUP DUMMY", 
		"GROUP DUMMY", "GROUP DUMMY", "GROUP DUMMY"};

	public PitGroupDefinitionScreen(Composite parent, int style) {
		super(parent, style);
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		setLayout(new FormLayout());

		Composite compositeCreateGroup = new Composite(this, SWT.BORDER);
		compositeCreateGroup.setLayout(new FormLayout());
		FormData fd_compositeCreateGroup = new FormData();
		fd_compositeCreateGroup.top = new FormAttachment(0, 10);
		fd_compositeCreateGroup.left = new FormAttachment(0, 10);
		fd_compositeCreateGroup.bottom = new FormAttachment(100, -10);
		fd_compositeCreateGroup.right = new FormAttachment(60);
		compositeCreateGroup.setLayoutData(fd_compositeCreateGroup);

		Label lblDefineNewGroup = new Label(compositeCreateGroup, SWT.NONE);
		lblDefineNewGroup.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		FormData fd_lblDefineNewGroup = new FormData();
		fd_lblDefineNewGroup.top = new FormAttachment(0, 10);
		fd_lblDefineNewGroup.left = new FormAttachment(0, 10);
		lblDefineNewGroup.setLayoutData(fd_lblDefineNewGroup);
		lblDefineNewGroup.setText("Create Pit Group:");

		textPitGroupName = new Text(compositeCreateGroup, SWT.BORDER);
		FormData fd_textPitGroupName = new FormData();
		fd_textPitGroupName.right = new FormAttachment(100, -10);
		fd_textPitGroupName.top = new FormAttachment(0, 10);
		fd_textPitGroupName.left = new FormAttachment(lblDefineNewGroup, 6);
		textPitGroupName.setLayoutData(fd_textPitGroupName);

		/*
		 * Grouping Pits
		 */

		//Existing Pits Label
		Label lblAllPits = new Label(compositeCreateGroup, SWT.NONE);
		lblAllPits.setLayoutData(new FormData());
		lblAllPits.setText("Existing Pits");
		FormData fd_lblAllPits = new FormData();
		fd_lblAllPits.top = new FormAttachment(lblDefineNewGroup, 18, SWT.BOTTOM);
		fd_lblAllPits.left = new FormAttachment(lblDefineNewGroup, 0, SWT.LEFT);
		lblAllPits.setLayoutData(fd_lblAllPits);

		//Added Pits Label
		Label lblAddedPits = new Label(compositeCreateGroup, SWT.NONE);
		lblAddedPits.setLayoutData(new FormData());
		lblAddedPits.setText("Added Pits");
		FormData fd_lblAddedPits = new FormData();
		fd_lblAddedPits.top = new FormAttachment(lblDefineNewGroup, 18, SWT.BOTTOM);
		fd_lblAddedPits.left = new FormAttachment(50, 25);
		lblAddedPits.setLayoutData(fd_lblAddedPits);

		//List Existing Pits
		List listAllPits = new List(compositeCreateGroup, SWT.BORDER|SWT.V_SCROLL|SWT.MULTI);
		FormData fd_listAllPits = new FormData();
		fd_listAllPits.bottom = new FormAttachment(50, -10);
		fd_listAllPits.top = new FormAttachment(lblAllPits, 2);
		fd_listAllPits.left = new FormAttachment(0, 10);
		fd_listAllPits.right = new FormAttachment(50, -25);
		listAllPits.setLayoutData(fd_listAllPits);
		// Add the items, one by one
	    for (int i = 0, n = PITITEMS.length; i < n; i++) {
	    	listAllPits.add(PITITEMS[i]);
	    }

		//Button Add Pit
		Button btnAddPit = new Button(compositeCreateGroup, SWT.NONE);
		btnAddPit.setText("->");
		btnAddPit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//implement add pit to group
			}
		});
		FormData fd_btnAddPit = new FormData();
		int offsetXbtnAddPit = -btnAddPit.computeSize(SWT.DEFAULT, SWT.DEFAULT).x / 2;
		fd_btnAddPit.top = new FormAttachment(listAllPits, 100, SWT.TOP);
		fd_btnAddPit.left = new FormAttachment(50, offsetXbtnAddPit);
		btnAddPit.setLayoutData(fd_btnAddPit);

		//List Added Pits
		List listAddedPits = new List(compositeCreateGroup, SWT.BORDER|SWT.V_SCROLL);
		FormData fd_listAddedPits = new FormData();
		fd_listAddedPits.top = new FormAttachment(listAllPits, 0, SWT.TOP);
		fd_listAddedPits.bottom = new FormAttachment(listAllPits, 0, SWT.BOTTOM);
		fd_listAddedPits.left = new FormAttachment(50, 25);
		fd_listAddedPits.right = new FormAttachment(100, -10);
		listAddedPits.setLayoutData(fd_listAddedPits);


		/*
		 * Grouping Groups
		 */

		//Existing Groups Label
		Label lblAllGroups = new Label(compositeCreateGroup, SWT.NONE);
		lblAllGroups.setLayoutData(new FormData());
		lblAllGroups.setText("Existing Groups");
		FormData fd_lblAllGroups = new FormData();
		fd_lblAllGroups.top = new FormAttachment(listAllPits, 10, SWT.BOTTOM);
		fd_lblAllGroups.left = new FormAttachment(lblDefineNewGroup, 0, SWT.LEFT);
		lblAllGroups.setLayoutData(fd_lblAllGroups);

		//Added Groups Label
		Label lblAddedGroups = new Label(compositeCreateGroup, SWT.NONE);
		lblAddedGroups.setLayoutData(new FormData());
		lblAddedGroups.setText("Added Groups");
		FormData fd_lblAddedGroups = new FormData();
		fd_lblAddedGroups.top = new FormAttachment(lblAllGroups, 0, SWT.TOP);
		fd_lblAddedGroups.left = new FormAttachment(50, 25);
		lblAddedGroups.setLayoutData(fd_lblAddedGroups);

		//List Existing Groups
		List listAllGroups = new List(compositeCreateGroup, SWT.BORDER|SWT.V_SCROLL|SWT.MULTI);
		FormData fd_listAllGroups = new FormData();
		fd_listAllGroups.bottom = new FormAttachment(100, -25);
		fd_listAllGroups.top = new FormAttachment(lblAllGroups, 2);
		fd_listAllGroups.left = new FormAttachment(0, 10);
		fd_listAllGroups.right = new FormAttachment(50, -25);
		listAllGroups.setLayoutData(fd_listAllGroups);
		// Add the items, one by one
		for (int i = 0, n = GROUPITEMS.length; i < n; i++) {
			listAllGroups.add(GROUPITEMS[i]);
	    }

		//Button Add Group
		Button buttonAddGroup = new Button(compositeCreateGroup, SWT.NONE);
		buttonAddGroup.setText("->");
		buttonAddGroup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//implement add group to group
			}
		});
		FormData fd_buttonAddGroup = new FormData();
		fd_buttonAddGroup.top = new FormAttachment(listAllGroups, 100, SWT.TOP);
		fd_buttonAddGroup.left = new FormAttachment(btnAddPit, 0, SWT.LEFT);
		buttonAddGroup.setLayoutData(fd_buttonAddGroup);

		//List Added Groups
		List listAddedGroups = new List(compositeCreateGroup, SWT.BORDER|SWT.V_SCROLL);
		FormData fd_listAddedGroups = new FormData();
		fd_listAddedGroups.top = new FormAttachment(listAllGroups, 0, SWT.TOP);
		fd_listAddedGroups.bottom = new FormAttachment(listAllGroups, 0, SWT.BOTTOM);
		fd_listAddedGroups.left = new FormAttachment(50, 25);
		fd_listAddedGroups.right = new FormAttachment(100, -10);
		listAddedGroups.setLayoutData(fd_listAddedGroups);

		//Button Save Pit Group
		Button btnSave = new Button(compositeCreateGroup, SWT.NONE);
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//implement save pit group definition
			}
		});
		FormData fd_btnSave = new FormData();
		btnSave.setText("SAVE");
		int offsetXbtnSave = -btnSave.computeSize(SWT.DEFAULT, SWT.DEFAULT).x / 2;
		fd_btnSave.bottom = new FormAttachment(100, -2);
		fd_btnSave.left = new FormAttachment(50, offsetXbtnSave);
		btnSave.setLayoutData(fd_btnSave);

		Composite compositeExistingGroups = new Composite(this, SWT.BORDER);
		compositeExistingGroups.setLayout(new FormLayout());
		FormData fd_compositeExistingGroups = new FormData();
		fd_compositeExistingGroups.bottom = new FormAttachment(compositeCreateGroup, 0, SWT.BOTTOM);
		fd_compositeExistingGroups.top = new FormAttachment(compositeCreateGroup, 0, SWT.TOP);
		fd_compositeExistingGroups.right = new FormAttachment(100, -10);
		fd_compositeExistingGroups.left = new FormAttachment(compositeCreateGroup, 20);
		compositeExistingGroups.setLayoutData(fd_compositeExistingGroups);

		Label lblExistingPitGroups = new Label(compositeExistingGroups, SWT.NONE);
		lblExistingPitGroups.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		FormData fd_lblExistingPitGroups = new FormData();
		fd_lblExistingPitGroups.top = new FormAttachment(0, 10);
		fd_lblExistingPitGroups.left = new FormAttachment(0, 10);
		lblExistingPitGroups.setLayoutData(fd_lblExistingPitGroups);
		lblExistingPitGroups.setText("Existing Pit Groups:");
	}

	@Override
	public void onGnosEventFired(GnosEvent e) {
		// TODO Auto-generated method stub

	}
}
