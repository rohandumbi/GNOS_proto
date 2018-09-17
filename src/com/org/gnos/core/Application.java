package com.org.gnos.core;

import com.org.gnos.db.DBManager;
import com.org.gnos.licensing.GNOSLicense;


public class Application {
	
	private static final String version = "1.0.2";
	
	public static void start() {
		
		try {
			GNOSLicense.initialize();
			GNOSConfig.load();
			LogManager.initialize();
			DBManager.initializePool();
			EndpointManager.start();
		} catch (Exception e) {
			System.err.println("Can not start application. Error :"+e.getMessage());
		}
		
	}

	public static void shutdown() {
		DBManager.terminatePool();
	}
	
	public static void main(String[] args) {
		start();
	}
}
