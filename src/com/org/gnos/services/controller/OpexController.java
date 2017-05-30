package com.org.gnos.services.controller;

import java.util.List;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.org.gnos.db.dao.OpexDAO;
import com.org.gnos.db.model.Model;
import com.org.gnos.db.model.OpexData;

public class OpexController {

	private OpexDAO dao;
	
	public OpexController() {
		dao = new OpexDAO();
	}
	
	public List<OpexData> getAll(String scenarioIdStr) {
		int scenarioId = Integer.parseInt(scenarioIdStr);
		return dao.getAll(scenarioId);
	}
	
	public OpexData create(JsonObject jsonObject, String scenarioIdStr) throws Exception {
		int modelId = -1;
		String productJoinName = "";
		if(!jsonObject.get("modelId").isJsonNull()) {
			modelId = jsonObject.get("modelId").getAsInt();
		} else if(!jsonObject.get("productJoinName").isJsonNull()) {
			productJoinName = jsonObject.get("productJoinName").getAsString();
		}
		short unitType = jsonObject.get("unitType").getAsShort();
		int unitId = jsonObject.get("unitId").getAsInt();
		boolean inUse = jsonObject.get("inUse").getAsBoolean();
		boolean isRevenue = jsonObject.get("isRevenue").getAsBoolean();
		JsonObject costDataObj = jsonObject.get("costData").getAsJsonObject();
		OpexData obj = new OpexData();
		obj.setModelId(modelId);
		obj.setProductJoinName(productJoinName);
		obj.setInUse(inUse);
		obj.setRevenue(isRevenue);
		//obj.setUnitType(unitType);
		
		if(unitType == Model.UNIT_FIELD) {
			obj.setFieldId(unitId);
		} else {
			obj.setExpressionId(unitId);
		}
		
		for (Entry<String, JsonElement> costData : costDataObj.entrySet()) {
			obj.addYear(Integer.parseInt(costData.getKey()), costData.getValue().getAsBigDecimal());
		}
		boolean created = dao.create(obj, Integer.parseInt(scenarioIdStr));
		if(created) return obj;
		throw new Exception();
	}
	
	
	public OpexData update(JsonObject jsonObject, String id) throws Exception {		
		int modelId = -1;
		String productJoinName = "";
		if(!jsonObject.get("modelId").isJsonNull()) {
			modelId = jsonObject.get("modelId").getAsInt();
		} else if(!jsonObject.get("productJoinName").isJsonNull()) {
			productJoinName = jsonObject.get("productJoinName").getAsString();
		}
		short unitType = jsonObject.get("unitType").getAsShort();
		int unitId = jsonObject.get("unitId").getAsInt();
		boolean inUse = jsonObject.get("inUse").getAsBoolean();
		boolean isRevenue = jsonObject.get("isRevenue").getAsBoolean();
		JsonObject costDataObj = jsonObject.get("costData").getAsJsonObject();
		OpexData obj = new OpexData();
		obj.setId(Integer.parseInt(id));
		obj.setModelId(modelId);
		obj.setProductJoinName(productJoinName);
		obj.setInUse(inUse);
		obj.setRevenue(isRevenue);
		obj.setUnitType(unitType);
		
		if(unitType == Model.UNIT_FIELD) {
			obj.setFieldId(unitId);
		} else {
			obj.setExpressionId(unitId);
		}
		
		for (Entry<String, JsonElement> costData : costDataObj.entrySet()) {
			obj.addYear(Integer.parseInt(costData.getKey()), costData.getValue().getAsBigDecimal());
		}
		boolean created = dao.update(obj);
		if(created) return obj;
		throw new Exception();
	}
	
	public boolean delete(String id) {
		if((id == null) || (id.isEmpty())){
			return false;
		}else{
			OpexData opexData = new OpexData();
			opexData.setId(Integer.parseInt(id));
			dao.delete(opexData);
			return true;
		}	
	}
}
