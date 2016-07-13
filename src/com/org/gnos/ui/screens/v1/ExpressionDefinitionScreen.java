package com.org.gnos.ui.screens.v1;

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
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.events.GnosEvent;
import com.org.gnos.services.ExpressionProcessor;
import com.org.gnos.ui.custom.controls.ExpressionBuilderGrid;
import com.org.gnos.ui.custom.controls.GnosScreen;

public class ExpressionDefinitionScreen extends GnosScreen {
	
	private ScrolledComposite scGridContainer;
	private ExpressionBuilderGrid expressionBuilderGrid;
	private Label labelScreenDescription;
	private Button btnAddExpression;
	private Button btnComputeExpressionValues;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ExpressionDefinitionScreen(Composite parent, int style) {
		super(parent, style);
		setForeground(SWTResourceManager.getColor(30, 144, 255));
		setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
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
		labelScreenName.setText("Expression Builder");
		
		this.labelScreenDescription = new Label(this, SWT.NONE);
		this.labelScreenDescription.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		this.labelScreenDescription.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_labelScreenDescription = new FormData();
		fd_labelScreenDescription.top = new FormAttachment(labelScreenName, 10, SWT.BOTTOM);
		fd_labelScreenDescription.left = new FormAttachment(0, 10);
		this.labelScreenDescription.setLayoutData(fd_labelScreenDescription);
		this.labelScreenDescription.setText("Define your own expressions to be used. Add filters.");
		
		this.initializeGridContainer();
		this.refreshGrid();
	}
	
	
	private void initializeGridContainer(){
		this.scGridContainer = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		FormData fd_scGridContainer = new FormData(500,500);// temp hack else size of scrolled composite keeps on increasing
		fd_scGridContainer.top = new FormAttachment(this.labelScreenDescription, 10, SWT.BOTTOM);
		fd_scGridContainer.left = new FormAttachment(this.labelScreenDescription, 0, SWT.LEFT);
		fd_scGridContainer.bottom = new FormAttachment(100, -10);
		fd_scGridContainer.right = new FormAttachment(100, -35);
		
		this.scGridContainer.setExpandHorizontal(true);
		this.scGridContainer.setExpandVertical(true);
		this.scGridContainer.setLayoutData(fd_scGridContainer);
		
		Rectangle r = this.scGridContainer.getClientArea();
		this.scGridContainer.setMinSize(this.scGridContainer.computeSize(SWT.DEFAULT, r.height, true));
		
		
		this.btnAddExpression = new Button(this, SWT.NONE);
		this.btnAddExpression.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TO DO implement row add
				expressionBuilderGrid.addRow();
				Rectangle r = expressionBuilderGrid.getClientArea();
				int gridWidth = r.width;
				
				int scrollableHeight = scGridContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).y;
				Point point = new Point(gridWidth, scrollableHeight);
				scGridContainer.setMinSize(point);
			}
		});
		this.btnAddExpression.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
		FormData fd_btnAddRow = new FormData();
		fd_btnAddRow.top = new FormAttachment(this.labelScreenDescription, 10, SWT.BOTTOM);
		fd_btnAddRow.right = new FormAttachment(100, -5);
		this.btnAddExpression.setLayoutData(fd_btnAddRow);
		this.btnAddExpression.setText("+");
		
		this.btnComputeExpressionValues = new Button(this, SWT.NONE);
		this.btnComputeExpressionValues.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TO DO implement row add
				//ProjectConfigutration.getInstance().saveExpressionData();
				(new ExpressionProcessor()).store();
			}
		});
		this.btnComputeExpressionValues.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
		FormData fd_btnComputeExpressionValues = new FormData();
		fd_btnComputeExpressionValues.top = new FormAttachment(this.btnAddExpression, 10, SWT.BOTTOM);
		fd_btnComputeExpressionValues.right = new FormAttachment(100, -5);
		this.btnComputeExpressionValues.setLayoutData(fd_btnComputeExpressionValues);
		this.btnComputeExpressionValues.setText("C");
		
		
	}
	
	public void refreshGrid(){
		
		if(this.expressionBuilderGrid != null){
			this.expressionBuilderGrid.dispose();
		}
		this.expressionBuilderGrid = new ExpressionBuilderGrid(scGridContainer, SWT.None);
		this.scGridContainer.setContent(this.expressionBuilderGrid);
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
