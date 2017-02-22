package com.org.gnos.services.controller;

import java.util.List;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.org.gnos.db.dao.GradeConstraintDAO;
import com.org.gnos.db.model.GradeConstraintData;

public class GradeConstraintController {

	private GradeConstraintDAO dao;
	
	public GradeConstraintController() {
		dao = new GradeConstraintDAO();
	}
	
	public List<GradeConstraintData> getAll(String scenarioIdStr) {
		int scenarioId = Integer.parseInt(scenarioIdStr);
		return dao.getAll(scenarioId);
	}
	
	public GradeConstraintData create(JsonObject jsonObject, String scenarioIdStr) throws Exception {
		String productJoinName = jsonObject.get("product_join").getAsString();
		int selectionType = jsonObject.get("selection_ype").getAsInt();
		String selectedGradeName = jsonObject.get("selected_grade").getAsString();
		String selectorName = jsonObject.get("selector_name").getAsString();
		int selectedGradeIndex = jsonObject.get("selected_grade_index").getAsInt();
		boolean inUse = jsonObject.get("in_use").getAsBoolean();
		boolean isMax = jsonObject.get("is_max").getAsBoolean();
		JsonObject constraintDataObj = jsonObject.get("constraintData").getAsJsonObject();
		GradeConstraintData obj = new GradeConstraintData();
		obj.setProductJoinName(productJoinName);
		obj.setSelectedGradeName(selectedGradeName);
		obj.setSelectedGradeIndex(selectedGradeIndex);
		obj.setSelectionType(selectionType);
		obj.setSelectorName(selectorName);
		obj.setInUse(inUse);
		obj.setMax(isMax);
		
		for (Entry<String, JsonElement> constraintData : constraintDataObj.entrySet()) {
			obj.addYear(Integer.parseInt(constraintData.getKey()), constraintData.getValue().getAsFloat());
		}
		boolean created = dao.create(obj, Integer.parseInt(scenarioIdStr));
		if(created) return obj;
		throw new Exception();
	}
	
	
	public GradeConstraintData update(JsonObject jsonObject, String id) throws Exception {		
		String productJoinName = jsonObject.get("product_join").getAsString();
		int selectionType = jsonObject.get("selection_ype").getAsInt();
		String selectedGradeName = jsonObject.get("selected_grade").getAsString();
		String selectorName = jsonObject.get("selector_name").getAsString();
		int selectedGradeIndex = jsonObject.get("selected_grade_index").getAsInt();
		boolean inUse = jsonObject.get("in_use").getAsBoolean();
		boolean isMax = jsonObject.get("is_max").getAsBoolean();
		JsonObject constraintDataObj = jsonObject.get("constraintData").getAsJsonObject();
		GradeConstraintData obj = new GradeConstraintData();
		obj.setId(Integer.parseInt(id));
		obj.setProductJoinName(productJoinName);
		obj.setSelectedGradeName(selectedGradeName);
		obj.setSelectedGradeIndex(selectedGradeIndex);
		obj.setSelectionType(selectionType);
		obj.setSelectorName(selectorName);
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
			GradeConstraintData gcd = new GradeConstraintData();
			gcd.setId(Integer.parseInt(id));
			dao.delete(gcd);
			return true;
		}	
	}
}
