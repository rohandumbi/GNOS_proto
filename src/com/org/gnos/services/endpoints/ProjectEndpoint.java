package com.org.gnos.services.endpoints;

import static com.org.gnos.services.JsonUtil.json;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

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
		get("/projects", (req, res) -> controller.getAll(), json());
		
		get("/projects/:id/export", new Route() {
			
			@Override
			public Object handle(Request req, Response res) throws Exception {
					try {
						res.type("application/octet-stream");
						res.header("Content-Disposition", "attachment; filename=\"project.data\"");
						return controller.export(req.params(":id"));
					} catch (Exception e) {
						res.status(400);
						return new ResponseError("Could not fetch report data. "+e.getMessage());
					}					
			}
		});
		
		/* Create new project */
		post("/projects", new Route() {
			
			@Override
			public Object handle(Request req, Response res) throws Exception {
				JsonElement requestObject = new JsonParser().parse(req.body());
				if(requestObject.isJsonObject()) {
					JsonObject jsonObject = requestObject.getAsJsonObject();
					try {
						return controller.create(jsonObject);
					} catch (Exception e) {
						res.status(400);
						return new ResponseError("Project creation failed. "+e.getMessage());
					}					
				}
				res.status(400);				
				return new ResponseError("Project creation failed due to improper input");
			}
		}, json());
		
		/* Create new project */
		put("/projects/:id", new Route() {
			
			@Override
			public Object handle(Request req, Response res) throws Exception {
				JsonElement requestObject = new JsonParser().parse(req.body());
				if(requestObject.isJsonObject()) {
					JsonObject jsonObject = requestObject.getAsJsonObject();
					try {
						return controller.update(jsonObject, req.params(":id"));
					} catch (Exception e) {
						res.status(400);
						return new ResponseError("Project update failed. "+e.getMessage());
					}					
				}
				res.status(400);				
				return new ResponseError("Project update failed due to improper input");
			}
		}, json());
		
		/* DELETE exisitng project */
		delete("/projects/:id", (req, res) -> controller.delete(req.params(":id")), json());
		
		/* Import a project */
		post("/projects/import", new Route() {
			
			@Override
			public Object handle(Request req, Response res) throws Exception {
				JsonElement requestObject = new JsonParser().parse(req.body());
				if(requestObject.isJsonObject()) {
					JsonObject jsonObject = requestObject.getAsJsonObject();
					try {
						return controller.importProject(jsonObject);
					} catch (Exception e) {
						res.status(400);
						return new ResponseError("Project creation failed. "+e.getMessage());
					}					
				}
				res.status(400);				
				return new ResponseError("Project creation failed due to improper input");
			}
		}, json());
	}
}
