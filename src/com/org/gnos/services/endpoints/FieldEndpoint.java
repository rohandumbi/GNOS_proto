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
import com.org.gnos.services.controller.FieldController;

import spark.Request;
import spark.Response;
import spark.Route;
public class FieldEndpoint {

	FieldController controller;
	
	public FieldEndpoint() {
		controller = new FieldController();
		get("/project/:id/fields", (req, res) -> controller.getAllFields(req.params(":id")), json());
		
		post("/project/:id/fields", new Route() {
			
			@Override
			public Object handle(Request req, Response res) throws Exception {
				JsonElement requestObject = new JsonParser().parse(req.body());
				if(requestObject.isJsonObject()) {
					JsonObject jsonObject = requestObject.getAsJsonObject();
					try {
						return controller.createField(jsonObject, req.params(":id"));
					} catch (Exception e) {
						res.status(400);
						return new ResponseError("Expression creation failed. "+e.getMessage());
					}					
				}
				res.status(400);				
				return new ResponseError("Expression creation failed due to improper input");
			}
		}, json());
		
        put("/expressions/:id", new Route() {
			
			@Override
			public Object handle(Request req, Response res) throws Exception {
				JsonElement requestObject = new JsonParser().parse(req.body());
				if(requestObject.isJsonObject()) {
					JsonObject jsonObject = requestObject.getAsJsonObject();
					try {
						return controller.updateField(jsonObject, req.params(":id"));
					} catch (Exception e) {
						res.status(400);
						return new ResponseError("Expression creation failed. "+e.getMessage());
					}					
				}
				res.status(400);				
				return new ResponseError("Expression creation failed due to improper input");
			}
		}, json());

		/* DELETE exisitng expression */
		delete("/expressions/:id", (req, res) -> controller.deleteField(req.params(":id")), json());
	}
	
	
}
