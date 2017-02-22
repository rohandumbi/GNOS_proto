package com.org.gnos.services.controller;

import java.util.List;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.org.gnos.db.dao.BenchConstraintDAO;
import com.org.gnos.db.model.PitBenchConstraintData;

public class BenchConstraintController {

	private BenchConstraintDAO dao;
	
	public BenchConstraintController() {
		dao = new BenchConstraintDAO();
	}
	
	public List<PitBenchConstraintData> getAll(String scenarioIdStr) {
		int scenarioId = Integer.parseInt(scenarioIdStr);
		return dao.getAll(scenarioId);
	}
	
	public PitBenchConstraintData create(JsonObject jsonObject, String scenarioIdStr) throws Exception {
		String pitName = jsonObject.get("pit_name").getAsString();
		boolean inUse = jsonObject.get("in_use").getAsBoolean();
		JsonObject constraintDataObj = jsonObject.get("constraintData").getAsJsonObject();
		PitBenchConstraintData obj = new PitBenchConstraintData();
		obj.setPitName(pitName);
		obj.setInUse(inUse);
		
		for (Entry<String, JsonElement> constraintData : constraintDataObj.entrySet()) {
			obj.addYear(Integer.parseInt(constraintData.getKey()), constraintData.getValue().getAsInt());
		}
		boolean created = dao.create(obj, Integer.parseInt(scenarioIdStr));
		if(created) return obj;
		throw new Exception();
	}
	
	
	public PitBenchConstraintData update(JsonObject jsonObject, String id) throws Exception {		

		String pitName = jsonObject.get("pit_name").getAsString();
		boolean inUse = jsonObject.get("in_use").getAsBoolean();
		JsonObject constraintDataObj = jsonObject.get("constraintData").getAsJsonObject();
		PitBenchConstraintData obj = new PitBenchConstraintData();
		obj.setId(Integer.parseInt(id));
		obj.setPitName(pitName);
		obj.setInUse(inUse);
		
		for (Entry<String, JsonElement> constraintData : constraintDataObj.entrySet()) {
			obj.addYear(Integer.parseInt(constraintData.getKey()), constraintData.getValue().getAsInt());
		}
		
		boolean created = dao.update(obj);
		if(created) return obj;
		throw new Exception();
	}
	
	public boolean delete(String id) {
		if((id == null) || (id.isEmpty())){
			return false;
		}else{
			PitBenchConstraintData pcd = new PitBenchConstraintData();
			pcd.setId(Integer.parseInt(id));
			dao.delete(pcd);
			return true;
		}	
	}
}
