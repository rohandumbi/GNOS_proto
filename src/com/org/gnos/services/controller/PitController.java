package com.org.gnos.services.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.org.gnos.db.DBManager;

public class PitController {
	
	public List<Pit> getAll(String projectId) {
		List<Pit> pits = new ArrayList<Pit>();
		String sql_required_mapping = "select mapped_field_name from required_field_mapping where field_name = 'pit_name' and project_id = "+projectId;
		try(
				Connection connection = DBManager.getConnection();
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(sql_required_mapping);
			){
				String pitFieldName = "pit_name";
				while(rs.next()){
					pitFieldName = rs.getString("mapped_field_name");
				}
				String sql = "select distinct a."+pitFieldName+" as pit_name, b.pit_no from gnos_data_"+projectId+" a, gnos_computed_data_"+projectId+" b where b.row_id = a.id";
				ResultSet rs1 = statement.executeQuery(sql);
				while(rs1.next()){
					Pit pit = new Pit();
					pit.pitName = rs1.getString("pit_name");
					pit.pitNo = rs1.getInt("pit_no");
					pits.add(pit);
				}

			} catch(SQLException e){
				e.printStackTrace();
			}
		
		return pits;
	}
	
	class Pit {
		String pitName;
		int pitNo;
	}
}
