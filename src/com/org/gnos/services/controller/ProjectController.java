package com.org.gnos.services.controller;

import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.org.gnos.db.dao.ProjectDAO;
import com.org.gnos.db.model.Project;

import spark.Request;
import spark.Response;

public class ProjectController {

	public List<Project> getAllprojects() {
		return new ProjectDAO().getAll();
	}
	
	public boolean createProject(Request req, Response res) {
		JsonElement requestObject = new JsonParser().parse(req.body());
		if(requestObject.isJsonObject()) {
			JsonObject jsonObject = requestObject.getAsJsonObject();
			String name = jsonObject.get("name").getAsString();
			String desc = jsonObject.get("desc").getAsString();
			String fileName = jsonObject.get("fileName").getAsString();
			Project newProject = new Project();
			newProject.setName(name);
			newProject.setDesc(desc);
			newProject.setFileName(fileName);
			
			return new ProjectDAO().create(newProject);
		}
		return false;
	}
	
	public boolean deleteProject (Request req, Response res) {
		String projectId = req.queryParams("id");
		if((projectId == null) || (projectId.isEmpty())){
			return false;
		}else{
			Project project = new Project();
			project.setId(Integer.parseInt(projectId));
			new ProjectDAO().delete(project);
			return true;
		}	
	}
}
