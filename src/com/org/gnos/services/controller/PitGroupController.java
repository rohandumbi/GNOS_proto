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
		
		return dao.getAll();
	}
	
	public PitGroup create(JsonObject jsonObject, String projectId) {
		
		return null;
	}
	
	public PitGroup update(JsonObject jsonObject, String id) {
		
		return null;
	}
	
	public boolean delete(String id) {
		return true;
	}
}
