package com.org.gnos.services.controller;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.org.gnos.db.dao.ProcessTreeDAO;
import com.org.gnos.db.dao.ProcessTreeStateDAO;
import com.org.gnos.db.model.ProcessTreeNode;
import com.org.gnos.db.model.ProcessTreeNodeState;

public class ProcessTreeController {

	ProcessTreeDAO dao;
	ProcessTreeStateDAO statedao;
	
	public ProcessTreeController() {
		dao = new ProcessTreeDAO();
		statedao = new ProcessTreeStateDAO();
	}
	
	public List<ProcessTreeNode> getAll(String projectId) {
		
		return dao.getAll(Integer.parseInt(projectId));
	}
	
	public List<ProcessTreeNodeState> getAllState(String projectId) {
		
		return statedao.getAll(Integer.parseInt(projectId));
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
	
	public boolean saveState(JsonObject jsonObject, String projectId) throws Exception {
		JsonArray elmArray = jsonObject.getAsJsonArray();
		if(elmArray.size() == 0) {
			throw new Exception("Nothing to save");
		}
		statedao.deleteAll(Integer.parseInt(projectId));
		for(JsonElement elm : elmArray) {
			ProcessTreeNodeState obj = new ProcessTreeNodeState();
			JsonObject elmObj = elm.getAsJsonObject();
			obj.setNodeName(elmObj.get("nodeName").getAsString());
			obj.setxLoc(elmObj.get("xLoc").getAsFloat());
			obj.setyLoc(elmObj.get("yLoc").getAsFloat());
			statedao.create(obj, Integer.parseInt(projectId));
		}
		return true;
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
