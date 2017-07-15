package com.org.gnos.services.controller;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.org.gnos.db.dao.ProjectDAO;
import com.org.gnos.db.model.Project;
import com.org.gnos.services.PitBenchProcessor;
import com.org.gnos.services.util.FileUploadHelper;

public class ProjectController {
	
	private ProjectDAO dao = null;
	
	public ProjectController() {
		dao = new ProjectDAO();
	}

	public List<Project> getAll() {
		return dao.getAll();
	}
	
	public Project create(JsonObject jsonObject) throws Exception {
		
		FileUploadHelper helper = null;
		String name = jsonObject.get("name").getAsString();
		String desc = jsonObject.get("desc").getAsString();
		JsonArray files = jsonObject.get("files").getAsJsonArray();
		Project obj = new Project();
		obj.setName(name);
		obj.setDesc(desc);
		
		List<String> fileNames = new ArrayList<String>();
		if(files.size() > 0) {
			for(JsonElement file: files) {
				fileNames.add(file.getAsString());
			}
			obj.setFiles(fileNames);
			
			helper = new FileUploadHelper(fileNames);
			boolean correct = helper.verifyHeaders();
			
			if(!correct) {
				throw new Exception("File headers are not matching.");
			}
		}
		
		boolean created = dao.create(obj);
		
		if(created && helper != null){
			helper.storeFields(obj.getId());
			helper.loadData(obj.getId(), false);
			new PitBenchProcessor().updatePitBenchData(obj.getId());			
			return obj;
		}
		throw new Exception();
	}
	
	public Project update(JsonObject jsonObject, String projectId) throws Exception {
		
		FileUploadHelper helper = null;
		String name = jsonObject.get("name").getAsString();
		String desc = jsonObject.get("desc").getAsString();
		boolean append = jsonObject.get("append").getAsBoolean();
		JsonArray files = jsonObject.get("files").getAsJsonArray();

		Project obj = new Project();
		obj.setId(Integer.parseInt(projectId));
		obj.setName(name);
		obj.setDesc(desc);

		List<String> fileNames = new ArrayList<String>();
		if(files.size() > 0) {
			for(JsonElement file: files) {
				fileNames.add(file.getAsString());
			}
			obj.setFiles(fileNames);
			helper = new FileUploadHelper(fileNames);
			boolean correct = helper.verifyHeaders();		
			if(!correct) {
				throw new Exception("File headers are not matching.");
			}
		}
		
		boolean updated = dao.update(obj, append);
		
		if(updated && helper != null) {
			helper.loadData(obj.getId(), append);
			new PitBenchProcessor().updatePitBenchData(obj.getId());			
			return obj;
		}
		throw new Exception();
	}

	public boolean delete(String projectId) {
		if((projectId == null) || (projectId.isEmpty())){
			return false;
		}else{
			Project project = new Project();
			project.setId(Integer.parseInt(projectId));
			dao.delete(project);
			return true;
		}	
	}

	public String export(String projectIdStr) {
		return null;
	}
}
