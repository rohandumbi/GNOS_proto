package com.org.gnos.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.db.DBManager;
import com.org.gnos.services.csv.GNOSCSVDataProcessor;


public class PitBenchProcessor {

	private Map<String, PitBenchMappingData> pitBenchMapping = null;
	private Map<String, String> requiredFieldMap = new LinkedHashMap<String, String>();
	
	public void updatePitBenchData(int projectId){
		loadRequiredFieldMapping(projectId);
		parsePitAndBenchData();
		updateDB(projectId);
	}
	
	private void loadRequiredFieldMapping(int projectId) {
		String sql_required_mapping = "select field_name, mapped_field_name from required_field_mapping where project_id = "+projectId;
		try(
				Connection connection = DBManager.getConnection();
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(sql_required_mapping);
			){
				while(rs.next()){
					requiredFieldMap.put(rs.getString("field_name"), rs.getString("mapped_field_name"));
				}
			} catch(SQLException e){
				e.printStackTrace();
			}
		
	}

	private void parsePitAndBenchData() {

		//Map<String, String> requiredFieldMap = ProjectConfigutration.getInstance().getRequiredFieldMapping();
		pitBenchMapping = new HashMap<String, PitBenchMappingData>();
		String[] columns = GNOSCSVDataProcessor.getInstance().getHeaderColumns();
		List<String[]> data = GNOSCSVDataProcessor.getInstance().getData();
		String pitNameAlias = requiredFieldMap.get("pit_name");
		String benchNameAlias = requiredFieldMap.get("bench_rl");
		int pitNameColIdx = -1;
		int benchColIdx = -1;
		for(int j=0; j < columns.length;j++){
			if(columns[j].equalsIgnoreCase(pitNameAlias)){
				pitNameColIdx = j;
			} else if(columns[j].equalsIgnoreCase(benchNameAlias)){
				benchColIdx = j;
			}
			if( pitNameColIdx >= 0 && benchColIdx >= 0 ){
				break;
			}
		}

		for(int i=0; i < data.size(); i++) {
			String[] rowValues = data.get(i);
			int pitNo = -1;
			String pitName = rowValues[pitNameColIdx];
			Integer bench_rl = Integer.parseInt(rowValues[benchColIdx]);
			PitBenchMappingData mappingData = pitBenchMapping.get(pitName);
			if(mappingData != null){
				mappingData.addBenchData(bench_rl);
				pitNo = mappingData.pitNo;
			} else {
				pitNo = pitBenchMapping.size()+1;
				mappingData = new PitBenchMappingData(pitNo);
				mappingData.addBenchData(bench_rl);
				pitBenchMapping.put(pitName, mappingData);
			}
		}
	}
	
	private void updateDB(int projectId) {

		Connection conn = DBManager.getConnection();
		//Map<String, String> requiredFieldMap = ProjectConfigutration.getInstance().getRequiredFieldMapping();
		String pitNameAlias = requiredFieldMap.get("pit_name");
		String benchNameAlias = requiredFieldMap.get("bench_rl");
		String blockNoAlias = requiredFieldMap.get("block");
		String update_sql = " update   gnos_data_"+projectId+" a, gnos_computed_data_"+projectId+" b set b.pit_no = ?, b.bench_no = ? , b.block_no = a."+blockNoAlias+" where a.id = b.row_id AND a."+pitNameAlias+" = ? AND a."+benchNameAlias+" = ? ";
		PreparedStatement pstmt = null;
		boolean autoCommit = true;

		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(update_sql);
			Set<String> keys = pitBenchMapping.keySet();
			Iterator<String> it = keys.iterator();
			
			while(it.hasNext()){
				String pitName = it.next();
				PitBenchMappingData data= pitBenchMapping.get(pitName);
				List<Integer>  benchValues= data.benchValues;
				Collections.reverse(benchValues);
				int i = 1;
				for(Integer benchValue: benchValues) {
					pstmt.setInt(1, data.pitNo);
					pstmt.setInt(2, i);
					pstmt.setString(3, pitName);
					pstmt.setInt(4, benchValue);
					pstmt.executeUpdate();
					i++;
				}
			}
			conn.commit();
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
				if(pstmt != null) pstmt.close();
				if(conn != null) DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	class PitBenchMappingData{
		int pitNo = -1;
		List<Integer> benchValues = new ArrayList<Integer>();
		
		PitBenchMappingData(int pitNo) {
			this.pitNo = pitNo;
		}
		void addBenchData(Integer benchVal) {
			if(benchValues.indexOf(benchVal) == -1){
				benchValues.add(benchVal);
				Collections.sort(benchValues);
			}
		}
	}
}
