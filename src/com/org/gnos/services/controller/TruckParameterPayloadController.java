package com.org.gnos.services.controller;

import java.util.List;

import com.google.gson.JsonObject;
import com.org.gnos.db.dao.TruckParameterPayloadDAO;
import com.org.gnos.db.model.TruckParameterPayload;

public class TruckParameterPayloadController {

	TruckParameterPayloadDAO dao;
	
	public TruckParameterPayloadController() {
		dao = new TruckParameterPayloadDAO();
	}
	
	public List<TruckParameterPayload> getAll(String projectId) {
		return dao.getAll(Integer.parseInt(projectId));
	}
	
	public TruckParameterPayload create(JsonObject jsonObject, String projectId) throws Exception {
		String materialName = jsonObject.get("materialName").getAsString();
		int payload = jsonObject.get("payload").getAsInt();
		TruckParameterPayload obj = new TruckParameterPayload();
		obj.setMaterialName(materialName);
		obj.setPayload(payload);

		boolean created = dao.create(obj, Integer.parseInt(projectId));
		if(created) return obj;
		throw new Exception();
	}
	
	public TruckParameterPayload update(JsonObject jsonObject, String projectId) throws Exception {
		String materialName = jsonObject.get("materialName").getAsString();
		int payload = jsonObject.get("payload").getAsInt();
		TruckParameterPayload obj = new TruckParameterPayload();
		obj.setMaterialName(materialName);
		obj.setPayload(payload);

		boolean created = dao.update(obj, Integer.parseInt(projectId));
		if(created) return obj;
		throw new Exception();
	}
	
	public boolean deleteAll(String projectId) {
		if((projectId == null) || (projectId.isEmpty())){
			return false;
		}else{
			dao.deleteAll(Integer.parseInt(projectId));
			return true;
		}	
	}
	
	public boolean delete(String projectId, String materialName) {
		if((projectId == null) || (projectId.isEmpty())){
			return false;
		}else{
			dao.delete(Integer.parseInt(projectId), materialName);
			return true;
		}	
	}
	
}
