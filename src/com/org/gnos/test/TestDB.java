package com.org.gnos.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.org.gnos.application.GNOSConfig;
import com.org.gnos.db.DBManager;

public class TestDB {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GNOSConfig.load();
		int  i= 0;
		while(i < 4) {
			try (
					Connection con = DBManager.getConnection();
					Statement stmt = con.createStatement();
			){
				System.out.println("");
				i++;
			} catch (SQLException e) {
				i++;
				e.printStackTrace();
			}
		}
	}

}
