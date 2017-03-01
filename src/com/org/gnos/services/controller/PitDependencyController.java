package com.org.gnos.services.controller;

import java.util.List;

import com.google.gson.JsonObject;
import com.org.gnos.db.dao.PitDependencyDAO;
import com.org.gnos.db.model.PitDependencyData;

public class PitDependencyController {

	private PitDependencyDAO dao;
	
	public PitDependencyController() {
		dao = new PitDependencyDAO();
	}
	
	public List<PitDependencyData> getAll(String scenarioIdStr) {
		int scenarioId = Integer.parseInt(scenarioIdStr);
		return dao.getAll(scenarioId);
	}
	
	public PitDependencyData create(JsonObject jsonObject, String id) throws Exception {
		PitDependencyData obj = new PitDependencyData();
		obj.setFirstPitName(jsonObject.get("firstPitName").getAsString());
		if(jsonObject.get("firstPitAssociatedBench") != null){
			obj.setFirstPitAssociatedBench(jsonObject.get("firstPitAssociatedBench").getAsString());
		}
		obj.setDependentPitName(jsonObject.get("dependentPitName").getAsString());
		if(jsonObject.get("dependentPitAssociatedBench") != null){
			obj.setDependentPitAssociatedBench(jsonObject.get("dependentPitAssociatedBench").getAsString());
		}
		obj.setMaxLead(jsonObject.get("maxLead").getAsInt());
		obj.setMinLead(jsonObject.get("minLead").getAsInt());
		obj.setInUse(jsonObject.get("inUse").getAsBoolean());
		boolean created = dao.create(obj, Integer.parseInt(id));
		if(created) return obj;
		throw new Exception();
	}
	
	
	public PitDependencyData update(JsonObject jsonObject, String id) throws Exception {		
		PitDependencyData obj = new PitDependencyData();
		obj.setId(Integer.parseInt(id));
		obj.setFirstPitName(jsonObject.get("firstPitName").getAsString());
		if(jsonObject.get("firstPitAssociatedBench") != null){
			obj.setFirstPitAssociatedBench(jsonObject.get("firstPitAssociatedBench").getAsString());
		}
		obj.setDependentPitName(jsonObject.get("dependentPitName").getAsString());
		if(jsonObject.get("dependentPitAssociatedBench") != null){
			obj.setDependentPitAssociatedBench(jsonObject.get("dependentPitAssociatedBench").getAsString());
		}
		obj.setMaxLead(jsonObject.get("maxLead").getAsInt());
		obj.setMinLead(jsonObject.get("minLead").getAsInt());
		obj.setInUse(jsonObject.get("inUse").getAsBoolean());
		boolean updated = dao.update(obj);
		if(updated) return obj;
		throw new Exception();
	}
	
	public boolean delete(String id) {
		if((id == null) || (id.isEmpty())){
			return false;
		}else{
			PitDependencyData obj = new PitDependencyData();
			obj.setId(Integer.parseInt(id));
			dao.delete(obj);
			return true;
		}	
	}
}
