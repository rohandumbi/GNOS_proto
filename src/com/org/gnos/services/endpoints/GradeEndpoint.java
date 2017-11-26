package com.org.gnos.services.endpoints;

import static com.org.gnos.services.JsonUtil.json;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.org.gnos.services.ResponseError;
import com.org.gnos.services.controller.GradeController;

import spark.Request;
import spark.Response;
import spark.Route;

public class GradeEndpoint {

	GradeController controller;
	
	public GradeEndpoint() {
		
		controller = new GradeController();
		
		get("/project/:id/grades", (req, res) -> controller.getAll(req.params(":id")), json());
		
		post("/project/:id/grades", new Route() {
			
			@Override
			public Object handle(Request req, Response res) throws Exception {
				JsonElement requestObject = new JsonParser().parse(req.body());
				if(requestObject.isJsonObject()) {
					JsonObject jsonObject = requestObject.getAsJsonObject();
					try {
						return controller.create(jsonObject, req.params(":id"));
					} catch (Exception e) {
						res.status(400);
						return new ResponseError("Grade creation failed. "+e.getMessage());
					}					
				}
				res.status(400);				
				return new ResponseError("Grade creation failed due to improper input");
			}
		}, json());
		

		/* DELETE exisitng expression */
		delete("/grades/:id", (req, res) -> controller.deleteAll(req.params(":id")), json());

	}
	
}
