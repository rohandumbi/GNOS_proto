package com.org.gnos.custom.controls;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

public class GnosExpressionComposite extends Composite {

	private Composite parent;
	private Text textConditionValue;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public GnosExpressionComposite(Composite parent, int style) {
		super(parent, style);
		this.parent = parent;
		setLayout(new FillLayout(SWT.VERTICAL));
		
		Composite compositeExpressionRow1 = new Composite(this, SWT.NONE);
		compositeExpressionRow1.setLayout(new FormLayout());
		
		Combo comboConditionType = new Combo(compositeExpressionRow1, SWT.NONE);
		FormData fd_comboConditionType = new FormData();
		fd_comboConditionType.right = new FormAttachment(0, 62);
		fd_comboConditionType.left = new FormAttachment(0);
		comboConditionType.setLayoutData(fd_comboConditionType);
		
		Combo comboField = new Combo(compositeExpressionRow1, SWT.NONE);
		FormData fd_comboField = new FormData();
		fd_comboField.left = new FormAttachment(comboConditionType);
		fd_comboField.right = new FormAttachment(40);
		comboField.setLayoutData(fd_comboField);
		
		Combo comboOperator = new Combo(compositeExpressionRow1, SWT.NONE);
		FormData fd_comboOperator = new FormData();
		fd_comboOperator.left = new FormAttachment(comboField);
		fd_comboOperator.right = new FormAttachment(60);
		comboOperator.setLayoutData(fd_comboOperator);
		
		Text textConditionValue = new Text(compositeExpressionRow1, SWT.BORDER);
		FormData fd_textConditionValue = new FormData();
		fd_textConditionValue.top = new FormAttachment(comboConditionType, 0, SWT.TOP);
		fd_textConditionValue.left = new FormAttachment(comboOperator);
		fd_textConditionValue.right = new FormAttachment(100);
		textConditionValue.setLayoutData(fd_textConditionValue);
		
		Composite compositeExpressionRow2 = new Composite(this, SWT.NONE);
		compositeExpressionRow2.setLayout(new FormLayout());
		
		Combo comboConditionType2 = new Combo(compositeExpressionRow2, SWT.NONE);
		FormData fd_comboConditionType2 = new FormData();
		fd_comboConditionType2.right = new FormAttachment(0, 62);
		fd_comboConditionType2.left = new FormAttachment(0);
		comboConditionType2.setLayoutData(fd_comboConditionType2);
		
		Combo comboField2 = new Combo(compositeExpressionRow2, SWT.NONE);
		FormData fd_comboField2 = new FormData();
		fd_comboField2.left = new FormAttachment(comboConditionType2);
		fd_comboField2.right = new FormAttachment(40);
		comboField2.setLayoutData(fd_comboField2);
		
		Combo comboOperator2 = new Combo(compositeExpressionRow2, SWT.NONE);
		FormData fd_comboOperator2 = new FormData();
		fd_comboOperator2.left = new FormAttachment(comboField2);
		fd_comboOperator2.right = new FormAttachment(60);
		comboOperator2.setLayoutData(fd_comboOperator2);
		
		Text textConditionValue2 = new Text(compositeExpressionRow2, SWT.BORDER);
		FormData fd_textConditionValue2 = new FormData();
		fd_textConditionValue2.top = new FormAttachment(comboConditionType, 0, SWT.TOP);
		fd_textConditionValue2.left = new FormAttachment(comboOperator2);
		fd_textConditionValue2.right = new FormAttachment(100);
		textConditionValue2.setLayoutData(fd_textConditionValue2);
		
		
		
		
		
		
		Button buttonAddCondition = new Button(this, SWT.NONE);
		buttonAddCondition.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO add handler
			}
		});
		buttonAddCondition.setText("Add Condition");
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
