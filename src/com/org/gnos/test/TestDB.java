package com.org.gnos.test;

import com.org.gnos.core.GNOSConfig;
import com.org.gnos.db.dao.ExpressionDAO;
import com.org.gnos.db.model.Expression;

public class TestDB {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GNOSConfig.load();
		Expression expression = new Expression();
		expression.setId(1);
		expression.setName("test1");
		expression.setExprvalue("a+b");
		expression.setGrade(true);
		new ExpressionDAO().update(expression);
	}

}
