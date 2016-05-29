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
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Field;
import com.org.gnos.db.model.FixedOpexCost;
import com.org.gnos.db.model.Model;
import com.org.gnos.db.model.OpexData;
import com.org.gnos.db.model.Pit;
import com.org.gnos.db.model.ProcessConstraintData;
import com.org.gnos.db.model.ProcessJoin;
import com.org.gnos.services.PitBenchProcessor;

public class ProjectConfigutration {

	final static ProjectConfigutration instance = new ProjectConfigutration();

	private List<Field> fields = new ArrayList<Field>();
	private Map<String, String> requiredFieldMapping = new LinkedHashMap<String, String>();
	private List<Expression> expressions = new ArrayList<Expression>();
	private List<Model> models = new ArrayList<Model>();
	private List<OpexData> opexDataList = new ArrayList<OpexData>();
	private FixedOpexCost[] fixedCost;
	private Tree processTree = null;
	private List<ProcessJoin> processJoins = new ArrayList<ProcessJoin>();
	private List<ProcessConstraintData> processConstraintDataList = new ArrayList<ProcessConstraintData>();

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
		opexDataList = new ArrayList<OpexData>();

		loadFieldData();
		loadFieldMappingData();
		loadExpressions();
		loadModels();
		loadProcessTree();
		loadProcessJoins();
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

	public ArrayList<Pit> getPitList() {
		ArrayList<Pit> pitList = new ArrayList<Pit>();
		String dataTableName = "gnos_data_" + this.projectId;
		String computedDataTableName = "gnos_computed_data_" + this.projectId;
		String sql = "select  distinct a.pit_name, b.pit_no from "
				+ dataTableName + " a, " + computedDataTableName
				+ " b where a.id = b.row_id";

		try (Connection conn = DBManager.getConnection();
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

		return pitList;
	}

	public void loadProcessTree() {
		String sql = "select model_id, parent_model_id from process_route_defn where project_id = "
				+ this.projectId + " order by model_id ";
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = DBManager.getConnection();
		Map<String, Node> nodes = new HashMap<String, Node>();
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();

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
		} finally {
			// processTree.setNodes((HashMap)nodes);
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

	public void loadProcessJoins() {
		String sql = "select name, child_model_id from process_join_defn where project_id = "
				+ this.projectId + " order by name ";
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





	public void save() {
		saveFieldData();
		saveRequiredFieldMappingData();
		saveExpressionData();
		saveModelData();
		saveProcessTree();
		saveProcessJoins();

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

	public OpexData getOpexDataById(int id) {
		if (this.opexDataList == null)
			return null;
		for (OpexData od : this.opexDataList) {
			if (od.getId() == id) {
				return od;
			}
		}
		return null;
	}
	
	public ProcessConstraintData getProcessConstraintDataById(int id) {
		if (this.processConstraintDataList == null)
			return null;
		for (ProcessConstraintData pcd : this.processConstraintDataList) {
			if (pcd.getId() == id) {
				return pcd;
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

	public List<OpexData> getOpexDataList() {
		return opexDataList;
	}

	public FixedOpexCost[] getFixedCost() {
		return fixedCost;
	}

	public void setFixedCost(FixedOpexCost[] fixedCost) {
		this.fixedCost = fixedCost;
	}

	public void setOpexDataList(List<OpexData> opexDataList) {
		this.opexDataList = opexDataList;
	}

	public List<ProcessJoin> getProcessJoins() {
		return processJoins;
	}

	public void setProcessJoins(List<ProcessJoin> processJoins) {
		this.processJoins = processJoins;
	}


	public List<ProcessConstraintData> getProcessConstraintDataList() {
		return processConstraintDataList;
	}

	public void setProcessConstraintDataList(List<ProcessConstraintData> processConstraintDataList) {
		this.processConstraintDataList = processConstraintDataList;
	}

	public void addProcessJoin(ProcessJoin processJoin) {
		this.processJoins.add(processJoin);
	}
	
	public void addProcesssConstraintData(ProcessConstraintData processConstraintData) {
		this.processConstraintDataList.add(processConstraintData);
	}


}
