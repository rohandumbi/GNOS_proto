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
import com.org.gnos.services.controller.CycleTimeMappingController;

import spark.Request;
import spark.Response;
import spark.Route;
public class CycleTimeMappingEndpoint {

	CycleTimeMappingController controller;
	
	public CycleTimeMappingEndpoint() {
		controller = new CycleTimeMappingController();
		get("/project/:id/cycletimemappings", (req, res) -> controller.getAll(req.params(":id")), json());
		
		post("/project/:id/cycletimemappings", new Route() {
			
			@Override
			public Object handle(Request req, Response res) throws Exception {
				JsonElement requestObject = new JsonParser().parse(req.body());
				if(requestObject.isJsonObject()) {
					JsonObject jsonObject = requestObject.getAsJsonObject();
					try {
						return controller.create(jsonObject, req.params(":id"));
					} catch (Exception e) {
						res.status(400);
						return new ResponseError("Cycle time mapping creation failed. "+e.getMessage());
					}					
				}
				res.status(400);				
				return new ResponseError("Cycle time mapping creation failed due to improper input");
			}
		}, json());
		
        put("/project/:id/cycletimemappings", new Route() {
			
			@Override
			public Object handle(Request req, Response res) throws Exception {
				JsonElement requestObject = new JsonParser().parse(req.body());
				if(requestObject.isJsonObject()) {
					JsonObject jsonObject = requestObject.getAsJsonObject();
					try {
						return controller.update(jsonObject, req.params(":id"));
					} catch (Exception e) {
						res.status(400);
						return new ResponseError("Cycle time mapping update failed. "+e.getMessage());
					}					
				}
				res.status(400);				
				return new ResponseError("Cycle time mapping update failed due to improper input");
			}
		}, json());

		/* DELETE exisitng expression */
		delete("/project/:id/cycletimemappings", (req, res) -> controller.deleteAll(req.params(":id")), json());
		
		delete("/project/:id/cycletimemappings/filed/:fname/type/:mtype", (req, res) -> controller.delete(req.params(":id"), req.params(":fname"), Short.parseShort(req.params(":mtype"))), json());
		
		//Fixed time endpoint
		
		get("/project/:id/fixedtime", (req, res) -> controller.getFixedTime(req.params(":id")), json());
		
		post("/project/:id/fixedtime/:value",  (req, res) -> controller.createFixedTime(req.params(":id"), req.params(":value")), json());
		
        put("/project/:id/fixedtime/:value", (req, res) -> controller.updateFixedTime(req.params(":id"), req.params(":value")), json());

		/* DELETE exisitng expression */
		delete("/project/:id/fixedtime", (req, res) -> controller.deleteFixedTime(req.params(":id")), json());
	}
	
	
}
