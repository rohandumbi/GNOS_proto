package com.org.gnos.ui.screens.v1;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.core.Expression;
import com.org.gnos.core.Field;
import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.events.GnosEvent;
import com.org.gnos.services.ExpressionProcessor;
import com.org.gnos.services.csv.GNOSCSVDataProcessor;
import com.org.gnos.ui.custom.controls.ExpressionBuilderGrid;
import com.org.gnos.ui.custom.controls.GnosScreen;

public class ExpressionDefinitionScreen extends GnosScreen {

	private ExpressionBuilderGrid expressionBuilderGrid;
	private List<Field> fields;
	private List<Expression> allDefinedExpressions;
	private List<Expression> expressions;
	private Composite parent;
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
		this.parent = parent;
		this.fields = ProjectConfigutration.getInstance().getFields();
		this.expressions = ProjectConfigutration.getInstance().getExpressions();
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
		
		Label labelScreenDescription = new Label(this, SWT.NONE);
		labelScreenDescription.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		labelScreenDescription.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_labelScreenDescription = new FormData();
		fd_labelScreenDescription.top = new FormAttachment(labelScreenName, 10, SWT.BOTTOM);
		fd_labelScreenDescription.left = new FormAttachment(0, 10);
		//fd_labelScreenDescription.right = new FormAttachment(0, 866);
		labelScreenDescription.setLayoutData(fd_labelScreenDescription);
		labelScreenDescription.setText("Define your own expressions to be used. Add filters.");
		
		expressionBuilderGrid = new ExpressionBuilderGrid(this, SWT.NONE, this.fields, this.expressions);
		FormData fd_expressionBuilderGrid = new FormData();
		fd_expressionBuilderGrid.top = new FormAttachment(labelScreenDescription, 6);
		fd_expressionBuilderGrid.left = new FormAttachment(0, 10);
		fd_expressionBuilderGrid.right = new FormAttachment(100, -10);
		expressionBuilderGrid.setLayoutData(fd_expressionBuilderGrid);
		expressionBuilderGrid.addRow(); // Add a row by default
		final Composite self = this;
		expressionBuilderGrid.addControlListener(new ControlAdapter() {
		    public void controlResized(ControlEvent e) {
		        //System.out.println("Expression builder grid resized");
		    	Composite parent = self.getParent();
		    	if((parent.getParent() !=null) && parent.getParent().getParent() instanceof WorkbenchScreen){//hack for the time being
		    		 WorkbenchScreen workbenchScreen = (WorkbenchScreen)parent.getParent().getParent();
					 workbenchScreen.setScrolledCompositeMinSize();
		    	}
		    }
		});
		
		Button btnAddNewRow = new Button(this, SWT.NONE);
		btnAddNewRow.setText("ADD");
		final Composite me = this;
		btnAddNewRow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				expressionBuilderGrid.addRow();
				me.layout();
				parent.layout(true, true);
			}
		});
		FormData fd_btnAddNewRow = new FormData();
		btnAddNewRow.setLayoutData(fd_btnAddNewRow);
		
		fd_btnAddNewRow.top = new FormAttachment(expressionBuilderGrid, 10, SWT.BOTTOM);
		fd_btnAddNewRow.left = new FormAttachment(50, -145);
		fd_btnAddNewRow.right = new FormAttachment(50);
		
		
		Button buttonExpressionDefinition = new Button(this, SWT.NONE);
		buttonExpressionDefinition.setText("NEXT");
		FormData fd_buttonExpressionDefinition = new FormData();
		fd_buttonExpressionDefinition.top = new FormAttachment(expressionBuilderGrid, 10, SWT.BOTTOM);
		fd_buttonExpressionDefinition.right = new FormAttachment(btnAddNewRow, -5, SWT.LEFT);
		fd_buttonExpressionDefinition.left = new FormAttachment(btnAddNewRow, -145, SWT.LEFT);
		//fd_buttonMapRqrdFields.right = new FormAttachment(0, 282);
		buttonExpressionDefinition.setLayoutData(fd_buttonExpressionDefinition);
		buttonExpressionDefinition.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean isUpdateExpressionSuccessful = updateExpressionList();
				if(isUpdateExpressionSuccessful == true){
					GnosEvent event = new GnosEvent(this, "complete:expression-defintion");
					triggerGnosEvent(event);
				}else{
					
				}
				
			}
		});
		
		/*
		 * Temporary Save button
		 */
		Button buttonSave = new Button(this, SWT.NONE);
		buttonSave.setText("SAVE");
		FormData fd_buttonSave = new FormData();
		fd_buttonSave.top = new FormAttachment(expressionBuilderGrid, 10, SWT.BOTTOM);
		fd_buttonSave.left = new FormAttachment(btnAddNewRow, 5, SWT.RIGHT);
		fd_buttonSave.right = new FormAttachment(btnAddNewRow, 145, SWT.RIGHT);
		//fd_buttonMapRqrdFields.right = new FormAttachment(0, 282);
		buttonSave.setLayoutData(fd_buttonSave);

		/*
		 * Temporary Export to CSV button
		 */
		Button buttonExportToCSV = new Button(this, SWT.NONE);
		buttonExportToCSV.setText("Compute");
		FormData fd_buttonExportToCSV = new FormData();
		fd_buttonExportToCSV.top = new FormAttachment(expressionBuilderGrid, 10, SWT.BOTTOM);
		fd_buttonExportToCSV.left = new FormAttachment(buttonSave, 5, SWT.RIGHT);
		fd_buttonExportToCSV.right = new FormAttachment(buttonSave, 145, SWT.RIGHT);
		//fd_buttonMapRqrdFields.right = new FormAttachment(0, 282);
		buttonExportToCSV.setLayoutData(fd_buttonExportToCSV);
		
		buttonSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO mapping complete
				//projectModel.setAllProjectFields(fieldDatatypeDefinitionGrid.getFieldDatatypes());
				boolean isUpdateExpressionSuccessful = updateExpressionList();
				if(isUpdateExpressionSuccessful){
					resetExpressionList();
				}				
			}
		});
		
		buttonExportToCSV.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean isUpdateExpressionSuccessful = updateExpressionList();
				if(isUpdateExpressionSuccessful){
/*					GNOSCSVDataProcessor.getInstance().compute();
					GNOSCSVDataProcessor.getInstance().dumpToDB();
					GNOSCSVDataProcessor.getInstance().dumpToCsv();
					List<Composite> allExpressions = expressionBuilderGrid.getAllRowsComposite();
					me.layout();
					parent.layout(true, true);
					resetExpressionList();*/
					(new ExpressionProcessor()).store();
				}
			}
		});
	}
	
	private boolean updateExpressionList(){
		//Expressions expressions = new Expressions();
		List<Expression> expressions = expressionBuilderGrid.getDefinedExpressions();
		if(expressions == null){
			return false;
		}
		/*for(Expression expression: this.allDefinedExpressions){
			//Expressions expressions = new Expressions();
			Expressions.add(expression);
		}*/
		ProjectConfigutration.getInstance().setExpressions(expressions);
		ProjectConfigutration.getInstance().saveExpressionData();
		return true;
	}
	
	public void resetExpressionList(){
		expressionBuilderGrid.resetAllRows();
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
