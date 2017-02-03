package com.org.gnos.services.endpoints;

import static spark.Spark.*;
import static com.org.gnos.services.JsonUtil.*;

import com.org.gnos.services.controller.ProjectController;

public class ProjectEndpoint {
	
	ProjectController controller;
	
	public  ProjectEndpoint() {
		controller = new ProjectController();
		get("/projects", (req, res) -> controller.getAllprojects(), json());
	}
}
