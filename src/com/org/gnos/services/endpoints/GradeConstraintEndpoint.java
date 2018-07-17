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
import com.org.gnos.services.controller.GradeConstraintController;

public class GradeConstraintEndpoint {

		GradeConstraintController controller;
		
		public GradeConstraintEndpoint() {
			controller = new GradeConstraintController();
			
			get("/scenario/:id/gradeconstraints", (req, res) -> controller.getAll(req.params(":id")), json());
			
			post("/scenario/:id/gradeconstraints", new Route() {
				
				@Override
				public Object handle(Request req, Response res) throws Exception {
					JsonElement requestObject = new JsonParser().parse(req.body());
					if(requestObject.isJsonObject()) {
						JsonObject jsonObject = requestObject.getAsJsonObject();
						try {
							return controller.create(jsonObject, req.params(":id"));
						} catch (Exception e) {
							res.status(400);
							return new ResponseError("Grade constraint creation failed. "+e.getMessage());
						}					
					}
					res.status(400);				
					return new ResponseError("Grade constraint creation failed due to improper input");
				}
			}, json());
			
	        put("/gradeconstraints/:id", new Route() {
				
				@Override
				public Object handle(Request req, Response res) throws Exception {
					JsonElement requestObject = new JsonParser().parse(req.body());
					if(requestObject.isJsonObject()) {
						JsonObject jsonObject = requestObject.getAsJsonObject();
						try {
							return controller.update(jsonObject, req.params(":id"));
						} catch (Exception e) {
							res.status(400);
							return new ResponseError("Grade constraint update failed. "+e.getMessage());
						}					
					}
					res.status(400);				
					return new ResponseError("Grade constraint update failed due to improper input");
				}
			}, json());

			delete("/gradeconstraints/:id", (req, res) -> controller.delete(req.params(":id")), json());
		}
		
}
