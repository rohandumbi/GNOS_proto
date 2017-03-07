package com.org.gnos.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.org.gnos.db.DBManager;
import com.org.gnos.db.dao.RequiredFieldDAO;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.RequiredField;

public class ExpressionProcessor {
	
	List<Expression> expressions;
	
	public void store(int projectId) {		
		
		modifyTable(projectId);
		processExpressions(projectId);
		//loadBlockData(projectId);
		
	}
	
	private void modifyTable(int projectId){
		Connection conn = DBManager.getConnection();
		String  alter_sql = "ALTER  TABLE gnos_computed_data_"+projectId+" ADD COLUMN ";
		Statement stmt = null;
		
		
		try {
			stmt = conn.createStatement();
			for(Expression expr: this.expressions){
				String columnName = expr.getName().replaceAll("\\s+","_").toLowerCase();
				try {
					stmt.executeUpdate(alter_sql+ columnName +" VARCHAR(50) NOT NULL default 0");
				} catch (SQLException  e) {
					System.out.println(e.getMessage());
				}
				
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {			
			try {
				DBManager.releaseConnection(conn);
				if(stmt != null) stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.err.println(e.getMessage());
			}
			
		}
		
	}
	
	private void processExpressions (int projectId) {
		
		RequiredFieldDAO rdo = new RequiredFieldDAO();
		List<RequiredField> requiredFields = rdo.getAll(projectId);
		String tonnes_wt_alias = "tonnes_wt";
		for(RequiredField rf: requiredFields){
			if(rf.getFieldName().equals("tonnes_wt")){
				tonnes_wt_alias = rf.getMappedFieldname();
				break;
			}
		}
				
		Connection conn = DBManager.getConnection();
		Statement stmt = null;
		String  sql = null;
		try {
			stmt = conn.createStatement();
			for(Expression expr: this.expressions){
				String columnName = expr.getName().replaceAll("\\s+","_").toLowerCase();
				if(expr.isGrade()) {
					sql = "update gnos_data_"+projectId+" a, gnos_computed_data_"+projectId+" b set b."+columnName +" = IFNULL("+ expr.getExprvalue()+" , 0)";
				} else {
					sql = "update gnos_data_"+projectId+" a, gnos_computed_data_"+projectId+" b set b."+columnName +" = IFNULL( ("+ expr.getExprvalue() +") / "+tonnes_wt_alias +" , 0)";
				}
				sql = sql+ " where a.id = b.row_id ";
				if(expr.getFilter() != null && expr.getFilter().trim().length() > 0) {
					sql = sql+ " AND ( "+expr.getFilter() +" )";
				}
				System.out.println("sql :"+sql);
				stmt.executeUpdate(sql);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			try {
				DBManager.releaseConnection(conn);
				if(stmt != null) stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	public List<Expression> getExpressions() {
		return expressions;
	}

	public void setExpressions(List<Expression> expressions) {
		this.expressions = expressions;
	}
	
/*	private void loadBlockData(int projectId) {
		String sql = "select * from gnos_computed_data_"+projectId +" order by block_no";
		try(
				Connection conn = DBManager.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet resultSet = stmt.executeQuery(sql);
			) {
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	
}
