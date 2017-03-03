package com.org.gnos.services.controller;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.JsonObject;
import com.org.gnos.db.dao.TruckParameterCycleTimeDAO;
import com.org.gnos.db.model.TruckParameterCycleTime;

public class TruckParameterCycleTimeController {

	TruckParameterCycleTimeDAO dao;
	
	public TruckParameterCycleTimeController() {
		dao = new TruckParameterCycleTimeDAO();
	}
	
	public List<TruckParameterCycleTime> getAll(String projectId) {
		return dao.getAll(Integer.parseInt(projectId));
	}
	
	public TruckParameterCycleTime create(JsonObject jsonObject, String projectId) throws Exception {
			String stockpileName = jsonObject.get("stockpileName").getAsString();
			String processName = jsonObject.get("processName").getAsString();
			BigDecimal value = jsonObject.get("value").getAsBigDecimal();
			TruckParameterCycleTime obj = new TruckParameterCycleTime();
			obj.setStockPileName(stockpileName);
			obj.getProcessData().put(processName, value);

			boolean created = dao.create(obj, Integer.parseInt(projectId));
			if(created) return obj;
			throw new Exception();
	}
	
	public TruckParameterCycleTime update(JsonObject jsonObject, String projectId) throws Exception {
		String stockpileName = jsonObject.get("stockpileName").getAsString();
		String processName = jsonObject.get("processName").getAsString();
		BigDecimal value = jsonObject.get("value").getAsBigDecimal();
		TruckParameterCycleTime obj = new TruckParameterCycleTime();
		obj.setStockPileName(stockpileName);
		obj.getProcessData().put(processName, value);

		boolean created = dao.update(obj, Integer.parseInt(projectId));
		if(created) return obj;
		throw new Exception();
	}
	
	public boolean delete(String projectId) {
		if((projectId == null) || (projectId.isEmpty())){
			return false;
		}else{
			dao.delete(Integer.parseInt(projectId));
			return true;
		}	
	}
	
	
}
