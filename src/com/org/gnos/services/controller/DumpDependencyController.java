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
		if(jsonObject.get("firstPitName") != null){
			obj.setFirstPitName(jsonObject.get("firstPitName").getAsString());
		}
		if(jsonObject.get("firstDumpName") !=null){
			obj.setFirstDumpName(jsonObject.get("firstDumpName").getAsString());
		}
		obj.setDependentDumpName(jsonObject.get("dependentDumpName").getAsString());
		obj.setInUse(jsonObject.get("inUse").getAsBoolean());
		
		boolean created = dao.create(obj, Integer.parseInt(id));
		if(created) return obj;
		throw new Exception();
	}
	
	
	public DumpDependencyData update(JsonObject jsonObject, String id) throws Exception {		
		DumpDependencyData obj = new DumpDependencyData();
		obj.setId(Integer.parseInt(id));
		if(jsonObject.get("firstPitName") != null){
			obj.setFirstPitName(jsonObject.get("firstPitName").getAsString());
		}
		if(jsonObject.get("firstDumpName") !=null){
			obj.setFirstDumpName(jsonObject.get("firstDumpName").getAsString());
		}
		obj.setDependentDumpName(jsonObject.get("dependentDumpName").getAsString());
		obj.setInUse(jsonObject.get("inUse").getAsBoolean());
		
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
