package com.org.gnos.services.controller;

import java.util.List;

import com.google.gson.JsonObject;
import com.org.gnos.db.dao.FieldDAO;
import com.org.gnos.db.model.Field;

public class FieldController {
	
	FieldDAO dao; 
	public FieldController() {
		dao = new FieldDAO();
	}
	
	public List<Field> getAllFields(String projectId) {
		return dao.getAll(Integer.parseInt(projectId));
	}
	
	public Field createField(JsonObject jsonObject, String pid) throws Exception {
		String name = jsonObject.get("name").getAsString();
		short dataType = jsonObject.get("data_type").getAsShort();
		Field obj = new Field(name);
		obj.setDataType(dataType);

		boolean created = dao.create(obj, Integer.parseInt(pid));
		if(created) return obj;
		throw new Exception();
	}
	
	
	public Field updateField(JsonObject jsonObject, String id) throws Exception {		
		String name = jsonObject.get("name").getAsString();
		short dataType = jsonObject.get("data_type").getAsShort();
		Field obj = new Field(name);
		obj.setId(Integer.parseInt(id));
		obj.setDataType(dataType);
		boolean created = dao.update(obj);
		if(created) return obj;
		throw new Exception();
	}
	
	public boolean deleteField (String id) {
		if((id == null) || (id.isEmpty())){
			return false;
		}else{
			Field obj = new Field();
			obj.setId(Integer.parseInt(id));
			dao.delete(obj);
			return true;
		}	
	}
}
