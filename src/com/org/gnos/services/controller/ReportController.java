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
	public Map<Integer, ? > getExpitReport(JsonObject jsonObject, String projectIdStr) {
		
		Map<Integer, ?> expitDataMap = null;
		StringBuilder sqlbuilder = new StringBuilder("");
		String groupByClause = "";
		String pitFieldName = "pit_name";
		short groupbytype;
		
		String scenarioName = jsonObject.get("scenario_name").getAsString();
		String groupBy = null;
		JsonElement groupByElement = jsonObject.get("group_by");
		JsonElement filtersElement = jsonObject.get("filters");
		if(groupByElement != null) {
			groupBy = groupByElement.getAsString();
		}
			
		if(groupBy == null) {			
			expitDataMap = new HashMap<Integer, Double>();
			groupbytype = 1;
			sqlbuilder.append("select period,  sum(quantity_mined) as quantity ");
			groupByClause = "group by period";
		} else if(groupBy.equalsIgnoreCase("PIT")){
			expitDataMap = new HashMap<Integer, List<ExpitReportData>>();
			groupbytype = 2;
			List<RequiredField> rFields = rdao.getAll(Integer.parseInt(projectIdStr));
			for(RequiredField rField : rFields) {
				if(rField.getFieldName().equalsIgnoreCase("pit_name")) {
					pitFieldName = rField.getMappedFieldname();
				}
			}
			sqlbuilder.append("select period, "+ pitFieldName+ " as group_by_name, sum(quantity_mined) as quantity ");
			groupByClause = "group by period, "+ pitFieldName;
		} else if( groupBy.equalsIgnoreCase("DESTINATION_TYPE")) {
			expitDataMap = new HashMap<Integer, List<ExpitReportData>>();
			groupbytype = 3;
			sqlbuilder.append("select period, destination_type as group_by_name, sum(quantity_mined) as quantity ");
			groupByClause = "group by period, destination_type";
		} else {
			return null;
		}
		sqlbuilder.append(" from gnos_report_"+projectIdStr +" where scenario_name = ? ");
		
		if(filtersElement != null) {
			JsonArray filterdArr = jsonObject.get("filters").getAsJsonArray();
			if(filterdArr != null) {
				for( JsonElement elm : filterdArr) {
					sqlbuilder.append(" AND "+ elm.getAsString());
				}
			}
		}
		if(groupbytype == 2) {
			sqlbuilder.append(" AND origin_type = 1 ");
		}
		
		sqlbuilder.append(groupByClause);

		Object[] values = { scenarioName };
		try (
				Connection connection = DBManager.getConnection();
				PreparedStatement statement = prepareStatement(connection, sqlbuilder.toString(), false, values);
				ResultSet resultSet = statement.executeQuery();				
			){
			while(resultSet.next()) {
				int period = resultSet.getInt("period");
				if(groupbytype == 1) {
					double quantity = resultSet.getDouble("quantity");
					((HashMap<Integer, Double>)expitDataMap).put(period, quantity);
				} else {
					List<ExpitReportData> expitData = (List<ExpitReportData>)expitDataMap.get(period);
					if(expitData == null) {
						expitData = new ArrayList<ExpitReportData>();
						((HashMap<Integer, List<ExpitReportData>>)expitDataMap).put(period, expitData);
					}
					ExpitReportData expitReportData = new ExpitReportData();
					expitReportData.name = resultSet.getString("group_by_name");
					expitReportData.quantity = resultSet.getDouble("quantity");
					expitData.add(expitReportData);
				}
				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return expitDataMap;
	}
	
	class ExpitReportData {
		String name;
		double quantity;
		
	}
}
