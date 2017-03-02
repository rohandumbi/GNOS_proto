package com.org.gnos.services.controller;

import java.util.List;

import com.google.gson.JsonObject;
import com.org.gnos.db.dao.ProcessTreeDAO;
import com.org.gnos.db.model.ProcessTreeNode;

public class ProcessTreeController {

	ProcessTreeDAO dao;
	
	public ProcessTreeController() {
		dao = new ProcessTreeDAO();
	}
	
	public List<ProcessTreeNode> getAll(String projectId) {
		
		return dao.getAll(Integer.parseInt(projectId));
	}
	
	public ProcessTreeNode create(JsonObject jsonObject, String projectId) throws Exception {
		int modelId = jsonObject.get("modelId").getAsInt();
		int parentModelId =  jsonObject.get("parentModelId").getAsInt();
		ProcessTreeNode obj = new ProcessTreeNode();
		obj.setModelId(modelId);
		obj.setParentModelId(parentModelId);

		boolean created = dao.create(obj, Integer.parseInt(projectId));
		if(created) return obj;
		throw new Exception();
	}
	
	
	public boolean deleteAll(String projectId) {
		dao.deleteAll(Integer.parseInt(projectId));
		return true;
	}
	
	public boolean deleteProcessTreeNode(String projectId, int modelId) {	
		dao.deleteProcessTreeNode(Integer.parseInt(projectId), modelId);
		return true;
	}
}
