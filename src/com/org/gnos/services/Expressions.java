package com.org.gnos.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.org.gnos.application.GNOSConfig;
import com.org.gnos.db.DBManager;

public class Expressions {

	private List<Expression> expressions = new ArrayList<Expression>();
	
	public List<Expression> getAll() {
		Connection conn = DBManager.getConnection();
		String sql = "select * from expressions";
		Statement stmt = null;
		ResultSet rs= null;
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			Expression expr = null;
			while(rs.next()){
				expr = new Expression(rs.getInt(1), rs.getString(2));
				expr.setGrade(rs.getBoolean(3));
				expressions.add(expr);
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
		
		return expressions;
	}
	
	public boolean delete(int exp_id) {
		
		Connection conn = DBManager.getConnection();
		String sql = "delete from expressions where id = "+exp_id;
		Statement stmt = null;
		
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			try {
				if(stmt != null) stmt.close();
				if(conn != null) DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return true;
	}
	
	public boolean add(Expression expr){
		
		Connection conn = DBManager.getConnection();
		String sql = "insert into expressions (name, grade, value) values (?, ?, ?)";
		PreparedStatement pstmt = null;
		ResultSet rs = null; 

		try {
			pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, expr.getName());
			pstmt.setBoolean(2, expr.isGrade());
			pstmt.setInt(3, expr.getValue());
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();    
			rs.next();  
			expr.setId(rs.getInt(1));
			
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(rs != null) rs.close();
				if(conn != null) DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return true;
	}
	
    public boolean update(Expression expr){
		
    	Connection conn = DBManager.getConnection();
		String sql = "update expressions set grade = "+expr.isGrade()+" where id="+expr.getId();
		Statement stmt = null;
		
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			try {
				if(stmt != null) stmt.close();
				if(conn != null) DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return true;
	}
    
    public static void main(String[] args) {
    	GNOSConfig.load();
    	Expressions ex = new Expressions();
    	List<Expression> exp = ex.getAll();
    	Expression expr = new Expression("test3");
    	expr.setGrade(true);
    	ex.add(expr);
    	
	}
}
