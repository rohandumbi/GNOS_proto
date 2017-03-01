package com.org.gnos.services.controller;

import java.util.List;

import com.google.gson.JsonObject;
import com.org.gnos.db.dao.ProductDAO;
import com.org.gnos.db.model.Product;

public class ProductController {

	ProductDAO dao;
	
	public ProductController() {
		dao = new ProductDAO();
	}
	
	public List<Product> getAll(String projectId) {
		
		return dao.getAll(Integer.parseInt(projectId));
	}
	
	public Product create(JsonObject jsonObject, String projectId) throws Exception {
		String name = jsonObject.get("name").getAsString();
		int modelId = jsonObject.get("modelId").getAsInt();
		int expressionId =  jsonObject.get("expressionId").getAsInt();
		Product obj = new Product();
		obj.setName(name);
		obj.setModelId(modelId);
		obj.getExpressionIdList().add(expressionId);
		boolean created = dao.create(obj, Integer.parseInt(projectId));
		if(created) return obj;
		throw new Exception();
	}
	
	
	public boolean deleteAll(String projectId) {
		dao.delete(Integer.parseInt(projectId));
		return true;
	}
}
