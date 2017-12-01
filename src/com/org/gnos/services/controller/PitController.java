package com.org.gnos.services.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.org.gnos.db.DBManager;
import com.org.gnos.db.dao.RequiredFieldDAO;
import com.org.gnos.db.model.RequiredField;

public class PitController {
	
	public List<Pit> getAll(String projectId) {
		List<Pit> pits = new ArrayList<Pit>();
		RequiredFieldDAO rdo = new RequiredFieldDAO();
		List<RequiredField> requiredFields = rdo.getAll(Integer.parseInt(projectId));
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
	
	public List<Bench> getAllBenchesForPit(String projectId, String pitNo) {
		List<Bench> benches = new ArrayList<Bench>();
		RequiredFieldDAO rdo = new RequiredFieldDAO();
		List<RequiredField> requiredFields = rdo.getAll(Integer.parseInt(projectId));
		try(
				Connection connection = DBManager.getConnection();
				Statement statement = connection.createStatement();
			){
				String benchFieldName = "bench_rl";
				if(requiredFields != null){
					for(RequiredField rf: requiredFields){
						if(rf.getFieldName().equals("bench_rl")){
							benchFieldName = rf.getMappedFieldname();
							break;
						}
					}
				}
				String sql = "select distinct a."+benchFieldName+" as bench_name, b.bench_no from gnos_data_"+projectId+" a, gnos_computed_data_"+projectId+" b where b.row_id = a.id and b.pit_no ="+pitNo;
				ResultSet rs1 = statement.executeQuery(sql);
				while(rs1.next()){
					Bench bench = new Bench();
					bench.benchName = rs1.getString("bench_name");
					bench.benchNo = rs1.getInt("bench_no");
					benches.add(bench);
				}

			} catch(SQLException e){
				e.printStackTrace();
			}
		
		return benches;
	}
	class Pit {
		String pitName;
		int pitNo;
		List<Bench> benches;
	}
	
	class Bench {
		String benchName;
		int benchNo;
	}
}
