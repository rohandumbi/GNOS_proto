package com.org.gnos.ui.custom.controls;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Model;

public class ModelDefinitionGrid extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private Composite compositeGridHeader;
	private List<Composite> allRows;
	private String[] sourceFieldsComboItems;
	private Composite presentRow;
	private List<Model> existingModels;
	private Composite parent;
	private List<String> presentmodelNames;

	public ModelDefinitionGrid(Composite parent, int style) {
		super(parent, style);
		this.parent = parent;
		this.allRows = new ArrayList<Composite>();
		this.existingModels = ProjectConfigutration.getInstance().getModels();
		this.createContent(parent);
	}
	
	private void createContent(Composite parent){
		this.setLayout(new FormLayout());
		this.createHeader();
		this.createRows();
	}
	
	private boolean isModelNameDuplicate(String modelName){
		boolean isPresentInModelGrid = false;
		for(String str: presentmodelNames) {
		    if(str.trim().equalsIgnoreCase(modelName.trim()))
		    	isPresentInModelGrid = true;
		}
		return isPresentInModelGrid;
	}

	
	private String[] getExpressionComboItems(){
		
		List<Expression> expressions = ProjectConfigutration.getInstance().getExpressions();
		this.sourceFieldsComboItems = new String[expressions.size()];
		for(int i=0; i<expressions.size(); i++){
			this.sourceFieldsComboItems[i] = expressions.get(i).getName();
		}
				
		return this.sourceFieldsComboItems;
	}


	private void createHeader(){
		compositeGridHeader = new Composite(this, SWT.BORDER);
		compositeGridHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));
		compositeGridHeader.setLayout(new FormLayout());
		FormData fd_compositeGridHeader = new FormData();
		fd_compositeGridHeader.bottom = new FormAttachment(0, 31);
		fd_compositeGridHeader.top = new FormAttachment(0);
		fd_compositeGridHeader.left = new FormAttachment(0);
		fd_compositeGridHeader.right = new FormAttachment(100);
		compositeGridHeader.setLayoutData(fd_compositeGridHeader);

		Label firstSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_firstSeparator = new FormData();
		fd_firstSeparator.left = new FormAttachment(33);
		firstSeparator.setLayoutData(fd_firstSeparator);

		Label secondSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_secondSeparator = new FormData();
		fd_secondSeparator.left = new FormAttachment(66);
		secondSeparator.setLayoutData(fd_secondSeparator);


		Label lblGradeHeader = new Label(compositeGridHeader, SWT.NONE);
		lblGradeHeader.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		FormData fd_lblGradeHeader = new FormData();
		fd_lblGradeHeader.top = new FormAttachment(0,2);
		fd_lblGradeHeader.left = new FormAttachment(0, 10);
		lblGradeHeader.setLayoutData(fd_lblGradeHeader);
		lblGradeHeader.setText("IDENTIFIER/NAME");
		lblGradeHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));

		Label lblExpressionNameHeader = new Label(compositeGridHeader, SWT.NONE);
		lblExpressionNameHeader.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		FormData fd_lblExpressionNameHeader = new FormData();
		fd_lblExpressionNameHeader.top = new FormAttachment(0,2);
		fd_lblExpressionNameHeader.left = new FormAttachment(firstSeparator, 10);
		lblExpressionNameHeader.setLayoutData(fd_lblExpressionNameHeader);
		lblExpressionNameHeader.setText("FIELD");
		lblExpressionNameHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));

		Label lblExpressionTypeHeader = new Label(compositeGridHeader, SWT.NONE);
		lblExpressionTypeHeader.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		FormData fd_lblExpressionTypeHeader = new FormData();
		fd_lblExpressionTypeHeader.top = new FormAttachment(0,2);
		fd_lblExpressionTypeHeader.left = new FormAttachment(secondSeparator, 10);
		lblExpressionTypeHeader.setLayoutData(fd_lblExpressionTypeHeader);
		lblExpressionTypeHeader.setText("CONDITION");
		lblExpressionTypeHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));

		this.presentRow = this.compositeGridHeader;//referring to the header as the 1st row when there are no rows inserted yet

	}

	private void createRows() {
		
		for(final Model model: this.existingModels) {
			final Composite compositeRow = new Composite(this, SWT.BORDER);
			compositeRow.setData(model);
			compositeRow.setLayout(new FormLayout());
			Color backgroundColor = SWTResourceManager.getColor(SWT.COLOR_WHITE);
			if((this.allRows != null) && (this.allRows.size()%2 != 0)){
				backgroundColor =  SWTResourceManager.getColor(245, 245, 245);
			}

			compositeRow.setBackground(backgroundColor);
			FormData fd_compositeRow = new FormData();
			fd_compositeRow.left = new FormAttachment(this.presentRow, 0, SWT.LEFT);
			//fd_compositeRow.bottom = new FormAttachment(this.presentRow, 26, SWT.BOTTOM);
			fd_compositeRow.right = new FormAttachment(this.presentRow, 0, SWT.RIGHT);
			fd_compositeRow.top = new FormAttachment(this.presentRow);


			Text modelName = new Text(compositeRow, SWT.BORDER);
			modelName.setText(model.getName());
			modelName.addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent event) {
					// Get the widget whose text was modified
					Text text = (Text) event.widget;
					String modelName = text.getText();
					model.setName(modelName);
				}
			});
			FormData fd_modelName = new FormData();
			fd_modelName.left = new FormAttachment(0, 10);
			fd_modelName.top = new FormAttachment(0);
			fd_modelName.right = new FormAttachment(20, -5);
			modelName.setLayoutData(fd_modelName);
			
			final Combo comboExpressionList = new Combo(compositeRow, SWT.NONE);
			FormData fd_comboExpressionList = new FormData();
			fd_comboExpressionList.right = new FormAttachment(60, -5);
			fd_comboExpressionList.left = new FormAttachment(33, 5);
			comboExpressionList.setLayoutData(fd_comboExpressionList);
			comboExpressionList.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
			comboExpressionList.setItems(getExpressionComboItems());
			comboExpressionList.setText(model.getExpression().getName());
			comboExpressionList.addListener(SWT.MouseDown, new Listener(){
				@Override
				public void handleEvent(Event event) {
					// TODO Auto-generated method stub
					//System.out.println("detected combo click");
					comboExpressionList.removeAll();
					comboExpressionList.setItems(getExpressionComboItems());
					comboExpressionList.getParent().layout();
					comboExpressionList.setListVisible(true);
				}
			});
			comboExpressionList.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					String expressionName = comboExpressionList.getText();
					model.setExpression(ProjectConfigutration.getInstance().getExpressionByName(expressionName));
				}
			});


			Text textCondition = new Text(compositeRow, SWT.BORDER);
			if(model.getCondition() != null) textCondition.setText(model.getCondition());
			textCondition.addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent event) {
					// Get the widget whose text was modified
					Text text = (Text) event.widget;
					String condition = text.getText();
					model.setCondition(condition);
				}
			});
			FormData fd_textCondition = new FormData();
			fd_textCondition.left = new FormAttachment(66, 2);
			fd_textCondition.right = new FormAttachment(100, -2);
			textCondition.setLayoutData(fd_textCondition);

			this.presentRow = compositeRow;
			this.allRows.add(compositeRow);
			compositeRow.setLayoutData(fd_compositeRow);
		}
	}
	
	public void addRow(){
		final Composite compositeRow = new Composite(this, SWT.BORDER);
		final Model newModel = new Model();
		this.existingModels.add(newModel);
		compositeRow.setData(newModel);
		compositeRow.setLayout(new FormLayout());
		Color backgroundColor = SWTResourceManager.getColor(SWT.COLOR_WHITE);
		if((this.allRows != null) && (this.allRows.size()%2 != 0)){
			backgroundColor =  SWTResourceManager.getColor(245, 245, 245);
		}

		compositeRow.setBackground(backgroundColor);
		FormData fd_compositeRow = new FormData();
		fd_compositeRow.left = new FormAttachment(this.presentRow, 0, SWT.LEFT);
		//fd_compositeRow.bottom = new FormAttachment(this.presentRow, 26, SWT.BOTTOM);
		fd_compositeRow.right = new FormAttachment(this.presentRow, 0, SWT.RIGHT);
		fd_compositeRow.top = new FormAttachment(this.presentRow);


		Text modelName = new Text(compositeRow, SWT.BORDER);
		//fd_grade.top = new FormAttachment(expressionName, 2, SWT.TOP);
		modelName.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent event) {
				// Get the widget whose text was modified
				Text text = (Text) event.widget;
				String modelName = text.getText();
				newModel.setName(modelName);
			}
		});
		FormData fd_modelName = new FormData();
		fd_modelName.left = new FormAttachment(0, 10);
		fd_modelName.top = new FormAttachment(0);
		fd_modelName.right = new FormAttachment(20, -5);
		modelName.setLayoutData(fd_modelName);
		
		final Combo comboExpressionList = new Combo(compositeRow, SWT.NONE);
		FormData fd_comboExpressionDefinition = new FormData();
		fd_comboExpressionDefinition.right = new FormAttachment(60, -5);
		fd_comboExpressionDefinition.left = new FormAttachment(33, 5);
		comboExpressionList.setLayoutData(fd_comboExpressionDefinition);
		comboExpressionList.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		comboExpressionList.setItems(getExpressionComboItems());
		comboExpressionList.setText("Field Value");
		comboExpressionList.addListener(SWT.MouseDown, new Listener(){
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				//System.out.println("detected combo click");
				comboExpressionList.removeAll();
				comboExpressionList.setItems(getExpressionComboItems());
				comboExpressionList.getParent().layout();
				comboExpressionList.setListVisible(true);
			}
		});
		comboExpressionList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String expressionName = comboExpressionList.getText();
				newModel.setExpression(ProjectConfigutration.getInstance().getExpressionByName(expressionName));
			}
		});

		Text textCondition = new Text(compositeRow, SWT.BORDER);
		textCondition.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent event) {
				// Get the widget whose text was modified
				Text text = (Text) event.widget;
				String condition = text.getText();
				newModel.setCondition(condition);
			}
		});
		FormData fd_textCondition = new FormData();
		fd_textCondition.left = new FormAttachment(66, 2);
		fd_textCondition.right = new FormAttachment(100, -2);
		textCondition.setLayoutData(fd_textCondition);

		this.presentRow = compositeRow;
		this.allRows.add(compositeRow);
		compositeRow.setLayoutData(fd_compositeRow);
		this.layout();
	}


	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
