package com.org.gnos.services.endpoints;

import static  com.org.gnos.services.JsonUtil.json;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.org.gnos.services.ResponseError;
import com.org.gnos.services.controller.ScenarioController;

import spark.Request;
import spark.Response;
import spark.Route;

public class ScenarioEndpoint {

	ScenarioController controller;
	
	public ScenarioEndpoint() {
		controller = new ScenarioController();
		
		get("/project/:id/scenarios", (req,res) -> controller.getAll(req.params(":id")), json());
		
        post("/project/:id/scenarios", new Route() {
			
			@Override
			public Object handle(Request req, Response res) throws Exception {
				JsonElement requestObject = new JsonParser().parse(req.body());
				if(requestObject.isJsonObject()) {
					JsonObject jsonObject = requestObject.getAsJsonObject();
					try {
						return controller.create(jsonObject, req.params(":id"));
					} catch (Exception e) {
						res.status(400);
						return new ResponseError("Scenario creation failed. "+e.getMessage());
					}					
				}
				res.status(400);				
				return new ResponseError("Scenario creation failed due to improper input");
			}
		}, json());
		
        post("/project/:pid/scenarios/:sid/copy", new Route() {
			
			@Override
			public Object handle(Request req, Response res) throws Exception {		
				try {
					return controller.copy(req.params(":pid"), req.params(":sid"));
				} catch (Exception e) {
					res.status(400);
					return new ResponseError(e.getMessage());
				}					
			}
		}, json());

        put("/scenarios/:id", new Route() {
			
			@Override
			public Object handle(Request req, Response res) throws Exception {
				JsonElement requestObject = new JsonParser().parse(req.body());
				if(requestObject.isJsonObject()) {
					JsonObject jsonObject = requestObject.getAsJsonObject();
					try {
						return controller.update(jsonObject, req.params(":id"));
					} catch (Exception e) {
						res.status(400);
						return new ResponseError("Scenario update failed. "+e.getMessage());
					}					
				}
				res.status(400);				
				return new ResponseError("Scenario update failed due to improper input");
			}
		}, json());

		/* DELETE exisitng dump */
		delete("/scenarios/:id", (req, res) -> controller.delete(req.params(":id")), json());
		
	}
}
