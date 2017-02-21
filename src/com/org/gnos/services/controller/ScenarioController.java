package com.org.gnos.services.controller;

import java.util.List;

import com.google.gson.JsonObject;
import com.org.gnos.db.dao.ScenarioDAO;
import com.org.gnos.db.model.Scenario;

public class ScenarioController {

	ScenarioDAO dao;
	
	public ScenarioController() {
		dao = new ScenarioDAO();
	}
	public List<Scenario> getAll(String projectIdStr) {
		int projectId = Integer.parseInt(projectIdStr);
		return dao.getAll(projectId);
	}
	
	public Scenario create(JsonObject jsonObject, String pid) throws Exception {
		String name = jsonObject.get("name").getAsString();
		int startYear = jsonObject.get("start_year").getAsInt();
		int timePeriod = jsonObject.get("time_period").getAsInt();
		float discount = jsonObject.get("discount").getAsFloat();
		Scenario obj = new Scenario();
		obj.setName(name);
		obj.setStartYear(startYear);
		obj.setTimePeriod(timePeriod);
		obj.setDiscount(discount);
		boolean created = dao.create(obj, Integer.parseInt(pid));
		if(created) return obj;
		throw new Exception();
	}
	
	
	public Scenario update(JsonObject jsonObject, String id) throws Exception {		
		String name = jsonObject.get("name").getAsString();
		int startYear = jsonObject.get("start_year").getAsInt();
		int timePeriod = jsonObject.get("time_period").getAsInt();
		float discount = jsonObject.get("discount").getAsFloat();
		Scenario obj = new Scenario();
		obj.setId(Integer.parseInt(id));
		obj.setName(name);
		obj.setStartYear(startYear);
		obj.setTimePeriod(timePeriod);
		obj.setDiscount(discount);
		boolean created = dao.update(obj);
		if(created) return obj;
		throw new Exception();
	}
	
	public boolean delete(String id) {
		if((id == null) || (id.isEmpty())){
			return false;
		}else{
			Scenario obj = new Scenario();
			obj.setId(Integer.parseInt(id));
			dao.delete(obj);
			return true;
		}	
	}
}
