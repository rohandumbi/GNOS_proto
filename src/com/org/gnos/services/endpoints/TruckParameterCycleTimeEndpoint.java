package com.org.gnos.services.endpoints;

import static com.org.gnos.services.JsonUtil.json;
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
import com.org.gnos.services.ResponseError;
import com.org.gnos.services.controller.TruckParameterCycleTimeController;

public class TruckParameterCycleTimeEndpoint {
	
	TruckParameterCycleTimeController controller;
	
	public TruckParameterCycleTimeEndpoint() {
		controller = new TruckParameterCycleTimeController();
		get("/project/:id/truckcycletimes", (req, res) -> controller.getAll(req.params(":id")), json());
		
		post("/project/:id/truckcycletimes", new Route() {
			
			@Override
			public Object handle(Request req, Response res) throws Exception {
				JsonElement requestObject = new JsonParser().parse(req.body());
				if(requestObject.isJsonObject()) {
					JsonObject jsonObject = requestObject.getAsJsonObject();
					try {
						return controller.create(jsonObject, req.params(":id"));
					} catch (Exception e) {
						res.status(400);
						return new ResponseError("Truck cycle time mapping creation failed. "+e.getMessage());
					}					
				}
				res.status(400);				
				return new ResponseError("Truck cycle time mapping creation failed due to improper input");
			}
		}, json());
		
        put("/project/:id/truckcycletimes", new Route() {
			
			@Override
			public Object handle(Request req, Response res) throws Exception {
				JsonElement requestObject = new JsonParser().parse(req.body());
				if(requestObject.isJsonObject()) {
					JsonObject jsonObject = requestObject.getAsJsonObject();
					try {
						return controller.update(jsonObject, req.params(":id"));
					} catch (Exception e) {
						res.status(400);
						return new ResponseError("Truck cycle time mapping update failed. "+e.getMessage());
					}					
				}
				res.status(400);				
				return new ResponseError("Truck cycle time mapping update failed due to improper input");
			}
		}, json());

		/* DELETE exisitng expression */
		delete("/project/:id/truckcycletimes", (req, res) -> controller.delete(req.params(":id")), json());
	}
	
}
