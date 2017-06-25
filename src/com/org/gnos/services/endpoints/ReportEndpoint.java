package com.org.gnos.services.endpoints;

import static com.org.gnos.services.JsonUtil.json;
import static spark.Spark.get;
import static spark.Spark.post;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.org.gnos.services.ResponseError;
import com.org.gnos.services.controller.ReportController;

import spark.Request;
import spark.Response;
import spark.Route;

public class ReportEndpoint {

		ReportController controller;
		
		public ReportEndpoint() {
			controller = new ReportController();

			get("/project/:id/scenario/:sid/report/csv", new Route() {
				
				@Override
				public Object handle(Request req, Response res) throws Exception {
						try {
							res.type("text/csv");
							return controller.getReportCSV(req.params(":id"), req.params(":sid"));
						} catch (Exception e) {
							res.status(400);
							return new ResponseError("Could not fetch report data. "+e.getMessage());
						}					
				}
			});
			
			post("/project/:id/report", new Route() {
				
				@Override
				public Object handle(Request req, Response res) throws Exception {
					JsonElement requestObject = new JsonParser().parse(req.body());
					if(requestObject.isJsonObject()) {
						JsonObject jsonObject = requestObject.getAsJsonObject();
						try {
							return controller.getReport(jsonObject, req.params(":id"));
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
