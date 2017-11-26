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
import com.org.gnos.services.controller.DumpDependencyController;

import spark.Request;
import spark.Response;
import spark.Route;

public class DumpDependencyEndpoint {

		DumpDependencyController controller;
		
		public DumpDependencyEndpoint() {
			controller = new DumpDependencyController();
			
			get("/scenario/:id/dumpdependencies", (req, res) -> controller.getAll(req.params(":id")), json());
			
			post("/scenario/:id/dumpdependencies", new Route() {
				
				@Override
				public Object handle(Request req, Response res) throws Exception {
					JsonElement requestObject = new JsonParser().parse(req.body());
					if(requestObject.isJsonObject()) {
						JsonObject jsonObject = requestObject.getAsJsonObject();
						try {
							return controller.create(jsonObject, req.params(":id"));
						} catch (Exception e) {
							res.status(400);
							return new ResponseError("Dump dependency creation failed. "+e.getMessage());
						}					
					}
					res.status(400);				
					return new ResponseError("Dump dependency creation failed due to improper input");
				}
			}, json());
			
	        put("/dumpdependencies/:id", new Route() {
				
				@Override
				public Object handle(Request req, Response res) throws Exception {
					JsonElement requestObject = new JsonParser().parse(req.body());
					if(requestObject.isJsonObject()) {
						JsonObject jsonObject = requestObject.getAsJsonObject();
						try {
							return controller.update(jsonObject, req.params(":id"));
						} catch (Exception e) {
							res.status(400);
							return new ResponseError("Dump dependency update failed. "+e.getMessage());
						}					
					}
					res.status(400);				
					return new ResponseError("Dump dependency update failed due to improper input");
				}
			}, json());

			delete("/dumpdependencies/:id", (req, res) -> controller.delete(req.params(":id")), json());
		}
		
}
