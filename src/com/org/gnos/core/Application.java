package com.org.gnos.core;

import com.org.gnos.db.DBManager;


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
	
	public static void main(String[] args) {
		start();
	}
}
