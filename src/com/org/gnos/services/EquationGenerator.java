package com.org.gnos.services;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.org.gnos.core.Expression;
import com.org.gnos.core.Model;
import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.db.DBManager;

public class EquationGenerator {

	private BufferedOutputStream output;
	private ProjectConfigutration projectConfiguration;
	private Map<String, Node> nodes;
	
	public void generate() throws IOException {
		projectConfiguration = ProjectConfigutration.getInstance();
		Tree processtree = projectConfiguration.getProcessTree();
		
		int bufferSize = 8 * 1024;
		output = new BufferedOutputStream(new FileOutputStream("output.txt"), bufferSize);
		nodes = processtree.getNodes();
		Iterator<Node> it = processtree.iterator("Block");
		Node rootNode = it.next(); // this is first node.. so this must be block node. skipping this.
		traverseNode(rootNode,"", 1);
		output.flush();
		output.close();
		System.out.println("Inside generate");
	}

	private void traverseNode(Node node, String condition, int depth) {
		System.out.println("traverseNode "+depth+ node.getIdentifier());
		List<String> childrens = node.getChildren();
		Model model = projectConfiguration.getModelByName(node.getIdentifier());
		if(model != null){
			condition = condition + model.getCondition();
			buildEquation(model, condition, depth);
			depth++;
		}
		
		for(int i=0; i < childrens.size(); i++){
			traverseNode(nodes.get(childrens.get(i)), condition, depth);		
		}		
	}
	
	private void buildEquation(Model model, String condition, int depth) {
		Expression expr = model.getExpression();
		String expr_name = expr.getName().replaceAll("\\s+","_").toLowerCase();
		String sql = "select id, pit_no, "+expr_name+" from gnos_data_"+projectConfiguration.getProjectId() ;
		if(condition != null  && condition.trim().length() > 0) {
			sql = sql + condition;
		}
		
		Connection conn = DBManager.getConnection();
		Statement stmt;
		ResultSet rs;
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				String eq = "p"+rs.getString(2)+"x"+rs.getString(1)+"p"+depth;
				System.out.println(eq);
				output.write(eq.getBytes());
			}
		} catch (SQLException e) {
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
