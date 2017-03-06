package com.org.gnos.services.controller;

import java.util.List;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.org.gnos.db.dao.ProcessConstraintDAO;
import com.org.gnos.db.model.ProcessConstraintData;

public class ProcessConstraintController {

	private ProcessConstraintDAO dao;
	
	public ProcessConstraintController() {
		dao = new ProcessConstraintDAO();
	}
	
	public List<ProcessConstraintData> getAll(String scenarioIdStr) {
		int scenarioId = Integer.parseInt(scenarioIdStr);
		return dao.getAll(scenarioId);
	}
	
	public ProcessConstraintData create(JsonObject jsonObject, String scenarioIdStr) throws Exception {
		int coefficientType = jsonObject.get("coefficientType").getAsInt();
		int selectionType = jsonObject.get("selectionType").getAsInt();
		String coefficientName = jsonObject.get("coefficient_name").getAsString();
		String selectorName = jsonObject.get("selector_name").getAsString();
		boolean inUse = jsonObject.get("inUse").getAsBoolean();
		boolean isMax = jsonObject.get("isMax").getAsBoolean();
		JsonObject constraintDataObj = jsonObject.get("constraintData").getAsJsonObject();
		ProcessConstraintData obj = new ProcessConstraintData();
		obj.setCoefficient_name(coefficientName);
		obj.setCoefficientType(coefficientType);
		obj.setSelectionType(selectionType);
		obj.setSelector_name(selectorName);
		obj.setInUse(inUse);
		obj.setMax(isMax);
		
		for (Entry<String, JsonElement> constraintData : constraintDataObj.entrySet()) {
			obj.addYear(Integer.parseInt(constraintData.getKey()), constraintData.getValue().getAsFloat());
		}
		boolean created = dao.create(obj, Integer.parseInt(scenarioIdStr));
		if(created) return obj;
		throw new Exception();
	}
	
	
	public ProcessConstraintData update(JsonObject jsonObject, String id) throws Exception {		
		int coefficientType = jsonObject.get("coefficientType").getAsInt();
		int selectionType = jsonObject.get("selectionType").getAsInt();
		String coefficientName = jsonObject.get("coefficient_name").getAsString();
		String selectorName = jsonObject.get("selector_name").getAsString();
		boolean inUse = jsonObject.get("inUse").getAsBoolean();
		boolean isMax = jsonObject.get("isMax").getAsBoolean();
		JsonObject constraintDataObj = jsonObject.get("constraintData").getAsJsonObject();
		ProcessConstraintData obj = new ProcessConstraintData();
		obj.setId(Integer.parseInt(id));
		obj.setCoefficient_name(coefficientName);
		obj.setCoefficientType(coefficientType);
		obj.setSelectionType(selectionType);
		obj.setSelector_name(selectorName);
		obj.setInUse(inUse);
		obj.setMax(isMax);
		
		for (Entry<String, JsonElement> constraintData : constraintDataObj.entrySet()) {
			obj.addYear(Integer.parseInt(constraintData.getKey()), constraintData.getValue().getAsFloat());
		}
		boolean created = dao.update(obj);
		if(created) return obj;
		throw new Exception();
	}
	
	public boolean delete(String id) {
		if((id == null) || (id.isEmpty())){
			return false;
		}else{
			ProcessConstraintData pcd = new ProcessConstraintData();
			pcd.setId(Integer.parseInt(id));
			dao.delete(pcd);
			return true;
		}	
	}
}
