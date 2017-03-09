package com.org.gnos.services.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonObject;
import com.org.gnos.db.dao.ExpressionDAO;
import com.org.gnos.db.dao.FieldDAO;
import com.org.gnos.db.dao.ProductDAO;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Field;
import com.org.gnos.db.model.Grade;
import com.org.gnos.db.model.Product;

public class ProductController {

	ProductDAO dao;
	
	public ProductController() {
		dao = new ProductDAO();
	}
	
	public List<Product> getAll(String projectId) {
		
		return dao.getAll(Integer.parseInt(projectId));
	}
	
	public List<Grade> getAllGrades(String projectIdStr, String productName) {
		int projectId = Integer.parseInt(projectIdStr);
		List<Grade> gradeList = new ArrayList<Grade>();
		Product product = dao.get(projectId, productName);
		Set<Integer> fieldIdList = product.getFieldIdList();
		Set<Integer> expressionIdList = product.getExpressionIdList();
		
		if(fieldIdList != null && fieldIdList.size() > 0) {
			FieldDAO fieldDAO = new FieldDAO();
			List<Field> fields = fieldDAO.getAll(projectId);
			for(Integer fieldId: fieldIdList) {
				Field field = getFieldById(fieldId, fields);
				if(field != null) {
					List<Field> list = fieldDAO.getAllByWeightedUnit(projectId, field.getName());
					for(Field f: list) {
						Grade grade = new Grade();
						grade.setName(f.getName());
						grade.setProductName(productName);
						grade.setType(Grade.GRADE_FIELD);
						grade.setMappedName(f.getName());
						gradeList.add(grade);
					}
				}
			}
		}
		if(expressionIdList != null && expressionIdList.size() > 0) {
			ExpressionDAO expressionDAO = new ExpressionDAO();	
			List<Expression> expressions = expressionDAO.getAll(projectId);
			for(Integer expressionId: expressionIdList) {
				Expression expression = getExpressionById(expressionId, expressions);
				if(expression != null) {
					List<Expression> list = expressionDAO.getAllByWeightedField(projectId, expression.getName());
					for(Expression e: list) {
						Grade grade = new Grade();
						grade.setName(e.getName());
						grade.setProductName(productName);
						grade.setType(Grade.GRADE_EXPRESSION);
						grade.setMappedName(e.getName());
						gradeList.add(grade);
					}
				}
			}
		}
		
		
		return gradeList;
	}
	
	private Expression getExpressionById(Integer expressionId, List<Expression> expressions) {
		return null;
	}

	private Field getFieldById(Integer fieldId, List<Field> fields) {
		return null;
	}

	public Product create(JsonObject jsonObject, String projectId) throws Exception {
		String name = jsonObject.get("name").getAsString();
		int modelId = jsonObject.get("modelId").getAsInt();
		short unitType = jsonObject.get("unitType").getAsShort();
		int unitId =  jsonObject.get("unitId").getAsInt();
		Product obj = new Product();
		obj.setName(name);
		obj.setModelId(modelId);
		if(unitType == Product.UNIT_FIELD) {
			obj.getFieldIdList().add(unitId);
		} else {
			obj.getExpressionIdList().add(unitId);
		}
		
		boolean created = dao.create(obj, Integer.parseInt(projectId));
		if(created) return obj;
		throw new Exception();
	}
	
	
	public boolean deleteAll(String projectId) {
		dao.delete(Integer.parseInt(projectId));
		return true;
	}
	
	public boolean deleteUnit(JsonObject jsonObject, String projectId, String productName) {
		short unitType = jsonObject.get("unitType").getAsShort();
		int unitId =  jsonObject.get("unitId").getAsInt();
		dao.deleteUnit(Integer.parseInt(projectId), productName, unitType, unitId);
		return true;
	}
}
