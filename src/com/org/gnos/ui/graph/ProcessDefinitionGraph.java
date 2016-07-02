package com.org.gnos.ui.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.core.widgets.internal.GraphLabel;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import com.org.gnos.core.Node;
import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.core.Tree;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Grade;
import com.org.gnos.db.model.Model;
import com.org.gnos.db.model.ProcessJoin;
import com.org.gnos.db.model.Product;
import com.org.gnos.db.model.ProductJoin;
import com.org.gnos.ui.custom.controls.ProductDefinitionDialog;
import com.org.gnos.ui.custom.controls.ProductJoinAdditionDialog;
import com.org.gnos.ui.custom.controls.ProductJoinDefinitionDialog;
import com.org.gnos.ui.custom.controls.ProductPropertiesDialog;
import com.org.gnos.utilities.SWTResourceManager;

public class ProcessDefinitionGraph extends Composite {

	private Composite parent;
	private Graph graph;
	private GraphNode rootNode;
	private GraphNode presentNode;
	private HashMap<String, GraphNode> existingProcessGraphNodes;
	private HashMap<String, GraphNode> existingProcessJoinGraphNodes;
	private HashMap<String, GraphNode> existingProductGraphNodes;
	private HashMap<String, GraphNode> existingProductJoinGraphNodes;
	private List<Product> listOfProducts;
	private List<ProductJoin> listOfProductJoins;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ProcessDefinitionGraph(Composite parent, int style) {
		super(parent, style);
		this.parent = parent;
		this.setLayout(new FillLayout());
		this.existingProcessGraphNodes = new HashMap<String, GraphNode>();
		this.existingProcessJoinGraphNodes = new HashMap<String, GraphNode>();
		this.existingProductGraphNodes = new HashMap<String, GraphNode>();
		this.existingProductJoinGraphNodes = new HashMap<String, GraphNode>();
		this.listOfProducts = ProjectConfigutration.getInstance().getProductList();
		this.listOfProductJoins = ProjectConfigutration.getInstance().getProductJoinList();
	}

	private boolean isLeafNode(String nodeName){
		List<Node> leafNodes = ProjectConfigutration.getInstance().getProcessTree().getLeafNodes();
		for(Node node: leafNodes){
			if(node.getData().getName().equals(nodeName)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isProductNode(String productName) {
		List<Product> products = ProjectConfigutration.getInstance().getProductList();
		for(Product product: products){
			if(product.getName().equals(productName)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isProductJoinNode(String productJoinName) {
		List<ProductJoin> productJoins = ProjectConfigutration.getInstance().getProductJoinList();
		for(ProductJoin productJoin: productJoins){
			if(productJoin.getName().equals(productJoinName)){
				return true;
			}
		}
		return false;
	}


	public void refreshTree(Tree processTree){
		if(this.graph != null){
			this.graph.dispose();
		}
		this.graph = new Graph(this, SWT.NONE);
		this.layout();
		this.graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
		this.graph.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println(e.getSource());
			}

		});
		this.graph.addMenuDetectListener(new MenuDetectListener()
		{
			@Override
			public void menuDetected(MenuDetectEvent e)
			{
				Point point = graph.toControl(e.x, e.y);
				IFigure fig = graph.getFigureAt(point.x, point.y);

				if (fig != null){
					final String nodeName = ((GraphLabel) fig).getText();
					if(isLeafNode(nodeName)){
						Menu menu = new Menu(getShell(), SWT.POP_UP);
						MenuItem itemAddProduct = new MenuItem(menu, SWT.NONE);
						itemAddProduct.setText("Add a product to " + ((GraphLabel) fig).getText());
						itemAddProduct.addListener(SWT.Selection, new Listener() {
							public void handleEvent(Event e) {
								handleAddProductToProcess(nodeName);
							}
						});
						menu.setVisible(true);
					}else if(isProductNode(nodeName)){
						Menu menu = new Menu(getShell(), SWT.POP_UP);
						MenuItem itemAddProductJoin = new MenuItem(menu, SWT.NONE);
						itemAddProductJoin.setText("Join with other products to create a join");
						itemAddProductJoin.addListener(SWT.Selection, new Listener() {
							public void handleEvent(Event e) {
								handleCreateProductJoin(nodeName);
							}
						});
						MenuItem itemShowProperties = new MenuItem(menu, SWT.NONE);
						itemShowProperties.setText("Properties");
						itemShowProperties.addListener(SWT.Selection, new Listener() {
							public void handleEvent(Event e) {
								//handleCreateProductJoin(nodeName);
								System.out.println("show properties");
								handleShowProductProperties(nodeName);
							}
						});
						menu.setVisible(true);
					}else if(isProductJoinNode(nodeName)){
						Menu menu = new Menu(getShell(), SWT.POP_UP);
						MenuItem itemAddProductJoin = new MenuItem(menu, SWT.NONE);
						itemAddProductJoin.setText("Join with other product joins to create a join");
						itemAddProductJoin.addListener(SWT.Selection, new Listener() {
							public void handleEvent(Event e) {
								handleCombineProductJoins(nodeName);
							}
						});
						menu.setVisible(true);
					}
				}
			}
		});
		this.displayProcess(processTree.getRoot());
	}
	
	private void handleShowProductProperties(String nodeName){
		Product selectedProduct = ProjectConfigutration.getInstance().getProductByName(nodeName);
		ProductPropertiesDialog productPropertiesDialog = new ProductPropertiesDialog(getShell(), selectedProduct);
		if (Window.OK == productPropertiesDialog.open()) {
			
		}
	}
	
	private void handleCombineProductJoins(String initialProductJoinName){
		System.out.println("Create join with " + initialProductJoinName);
		String[] listOfProductNames = this.getProductJoinNames();
		ProductJoinAdditionDialog productJoinAdditionDialog = new ProductJoinAdditionDialog(getShell(), listOfProductNames, initialProductJoinName);
		if (Window.OK == productJoinAdditionDialog.open()) {
			String definedProductJoinName = productJoinAdditionDialog.getProductJoinName();
			List<ProductJoin> childProductJoins = productJoinAdditionDialog.getChildProductJoins();
			
			ProductJoin newProductJoin = new ProductJoin(definedProductJoinName);
			newProductJoin.setListChildProductJoins(childProductJoins);
			
			this.addProductJoinToProductJoins(newProductJoin);
			this.listOfProductJoins.add(newProductJoin);
		}
	}
	
	private void handleCreateProductJoin(String initialProductName) {
		System.out.println("Create join with " + initialProductName);
		String[] listOfProductNames = this.getProductNames();
		ProductJoinDefinitionDialog productJoinDefinitionDialog = new ProductJoinDefinitionDialog(getShell(), listOfProductNames, initialProductName);
		if (Window.OK == productJoinDefinitionDialog.open()) {
			String definedProductJoinName = productJoinDefinitionDialog.getProductJoinName();
			List<Product> associatedProducts = productJoinDefinitionDialog.getAssociatedProducts();
			
			ProductJoin newProductJoin = new ProductJoin(definedProductJoinName);
			newProductJoin.setListChildProducts(associatedProducts);
			
			this.addProductJoin(newProductJoin);
			this.listOfProductJoins.add(newProductJoin);
		}
	}
	
	public void addProduct(Product product){
		GraphNode productNode = new GraphNode(this.graph, SWT.NONE, product.getName());
		productNode.setBackgroundColor(SWTResourceManager.getColor(SWT.COLOR_GREEN));
		GraphNode parentNode = this.existingProcessGraphNodes.get(product.getAssociatedProcess().getName());
		this.existingProductGraphNodes.put(product.getName(), productNode);
		new GraphConnection(this.graph, ZestStyles.CONNECTIONS_DIRECTED, parentNode, productNode);
		this.graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
	}
	
	private void handleAddProductToProcess(String processName){
		System.out.println("We must now add a product to the process: " + processName);
		
		String[] listOfExpressionNames = this.getNonGradeExpressionNames();

		ProductDefinitionDialog productDefinitionDialog = new ProductDefinitionDialog(getShell(), listOfExpressionNames);
		if (Window.OK == productDefinitionDialog.open()) {
			String definedProductName = productDefinitionDialog.getProductName();
			List<Expression> associatedExpressions = productDefinitionDialog.getAssociatedExpressions();
			ArrayList<Grade> associatedGrades = productDefinitionDialog.getAssociatedGrades();
			String createdProductName = processName + '_' + definedProductName;
			Model associatedProcess = ProjectConfigutration.getInstance().getModelByName(processName);
			
			Product newProduct = new Product(createdProductName, associatedProcess);
			newProduct.setListOfExpressions(associatedExpressions);
			if(associatedGrades.size() > 0){
				newProduct.setListOfGrades(associatedGrades);
			}
			
			this.addProduct(newProduct);
			this.listOfProducts.add(newProduct);
		}
	}
	
	private String[] getNonGradeExpressionNames(){

		List<Expression> expressions = ProjectConfigutration.getInstance().getNonGradeExpressions();
        String[] expressioNamesArray = new String[expressions.size()];
		for(int i=0; i<expressions.size(); i++){
			expressioNamesArray[i] = expressions.get(i).getName();
		}

		return expressioNamesArray;
	}
	
	private String[] getProductNames(){

		List<Product> products = ProjectConfigutration.getInstance().getProductList();
        String[] productNamesArray = new String[products.size()];
		for(int i=0; i<products.size(); i++){
			productNamesArray[i] = products.get(i).getName();
		}

		return productNamesArray;
	}
	
	private String[] getProductJoinNames(){

		List<ProductJoin> productJoins = ProjectConfigutration.getInstance().getProductJoinList();
        String[] productJoinNamesArray = new String[productJoins.size()];
		for(int i=0; i<productJoins.size(); i++){
			productJoinNamesArray[i] = productJoins.get(i).getName();
		}

		return productJoinNamesArray;
	}

	public void displayProcess(Node rootnode) {
		this.displayProcess(rootnode, null);
	}


	public void displayProcess(Node node, GraphNode parent){
		List<Node> children = node.getChildrens();
		GraphNode graphNode = new GraphNode(this.graph, SWT.NONE, node.getIdentifier());
		this.existingProcessGraphNodes.put(node.getIdentifier(), graphNode);
		if(parent != null){
			new GraphConnection(this.graph, ZestStyles.CONNECTIONS_DIRECTED, parent, graphNode);
		}

		for (Node child : children) {
			// Recursive call
			this.displayProcess(child, graphNode);
		}
	}

	public void addProcess(Node node){
		GraphNode processNode = new GraphNode(this.graph, SWT.NONE, node.getIdentifier());
		GraphNode parentNode = this.existingProcessGraphNodes.get(node.getParent().getIdentifier());
		this.existingProcessGraphNodes.put(node.getIdentifier(), processNode);
		new GraphConnection(this.graph, ZestStyles.CONNECTIONS_DIRECTED, parentNode, processNode);
		this.graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
	}

	public void addProcessJoin(ProcessJoin processJoin) {
		GraphNode processJoinNode = new GraphNode(this.graph, SWT.NONE, "Process Join: " + processJoin.getName());
		processJoinNode.setBackgroundColor(SWTResourceManager.getColor(SWT.COLOR_RED));
		this.existingProcessJoinGraphNodes.put(processJoin.getName(), processJoinNode);
		for(Model model : processJoin.getlistChildProcesses()){
			GraphNode processNode = this.existingProcessGraphNodes.get(model.getName());
			new GraphConnection(this.graph, ZestStyles.CONNECTIONS_DIRECTED, processJoinNode, processNode);
		}
		this.graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
	}
	
	public void addProductJoin(ProductJoin productJoin) {
		GraphNode productJoinNode = new GraphNode(this.graph, SWT.NONE, productJoin.getName());
		productJoinNode.setBackgroundColor(SWTResourceManager.getColor(SWT.COLOR_GREEN));
		this.existingProductJoinGraphNodes.put(productJoin.getName(), productJoinNode);
		for(Product product : productJoin.getlistChildProducts()){
			GraphNode productNode = this.existingProductGraphNodes.get(product.getName());
			new GraphConnection(this.graph, ZestStyles.CONNECTIONS_DIRECTED, productNode, productJoinNode);
		}
		this.graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
	}
	
	public void addProductJoinToProductJoins(ProductJoin productJoin) {
		GraphNode parentProductJoinNode = new GraphNode(this.graph, SWT.NONE, productJoin.getName());
		parentProductJoinNode.setBackgroundColor(SWTResourceManager.getColor(SWT.COLOR_GREEN));
		this.existingProductJoinGraphNodes.put(productJoin.getName(), parentProductJoinNode);
		for(ProductJoin childProductJoin : productJoin.getListChildProductJoins()){
			GraphNode childProductNode = this.existingProductJoinGraphNodes.get(childProductJoin.getName());
			new GraphConnection(this.graph, ZestStyles.CONNECTIONS_DIRECTED, childProductNode, parentProductJoinNode);
		}
		this.graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}