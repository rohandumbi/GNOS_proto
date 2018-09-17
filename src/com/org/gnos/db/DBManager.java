package com.org.gnos.db;

import java.sql.Connection;

public class DBManager {

	private static IConnectionPool pool;
	
	public static Connection getConnection(){
		if(pool == null) initializePool();		
		return pool.getConnection();
	}
	
	public static void releaseConnection(Connection conn){
		if(pool != null && conn != null){
			pool.releaseConnection(conn);
		}	
	}
	
	public static void initializePool(){
		if(pool == null) {
			pool = new DBConnectionPool();
			pool.init();
		}
	}
	
	public static void terminatePool(){
		if(pool != null){
			pool.destroy();
		}
		
	}
}
