package com.org.gnos.test;

import java.util.List;

import com.org.gnos.core.GNOSConfig;
import com.org.gnos.core.LogManager;
import com.org.gnos.db.DBManager;
import com.org.gnos.db.dao.ExpressionDAO;
import com.org.gnos.db.dao.ProjectDAO;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Project;
import com.org.gnos.db.model.RunConfig;
import com.org.gnos.scheduler.SchedulerService;
import com.org.gnos.services.ExpressionProcessor;
import com.org.gnos.services.PitBenchProcessor;
import com.org.gnos.services.csv.GNOSCSVDataProcessor;

public class TestApplication {

	
	private Project createProject () {
		Project project = new Project();
		project.setName("test1");
		project.setDesc("test1");
		new ProjectDAO().create(project);
		
		return project;
	}
	
	private void deleteProject (Project project) {
		
		new ProjectDAO().delete(project);
	}
	
	private void loadData(int projectId) {
		GNOSCSVDataProcessor processor = new GNOSCSVDataProcessor();
		processor.processCsv("D:\\proj-workspace\\GitRepository\\PersonalWork\\GNOS_proto\\data\\GNOS_data_micro.csv");
		processor.dumpToDB(projectId);
	}
	
	public void reloadData(int projectId) {
		GNOSCSVDataProcessor processor = new GNOSCSVDataProcessor();
		processor.processCsv("C:\\Arpan\\Workspace\\personal\\GNOS\\data\\electron_test_data_v2.csv");
		processor.reImportToDB(projectId);
		new PitBenchProcessor().updatePitBenchData(projectId);
		
		List<Expression> expressions = new ExpressionDAO().getAll(projectId);
		ExpressionProcessor expprocessor = new ExpressionProcessor();
		expprocessor.setExpressions(expressions);
		expprocessor.store(projectId);
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
		
		//Application.start();
		//new TestApplication().reloadData(1);

		GNOSConfig.load();
		LogManager.initialize();
		DBManager.initializePool();
		LogManager.log("This is test");
		short period = 3;
		short window = 2;
		short stepsize = 1;
		RunConfig runconfig = new RunConfig();
		runconfig.setMode(RunConfig.GLOBAL_MODE);
		runconfig.setPeriod(period);
		runconfig.setWindow(window);
		runconfig.setStepSize(stepsize);
		runconfig.setProjectId(2);
		runconfig.setScenarioId(3);
		SchedulerService service = new SchedulerService();
		service.setRunconfig(runconfig);
		service.execute();
	}

}
