package com.org.gnos.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.Project;


public class Projects {

	private static List<Project> projects ;
	private static Project activeProject;
	
	public static List<Project> getAll() {
		
		if(projects != null) return projects;
		
		projects = new ArrayList<Project>();
		
		Connection conn = DBManager.getConnection();
		String sql = "select * from  project";
		
		Statement stmt = null;
		ResultSet rs = null; 
		
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			Project project = null;
			while(rs.next()){
				project = new Project(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), (Date)rs.getTimestamp(5), (Date)rs.getTimestamp(6));
				projects.add(project);
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
		
		return projects;
	}
	
	public static boolean add(Project project){
		
		Connection conn = DBManager.getConnection();
		String sql = "insert into project (name, description, fileName, created_date, modified_date) values (?, ?, ?, ?,?)";
		PreparedStatement pstmt = null;
		ResultSet rs = null; 

		try {
			pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, project.getName());
			pstmt.setString(2, project.getDesc());
			pstmt.setString(3, project.getFileName());
			pstmt.setTimestamp(4, new Timestamp(project.getCreatedDate().getTime()));
			pstmt.setTimestamp(5, new Timestamp(project.getModifiedDate().getTime()));
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();    
			rs.next();  
			project.setId(rs.getInt(1));
			
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

	public static Project getActiveProject() {
		return activeProject;
	}

	public static void setActiveProject(Project activeProject) {
		Projects.activeProject = activeProject;
	}
	
}
