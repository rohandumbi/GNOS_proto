package com.org.gnos.services.controller;

import java.util.List;

import com.google.gson.JsonObject;
import com.org.gnos.db.dao.ProductJoinDAO;
import com.org.gnos.db.model.ProductJoin;

public class ProductJoinController {

	ProductJoinDAO dao;
	
	public ProductJoinController() {
		dao = new ProductJoinDAO();
	}
	
	public List<ProductJoin> getAll(String projectId) {
		
		return dao.getAll(Integer.parseInt(projectId));
	}
	
	public ProductJoin create(JsonObject jsonObject, String projectId) throws Exception {
		String name = jsonObject.get("name").getAsString();
		short childType = jsonObject.get("childType").getAsShort();
		String child =  jsonObject.get("child").getAsString();
		ProductJoin obj = new ProductJoin();
		obj.setName(name);
		if(childType == ProductJoin.CHILD_PRODUCT) {
			obj.getProductList().add(child);
		} else if(childType == ProductJoin.CHILD_PRODUCT_JOIN) {
			obj.getProductJoinList().add(child);
		}
		boolean created = dao.create(obj, Integer.parseInt(projectId));
		if(created) return obj;
		throw new Exception();
	}
	
	
	public boolean deleteAll(String projectId, String name) {
		dao.deleteAll(Integer.parseInt(projectId), name);
		return true;
	}
	
	public boolean deleteProduct(String projectId, String name, String productName) {	
		dao.deleteProduct(Integer.parseInt(projectId), name, productName);;
		return true;
	}
	
	public boolean deleteProductJoin(String projectId, String name, String productJoinName) {
		dao.deleteProductJoin(Integer.parseInt(projectId), name, productJoinName);
		return true;
	}
}
