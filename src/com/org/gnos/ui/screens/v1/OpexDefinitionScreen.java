package com.org.gnos.ui.screens.v1;


import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.db.model.OpexData;
import com.org.gnos.events.GnosEvent;
import com.org.gnos.services.TimePeriod;
import com.org.gnos.ui.custom.controls.GnosScreen;
import com.org.gnos.ui.custom.controls.OpexDefinitionGrid;

public class OpexDefinitionScreen extends GnosScreen {

	private Text textStartYear;
	private Text textNumberOfIncrements;
	private ScrolledComposite scGridContainer;
	private OpexDefinitionGrid opexDefinitionGrid;
	private Label labelScreenName;
	private List<OpexData> opexDataList;
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
		this.createContent();

	}
	
	private void createContent(){
		setLayout(new FormLayout());
		labelScreenName = new Label(this, SWT.NONE);
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
		fd_textStartYear.right = new FormAttachment(lblStartYear, 58, SWT.RIGHT);
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
		fd_textNumberOfIncrements.right = new FormAttachment(lblNumberOfIncrements, 58, SWT.RIGHT);
		textNumberOfIncrements.setLayoutData(fd_textNumberOfIncrements);
		
		Button btnAddTimePeriod = new Button(this, SWT.NONE);
		btnAddTimePeriod.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int startingYear = Integer.parseInt(textStartYear.getText());
				int numberOfIncrements = Integer.parseInt(textNumberOfIncrements.getText());
				TimePeriod timePeriod = new TimePeriod(startingYear, numberOfIncrements);
				initializeOpexGrid(timePeriod);
			}
		});
		FormData fd_btnAddTimePeriod = new FormData();
		fd_btnAddTimePeriod.top = new FormAttachment(textNumberOfIncrements, 0, SWT.TOP);
		fd_btnAddTimePeriod.left = new FormAttachment(textNumberOfIncrements, 6);
		btnAddTimePeriod.setLayoutData(fd_btnAddTimePeriod);
		fd_btnAddTimePeriod.bottom = new FormAttachment(textNumberOfIncrements, 0, SWT.BOTTOM);
		btnAddTimePeriod.setText("Save");
		
		/*
		 * If there is an existing Opex data for the project
		 */
		this.opexDataList = ProjectConfigutration.getInstance().getOpexDataList();
		if(this.opexDataList.size() > 0){
			OpexData opexData = this.opexDataList.get(0);
			Map<Integer, Integer> mapCostData = opexData.getCostData();
			int numberOfIncrements = mapCostData.size();
			int startYear = 0;
			for(Integer key : mapCostData.keySet()){
				startYear = key;
				break;
			}
			textNumberOfIncrements.setText(String.valueOf(numberOfIncrements));
			textStartYear.setText(String.valueOf(startYear));
			TimePeriod savedTimePeriod = new TimePeriod(startYear, numberOfIncrements);
			initializeOpexGrid(savedTimePeriod);
		}
		
	}
	
	private void initializeOpexGrid(TimePeriod timePeriod){
		if(scGridContainer != null){
			scGridContainer.dispose();
		}
		scGridContainer = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		FormData fd_scGridContainer = new FormData(500,500);// temp hack else size of scrolled composite keeps on increasing
		fd_scGridContainer.top = new FormAttachment(textStartYear, 10, SWT.BOTTOM);
		fd_scGridContainer.left = new FormAttachment(labelScreenName, 0, SWT.LEFT);
		fd_scGridContainer.bottom = new FormAttachment(100, -10);
		fd_scGridContainer.right = new FormAttachment(100, -35);
		
		opexDefinitionGrid = new OpexDefinitionGrid(scGridContainer, SWT.None, timePeriod);
		scGridContainer.setContent(opexDefinitionGrid);
		
		scGridContainer.setExpandHorizontal(true);
		scGridContainer.setExpandVertical(true);
		scGridContainer.setLayoutData(fd_scGridContainer);
		
		Rectangle r = scGridContainer.getClientArea();
		scGridContainer.setMinSize(scGridContainer.computeSize(SWT.DEFAULT, r.height, true));
		
		
		Button btnAddRow = new Button(this, SWT.NONE);
		btnAddRow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TO DO implement row add
				opexDefinitionGrid.addRow();
				Rectangle r = opexDefinitionGrid.getClientArea();
				int gridWidth = r.width;
				
				int scrollableHeight = scGridContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).y;
				Point point = new Point(gridWidth, scrollableHeight);
				scGridContainer.setMinSize(point);
			}
		});
		btnAddRow.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
		FormData fd_btnAddRow = new FormData();
		fd_btnAddRow.top = new FormAttachment(textStartYear, 10, SWT.BOTTOM);
		fd_btnAddRow.right = new FormAttachment(100, -5);
		btnAddRow.setLayoutData(fd_btnAddRow);
		btnAddRow.setText("+");
		
		Button btnSaveOpexData = new Button(this, SWT.NONE);
		btnSaveOpexData.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TO DO implement row add
				opexDefinitionGrid.saveOpexData();
			}
		});
		btnSaveOpexData.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
		btnSaveOpexData.setImage(SWTResourceManager.getImage(OpexDefinitionScreen.class, "/com/org/gnos/resources/save.png"));
		FormData fd_btnSaveOpexData = new FormData();
		fd_btnSaveOpexData.top = new FormAttachment(btnAddRow, 5, SWT.BOTTOM);
		fd_btnSaveOpexData.right = new FormAttachment(100, -5);
		btnSaveOpexData.setLayoutData(fd_btnSaveOpexData);
		
		this.layout();
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
