package com.org.gnos.services.controller;

import java.util.List;

import com.google.gson.JsonObject;
import com.org.gnos.db.dao.DumpDependencyDAO;
import com.org.gnos.db.model.DumpDependencyData;

public class DumpDependencyController {

	private DumpDependencyDAO dao;
	
	public DumpDependencyController() {
		dao = new DumpDependencyDAO();
	}
	
	public List<DumpDependencyData> getAll(String scenarioIdStr) {
		int scenarioId = Integer.parseInt(scenarioIdStr);
		return dao.getAll(scenarioId);
	}
	
	public DumpDependencyData create(JsonObject jsonObject, String id) throws Exception {
		DumpDependencyData obj = new DumpDependencyData();
		obj.setFirstPitName(jsonObject.get("first_pit_name").getAsString());
		obj.setFirstDumpName(jsonObject.get("first_dump_name").getAsString());
		obj.setDependentDumpName(jsonObject.get("dependent_dump_name").getAsString());
		obj.setInUse(jsonObject.get("in_use").getAsBoolean());
		
		boolean created = dao.create(obj, Integer.parseInt(id));
		if(created) return obj;
		throw new Exception();
	}
	
	
	public DumpDependencyData update(JsonObject jsonObject, String id) throws Exception {		
		DumpDependencyData obj = new DumpDependencyData();
		obj.setId(Integer.parseInt(id));
		obj.setFirstPitName(jsonObject.get("first_pit_name").getAsString());
		obj.setFirstDumpName(jsonObject.get("first_dump_name").getAsString());
		obj.setDependentDumpName(jsonObject.get("dependent_dump_name").getAsString());
		obj.setInUse(jsonObject.get("in_use").getAsBoolean());
		
		boolean updated = dao.update(obj);
		if(updated) return obj;
		throw new Exception();
	}
	
	public boolean delete(String id) {
		if((id == null) || (id.isEmpty())){
			return false;
		}else{
			DumpDependencyData obj = new DumpDependencyData();
			obj.setId(Integer.parseInt(id));
			dao.delete(obj);
			return true;
		}	
	}
}
