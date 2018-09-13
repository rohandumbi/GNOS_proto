package com.org.gnos.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.org.gnos.core.GNOSConfig;
import com.org.gnos.db.DBManager;

public class TestDB {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GNOSConfig.load();
/*		Expression expression = new Expression();
		expression.setId(1);
		expression.setName("test1");
		expression.setExprvalue("a+b");
		expression.setGrade(true);
		new ExpressionDAO().update(expression);*/
		Connection conn = DBManager.getConnection();
		DBManager.releaseConnection(conn);
		try {
			Thread.sleep(80000);
			conn = DBManager.getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeQuery("select * from gnos_expressions");
			DBManager.releaseConnection(conn);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
