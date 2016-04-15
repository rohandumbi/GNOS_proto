package com.org.gnos.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.db.DBManager;


public class ProjectConfigutration {

	final static ProjectConfigutration instance = new ProjectConfigutration();

	private List<Field> fields = new ArrayList<Field>();
	private Map<String, String> requiredFieldMapping = new LinkedHashMap<String, String>();
	private List<Expression> expressions = new ArrayList<Expression>();
	
	private Map<String, String> savedRequiredFieldMapping;
	
	private int projectId = -1;
	
	public static ProjectConfigutration getInstance() {
		return instance;
	}
	
	public void setProjectId(int projectId){
		this.projectId = projectId;
	}
	
	public void load(int projectId){
		
		if(projectId == -1) {
			System.err.println("Can not load project unless projectId is present");
			return;
		}
		this.projectId = projectId;
		
		// Reinitializing the structures
		fields = new ArrayList<Field>();
		requiredFieldMapping = new HashMap<String, String>();
		expressions = new ArrayList<Expression>();
		
		loadFieldData();
		loadFieldMappingData();
		loadExpressions();
	}

	private void loadFieldData() {
		String sql = "select id, name, data_type from fields where project_id = "+ this.projectId;
		Statement stmt = null;
		ResultSet rs = null; 
		Connection conn = DBManager.getConnection();
		Date currDate = new Date();
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			Field field = null;
			while(rs.next()){
				field = new Field(rs.getInt(1), rs.getString(2), rs.getShort(3));
				fields.add(field);
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
		String sql = "select id, name, grade, is_complex, field_left, field_right, operator, filter_str from expressions where project_id = "+ this.projectId;
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
				expression.setField_left(rs.getString(5));
				expression.setField_right(rs.getString(6));
				expression.setOperator(rs.getShort(7));
				expression.setCondition(rs.getString(8));
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
	
	public void save() {
		saveFieldData();
		saveRequiredFieldMappingData();
		//saveExpressionData();
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
		String insert_sql = " insert into expressions (project_id, name, grade, is_complex, field_left, field_right, operator, filter_str) values (?, ?, ?, ?, ?, ?, ?, ?)";
		String update_sql = " update expressions set grade= ?,  is_complex = ?, field_left = ?, field_right = ?, operand = ?,  filter_str = ? where id = ?";
		PreparedStatement pstmt = null;
		ResultSet rs = null; 
		boolean autoCommit = true;

		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insert_sql, Statement.RETURN_GENERATED_KEYS);
			
			for(Expression expression: expressions) {
				pstmt.setInt(1, projectId);
				pstmt.setString(2, expression.getName());
				pstmt.setBoolean(3, expression.isGrade());
				pstmt.setBoolean(4, expression.isComplex());
				pstmt.setString(5, expression.getField_left());
				pstmt.setString(6, expression.getField_right());
				pstmt.setShort(7, expression.getOperator());
				pstmt.setString(8, expression.getCondition());
				pstmt.executeUpdate();
				rs = pstmt.getGeneratedKeys();    
				rs.next();  
				expression.setId(rs.getInt(1));
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
	
	
}
