package com.org.gnos.services.endpoints;

import static com.org.gnos.services.JsonUtil.json;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.org.gnos.services.ResponseError;
import com.org.gnos.services.controller.ProjectController;

import spark.Request;
import spark.Response;
import spark.Route;

public class ProjectEndpoint {
	
	ProjectController controller;
	
	public  ProjectEndpoint() {
		controller = new ProjectController();	
		/* GET all projects */
		get("/projects", (req, res) -> controller.getAllProjects(), json());
		/* Create new project */
		post("/projects", new Route() {
			
			@Override
			public Object handle(Request req, Response res) throws Exception {
				JsonElement requestObject = new JsonParser().parse(req.body());
				if(requestObject.isJsonObject()) {
					JsonObject jsonObject = requestObject.getAsJsonObject();
					try {
						return controller.createProject(jsonObject);
					} catch (Exception e) {
						res.status(400);
						return new ResponseError("Project creation failed. "+e.getMessage());
					}					
				}
				res.status(400);				
				return new ResponseError("Project creation failed due to improper input");
			}
		}, json());
		
		/* DELETE exisitng project */
		delete("/projects/:id", (req, res) -> controller.deleteProject(req.params(":id")), json());
	}
}
