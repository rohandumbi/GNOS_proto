package com.org.gnos.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.org.gnos.application.GNOSConfig;

public class DBConnectionPool implements IConnectionPool {

	Connection conn = null;
	
	@Override
	public void init() {
		createConnections();
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public void releaseConnection() {
		// TODO Auto-generated method stub
		
	}
	
	private void createConnections(){
		
		String url = "jdbc:mysql://"+GNOSConfig.get("db.host")+":"+GNOSConfig.get("db.port")+"/"+GNOSConfig.get("db.schema");
		try {
			conn = DriverManager.getConnection(url, GNOSConfig.get("db.user"), GNOSConfig.get("db.password"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Connection getConnection() {
		// TODO Auto-generated method stub
		return conn;
	}

}
