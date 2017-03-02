package com.org.gnos.test;

import com.org.gnos.core.GNOSConfig;
import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.db.dao.ProjectDAO;
import com.org.gnos.db.model.Project;
import com.org.gnos.services.csv.GNOSCSVDataProcessor;
import com.org.gnos.services.endpoints.BenchConstraintEndpoint;
import com.org.gnos.services.endpoints.CapexEndpoint;
import com.org.gnos.services.endpoints.DumpDependencyEndpoint;
import com.org.gnos.services.endpoints.DumpEndpoint;
import com.org.gnos.services.endpoints.ExpressionEndpoint;
import com.org.gnos.services.endpoints.FieldEndpoint;
import com.org.gnos.services.endpoints.FixedCostEndpoint;
import com.org.gnos.services.endpoints.GradeConstraintEndpoint;
import com.org.gnos.services.endpoints.ModelEndpoint;
import com.org.gnos.services.endpoints.OpexEndpoint;
import com.org.gnos.services.endpoints.PitDependencyEndpoint;
import com.org.gnos.services.endpoints.PitEndpoint;
import com.org.gnos.services.endpoints.ProcessConstraintEndpoint;
import com.org.gnos.services.endpoints.ProcessEndpoint;
import com.org.gnos.services.endpoints.ProjectEndpoint;
import com.org.gnos.services.endpoints.ScenarioEndpoint;
import com.org.gnos.services.endpoints.StockpileEndpoint;

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
	public static void testService() {
		new PitEndpoint();
		new ProjectEndpoint();
		new FieldEndpoint();
		new ExpressionEndpoint();
		new ModelEndpoint();
		new DumpEndpoint();
		new StockpileEndpoint();
		new ScenarioEndpoint();
		new OpexEndpoint();
		new FixedCostEndpoint();
		new CapexEndpoint();
		new PitDependencyEndpoint();
		new DumpDependencyEndpoint();
		new BenchConstraintEndpoint();
		new ProcessConstraintEndpoint();
		new GradeConstraintEndpoint();
		new ProcessEndpoint();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		GNOSConfig.load();
/*		ProjectConfigutration.getInstance().load(24);
		ScenarioConfigutration.getInstance().load(25);
		SchedulerService.getInstance().execute();*/
		testService();
	}

}
