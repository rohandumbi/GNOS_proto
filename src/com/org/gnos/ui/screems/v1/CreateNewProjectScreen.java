package com.org.gnos.ui.screems.v1;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class CreateNewProjectScreen extends Composite {
	private Text textProjectName;
	private Text textLocation;
	private Text textDescription;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CreateNewProjectScreen(Composite parent, int style) {
		super(parent, style);
		setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		setLayout(new FormLayout());
		
		Label labelCreateNewIcon = new Label(this, SWT.NONE);
		labelCreateNewIcon.setImage(SWTResourceManager.getImage(CreateNewProjectScreen.class, "/com/org/gnos/resources/addFile24.png"));
		FormData fd_labelCreateNewIcon = new FormData();
		fd_labelCreateNewIcon.top = new FormAttachment(0);
		fd_labelCreateNewIcon.left = new FormAttachment(0, 10);
		fd_labelCreateNewIcon.bottom = new FormAttachment(0, 38);
		fd_labelCreateNewIcon.right = new FormAttachment(0, 34);
		labelCreateNewIcon.setLayoutData(fd_labelCreateNewIcon);
		
		Label labelCreateNew = new Label(this, SWT.NONE);
		labelCreateNew.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		FormData fd_labelCreateNew = new FormData();
		fd_labelCreateNew.top = new FormAttachment(0, 10);
		fd_labelCreateNew.left = new FormAttachment(labelCreateNewIcon, 6);
		labelCreateNew.setLayoutData(fd_labelCreateNew);
		labelCreateNew.setText("Create New Project");
		
		Composite composite = new Composite(this, SWT.BORDER);
		composite.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		FormData fd_composite = new FormData();
		fd_composite.bottom = new FormAttachment(50, 221);
		fd_composite.right = new FormAttachment(50, 325);
		/*fd_composite.bottom = new FormAttachment(labelCreateNew, 467, SWT.BOTTOM);
		fd_composite.top = new FormAttachment(labelCreateNew, 32);
		fd_composite.right = new FormAttachment(100, -91);
		fd_composite.left = new FormAttachment(0, 92);
		composite.setLayoutData(fd_composite);*/
		
		Label labelProjectName = new Label(composite, SWT.NONE);
		labelProjectName.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		labelProjectName.setBounds(10, 10, 105, 24);
		labelProjectName.setText("Project Name:");
		
		textProjectName = new Text(composite, SWT.BORDER);
		textProjectName.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		textProjectName.setBounds(10, 40, 589, 33);
		
		Label labelProjectLocation = new Label(composite, SWT.NONE);
		labelProjectLocation.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		labelProjectLocation.setBounds(10, 103, 143, 24);
		labelProjectLocation.setText("Project Location:");
		
		textLocation = new Text(composite, SWT.BORDER);
		textLocation.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		textLocation.setBounds(10, 133, 589, 33);
		
		Button btnProjectLocation = new Button(composite, SWT.NONE);
		btnProjectLocation.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//TODO open system directory
			}
		});
		btnProjectLocation.setBounds(605, 133, 29, 33);
		btnProjectLocation.setText("....");
		
		Label labelDescription = new Label(composite, SWT.NONE);
		labelDescription.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		labelDescription.setBounds(10, 182, 143, 24);
		labelDescription.setText("Project Description:");
		
		textDescription = new Text(composite, SWT.BORDER);
		textDescription.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		textDescription.setBounds(10, 212, 589, 162);
		
		Button btnNewButton = new Button(composite, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//TO Create New Project
			}
		});
		btnNewButton.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		btnNewButton.setBounds(233, 380, 181, 33);
		btnNewButton.setText("Create");
		
		/*fd_composite.bottom = new FormAttachment(labelCreateNew, 467, SWT.BOTTOM);
		fd_composite.top = new FormAttachment(labelCreateNew, 32);
		fd_composite.right = new FormAttachment(100, -91);
		fd_composite.left = new FormAttachment(0, 92);
		composite.setLayoutData(fd_composite);*/
		
		
		int offsetX = -composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).x / 2;
		int offsetY = -composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y / 2;
		fd_composite.left = new FormAttachment(50,offsetX);
		fd_composite.top = new FormAttachment(50,offsetY);
		composite.setLayoutData(fd_composite);

	}

	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
