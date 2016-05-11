package com.org.gnos.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.org.gnos.application.GNOSConfig;

public class DBConnectionPool implements IConnectionPool {

	private List<Connection> connections = new ArrayList<Connection>();
	
	private int min_connections = 1;
	private int max_connections = 1;
	
	private String url = null;
	private String user = null;
	private String password = null;
	
	@Override
	public void init() {
		url = "jdbc:mysql://"+GNOSConfig.get("db.host")+":"+GNOSConfig.get("db.port")+"/"+GNOSConfig.get("db.schema");
		user = GNOSConfig.get("db.user");
		password = GNOSConfig.get("db.password");
		
		min_connections = Integer.parseInt(GNOSConfig.get("db.pool.min"));
		max_connections = Integer.parseInt(GNOSConfig.get("db.pool.max"));
		for(int i=0; i < min_connections; i++){
			this.createConnection();
		}
		
	}

	@Override
	public void destroy() {
		for(int i=0; i < connections.size(); i++) {
			try {
				((DBConnection)connections.get(i)).closeConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	
	@Override
	public synchronized Connection getConnection() {
		Connection conn = null;
		for(int i=0; i < connections.size(); i++) {
			if(!((DBConnection)connections.get(i)).isInUse()) {
				conn = connections.get(i);
			}
		} 
		
		if(conn == null && connections.size() < max_connections){
			conn = createConnection();
		}
		((DBConnection)conn).setInUse(true);
		return conn;
	}
	
	@Override
	public synchronized void releaseConnection(Connection conn) {		
		((DBConnection)conn).setInUse(false);
	}
	
	private Connection createConnection(){
		Connection dbconnection = null;
		try {
			System.out.println("Creating new connection");
			Connection conn = DriverManager.getConnection(url, user, password);
			dbconnection = new DBConnection(conn);
			connections.add(dbconnection);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dbconnection;
	}



}
