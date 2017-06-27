package com.org.gnos.core;

import com.org.gnos.db.DBManager;


public class Application {
	
	public static void start() {
		GNOSConfig.load();
		LogManager.initialize();
		DBManager.initializePool();
		EndpointManager.start();
	}

	public static void shutdown() {
		DBManager.terminatePool();
	}
	
	public static void main(String[] args) {
		start();
	}
}
