package com.org.gnos.services.endpoints;

import static com.org.gnos.services.JsonUtil.json;
import static spark.Spark.get;

import com.org.gnos.services.controller.PitController;
public class PitEndpoint {

	PitController controller;
	
	public PitEndpoint() {
		controller = new PitController();
		get("/project/:id/pits", (req, res) -> controller.getAll(req.params(":id")), json());
	}
	
	
}
