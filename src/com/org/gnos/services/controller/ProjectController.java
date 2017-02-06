package com.org.gnos.services.controller;

import java.util.List;

import com.google.gson.Gson;
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
			System.out.println(name + ":" + desc + ":" + fileName);
			Project newProject = new Project();
			newProject.setName(name);
			newProject.setDesc(desc);
			newProject.setFileName(fileName);
			
			return new ProjectDAO().create(newProject);
		}
		return false;
	}
}
