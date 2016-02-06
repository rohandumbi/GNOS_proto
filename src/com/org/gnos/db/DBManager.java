package com.org.gnos.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.org.gnos.application.GNOSConfig;

public class DBManager {

	private static IConnectionPool pool;
	
	public static Connection getConnection(){
		if(pool == null) initializePool();
		
		return pool.getConnection();
	}
	
	
	private static void initializePool(){
		pool = new DBConnectionPool();
		pool.init();
	}
}
