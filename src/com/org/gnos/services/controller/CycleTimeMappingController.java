package com.org.gnos.services.controller;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.JsonObject;
import com.org.gnos.db.dao.CycleFixedTimeDAO;
import com.org.gnos.db.dao.CycleTimeFieldMappingDAO;
import com.org.gnos.db.model.CycleTimeFieldMapping;

public class CycleTimeMappingController {
	
	CycleTimeFieldMappingDAO dao; 
	CycleFixedTimeDAO fdao;
	
	public CycleTimeMappingController() {
		dao = new CycleTimeFieldMappingDAO();
		fdao = new CycleFixedTimeDAO();
	}
	
	public List<CycleTimeFieldMapping> getAll(String projectId) {
		return dao.getAll(Integer.parseInt(projectId));
	}
	
	public CycleTimeFieldMapping create(JsonObject jsonObject, String pid) throws Exception {
		String fieldName = jsonObject.get("fieldName").getAsString();
		short mappingType = jsonObject.get("mappingType").getAsShort();
		String mappedFieldName = jsonObject.get("mappedFieldName").getAsString();
		CycleTimeFieldMapping obj = new CycleTimeFieldMapping();
		obj.setFieldName(fieldName);
		obj.setMappingType(mappingType);
		obj.setMappedFieldName(mappedFieldName);
		boolean created = dao.create(obj, Integer.parseInt(pid));
		if(created) return obj;
		throw new Exception();
	}
	
	
	public CycleTimeFieldMapping update(JsonObject jsonObject, String id) throws Exception {		
		String fieldName = jsonObject.get("fieldName").getAsString();
		short mappingType = jsonObject.get("mappingType").getAsShort();
		String mappedFieldName = jsonObject.get("mappedFieldName").getAsString();
		CycleTimeFieldMapping obj = new CycleTimeFieldMapping();
		obj.setFieldName(fieldName);
		obj.setMappingType(mappingType);
		obj.setMappedFieldName(mappedFieldName);
		boolean created = dao.update(obj, Integer.parseInt(id));
		if(created) return obj;
		throw new Exception();
	}
	
	public boolean deleteAll(String id) {
		if((id == null) || (id.isEmpty())){
			return false;
		}else{
			dao.deleteAll(Integer.parseInt(id));
			return true;
		}	
	}
	
	public boolean delete(String id, String fieldName, short mappingType) {
		if((id == null) || (id.isEmpty())){
			return false;
		}else{
			CycleTimeFieldMapping obj = new CycleTimeFieldMapping();
			obj.setFieldName(fieldName);
			obj.setMappingType(mappingType);
			dao.delete(obj, Integer.parseInt(id));
			return true;
		}	
	}

	public BigDecimal getFixedTime(String id) {
		return fdao.getAll(Integer.parseInt(id));
	}

	public boolean createFixedTime(String id, String fixedTime) {
		return fdao.create(Integer.parseInt(id), new BigDecimal(fixedTime));
	}

	public boolean updateFixedTime(String id, String fixedTime) {
		return fdao.update(Integer.parseInt(id), new BigDecimal(fixedTime));
	}

	public boolean deleteFixedTime(String id) {
		return fdao.delete(Integer.parseInt(id));
	}
}
