package com.org.gnos.services.controller;

import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.org.gnos.db.model.RunConfig;
import com.org.gnos.scheduler.SchedulerService;

public class SchedulerController {

	public Object execute(String projectId, String ScenarioId, JsonObject jsonObject) {
		
		RunConfig runconfig = new RunConfig();
		runconfig.setProjectId(Integer.parseInt(projectId));
		runconfig.setScenarioId(Integer.parseInt(ScenarioId));
		
		if(jsonObject != null) {
			short mode = jsonObject.get("mode").getAsShort();
			boolean isReclaim = jsonObject.get("isReclaim").getAsBoolean();
			
			runconfig.setMode(mode);
			runconfig.setReclaim(isReclaim);
			if(mode == 2) {
				short period = jsonObject.get("period").getAsShort();
				short window = jsonObject.get("window").getAsShort();
				short stepSize = jsonObject.get("stepSize").getAsShort();
				
				runconfig.setPeriod(period);
				runconfig.setWindow(window);
				runconfig.setStepSize(stepSize);

			}
			
			JsonElement elm = jsonObject.get("enableEquations");
			if(elm != null) {
				JsonObject enableEquations = elm.getAsJsonObject();
				
				for (Entry<String, JsonElement> data : enableEquations.entrySet()) {
					runconfig.addEqnenablestate(data.getKey(), data.getValue().getAsBoolean());
				}
			}
			 
		}
		SchedulerService service = SchedulerService.getInstance();
		service.setRunconfig(runconfig);
		new Thread(service).start();
		return null;
	}

}
