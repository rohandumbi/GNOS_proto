package com.org.gnos.test;

import com.org.gnos.application.GNOSConfig;
import com.org.gnos.db.dao.ProjectDAO;

public class TestDB {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GNOSConfig.load();
		new ProjectDAO().getAll();
	}

}
