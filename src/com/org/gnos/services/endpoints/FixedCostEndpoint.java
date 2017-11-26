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
import com.org.gnos.services.controller.FixedCostController;

import spark.Request;
import spark.Response;
import spark.Route;

public class FixedCostEndpoint {

	FixedCostController controller;
		
		public FixedCostEndpoint() {
			controller = new FixedCostController();
			
			get("/scenario/:id/fixedcosts", (req, res) -> controller.getAll(req.params(":id")), json());
			
			post("/scenario/:id/fixedcosts", new Route() {
				
				@Override
				public Object handle(Request req, Response res) throws Exception {
					JsonElement requestObject = new JsonParser().parse(req.body());
					if(requestObject.isJsonObject()) {
						JsonObject jsonObject = requestObject.getAsJsonObject();
						try {
							return controller.create(jsonObject, req.params(":id"));
						} catch (Exception e) {
							res.status(400);
							return new ResponseError("Operating cost entry creation failed. "+e.getMessage());
						}					
					}
					res.status(400);				
					return new ResponseError("Operating cost entry creation failed due to improper input");
				}
			}, json());
			
	        put("/scenario/:id/fixedcosts", new Route() {
				
				@Override
				public Object handle(Request req, Response res) throws Exception {
					JsonElement requestObject = new JsonParser().parse(req.body());
					if(requestObject.isJsonObject()) {
						JsonObject jsonObject = requestObject.getAsJsonObject();
						try {
							return controller.update(jsonObject, req.params(":id"));
						} catch (Exception e) {
							res.status(400);
							return new ResponseError("Operating cost entry update failed. "+e.getMessage());
						}					
					}
					res.status(400);				
					return new ResponseError("Operating cost entry update failed due to improper input");
				}
			}, json());

			/* DELETE existing fixedcosts for scenario */
			delete("/scenario/:id/fixedcosts", (req, res) -> controller.delete(req.params(":id")), json());
			
			/* DELETE fixedcosts by id */
			delete("/fixedcost/:id", (req, res) -> controller.deleteById(req.params(":id")), json());
		}
		
}
