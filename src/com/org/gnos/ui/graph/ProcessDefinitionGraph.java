package com.org.gnos.ui.graph;

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
import com.org.gnos.db.model.Model;
import com.org.gnos.db.model.ProcessJoin;
import com.org.gnos.db.model.Product;
import com.org.gnos.ui.custom.controls.ProductDefinitionDialog;
import com.org.gnos.utilities.SWTResourceManager;

public class ProcessDefinitionGraph extends Composite {

	private Composite parent;
	private Graph graph;
	private GraphNode rootNode;
	private GraphNode presentNode;
	private HashMap<String, GraphNode> existingProcessGraphNodes;
	private HashMap<String, GraphNode> existingProcessJoinGraphNodes;
	private HashMap<String, GraphNode> existingProductGraphNodes;
	private List<Product> listOfProducts;
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
		this.listOfProducts = ProjectConfigutration.getInstance().getProductList();
	}

	private boolean isLeafNode(String nodeName){
		//List<Model> models = new ArrayList<Model>();
		List<Node> leafNodes = ProjectConfigutration.getInstance().getProcessTree().getLeafNodes();
		for(Node node: leafNodes){
			//models.add(node.getData());
			if(node.getData().getName().equals(nodeName)){
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
		// Selection listener on graphConnect or GraphNode is not supported
		// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=236528
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
					}
				}
			}
		});
		this.displayProcess(processTree.getRoot());
	}
	
	public void addProduct(Product product){
		GraphNode productNode = new GraphNode(this.graph, SWT.NONE, product.getName());
		productNode.setBackgroundColor(SWTResourceManager.getColor(SWT.COLOR_GREEN));
		GraphNode parentNode = this.existingProcessGraphNodes.get(product.getAssociatedProcess().getName());
		this.existingProductGraphNodes.put(product.getName(), productNode);
		new GraphConnection(this.graph, ZestStyles.CONNECTIONS_DIRECTED, parentNode, productNode);
		this.graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
	}
	
	public void handleAddProductToProcess(String processName){
		System.out.println("We must now add a product to the process: " + processName);
		
		String[] listOfExpressionNames = this.getNonGradeExpressionNames();

		ProductDefinitionDialog productDefinitionDialog = new ProductDefinitionDialog(getShell(), listOfExpressionNames);
		if (Window.OK == productDefinitionDialog.open()) {
			String definedProductName = productDefinitionDialog.getProductName();
			List<Expression> associatedExpressions = productDefinitionDialog.getAssociatedExpressions();
			String createdProductName = processName + '_' + definedProductName;
			Model associatedProcess = ProjectConfigutration.getInstance().getModelByName(processName);
			
			Product newProduct = new Product(createdProductName, associatedProcess);
			newProduct.setListOfExpressions(associatedExpressions);
			
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
		this.existingProcessJoinGraphNodes.put(processJoin.getName(), processJoinNode);
		for(Model model : processJoin.getlistChildProcesses()){
			GraphNode processNode = this.existingProcessGraphNodes.get(model.getName());
			new GraphConnection(this.graph, ZestStyles.CONNECTIONS_DIRECTED, processJoinNode, processNode);
		}
		this.graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}