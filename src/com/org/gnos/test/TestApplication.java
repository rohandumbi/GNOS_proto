package com.org.gnos.test;

import com.org.gnos.application.GNOSConfig;
import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.services.csv.GNOSCSVDataProcessor;

public class TestApplication {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GNOSConfig.load();
		GNOSCSVDataProcessor.getInstance().processCsv("D:\\proj-workspace\\GitRepository\\PersonalWork\\GNOS_proto\\data\\GNOS_data_micro.csv");
		GNOSCSVDataProcessor.getInstance().dumpToDB(1);
		ProjectConfigutration.getInstance().load(1);
	}

}
