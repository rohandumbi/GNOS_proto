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

import com.org.gnos.core.Field;
import com.org.gnos.core.Model;
import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.events.GnosEvent;
import com.org.gnos.services.csv.GNOSCSVDataProcessor;
import com.org.gnos.ui.custom.controls.GnosScreen;
import com.org.gnos.ui.custom.controls.ModelDefinitionGrid;

public class ModelDefinitionScreen extends GnosScreen {

	private ModelDefinitionGrid modelDefinitionGrid;
	private List<Model> allDefinedModels;
	private Composite parent;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ModelDefinitionScreen(Composite parent, int style) {
		super(parent, style);
		setForeground(SWTResourceManager.getColor(30, 144, 255));
		setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
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
		fd_labelScreenName.top = new FormAttachment(0, 20);
		fd_labelScreenName.left = new FormAttachment(0, 10);
		labelScreenName.setLayoutData(fd_labelScreenName);
		labelScreenName.setFont(SWTResourceManager.getFont("Arial", 9, SWT.BOLD));
		labelScreenName.setText("Model Definition");
		
		Label labelScreenDescription = new Label(this, SWT.NONE);
		labelScreenDescription.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		labelScreenDescription.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_labelScreenDescription = new FormData();
		fd_labelScreenDescription.top = new FormAttachment(labelScreenName, 10, SWT.BOTTOM);
		fd_labelScreenDescription.left = new FormAttachment(0, 10);
		//fd_labelScreenDescription.right = new FormAttachment(0, 866);
		labelScreenDescription.setLayoutData(fd_labelScreenDescription);
		labelScreenDescription.setText("Define your own models. Add filters.");
		
		modelDefinitionGrid = new ModelDefinitionGrid(this, SWT.NONE);
		FormData fd_expressionBuilderGrid = new FormData();
		fd_expressionBuilderGrid.top = new FormAttachment(labelScreenDescription, 6);
		fd_expressionBuilderGrid.left = new FormAttachment(0, 10);
		fd_expressionBuilderGrid.right = new FormAttachment(100, -10);
		modelDefinitionGrid.setLayoutData(fd_expressionBuilderGrid);
		
		final Composite self = this;
		modelDefinitionGrid.addControlListener(new ControlAdapter() {
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
		final Composite me = this;
		btnAddNewRow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				modelDefinitionGrid.addRow();
				me.layout();
				parent.layout(true, true);
			}
		});
		FormData fd_btnAddNewRow = new FormData();
		btnAddNewRow.setLayoutData(fd_btnAddNewRow);
		btnAddNewRow.setText("ADD NEW MODEL");
		btnAddNewRow.setSize(145, SWT.DEFAULT);
		//int offsetX = -btnAddNewRow.computeSize(SWT.DEFAULT, SWT.DEFAULT).x / 2;
		fd_btnAddNewRow.top = new FormAttachment(modelDefinitionGrid, 10, SWT.BOTTOM);
		fd_btnAddNewRow.left = new FormAttachment(50, -145);
		fd_btnAddNewRow.right = new FormAttachment(50);
		
		
		Button buttonNext = new Button(this, SWT.NONE);
		buttonNext.setText("NEXT");
		FormData fd_buttonNext = new FormData();
		fd_buttonNext.top = new FormAttachment(modelDefinitionGrid, 10, SWT.BOTTOM);
		fd_buttonNext.right = new FormAttachment(btnAddNewRow, -5, SWT.LEFT);
		fd_buttonNext.left = new FormAttachment(btnAddNewRow, -145, SWT.LEFT);
		//fd_buttonMapRqrdFields.right = new FormAttachment(0, 282);
		buttonNext.setLayoutData(fd_buttonNext);
		buttonNext.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO mapping complete
				//updateHeadersWithRequiredFieldsMapping();
				boolean isUpdateExpressionSuccessful = updateModelList();
				if(isUpdateExpressionSuccessful == true){
					GnosEvent event = new GnosEvent(this, "complete:model-defintion");
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
		fd_buttonSave.top = new FormAttachment(modelDefinitionGrid, 10, SWT.BOTTOM);
		fd_buttonSave.left = new FormAttachment(btnAddNewRow, 5, SWT.RIGHT);
		fd_buttonSave.right = new FormAttachment(btnAddNewRow, 145, SWT.RIGHT);
		//fd_buttonMapRqrdFields.right = new FormAttachment(0, 282);
		buttonSave.setLayoutData(fd_buttonSave);
		
		buttonSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO mapping complete
				boolean isUpdateExpressionSuccessful = updateModelList();
				if(isUpdateExpressionSuccessful){
					//GNOSCSVDataProcessor.getInstance().compute();
					//List<Composite> allModels = modelDefinitionGrid.getAllRowsComposite();
					me.layout();
					parent.layout(true, true);
					resetModelList();
				}				
			}
		});
	}
	
	private boolean updateModelList(){
		//Expressions expressions = new Expressions();
		this.allDefinedModels = modelDefinitionGrid.getDefinedModels();
		if(this.allDefinedModels == null){
			return false;
		}
		ProjectConfigutration.getInstance().setModels(this.allDefinedModels);
		ProjectConfigutration.getInstance().saveModelData();
		return true;
	}
	
	public void resetModelList(){
		modelDefinitionGrid.resetAllRows();
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
