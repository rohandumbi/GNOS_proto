package com.org.gnos.services.controller;

import java.util.List;

import com.google.gson.JsonObject;
import com.org.gnos.db.dao.RequiredFieldDAO;
import com.org.gnos.db.model.RequiredField;
import com.org.gnos.services.util.PitBenchProcessor;

public class RequiredFieldController {

	RequiredFieldDAO dao;
	
	public RequiredFieldController() {
		dao = new RequiredFieldDAO();
	}
	
	public List<RequiredField> getAll(String projectId) {
		return dao.getAll(Integer.parseInt(projectId));
	}
	
	public RequiredField create(JsonObject jsonObject, String pid) throws Exception{
		String fieldName = jsonObject.get("fieldName").getAsString();
		String mappedFieldname = jsonObject.get("mappedFieldname").getAsString();
		RequiredField obj = new RequiredField();
		obj.setFieldName(fieldName);
		obj.setMappedFieldname(mappedFieldname);
		boolean created = dao.create(obj, Integer.parseInt(pid));
		if(created) return obj;
		throw new Exception();
	}
	
	public RequiredField update(JsonObject jsonObject, String pid) throws Exception{
		String fieldName = jsonObject.get("fieldName").getAsString();
		String mappedFieldname = jsonObject.get("mappedFieldname").getAsString();
		RequiredField obj = new RequiredField();
		obj.setFieldName(fieldName);
		obj.setMappedFieldname(mappedFieldname);
		boolean updated = dao.update(obj, Integer.parseInt(pid));
		new PitBenchProcessor().updatePitBenchData(Integer.parseInt(pid));
		if(updated) return obj;
		throw new Exception();
	}
	
	public boolean delete(String id) {
		dao.delete(Integer.parseInt(id));
		return true;	
	}
}
