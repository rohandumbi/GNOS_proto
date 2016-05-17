package com.org.gnos.test;

import java.io.IOException;

import com.org.gnos.application.GNOSConfig;
import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.db.dao.ProjectDAO;
import com.org.gnos.db.model.Project;
import com.org.gnos.services.EquationGenerator;
import com.org.gnos.services.csv.GNOSCSVDataProcessor;

public class TestApplication {

	
	private Project createProject () {
		Project project = new Project();
		project.setName("test1");
		project.setDesc("test1");
		project.setFileName("test1");
		new ProjectDAO().create(project);
		
		return project;
	}
	
	private void deleteProject (Project project) {
		
		new ProjectDAO().delete(project);
	}
	
	private void loadData(int projectId) {
		GNOSCSVDataProcessor.getInstance().processCsv("D:\\proj-workspace\\GitRepository\\PersonalWork\\GNOS_proto\\data\\GNOS_data_micro.csv");
		GNOSCSVDataProcessor.getInstance().dumpToDB(projectId);
		ProjectConfigutration.getInstance().load(projectId);
	}
	private boolean test() {
		
		Project project = createProject();
		loadData(project.getId());
		deleteProject(project);
		return true;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestApplication application = new TestApplication();
		
		GNOSConfig.load();
		//application.test();
		ProjectConfigutration.getInstance().load(3);

		new EquationGenerator().generate();

	}

}
