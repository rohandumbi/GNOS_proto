package com.org.gnos.core;

import java.io.IOException;
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
import com.org.gnos.db.dao.FieldDAO;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Field;
import com.org.gnos.db.model.Model;
import com.org.gnos.db.model.OpexData;
import com.org.gnos.services.EquationGenerator;
import com.org.gnos.services.Node;
import com.org.gnos.services.PitBenchProcessor;
import com.org.gnos.services.Tree;


public class ProjectConfigutration {

	final static ProjectConfigutration instance = new ProjectConfigutration();

	private List<Field> fields = new ArrayList<Field>();
	private Map<String, String> requiredFieldMapping = new LinkedHashMap<String, String>();
	private List<Expression> expressions = new ArrayList<Expression>();
	private List<Model> models = new ArrayList<Model>();
	private List<OpexData> opexDataList = new ArrayList<OpexData>();
	private Tree processTree = null;
	
	private boolean newProject = true;
	private Map<String, String> savedRequiredFieldMapping;
	
	private int projectId = -1;
	
	public static ProjectConfigutration getInstance() {
		return instance;
	}
	
	public void load(int projectId){
		
		if(projectId == -1) {
			System.err.println("Can not load project unless projectId is present");
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
		loadOpexData();
	}

	private void loadFieldData() {
		fields = (new FieldDAO()).get();
	}

	private void loadFieldMappingData() {
		String sql = "select field_name, mapped_field_name from required_field_mapping where project_id = "+ this.projectId;
		Statement stmt = null;
		ResultSet rs = null; 
		Connection conn = DBManager.getConnection();
		
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			while(rs.next()){
				requiredFieldMapping.put(rs.getString(1), rs.getString(2));
			}
			savedRequiredFieldMapping = requiredFieldMapping;
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			try {
				if(stmt != null) stmt.close();
				if(rs != null) rs.close();
				if(conn != null) DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void loadExpressions() {
		String sql = "select id, name, grade, is_complex, expr_str, filter_str from expressions where project_id = "+ this.projectId;
		Statement stmt = null;
		ResultSet rs = null; 
		Connection conn = DBManager.getConnection();
		
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			Expression expression = null;
			while(rs.next()){
				expression = new Expression(rs.getInt(1), rs.getString(2));
				expression.setGrade(rs.getBoolean(3));
				expression.setComplex(rs.getBoolean(4));
				expression.setExpr_str(rs.getString(5));
				expression.setCondition(rs.getString(6));
				expressions.add(expression);
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			try {
				if(stmt != null) stmt.close();
				if(rs != null) rs.close();
				if(conn != null) DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void loadModels() {
		String sql = "select id, name, expr_id, filter_str from models where project_id = "+ this.projectId;
		Statement stmt = null;
		ResultSet rs = null; 
		Connection conn = DBManager.getConnection();
		
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			Model model = null;
			while(rs.next()){
				model = new Model(rs.getInt(1), rs.getString(2));
				int expressionId = rs.getInt(3);
				for(Expression expression: expressions){
					if(expression.getId() == expressionId){
						model.setExpression(expression);
						break;
					}
				}
				
				model.setCondition(rs.getString(4));
				models.add(model);
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			try {
				if(stmt != null) stmt.close();
				if(rs != null) rs.close();
				if(conn != null) DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void loadProcessTree() {
		String sql = "select model_id, parent_model_id from process_route_defn where project_id = "+ this.projectId;
		Statement stmt = null;
		ResultSet rs = null; 
		Connection conn = DBManager.getConnection();
		Map<String, Node> nodes = new HashMap<String, Node>();
		Node rootNode = new Node("Block");
		rootNode.setSaved(true);
		nodes.put("Block", rootNode);
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			
			while(rs.next()) {
				int modelId = rs.getInt(1);
				int parentModelId = rs.getInt(2);
				Model model = this.getModelById(modelId);
				if(model != null) {
					Node node = nodes.get(model.getName());
					if(node == null){
						node = new Node(model.getName());
						node.setSaved(true);
						nodes.put(model.getName(), node);
					}
					if(parentModelId == -1){
						rootNode.addChild(node.getIdentifier());
						node.setParent("Block");
					} else {
						Model pModel = this.getModelById(parentModelId);
						if(pModel != null) {
							Node pNode = nodes.get(pModel.getName());
							if(pNode == null){
								pNode = new Node(pModel.getName());
								pNode.setSaved(true);
								nodes.put(pModel.getName(), pNode);
							} 
							pNode.addChild(node.getIdentifier());
							node.setParent(pModel.getName());
							
						}
					}

					
					
				}
			}		
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			processTree.setNodes((HashMap)nodes);
			try {
				if(stmt != null) stmt.close();
				if(rs != null) rs.close();
				if(conn != null) DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void loadOpexData() {
		String sql = "select id, model_id, expression_id, in_use, is_revenue, year, value from opex_defn, model_year_mapping where id= opex_id and project_id = "+ this.projectId + " order by id";
		Statement stmt = null;
		ResultSet rs = null; 
		Connection conn = DBManager.getConnection();
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			OpexData od;
			while(rs.next()) {
				int id = rs.getInt(1);
				int modelId = rs.getInt(2);
				int expressionId = rs.getInt(3);
				Model model = this.getModelById(modelId);
				Expression expression = this.getExpressionById(expressionId);
				od = getOpexDataById(id);
				if(od == null) {
					od = new OpexData(model);
					od.setExpression(expression);
					od.setId(id);
					od.setInUse(rs.getBoolean(4));
					od.setRevenue(rs.getBoolean(5));
					this.opexDataList.add(od);
				}
				od.addYear(rs.getInt(6), rs.getInt(7));			
			}		
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			try {
				if(stmt != null) stmt.close();
				if(rs != null) rs.close();
				if(conn != null) DBManager.releaseConnection(conn);
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
		saveOpexData();
		try {
			new EquationGenerator().generate();
		} catch ( IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			pstmt = conn.prepareStatement(insert_sql, Statement.RETURN_GENERATED_KEYS);
			pstmt1 = conn.prepareStatement(update_sql);
			
			for(Field field: fields) {
				if(field.getId() == -1){
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
			
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
				if(pstmt != null) pstmt.close();
				if(rs != null) rs.close();
				if(conn != null) DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void saveRequiredFieldMappingData() {
		
		if(this.newProject) {
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
			if(savedRequiredFieldMapping == null || savedRequiredFieldMapping.size() == 0) {
				pstmt = conn.prepareStatement(insert_sql);
				Set keys = requiredFieldMapping.keySet();
				Iterator<String> it = keys.iterator();
				while(it.hasNext()) {
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
				while(it.hasNext()) {
					String key = it.next();				
					pstmt.setString(1, requiredFieldMapping.get(key));
					pstmt.setInt(2, projectId);
					pstmt.setString(3, key);
					
					pstmt.executeUpdate();   
				}
			}


			conn.commit();
			savedRequiredFieldMapping = requiredFieldMapping;
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
				if(pstmt != null) pstmt.close();
				if(conn != null) DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void saveExpressionData() {
		
		Connection conn = DBManager.getConnection();
		String insert_sql = " insert into expressions (project_id, name, grade, is_complex, expr_str, filter_str) values (?, ?, ?, ?, ?, ?)";
		String update_sql = " update expressions set grade= ?,  is_complex = ?, expr_str = ?,  filter_str = ? where id = ?";
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		
		ResultSet rs = null; 
		boolean autoCommit = true;

		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insert_sql, Statement.RETURN_GENERATED_KEYS);
			pstmt1 = conn.prepareStatement(update_sql);
			for(Expression expression: expressions) {
				if(expression.getId() == -1){
					pstmt.setInt(1, projectId);
					pstmt.setString(2, expression.getName());
					pstmt.setBoolean(3, expression.isGrade());
					pstmt.setBoolean(4, expression.isComplex());
					pstmt.setString(5, expression.getExpr_str());
					pstmt.setString(6, expression.getCondition());
					pstmt.executeUpdate();
					rs = pstmt.getGeneratedKeys();    
					rs.next();  
					expression.setId(rs.getInt(1));
				} else {
					pstmt1.setBoolean(1, expression.isGrade());
					pstmt1.setBoolean(2, expression.isComplex());
					pstmt1.setString(3, expression.getExpr_str());
					pstmt1.setString(4, expression.getCondition());
					pstmt1.setInt(5, expression.getId());
					pstmt1.executeUpdate();
				}

			}

			conn.commit();
			
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
				if(pstmt != null) pstmt.close();
				if(rs != null) rs.close();
				if(conn != null) DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
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
			pstmt = conn.prepareStatement(insert_sql, Statement.RETURN_GENERATED_KEYS);
			pstmt1 = conn.prepareStatement(update_sql);
			for(Model model: models) {
				if(model.getId() == -1){
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
			
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
				if(pstmt != null) pstmt.close();
				if(rs != null) rs.close();
				if(conn != null) DBManager.releaseConnection(conn);
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

		if(nodes == null) return ;
		
		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insert_sql);
			
			Set<String> keys = nodes.keySet();
			Iterator<String> it = keys.iterator();
			
			while (it.hasNext()) {
				String key = it.next();
				Node  node = nodes.get(key);
				if(node.isSaved()) continue;
				int modelId = this.getModelByName(node.getIdentifier()).getId(); 
				Model parentModel = this.getModelByName(node.getParent()); 
				
				pstmt.setInt(1, this.projectId);
				pstmt.setInt(2, modelId);
				if(parentModel == null){
					pstmt.setInt(3, -1);
				} else {
					pstmt.setInt(3, parentModel.getId());
				}
				pstmt.executeUpdate();
				node.setSaved(true);
				
			}
			conn.commit();
			
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
				if(pstmt != null) pstmt.close();
				if(conn != null) DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void saveOpexData() {
		Connection conn = DBManager.getConnection();
		String insert_sql = "insert into opex_defn (project_id, scenario_id, model_id, expression_id, in_use, is_revenue) values (?, ?, ?, ?, ?, ?)";
		String mapping_sql = "insert into model_year_mapping (opex_id, year, value) values (?, ?, ?)";
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		boolean autoCommit = true;
		
		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insert_sql, Statement.RETURN_GENERATED_KEYS);
			pstmt1 = conn.prepareStatement(mapping_sql);
			
			for(OpexData od: this.opexDataList) {
				if(od.getId() > 0 ) continue;
				pstmt.setInt(1, projectId);
				pstmt.setInt(2, 1);
				pstmt.setInt(3, od.getModel().getId());
				pstmt.setInt(4, od.getExpression().getId());
				pstmt.setBoolean(5, od.isInUse());
				pstmt.setBoolean(6, od.isRevenue());
				pstmt.executeUpdate();
				rs = pstmt.getGeneratedKeys();
				if(rs.next());{
					int id = rs.getInt(1);
					od.setId(id);
					
					Set keys = od.getCostData().keySet();
					Iterator<Integer> it = keys.iterator();
					while(it.hasNext()){
						int key = it.next();
						pstmt1.setInt(1, od.getId());
						pstmt1.setInt(2, key);
						pstmt1.setInt(3, od.getCostData().get(key));
						pstmt1.executeUpdate();
					}
				}		
			}
			conn.commit();
			
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
				if(pstmt != null) pstmt.close();
				if(conn != null) DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Expression getExpressionById(int expressionId) {
		for(Expression expression:expressions){
			if(expression.getId() == expressionId) {
				return expression;
			}
		}
		return null;
	}
	
	public Expression getExpressionByName(String name) {
		if(name == null) return null;
		for(Expression expression:expressions){
			if(expression.getName().equals(name)) {
				return expression;
			}
		}
		return null;
	}
	
	public Model getModelById(int modelId) {
		for(Model model:models){
			if(model.getId() == modelId) {
				return model;
			}
		}
		return null;
	}
	
	public Model getModelByName(String name) {
		if(name == null) return null;
		for(Model model:models){
			if(model.getName().equals(name)) {
				return model;
			}
		}
		return null;
	}
	
	public OpexData getOpexDataById(int id){
		if(this.opexDataList == null) return null;
		for(OpexData od: this.opexDataList) {
			if(od.getId() == id) {
				return od;
			}
		}
		return null;
	}
	
	public int getProjectId(){
		return this.projectId;
	}
	
	public void setProjectId(int projectId){
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

	public void setOpexDataList(List<OpexData> opexDataList) {
		this.opexDataList = opexDataList;
	}

}
