package com.org.gnos.services.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.org.gnos.core.Node;
import com.org.gnos.core.Tree;
import com.org.gnos.db.DBManager;
import com.org.gnos.db.dao.ModelDAO;
import com.org.gnos.db.model.Model;

public class ProcessController {

	public List<Model> getAll(String projectId) {
		
		List<Model> processes = new ArrayList<Model>();
		List<Model> models = new ModelDAO().getAll(Integer.parseInt(projectId));
		String sql = "select model_id, parent_model_id from process_route_defn where project_id = "+projectId;
		try(
				Connection connection = DBManager.getConnection();
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(sql);
			){
				Map<String, Node> nodes = new HashMap<String, Node>();
				Tree processTree = new Tree();
				while (rs.next()) {
					int modelId = rs.getInt(1);
					int parentModelId = rs.getInt(2);
					
					Model model = this.getModelById(modelId, models);;
				
					Node node = nodes.get(model.getName());
					if (node == null) {
						node = new Node(model);
						nodes.put(model.getName(), node);
					}
					if (parentModelId == -1) {
						processTree.addNode(node, null);
					} else {
						Model pModel = this.getModelById(parentModelId, models);
						if (pModel != null) {
							Node pNode = nodes.get(pModel.getName());
							if (pNode == null) {
								pNode = new Node(pModel);
								nodes.put(pModel.getName(), pNode);
							}
							processTree.addNode(node, pNode);
							node.setParent(pNode);
						}
					}
				}
				List<Node> leafNodes = processTree.getLeafNodes();
				for(Node node: leafNodes) {
					processes.add(node.getData());
				}
			} catch(SQLException e){
				e.printStackTrace();
			}
		
		return processes;
	}
	
	public Model getModelById(int modelId, List<Model> models) {
		for (Model model : models) {
			if (model.getId() == modelId) {
				return model;
			}
		}
		return null;
	}
}
