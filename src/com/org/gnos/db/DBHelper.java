package com.org.gnos.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DBHelper {

	public static void updateDB(String sql) {
		Statement stmt = null;
		Connection conn = DBManager.getConnection();

		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBManager.releaseConnection(conn);
			if(stmt != null){
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
