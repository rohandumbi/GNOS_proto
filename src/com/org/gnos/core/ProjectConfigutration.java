package com.org.gnos.core;

public class ProjectConfigutration {

	final static ProjectConfigutration instance = new ProjectConfigutration();

	private int projectId = -1;

	public static ProjectConfigutration getInstance() {
		return instance;
	}

	public void load(int projectId) {

		if (projectId == -1) {
			System.err
			.println("Can not load project unless projectId is present");
			return;
		}
		this.projectId = projectId;

	}	

}
