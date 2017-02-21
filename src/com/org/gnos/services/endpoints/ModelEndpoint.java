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
import com.org.gnos.services.controller.ModelController;

import spark.Request;
import spark.Response;
import spark.Route;

public class ModelEndpoint {

	ModelController controller;
	
	public ModelEndpoint() {
		controller = new ModelController();
		
		get("/project/:pid/models",	(req, res) -> controller.getAll(req.params(":pid")), json());
		
        post("/project/:pid/model", new Route() {
			
			@Override
			public Object handle(Request req, Response res) throws Exception {
				JsonElement requestObject = new JsonParser().parse(req.body());
				if(requestObject.isJsonObject()) {
					JsonObject jsonObject = requestObject.getAsJsonObject();
					try {
						return controller.create(jsonObject, req.params(":pid"));
					} catch (Exception e) {
						res.status(400);
						return new ResponseError("Model creation failed. "+e.getMessage());
					}					
				}
				res.status(400);				
				return new ResponseError("Model creation failed due to improper input");
			}
		}, json());
		
        put("/model/:id", new Route() {
			
			@Override
			public Object handle(Request req, Response res) throws Exception {
				JsonElement requestObject = new JsonParser().parse(req.body());
				if(requestObject.isJsonObject()) {
					JsonObject jsonObject = requestObject.getAsJsonObject();
					try {
						return controller.update(jsonObject, req.params(":id"));
					} catch (Exception e) {
						res.status(400);
						return new ResponseError("Model creation failed. "+e.getMessage());
					}					
				}
				res.status(400);				
				return new ResponseError("Model creation failed due to improper input");
			}
		}, json());

		/* DELETE exisitng expression */
		delete("/expressions/:id", (req, res) -> controller.delete(req.params(":id")), json());
	}

}
