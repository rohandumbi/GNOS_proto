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
	
	public List<Expression> getAllExpressions(String projectIdStr) {
		int projectId = Integer.parseInt(projectIdStr);
		return dao.getAll(projectId);
	}
	
	public Expression createExpression(JsonObject jsonObject, String pid) throws Exception {
		String name = jsonObject.get("name").getAsString();
		boolean isGrade = jsonObject.get("is_grade").getAsBoolean();
		boolean isComplex = jsonObject.get("is_complex").getAsBoolean();
		String exprvalue = jsonObject.get("expr_value").getAsString();
		String filter = jsonObject.get("filter").getAsString();
		Expression obj = new Expression();
		obj.setName(name);
		obj.setGrade(isGrade);
		obj.setComplex(isComplex);
		obj.setExprvalue(exprvalue);
		obj.setFilter(filter);	
		boolean created = dao.create(obj, Integer.parseInt(pid));
		if(created) return obj;
		throw new Exception();
	}
	
	
	public Expression updateExpression(JsonObject jsonObject, String id) throws Exception {		
		String name = jsonObject.get("name").getAsString();
		boolean isGrade = jsonObject.get("is_grade").getAsBoolean();
		boolean isComplex = jsonObject.get("is_complex").getAsBoolean();
		String exprvalue = jsonObject.get("expr_value").getAsString();
		String filter = jsonObject.get("filter").getAsString();
		Expression obj = new Expression();
		obj.setId(Integer.parseInt(id));
		obj.setName(name);
		obj.setGrade(isGrade);
		obj.setComplex(isComplex);
		obj.setExprvalue(exprvalue);
		obj.setFilter(filter);	
		boolean created = dao.update(obj);
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
