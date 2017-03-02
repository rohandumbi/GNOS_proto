package com.org.gnos.services.endpoints;

import static com.org.gnos.services.JsonUtil.json;
import static spark.Spark.get;

import com.org.gnos.services.controller.ProcessController;
public class ProcessEndpoint {

	ProcessController controller;
	
	public ProcessEndpoint() {
		controller = new ProcessController();
		get("/project/:id/processes", (req, res) -> controller.getAll(req.params(":id")), json());
	}
}
