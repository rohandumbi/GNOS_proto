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
		//DBManager.releaseConnection(conn);
		try {
			Statement stmt = conn.createStatement();
			Thread.sleep(40000);
			//conn = DBManager.getConnection();
			
			stmt.executeQuery("select * from expressions");
			Thread.sleep(40000);
			stmt.executeQuery("select * from expressions");
			DBManager.releaseConnection(conn);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
