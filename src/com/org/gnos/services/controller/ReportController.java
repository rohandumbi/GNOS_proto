package com.org.gnos.services.controller;

import static com.org.gnos.db.dao.util.DAOUtil.prepareStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
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
			Scenario scenario = dao.get(array.get(i).getAsInt());
			if(scenario != null) {
				scenarios.add(scenario);
			}
		}
		if(scenarios.size() == 0) {
			throw new Exception("Please select a scenario.");
		}
		StringBuilder output = new StringBuilder("");
		List<ResultSet> resultSets = new ArrayList<ResultSet>();
		List<Statement> statements = new ArrayList<Statement>();
		List<String> columns = new ArrayList<String>();
		Map<Integer, Map<Integer, Integer>> scenarioIdxListMap = new HashMap<Integer, Map<Integer, Integer>>();
		List<Integer> exclusionIdxList = new ArrayList<Integer>();
		int periodFieldIdx = -1;
		int originTypeIdx = -1;
		try {
			for(Scenario scenario: scenarios) {
				String sql = "select * from gnos_report_"+projectId +"_"+scenario.getId();
				try ( Connection connection = DBManager.getConnection(); ){
					Statement statement  = connection.createStatement();
					ResultSet rs = statement.executeQuery(sql);
					resultSets.add(rs);
					statements.add(statement);
				} catch (SQLException sqle) {
					System.err.println(sqle.getMessage());
				} 
			}
			
			int count = 0;
			for(ResultSet rs: resultSets) {
				Scenario  scenario = scenarios.get(count);
				Map<Integer, Integer> idxRsMap = new HashMap<Integer, Integer>();
				scenarioIdxListMap.put(scenario.getId(), idxRsMap);
				ResultSetMetaData md = rs.getMetaData();
				int columnCount = md.getColumnCount();
				for (int i = 1; i <= columnCount; i++) {
					String columnName = md.getColumnName(i);
					if(!columns.contains(columnName)) {
						columns.add(columnName);
						idxRsMap.put(columns.size()-1, i);
					} else {
						idxRsMap.put(columns.indexOf(columnName), i);
					}
				}
				count ++;
			}
			
			
			for (int i = 0; i < columns.size(); i++) {
				String columnName = columns.get(i);
				if(columnName.equals("pit_no")|| columnName.equals("bench_no")) {
					exclusionIdxList.add(i);
					continue;
				}
				output.append(columnName);
				if (columnName.equals("period")){
					periodFieldIdx = i;
				} else if (columnName.equals("origin_type")){
					originTypeIdx = i;
				}
				if(i == columns.size() -1 ) {
					output.append("\n");
				} else {
					output.append(",");
				}
			}
			
			count = 0;
			for(ResultSet rs: resultSets) {
				Scenario  scenario = scenarios.get(count);
				int startYear = scenario.getStartYear();
				Map<Integer, Integer> idxRsMap  = scenarioIdxListMap.get(scenario.getId());
				while(rs.next()) {
					for(int i=0; i < columns.size(); i++) {
						if(exclusionIdxList.contains(i)) {
							continue;
						}
						Integer rsIndx = idxRsMap.get(i);
						if(rsIndx != null) {						
							if(i == periodFieldIdx){
								output.append(rs.getInt(rsIndx)+startYear - 1);
							} else if(i == originTypeIdx) {
								int originType = rs.getInt(rsIndx);
								if(originType == Record.ORIGIN_PIT) {
									output.append("expit");
								} else if(originType == Record.ORIGIN_SP) {
									output.append("stockpile");
								}
								
							} else {			
								output.append(rs.getString(rsIndx));
							}
						}
						
						if(i == columns.size() -1 ) {
							output.append("\n");
						} else {
							output.append(",");
						}
					}
				}
				
				count ++;
			}
		} catch(Exception e) { 
			e.printStackTrace();
		} finally {
			for(ResultSet rs: resultSets) {
				if(rs != null && !rs.isClosed()) {
					rs.close();
				}
			}
			for(Statement stmt: statements) {
				if(stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
			}
		}
		
	
		return output.toString();
	}
	
	public String getCapexReportCSV(JsonObject jsonObject, String projectId) throws Exception {
		
		JsonArray array = jsonObject.get("scenarioIds").getAsJsonArray();
		List<Scenario> scenarios = new ArrayList<Scenario>();
		ScenarioDAO  dao = new ScenarioDAO();
		for (int i = 0; i < array.size(); i++) {
			Scenario scenario = dao.get(array.get(i).getAsInt());
			if(scenario != null) {
				scenarios.add(scenario);
			}
		}
		if(scenarios.size() == 0) {
			throw new Exception("Please select a scenario.");
		}
		StringBuilder output = new StringBuilder("");
		List<ResultSet> resultSets = new ArrayList<ResultSet>();
		List<Statement> statements = new ArrayList<Statement>();
		List<String> columns = new ArrayList<String>();
		Map<Integer, Map<Integer, Integer>> scenarioIdxListMap = new HashMap<Integer, Map<Integer, Integer>>();
		List<Integer> exclusionIdxList = new ArrayList<Integer>();
		try {
			for(Scenario scenario: scenarios) {
				String sql = "select * from gnos_capex_report_"+projectId +"_"+scenario.getId();
				try ( Connection connection = DBManager.getConnection(); ){
					Statement statement  = connection.createStatement();
					ResultSet rs = statement.executeQuery(sql);
					resultSets.add(rs);
					statements.add(statement);
				} catch (SQLException sqle) {
					System.err.println(sqle.getMessage());
				} 
			}
			
			int count = 0;
			for(ResultSet rs: resultSets) {
				Scenario  scenario = scenarios.get(count);
				Map<Integer, Integer> idxRsMap = new HashMap<Integer, Integer>();
				scenarioIdxListMap.put(scenario.getId(), idxRsMap);
				ResultSetMetaData md = rs.getMetaData();
				int columnCount = md.getColumnCount();
				for (int i = 1; i <= columnCount; i++) {
					String columnName = md.getColumnName(i);
					if(!columns.contains(columnName)) {
						columns.add(columnName);
						idxRsMap.put(columns.size()-1, i);
					} else {
						idxRsMap.put(columns.indexOf(columnName), i);
					}
				}
				count ++;
			}
			
			
			for (int i = 0; i < columns.size(); i++) {
				String columnName = columns.get(i);
				if(!columnName.equals("period") && !columnName.equals("total_capex") && !columnName.equals("capex_dcf") && !columnName.endsWith("_cost")) {
					exclusionIdxList.add(i);
					continue;
				}
				output.append(columnName);
				if(i == columns.size() -1 ) {
					output.append("\n");
				} else {
					output.append(",");
				}
			}
			
			count = 0;
			for(ResultSet rs: resultSets) {
				Scenario  scenario = scenarios.get(count);
				int startYear = scenario.getStartYear();
				Map<Integer, Integer> idxRsMap  = scenarioIdxListMap.get(scenario.getId());
				while(rs.next()) {
					for(int i=0; i < columns.size(); i++) {
						if(exclusionIdxList.contains(i)) {
							continue;
						}
						Integer rsIndx = idxRsMap.get(i);
						
						output.append(rs.getString(rsIndx));
						if(i == columns.size() -1 ) {
							output.append("\n");
						} else {
							output.append(",");
						}
					}
				}
				
				count ++;
			}
		} catch(Exception e) { 
			e.printStackTrace();
		} finally {
			for(ResultSet rs: resultSets) {
				if(rs != null && !rs.isClosed()) {
					rs.close();
				}
			}
			for(Statement stmt: statements) {
				if(stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
			}
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
