package com.org.gnos.services.controller;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.org.gnos.db.dao.ExpressionDAO;
import com.org.gnos.db.model.Expression;
import com.org.gnos.services.util.ExpressionProcessor;

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
		String weightedField = null;
		short weightedFieldType = Expression.UNIT_NONE;
		String filter = null;
		if(jsonObject.get("filter") != null){
			filter = jsonObject.get("filter").getAsString();
		}
		if(jsonObject.get("weightedField") != null){
			weightedField = jsonObject.get("weightedField").getAsString();
			weightedFieldType = jsonObject.get("weightedFieldType").getAsShort();
		}
		Expression obj = new Expression();
		obj.setName(name);
		obj.setGrade(isGrade);
		obj.setComplex(isComplex);
		obj.setExprvalue(exprvalue);
		obj.setFilter(filter);
		obj.setWeightedField(weightedField);
		obj.setWeightedFieldType(weightedFieldType);
		boolean created = dao.create(obj, Integer.parseInt(pid));
		if(created){
			List<Expression> expressions = new ArrayList<Expression>();
			expressions.add(obj);
			ExpressionProcessor processor = new ExpressionProcessor();
			processor.setExpressions(expressions);
			processor.store(Integer.parseInt(pid));
			return obj;
		}
		throw new Exception();
	}
	
	
	public Expression update(JsonObject jsonObject, String projectId, String eid) throws Exception {		
		String name = jsonObject.get("name").getAsString();
		boolean isGrade = jsonObject.get("isGrade").getAsBoolean();
		boolean isComplex = jsonObject.get("isComplex").getAsBoolean();
		String exprvalue = jsonObject.get("exprvalue").getAsString();
		String weightedField = null;
		short weightedFieldType = Expression.UNIT_NONE;
		String filter = null;
		if(jsonObject.get("filter") != null){
			filter = jsonObject.get("filter").getAsString();
		}
		if(jsonObject.get("weightedField") != null){
			weightedField = jsonObject.get("weightedField").getAsString();
			weightedFieldType = jsonObject.get("weightedFieldType").getAsShort();
		}
		int id = Integer.parseInt(eid);
		Expression obj = new Expression();
		obj.setId(id);
		obj.setName(name);
		obj.setGrade(isGrade);
		obj.setComplex(isComplex);
		obj.setExprvalue(exprvalue);
		obj.setFilter(filter);
		obj.setWeightedField(weightedField);
		obj.setWeightedFieldType(weightedFieldType);
		boolean updated = dao.update(obj);
		if(updated){
			List<Expression> expressions = new ArrayList<Expression>();
			expressions.add(obj);
			ExpressionProcessor processor = new ExpressionProcessor();
			processor.setExpressions(expressions);
			processor.store(Integer.parseInt(projectId));
			return obj;
		}

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
