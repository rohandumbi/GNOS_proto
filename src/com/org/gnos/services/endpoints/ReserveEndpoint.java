package com.org.gnos.services.endpoints;

import static com.org.gnos.services.util.JsonUtil.json;
import static spark.Spark.get;

import com.org.gnos.services.controller.ReserveDataController;

public class ReserveEndpoint {

	ReserveDataController controller;
	
	public ReserveEndpoint() {
		
		controller = new ReserveDataController();
		
		get("/project/:id/reserve", (req, res) -> controller.getAll(req.params(":id")), json());

	}
	
}
