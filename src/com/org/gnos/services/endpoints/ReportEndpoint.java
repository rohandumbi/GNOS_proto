package com.org.gnos.services.endpoints;

import static com.org.gnos.services.util.JsonUtil.json;

import static spark.Spark.post;
import spark.Request;
import spark.Response;
import spark.Route;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.org.gnos.services.common.ResponseError;
import com.org.gnos.services.controller.ReportController;

public class ReportEndpoint {

		ReportController controller;
		
		public ReportEndpoint() {
			controller = new ReportController();

			post("/project/:id/report/csv", new Route() {
				
				@Override
				public Object handle(Request req, Response res) throws Exception {
					JsonElement requestObject = new JsonParser().parse(req.body());
					if(requestObject.isJsonObject()) {
						JsonObject jsonObject = requestObject.getAsJsonObject();
						try {
							res.type("text/csv");
							res.header("Content-Disposition", "attachment; filename=\"report.csv\"");
							return controller.getReportCSV(jsonObject, req.params(":id"));
						} catch (Exception e) {
							res.status(400);
							return new ResponseError("Could not fetch report data. "+e.getMessage());
						}
					}
					res.status(400);				
					return new ResponseError("Could not fetch report data due to improper input");
					
				}
			});
			
			post("/project/:id/report/capex/csv", new Route() {
				
				@Override
				public Object handle(Request req, Response res) throws Exception {
					JsonElement requestObject = new JsonParser().parse(req.body());
					if(requestObject.isJsonObject()) {
						JsonObject jsonObject = requestObject.getAsJsonObject();
						try {
							res.type("text/csv");
							res.header("Content-Disposition", "attachment; filename=\"capex-report.csv\"");
							return controller.getCapexReportCSV(jsonObject, req.params(":id"));
						} catch (Exception e) {
							res.status(400);
							return new ResponseError("Could not fetch report data. "+e.getMessage());
						}
					}
					res.status(400);				
					return new ResponseError("Could not fetch report data due to improper input");
					
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
							e.printStackTrace();
							res.status(400);
							return new ResponseError("Could not fetch report data. "+e.getMessage());
						}					
					}
					res.status(400);				
					return new ResponseError("Could not fetch report data due to improper input");
				}
			}, json());
		}
		
}
