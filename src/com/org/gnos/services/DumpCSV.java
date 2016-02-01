package com.org.gnos.services;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.org.gnos.db.DBManager;

public class DumpCSV {
	
	
	public boolean dump(String fileName){
		BufferedReader br = null;
		String line = "";
		int count = 0;
		
		try {
			br = new BufferedReader(new FileReader(fileName));		
			while ((line = br.readLine()) != null) {				
				if(count == 0){
					parseHeader(line);
				} else {
					parseRecord(line);
				}			
				count++;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return true;
	}

	private void parseHeader(String line) throws Exception {
		
		String rec[] = line.split(",");
		validate();
		dropTable();
		createTable(rec);
		
	}
	
	private void parseRecord(String line) throws SQLException{
		String rec[] = line.split(",");
		Statement stmt = null;
		String  sql = "insert into gnos_data values (";
		
		for(int i =0; i< rec.length; i++){
			if(rec[i] != null){
				sql += "'"+rec[i]+ "'";
			}			
			if(i<rec.length-1) sql += ", ";
		}
		sql += ");";
		
		try {
			Connection conn = DBManager.getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} finally {
			if(stmt != null){
				stmt.close();
			}
		}
	}
	
	private boolean validate() {
		return true;
	}
	
	private void dropTable() throws SQLException {
		
		Statement stmt = null;
		String  sql = "DROP TABLE IF EXISTS gnos_data;";
		
		try {
			Connection conn = DBManager.getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} finally {
			if(stmt != null){
				stmt.close();
			}
		}
	}

	private void createTable(String[] rec) throws SQLException {
		
		Statement stmt = null;
		String  sql = "CREATE TABLE gnos_data ( ";
		
		for(int i =0; i< rec.length; i++){
			String columnName = rec[i].replaceAll("\\s+","_").toLowerCase();
			sql += columnName +" VARCHAR(40)";
			if(i<rec.length-1) sql += ", ";
		}
		sql += ");";

		try {
			Connection conn = DBManager.getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} finally {
			if(stmt != null){
				stmt.close();
			}
		}
	}
	
	public static void main(String[] args) {
		DumpCSV dumper = new DumpCSV();
		dumper.dump("C:\\Arpan\\Workspace\\personal\\workspace\\GNOS_proto\\data\\GNOS_Test_data.csv");
	}
}
