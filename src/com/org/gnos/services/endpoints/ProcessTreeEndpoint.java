package com.org.gnos.services.endpoints;

import static com.org.gnos.services.JsonUtil.json;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.org.gnos.services.ResponseError;
import com.org.gnos.services.controller.ProcessTreeController;

import spark.Request;
import spark.Response;
import spark.Route;

public class ProcessTreeEndpoint {
	
	ProcessTreeController controller;
	
	public ProcessTreeEndpoint() {
		
		controller = new ProcessTreeController();
		
		get("/project/:id/processtreenodes", (req, res) -> controller.getAll(req.params(":id")), json());
		
		post("/project/:id/processtreenodes", new Route() {
			
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
		

		/* DELETE exisitng expression */
		delete("/project/:id/processtreenodes", (req, res) -> controller.deleteAll(req.params(":id")), json());
		
		delete("/project/:id/processtreenodes/model/:mid", 
				(req, res) -> controller.deleteProcessTreeNode(req.params(":id"), Integer.parseInt(req.params(":mid"))), json());

	}

}
