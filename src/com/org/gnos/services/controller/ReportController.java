package com.org.gnos.services.controller;

import static com.org.gnos.db.dao.util.DAOUtil.prepareStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.org.gnos.db.DBManager;
import com.org.gnos.db.dao.ExpressionDAO;
import com.org.gnos.db.dao.FieldDAO;
import com.org.gnos.db.dao.ScenarioDAO;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Field;
import com.org.gnos.db.model.Scenario;
import com.org.gnos.scheduler.processor.Record;


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
	
	public String getReportCSV(JsonObject jsonObject, String projectId) throws Exception {
		
		JsonArray array = jsonObject.get("scenarioIds").getAsJsonArray();
		List<Scenario> scenarios = new ArrayList<Scenario>();
		ScenarioDAO  dao = new ScenarioDAO();
		for (int i = 0; i < array.size(); i++) {
			Scenario scenario = dao.get(array.getAsInt());
			if(scenario != null) {
				scenarios.add(scenario);
			}
		}
		if(scenarios.size() == 0) {
			throw new Exception("Please select a scenario.");
		}
		StringBuilder output = new StringBuilder("");
		Scenario scenario = scenarios.get(0);
		int startYear = scenario.getStartYear();
		int periodFieldIdx = -1;
		int originTypeIdx = -1;
		String sql = "select * from gnos_report_"+projectId +"_"+scenario.getId() +" where scenario_name = ? ";
		List<Integer> exclusionIdxList = new ArrayList<Integer>();
		
		Object[] values = { scenario.getName() };
		try (
				Connection connection = DBManager.getConnection();
				PreparedStatement statement = prepareStatement(connection, sql, false, values);
				ResultSet resultSet = statement.executeQuery();				
			){
			ResultSetMetaData md = resultSet.getMetaData();
			int columnCount = md.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				
				if(md.getColumnName(i).equals("pit_no")|| md.getColumnName(i).equals("bench_no")) {
					exclusionIdxList.add(i);
					continue;
				}
				output.append(md.getColumnName(i));
				if (md.getColumnName(i).equals("period")){
					periodFieldIdx = i;
				} else if (md.getColumnName(i).equals("origin_type")){
					originTypeIdx = i;
				}
				if(i == columnCount) {
					output.append("\n");
				} else {
					output.append(",");
				}
			}
			while(resultSet.next()) {
				for (int i = 1; i <= columnCount; i++) {
					if(exclusionIdxList.contains(i)) {
						continue;
					}
					if(i == periodFieldIdx){
						output.append(resultSet.getInt(i)+startYear - 1);
					} else if(i == originTypeIdx) {
						int originType = resultSet.getInt(i);
						if(originType == Record.ORIGIN_PIT) {
							output.append("expit");
						} else if(originType == Record.ORIGIN_SP) {
							output.append("stockpile");
						}
						
					} else {			
						output.append(resultSet.getString(i));
					}
					
					if(i == columnCount) {
						output.append("\n");
					} else {
						output.append(",");
					}
				}
			}
		} catch (SQLException e) {
				e.printStackTrace();
		}
				
		return output.toString();
	}
	
	public Map<Integer, ? > getReport(JsonObject jsonObject, String projectIdStr)  {
		String scenarioName = jsonObject.get("scenario_name").getAsString();
		short reportType =  jsonObject.get("report_type").getAsShort();
		short dataType = jsonObject.get("data_type").getAsShort();
		String dataName = jsonObject.get("data_name") == null ? null :  jsonObject.get("data_name").getAsString().replaceAll("\\s+", "_");
		String groupBy = jsonObject.get("group_by") == null ? null : jsonObject.get("group_by").getAsString().replaceAll("\\s+", "_");
		int projectId = Integer.parseInt(projectIdStr);
		Scenario  scenario = null;
		List<Scenario> scenarios = new ScenarioDAO().getAll(Integer.parseInt(projectIdStr));
		for(Scenario s: scenarios) {
			if(s.getName().equals(scenarioName)){
				scenario = s;
				break;
			}
		}
		
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

		sqlbuilder.append(" from gnos_report_"+projectId +"_"+scenario.getId()+" where scenario_name = ? ");
		
		switch(reportType) {
			case TYPE_EXPIT: sqlbuilder.append(" AND origin_type = 1 "); break;
			case TYPE_RECLAIM: sqlbuilder.append(" AND origin_type = 2 "); break;
			case TYPE_PROCESS: sqlbuilder.append(" AND destination_type = 1 "); break;
		}
		
		sqlbuilder.append(groupByClause);
		System.out.println("Report SQL :"+ sqlbuilder.toString());
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
