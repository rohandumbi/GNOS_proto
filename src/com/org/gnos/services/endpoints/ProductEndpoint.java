package com.org.gnos.services.endpoints;

import static com.org.gnos.services.JsonUtil.json;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.org.gnos.services.ResponseError;
import com.org.gnos.services.controller.ProductController;

import spark.Request;
import spark.Response;
import spark.Route;

public class ProductEndpoint {

	ProductController controller;
	
	public ProductEndpoint() {
		
		controller = new ProductController();
		
		get("/project/:id/products", (req, res) -> controller.getAll(req.params(":id")), json());
		
		get("/project/:id/product/:name/grades", (req, res) -> controller.getAllGrades(req.params(":id"), req.params("name")), json());
		
		post("/project/:id/products", new Route() {
			
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
		delete("/project/:id/products", (req, res) -> controller.deleteAll(req.params(":id")), json());

	}
	
}
