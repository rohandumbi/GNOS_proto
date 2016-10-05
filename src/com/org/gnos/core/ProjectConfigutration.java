package com.org.gnos.core;

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
import com.org.gnos.services.PitBenchProcessor;

public class ProjectConfigutration {

	final static ProjectConfigutration instance = new ProjectConfigutration();

	private List<Field> fields = new ArrayList<Field>();
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

	private boolean newProject = true;
	private Map<String, String> savedRequiredFieldMapping;

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
		requiredFieldMapping = new HashMap<String, String>();
		expressions = new ArrayList<Expression>();
		models = new ArrayList<Model>();
		processTree = new Tree();
		processList = new ArrayList<Process>();
		pitGroupList = new ArrayList<PitGroup>();
		dumpList = new ArrayList<Dump>();
		stockPileList = new ArrayList<Stockpile>();
		
		loadFieldData();
		loadFieldMappingData();
		loadExpressions();
		loadModels();
		loadProcessDetails();
		loadPitGroups();
		loadDumps();
		loadStockpiles();
	}

	private void loadFieldData() {
		fields = (new FieldDAO()).get();
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
		expressions = new ExpressionDAO().getAll();
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
				for (Expression expression : expressions) {
					if (expression.getId() == expressionId) {
						model.setExpression(expression);
						break;
					}
				}

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

		Map<String, Node> nodes = new HashMap<String, Node>();
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
		String sql = "select distinct a.name, associated_model_id, child_expression_id, b.id, b.name, b.value from product_defn a, grade b where a.project_id = "
				+ this.projectId +" and b.product_name = a.name" + " and b.project_id="+ this.projectId + " order by a.name, b.id asc";
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
				String name = rs.getString(2);
				PitGroup pitGroup = this.getPitGroupfromName(name);
				if(pitGroup == null){
					pitGroup = new PitGroup(name);
					pitGroupList.add(pitGroup);
				}			
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
		String sql = "select id, project_id, dumpType, name, expression, pitgroup_name, has_capacity, capacity from dump_pit_mapping where project_id = "+ this.projectId +" order by id asc ";

		try (
				Connection conn = DBManager.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				
			){
			int count = 1;
			while(rs.next()){
				int id = rs.getInt(1);
				int dumpType = rs.getInt(3);
				String name = rs.getString(4);
				String expression = rs.getString(5);
				String pitGroupName = rs.getString(6);
				boolean hasCapacity = rs.getBoolean(7);
				int capacity = rs.getInt(8);
				PitGroup pitGroup = this.getPitGroupfromName(pitGroupName);
				Dump dump = new Dump();
				dump.setId(id);
				dump.setDumpType(dumpType);
				dump.setName(name);
				dump.setExpression(expression);
				dump.setAssociatedPitGroup(pitGroup);
				dump.setHasCapacity(hasCapacity);
				dump.setCapacity(capacity);
				dump.setDumpNumber(count);
				count ++;
				this.dumpList.add(dump);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void loadStockpiles() {
		String sql = "select id, project_id, stockpileType, name, expression, pitgroup_name, has_capacity, capacity, is_reclaim from stockpile_pit_mapping where project_id = "+ this.projectId +" order by id asc ";

		try (
				Connection conn = DBManager.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				
			){
			int count = 1;
			while(rs.next()){
				int id = rs.getInt(1);
				int stockpileType = rs.getInt(3);
				String name = rs.getString(4);
				String expression = rs.getString(5);
				String pitGroupName = rs.getString(6);
				boolean hasCapacity = rs.getBoolean(7);
				int capacity = rs.getInt(8);
				boolean isReclaim = rs.getBoolean(9);
				PitGroup pitGroup = this.getPitGroupfromName(pitGroupName);
				Stockpile stockpile = new Stockpile();
				stockpile.setId(id);
				stockpile.setStockpileType(stockpileType);
				stockpile.setName(name);
				stockpile.setExpression(expression);
				stockpile.setAssociatedPitGroup(pitGroup);
				stockpile.setHasCapacity(hasCapacity);
				stockpile.setCapacity(capacity);
				stockpile.setStockpileNumber(count);
				stockpile.setReclaim(isReclaim);
				count ++;
				this.stockPileList.add(stockpile);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
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
				Set keys = requiredFieldMapping.keySet();
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
				Set keys = requiredFieldMapping.keySet();
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
				expressiondao.create(expression);
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
					pstmt.setInt(3, model.getExpression().getId());
					pstmt.setString(4, model.getCondition());
					pstmt.executeUpdate();
					rs = pstmt.getGeneratedKeys();
					rs.next();
					model.setId(rs.getInt(1));
				} else {
					pstmt1.setInt(1, model.getExpression().getId());
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
		Connection conn = DBManager.getConnection();
		String insert_sql = " insert into process_route_defn (project_id, model_id, parent_model_id) values (?, ?, ?)";
		PreparedStatement pstmt = null;
		Map<String, Node> nodes = processTree.getNodes();
		boolean autoCommit = true;

		if (nodes == null)
			return;

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
		Connection conn = DBManager.getConnection();
		String insert_sql = " insert into process_join_defn (project_id, name, child_model_id) values (?, ?, ?)";
		PreparedStatement pstmt = null;
		boolean autoCommit = true;

		if(this.processJoins.size() < 1){ // no process joins defined
			return;
		}

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
		Connection conn = DBManager.getConnection();
		String insert_sql = " insert into product_join_defn (project_id, name, child_product_name, child_product_join_name) values (?, ?, ?, ?)";
		String insert_grade_sql = " insert into product_join_grade_name_mapping (project_id, name, product_join_name) values (?, ?, ?)";
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		boolean autoCommit = true;

		if(this.productJoinList.size() < 1){ // no process joins defined
			return;
		}

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
		Connection conn = DBManager.getConnection();
		String insert_sql = " insert into product_defn (project_id, name, associated_model_id, child_expression_id) values (?, ?, ?, ?)";
		String insert_grade_sql = " insert into grade (project_id, name, product_name, value) values (?, ?, ?, ?)";
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		boolean autoCommit = true;

		if(this.productList.size() < 1){ // no products defined
			return;
		}

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
	

	public void savePitGroups() {
		String insert_sql = "insert into pitgroup_pit_mapping ( project_id , name, child_pit_name, child_pitgroup_name) values ( ? , ?, ?, ?) ";

		try (
				Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(insert_sql);
				
			){
			for(PitGroup pitGroup: pitGroupList) {
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
			
		} catch (SQLException e) {
			System.err.println("Failed saving pit groups. "+e.getMessage());
		}
	}
	public void saveDumps() {
		String insert_sql = "insert into dump_pit_mapping ( project_id , dumpType, name, expression, pitgroup_name, has_capacity, capacity) values ( ? , ?, ?, ?, ?, ?, ?)";
		String update_sql = "update dump_pit_mapping set dumpType= ? , name = ?, expression = ?, pitgroup_name = ?, has_capacity = ?, capacity = ? where id = ?";
		try (
				Connection conn = DBManager.getConnection();
				PreparedStatement pstmt1 = conn.prepareStatement(insert_sql);
				PreparedStatement pstmt2 = conn.prepareStatement(update_sql);
			){
			int count = 1;
			for(Dump dump : dumpList) {
				if(dump.getId() == -1){
					pstmt1.setInt(1, projectId);
					pstmt1.setInt(2, dump.getDumpType());
					pstmt1.setString(3, dump.getName());
					pstmt1.setString(4, dump.getExpression());
					pstmt1.setString(5, dump.getAssociatedPitGroup().getName());
					pstmt1.setBoolean(6, dump.isHasCapacity());
					pstmt1.setInt(7, dump.getCapacity());
					pstmt1.executeUpdate();
				}else{
					pstmt2.setInt(1, dump.getDumpType());
					pstmt2.setString(2, dump.getName());
					pstmt2.setString(3, dump.getExpression());
					pstmt2.setString(4, dump.getAssociatedPitGroup().getName());
					pstmt2.setBoolean(5, dump.isHasCapacity());
					pstmt2.setInt(6, dump.getCapacity());
					pstmt2.setInt(7, dump.getId());
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
		String insert_sql = "insert into stockpile_pit_mapping ( project_id , stockpileType, name, expression, pitgroup_name, has_capacity, capacity, is_reclaim) values ( ? , ?, ?, ?, ?, ?, ?, ?)";
		String update_sql = "update stockpile_pit_mapping set stockpileType= ? , name = ?, expression = ?, pitgroup_name = ?, has_capacity = ?, capacity = ?, is_reclaim = ? where id = ?";
		try (
				Connection conn = DBManager.getConnection();
				PreparedStatement pstmt1 = conn.prepareStatement(insert_sql);
				PreparedStatement pstmt2 = conn.prepareStatement(update_sql);
			){
			int count = 1;
			for(Stockpile stockpile : stockPileList) {
				if(stockpile.getId() == -1){
					pstmt1.setInt(1, projectId);
					pstmt1.setInt(2, stockpile.getStockpileType());
					pstmt1.setString(3, stockpile.getName());
					pstmt1.setString(4, stockpile.getExpression());
					pstmt1.setString(5, stockpile.getAssociatedPitGroup().getName());
					pstmt1.setBoolean(6, stockpile.isHasCapacity());
					pstmt1.setInt(7, stockpile.getCapacity());
					pstmt1.setBoolean(8, stockpile.isReclaim());
					pstmt1.executeUpdate();
				}else{
					pstmt2.setInt(1, stockpile.getStockpileType());
					pstmt2.setString(2, stockpile.getName());
					pstmt2.setString(3, stockpile.getExpression());
					pstmt2.setString(4, stockpile.getAssociatedPitGroup().getName());
					pstmt2.setBoolean(5, stockpile.isHasCapacity());
					pstmt2.setInt(6, stockpile.getCapacity());
					pstmt2.setBoolean(7, stockpile.isReclaim());
					pstmt2.setInt(8, stockpile.getId());
					pstmt2.executeUpdate();
				}
				stockpile.setStockpileNumber(count);				
				count++;
			}
			
		} catch (SQLException e) {
			System.err.println("Failed saving dump data. "+e.getMessage());
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
	
	public PitGroup getPitGroupfromName(String name) {

		for(PitGroup pg : this.pitGroupList){
			if(pg.getName().equals(name)){
				return pg;
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

}
