package com.org.gnos.services.controller;

import static com.org.gnos.db.dao.util.DAOUtil.prepareStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.org.gnos.db.DBManager;
import com.org.gnos.db.dao.RequiredFieldDAO;
import com.org.gnos.db.model.RequiredField;

public class ReportController {

	private RequiredFieldDAO rdao;
	
	public ReportController() {
		rdao = new RequiredFieldDAO();
		
	}
	public Map<Integer, List<ExpitReportData>> getExpitReport(JsonObject jsonObject, String projectIdStr) {
		
		Map<Integer, List<ExpitReportData>> expitReportDataMap = new HashMap<Integer, List<ExpitReportData>>();
		List<RequiredField> rFields = rdao.getAll(Integer.parseInt(projectIdStr));
		String pitFieldName = "pit_name";
		for(RequiredField rField : rFields) {
			if(rField.getFieldName().equalsIgnoreCase("pit_name")) {
				pitFieldName = rField.getMappedFieldname();
			}
		}
		String sql = "select period, "+ pitFieldName+ ", sum(quantity_mined) as quantity ";
		String scenarioName = jsonObject.get("scenario_name").getAsString();
	
		sql += " from gnos_report_"+projectIdStr +" where scenario_name = ? ";
		sql += "group by period, "+ pitFieldName;

		Object[] values = { scenarioName };
		try (
				Connection connection = DBManager.getConnection();
				PreparedStatement statement = prepareStatement(connection, sql, false, values);
				ResultSet resultSet = statement.executeQuery();				
			){
			while(resultSet.next()) {
				int period = resultSet.getInt("period");
				List<ExpitReportData> expitData = expitReportDataMap.get(period);
				if(expitData == null) {
					expitData = new ArrayList<ExpitReportData>();
					expitReportDataMap.put(period, expitData);
				}
				ExpitReportData expitReportData = new ExpitReportData();
				expitReportData.pitName = resultSet.getString(pitFieldName);
				expitReportData.quantity = resultSet.getDouble("quantity");
				expitData.add(expitReportData);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return expitReportDataMap;
	}
	
	public Object getGradeReport(JsonObject jsonObject, String projectIdStr) {
		
		String sql = "select period, ";
		String scenarioName = jsonObject.get("scenario_name").getAsString();
		
		JsonArray groupByArr = null, fieldArr = null, filterdArr = null;
		if( jsonObject.get("group_by") != null ) {
			groupByArr = jsonObject.get("group_by").getAsJsonArray();
		}
		
		if( jsonObject.get("fields") != null ) {
			fieldArr = jsonObject.get("fields").getAsJsonArray();
		}
		
		if( jsonObject.get("filters") != null ) {
			filterdArr = jsonObject.get("filters").getAsJsonArray();
		}
		
		
		
		if(fieldArr == null || fieldArr.size() == 0) {
			sql += " * ";
		}
		for( JsonElement elm : fieldArr) {					
			
		}
		
		sql += " from gnos_report_"+projectIdStr +" where scenario_name = ? "; 
		if(filterdArr != null) {
			for( JsonElement elm : filterdArr) {
				sql += " AND "+ elm.getAsString();
			}
		}
		
		
		/*for( JsonElement elm : groupByArr) {
			
		}*/
		Object[] values = { scenarioName };
		try (
				Connection connection = DBManager.getConnection();
				PreparedStatement statement = prepareStatement(connection, sql, false, values);
				ResultSet resultSet = statement.executeQuery();				
			){
			
			return resultSet;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	class ExpitReportData {
		String pitName;
		double quantity;
		
	}
}
