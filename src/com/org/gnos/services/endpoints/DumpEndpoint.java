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
import com.org.gnos.services.controller.DumpController;

import spark.Request;
import spark.Response;
import spark.Route;;

public class DumpEndpoint {
	
	DumpController controller;
	
	public DumpEndpoint() {
		controller = new DumpController();
		
		get("/project/:id/dumps", (req, res) -> controller.getAll(req.params(":id")),  json());
		
        post("/project/:id/dumps", new Route() {
			
			@Override
			public Object handle(Request req, Response res) throws Exception {
				JsonElement requestObject = new JsonParser().parse(req.body());
				if(requestObject.isJsonObject()) {
					JsonObject jsonObject = requestObject.getAsJsonObject();
					try {
						return controller.create(jsonObject, req.params(":id"));
					} catch (Exception e) {
						res.status(400);
						return new ResponseError("Dump creation failed. "+e.getMessage());
					}					
				}
				res.status(400);				
				return new ResponseError("Dump creation failed due to improper input");
			}
		}, json());
		
        put("/dumps/:id", new Route() {
			
			@Override
			public Object handle(Request req, Response res) throws Exception {
				JsonElement requestObject = new JsonParser().parse(req.body());
				if(requestObject.isJsonObject()) {
					JsonObject jsonObject = requestObject.getAsJsonObject();
					try {
						return controller.update(jsonObject, req.params(":id"));
					} catch (Exception e) {
						res.status(400);
						return new ResponseError("Dump update failed. "+e.getMessage());
					}					
				}
				res.status(400);				
				return new ResponseError("Dump update failed due to improper input");
			}
		}, json());

		/* DELETE exisitng dump */
		delete("/dumps/:id", (req, res) -> controller.delete(req.params(":id")), json());
	}

}
