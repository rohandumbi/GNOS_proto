package com.org.gnos.services.controller;

import java.util.List;

import com.google.gson.JsonObject;
import com.org.gnos.db.dao.StockpileDAO;
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Stockpile;

public class StockpileController {

	private StockpileDAO dao;
	
	public StockpileController() {
		dao = new StockpileDAO();
	}
	
	public List<Stockpile> getAll(String projectIdStr) {
		int projectId = Integer.parseInt(projectIdStr);
		return dao.getAll(projectId);
	}
	
	public Stockpile create(JsonObject jsonObject, String pid) throws Exception {
		String name = jsonObject.get("name").getAsString();
		short type = jsonObject.get("type").getAsShort();
		String condition = jsonObject.get("condition").getAsString();
		String mappedTo = jsonObject.get("mapped_to").getAsString();
		int mappingType = jsonObject.get("mapping_type").getAsInt();
		boolean hasCapacity = jsonObject.get("has_capacity").getAsBoolean();
		int capacity = jsonObject.get("capacity").getAsInt();
		boolean isReclaim = jsonObject.get("is_reclaim").getAsBoolean();
		Stockpile obj = new Stockpile();
		obj.setName(name);
		obj.setType(type);
		obj.setCondition(condition);
		obj.setMappedTo(mappedTo);
		obj.setMappingType(mappingType);
		obj.setHasCapacity(hasCapacity);
		obj.setCapacity(capacity);
		obj.setReclaim(isReclaim);
		boolean created = dao.create(obj, Integer.parseInt(pid));
		if(created) return obj;
		throw new Exception();
	}
	
	
	public Stockpile update(JsonObject jsonObject, String id) throws Exception {		
		String name = jsonObject.get("name").getAsString();
		short type = jsonObject.get("type").getAsShort();
		String condition = jsonObject.get("condition").getAsString();
		String mappedTo = jsonObject.get("mapped_to").getAsString();
		int mappingType = jsonObject.get("mapping_type").getAsInt();
		boolean hasCapacity = jsonObject.get("has_capacity").getAsBoolean();
		int capacity = jsonObject.get("capacity").getAsInt();
		boolean isReclaim = jsonObject.get("is_reclaim").getAsBoolean();
		Stockpile obj = new Stockpile();
		obj.setId(Integer.parseInt(id));
		obj.setName(name);
		obj.setType(type);
		obj.setCondition(condition);
		obj.setMappedTo(mappedTo);
		obj.setMappingType(mappingType);
		obj.setHasCapacity(hasCapacity);
		obj.setCapacity(capacity);
		obj.setReclaim(isReclaim);

		boolean created = dao.update(obj);
		if(created) return obj;
		throw new Exception();
	}
	
	public boolean delete(String id) {
		if((id == null) || (id.isEmpty())){
			return false;
		}else{
			Stockpile stockpile = new Stockpile();
			stockpile.setId(Integer.parseInt(id));
			dao.delete(stockpile);
			return true;
		}	
	}
}
