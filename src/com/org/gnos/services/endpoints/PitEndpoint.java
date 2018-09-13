package com.org.gnos.services.endpoints;

import static com.org.gnos.services.util.JsonUtil.json;
import static spark.Spark.get;

import com.org.gnos.services.controller.PitController;
public class PitEndpoint {

	PitController controller;
	
	public PitEndpoint() {
		controller = new PitController();
		get("/project/:id/pits", (req, res) -> controller.getAll(req.params(":id")), json());
		
		get("/project/:id/pit/:pitno/benches", (req, res) -> controller.getAllBenchesForPit(req.params(":id"), req.params(":pitno")), json());
	}
	
	
}
