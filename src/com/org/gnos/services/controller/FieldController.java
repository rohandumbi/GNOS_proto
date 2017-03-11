package com.org.gnos.services.controller;

import java.util.List;

import com.google.gson.JsonObject;
import com.org.gnos.db.dao.FieldDAO;
import com.org.gnos.db.model.CycletimeField;
import com.org.gnos.db.model.Field;

public class FieldController {
	
	FieldDAO dao; 
	public FieldController() {
		dao = new FieldDAO();
	}
	
	public List<Field> getAll(String projectId, String datatype) {
		if(datatype != null && datatype.trim().length() > 0) {
			return dao.getAllByType(Integer.parseInt(projectId), Short.parseShort(datatype));
		} else {
			return dao.getAll(Integer.parseInt(projectId));
		}
		
	}
	
	public List<CycletimeField> getAllCycletimeFields(String projectId) {
		return dao.getAllCycletimeFields(Integer.parseInt(projectId));		
	}
	
	public Field create(JsonObject jsonObject, String pid) throws Exception {
		String name = jsonObject.get("name").getAsString();
		short dataType = jsonObject.get("dataType").getAsShort();
		String weightedUnit = jsonObject.get("weightedUnit").getAsString();
		Field obj = new Field(name);
		obj.setDataType(dataType);
		obj.setWeightedUnit(weightedUnit);
		boolean created = dao.create(obj, Integer.parseInt(pid));
		if(created) return obj;
		throw new Exception();
	}
	
	
	public Field update(JsonObject jsonObject, String id) throws Exception {		
		String name = jsonObject.get("name").getAsString();
		short dataType = jsonObject.get("dataType").getAsShort();
		int fieldId = jsonObject.get("id").getAsInt();
		String weightedUnit = null;
		if(jsonObject.get("weightedUnit") != null){
			weightedUnit = jsonObject.get("weightedUnit").getAsString();
		}
		Field obj = new Field(name);
		obj.setId(fieldId);
		obj.setDataType(dataType);
		obj.setWeightedUnit(weightedUnit);
		boolean created = dao.update(obj);
		if(created) return obj;
		throw new Exception();
	}
	
	public boolean delete(String id) {
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
