package com.org.gnos.services.endpoints;

import static com.org.gnos.services.JsonUtil.json;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import spark.Request;
import spark.Response;
import spark.Route;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.org.gnos.services.ResponseError;
import com.org.gnos.services.controller.PitGroupController;
public class PitGroupEndpoint {

	PitGroupController controller;
	
	public PitGroupEndpoint() {
		
		controller = new PitGroupController();
		
		get("/project/:id/pitgroups", (req, res) -> controller.getAll(req.params(":id")), json());
		
		post("/project/:id/pitgroups", new Route() {
			
			@Override
			public Object handle(Request req, Response res) throws Exception {
				JsonElement requestObject = new JsonParser().parse(req.body());
				if(requestObject.isJsonObject()) {
					JsonObject jsonObject = requestObject.getAsJsonObject();
					try {
						return controller.create(jsonObject, req.params(":id"));
					} catch (Exception e) {
						res.status(400);
						return new ResponseError("Pit Group creation failed. "+e.getMessage());
					}					
				}
				res.status(400);				
				return new ResponseError("Pit Group creation failed due to improper input");
			}
		}, json());
		

		/* DELETE exisitng expression */
		delete("/project/:id/pitgroups", (req, res) -> controller.deleteAll(req.params(":id")), json());
		
		delete("/project/:id/pitgroup/:pgname/pit/:pname", 
				(req, res) -> controller.deletePit(req.params(":id"),req.params(":pgname"), req.params(":pname")), json());
		
		delete("/project/:id/pitgroup/:pgname", 
				(req, res) -> controller.deletePitGroup(req.params(":id"),req.params(":pgname")), json());
		
		delete("/project/:id/pitgroup/:pgname/cpitgroup/:cpgname", 
				(req, res) -> controller.deleteChildPitGroup(req.params(":id"),req.params(":pgname"), req.params(":cpgname")), json());
	}
	
	
}
