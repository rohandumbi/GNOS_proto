package com.org.gnos.services.controller;

import java.util.List;

import com.org.gnos.db.dao.FieldDAO;
import com.org.gnos.db.model.Field;

public class FieldController {
	
	FieldDAO dao; 
	public FieldController() {
		dao = new FieldDAO();
	}
	public List<Field> getAllFields(String projectId) {
		return dao.getAll();
	}
}
