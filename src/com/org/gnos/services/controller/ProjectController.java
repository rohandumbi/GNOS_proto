package com.org.gnos.services.controller;

import java.util.List;

import com.org.gnos.db.dao.ProjectDAO;
import com.org.gnos.db.model.Project;

public class ProjectController {

	public List<Project> getAllprojects() {
		return new ProjectDAO().getAll();
	}
}
