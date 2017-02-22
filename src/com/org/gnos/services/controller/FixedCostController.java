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
		int costHead = jsonObject.get("costHead").getAsInt();
		JsonObject costDataObj = jsonObject.get("costData").getAsJsonObject();
		FixedOpexCost obj = new FixedOpexCost();
		obj.setCostHead(costHead);
		
		for (Entry<String, JsonElement> costData : costDataObj.entrySet()) {
			obj.addCostData(Integer.parseInt(costData.getKey()), costData.getValue().getAsBigDecimal());
		}
		boolean created = dao.create(obj, Integer.parseInt(scenarioIdStr));
		if(created) return obj;
		throw new Exception();
	}
	
	
	public FixedOpexCost update(JsonObject jsonObject, String scenarioIdStr) throws Exception {		
		int costHead = jsonObject.get("costHead").getAsInt();
		JsonObject costDataObj = jsonObject.get("costData").getAsJsonObject();
		FixedOpexCost obj = new FixedOpexCost();
		obj.setCostHead(costHead);
		
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
