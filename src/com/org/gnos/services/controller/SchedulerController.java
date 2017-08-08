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
			float gap = jsonObject.get("gap").getAsFloat();
			short period = jsonObject.get("period").getAsShort();
			
			runconfig.setMode(mode);
			runconfig.setReclaim(isReclaim);
			runconfig.setPeriod(period);			
			
			if(gap > 0 && gap <= 100) {
				runconfig.setMIPGAP(gap/100);
			}
			if(mode == 2) {				
				short window = jsonObject.get("window").getAsShort();
				short stepSize = jsonObject.get("stepSize").getAsShort();			
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
		SchedulerService service = new SchedulerService();
		service.setRunconfig(runconfig);
		new Thread(service).start();
		return null;
	}

}
