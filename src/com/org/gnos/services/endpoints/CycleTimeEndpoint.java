package com.org.gnos.services.endpoints;

import static com.org.gnos.services.JsonUtil.json;
import static spark.Spark.post;
import spark.Request;
import spark.Response;
import spark.Route;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.org.gnos.services.ResponseError;
import com.org.gnos.services.controller.CycleTimeController;

public class CycleTimeEndpoint {
	
	CycleTimeController controller;
	
	public  CycleTimeEndpoint() {
		controller = new CycleTimeController();	

		/* Create new project */
		post("/project/:id/cycletimes", new Route() {
			
			@Override
			public Object handle(Request req, Response res) throws Exception {
				JsonElement requestObject = new JsonParser().parse(req.body());
				if(requestObject.isJsonObject()) {
					JsonObject jsonObject = requestObject.getAsJsonObject();
					try {
						return controller.load(req.params(":id"), jsonObject);
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
