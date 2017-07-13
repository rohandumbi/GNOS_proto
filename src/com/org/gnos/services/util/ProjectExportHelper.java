package com.org.gnos.services.util;

import java.util.List;

import com.org.gnos.db.dao.ExpressionDAO;
import com.org.gnos.db.dao.FieldDAO;
import com.org.gnos.db.dao.ModelDAO;
import com.org.gnos.db.dao.ProjectDAO;
import com.org.gnos.db.dao.RequiredFieldDAO;
import com.org.gnos.db.dao.ScenarioDAO;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Field;
import com.org.gnos.db.model.Model;
import com.org.gnos.db.model.Project;
import com.org.gnos.db.model.RequiredField;
import com.org.gnos.db.model.Scenario;

public class ProjectExportHelper {

	private static final int PROJECT_INFO = 1;
	private static final int SCENARIO_INFO = 2;
	
	public String export(int projectId) {
		StringBuilder output = new StringBuilder("");
		Project project = new ProjectDAO().get(projectId);
		if(project != null) {
			output.append(PROJECT_INFO+"|"+project.toString()+"\n");
			//get Fields
			List<Field> fields = new FieldDAO().getAll(projectId);
			//get RequiredFields 
			List<RequiredField> requireFields = new RequiredFieldDAO().getAll(projectId);
			//get models
			List<Model> models = new ModelDAO().getAll(projectId);
			//get Expressions
			List<Expression> exporessions = new ExpressionDAO().getAll(projectId);
			//get scenarios
			List<Scenario> scenarios = new ScenarioDAO().getAll(projectId);
			for(Scenario scenario : scenarios) {
				output.append(SCENARIO_INFO+"|"+scenario.toString()+"\n");
				output.append(exportScenario(scenario.getId()));
			}
		}
		return output.toString();
	}
	
	public String exportScenario(int scenarioId) {	
		StringBuilder output = new StringBuilder("");
		
		return output.toString();
	}
}
