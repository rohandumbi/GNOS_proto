package com.org.gnos.db;

import java.sql.Connection;

public interface IConnectionPool {

	public void init();
	
	public void destroy();
	
	public Connection getConnection();
	
	public void releaseConnection();
	
}
