package com.org.gnos.custom.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

public class MapRequiredFieldsGrid extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public MapRequiredFieldsGrid(Composite parent, int style) {
		super(parent, style);
		this.createContent(parent);
	}
	
	private void createContent(Composite parent){
		this.setLayout(new FormLayout());
		Composite compositeGridHeader = new Composite(this, SWT.BORDER);
		//compositeGridHeader.setBounds(0, 0, 749, 37);
		compositeGridHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));
		compositeGridHeader.setLayout(new FormLayout());
		FormData fd_compositeGridHeader = new FormData();
		fd_compositeGridHeader.bottom = new FormAttachment(0, 31);
		fd_compositeGridHeader.top = new FormAttachment(0);
		fd_compositeGridHeader.left = new FormAttachment(0);
		fd_compositeGridHeader.right = new FormAttachment(100);
		compositeGridHeader.setLayoutData(fd_compositeGridHeader);
		
		Label label = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_label = new FormData();
		fd_label.left = new FormAttachment(40);
		label.setLayoutData(fd_label);
		
		Label label_1 = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_label_1 = new FormData();
		fd_label_1.left = new FormAttachment(70);
		label_1.setLayoutData(fd_label_1);
		
		Label lblRqrdFieldHeader = new Label(compositeGridHeader, SWT.NONE);
		lblRqrdFieldHeader.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		FormData fd_lblRqrdFieldHeader = new FormData();
		fd_lblRqrdFieldHeader.top = new FormAttachment(0,2);
		fd_lblRqrdFieldHeader.left = new FormAttachment(0, 10);
		lblRqrdFieldHeader.setLayoutData(fd_lblRqrdFieldHeader);
		lblRqrdFieldHeader.setText("REQUIRED FIELD");
		lblRqrdFieldHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		Label lblSourceFieldHeader = new Label(compositeGridHeader, SWT.NONE);
		lblSourceFieldHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));
		lblSourceFieldHeader.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		FormData fd_lblSourceFieldHeader = new FormData();
		fd_lblSourceFieldHeader.top = new FormAttachment(0, 2);
		fd_lblSourceFieldHeader.left = new FormAttachment(label, 10);
		lblSourceFieldHeader.setLayoutData(fd_lblSourceFieldHeader);
		lblSourceFieldHeader.setText("SOURCE FIELD");
		
		Label lblDatatypeHeader = new Label(compositeGridHeader, SWT.NONE);
		lblDatatypeHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));
		lblDatatypeHeader.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		FormData fd_lblDatatypeHeader = new FormData();
		fd_lblDatatypeHeader.top = new FormAttachment(0,2);
		fd_lblDatatypeHeader.left = new FormAttachment(label_1, 10);
		lblDatatypeHeader.setLayoutData(fd_lblDatatypeHeader);
		lblDatatypeHeader.setText("DATATYPE");
		
		/*
		 * Row 1
		 */
		Composite compositeRow1 = new Composite(this, SWT.BORDER);
		compositeRow1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		compositeRow1.setLayout(new FormLayout());
		FormData fd_compositeRow1 = new FormData();
		fd_compositeRow1.bottom = new FormAttachment(compositeGridHeader, 25, SWT.BOTTOM);
		fd_compositeRow1.top = new FormAttachment(compositeGridHeader);
		fd_compositeRow1.right = new FormAttachment(compositeGridHeader, 0, SWT.RIGHT);
		fd_compositeRow1.left = new FormAttachment(compositeGridHeader, 0, SWT.LEFT);
		
		Label lblRqrdFieldNameRow1 = new Label(compositeRow1, SWT.NONE);
		lblRqrdFieldNameRow1.setForeground(SWTResourceManager.getColor(0,191,255));
		lblRqrdFieldNameRow1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblRqrdFieldNameRow1.setFont(SWTResourceManager.getFont("Arial", 9, SWT.BOLD));
		FormData fd_lblRqrdFieldNameRow1 = new FormData();
		fd_lblRqrdFieldNameRow1.top = new FormAttachment(0);
		fd_lblRqrdFieldNameRow1.left = new FormAttachment(0, 10);
		lblRqrdFieldNameRow1.setLayoutData(fd_lblRqrdFieldNameRow1);
		lblRqrdFieldNameRow1.setText("block");
		
		Combo comboSourceFieldRow1 = new Combo(compositeRow1, SWT.NONE);
		FormData fd_comboSourceFieldRow1 = new FormData();
		fd_comboSourceFieldRow1.left = new FormAttachment(40, 12);
		comboSourceFieldRow1.setLayoutData(fd_comboSourceFieldRow1);
		
		Combo comboDatatypeRow1 = new Combo(compositeRow1, SWT.NONE);
		FormData fd_comboDatatypeRow1 = new FormData();
		fd_comboDatatypeRow1.left = new FormAttachment(70, 12);
		comboDatatypeRow1.setLayoutData(fd_comboDatatypeRow1);
		
		compositeRow1.setLayoutData(fd_compositeRow1);
		
		
		/*
		 * Row 2
		 */
		Composite compositeRow2 = new Composite(this, SWT.BORDER);
		compositeRow2.setBackground(SWTResourceManager.getColor(245, 245, 245));
		compositeRow2.setLayout(new FormLayout());
		FormData fd_compositeRow2 = new FormData();
		fd_compositeRow2.right = new FormAttachment(compositeGridHeader, 0, SWT.RIGHT);
		fd_compositeRow2.bottom = new FormAttachment(compositeRow1, 26, SWT.BOTTOM);
		fd_compositeRow2.top = new FormAttachment(compositeRow1, 1);
		fd_compositeRow2.left = new FormAttachment(compositeGridHeader, 0, SWT.LEFT);
		
		Label lblRqrdFieldNameRow2 = new Label(compositeRow2, SWT.NONE);
		lblRqrdFieldNameRow2.setForeground(SWTResourceManager.getColor(0,191,255));
		lblRqrdFieldNameRow2.setBackground(SWTResourceManager.getColor(245, 245, 245));
		lblRqrdFieldNameRow2.setFont(SWTResourceManager.getFont("Arial", 9, SWT.BOLD));
		FormData fd_lblRqrdFieldNameRow2 = new FormData();
		fd_lblRqrdFieldNameRow2.top = new FormAttachment(0);
		fd_lblRqrdFieldNameRow2.left = new FormAttachment(0, 10);
		lblRqrdFieldNameRow2.setLayoutData(fd_lblRqrdFieldNameRow2);
		lblRqrdFieldNameRow2.setText("bin");
		
		Combo comboSourceFieldRow2 = new Combo(compositeRow2, SWT.NONE);
		FormData fd_comboSourceFieldRow2 = new FormData();
		fd_comboSourceFieldRow2.left = new FormAttachment(40, 12);
		comboSourceFieldRow2.setLayoutData(fd_comboSourceFieldRow2);
		
		Combo comboDatatypeRow2 = new Combo(compositeRow2, SWT.NONE);
		FormData fd_comboDatatypeRow2 = new FormData();
		fd_comboDatatypeRow2.left = new FormAttachment(70, 12);
		comboDatatypeRow2.setLayoutData(fd_comboDatatypeRow2);
		
		compositeRow2.setLayoutData(fd_compositeRow2);
		
		
		/*
		 * Row 3
		 */
		Composite compositeRow3 = new Composite(this, SWT.BORDER);
		compositeRow3.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		compositeRow3.setLayout(new FormLayout());
		FormData fd_compositeRow3 = new FormData();
		fd_compositeRow3.bottom = new FormAttachment(compositeRow2, 26, SWT.BOTTOM);
		fd_compositeRow3.top = new FormAttachment(compositeRow2, 1, SWT.BOTTOM);
		fd_compositeRow3.right = new FormAttachment(compositeGridHeader, 0, SWT.RIGHT);
		fd_compositeRow3.left = new FormAttachment(compositeGridHeader, 0, SWT.LEFT);
		
		Label lblRqrdFieldNameRow3 = new Label(compositeRow3, SWT.NONE);
		lblRqrdFieldNameRow3.setForeground(SWTResourceManager.getColor(0,191,255));
		lblRqrdFieldNameRow3.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblRqrdFieldNameRow3.setFont(SWTResourceManager.getFont("Arial", 9, SWT.BOLD));
		FormData fd_lblRqrdFieldNameRow3 = new FormData();
		fd_lblRqrdFieldNameRow3.top = new FormAttachment(0);
		fd_lblRqrdFieldNameRow3.left = new FormAttachment(0, 10);
		lblRqrdFieldNameRow3.setLayoutData(fd_lblRqrdFieldNameRow3);
		lblRqrdFieldNameRow3.setText("mining_area");
		
		Combo comboSourceFieldRow3 = new Combo(compositeRow3, SWT.NONE);
		FormData fd_comboSourceFieldRow3 = new FormData();
		fd_comboSourceFieldRow3.left = new FormAttachment(40, 12);
		comboSourceFieldRow3.setLayoutData(fd_comboSourceFieldRow3);
		
		Combo comboDatatypeRow3 = new Combo(compositeRow3, SWT.NONE);
		FormData fd_comboDatatypeRow3 = new FormData();
		fd_comboDatatypeRow3.left = new FormAttachment(70, 12);
		comboDatatypeRow3.setLayoutData(fd_comboDatatypeRow3);
		
		compositeRow3.setLayoutData(fd_compositeRow3);
		
		/*
		 * Row 4
		 */
		Composite compositeRow4 = new Composite(this, SWT.BORDER);
		compositeRow4.setBackground(SWTResourceManager.getColor(245, 245, 245));
		compositeRow4.setLayout(new FormLayout());
		FormData fd_compositeRow4 = new FormData();
		fd_compositeRow4.right = new FormAttachment(compositeGridHeader, 0, SWT.RIGHT);
		fd_compositeRow4.bottom = new FormAttachment(compositeRow3, 26, SWT.BOTTOM);
		fd_compositeRow4.top = new FormAttachment(compositeRow3, 1);
		fd_compositeRow4.left = new FormAttachment(compositeGridHeader, 0, SWT.LEFT);
		
		Label lblRqrdFieldNameRow4 = new Label(compositeRow4, SWT.NONE);
		lblRqrdFieldNameRow4.setForeground(SWTResourceManager.getColor(0,191,255));
		lblRqrdFieldNameRow4.setBackground(SWTResourceManager.getColor(245,245,245));
		lblRqrdFieldNameRow4.setFont(SWTResourceManager.getFont("Arial", 9, SWT.BOLD));
		FormData fd_lblRqrdFieldNameRow4 = new FormData();
		fd_lblRqrdFieldNameRow4.top = new FormAttachment(0);
		fd_lblRqrdFieldNameRow4.left = new FormAttachment(0, 10);
		lblRqrdFieldNameRow4.setLayoutData(fd_lblRqrdFieldNameRow4);
		lblRqrdFieldNameRow4.setText("pit_name");
		

		Combo comboSourceFieldRow4 = new Combo(compositeRow4, SWT.NONE);
		FormData fd_comboSourceFieldRow4 = new FormData();
		fd_comboSourceFieldRow4.left = new FormAttachment(40, 12);
		comboSourceFieldRow4.setLayoutData(fd_comboSourceFieldRow4);
		
		Combo comboDatatypeRow4 = new Combo(compositeRow4, SWT.NONE);
		FormData fd_comboDatatypeRow4 = new FormData();
		fd_comboDatatypeRow4.left = new FormAttachment(70, 12);
		comboDatatypeRow4.setLayoutData(fd_comboDatatypeRow4);
		
		compositeRow4.setLayoutData(fd_compositeRow4);
		
		/*
		 * Row 5
		 */
		Composite compositeRow5 = new Composite(this, SWT.BORDER);
		compositeRow5.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		compositeRow5.setLayout(new FormLayout());
		FormData fd_compositeRow5 = new FormData();
		fd_compositeRow5.bottom = new FormAttachment(compositeRow4, 26, SWT.BOTTOM);
		fd_compositeRow5.top = new FormAttachment(compositeRow4, 1, SWT.BOTTOM);
		fd_compositeRow5.right = new FormAttachment(compositeGridHeader, 0, SWT.RIGHT);
		fd_compositeRow5.left = new FormAttachment(compositeGridHeader, 0, SWT.LEFT);
		
		Label lblRqrdFieldNameRow5 = new Label(compositeRow5, SWT.NONE);
		lblRqrdFieldNameRow5.setForeground(SWTResourceManager.getColor(0,191,255));
		lblRqrdFieldNameRow5.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblRqrdFieldNameRow5.setFont(SWTResourceManager.getFont("Arial", 9, SWT.BOLD));
		FormData fd_lblRqrdFieldNameRow5 = new FormData();
		fd_lblRqrdFieldNameRow5.top = new FormAttachment(0);
		fd_lblRqrdFieldNameRow5.left = new FormAttachment(0, 10);
		lblRqrdFieldNameRow5.setLayoutData(fd_lblRqrdFieldNameRow5);
		lblRqrdFieldNameRow5.setText("bench_rl");
		

		Combo comboSourceFieldRow5 = new Combo(compositeRow5, SWT.NONE);
		FormData fd_comboSourceFieldRow5 = new FormData();
		fd_comboSourceFieldRow5.left = new FormAttachment(40, 12);
		comboSourceFieldRow5.setLayoutData(fd_comboSourceFieldRow5);
		
		Combo comboDatatypeRow5 = new Combo(compositeRow5, SWT.NONE);
		FormData fd_comboDatatypeRow5 = new FormData();
		fd_comboDatatypeRow5.left = new FormAttachment(70, 12);
		comboDatatypeRow5.setLayoutData(fd_comboDatatypeRow5);
		
		compositeRow5.setLayoutData(fd_compositeRow5);
		
		/*
		 * Row 6
		 */
		Composite compositeRow6 = new Composite(this, SWT.BORDER);
		compositeRow6.setBackground(SWTResourceManager.getColor(245, 245, 245));
		compositeRow6.setLayout(new FormLayout());
		FormData fd_compositeRow6 = new FormData();
		fd_compositeRow6.right = new FormAttachment(compositeGridHeader, 0, SWT.RIGHT);
		fd_compositeRow6.bottom = new FormAttachment(compositeRow5, 26, SWT.BOTTOM);
		fd_compositeRow6.top = new FormAttachment(compositeRow5, 1);
		fd_compositeRow6.left = new FormAttachment(compositeGridHeader, 0, SWT.LEFT);
		
		Label lblRqrdFieldNameRow6 = new Label(compositeRow6, SWT.NONE);
		lblRqrdFieldNameRow6.setForeground(SWTResourceManager.getColor(0,191,255));
		lblRqrdFieldNameRow6.setBackground(SWTResourceManager.getColor(245,245,245));
		lblRqrdFieldNameRow6.setFont(SWTResourceManager.getFont("Arial", 9, SWT.BOLD));
		FormData fd_lblRqrdFieldNameRow6 = new FormData();
		fd_lblRqrdFieldNameRow6.top = new FormAttachment(0);
		fd_lblRqrdFieldNameRow6.left = new FormAttachment(0, 10);
		lblRqrdFieldNameRow6.setLayoutData(fd_lblRqrdFieldNameRow6);
		lblRqrdFieldNameRow6.setText("tonnes_wt");
		

		Combo comboSourceFieldRow6 = new Combo(compositeRow6, SWT.NONE);
		FormData fd_comboSourceFieldRow6 = new FormData();
		fd_comboSourceFieldRow6.left = new FormAttachment(40, 12);
		comboSourceFieldRow6.setLayoutData(fd_comboSourceFieldRow6);
		
		Combo comboDatatypeRow6 = new Combo(compositeRow6, SWT.NONE);
		FormData fd_comboDatatypeRow6 = new FormData();
		fd_comboDatatypeRow6.left = new FormAttachment(70, 12);
		comboDatatypeRow6.setLayoutData(fd_comboDatatypeRow6);
		
		compositeRow6.setLayoutData(fd_compositeRow6);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
