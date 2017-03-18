package com.org.gnos.test;

import java.util.List;

import com.org.gnos.core.Application;
import com.org.gnos.db.dao.ExpressionDAO;
import com.org.gnos.db.dao.ProjectDAO;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Project;
import com.org.gnos.services.ExpressionProcessor;
import com.org.gnos.services.PitBenchProcessor;
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
	}
	
	public void reloadData(int projectId) {
		GNOSCSVDataProcessor.getInstance().processCsv("C:\\Arpan\\Workspace\\personal\\GNOS\\data\\electron_test_data_v2.csv");
		GNOSCSVDataProcessor.getInstance().reImportToDB(projectId);
		new PitBenchProcessor().updatePitBenchData(projectId);
		
		List<Expression> expressions = new ExpressionDAO().getAll(projectId);
		ExpressionProcessor processor = new ExpressionProcessor();
		processor.setExpressions(expressions);
		processor.store(projectId);
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
		
		Application.start();
		//new TestApplication().reloadData(1);

		/*RunConfig runconfig = new RunConfig();
		runconfig.setMode(RunConfig.GLOBAL_MODE);
		runconfig.setProjectId(1);
		runconfig.setScenarioId(2);
		SchedulerService service = SchedulerService.getInstance();
		service.setRunconfig(runconfig);
		service.execute();*/
	}

}
