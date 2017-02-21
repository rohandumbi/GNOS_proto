package com.org.gnos.services.controller;

import java.util.List;

import com.google.gson.JsonObject;
import com.org.gnos.db.dao.ModelDAO;
import com.org.gnos.db.model.Model;

public class ModelController {
	private ModelDAO dao;
	
	public ModelController() {
		dao = new ModelDAO();
	}
	
	public List<Model> getAll(String projectIdStr) {
		int projectId = Integer.parseInt(projectIdStr);
		return dao.getAll(projectId);
	}
	
	public Model create(JsonObject jsonObject, String pid) throws Exception {
		String name = jsonObject.get("name").getAsString();
		int expressionId = jsonObject.get("expressionId").getAsInt();
		String condition = jsonObject.get("condition").getAsString();
		Model obj = new Model();
		obj.setName(name);
		obj.setExpressionId(expressionId);
		obj.setCondition(condition);	
		boolean created = dao.create(obj, Integer.parseInt(pid));
		if(created) return obj;
		throw new Exception();
	}
	
	
	public Model update(JsonObject jsonObject, String id) throws Exception {		
		String name = jsonObject.get("name").getAsString();
		int expressionId = jsonObject.get("expressionId").getAsInt();
		String condition = jsonObject.get("condition").getAsString();
		Model obj = new Model();
		obj.setId(Integer.parseInt(id));
		obj.setName(name);
		obj.setExpressionId(expressionId);
		obj.setCondition(condition);
		boolean created = dao.update(obj );
		if(created) return obj;
		throw new Exception();
	}
	
	public boolean delete(String id) {
		if((id == null) || (id.isEmpty())){
			return false;
		}else{
			Model model = new Model();
			model.setId(Integer.parseInt(id));
			dao.delete(model);
			return true;
		}	
	}

}
