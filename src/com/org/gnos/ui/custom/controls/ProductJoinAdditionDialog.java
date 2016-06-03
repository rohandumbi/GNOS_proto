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
import com.org.gnos.db.model.Product;
import com.org.gnos.db.model.ProductJoin;

public class ProductJoinAdditionDialog extends Dialog {

	private String[] availableProductJoinNames;
	private Label lblProductJoinName;
	private Composite container;
	private Text textProductJoinName;
	private Button btnAddProductJoin;
	private ArrayList<Combo> listOfChildProductJoinCombos;
	private Control presentRow;
	private String productJoinName;
	private List<ProductJoin> childProductJoins;
	private String defaultProductJoinName;
	private ScrolledComposite scrollContainer;
	private Composite productJoinListContainerComposite;
	
	
	public ProductJoinAdditionDialog(Shell parentShell, String[] availableProductJoinNames, String defaultProductJoinName) {
		super(parentShell);
		this.availableProductJoinNames = availableProductJoinNames;
		this.defaultProductJoinName = defaultProductJoinName;
		this.listOfChildProductJoinCombos = new ArrayList<Combo>();
		this.childProductJoins = new ArrayList<ProductJoin>();
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		this.container = (Composite) super.createDialogArea(parent);
		this.container.setLayout(new FormLayout());
		
		this.lblProductJoinName = new Label(this.container, SWT.NONE);
		this.lblProductJoinName.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		FormData fd_lblProductJoinName = new FormData();
		fd_lblProductJoinName.top = new FormAttachment(0, 10);
		fd_lblProductJoinName.left = new FormAttachment(0, 10);
		this.lblProductJoinName.setLayoutData(fd_lblProductJoinName);
		this.lblProductJoinName.setText("Product Join Name:");
		
		this.textProductJoinName = new Text(this.container, SWT.BORDER);
		FormData fd_textProductJoinName = new FormData();
		fd_textProductJoinName.top = new FormAttachment(0, 10);
		fd_textProductJoinName.left = new FormAttachment(this.lblProductJoinName, 6);
		fd_textProductJoinName.right = new FormAttachment(100, -10);
		this.textProductJoinName.setLayoutData(fd_textProductJoinName);
		
		this.btnAddProductJoin = new Button(container, SWT.NONE);
		this.btnAddProductJoin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO add implementation for button click
				addProductJoinDefinitionRow();
			}
		});
		FormData fd_btnAddProduct = new FormData();
		fd_btnAddProduct.right = new FormAttachment(textProductJoinName, 0, SWT.RIGHT);
		fd_btnAddProduct.top = new FormAttachment(lblProductJoinName, 20);
		fd_btnAddProduct.left = new FormAttachment(0, 10);
		this.btnAddProductJoin.setLayoutData(fd_btnAddProduct);
		this.btnAddProductJoin.setText("Add Product Joins");
		//this.presentRow = this.btnAddProductJoin;
		
		this.scrollContainer = new ScrolledComposite(this.container, SWT.BORDER | SWT.V_SCROLL);
		FormData fd_scrollContainer = new FormData(500,500);// temp hack else size of scrolled composite keeps on increasing
		fd_scrollContainer.top = new FormAttachment(this.btnAddProductJoin);
		fd_scrollContainer.bottom = new FormAttachment(100, -5);
		fd_scrollContainer.right = new FormAttachment(100, -10);
		fd_scrollContainer.left = new FormAttachment(0, 10);
		
		this.scrollContainer.setExpandHorizontal(true);
		this.scrollContainer.setExpandVertical(true);
		this.scrollContainer.setLayoutData(fd_scrollContainer);
		
		this.productJoinListContainerComposite = new Composite(this.scrollContainer, SWT.NONE);
		this.productJoinListContainerComposite.setLayout(new FormLayout());
		this.productJoinListContainerComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.scrollContainer.setContent(this.productJoinListContainerComposite);
		
		Rectangle r = this.scrollContainer.getClientArea();
		this.scrollContainer.setMinSize(this.scrollContainer.computeSize(SWT.DEFAULT, r.height, true));
		
		this.addProductJoinDefinitionRow(this.defaultProductJoinName);
				
		container.getShell().setText("Join Product Joins");
		this.setDialogLocation();
		return this.container;
	}
	
	private void addProductJoinDefinitionRow() {
		Label lblSelectProductJoin = new Label(this.productJoinListContainerComposite, SWT.NONE);
		lblSelectProductJoin.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		lblSelectProductJoin.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_lblSelectProductJoin = new FormData();
		if(this.presentRow == null){
			fd_lblSelectProductJoin.top = new FormAttachment(0, 10);
		}else{
			fd_lblSelectProductJoin.top = new FormAttachment(this.presentRow, 10);
		}
		fd_lblSelectProductJoin.left = new FormAttachment(0, 10);
		lblSelectProductJoin.setLayoutData(fd_lblSelectProductJoin);
		lblSelectProductJoin.setText("Select Join:");
		
		Combo comboProductJoin = new Combo(this.productJoinListContainerComposite, SWT.NONE);
		comboProductJoin.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		comboProductJoin.setItems(this.availableProductJoinNames);
		FormData fd_comboProduct = new FormData();
		fd_comboProduct.top = new FormAttachment(lblSelectProductJoin, 0, SWT.TOP);
		fd_comboProduct.left = new FormAttachment(lblSelectProductJoin, 6);
		fd_comboProduct.right = new FormAttachment(100, -10);
		comboProductJoin.setLayoutData(fd_comboProduct);
		
		this.productJoinListContainerComposite.layout();
		this.presentRow = lblSelectProductJoin;
		this.listOfChildProductJoinCombos.add(comboProductJoin);
		
		Rectangle r = this.scrollContainer.getClientArea();
		this.scrollContainer.setMinSize(this.scrollContainer.computeSize((r.width - 20), SWT.DEFAULT, true));
	}
	
	private void addProductJoinDefinitionRow(String selectedProductName) {
		Label lblSelectProductJoin = new Label(this.productJoinListContainerComposite, SWT.NONE);
		lblSelectProductJoin.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		lblSelectProductJoin.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_lblSelectProductJoin = new FormData();
		if(this.presentRow == null){
			fd_lblSelectProductJoin.top = new FormAttachment(0, 10);
		}else{
			fd_lblSelectProductJoin.top = new FormAttachment(this.presentRow, 10);
		}
		fd_lblSelectProductJoin.top = new FormAttachment(this.presentRow, 10);
		fd_lblSelectProductJoin.left = new FormAttachment(0, 10);
		lblSelectProductJoin.setLayoutData(fd_lblSelectProductJoin);
		lblSelectProductJoin.setText("Select Join:");
		
		Combo comboProductJoin = new Combo(this.productJoinListContainerComposite, SWT.NONE);
		comboProductJoin.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		comboProductJoin.setItems(this.availableProductJoinNames);
		FormData fd_comboProduct = new FormData();
		fd_comboProduct.top = new FormAttachment(lblSelectProductJoin, 0, SWT.TOP);
		fd_comboProduct.left = new FormAttachment(lblSelectProductJoin, 6);
		fd_comboProduct.right = new FormAttachment(100, -10);
		comboProductJoin.setLayoutData(fd_comboProduct);
		
		int defaultIndex = 0;
		for(int i=0; i<this.availableProductJoinNames.length; i++){
			if(this.availableProductJoinNames[i].equals(selectedProductName)){
				defaultIndex = i;
				break;
			}
		}
		comboProductJoin.select(defaultIndex);
		
		this.productJoinListContainerComposite.layout();
		this.presentRow = lblSelectProductJoin;
		this.listOfChildProductJoinCombos.add(comboProductJoin);
		
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
		for(Combo productCombo : listOfChildProductJoinCombos){
			String productName = productCombo.getText();
			ProductJoin product = ProjectConfigutration.getInstance().getProductJoinByName(productName);
			this.childProductJoins.add(product);
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
	
	public List<ProductJoin> getChildProductJoins() {
		return this.childProductJoins;
	}
	
	
}
