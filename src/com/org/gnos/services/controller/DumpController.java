package com.org.gnos.services.controller;

import java.util.List;

import com.google.gson.JsonObject;
import com.org.gnos.db.dao.DumpDAO;
import com.org.gnos.db.model.Dump;

public class DumpController {

	private DumpDAO dao;
	
	public DumpController() {
		dao = new DumpDAO();
	}
	
	public List<Dump> getAll(String projectIdStr) {
		int projectId = Integer.parseInt(projectIdStr);
		return dao.getAll(projectId);
	}
	
	public Dump create(JsonObject jsonObject, String pid) throws Exception {
		String name = jsonObject.get("name").getAsString();
		short type = jsonObject.get("type").getAsShort();
		String condition = jsonObject.get("condition").getAsString();
		String mappedTo = jsonObject.get("mappedTo").getAsString();
		int mappingType = jsonObject.get("mappingType").getAsInt();
		boolean hasCapacity = jsonObject.get("hasCapacity").getAsBoolean();
		int capacity = jsonObject.get("capacity").getAsInt();
		Dump obj = new Dump();
		obj.setName(name);
		obj.setType(type);
		obj.setCondition(condition);
		obj.setMappedTo(mappedTo);
		obj.setMappingType(mappingType);
		obj.setHasCapacity(hasCapacity);
		obj.setCapacity(capacity);
		boolean created = dao.create(obj, Integer.parseInt(pid));
		if(created) return obj;
		throw new Exception();
	}
	
	
	public Dump update(JsonObject jsonObject, String id) throws Exception {		
		String name = jsonObject.get("name").getAsString();
		short type = jsonObject.get("type").getAsShort();
		String condition = jsonObject.get("condition").getAsString();
		String mappedTo = jsonObject.get("mappedTo").getAsString();
		int mappingType = jsonObject.get("mappingType").getAsInt();
		boolean hasCapacity = jsonObject.get("hasCapacity").getAsBoolean();
		int capacity = jsonObject.get("capacity").getAsInt();
		Dump obj = new Dump();
		obj.setId(Integer.parseInt(id));
		obj.setName(name);
		obj.setType(type);
		obj.setCondition(condition);
		obj.setMappedTo(mappedTo);
		obj.setMappingType(mappingType);
		obj.setHasCapacity(hasCapacity);
		obj.setCapacity(capacity);
		boolean created = dao.update(obj);
		if(created) return obj;
		throw new Exception();
	}
	
	public boolean delete(String id) {
		if((id == null) || (id.isEmpty())){
			return false;
		}else{
			Dump dump = new Dump();
			dump.setId(Integer.parseInt(id));
			dao.delete(dump);
			return true;
		}	
	}
}
