package com.org.gnos.services.controller;

import java.util.List;

import com.google.gson.JsonObject;
import com.org.gnos.db.dao.ProcessJoinDAO;
import com.org.gnos.db.model.ProcessJoin;

public class ProcessJoinController {

	ProcessJoinDAO dao;
	
	public ProcessJoinController() {
		dao = new ProcessJoinDAO();
	}
	
	public List<ProcessJoin> getAll(String projectId) {
		
		return dao.getAll(Integer.parseInt(projectId));
	}
	
	public ProcessJoin create(JsonObject jsonObject, String projectId) throws Exception {
		String name = jsonObject.get("name").getAsString();
		int processId =  jsonObject.get("processId").getAsInt();
		ProcessJoin obj = new ProcessJoin();
		obj.setName(name);
		obj.getChildProcessList().add(processId);
		boolean created = dao.create(obj, Integer.parseInt(projectId));
		if(created) return obj;
		throw new Exception();
	}
	
	
	public boolean deleteAll(String projectId, String name) {
		dao.deleteAll(Integer.parseInt(projectId), name);
		return true;
	}
	
	public boolean deleteProcess(String projectId, String name, Integer processId) {	
		dao.deleteProcess(Integer.parseInt(projectId), name, processId);
		return true;
	}
}
