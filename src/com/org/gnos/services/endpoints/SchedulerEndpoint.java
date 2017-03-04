package com.org.gnos.services.endpoints;

import static com.org.gnos.services.JsonUtil.json;
import static spark.Spark.post;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.org.gnos.services.ResponseError;
import com.org.gnos.services.controller.SchedulerController;

import spark.Request;
import spark.Response;
import spark.Route;

public class SchedulerEndpoint {

	SchedulerController controller;
	
	public SchedulerEndpoint() {
		
		controller = new SchedulerController();
		
		post("/project/:pid/scenario/:sid/runscheduler", new Route() {
			
			@Override
			public Object handle(Request req, Response res) throws Exception {
				JsonElement requestObject = new JsonParser().parse(req.body());
				if(requestObject.isJsonObject()) {
					JsonObject jsonObject = requestObject.getAsJsonObject();
					try {
						return controller.execute(jsonObject, req.params(":id"));
					} catch (Exception e) {
						res.status(400);
						return new ResponseError("Field creation failed. "+e.getMessage());
					}					
				}
				res.status(400);				
				return new ResponseError("Field creation failed due to improper input");
			}
		}, json());
	}
}
