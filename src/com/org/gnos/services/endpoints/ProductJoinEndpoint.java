package com.org.gnos.services.endpoints;

import static com.org.gnos.services.JsonUtil.json;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.org.gnos.services.ResponseError;
import com.org.gnos.services.controller.ProductJoinController;

import spark.Request;
import spark.Response;
import spark.Route;
public class ProductJoinEndpoint {
	
	ProductJoinController controller;
	
	public ProductJoinEndpoint() {
		
		controller = new ProductJoinController();
		
		get("/project/:id/productjoins", (req, res) -> controller.getAll(req.params(":id")), json());
		
		post("/project/:id/productjoins", new Route() {
			
			@Override
			public Object handle(Request req, Response res) throws Exception {
				JsonElement requestObject = new JsonParser().parse(req.body());
				if(requestObject.isJsonObject()) {
					JsonObject jsonObject = requestObject.getAsJsonObject();
					try {
						return controller.create(jsonObject, req.params(":id"));
					} catch (Exception e) {
						res.status(400);
						return new ResponseError("Product join creation failed. "+e.getMessage());
					}					
				}
				res.status(400);				
				return new ResponseError("Product join creation failed due to improper input");
			}
		}, json());
		

		/* DELETE exisitng expression */
		delete("/project/:id/productjoins", (req, res) -> controller.deleteAll(req.params(":id")), json());
		
		delete("/project/:id/productjoins/:name", (req, res) -> controller.deleteProductJoin(req.params(":id"), req.params(":name")), json());
		
		delete("/project/:id/productjoins/:name/product/:pname", 
				(req, res) -> controller.deleteProductFromProductJoin(req.params(":id"),req.params(":name"), req.params(":pname")), json());
		
		delete("/project/:id/productjoins/:name/productjoin/:pjname", 
				(req, res) -> controller.deleteProductJoinFromProductJoin(req.params(":id"),req.params(":name"), req.params(":pjname")), json());
	}
	
}
