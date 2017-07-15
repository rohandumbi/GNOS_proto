package com.org.gnos.services.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.org.gnos.db.DBManager;

public class ReserveDataController {

public List<List<String>> getAll(String projectId) {
		List<List<String>> data = new ArrayList<List<String>>();
		
		String sql = "select * from gnos_data_"+projectId ;
		try (
				Connection connection = DBManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet resultSet = statement.executeQuery();				
			){
			ResultSetMetaData md = resultSet.getMetaData();
			int columnCount = md.getColumnCount();
			List<String> headers = new ArrayList<String>();
			for (int i = 1; i <= columnCount; i++) {
				headers.add(md.getColumnName(i));
			}
			data.add(headers);
			while(resultSet.next()) {
				List<String> datarow = new ArrayList<String>();
				for (int i = 1; i <= columnCount; i++) {
					datarow.add(resultSet.getString(i));
				}
				data.add(datarow);
			}
		} catch (SQLException e) {
				e.printStackTrace();
		}
		return data;
	}
}
