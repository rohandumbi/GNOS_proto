package com.org.gnos.ui.screens.v1;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.custom.controls.ExpressionBuilderGrid;
import com.org.gnos.custom.controls.GnosScreen;
import com.org.gnos.custom.controls.MapRequiredFieldsGrid;
import com.org.gnos.custom.models.ProjectMetaDataModel;
import com.org.gnos.events.GnosEvent;
import com.org.gnos.events.interfaces.GnosEventListener;
import com.org.gnos.services.csv.ColumnHeader;
import com.org.gnos.services.csv.GNOSDataProcessor;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;

public class ExpressionDefinitionScreen extends GnosScreen {

	private ExpressionBuilderGrid expressionBuilderGrid;
	private List<ColumnHeader> allHeaders;
	private GNOSDataProcessor csvProcessor;
	private ProjectMetaDataModel projectMetaData;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ExpressionDefinitionScreen(Composite parent, int style, ProjectMetaDataModel projectMetaData) {
		super(parent, style);
		setForeground(SWTResourceManager.getColor(30, 144, 255));
		setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.projectMetaData = projectMetaData;
		this.allHeaders = this.getAllHeaders();
		this.createContent();
	}
	
	private List<ColumnHeader> getAllHeaders(){
		try {
			csvProcessor = new GNOSDataProcessor(this.projectMetaData.get("recordFileName"));
			csvProcessor.doInBackground();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return csvProcessor.getHeaderColumns();
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
		labelScreenName.setFont(SWTResourceManager.getFont("Arial", 9, SWT.BOLD));
		labelScreenName.setText("Expression Builder");
		
		Label labelScreenDescription = new Label(this, SWT.NONE);
		labelScreenDescription.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		labelScreenDescription.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_labelScreenDescription = new FormData();
		fd_labelScreenDescription.top = new FormAttachment(labelScreenName, 10, SWT.BOTTOM);
		fd_labelScreenDescription.left = new FormAttachment(0, 10);
		fd_labelScreenDescription.right = new FormAttachment(0, 866);
		labelScreenDescription.setLayoutData(fd_labelScreenDescription);
		labelScreenDescription.setText("Define your own expressions to be used. Add filters.");
		
		expressionBuilderGrid = new ExpressionBuilderGrid(this, SWT.NONE, this.allHeaders);
		FormData fd_expressionBuilderGrid = new FormData();
		fd_expressionBuilderGrid.top = new FormAttachment(labelScreenDescription, 6);
		fd_expressionBuilderGrid.left = new FormAttachment(0, 10);
		fd_expressionBuilderGrid.right = new FormAttachment(100, -10);
		expressionBuilderGrid.setLayoutData(fd_expressionBuilderGrid);
		
		Button buttonExpressionDefinition = new Button(this, SWT.NONE);
		buttonExpressionDefinition.setText("NEXT");
		int offsetX = -buttonExpressionDefinition.computeSize(SWT.DEFAULT, SWT.DEFAULT).x / 2;
		FormData fd_buttonExpressionDefinition = new FormData();
		fd_buttonExpressionDefinition.top = new FormAttachment(expressionBuilderGrid, 10, SWT.BOTTOM);
		fd_buttonExpressionDefinition.left = new FormAttachment(50, offsetX);
		//fd_buttonMapRqrdFields.right = new FormAttachment(0, 282);
		buttonExpressionDefinition.setLayoutData(fd_buttonExpressionDefinition);
		buttonExpressionDefinition.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO mapping complete
				//updateHeadersWithRequiredFieldsMapping();
				GnosEvent event = new GnosEvent(this, "complete:expression-defintion");
				triggerGnosEvent(event);
			}
		});
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}


	@Override
	public void onGnosEventFired(GnosEvent e) {
		// TODO Auto-generated method stub
		
	}
	private void triggerGnosEvent(GnosEvent event){
		int j = listeners.size();
		int i = 0;
		for(i=0; i<j; i++){
			listeners.get(i).onGnosEventFired(event);
		}
	}

}
