package com.org.gnos.services;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.core.Expression;
import com.org.gnos.core.Model;
import com.org.gnos.core.OpexData;
import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.db.DBManager;

public class EquationGenerator {

	private BufferedOutputStream output;
	private ProjectConfigutration projectConfiguration;
	private Map<String, Node> nodes;
	private Map<Integer, List<CostRevenueData>> modelOpexDataMapping;
	
	public void generate() throws IOException {
		projectConfiguration = ProjectConfigutration.getInstance();
		Tree processtree = projectConfiguration.getProcessTree();
		
		int bufferSize = 8 * 1024;
		output = new BufferedOutputStream(new FileOutputStream("c:\\output.txt"), bufferSize);
		nodes = processtree.getNodes();
		Iterator<Node> it = processtree.iterator("Block");
		Node rootNode = it.next(); // this is first node.. so this must be block node. skipping this.
		parseOpexData();
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
		int modelId = model.getId();
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
				float exprvalue = Float.parseFloat(rs.getString(3));
				String eq = "p"+rs.getString(2)+"x"+rs.getString(1)+"p"+depth;
				List<CostRevenueData> costYearData = modelOpexDataMapping.get(modelId);
				if(costYearData != null){
					int count = 1 ;
					for(CostRevenueData crd: costYearData) {
						String ueq ;
						float value = crd.revenue * exprvalue - crd.cost;
						ueq = value+ eq + "t"+count +" ";
						if(value >= 0){
							ueq = "+"+ ueq;
						} 
						output.write(ueq.getBytes());
						count++;
					}
				} else {
					String ueq = "+0"+ eq +"t1 ";
					output.write(ueq.getBytes());
				}
			}
		} catch (SQLException e) {
			System.out.println("Error "+ e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void parseOpexData () {
		List<OpexData> opexDataList = projectConfiguration.getOpexDataList();
		modelOpexDataMapping = new LinkedHashMap<Integer, List<CostRevenueData>>();
		
		for(OpexData opexData: opexDataList) {
			int modelId = opexData.getModel().getId();
			List<CostRevenueData> yearCostData = modelOpexDataMapping.get(modelId);
			if(yearCostData == null) {
				yearCostData = new ArrayList<CostRevenueData>();
				modelOpexDataMapping.put(modelId, yearCostData);
					
				Map<Integer, Integer> costData = opexData.getCostData();
				Set<Integer> keys = costData.keySet();
				Iterator<Integer> it = keys.iterator();
				while(it.hasNext()){
					CostRevenueData crd = new CostRevenueData();
					crd.year = it.next();
				
					if(opexData.isRevenue()) {
						crd.revenue = costData.get(crd.year);
					} else {
						crd.cost = costData.get(crd.year);
					}
					yearCostData.add(crd);
				}
			} else {
				Map<Integer, Integer> costData = opexData.getCostData();
				Set<Integer> keys = costData.keySet();
				Iterator<Integer> it = keys.iterator();
				while(it.hasNext()){
					int year = it.next();
					for(CostRevenueData crd: yearCostData){
						if(crd.year == year) {
							if(opexData.isRevenue()) {
								crd.revenue = costData.get(crd.year);
							} else {
								crd.cost = costData.get(crd.year);
							}
							break;
						}
					}				
				}
			}
		}
		
	}
	
	private class CostRevenueData {
		int year;
		int cost;
		int revenue;
	}
}
