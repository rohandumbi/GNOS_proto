package com.org.gnos.services.controller;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.org.gnos.db.dao.CapexDAO;
import com.org.gnos.db.model.CapexData;
import com.org.gnos.db.model.CapexInstance;

public class CapexController {

	private CapexDAO dao;
	
	public CapexController() {
		dao = new CapexDAO();
	}
	
	public List<CapexData> getAll(String scenarioIdStr) {
		int scenarioId = Integer.parseInt(scenarioIdStr);
		return dao.getAll(scenarioId);
	}
	
	public CapexData create(JsonObject jsonObject, String id) throws Exception {
		String name = jsonObject.get("name").getAsString();
		JsonArray instanceArr = jsonObject.get("instances").getAsJsonArray();
		CapexData obj = new CapexData();
		obj.setName(name);
		for(JsonElement elm : instanceArr){
			CapexInstance ci = new CapexInstance();
			JsonObject instanceObj = elm.getAsJsonObject();
			ci.setName(instanceObj.get("name").getAsString());
			ci.setGroupingName(instanceObj.get("group_name").getAsString());
			ci.setGroupingType(instanceObj.get("group_type").getAsInt());
			ci.setCapexAmount(instanceObj.get("capex_amount").getAsLong());
			ci.setExpansionCapacity(instanceObj.get("expansion_capacity").getAsLong());
			obj.addCapexInstance(ci);
		}
		
		boolean created = dao.create(obj, Integer.parseInt(id));
		if(created) return obj;
		throw new Exception();
	}
	
	
	public CapexData update(JsonObject jsonObject, String id) throws Exception {		
		String name = jsonObject.get("name").getAsString();
		JsonArray instanceArr = jsonObject.get("instances").getAsJsonArray();
		CapexData obj = new CapexData();
		obj.setName(name);
		obj.setId(Integer.parseInt(id));
		for(JsonElement elm : instanceArr){
			CapexInstance ci = new CapexInstance();
			JsonObject instanceObj = elm.getAsJsonObject();
			ci.setId(instanceObj.get("name").getAsInt());
			ci.setName(instanceObj.get("name").getAsString());
			ci.setGroupingName(instanceObj.get("group_name").getAsString());
			ci.setGroupingType(instanceObj.get("group_type").getAsInt());
			ci.setCapexAmount(instanceObj.get("capex_amount").getAsLong());
			ci.setExpansionCapacity(instanceObj.get("expansion_capacity").getAsLong());
			obj.addCapexInstance(ci);
		}
		
		boolean updated = dao.update(obj);
		if(updated) return obj;
		throw new Exception();
	}
	
	public boolean delete(String id) {
		if((id == null) || (id.isEmpty())){
			return false;
		}else{
			CapexData obj = new CapexData();
			obj.setId(Integer.parseInt(id));
			dao.delete(obj);
			return true;
		}	
	}
}
