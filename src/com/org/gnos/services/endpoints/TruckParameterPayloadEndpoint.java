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
import com.org.gnos.services.controller.TruckParameterPayloadController;

import spark.Request;
import spark.Response;
import spark.Route;

public class TruckParameterPayloadEndpoint {
	
	TruckParameterPayloadController controller;
	
	public TruckParameterPayloadEndpoint() {
		
		controller = new TruckParameterPayloadController();
		
		get("/project/:id/payloads", (req, res) -> controller.getAll(req.params(":id")), json());
		
		post("/project/:id/payloads", new Route() {
			
			@Override
			public Object handle(Request req, Response res) throws Exception {
				JsonElement requestObject = new JsonParser().parse(req.body());
				if(requestObject.isJsonObject()) {
					JsonObject jsonObject = requestObject.getAsJsonObject();
					try {
						return controller.create(jsonObject, req.params(":id"));
					} catch (Exception e) {
						res.status(400);
						return new ResponseError("Truck payload creation failed. "+e.getMessage());
					}					
				}
				res.status(400);				
				return new ResponseError("Truck payload mapping creation failed due to improper input");
			}
		}, json());

        put("/project/:id/payloads", new Route() {
			
			@Override
			public Object handle(Request req, Response res) throws Exception {
				JsonElement requestObject = new JsonParser().parse(req.body());
				if(requestObject.isJsonObject()) {
					JsonObject jsonObject = requestObject.getAsJsonObject();
					try {
						return controller.update(jsonObject, req.params(":id"));
					} catch (Exception e) {
						res.status(400);
						return new ResponseError("Truck payload update failed. "+e.getMessage());
					}					
				}
				res.status(400);				
				return new ResponseError("Truck payload update failed due to improper input");
			}
		}, json());

		/* DELETE exisitng expression */
		delete("/project/:id/payloads", (req, res) -> controller.deleteAll(req.params(":id")), json());
		
		delete("/project/:id/payloads/material/:name", (req, res) -> controller.delete(req.params(":id"), req.params(":name")), json());
	}
	
}
