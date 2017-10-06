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

public class ProjectExportHelper implements ProjectTypes {
	
	public String export(int projectId) {
		StringBuilder output = new StringBuilder("");
		Project project = new ProjectDAO().get(projectId);
		if(project != null) {
			output.append(PROJECT_IND+"|"+project.toString()+"\n");
			//get Fields
			List<Field> fields = new FieldDAO().getAll(projectId);
			for(Field field: fields) {
				output.append(FIELD_IND+"|"+field.toString()+"\n");
			}
			//get RequiredFields 
			List<RequiredField> requireFields = new RequiredFieldDAO().getAll(projectId);
			for(RequiredField reqField: requireFields) {
				output.append(REQ_FIELD_IND+"|"+reqField.toString()+"\n");
			}
			//get models
			List<Model> models = new ModelDAO().getAll(projectId);
			for(Model model: models) {
				output.append(MODEL_IND+"|"+model.toString()+"\n");
			}
			//get Expressions
			List<Expression> exporessions = new ExpressionDAO().getAll(projectId);
			for(Expression expression: exporessions) {
				output.append(EXPRESSION_IND+"|"+expression.toString()+"\n");
			}
			//get scenarios
			List<Scenario> scenarios = new ScenarioDAO().getAll(projectId);
			for(Scenario scenario : scenarios) {
				output.append(SCENARIO_IND+"|"+scenario.toString()+"\n");
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
