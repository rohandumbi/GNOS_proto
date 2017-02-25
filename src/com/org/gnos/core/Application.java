package com.org.gnos.core;

import com.org.gnos.db.DBManager;
import com.org.gnos.scheduler.SchedulerService;


public class Application {
	
	public static void start() {
		GNOSConfig.load();
		DBManager.initializePool();
		EndpointManager.start();
	}

	public static void shutdown() {
		DBManager.terminatePool();
	}
	
	public static void save() {
		ProjectConfigutration.getInstance().save();
		ScenarioConfigutration.getInstance().save();
	}
	
	public static void generate() {
		SchedulerService.getInstance().execute();
	}
	
	public static void main(String[] args) {
		start();
	}
}
