package com.org.gnos.ui.custom.controls;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Grade;
import com.org.gnos.db.model.Product;

public class ProductJoinDefinitionDialog extends Dialog {

	private String[] availableProductNames;
	private Label lblProductName;
	private Composite container;
	private Text textProductJoinName;
	private Button btnAddProduct;
	private ArrayList<Combo> listOfChildProductCombos;
	private Control presentRow;
	private String productJoinName;
	private List<Product> associatedProducts;
	private String defaultProductName;
	private ScrolledComposite scrollContainer;
	private ScrolledComposite scrollContainerGrades;
	private Composite productListContainerComposite;
	private Composite gradeListContainerComposite;
	private Composite presentGrade;
	private Label lblInfoMessageDefinedGrades;
	private ArrayList<Text> listOfGradeNames;
	private Label lblGradeName;
	private Text textGradeName;
	private Text lastGrade;
	private ArrayList<String> associatedGradeNames;
	
	
	
	public ProductJoinDefinitionDialog(Shell parentShell, String[] availableProductNames, String defaultProductName) {
		super(parentShell);
		this.availableProductNames = availableProductNames;
		this.defaultProductName = defaultProductName;
		this.listOfChildProductCombos = new ArrayList<Combo>();
		this.associatedProducts = new ArrayList<Product>();
		this.listOfGradeNames = new ArrayList<Text>();
		this.associatedGradeNames = new ArrayList<String>();
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		this.container = (Composite) super.createDialogArea(parent);
		this.container.setLayout(new FormLayout());
		
		this.lblProductName = new Label(this.container, SWT.NONE);
		this.lblProductName.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblProductName = new FormData();
		fd_lblProductName.top = new FormAttachment(0, 10);
		fd_lblProductName.left = new FormAttachment(0, 10);
		this.lblProductName.setLayoutData(fd_lblProductName);
		this.lblProductName.setText("Product Join Name:");
		
		this.textProductJoinName = new Text(this.container, SWT.BORDER);
		FormData fd_textProductJoinName = new FormData();
		fd_textProductJoinName.top = new FormAttachment(0, 10);
		fd_textProductJoinName.left = new FormAttachment(this.lblProductName, 6);
		fd_textProductJoinName.right = new FormAttachment(100, -10);
		this.textProductJoinName.setLayoutData(fd_textProductJoinName);
		
		this.btnAddProduct = new Button(container, SWT.NONE);
		this.btnAddProduct.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO add implementation for button click
				addProductDefinitionRow();
			}
		});
		FormData fd_btnAddProduct = new FormData();
		fd_btnAddProduct.right = new FormAttachment(textProductJoinName, 0, SWT.RIGHT);
		fd_btnAddProduct.top = new FormAttachment(lblProductName, 20);
		fd_btnAddProduct.left = new FormAttachment(0, 10);
		this.btnAddProduct.setLayoutData(fd_btnAddProduct);
		this.btnAddProduct.setText("Add Products");
		
		this.scrollContainer = new ScrolledComposite(this.container, SWT.BORDER | SWT.V_SCROLL);
		FormData fd_scrollContainer = new FormData(500,500);// temp hack else size of scrolled composite keeps on increasing
		fd_scrollContainer.top = new FormAttachment(this.btnAddProduct);
		fd_scrollContainer.bottom = new FormAttachment(60);
		fd_scrollContainer.right = new FormAttachment(100, -10);
		fd_scrollContainer.left = new FormAttachment(0, 10);
		
		this.scrollContainer.setExpandHorizontal(true);
		this.scrollContainer.setExpandVertical(true);
		this.scrollContainer.setLayoutData(fd_scrollContainer);
		
		this.productListContainerComposite = new Composite(this.scrollContainer, SWT.NONE);
		this.productListContainerComposite.setLayout(new FormLayout());
		this.productListContainerComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.scrollContainer.setContent(this.productListContainerComposite);
		
		this.scrollContainerGrades = new ScrolledComposite(this.container, SWT.BORDER | SWT.V_SCROLL);
		FormData fd_scrollContainerGrades = new FormData(500,500);// temp hack else size of scrolled composite keeps on increasing
		fd_scrollContainerGrades.top = new FormAttachment(scrollContainer, 40);
		fd_scrollContainerGrades.bottom = new FormAttachment(100, -5);
		fd_scrollContainerGrades.right = new FormAttachment(100, -10);
		fd_scrollContainerGrades.left = new FormAttachment(0, 10);
		
		this.scrollContainerGrades.setExpandHorizontal(true);
		this.scrollContainerGrades.setExpandVertical(true);
		this.scrollContainerGrades.setLayoutData(fd_scrollContainerGrades);
		
		this.gradeListContainerComposite = new Composite(this.scrollContainerGrades, SWT.NONE);
		this.gradeListContainerComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.gradeListContainerComposite.setLayout(new FormLayout());
		this.scrollContainerGrades.setContent(this.gradeListContainerComposite);
		
		
		Rectangle r1 = this.scrollContainer.getClientArea();
		this.scrollContainer.setMinSize(this.scrollContainer.computeSize(SWT.DEFAULT, r1.height, true));
		
		Rectangle r2 = this.scrollContainerGrades.getClientArea();
		this.scrollContainerGrades.setMinSize(this.scrollContainerGrades.computeSize(SWT.DEFAULT, r2.height, true));
		
		lblInfoMessageDefinedGrades = new Label(container, SWT.NONE);
		lblInfoMessageDefinedGrades.setText("Provide names for the associated grade groups inherited from products:");
		FormData fd_lblInfoMessageDefinedGrades = new FormData();
		fd_lblInfoMessageDefinedGrades.bottom = new FormAttachment(scrollContainerGrades, -6);
		fd_lblInfoMessageDefinedGrades.left = new FormAttachment(0, 10);
		lblInfoMessageDefinedGrades.setLayoutData(fd_lblInfoMessageDefinedGrades);
		
		this.addProductDefinitionRow(this.defaultProductName);
		this.prepareGradeDefinitionForm(this.defaultProductName);
				
		container.getShell().setText("Join Products");
		this.setDialogLocation();
		return this.container;
	}
	
	private void prepareGradeDefinitionForm(String selectedProductName) {
		Product assoiatedProduct = ProjectConfigutration.getInstance().getProductByName(selectedProductName);
		int i = 0;
		for(Grade grade: assoiatedProduct.getListOfGrades()){
			i++;
			Label lblGradeName = new Label(gradeListContainerComposite, SWT.NONE);
			FormData fd_lblGradeName = new FormData();
			if(this.lastGrade == null){
				fd_lblGradeName.top = new FormAttachment(0, 20);
			}else{
				fd_lblGradeName.top = new FormAttachment(this.lastGrade, 10);
			}
			fd_lblGradeName.left = new FormAttachment(0, 10);
			lblGradeName.setLayoutData(fd_lblGradeName);
			lblGradeName.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			lblGradeName.setText("Grade " + i + ":");
			
			Text textGradeName = new Text(gradeListContainerComposite, SWT.BORDER);
			FormData fd_textGradeName = new FormData();
			fd_textGradeName.right = new FormAttachment(100, -20);
			fd_textGradeName.top = new FormAttachment(lblGradeName, 0, SWT.TOP);
			fd_textGradeName.left = new FormAttachment(lblGradeName, 10, SWT.RIGHT);
			textGradeName.setLayoutData(fd_textGradeName);
			listOfGradeNames.add(textGradeName);
			this.lastGrade = textGradeName;
		}
	}
	
	private void addProductDefinitionRow() {
		Label lblSelectProduct = new Label(productListContainerComposite, SWT.NONE);
		lblSelectProduct.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		lblSelectProduct.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_lblSelectProduct = new FormData();
		if(this.presentRow == null){
			fd_lblSelectProduct.top = new FormAttachment(0, 10);
		}else{
			fd_lblSelectProduct.top = new FormAttachment(this.presentRow, 10);
		}
		fd_lblSelectProduct.left = new FormAttachment(0, 10);
		lblSelectProduct.setLayoutData(fd_lblSelectProduct);
		lblSelectProduct.setText("Select Product:");
		
		Combo comboProduct = new Combo(productListContainerComposite, SWT.NONE);
		comboProduct.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		comboProduct.setItems(this.availableProductNames);
		FormData fd_comboProduct = new FormData();
		fd_comboProduct.top = new FormAttachment(lblSelectProduct, 0, SWT.TOP);
		fd_comboProduct.left = new FormAttachment(lblSelectProduct, 6);
		fd_comboProduct.right = new FormAttachment(100, -10);
		comboProduct.setLayoutData(fd_comboProduct);
		
		this.productListContainerComposite.layout();
		this.presentRow = lblSelectProduct;
		this.listOfChildProductCombos.add(comboProduct);
		
		Rectangle r = this.scrollContainer.getClientArea();
		this.scrollContainer.setMinSize(this.scrollContainer.computeSize((r.width - 20), SWT.DEFAULT, true));
	}
	
	private void addProductDefinitionRow(String selectedProductName) {
		Label lblSelectProduct = new Label(productListContainerComposite, SWT.NONE);
		lblSelectProduct.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		lblSelectProduct.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_lblSelectProduct = new FormData();
		if(this.presentRow == null){
			fd_lblSelectProduct.top = new FormAttachment(0, 10);
		}else{
			fd_lblSelectProduct.top = new FormAttachment(this.presentRow, 10);
		}
		fd_lblSelectProduct.left = new FormAttachment(0, 10);
		lblSelectProduct.setLayoutData(fd_lblSelectProduct);
		lblSelectProduct.setText("Select Product:");
		
		Combo comboProduct = new Combo(productListContainerComposite, SWT.NONE);
		comboProduct.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		comboProduct.setItems(this.availableProductNames);
		FormData fd_comboProduct = new FormData();
		fd_comboProduct.top = new FormAttachment(lblSelectProduct, 0, SWT.TOP);
		fd_comboProduct.left = new FormAttachment(lblSelectProduct, 6);
		fd_comboProduct.right = new FormAttachment(100, -10);
		comboProduct.setLayoutData(fd_comboProduct);
		
		int defaultIndex = 0;
		for(int i=0; i<this.availableProductNames.length; i++){
			if(this.availableProductNames[i].equals(selectedProductName)){
				defaultIndex = i;
				break;
			}
		}
		comboProduct.select(defaultIndex);
		
		this.container.layout();
		this.presentRow = lblSelectProduct;
		this.listOfChildProductCombos.add(comboProduct);
		
		Rectangle r = this.scrollContainer.getClientArea();
		this.scrollContainer.setMinSize(this.scrollContainer.computeSize((r.width - 20), SWT.DEFAULT, true));
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Add", true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(735, 487);
	}

	@Override
	protected void okPressed() {
		System.out.println("OK Pressed");
		this.productJoinName = textProductJoinName.getText();
		for(Combo productCombo : listOfChildProductCombos){
			String productName = productCombo.getText();
			Product product = ProjectConfigutration.getInstance().getProductByName(productName);
			this.associatedProducts.add(product);
		}
		
		for(Text text: listOfGradeNames){
			String gradeName = text.getText();
			associatedGradeNames.add(gradeName);
		}
		super.okPressed();
	}

	private void setDialogLocation(){
		Rectangle monitorArea = getShell().getDisplay().getPrimaryMonitor().getBounds();
		//Rectangle shellArea = getShell().getBounds();
		int x = monitorArea.x + (monitorArea.width - 980)/2;
		int y = monitorArea.y + (monitorArea.height - 650)/2;
		System.out.println("Process dialog X: "+ x);
		System.out.println("Process dialog Y: "+ y);
		getShell().setLocation(x,y);
	}
	
	
	public String getProductJoinName() {
		return this.productJoinName;
	}
	
	public List<Product> getAssociatedProducts() {
		return this.associatedProducts;
	}
	
	public ArrayList<String> getAssociatedGradeNames() {
		return this.associatedGradeNames;
	}
}
