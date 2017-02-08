package com.org.gnos.services.endpoints;

import static spark.Spark.*;
import static com.org.gnos.services.JsonUtil.*;

import com.org.gnos.services.controller.ProjectController;

public class ProjectEndpoint {
	
	ProjectController controller;
	
	public  ProjectEndpoint() {
		controller = new ProjectController();
		
		/* GET all projects */
		get("/projects", (req, res) -> controller.getAllprojects(), json());
		/* Create new project */
		post("/projects", (req, res) -> controller.createProject(req, res), json());
		/* DELETE exisitng project */
		delete("/projects", (req, res) -> controller.deleteProject(req, res), json());
	}
}
