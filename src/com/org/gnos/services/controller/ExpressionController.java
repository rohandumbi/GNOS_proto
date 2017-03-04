package com.org.gnos.services.controller;

import java.util.List;

import com.google.gson.JsonObject;
import com.org.gnos.db.dao.ExpressionDAO;
import com.org.gnos.db.model.Expression;

public class ExpressionController {

	private ExpressionDAO dao = null;
	
	public ExpressionController() {
		dao = new ExpressionDAO();
	}
	
	public List<Expression> getAll(String projectIdStr) {
		int projectId = Integer.parseInt(projectIdStr);
		return dao.getAll(projectId);
	}
	
	public Expression create(JsonObject jsonObject, String pid) throws Exception {
		String name = jsonObject.get("name").getAsString();
		boolean isGrade = jsonObject.get("isGrade").getAsBoolean();
		boolean isComplex = jsonObject.get("isComplex").getAsBoolean();
		String exprvalue = jsonObject.get("exprvalue").getAsString();
		String weightedField = jsonObject.get("weightedField").getAsString();
		String filter = null;
		if(jsonObject.get("filter") != null){
			filter = jsonObject.get("filter").getAsString();
		}
		Expression obj = new Expression();
		obj.setName(name);
		obj.setGrade(isGrade);
		obj.setComplex(isComplex);
		obj.setExprvalue(exprvalue);
		obj.setFilter(filter);
		obj.setWeightedField(weightedField);
		boolean created = dao.create(obj, Integer.parseInt(pid));
		if(created) return obj;
		throw new Exception();
	}
	
	
	public Expression update(JsonObject jsonObject, String projectId) throws Exception {		
		String name = jsonObject.get("name").getAsString();
		boolean isGrade = jsonObject.get("isGrade").getAsBoolean();
		boolean isComplex = jsonObject.get("isComplex").getAsBoolean();
		String exprvalue = jsonObject.get("exprvalue").getAsString();
		String weightedField = jsonObject.get("weightedField").getAsString();
		String filter = null;
		if(jsonObject.get("filter") != null){
			filter = jsonObject.get("filter").getAsString();
		}
		int id = jsonObject.get("id").getAsInt();
		Expression obj = new Expression();
		obj.setId(id);
		obj.setName(name);
		obj.setGrade(isGrade);
		obj.setComplex(isComplex);
		obj.setExprvalue(exprvalue);
		obj.setFilter(filter);
		obj.setWeightedField(weightedField);
		boolean created = dao.update(obj);
		if(created) return obj;
		throw new Exception();
	}
	
	public boolean delete(String id) {
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
