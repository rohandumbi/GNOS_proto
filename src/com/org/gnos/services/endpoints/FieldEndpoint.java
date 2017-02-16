package com.org.gnos.services.endpoints;

import static com.org.gnos.services.JsonUtil.json;
import static spark.Spark.get;

import com.org.gnos.services.controller.FieldController;
public class FieldEndpoint {

	FieldController controller;
	
	public FieldEndpoint() {
		controller = new FieldController();
		get("/project/:id/fields", (req, res) -> controller.getAllFields(req.params(":id")), json());
	}
}
