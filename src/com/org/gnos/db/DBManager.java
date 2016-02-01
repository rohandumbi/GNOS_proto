package com.org.gnos.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {

	private static Connection conn;
	private static String url = "jdbc:mysql://localhost:3306/gnos";
	private static String userName = "arpan";
	private static String pwd = "arpan";
	

	
	public static Connection getConnection(){
		
		if(conn == null) {
			try {
				conn = DriverManager.getConnection(url, userName, pwd);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return conn;		
	}
}
