package com.org.gnos.services.controller;

import java.util.List;

import com.org.gnos.db.dao.ExpressionDAO;
import com.org.gnos.db.model.Expression;

public class ExpressionController {

	public List<Expression> getAllExpressions() {	
		return new ExpressionDAO().getAll();
	}
}
