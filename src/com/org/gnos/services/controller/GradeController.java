package com.org.gnos.services.controller;

import java.util.List;

import com.google.gson.JsonObject;
import com.org.gnos.db.dao.GradeDAO;
import com.org.gnos.db.model.Grade;

public class GradeController {

	GradeDAO dao;
	
	public GradeController() {
		dao = new GradeDAO();
	}
	
	public List<Grade> getAll(String projectId) {
		
		return dao.getAll(Integer.parseInt(projectId));
	}
	
	public Grade create(JsonObject jsonObject, String projectId) throws Exception {
		String productName = jsonObject.get("productName").getAsString();
		short type = jsonObject.get("type").getAsShort();
		String mappedName =  jsonObject.get("mappedName").getAsString();
		Grade obj = new Grade();
		obj.setProductName(productName);
		obj.setType(type);
		obj.setMappedName(mappedName);
		boolean created = dao.create(obj, Integer.parseInt(projectId));
		if(created) return obj;
		throw new Exception();
	}
	
	
	public boolean deleteAll(String id) {
		dao.delete(Integer.parseInt(id));
		return true;
	}
}
