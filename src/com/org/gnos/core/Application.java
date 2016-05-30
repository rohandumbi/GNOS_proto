package com.org.gnos.core;

import com.org.gnos.db.DBManager;
import com.org.gnos.services.ObjectiveFunctionEquationGenerator;
import com.org.gnos.services.ProcessConstraintEquationGenerator;


public class Application {
	
	public static void start() {
		GNOSConfig.load();
		DBManager.initializePool();
	}

	public static void shutdown() {
		DBManager.terminatePool();
	}
	
	public static void save() {
		ProjectConfigutration.getInstance().save();
		ScenarioConfigutration.getInstance().save();
	}
	
	public static void generate() {
		new ObjectiveFunctionEquationGenerator().generate();
		new ProcessConstraintEquationGenerator().generate();
	}
}
