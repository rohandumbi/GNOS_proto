package com.org.gnos.services.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.org.gnos.db.dao.ProjectDAO;
import com.org.gnos.db.model.Project;

public class ProjectImportHelper implements ProjectTypes {
	
	private BufferedReader br = null;
	
	public void importProject(String fileName) {
		Map<Integer, List<String[]>> projectData = new HashMap<Integer, List<String[]>>();

		try {
			br = new BufferedReader(new FileReader(fileName));
			String line = null;
			
			while ((line = br.readLine()) != null) {
				String[] linedataArr = line.split("\\|");
				if(linedataArr.length > 1) {
					Integer ind = Integer.parseInt(linedataArr[0]);
					List<String[]> data = projectData.get(ind);
					if(data == null) {
						data = new ArrayList<String[]>();
						projectData.put(ind, data);
					}
					data.add(linedataArr);
				}
			}
			if(projectData.get(PROJECT_IND) == null) {
				throw new Exception("Uploaded data file is not valid");
			}
			processData(projectData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processData(Map<Integer, List<String[]>> projectData) {
		Project project = createProject(projectData.get(PROJECT_IND).get(0));
	}

	private Project createProject(String[] list) {
		Project project = new Project();
		project.setName(list[1]);
		project.setDesc(list[2]);
		for(int i = 3; i<list.length; i++) {
			project.addFile(list[i]);
		}
		new ProjectDAO().create(project);
		return project;
	}
	
}
