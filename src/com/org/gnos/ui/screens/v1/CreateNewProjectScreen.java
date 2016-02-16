package com.org.gnos.ui.screens.v1;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.events.GnosEvent;
import com.org.gnos.events.GnosEventWithAttributeMap;
import com.org.gnos.events.interfaces.GnosEventGenerator;
import com.org.gnos.events.interfaces.GnosEventListener;
import com.org.gnos.services.DumpCSV;

public class CreateNewProjectScreen extends Composite implements GnosEventGenerator{
	private Text textProjectName;
	private Text textLocation;
	private Text textDescription;
	private ArrayList<GnosEventListener> listeners = new ArrayList<GnosEventListener>();
	private String csvFileName;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CreateNewProjectScreen(final Composite parent, int style) {
		super(parent, style);
		setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		setLayout(new FormLayout());

		Composite composite = new Composite(this, SWT.BORDER);
		composite.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		FormData fd_composite = new FormData();
		fd_composite.bottom = new FormAttachment(50, 254);
		fd_composite.right = new FormAttachment(50, 325);
		/*fd_composite.bottom = new FormAttachment(labelCreateNew, 467, SWT.BOTTOM);
		fd_composite.top = new FormAttachment(labelCreateNew, 32);
		fd_composite.right = new FormAttachment(100, -91);
		fd_composite.left = new FormAttachment(0, 92);
		composite.setLayoutData(fd_composite);*/

		Label labelProjectName = new Label(composite, SWT.NONE);
		labelProjectName.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		labelProjectName.setBounds(10, 73, 105, 24);
		labelProjectName.setText("Project Name:");

		textProjectName = new Text(composite, SWT.BORDER);
		textProjectName.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		textProjectName.setBounds(10, 103, 589, 33);

		Label labelProjectLocation = new Label(composite, SWT.NONE);
		labelProjectLocation.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		labelProjectLocation.setBounds(10, 142, 143, 24);
		labelProjectLocation.setText("Data Location:");

		textLocation = new Text(composite, SWT.BORDER);
		textLocation.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		textLocation.setBounds(10, 172, 589, 33);

		Button btnBrowseFile = new Button(composite, SWT.NONE);
		btnBrowseFile.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//TODO open system directory
				//FileDialog dialog = new FileDialog(parent.getShell(), SWT.OPEN);
				FileDialog dialog = new FileDialog(parent.getShell(), SWT.OPEN);
				dialog.setFilterExtensions(new String [] {"*.csv"});
				dialog.setFilterPath("c:\\");
				csvFileName = dialog.open();
				System.out.println("Selected file" + csvFileName);
				if(csvFileName != null){
					textLocation.setText(csvFileName);
				}
			}
		});
		btnBrowseFile.setBounds(601, 172, 29, 33);
		btnBrowseFile.setText("....");

		Label labelDescription = new Label(composite, SWT.NONE);
		labelDescription.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		labelDescription.setBounds(10, 211, 143, 24);
		labelDescription.setText("Project Description:");

		textDescription = new Text(composite, SWT.BORDER);
		textDescription.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		textDescription.setBounds(10, 241, 589, 162);

		Button btnSubmit = new Button(composite, SWT.NONE);
		btnSubmit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//Create New Project
				HashMap<String, String> attributes = new HashMap<String, String>();
				
				attributes.put("projectName", textProjectName.getText());
				attributes.put("projectDescription", textDescription.getText());
				attributes.put("recordFileName", csvFileName);
				
				GnosEventWithAttributeMap event = new GnosEventWithAttributeMap(this, "createNewProjectScreen:upload-records-complete", attributes);
				if(csvFileName != null){
					uploadFileToDB(csvFileName);
				}
				fireChildEvent(event);
			}
		});
		btnSubmit.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		btnSubmit.setBounds(230, 409, 181, 33);
		btnSubmit.setText("Create");

		/*fd_composite.bottom = new FormAttachment(labelCreateNew, 467, SWT.BOTTOM);
		fd_composite.top = new FormAttachment(labelCreateNew, 32);
		fd_composite.right = new FormAttachment(100, -91);
		fd_composite.left = new FormAttachment(0, 92);
		composite.setLayoutData(fd_composite);*/


		int offsetX = -composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).x / 2;
		int offsetY = -composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y / 2;
		fd_composite.left = new FormAttachment(50,offsetX);
		fd_composite.top = new FormAttachment(50, offsetY);
		composite.setLayoutData(fd_composite);

		Label labelCreateNewIcon = new Label(composite, SWT.NONE);
		labelCreateNewIcon.setBounds(10, 10, 24, 38);
		labelCreateNewIcon.setImage(SWTResourceManager.getImage(CreateNewProjectScreen.class, "/com/org/gnos/resources/addFile24.png"));

		Label labelCreateNew = new Label(composite, SWT.NONE);
		labelCreateNew.setBounds(36, 20, 136, 18);
		labelCreateNew.setFont(SWTResourceManager.getFont("Arial", 12, SWT.NORMAL));
		labelCreateNew.setText("Create New Project");

	}
	
	protected void uploadFileToDB(String fileName){
		DumpCSV dumpCSV = new DumpCSV();
		dumpCSV.dump(fileName);
	}

	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	private void fireChildEvent(GnosEvent event){
		int j = listeners.size();
		int i = 0;
		for(i=0; i<j; i++){
			listeners.get(i).onGnosEventFired(event);
		}
	}

	@Override
	public void registerEventListener(GnosEventListener listener) {
		// TODO Auto-generated method stub
		listeners.add(listener);
	}
}
