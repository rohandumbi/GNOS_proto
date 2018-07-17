package com.org.gnos.services.endpoints;

import static com.org.gnos.services.util.JsonUtil.json;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;
import spark.Request;
import spark.Response;
import spark.Route;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.org.gnos.services.common.ResponseError;
import com.org.gnos.services.controller.FieldController;
public class FieldEndpoint {

	FieldController controller;
	
	public FieldEndpoint() {
		controller = new FieldController();
		get("/project/:id/fields", (req, res) -> controller.getAll(req.params(":id"), req.queryParams("datatype")), json());
		
		get("/project/:id/cycletimefields", (req, res) -> controller.getAllCycletimeFields(req.params(":id")), json());
		
		post("/project/:id/fields", new Route() {
			
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
		
        put("/field/:id", new Route() {
			
			@Override
			public Object handle(Request req, Response res) throws Exception {
				JsonElement requestObject = new JsonParser().parse(req.body());
				if(requestObject.isJsonObject()) {
					JsonObject jsonObject = requestObject.getAsJsonObject();
					try {
						return controller.update(jsonObject, req.params(":id"));
					} catch (Exception e) {
						res.status(400);
						return new ResponseError("Field update failed. "+e.getMessage());
					}					
				}
				res.status(400);				
				return new ResponseError("Field update failed due to improper input");
			}
		}, json());

		/* DELETE exisitng expression */
		delete("/field/:id", (req, res) -> controller.delete(req.params(":id")), json());
	}
	
	
}
