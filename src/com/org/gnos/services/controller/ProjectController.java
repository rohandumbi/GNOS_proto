package com.org.gnos.services.controller;

import java.util.List;

import com.google.gson.JsonObject;
import com.org.gnos.db.dao.ProjectDAO;
import com.org.gnos.db.model.Project;
import com.org.gnos.services.PitBenchProcessor;
import com.org.gnos.services.csv.GNOSCSVDataProcessor;

public class ProjectController {
	
	private ProjectDAO dao = null;
	
	public ProjectController() {
		dao = new ProjectDAO();
	}

	public List<Project> getAll() {
		return dao.getAll();
	}
	
	public Project create(JsonObject jsonObject) throws Exception {
			String name = jsonObject.get("name").getAsString();
			String desc = jsonObject.get("desc").getAsString();
			String fileName = jsonObject.get("fileName").getAsString();
			Project newProject = new Project();
			newProject.setName(name);
			newProject.setDesc(desc);
			newProject.setFileName(fileName);

			boolean created = dao.create(newProject);
			loadCSVFile(fileName, newProject.getId());
			if(created) return newProject;
			throw new Exception();
	}
	

	public boolean delete(String projectId) {
		if((projectId == null) || (projectId.isEmpty())){
			return false;
		}else{
			Project project = new Project();
			project.setId(Integer.parseInt(projectId));
			dao.delete(project);
			return true;
		}	
	}
	
	
	private void loadCSVFile(String fileName, int projectId ) {
		GNOSCSVDataProcessor gnosCsvDataProcessor = GNOSCSVDataProcessor.getInstance();
		gnosCsvDataProcessor.processCsv(fileName);
		gnosCsvDataProcessor.storeFields(projectId);
		gnosCsvDataProcessor.dumpToDB(projectId);
		new PitBenchProcessor().updatePitBenchData(projectId);
	}
}
