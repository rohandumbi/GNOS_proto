package com.org.gnos.core;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.db.DBManager;
import com.org.gnos.db.dao.ExpressionDAO;
import com.org.gnos.db.dao.FieldDAO;
import com.org.gnos.db.model.CycleTimeMappingData;
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Field;
import com.org.gnos.db.model.Grade;
import com.org.gnos.db.model.Model;
import com.org.gnos.db.model.Pit;
import com.org.gnos.db.model.PitGroup;
import com.org.gnos.db.model.Process;
import com.org.gnos.db.model.ProcessJoin;
import com.org.gnos.db.model.Product;
import com.org.gnos.db.model.ProductJoin;
import com.org.gnos.db.model.Stockpile;
import com.org.gnos.db.model.TruckParameterCycleTime;
import com.org.gnos.db.model.TruckParameterData;
import com.org.gnos.services.PitBenchProcessor;

public class ProjectConfigutration {

	final static ProjectConfigutration instance = new ProjectConfigutration();

	private List<Field> fields = new ArrayList<Field>();
	private List<Field> cycletimefields = new ArrayList<Field>();
	private Map<String, String> requiredFieldMapping = new LinkedHashMap<String, String>();
	private List<Expression> expressions = new ArrayList<Expression>();
	private List<Model> models = new ArrayList<Model>();
	private Tree processTree = new Tree();
	private List<Process> processList = new ArrayList<Process>();
	private List<ProcessJoin> processJoins = new ArrayList<ProcessJoin>();
	private List<Product> productList = new ArrayList<Product>();
	private List<ProductJoin> productJoinList = new ArrayList<ProductJoin>();
	private List<PitGroup> pitGroupList = new ArrayList<PitGroup>();
	private List<Dump> dumpList = new ArrayList<Dump>();
	private List<Stockpile> stockPileList = new ArrayList<Stockpile>();
	private List<Pit> pitList = new ArrayList<Pit>();
	private CycleTimeMappingData cycleTimeMappingData = new CycleTimeMappingData();
	private TruckParameterData truckParameterData = new TruckParameterData();
	private ArrayList<TruckParameterCycleTime> truckParameterCycleTimeList = new ArrayList<TruckParameterCycleTime>();

	private boolean newProject = true;
	private Map<String, String> savedRequiredFieldMapping;

	/* Tracking existing cycle time data for the project instance */
	private ArrayList<String> existingCycleTimeFixedFields = new ArrayList<String>();
	private ArrayList<String> existingCycleTimeDumpFields = new ArrayList<String>();
	private ArrayList<String> existingCycleTimeStockpileFields = new ArrayList<String>();
	private ArrayList<String> existingCycleTimeProcessFields = new ArrayList<String>();

	/* Tracking existing truck param data for project instance */
	private ArrayList<String> existingTruckParamMaterials = new ArrayList<String>();
	private ArrayList<String> existingTruckParamCycleTimeStockpiles = new ArrayList<String>();
	

	private int projectId = -1;

	public static ProjectConfigutration getInstance() {
		return instance;
	}

	public void load(int projectId) {

		if (projectId == -1) {
			System.err
			.println("Can not load project unless projectId is present");
			return;
		}
		this.projectId = projectId;
		this.newProject = false;

		// Reinitializing the structures
		fields = new ArrayList<Field>();
		cycletimefields = new ArrayList<Field>();
		requiredFieldMapping = new HashMap<String, String>();
		expressions = new ArrayList<Expression>();
		models = new ArrayList<Model>();
		processTree = new Tree();
		processList = new ArrayList<Process>();
		pitGroupList = new ArrayList<PitGroup>();
		dumpList = new ArrayList<Dump>();
		stockPileList = new ArrayList<Stockpile>();

		loadFieldData();
		loadCycleTimeFieldData();
		loadFieldMappingData();
		loadExpressions();
		loadModels();
		loadProcessDetails();
		loadPitGroups();
		loadDumps();
		loadStockpiles();
		loadCycleTimeMappingData();
		loadTruckParameters();
	}

	private void loadFieldData() {
		fields = (new FieldDAO()).getAll(projectId);
	}

	private void loadCycleTimeFieldData(){
		String sql = "select id, name from cycle_time_fields where project_id = "+ ProjectConfigutration.getInstance().getProjectId();
		try (
				Connection conn = DBManager.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				) {
			
			Field field = null;
			while(rs.next()){
				field = new Field(rs.getString(2));
				field.setId(rs.getInt(1));
				cycletimefields.add(field);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void loadFieldMappingData() {
		String sql = "select field_name, mapped_field_name from required_field_mapping where project_id = "
				+ this.projectId;
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = DBManager.getConnection();

		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			while (rs.next()) {
				requiredFieldMapping.put(rs.getString(1), rs.getString(2));
			}
			savedRequiredFieldMapping = requiredFieldMapping;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadExpressions() {
		expressions = new ExpressionDAO().getAll(ProjectConfigutration.getInstance().getProjectId());
	}

	private void loadModels() {
		String sql = "select id, name, expr_id, filter_str from models where project_id = "
				+ this.projectId;
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = DBManager.getConnection();

		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			Model model = null;
			while (rs.next()) {
				model = new Model(rs.getInt(1), rs.getString(2));
				int expressionId = rs.getInt(3);
				model.setExpressionId(expressionId);
				model.setCondition(rs.getString(4));
				models.add(model);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadProcessDetails() {
		loadProcessTree();
		loadProcesses();
		loadProcessJoins();
		loadProducts();
		loadProductJoins();
	}

	public List<Pit> getPitList() {
		if(pitList != null && this.pitList.size() > 0) return this.pitList;
		this.pitList = new ArrayList<Pit>();
		String dataTableName = "gnos_data_" + this.projectId;
		String computedDataTableName = "gnos_computed_data_" + this.projectId;
		String sql = "select  distinct a.pit_name, b.pit_no from "
				+ dataTableName + " a, " + computedDataTableName
				+ " b where a.id = b.row_id";

		try (
				Connection conn = DBManager.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				) {

			while (rs.next()) {
				String pit_name = rs.getString(1);
				int pit_no = rs.getInt(2);

				Pit pit = new Pit(pit_no, pit_name);
				pitList.add(pit);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return this.pitList;
	}

	public List<String> getBenchNamesAssociatedWithPit(String pitName) {
		List<String> associatedBenchNames = new ArrayList<String>();
		String bench_rl_name = this.getRequiredFieldMapping().get("bench_rl");
		String dataTableName = "gnos_data_" + this.projectId;
		String sql = "select  distinct "+ bench_rl_name+" from "
				+ dataTableName + " where pit_name=" + "'" + pitName +  "'";

		try (
				Connection conn = DBManager.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				) {

			while (rs.next()) {
				String bench_name = rs.getString(1);
				/*int pit_no = rs.getInt(2);

				Pit pit = new Pit(pit_no, pit_name);*/
				associatedBenchNames.add(bench_name);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return associatedBenchNames;
	}

	public void loadProcessTree() {
		String sql = "select model_id, parent_model_id from process_route_defn where project_id = "
				+ this.projectId + " order by model_id ";

		Map<String, Node> nodes = new HashMap<String, Node>();
		try (
				Connection conn = DBManager.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs =stmt.executeQuery(sql);
				){

			while (rs.next()) {
				int modelId = rs.getInt(1);
				int parentModelId = rs.getInt(2);
				Model model = this.getModelById(modelId);
				if (model != null) {
					Node node = nodes.get(model.getName());
					if (node == null) {
						node = new Node(model);
						node.setSaved(true);
						nodes.put(model.getName(), node);
					}
					if (parentModelId == -1) {
						processTree.addNode(node, null);
					} else {
						Model pModel = this.getModelById(parentModelId);
						if (pModel != null) {
							Node pNode = nodes.get(pModel.getName());
							if (pNode == null) {
								pNode = new Node(pModel);
								pNode.setSaved(true);
								nodes.put(pModel.getName(), pNode);
							}
							processTree.addNode(node, pNode);
							node.setParent(pNode);

						}
					}

				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void loadProcesses() {
		String sql = "select model_id, process_no from process where project_id = " + this.projectId + " order by process_no ";

		try (
				Connection conn = DBManager.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs =stmt.executeQuery(sql);
				){

			while (rs.next()) {
				int modelId = rs.getInt(1);
				int processNo = rs.getInt(2);
				Model model = this.getModelById(modelId);
				Process process = new Process();
				process.setModel(model);
				process.setProcessNo(processNo);
				this.processList.add(process);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	public void loadProcessJoins() {
		String sql = "select name, child_model_id from process_join_defn where project_id = " + this.projectId + " order by name ";
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = DBManager.getConnection();
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();

			while (rs.next()) {

				String processJoinName = rs.getString(1);
				ProcessJoin processJoin = this.getProcessJoinByName(processJoinName);
				if(processJoin == null){
					processJoin = new ProcessJoin(processJoinName);
					this.processJoins.add(processJoin);
				}
				int modelId = rs.getInt(2);
				Model childModel = this.getModelById(modelId);
				if(childModel != null){
					processJoin.addProcess(childModel);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void loadProducts() {
		String sql = "select distinct a.name, associated_model_id, child_expression_id, b.id, b.name, b.value from product_defn a LEFT JOIN grade b on b.product_name = a.name and b.project_id = a.project_id "
				+" where a.project_id = "+ this.projectId + " order by a.name, b.id asc";
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = DBManager.getConnection();
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();

			while (rs.next()) {

				String productName = rs.getString(1);
				int associatedModelId = rs.getInt(2);
				Product product = this.getProductByName(productName);
				Model model = this.getModelById(associatedModelId);
				if(product == null){
					product = new Product(productName, model);
					this.productList.add(product);
				}
				int expressionId = rs.getInt(3);
				Expression expression = this.getExpressionById(expressionId);
				if(expression != null){
					product.addExpression(expression);
				}
				int gradeId = rs.getInt(4);
				if(gradeId > 0){
					Grade grade = new Grade();
					String gradeName = rs.getString(5);
					String value = rs.getString(6);
					Expression expr = this.getExpressionByName(value);
					grade.setName(gradeName);
					grade.setExpression(expr);
					grade.setId(gradeId);
					product.addGrade(grade);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void loadProductJoins() {
		String sql = "select distinct name, child_product_name, child_product_join_name from product_join_defn where project_id = "+ this.projectId  +
				" and ( child_product_name is not null  or child_product_join_name is not null ) order by child_product_name desc";

		String grade_sql = "select distinct id, name from product_join_grade_name_mapping where project_id = "+ this.projectId  +
				" and product_join_name = ?  order by id asc";

		try (
				Connection conn = DBManager.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				PreparedStatement pstmt = conn.prepareStatement(grade_sql);
				){

			while(rs.next()){
				String productJoinName = rs.getString(1);
				ProductJoin productJoin = this.getProductJoinByName(productJoinName);
				if(productJoin == null){
					productJoin = new ProductJoin(productJoinName);
					this.productJoinList.add(productJoin);

					// Add grade to product join .. but there must be a better place to do this

					ResultSet rs1 = null;
					try {
						pstmt.setString(1, productJoinName);
						rs1 = pstmt.executeQuery();
						while(rs1.next()){
							productJoin.addGradeName(rs1.getString("name"));
						}
					} catch(SQLException sqle){
						System.err.println("Failed to load grades for product Join. "+sqle.getMessage());
					} finally {
						try{
							if(rs1 != null) {
								rs1.close();
							}
						} catch(SQLException sqle){
							System.err.println("Failed to close resultset. "+sqle.getMessage());
						}
					}

				}
				String childProductName = rs.getString(2);
				if(childProductName != null) {
					Product product = this.getProductByName(childProductName);
					productJoin.addProduct(product);
				}

				String childProductJoinName = rs.getString(3);
				if(childProductJoinName != null) {
					ProductJoin childProductJoin = this.getProductJoinByName(childProductJoinName);
					productJoin.addProductJoin(childProductJoin);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void loadPitGroups() {
		String sql = "select id, name, child_pit_name, child_pitgroup_name from pitgroup_pit_mapping where project_id = "+ this.projectId +" order by id asc ";

		try (
				Connection conn = DBManager.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);

				){

			while(rs.next()){
				int id = rs.getInt(1);
				String name = rs.getString(2);
				PitGroup pitGroup = this.getPitGroupfromName(name);
				if(pitGroup == null){
					pitGroup = new PitGroup(name);
					pitGroupList.add(pitGroup);
				}	
				pitGroup.setId(id);
				String child_pit_name = rs.getString(3);
				String child_pitgroup_name = rs.getString(4);
				if(child_pit_name != null){
					pitGroup.addPit(this.getPitfromPitName(child_pit_name));
				}
				if(child_pitgroup_name != null){
					pitGroup.addPitGroup(this.getPitGroupfromName(child_pitgroup_name));
				}
				//pitGroupList.add(pitGroup);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void loadDumps() {
		String sql = "select id, type, name, condition_str, mapped_to, mapping_type, has_capacity, capacity from dump where project_id = "+ this.projectId +" order by id asc ";

		try (
				Connection conn = DBManager.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);

				){
			int count = 1;
			while(rs.next()){
				int id = rs.getInt(1);
				int type = rs.getInt(2);
				String name = rs.getString(3);
				String condition = rs.getString(4);
				String mappedTo = rs.getString(5);
				int mappingType = rs.getInt(6);
				boolean hasCapacity = rs.getBoolean(7);
				int capacity = rs.getInt(8);
				Dump dump = new Dump();
				dump.setId(id);
				dump.setType(type);
				dump.setName(name);
				dump.setCondition(condition);
				dump.setMappingType(mappingType);
				dump.setHasCapacity(hasCapacity);
				dump.setCapacity(capacity);
				dump.setDumpNumber(count);
				dump.setMappedTo(mappedTo);
				count ++;
				this.dumpList.add(dump);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void loadStockpiles() {
		String sql = "select id, type, name, condition_str, mapped_to, mapping_type, has_capacity, capacity, is_reclaim from stockpile where project_id = "+ this.projectId +" order by id asc ";

		try (
				Connection conn = DBManager.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);

				){
			int count = 1;
			while(rs.next()){
				int id = rs.getInt(1);
				int type = rs.getInt(2);
				String name = rs.getString(3);
				String condition = rs.getString(4);
				String mappedTo = rs.getString(5);
				int mappingType = rs.getInt(6);
				boolean hasCapacity = rs.getBoolean(7);
				int capacity = rs.getInt(8);
				boolean isReclaim = rs.getBoolean(9);
				Stockpile stockpile = new Stockpile();
				stockpile.setId(id);
				stockpile.setType(type);
				stockpile.setName(name);
				stockpile.setCondition(condition);
				stockpile.setMappingType(mappingType);
				stockpile.setHasCapacity(hasCapacity);
				stockpile.setCapacity(capacity);
				stockpile.setStockpileNumber(count);
				stockpile.setReclaim(isReclaim);
				stockpile.setMappedTo(mappedTo);
				count ++;
				this.stockPileList.add(stockpile);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void loadCycleTimeMappingData(){
		loadCycleTimeFixedFieldMappingData();
		loadCycleTimeDumpFieldMappingData();
		loadCycleTimeStockpileFieldMappingData();
		loadCycleTimeProcessFieldMappingData();
	}

	private void loadCycleTimeFixedFieldMappingData() {
		String sql = "select field_name, mapped_field_name from cycletime_fixed_field_mapping where project_id = "
				+ this.projectId;
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = DBManager.getConnection();
		Map<String, String> fixedFieldMap = this.cycleTimeMappingData.getFixedFieldMap();

		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			while (rs.next()) {
				existingCycleTimeFixedFields.add(rs.getString(1));
				fixedFieldMap.put(rs.getString(1), rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadCycleTimeDumpFieldMappingData() {
		String sql = "select field_name, mapped_field_name from cycletime_dump_field_mapping where project_id = "
				+ this.projectId;
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = DBManager.getConnection();
		Map<String, String> dumpFieldMap = this.cycleTimeMappingData.getDumpFieldMap();

		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			while (rs.next()) {
				existingCycleTimeDumpFields.add(rs.getString(1));
				dumpFieldMap.put(rs.getString(1), rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadCycleTimeStockpileFieldMappingData() {
		String sql = "select field_name, mapped_field_name from cycletime_stockpile_field_mapping where project_id = "
				+ this.projectId;
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = DBManager.getConnection();
		Map<String, String> stockpileFieldMap = this.cycleTimeMappingData.getStockpileFieldMap();

		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			while (rs.next()) {
				existingCycleTimeStockpileFields.add(rs.getString(1));
				stockpileFieldMap.put(rs.getString(1), rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadCycleTimeProcessFieldMappingData() {
		String sql = "select field_name, mapped_field_name from cycletime_process_field_mapping where project_id = "
				+ this.projectId;
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = DBManager.getConnection();
		Map<String, String> processFieldMap = this.cycleTimeMappingData.getChildProcessFieldMap();

		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			while (rs.next()) {
				existingCycleTimeProcessFields.add(rs.getString(1));
				processFieldMap.put(rs.getString(1), rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadTruckParameters(){
		loadTruckParamMaterialPayloadMapping();
		loadTruckParamterFixedTime();
		loadTruckParamCycleTime();
	}

	private void loadTruckParamMaterialPayloadMapping(){
		String sql = "select material_name, payload from truckparam_material_payload_mapping where project_id = "
				+ this.projectId;
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = DBManager.getConnection();
		Map<String, Integer> materialPayloadMap = this.truckParameterData.getMaterialPayloadMap();

		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			while (rs.next()) {
				existingTruckParamMaterials.add(rs.getString(1));
				materialPayloadMap.put(rs.getString(1), rs.getInt(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void loadTruckParamCycleTime(){
		String sql = "select stockpile_name, process_name, value from truckparam_cycle_time where project_id = "
				+ this.projectId + " order by stockpile_name";
		//fixedCost = new FixedOpexCost[5];
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = DBManager.getConnection();
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			while (rs.next()) {
				String stockpileName = rs.getString(1);
				String processName = rs.getString(2);
				BigDecimal value = rs.getBigDecimal(3);
				//float value = rs.getFloat(3);
				//FixedOpexCost fixedOpexCost = fixedCost[costHead];
				
				TruckParameterCycleTime truckParamCycleTime = this.getTruckParamCycleTimeByStockpileName(stockpileName);
				if(truckParamCycleTime == null){
					truckParamCycleTime = new TruckParameterCycleTime();
					truckParamCycleTime.setProjectId(projectId);
					truckParamCycleTime.setStockPileName(stockpileName);
					this.truckParameterCycleTimeList.add(truckParamCycleTime);
					this.existingTruckParamCycleTimeStockpiles.add(stockpileName);
				}

				truckParamCycleTime.getProcessData().put(processName, value);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadTruckParamterFixedTime(){
		String sql = "select fixed_time from truckparam_fixed_time where project_id = "
				+ this.projectId;
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = DBManager.getConnection();

		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			while (rs.next()) {
				truckParameterData.setFixedTime(rs.getBigDecimal(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void save() {
		saveFieldData();
		saveRequiredFieldMappingData();
		saveExpressionData();
		saveModelData();
		saveProcessTree();
		saveProcessJoins();
		saveProducts();
		saveProductJoins();
		savePitGroups();
		saveDumps();
		saveStockpiles();
		saveCycleTimeData();
		saveTruckParameterData();
	}

	public void saveFieldData() {

		Connection conn = DBManager.getConnection();
		String insert_sql = " insert into fields (project_id, name, data_type) values (?, ?, ?)";
		String update_sql = " update fields set data_type = ?  where id = ? ";
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		boolean autoCommit = true;

		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insert_sql,
					Statement.RETURN_GENERATED_KEYS);
			pstmt1 = conn.prepareStatement(update_sql);

			for (Field field : fields) {
				if (field.getId() == -1) {
					pstmt.setInt(1, projectId);
					pstmt.setString(2, field.getName());
					pstmt.setShort(3, field.getDataType());
					pstmt.executeUpdate();
					rs = pstmt.getGeneratedKeys();
					rs.next();
					field.setId(rs.getInt(1));
				} else {
					pstmt1.setShort(1, field.getDataType());
					pstmt1.setInt(2, field.getId());
					pstmt1.executeUpdate();
				}

			}

			conn.commit();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
				if (pstmt != null)
					pstmt.close();
				if (rs != null)
					rs.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public void saveCycleTimeFields() {
		String inset_sql = " insert into cycle_time_fields (project_id, name) values (?, ?) ";
		String delete_sql = " delete from cycle_time_fields where project_id = ? ";
		try (
				Connection connection = DBManager.getConnection();
	            PreparedStatement ps1 = connection.prepareStatement(inset_sql);
				PreparedStatement ps2 = connection.prepareStatement(delete_sql);
			){
			
			ps2.setInt(1, projectId);
			ps2.executeUpdate();
			for (Field field : cycletimefields) {
				ps1.setInt(1, projectId);
				ps1.setString(2, field.getName());
				ps1.executeUpdate();
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		}
	}
	public void saveRequiredFieldMappingData() {

		if (this.newProject) {
			new PitBenchProcessor().updatePitBenchData(projectId);
		}
		Connection conn = DBManager.getConnection();
		String insert_sql = " insert into required_field_mapping (project_id, field_name, mapped_field_name) values (?, ?, ?)";
		String update_sql = " update required_field_mapping set mapped_field_name = ? where project_id = ? AND field_name = ? ";
		PreparedStatement pstmt = null;
		boolean autoCommit = true;

		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			if (savedRequiredFieldMapping == null
					|| savedRequiredFieldMapping.size() == 0) {
				pstmt = conn.prepareStatement(insert_sql);
				Set<String> keys = requiredFieldMapping.keySet();
				Iterator<String> it = keys.iterator();
				while (it.hasNext()) {
					String key = it.next();
					pstmt.setInt(1, projectId);
					pstmt.setString(2, key);
					pstmt.setString(3, requiredFieldMapping.get(key));
					pstmt.executeUpdate();
				}
			} else {
				pstmt = conn.prepareStatement(update_sql);
				Set<String> keys = requiredFieldMapping.keySet();
				Iterator<String> it = keys.iterator();
				while (it.hasNext()) {
					String key = it.next();
					pstmt.setString(1, requiredFieldMapping.get(key));
					pstmt.setInt(2, projectId);
					pstmt.setString(3, key);

					pstmt.executeUpdate();
				}
			}

			conn.commit();
			savedRequiredFieldMapping = requiredFieldMapping;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public void saveExpressionData() {
		ExpressionDAO expressiondao = new ExpressionDAO();
		for (Expression expression : expressions) {
			if (expression.getId() == -1) {
				expressiondao.create(expression, projectId);
			} else {
				expressiondao.update(expression);
			}

		}

	}

	public void saveModelData() {

		Connection conn = DBManager.getConnection();
		String insert_sql = " insert into models (project_id, name, expr_id, filter_str) values (?, ?, ?, ?)";
		String update_sql = " update models set expr_id= ? , filter_str = ? where id = ?";
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;

		ResultSet rs = null;
		boolean autoCommit = true;

		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insert_sql,
					Statement.RETURN_GENERATED_KEYS);
			pstmt1 = conn.prepareStatement(update_sql);
			for (Model model : models) {
				if (model.getId() == -1) {
					pstmt.setInt(1, projectId);
					pstmt.setString(2, model.getName());
					pstmt.setInt(3, model.getExpressionId());
					pstmt.setString(4, model.getCondition());
					pstmt.executeUpdate();
					rs = pstmt.getGeneratedKeys();
					rs.next();
					model.setId(rs.getInt(1));
				} else {
					pstmt1.setInt(1, model.getExpressionId());
					pstmt1.setString(2, model.getCondition());
					pstmt1.setInt(3, model.getId());
					pstmt1.executeUpdate();
				}
			}
			conn.commit();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
				if (pstmt != null)
					pstmt.close();
				if (rs != null)
					rs.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public void saveProcessTree() {

		Map<String, Node> nodes = processTree.getNodes();
		if (nodes == null)
			return;

		Connection conn = DBManager.getConnection();
		String insert_sql = " insert into process_route_defn (project_id, model_id, parent_model_id) values (?, ?, ?)";
		PreparedStatement pstmt = null;
		boolean autoCommit = true;
		
		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insert_sql);

			Set<String> keys = nodes.keySet();
			Iterator<String> it = keys.iterator();

			while (it.hasNext()) {
				String key = it.next();
				Node node = nodes.get(key);
				if (node.isSaved())
					continue;
				int modelId = this.getModelByName(node.getIdentifier()).getId();
				Model parentModel = node.getParent().getData();

				pstmt.setInt(1, this.projectId);
				pstmt.setInt(2, modelId);
				if (parentModel == null) {
					pstmt.setInt(3, -1);
				} else {
					pstmt.setInt(3, parentModel.getId());
				}
				pstmt.executeUpdate();
				node.setSaved(true);

			}
			conn.commit();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		saveProcesses();
	}

	public void saveProcesses() {
		List<Process> processes = this.getProcessList();

		if(processes.size() < 1){ 
			return;
		}

		Connection conn = DBManager.getConnection();
		String delete_sql = " delete from process where project_id = "+ this.projectId;
		String insert_sql = " insert into process (project_id, model_id, process_no) values (?, ?, ?)";
		PreparedStatement pstmt = null;
		Statement stmt = null;
		boolean autoCommit = true;

		try{
			autoCommit = conn.getAutoCommit();

			stmt = conn.createStatement();
			stmt.executeUpdate(delete_sql);
			conn.setAutoCommit(false);	
			pstmt = conn.prepareStatement(insert_sql);
			int count = 1;
			for(Process process : processes){
				pstmt.setInt(1, this.projectId);
				pstmt.setInt(2, process.getModel().getId());
				pstmt.setInt(3, count);
				pstmt.executeUpdate();
				count++;
			}
			conn.commit();
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void saveProcessJoins() {

		if(this.processJoins.size() < 1){ // no process joins defined
			return;
		}
		Connection conn = DBManager.getConnection();
		String insert_sql = " insert into process_join_defn (project_id, name, child_model_id) values (?, ?, ?)";
		PreparedStatement pstmt = null;
		boolean autoCommit = true;
		try{
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insert_sql);

			for(ProcessJoin processJoin : this.processJoins){
				String processJoinName = processJoin.getName();
				for(Model childModel : processJoin.getlistChildProcesses()){
					int childModelId = childModel.getId();
					pstmt.setInt(1, this.projectId);
					pstmt.setString(2, processJoinName);
					pstmt.setInt(3, childModelId);
					pstmt.executeUpdate();
				}
			}
			conn.commit();
		}catch (SQLException e) {
			System.err.println("Failed saving process join. "+e.getMessage());
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void saveProductJoins() {

		if(this.productJoinList.size() < 1){ // no process joins defined
			return;
		}
		Connection conn = DBManager.getConnection();
		String insert_sql = " insert into product_join_defn (project_id, name, child_product_name, child_product_join_name) values (?, ?, ?, ?)";
		String insert_grade_sql = " insert into product_join_grade_name_mapping (project_id, name, product_join_name) values (?, ?, ?)";
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		boolean autoCommit = true;
		
		try{
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insert_sql);
			pstmt1 = conn.prepareStatement(insert_grade_sql);

			for(ProductJoin productJoin : this.productJoinList) {
				String productJoinName = productJoin.getName();
				List<Product> childProducts = productJoin.getlistChildProducts();
				if(childProducts.size() > 0){//this product join is 0th level product joins and consists only of products ie join of products
					for(Product childProduct : childProducts) {
						pstmt.setInt(1, this.projectId);
						pstmt.setString(2, productJoinName);
						pstmt.setString(3, childProduct.getName());
						pstmt.setNull(4, java.sql.Types.VARCHAR);
						pstmt.executeUpdate();
					}
				}else{// this product join is join of product joins
					List<ProductJoin> childProductJoins = productJoin.getListChildProductJoins();
					for(ProductJoin chilsProductJoin : childProductJoins) {
						pstmt.setInt(1, this.projectId);
						pstmt.setString(2, productJoinName);
						pstmt.setNull(3, java.sql.Types.VARCHAR);
						pstmt.setString(4, chilsProductJoin.getName());
						pstmt.executeUpdate();
					}
				}
				List<String> gradeNames = productJoin.getGradeNames();
				for(String gradeName: gradeNames){
					try {
						pstmt1.setInt(1, this.projectId);
						pstmt1.setString(2, gradeName);
						pstmt1.setString(3, productJoinName);
						pstmt1.executeUpdate();					
					} catch(SQLException sqle) {
						System.err.println("Failed saving grade data."+sqle.getMessage());
					}
				}
			}

			conn.commit();
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void saveProducts() {

		if(this.productList.size() < 1){ // no products defined
			return;
		}

		Connection conn = DBManager.getConnection();
		String insert_sql = " insert into product_defn (project_id, name, associated_model_id, child_expression_id) values (?, ?, ?, ?)";
		String insert_grade_sql = " insert into grade (project_id, name, product_name, value) values (?, ?, ?, ?)";
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		boolean autoCommit = true;
		
		try{
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insert_sql);
			pstmt1 = conn.prepareStatement(insert_grade_sql);

			for(Product product : this.productList){
				String productName = product.getName();
				int modelId = product.getAssociatedProcess().getId();

				for(Expression expression : product.getListOfExpressions()){
					int expId = expression.getId();
					pstmt.setInt(1, this.projectId);
					pstmt.setString(2, productName);
					pstmt.setInt(3, modelId);
					pstmt.setInt(4, expId);
					pstmt.executeUpdate();
				}
				List<Grade> grades = product.getListOfGrades();
				for(Grade grade: grades){
					try {
						pstmt1.setInt(1, this.projectId);
						pstmt1.setString(2, grade.getName());
						pstmt1.setString(3, product.getName());
						pstmt1.setString(4, grade.getExpression().getName());
						pstmt1.executeUpdate();					
					} catch(SQLException sqle) {
						System.err.println("Failed saving grade data."+sqle.getMessage());
					}
				}

			}
			conn.commit();
		}catch (SQLException e) {
			e.printStackTrace();
		} 
		catch(Exception e) {
			
		}	
		finally {
			try {
				conn.setAutoCommit(autoCommit);
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}


	public void savePitGroups() {
		String insert_sql = "insert into pitgroup_pit_mapping ( project_id , name, child_pit_name, child_pitgroup_name) values ( ? , ?, ?, ?) ";

		try (
				Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(insert_sql);

				){
			for(PitGroup pitGroup: pitGroupList) {

				if(pitGroup.getId() == -1){//unsaved pit group
					for(Pit pit: pitGroup.getListChildPits()){
						pstmt.setInt(1, projectId);
						pstmt.setString(2, pitGroup.getName());
						pstmt.setString(3, pit.getPitName());
						pstmt.setNull(4, java.sql.Types.VARCHAR);
						pstmt.executeUpdate();
					}
					for(PitGroup childGroup: pitGroup.getListChildPitGroups()){
						pstmt.setInt(1, projectId);
						pstmt.setString(2, pitGroup.getName());
						pstmt.setNull(3, java.sql.Types.VARCHAR);
						pstmt.setString(4, childGroup.getName());
						pstmt.executeUpdate();
					}
				}
			}

		} catch (SQLException e) {
			System.err.println("Failed saving pit groups. "+e.getMessage());
		}
	}
	public void saveDumps() {
		String insert_sql = "insert into dump ( project_id , type, name, condition_str, mapped_to, mapping_type, has_capacity, capacity) values ( ? , ?, ?, ?, ?, ?, ?, ?)";
		String update_sql = "update dump set type = ? , name = ?, condition_str = ?, mapped_to = ?, mapping_type= ?, has_capacity = ?, capacity = ? where id = ?";
		try (
				Connection conn = DBManager.getConnection();
				PreparedStatement pstmt1 = conn.prepareStatement(insert_sql);
				PreparedStatement pstmt2 = conn.prepareStatement(update_sql);
				){
			int count = 1;
			for(Dump dump : dumpList) {
				if(dump.getId() == -1){
					pstmt1.setInt(1, projectId);
					pstmt1.setInt(2, dump.getType());
					pstmt1.setString(3, dump.getName());
					pstmt1.setString(4, dump.getCondition());
					pstmt1.setString(5, dump.getMappedTo());	
					pstmt1.setInt(6, dump.getMappingType());
					pstmt1.setBoolean(7, dump.isHasCapacity());
					pstmt1.setInt(8, dump.getCapacity());
					pstmt1.executeUpdate();
				}else{
					pstmt2.setInt(1, dump.getType());
					pstmt2.setString(2, dump.getName());
					pstmt2.setString(3, dump.getCondition());
					pstmt2.setString(4, dump.getMappedTo());
					pstmt2.setInt(5, dump.getMappingType());
					pstmt2.setBoolean(6, dump.isHasCapacity());
					pstmt2.setInt(7, dump.getCapacity());
					pstmt2.setInt(8, dump.getId());
					pstmt2.executeUpdate();
				}
				dump.setDumpNumber(count);				
				count++;
			}

		} catch (SQLException e) {
			System.err.println("Failed saving dump data. "+e.getMessage());
		}
	}

	public void saveStockpiles() {
		String insert_sql = "insert into stockpile ( project_id , type, name, condition_str, mapped_to, mapping_type, has_capacity, capacity, is_reclaim) values ( ? , ?, ?, ?, ?, ?, ?, ?, ?)";
		String update_sql = "update stockpile set type = ? , name = ?, condition_str = ?, mapped_to = ?, mapping_type= ?, has_capacity = ?, capacity = ?, is_reclaim = ? where id = ?";
		try (
				Connection conn = DBManager.getConnection();
				PreparedStatement pstmt1 = conn.prepareStatement(insert_sql);
				PreparedStatement pstmt2 = conn.prepareStatement(update_sql);
				){
			int count = 1;
			for(Stockpile stockpile : stockPileList) {
				if(stockpile.getId() == -1){
					pstmt1.setInt(1, projectId);
					pstmt1.setInt(2, stockpile.getType());
					pstmt1.setString(3, stockpile.getName());
					pstmt1.setString(4, stockpile.getCondition());
					pstmt1.setString(5, stockpile.getMappedTo());					
					pstmt1.setInt(6, stockpile.getMappingType());
					pstmt1.setBoolean(7, stockpile.isHasCapacity());
					pstmt1.setInt(8, stockpile.getCapacity());
					pstmt1.setBoolean(9, stockpile.isReclaim());
					pstmt1.executeUpdate();
				}else{
					pstmt2.setInt(1, stockpile.getType());
					pstmt2.setString(2, stockpile.getName());
					pstmt2.setString(3, stockpile.getCondition());
					pstmt2.setString(4, stockpile.getMappedTo());
					pstmt2.setInt(5, stockpile.getMappingType());
					pstmt2.setBoolean(6, stockpile.isHasCapacity());
					pstmt2.setInt(7, stockpile.getCapacity());
					pstmt2.setBoolean(8, stockpile.isReclaim());
					pstmt2.setInt(9, stockpile.getId());
					pstmt2.executeUpdate();
				}
				stockpile.setStockpileNumber(count);				
				count++;
			}

		} catch (SQLException e) {
			System.err.println("Failed saving dump data. "+e.getMessage());
		}
	}

	public void saveCycleTimeData(){
		saveCycleTimeFixedFieldData();
		saveCycleTimeDumpFieldData();
		saveCycleTimeStockpileFieldData();
		saveCycleTimeProcessFieldData();
	}

	public void saveCycleTimeFixedFieldData() {

		/*if (this.newProject) {
			new PitBenchProcessor().updatePitBenchData(projectId);
		}*/
		Connection conn = DBManager.getConnection();
		String insert_sql = " insert into cycletime_fixed_field_mapping (project_id, field_name, mapped_field_name) values (?, ?, ?)";
		String update_sql = " update cycletime_fixed_field_mapping set mapped_field_name = ? where project_id = ? AND field_name = ? ";
		PreparedStatement insertPstmt = null;
		PreparedStatement updatePstmt = null;
		boolean autoCommit = true;

		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			insertPstmt = conn.prepareStatement(insert_sql);
			updatePstmt = conn.prepareStatement(update_sql);
			Map<String, String> fixedFieldMap = this.cycleTimeMappingData.getFixedFieldMap();
			Set<String> keys = fixedFieldMap.keySet();
			Iterator<String> it = keys.iterator();
			while (it.hasNext()) {
				String key = it.next();
				if(existingCycleTimeFixedFields.contains(key)){
					updatePstmt.setString(1, fixedFieldMap.get(key));
					updatePstmt.setInt(2, projectId);
					updatePstmt.setString(3, key);
					updatePstmt.executeUpdate();
				}else{
					insertPstmt.setInt(1, projectId);
					insertPstmt.setString(2, key);
					insertPstmt.setString(3, fixedFieldMap.get(key));
					insertPstmt.executeUpdate();
				}
			}
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
				if (insertPstmt != null)
					insertPstmt.close();
				if (updatePstmt != null)
					updatePstmt.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public void saveCycleTimeDumpFieldData() {

		Connection conn = DBManager.getConnection();
		String insert_sql = " insert into cycletime_dump_field_mapping (project_id, field_name, mapped_field_name) values (?, ?, ?)";
		String update_sql = " update cycletime_dump_field_mapping set mapped_field_name = ? where project_id = ? AND field_name = ? ";
		PreparedStatement insertPstmt = null;
		PreparedStatement updatePstmt = null;
		boolean autoCommit = true;

		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			insertPstmt = conn.prepareStatement(insert_sql);
			updatePstmt = conn.prepareStatement(update_sql);
			Map<String, String> dumpFieldMap = this.cycleTimeMappingData.getDumpFieldMap();
			Set<String> keys = dumpFieldMap.keySet();
			Iterator<String> it = keys.iterator();
			while (it.hasNext()) {
				String key = it.next();
				if(existingCycleTimeDumpFields.contains(key)){
					updatePstmt.setString(1, dumpFieldMap.get(key));
					updatePstmt.setInt(2, projectId);
					updatePstmt.setString(3, key);
					updatePstmt.executeUpdate();
				}else{
					insertPstmt.setInt(1, projectId);
					insertPstmt.setString(2, key);
					insertPstmt.setString(3, dumpFieldMap.get(key));
					insertPstmt.executeUpdate();
				}
			}
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
				if (insertPstmt != null)
					insertPstmt.close();
				if (updatePstmt != null)
					updatePstmt.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public void saveCycleTimeStockpileFieldData() {

		Connection conn = DBManager.getConnection();
		String insert_sql = " insert into cycletime_stockpile_field_mapping (project_id, field_name, mapped_field_name) values (?, ?, ?)";
		String update_sql = " update cycletime_stockpile_field_mapping set mapped_field_name = ? where project_id = ? AND field_name = ? ";
		PreparedStatement insertPstmt = null;
		PreparedStatement updatePstmt = null;
		boolean autoCommit = true;

		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			insertPstmt = conn.prepareStatement(insert_sql);
			updatePstmt = conn.prepareStatement(update_sql);
			Map<String, String> stockpileFieldMap = this.cycleTimeMappingData.getStockpileFieldMap();
			Set<String> keys = stockpileFieldMap.keySet();
			Iterator<String> it = keys.iterator();
			while (it.hasNext()) {
				String key = it.next();
				if(existingCycleTimeStockpileFields.contains(key)){
					updatePstmt.setString(1, stockpileFieldMap.get(key));
					updatePstmt.setInt(2, projectId);
					updatePstmt.setString(3, key);
					updatePstmt.executeUpdate();
				}else{
					insertPstmt.setInt(1, projectId);
					insertPstmt.setString(2, key);
					insertPstmt.setString(3, stockpileFieldMap.get(key));
					insertPstmt.executeUpdate();
				}
			}
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
				if (insertPstmt != null)
					insertPstmt.close();
				if (updatePstmt != null)
					updatePstmt.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public void saveCycleTimeProcessFieldData() {

		Connection conn = DBManager.getConnection();
		String insert_sql = " insert into cycletime_process_field_mapping (project_id, field_name, mapped_field_name) values (?, ?, ?)";
		String update_sql = " update cycletime_process_field_mapping set mapped_field_name = ? where project_id = ? AND field_name = ? ";
		PreparedStatement insertPstmt = null;
		PreparedStatement updatePstmt = null;
		boolean autoCommit = true;

		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			insertPstmt = conn.prepareStatement(insert_sql);
			updatePstmt = conn.prepareStatement(update_sql);
			Map<String, String> processFieldMap = this.cycleTimeMappingData.getChildProcessFieldMap();
			Set<String> keys = processFieldMap.keySet();
			Iterator<String> it = keys.iterator();
			while (it.hasNext()) {
				String key = it.next();
				if(existingCycleTimeProcessFields.contains(key)){
					updatePstmt.setString(1, processFieldMap.get(key));
					updatePstmt.setInt(2, projectId);
					updatePstmt.setString(3, key);
					updatePstmt.executeUpdate();
				}else{
					insertPstmt.setInt(1, projectId);
					insertPstmt.setString(2, key);
					insertPstmt.setString(3, processFieldMap.get(key));
					insertPstmt.executeUpdate();
				}
			}
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
				if (insertPstmt != null)
					insertPstmt.close();
				if (updatePstmt != null)
					updatePstmt.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public void saveTruckParameterData(){
		saveTruckParameterFixedTime();
		saveTruckParamMaterialPayloadData();
		saveTruckParameterCycleTimeData();
	}

	public void saveTruckParameterFixedTime(){
		String sql = "select count(*) from truckparam_fixed_time where project_id = "
				+ this.projectId;
		String insert_sql = " insert into truckparam_fixed_time (project_id, fixed_time) values (?, ?)";
		String update_sql = " update truckparam_fixed_time set fixed_time = ? where project_id = ? ";
		PreparedStatement insertPstmt = null;
		PreparedStatement updatePstmt = null;
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = DBManager.getConnection();
		boolean isFixedTimeSavedInDB = false;

		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			insertPstmt = conn.prepareStatement(insert_sql);
			updatePstmt = conn.prepareStatement(update_sql);
			while (rs.next()) {
				if(rs.getInt(1) > 0){
					isFixedTimeSavedInDB = true;
				}
			}
			if(isFixedTimeSavedInDB == true){
				updatePstmt.setBigDecimal(1, this.truckParameterData.getFixedTime());
				updatePstmt.setInt(2, projectId);
				updatePstmt.executeUpdate();
			}else{
				insertPstmt.setInt(1, projectId);
				insertPstmt.setBigDecimal(2, this.truckParameterData.getFixedTime());
				insertPstmt.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (insertPstmt != null)
					insertPstmt.close();
				if (updatePstmt != null)
					updatePstmt.close();
				if (rs != null)
					rs.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void saveTruckParamMaterialPayloadData(){
		Connection conn = DBManager.getConnection();
		String insert_sql = " insert into truckparam_material_payload_mapping (project_id, material_name, payload) values (?, ?, ?)";
		String update_sql = " update truckparam_material_payload_mapping set payload = ? where project_id = ? AND material_name = ? ";
		PreparedStatement insertPstmt = null;
		PreparedStatement updatePstmt = null;
		boolean autoCommit = true;

		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			insertPstmt = conn.prepareStatement(insert_sql);
			updatePstmt = conn.prepareStatement(update_sql);
			Map<String, Integer> materialPayloadMap = this.truckParameterData.getMaterialPayloadMap();
			Set<String> keys = materialPayloadMap.keySet();
			Iterator<String> it = keys.iterator();
			while (it.hasNext()) {
				String key = it.next();
				if(existingTruckParamMaterials.contains(key)){
					updatePstmt.setInt(1, materialPayloadMap.get(key));
					updatePstmt.setInt(2, projectId);
					updatePstmt.setString(3, key);
					updatePstmt.executeUpdate();
				}else{
					insertPstmt.setInt(1, projectId);
					insertPstmt.setString(2, key);
					insertPstmt.setInt(3, materialPayloadMap.get(key));
					insertPstmt.executeUpdate();
				}
			}
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
				if (insertPstmt != null)
					insertPstmt.close();
				if (updatePstmt != null)
					updatePstmt.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean isTruckParameterCycleTimeInDB(String stockpileName, String processName){
		String sql = "select count(*) from truckparam_cycle_time where project_id = "
				+ this.projectId + " AND stockpile_name = '" + stockpileName + "'" +" AND process_name = '" +  processName + "'";
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = DBManager.getConnection();
		boolean isFixedTimeSavedInDB = false;
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			while (rs.next()) {
				if(rs.getInt(1) > 0){
					isFixedTimeSavedInDB = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return isFixedTimeSavedInDB;
	}
	
	public void saveTruckParameterCycleTimeData() {
		Connection conn = DBManager.getConnection();
		String insert_sql = "insert into truckparam_cycle_time (project_id, stockpile_name, process_name, value) values (?, ?, ?, ?)";
		String update_sql = "update truckparam_cycle_time set value = ? where project_id = ? AND (stockpile_name = ? AND process_name = ?)";
		PreparedStatement insertStmt = null;
		PreparedStatement updateStmt = null;

		boolean autoCommit = true;

		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			insertStmt = conn.prepareStatement(insert_sql,
					Statement.RETURN_GENERATED_KEYS);
			updateStmt = conn.prepareStatement(update_sql,
					Statement.RETURN_GENERATED_KEYS);

			for (TruckParameterCycleTime tpmCycleTime: truckParameterCycleTimeList) {
				//FixedOpexCost fixedOpexCost = fixedCost[i];
				if(tpmCycleTime == null || tpmCycleTime.getProcessData() == null) continue;
				Set<String> keys = tpmCycleTime.getProcessData().keySet();
				Iterator<String> it = keys.iterator();
				while (it.hasNext()) {
					String key = it.next();
					if(this.isTruckParameterCycleTimeInDB(tpmCycleTime.getStockPileName(), key)){
						System.out.println("Should update track cycle time");
						updateStmt.setBigDecimal(1, tpmCycleTime.getProcessData().get(key));
						updateStmt.setInt(2, projectId);
						updateStmt.setString(3, tpmCycleTime.getStockPileName());
						updateStmt.setString(4, key);
						updateStmt.executeUpdate();
					}else{
						insertStmt.setInt(1, projectId);
						insertStmt.setString(2, tpmCycleTime.getStockPileName());
						insertStmt.setString(3, key);
						insertStmt.setBigDecimal(4, tpmCycleTime.getProcessData().get(key));
						insertStmt.executeUpdate();
					}
				}
			}
			conn.commit();

		} catch (SQLException e) {
			System.err.println("Failed saving Fixed cost."+e.getMessage());
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
				if (insertStmt != null)
					insertStmt.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public Expression getExpressionById(int expressionId) {
		for (Expression expression : expressions) {
			if (expression.getId() == expressionId) {
				return expression;
			}
		}
		return null;
	}

	public Expression getExpressionByName(String name) {
		if (name == null)
			return null;
		for (Expression expression : expressions) {
			if (expression.getName().equals(name)) {
				return expression;
			}
		}
		return null;
	}

	public Model getModelById(int modelId) {
		for (Model model : models) {
			if (model.getId() == modelId) {
				return model;
			}
		}
		return null;
	}

	public Model getModelByName(String name) {
		if (name == null)
			return null;
		for (Model model : models) {
			if (model.getName().equals(name)) {
				return model;
			}
		}
		return null;
	}

	public ProcessJoin getProcessJoinByName(String name) {
		if (name == null)
			return null;
		for (ProcessJoin processJoin : processJoins) {
			if (processJoin.getName().equals(name)) {
				return processJoin;
			}
		}
		return null;
	}

	public Product getProductByName(String name) {
		if (name == null)
			return null;
		for (Product product : productList) {
			if (product.getName().equals(name)) {
				return product;
			}
		}
		return null;
	}

	public ProductJoin getProductJoinByName(String name) {
		if (name == null)
			return null;
		for (ProductJoin productJoin : productJoinList) {
			if (productJoin.getName().equals(name)) {
				return productJoin;
			}
		}
		return null;
	}

	public int getProjectId() {
		return this.projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public Map<String, String> getRequiredFieldMapping() {
		return requiredFieldMapping;
	}

	public void setRequiredFieldMapping(Map<String, String> requiredFieldMapping) {
		this.requiredFieldMapping = requiredFieldMapping;
	}

	public List<Expression> getExpressions() {
		return expressions;
	}

	public List<Expression> getGradeExpressions() {
		List<Expression> gradeExpressions = new ArrayList<Expression>();
		for(Expression expression : expressions){
			if(expression.isGrade() == true){
				gradeExpressions.add(expression);
			}
		}
		return gradeExpressions;
	}

	public List<Expression> getNonGradeExpressions() {
		List<Expression> nonGradeExpressions = new ArrayList<Expression>();
		for(Expression expression : expressions){
			if(expression.isGrade() == false){
				nonGradeExpressions.add(expression);
			}
		}
		return nonGradeExpressions;
	}

	public Pit getPitfromPitName(String name) {
		List<Pit> pitList = this.getPitList();
		for(Pit p : pitList){
			if(p.getPitName().equals(name)){
				return p;
			}
		}
		return null;
	}

	public Pit getPitfromPitNumber(int number) {
		List<Pit> pitList = this.getPitList();
		for(Pit p : pitList){
			if(p.getPitNumber()== number){
				return p;
			}
		}
		return null;
	}
	public PitGroup getPitGroupfromName(String name) {

		for(PitGroup pg : this.pitGroupList){
			if(pg.getName().equals(name)){
				return pg;
			}
		}
		return null;
	}
	
	public Dump getDumpfromDumpName(String name) {
		List<Dump> dumpList = this.getDumpList();
		for(Dump dump : dumpList){
			if(dump.getName().equals(name)){
				return dump;
			}
		}
		return null;
	}
	
	public Dump getDumpfromPitName(String name) {
		List<Dump> dumpList = this.getDumpList();
		for(Dump dump : dumpList){
			if(dump.getType() == 0) continue;
			if(dump.getName().equals(name)){
				return dump;
			}
		}
		return null;
	}
	
	public TruckParameterCycleTime getTruckParamCycleTimeByStockpileName(String stockpileName) {

		for(TruckParameterCycleTime tpmCycleTime : this.truckParameterCycleTimeList){
			if(tpmCycleTime.getStockPileName().equals(stockpileName)){
				return tpmCycleTime;
			}
		}
		return null;
	}
	
	public void setExpressions(List<Expression> expressions) {
		this.expressions = expressions;
	}

	public List<Model> getModels() {
		return models;
	}

	public void setModels(List<Model> models) {
		this.models = models;
	}

	public Tree getProcessTree() {
		return processTree;
	}

	public void setProcessTree(Tree processTree) {
		this.processTree = processTree;
	}

	public List<Process> getProcessList() {

		Map<String, Process> existingProcess = new HashMap<String, Process>();
		for(Process process: this.processList){
			existingProcess.put(process.getModel().getName(), process);
		}
		List<Node> nodes = processTree.getLeafNodes();
		for(Node node: nodes) {
			if(existingProcess.get(node.getData().getName()) == null){
				Process  p = new Process();
				p.setModel(node.getData());
				p.setProcessNo(this.processList.size() +1);
				this.processList.add(p);
			} else {
				existingProcess.remove(node.getData().getName());
			}
		}
		Set<String> keys = existingProcess.keySet();
		for(String key: keys){
			Process p = existingProcess.get(key);
			this.processList.remove(p);
		}
		return processList;
	}


	public List<ProcessJoin> getProcessJoins() {
		return processJoins;
	}

	public void setProcessJoins(List<ProcessJoin> processJoins) {
		this.processJoins = processJoins;
	}

	public void addProcessJoin(ProcessJoin processJoin) {
		this.processJoins.add(processJoin);
	}

	public List<Product> getProductList() {
		return productList;
	}

	public void setProductList(List<Product> productList) {
		this.productList = productList;
	}

	public void addProduct(Product product){
		this.productList.add(product);
	}

	public List<ProductJoin> getProductJoinList() {
		return productJoinList;
	}

	public List<ProductJoin> getProductJoinWithGrades() {
		List<ProductJoin> productJoinWithGrades = new ArrayList<ProductJoin>();
		for(ProductJoin pj: productJoinList){
			if(pj.getGradeNames().size() > 0){
				productJoinWithGrades.add(pj);
			}
		}
		return productJoinWithGrades;
	}

	public List<ProductJoin> getProductJoinOfProductsList() {
		List<ProductJoin> productJoinOfProducts = new ArrayList<ProductJoin>();
		for(ProductJoin pj: productJoinList){
			if(pj.getlistChildProducts().size() > 0){
				productJoinOfProducts.add(pj);
			}
		}
		return productJoinOfProducts;
	}

	public List<ProductJoin> getProductJoinOfProductsJoinsList() {
		List<ProductJoin> productJoinOfProductJoins = new ArrayList<ProductJoin>();
		for(ProductJoin pj: productJoinList){
			if(pj.getListChildProductJoins().size() > 0){
				productJoinOfProductJoins.add(pj);
			}
		}
		return productJoinOfProductJoins;
	}

	public void setProductJoinList(List<ProductJoin> productJoinList) {
		this.productJoinList = productJoinList;
	}

	public List<PitGroup> getPitGroupList() {
		return pitGroupList;
	}

	public List<Dump> getDumpList() {
		return dumpList;
	}

	public List<Stockpile> getStockPileList() {
		return stockPileList;
	}

	public CycleTimeMappingData getCycleTimeData() {
		return cycleTimeMappingData;
	}

	public void setCycleTimeData(CycleTimeMappingData cycleTimeData) {
		this.cycleTimeMappingData = cycleTimeData;
	}

	public TruckParameterData getTruckParameterData() {
		return truckParameterData;
	}

	public void setTruckParameterData(TruckParameterData truckParameterData) {
		this.truckParameterData = truckParameterData;
	}

	public ArrayList<TruckParameterCycleTime> getTruckParameterCycleTimeList() {
		return truckParameterCycleTimeList;
	}

	public void setTruckParameterCycleTimeList(ArrayList<TruckParameterCycleTime> truckParameterCycleTimeList) {
		this.truckParameterCycleTimeList = truckParameterCycleTimeList;
	}

	public List<Field> getCycletimefields() {
		return cycletimefields;
	}

	public void setCycletimefields(List<Field> cycletimefields) {
		this.cycletimefields = cycletimefields;
	}
}
