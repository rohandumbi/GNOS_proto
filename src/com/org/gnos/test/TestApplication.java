package com.org.gnos.test;

import com.org.gnos.core.GNOSConfig;
import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.core.ScenarioConfigutration;
import com.org.gnos.db.dao.ProjectDAO;
import com.org.gnos.db.model.Project;
import com.org.gnos.services.EquationGeneratorService;
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
	public boolean test() {
		
		Project project = createProject();
		loadData(project.getId());
		deleteProject(project);
		return true;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		GNOSConfig.load();
		ProjectConfigutration.getInstance().load(19);
		ScenarioConfigutration.getInstance().load(20);
		EquationGeneratorService.getInstance().execute();

		
	}

}
