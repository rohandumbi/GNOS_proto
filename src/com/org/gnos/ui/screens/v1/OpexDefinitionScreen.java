package com.org.gnos.ui.screens.v1;

import org.eclipse.swt.widgets.Composite;

import com.org.gnos.events.GnosEvent;
import com.org.gnos.ui.custom.controls.GnosScreen;
import com.org.gnos.ui.custom.controls.OpexDefinitionGrid;

import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.custom.ScrolledComposite;

public class OpexDefinitionScreen extends GnosScreen {

	private Composite parent;
	private Text textStartYear;
	private Text textNumberOfIncrements;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public OpexDefinitionScreen(Composite parent, int style) {
		super(parent, style);
		setForeground(SWTResourceManager.getColor(30, 144, 255));
		setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.parent = parent;
		this.createContent();

	}
	
	private void createContent(){
		setLayout(new FormLayout());
		Label labelScreenName = new Label(this, SWT.NONE);
		labelScreenName.setForeground(SWTResourceManager.getColor(0, 191, 255));
		labelScreenName.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_labelScreenName = new FormData();
		//fd_labelScreenName.bottom = new FormAttachment(100, -461);
		fd_labelScreenName.top = new FormAttachment(0, 10);
		fd_labelScreenName.left = new FormAttachment(0, 10);
		labelScreenName.setLayoutData(fd_labelScreenName);
		labelScreenName.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		labelScreenName.setText("OPEX Defintion");
		
		Label labelScreenDescription = new Label(this, SWT.NONE);
		labelScreenDescription.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		labelScreenDescription.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_labelScreenDescription = new FormData();
		fd_labelScreenDescription.top = new FormAttachment(labelScreenName, 10, SWT.BOTTOM);
		fd_labelScreenDescription.left = new FormAttachment(0, 10);
		//fd_labelScreenDescription.right = new FormAttachment(0, 866);
		labelScreenDescription.setLayoutData(fd_labelScreenDescription);
		labelScreenDescription.setText("Define your operational expenditures.");
		
		Label lblStartYear = new Label(this, SWT.NONE);
		lblStartYear.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_lblStartYear = new FormData();
		fd_lblStartYear.top = new FormAttachment(labelScreenDescription, 10);
		fd_lblStartYear.left = new FormAttachment(labelScreenDescription, 0, SWT.LEFT);
		lblStartYear.setLayoutData(fd_lblStartYear);
		lblStartYear.setText("Start Year:");
		
		textStartYear = new Text(this, SWT.BORDER);
		FormData fd_textStartYear = new FormData();
		fd_textStartYear.top = new FormAttachment(lblStartYear, -2, SWT.TOP);
		fd_textStartYear.left = new FormAttachment(lblStartYear, 8);
		textStartYear.setLayoutData(fd_textStartYear);
		
		Label lblNumberOfIncrements = new Label(this, SWT.NONE);
		lblNumberOfIncrements.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_lblNumberOfIncrements = new FormData();
		fd_lblNumberOfIncrements.top = new FormAttachment(lblStartYear, 0, SWT.TOP);
		fd_lblNumberOfIncrements.left = new FormAttachment(textStartYear, 10);
		lblNumberOfIncrements.setLayoutData(fd_lblNumberOfIncrements);
		lblNumberOfIncrements.setText("Number of Increments:");
		
		textNumberOfIncrements = new Text(this, SWT.BORDER);
		textNumberOfIncrements.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_textNumberOfIncrements = new FormData();
		fd_textNumberOfIncrements.top = new FormAttachment(textStartYear, -2, SWT.TOP);
		fd_textNumberOfIncrements.left = new FormAttachment(lblNumberOfIncrements, 8);
		textNumberOfIncrements.setLayoutData(fd_textNumberOfIncrements);
		
		Button btnAddTimePeriod = new Button(this, SWT.NONE);
		btnAddTimePeriod.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO implement time period add
			}
		});
		FormData fd_btnAddTimePeriod = new FormData();
		fd_btnAddTimePeriod.top = new FormAttachment(textNumberOfIncrements, 0, SWT.TOP);
		fd_btnAddTimePeriod.left = new FormAttachment(textNumberOfIncrements, 6);
		btnAddTimePeriod.setLayoutData(fd_btnAddTimePeriod);
		fd_btnAddTimePeriod.bottom = new FormAttachment(textNumberOfIncrements, 0, SWT.BOTTOM);
		btnAddTimePeriod.setText("Save");
		
		final ScrolledComposite scrolledComposite = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		FormData fd_scrolledComposite = new FormData();
		fd_scrolledComposite.top = new FormAttachment(textStartYear, 10, SWT.BOTTOM);
		fd_scrolledComposite.left = new FormAttachment(labelScreenName, 0, SWT.LEFT);
		fd_scrolledComposite.bottom = new FormAttachment(100, -10);
		fd_scrolledComposite.right = new FormAttachment(100, -10);
		
		OpexDefinitionGrid opexDefinitionGrid = new OpexDefinitionGrid(scrolledComposite, SWT.None);
		scrolledComposite.setContent(opexDefinitionGrid);
		scrolledComposite.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				System.out.println("MVCP resized");
				Rectangle r = scrolledComposite.getClientArea();
				//scViewPortContainer.setMinSize(mainConfigurationViewPort.computeSize(r.width, SWT.DEFAULT));
				scrolledComposite.setMinSize(scrolledComposite.computeSize(r.width, SWT.DEFAULT, true));
			}
		});
		
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setLayoutData(fd_scrolledComposite);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void onGnosEventFired(GnosEvent e) {
		// TODO Auto-generated method stub
		
	}
}
