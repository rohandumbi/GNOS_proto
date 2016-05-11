package com.org.gnos.test;

import com.org.gnos.application.GNOSConfig;
import com.org.gnos.db.dao.ProjectDAO;
import com.org.gnos.db.model.Project;

public class TestDB {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GNOSConfig.load();
		Project project = new Project();
		project.setName("test1");
		project.setDesc("test1");
		project.setFileName("test1");
		new ProjectDAO().create(project);
	}

}
