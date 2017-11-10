package com.org.gnos.services.controller;

import java.util.List;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.org.gnos.db.dao.FixedCostDAO;
import com.org.gnos.db.model.FixedOpexCost;

public class FixedCostController {

	private FixedCostDAO dao;
	
	public FixedCostController() {
		dao = new FixedCostDAO();
	}
	
	public List<FixedOpexCost> getAll(String scenarioIdStr) {
		int scenarioId = Integer.parseInt(scenarioIdStr);
		return dao.getAll(scenarioId);
	}
	
	public FixedOpexCost create(JsonObject jsonObject, String scenarioIdStr) throws Exception {
		int costType = jsonObject.get("costType").getAsInt();
		boolean inUse = jsonObject.get("inUse").getAsBoolean();
		boolean isDefault = jsonObject.get("isDefault").getAsBoolean();
		JsonObject costDataObj = jsonObject.get("costData").getAsJsonObject();
		int selectionType = jsonObject.get("selectionType").getAsInt();
		String selectorName = jsonObject.get("selectorName").getAsString();

		FixedOpexCost obj = new FixedOpexCost();
		obj.setCostType(costType);
		obj.setSelectionType(selectionType);
		obj.setSelectorName(selectorName);
		obj.setInUse(inUse);
		obj.setDefault(isDefault);
		
		for (Entry<String, JsonElement> costData : costDataObj.entrySet()) {
			obj.addCostData(Integer.parseInt(costData.getKey()), costData.getValue().getAsBigDecimal());
		}
		boolean created = dao.create(obj, Integer.parseInt(scenarioIdStr));
		if(created) return obj;
		throw new Exception();
	}
	
	
	public FixedOpexCost update(JsonObject jsonObject, String scenarioIdStr) throws Exception {		
		int id = jsonObject.get("id").getAsInt();
		int costType = jsonObject.get("costType").getAsInt();
		boolean inUse = jsonObject.get("inUse").getAsBoolean();
		boolean isDefault = jsonObject.get("isDefault").getAsBoolean();
		int selectionType = jsonObject.get("selectionType").getAsInt();
		String selectorName = null;//since this value can be null for default cost types
		if(jsonObject.get("selectorName") != null){
			selectorName = jsonObject.get("selectorName").getAsString();
		}
		JsonObject costDataObj = jsonObject.get("costData").getAsJsonObject();
		
		FixedOpexCost obj = new FixedOpexCost();
		obj.setId(id);
		obj.setCostType(costType);
		obj.setSelectionType(selectionType);
		obj.setSelectorName(selectorName);
		obj.setInUse(inUse);
		obj.setDefault(isDefault);
		
		for (Entry<String, JsonElement> costData : costDataObj.entrySet()) {
			obj.addCostData(Integer.parseInt(costData.getKey()), costData.getValue().getAsBigDecimal());
		}
		boolean created = dao.update(obj, Integer.parseInt(scenarioIdStr));
		if(created) return obj;
		throw new Exception();
	}
	
	public boolean delete(String scenarioIdStr) {
		dao.delete(Integer.parseInt(scenarioIdStr));
		return true;	
	}
}
