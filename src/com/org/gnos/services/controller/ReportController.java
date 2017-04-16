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

import com.google.gson.JsonObject;
import com.org.gnos.db.DBManager;
import com.org.gnos.db.dao.ExpressionDAO;
import com.org.gnos.db.dao.FieldDAO;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Field;


public class ReportController {

	//Report Types
	public final static short TYPE_EXPIT 			= 1;
	public final static short TYPE_RECLAIM 			= 2;
	public final static short TYPE_PROCESS			= 3;
	public final static short TYPE_TOTAL_MOVEMENT 	= 4;
	
	//Data types
	public final static short DATA_UNIT_FIELD 		= 1;
	public final static short DATA_EXPRESSION 		= 2;
	public final static short DATA_PRODUCT 			= 3;
	public final static short DATA_PRODUCT_JOIN 	= 4;
	public final static short DATA_TOTAL_TH 		= 5;
	public final static short DATA_GRADE 			= 6;
	
	public Map<Integer, ? > getReport(JsonObject jsonObject, String projectIdStr)  {
		String scenarioName = jsonObject.get("scenario_name").getAsString();
		short reportType =  jsonObject.get("report_type").getAsShort();
		short dataType = jsonObject.get("data_type").getAsShort();
		String dataName = jsonObject.get("data_name") == null ? null :  jsonObject.get("data_name").getAsString().replaceAll("\\s+", "_");
		String groupBy = jsonObject.get("group_by") == null ? null : jsonObject.get("group_by").getAsString().replaceAll("\\s+", "_");
		int projectId = Integer.parseInt(projectIdStr);
		
		
		Map<Integer, ?> reportData = new HashMap<Integer, Double>();
		
		if(dataName == null) return reportData;
		
		StringBuilder sqlbuilder = new StringBuilder("");
		String groupByClause = "group by period";
		if(dataType == DATA_GRADE) {
			short gradeType = jsonObject.get("grade_type").getAsShort();
			if(gradeType == 1) { //1 = Unit field
				FieldDAO fdao = new FieldDAO();
				List<Field> fields = fdao.getAllByType(projectId, Field.TYPE_GRADE);
				for(Field f: fields) {
					if(f.getName().equals(dataName)) {
						sqlbuilder.append("select period,  sum("+dataName+"_u)/sum("+f.getWeightedUnit()+") as value ");
					}
				}
				
			} else {
				ExpressionDAO edao = new ExpressionDAO();
				List<Expression> expressions = edao.getAll(projectId);
				for(Expression e: expressions) {				
					if(e.isGrade() && e.getName().equals(dataName)) {
						sqlbuilder.append("select period,  sum("+dataName+"_u)/sum("+e.getWeightedField()+") as value ");
					}
				}				
			}
		} else {
			sqlbuilder.append("select period,  sum("+dataName+") as value ");
		}
		
		
		if(groupBy != null) {
			sqlbuilder.append(", "+groupBy);
			groupByClause += ", "+groupBy;
			
			reportData = new HashMap<Integer, List<ReportData>>();
		}

		sqlbuilder.append(" from gnos_report_"+projectId +" where scenario_name = ? ");
		
		switch(reportType) {
			case TYPE_EXPIT: sqlbuilder.append(" AND origin_type = 1 "); break;
			case TYPE_RECLAIM: sqlbuilder.append(" AND origin_type = 2 "); break;
			case TYPE_PROCESS: sqlbuilder.append(" AND destination_type = 1 "); break;
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
				if(groupBy == null) {
					double quantity = resultSet.getDouble("value");
					((HashMap<Integer, Double>)reportData).put(period, quantity);				
				} else {
					List<ReportData> expitData = (List<ReportData>)reportData.get(period);
					if(expitData == null) {
						expitData = new ArrayList<ReportData>();
						((HashMap<Integer, List<ReportData>>)reportData).put(period, expitData);
					}
					ReportData expitReportData = new ReportData();
					expitReportData.name = resultSet.getString(groupBy);
					expitReportData.value = resultSet.getDouble("value");
					expitData.add(expitReportData);
				}
				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return reportData;
	}

	class ReportData {
		String name;
		double value;
		
	}
}
