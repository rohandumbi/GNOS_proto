package com.org.gnos.services.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import com.org.gnos.db.DBManager;
import com.org.gnos.db.dao.BenchConstraintDAO;
import com.org.gnos.db.dao.CapexDAO;
import com.org.gnos.db.dao.CycleFixedTimeDAO;
import com.org.gnos.db.dao.CycleTimeFieldMappingDAO;
import com.org.gnos.db.dao.DumpDAO;
import com.org.gnos.db.dao.DumpDependencyDAO;
import com.org.gnos.db.dao.ExpressionDAO;
import com.org.gnos.db.dao.FieldDAO;
import com.org.gnos.db.dao.FixedCostDAO;
import com.org.gnos.db.dao.GradeConstraintDAO;
import com.org.gnos.db.dao.ModelDAO;
import com.org.gnos.db.dao.OpexDAO;
import com.org.gnos.db.dao.PitDependencyDAO;
import com.org.gnos.db.dao.PitGroupDAO;
import com.org.gnos.db.dao.ProcessConstraintDAO;
import com.org.gnos.db.dao.ProcessJoinDAO;
import com.org.gnos.db.dao.ProcessTreeDAO;
import com.org.gnos.db.dao.ProcessTreeStateDAO;
import com.org.gnos.db.dao.ProductDAO;
import com.org.gnos.db.dao.ProductJoinDAO;
import com.org.gnos.db.dao.ProjectDAO;
import com.org.gnos.db.dao.RequiredFieldDAO;
import com.org.gnos.db.dao.ScenarioDAO;
import com.org.gnos.db.dao.StockpileDAO;
import com.org.gnos.db.dao.TruckParameterCycleTimeDAO;
import com.org.gnos.db.dao.TruckParameterPayloadDAO;
import com.org.gnos.db.model.CapexData;
import com.org.gnos.db.model.CycleTimeFieldMapping;
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.DumpDependencyData;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Field;
import com.org.gnos.db.model.FixedOpexCost;
import com.org.gnos.db.model.GradeConstraintData;
import com.org.gnos.db.model.Model;
import com.org.gnos.db.model.OpexData;
import com.org.gnos.db.model.PitBenchConstraintData;
import com.org.gnos.db.model.PitDependencyData;
import com.org.gnos.db.model.PitGroup;
import com.org.gnos.db.model.ProcessConstraintData;
import com.org.gnos.db.model.ProcessJoin;
import com.org.gnos.db.model.ProcessTreeNode;
import com.org.gnos.db.model.ProcessTreeNodeState;
import com.org.gnos.db.model.Product;
import com.org.gnos.db.model.ProductJoin;
import com.org.gnos.db.model.Project;
import com.org.gnos.db.model.RequiredField;
import com.org.gnos.db.model.Scenario;
import com.org.gnos.db.model.Stockpile;
import com.org.gnos.db.model.TruckParameterCycleTime;
import com.org.gnos.db.model.TruckParameterPayload;

public class ProjectExportHelper implements ProjectTypes {
	
	public String export(int projectId) {
		StringBuilder output = new StringBuilder("");
		Project project = new ProjectDAO().get(projectId);
		if(project != null) {
			output.append(PROJECT_IND+"|"+project.toString()+"\n");
			//get Fields
			List<Field> fields = new FieldDAO().getAll(projectId);
			for(Field field: fields) {
				output.append(FIELD_IND+"|"+field.toString()+"\n");
			}
			//get RequiredFields 
			List<RequiredField> requireFields = new RequiredFieldDAO().getAll(projectId);
			for(RequiredField reqField: requireFields) {
				output.append(REQ_FIELD_IND+"|"+reqField.toString()+"\n");
			}
			//get Expressions
			List<Expression> exporessions = new ExpressionDAO().getAll(projectId);
			for(Expression expression: exporessions) {
				output.append(EXPRESSION_IND+"|"+expression.toString()+"\n");
			}
			//get models
			List<Model> models = new ModelDAO().getAll(projectId);
			for(Model model: models) {
				output.append(MODEL_IND+"|"+model.toString()+"\n");
			}
			//get processes
			List<ProcessTreeNode> processTreeNodes = new ProcessTreeDAO().getAll(projectId);
			for(ProcessTreeNode node: processTreeNodes) {
				output.append(PROCESS_IND+"|"+ node.toString()+"\n");
			}
			
			//get process tree states
			List<ProcessTreeNodeState> processTreeNodeStates = new ProcessTreeStateDAO().getAll(projectId);
			for(ProcessTreeNodeState node: processTreeNodeStates) {
				output.append(PROCESS_TREE_STATE_IND+"|"+ node.toString()+"\n");
			}
			//get process joins
			List<ProcessJoin> processJoins = new ProcessJoinDAO().getAll(projectId);
			for(ProcessJoin processJoin: processJoins) {
				output.append(PROCESS_JOIN_IND+"|"+ processJoin.toString()+"\n");
			}
			// get products
			List<Product> products = new ProductDAO().getAll(projectId);
			for(Product product: products) {
				output.append(PRODUCT_IND+"|"+ product.toString()+"\n");
			}
			// get product joins
			List<ProductJoin> productJoins = new ProductJoinDAO().getAll(projectId);
			for(ProductJoin productJoin: productJoins) {
				output.append(PRODUCT_JOIN_IND+"|"+ productJoin.toString()+"\n");
			}
			// get pit_groups
			List<PitGroup> pitGroups  = new PitGroupDAO().getAll(projectId);
			for(PitGroup pitGroup: pitGroups) {
				output.append(PIT_GROUP_IND+"|"+ pitGroup.toString()+"\n");
			}
			
			// export dumps
			List<Dump> dumps = new DumpDAO().getAll(projectId);
			for(Dump dump: dumps) {
				output.append(DUMP_IND+"|"+ dump.toString()+"\n");
			}
			// export stockpiles
			List<Stockpile> stockpiles = new StockpileDAO().getAll(projectId);
			for(Stockpile sp : stockpiles) {
				output.append(STOCKPILE_IND+"|"+ sp.toString()+"\n");
			}
			
			// export truck parameter data 
			List<TruckParameterPayload> truckPparamPayloads = new TruckParameterPayloadDAO().getAll(projectId);
			for(TruckParameterPayload tpp : truckPparamPayloads) {
				output.append(TRUCKPARAM_PAYLOAD+"|"+ tpp.toString()+"\n");
			}
			// export truck parameter data 
			List<TruckParameterCycleTime> truckPparamCycleTimes = new TruckParameterCycleTimeDAO().getAll(projectId);
			for(TruckParameterCycleTime tpct : truckPparamCycleTimes) {
				output.append(TRUCKPARAM_CYCLE_TIME+"|"+ tpct.toString()+"\n");
			}
			
			// export truck parameter fixed time 
			BigDecimal fixedTime = new CycleFixedTimeDAO().getAll(projectId);
			output.append(CYCLE_FIXED_TIME+"|"+ fixedTime.toString()+"\n");

			// export cycle time field mapping		
			List<CycleTimeFieldMapping> cycleTimeFieldMappings = new CycleTimeFieldMappingDAO().getAll(projectId);
			for(CycleTimeFieldMapping ctfm : cycleTimeFieldMappings) {
				output.append(CYCLE_TIME_FIELD_MAPPING+"|"+ ctfm.toString()+"\n");
			}
			
			//get scenarios
			List<Scenario> scenarios = new ScenarioDAO().getAll(projectId);
			for(Scenario scenario : scenarios) {
				int scenarioId = scenario.getId();
				
				output.append(SCENARIO_IND+"|"+ scenarioId+"|"+SCENARIO_IND+"|"+scenario.toString()+"\n");
				
				
				List<OpexData> opexDataList = new OpexDAO().getAll(scenarioId);
				for(OpexData od: opexDataList) {
					output.append(SCENARIO_IND+"|"+scenarioId+"|"+SCN_OPEX+"|"+od.toString()+"\n");
				}
				//FixedCost 
				List<FixedOpexCost> fixedCostList = new FixedCostDAO().getAll(scenarioId);
				for(FixedOpexCost foc : fixedCostList) {
					output.append(SCENARIO_IND+"|"+scenarioId+"|"+SCN_FIXED_COST+"|"+foc.toString()+"\n");
				}
				//Process Constraints 
				List<ProcessConstraintData> processConstraintList = new ProcessConstraintDAO().getAll(scenarioId);
				for(ProcessConstraintData pcd: processConstraintList) {
					output.append(SCENARIO_IND+"|"+scenarioId+"|"+SCN_PROCESS_CONSTRAINT+"|"+pcd.toString()+"\n");
				}
				//Bench Cobstraints
				List<PitBenchConstraintData> benchConstraintList =  new BenchConstraintDAO().getAll(scenarioId);
				for(PitBenchConstraintData pbcd: benchConstraintList) {
					output.append(SCENARIO_IND+"|"+scenarioId+"|"+SCN_BENCH_CONSTRAINT+"|"+pbcd.toString()+"\n");
				}			
				//Grade Cobstraints
				List<GradeConstraintData> gradeConstraintList = new GradeConstraintDAO().getAll(scenarioId);
				for(GradeConstraintData gcd: gradeConstraintList) {
					output.append(SCENARIO_IND+"|"+scenarioId+"|"+SCN_GRADE_CONSTRAINT+"|"+gcd.toString()+"\n");
				}
				//Pit dependency
				List<PitDependencyData> pitDependencyList = new PitDependencyDAO().getAll(scenarioId);
				for(PitDependencyData pdd: pitDependencyList) {
					output.append(SCENARIO_IND+"|"+scenarioId+"|"+SCN_PIT_DEPENDENCY+"|"+pdd.toString()+"\n");
				}			
				//Dump dependency
				List<DumpDependencyData> dumpDependencyList = new DumpDependencyDAO().getAll(scenarioId);
				for(DumpDependencyData ddd: dumpDependencyList) {
					output.append(SCENARIO_IND+"|"+scenarioId+"|"+SCN_DUMP_DEPENDENCY+"|"+ddd.toString()+"\n");
				}
				//Capex
				List<CapexData> CapexDataList = new CapexDAO().getAll(scenarioId);
				for(CapexData cd: CapexDataList) {
					output.append(SCENARIO_IND+"|"+scenarioId+"|"+SCN_CAPEX+"|"+cd.toString()+"\n");
				}
				
			}
			
			// get data
			output.append(exportCsvData(projectId));
			
			// get cycle time data
			output.append(exportCycleTimeData(projectId));
			
		}
		return output.toString();
	}
	
	public String exportCsvData(int projectId) {	
		StringBuilder output = new StringBuilder("");
		
		String sql = "select * from gnos_data_"+projectId ;
		try (
				Connection connection = DBManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet resultSet = statement.executeQuery();				
			){
			ResultSetMetaData md = resultSet.getMetaData();
			int columnCount = md.getColumnCount();
			output.append(PROJECT_DATA_IND +"|1");
			for (int i = 1; i <= columnCount; i++) {
				output.append("|"+md.getColumnName(i));
			}
			output.append("\n");
			while(resultSet.next()) {
				output.append(PROJECT_DATA_IND +"|2");
				for (int i = 1; i <= columnCount; i++) {
					output.append("|"+resultSet.getString(i));
				}
				output.append("\n");
			}
		} catch (SQLException e) {
				e.printStackTrace();
		}
		
		return output.toString();
	}
	
	public String exportCycleTimeData(int projectId) {	
		StringBuilder output = new StringBuilder("");
		
		String sql = "select * from gnos_cycle_time_data_"+projectId ;
		try (
				Connection connection = DBManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet resultSet = statement.executeQuery();				
			){
			ResultSetMetaData md = resultSet.getMetaData();
			int columnCount = md.getColumnCount();
			output.append(PROJECT_CYCLETIME_DATA_IND +"|1");
			for (int i = 1; i <= columnCount; i++) {
				output.append("|"+md.getColumnName(i));
			}
			output.append("\n");
			while(resultSet.next()) {
				output.append(PROJECT_CYCLETIME_DATA_IND +"|2");
				for (int i = 1; i <= columnCount; i++) {
					output.append("|"+resultSet.getString(i));
				}
				output.append("\n");
			}
		} catch (SQLException e) {
				e.printStackTrace();
		}
		
		return output.toString();
	}
}
