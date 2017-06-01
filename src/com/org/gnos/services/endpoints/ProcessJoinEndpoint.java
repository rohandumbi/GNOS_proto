package com.org.gnos.services.endpoints;

import static com.org.gnos.services.JsonUtil.json;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.org.gnos.services.ResponseError;
import com.org.gnos.services.controller.ProcessJoinController;

import spark.Request;
import spark.Response;
import spark.Route;

public class ProcessJoinEndpoint {
	
	ProcessJoinController controller;
	
	public ProcessJoinEndpoint() {
		
		controller = new ProcessJoinController();
		
		get("/project/:id/processjoins", (req, res) -> controller.getAll(req.params(":id")), json());
		
		post("/project/:id/processjoins", new Route() {
			
			@Override
			public Object handle(Request req, Response res) throws Exception {
				JsonElement requestObject = new JsonParser().parse(req.body());
				if(requestObject.isJsonObject()) {
					JsonObject jsonObject = requestObject.getAsJsonObject();
					try {
						return controller.create(jsonObject, req.params(":id"));
					} catch (Exception e) {
						res.status(400);
						return new ResponseError("Field creation failed. "+e.getMessage());
					}					
				}
				res.status(400);				
				return new ResponseError("Field creation failed due to improper input");
			}
		}, json());
		

		/* DELETE exisitng expression */
		delete("/project/:id/processjoins/:name", (req, res) -> controller.deleteAll(req.params(":id"), req.params(":name")), json());
		
		delete("/project/:id/processjoins/:name/process/:pid", 
				(req, res) -> controller.deleteProcess(req.params(":id"), req.params(":name"),  Integer.parseInt(req.params(":pid"))), json());

	}

}
