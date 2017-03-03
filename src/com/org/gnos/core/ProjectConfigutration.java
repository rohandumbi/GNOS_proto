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
import com.org.gnos.db.model.CycleTimeMappingData;
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Field;
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

		loadCycleTimeFieldData();
		loadCycleTimeMappingData();
		loadTruckParameters();
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
		saveProcessTree();
		saveTruckParameterData();
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


/*	public void saveProducts() {

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
	}*/

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
