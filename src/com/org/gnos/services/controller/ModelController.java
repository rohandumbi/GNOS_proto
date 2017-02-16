package com.org.gnos.services.controller;

import java.util.List;

import com.google.gson.JsonObject;
import com.org.gnos.db.dao.ModelDAO;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Model;

public class ModelController {
	private ModelDAO dao;
	
	public ModelController() {
		dao = new ModelDAO();
	}
	
	public List<Model> getAllExpressions(String projectIdStr) {
		int projectId = Integer.parseInt(projectIdStr);
		return dao.getAll(projectId);
	}
	
	public Model createModel(JsonObject jsonObject, String pid) throws Exception {
		String name = jsonObject.get("name").getAsString();
		boolean expr_id = jsonObject.get("expr_id").getAsBoolean();
		String condition = jsonObject.get("condition").getAsString();
		Model obj = new Model();
		obj.setName(name);
		obj.setCondition(condition);	
		boolean created = dao.create(obj, Integer.parseInt(pid));
		if(created) return obj;
		throw new Exception();
	}
	
	
	public Model updateModel(JsonObject jsonObject, String id) throws Exception {		
		String name = jsonObject.get("name").getAsString();
		boolean expr_id = jsonObject.get("expr_id").getAsBoolean();
		String condition = jsonObject.get("condition").getAsString();
		Model obj = new Model();
		obj.setId(Integer.parseInt(id));
		obj.setName(name);
		obj.setCondition(condition);
		boolean created = dao.update(obj );
		if(created) return obj;
		throw new Exception();
	}
	
	public boolean deleteExpression (String id) {
		if((id == null) || (id.isEmpty())){
			return false;
		}else{
			Expression expression = new Expression();
			expression.setId(Integer.parseInt(id));
			dao.delete(expression);
			return true;
		}	
	}

}
