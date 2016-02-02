package com.org.gnos.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.org.gnos.application.GNOSConfig;

public class DBManager {

	private static Connection conn;
	
	public static Connection getConnection(){
		
		if(conn == null) {
			try {
				String url = "jdbc:mysql://"+GNOSConfig.get("db.host")+":"+GNOSConfig.get("db.port")+"/"+GNOSConfig.get("db.schema");
				conn = DriverManager.getConnection(url, GNOSConfig.get("db.user"), GNOSConfig.get("db.password"));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return conn;		
	}
}
