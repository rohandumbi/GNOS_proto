package com.org.gnos.services.controller;

import java.util.List;

import com.google.gson.JsonObject;
import com.org.gnos.db.dao.PitGroupDAO;
import com.org.gnos.db.model.PitGroup;

public class PitGroupController {

	PitGroupDAO dao;
	
	public PitGroupController() {
		dao = new PitGroupDAO();
	}
	
	public List<PitGroup> getAll(String projectId) {
		
		return dao.getAll(Integer.parseInt(projectId));
	}
	
	public PitGroup create(JsonObject jsonObject, String projectId) throws Exception {
		String name = jsonObject.get("name").getAsString();
		short childType = jsonObject.get("childType").getAsShort();
		String child =  jsonObject.get("child").getAsString();
		PitGroup obj = new PitGroup();
		obj.setName(name);
		if(childType == PitGroup.CHILD_PIT) {
			obj.addPit(child);
		} else if(childType == PitGroup.CHILD_PIT_GROUP) {
			obj.addPitGroup(child);
		}
		boolean created = dao.create(obj, Integer.parseInt(projectId));
		if(created) return obj;
		throw new Exception();
	}
	
	
	public boolean deleteAll(String projectId) {
		dao.deleteAll(Integer.parseInt(projectId));
		return true;
	}
	
	public boolean deletePit(String projectId, String name, String pitName) {	
		dao.deletePit(Integer.parseInt(projectId), name, pitName);;
		return true;
	}
	
	public boolean deletePitGroup(String projectId, String name, String pitGroupname) {
		dao.deletePitGroup(Integer.parseInt(projectId), name, pitGroupname);
		return true;
	}
}
