package com.org.gnos.db.dao;

import static com.org.gnos.db.dao.util.DAOUtil.prepareStatement;
import static com.org.gnos.db.dao.util.DAOUtil.toSqlTimeStamp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.Project;


public class ProjectDAO {

	private static final String SQL_LIST_ORDER_BY_MODIFIED_DATE = "select id, name, description, fileName, created_date, modified_date from  project order by modified_date";
	private static final String SQL_INSERT = "insert into project (name, description, fileName, created_date, modified_date) values (?, ?, ?, ?,?)";
	private static final String SQL_DELETE = "delete from project where id = ?";
	
	public List<Project> getAll() {
		
		List<Project> projects = new ArrayList<Project>();
		
		try (
	            Connection connection = DBManager.getConnection();
	            PreparedStatement statement = connection.prepareStatement(SQL_LIST_ORDER_BY_MODIFIED_DATE);
	            ResultSet resultSet = statement.executeQuery();
	        ){
			while(resultSet.next()){
				projects.add(map(resultSet));				
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		}
		
		return projects;
	}
	
	public boolean create(Project project){
		
		if (project.getId() != -1) {
            throw new IllegalArgumentException("Project is already created.");
        }
		
		Object[] values = {
				project.getName(),
				project.getDesc(),
	            project.getFileName(),
	            toSqlTimeStamp(project.getCreatedDate()),
	            toSqlTimeStamp(project.getModifiedDate())
	   };

		try ( Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_INSERT, true, values);
			){
			
			int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                //throw new DAOException("Creating user failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    project.setId(generatedKeys.getInt(1));
                } else {
                    //throw new DAOException("Creating user failed, no generated key obtained.");
                }
            }
		} catch(SQLException e){
			e.printStackTrace();
		}
		return true;
	}
	
	public void delete(Project project){
		
		Object[] values = { 
	            project.getId()
	        };

	        try (
	            Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_DELETE, false, values);
	        ) {
	            int affectedRows = statement.executeUpdate();
	            if (affectedRows == 0) {
	                //throw new DAOException("Deleting user failed, no rows affected.");
	            } else {
	            	project.setId(-1);
	            }
	        } catch (SQLException e) {
	            //throw new DAOException(e);
	        }
	}
	
	private Project map(ResultSet rs) throws SQLException {
		Project project = new Project();
		project.setId(rs.getInt("id"));
		project.setName(rs.getString("name"));
		project.setDesc(rs.getString("description"));
		project.setFileName(rs.getString("fileName"));
		project.setCreatedDate((Date)rs.getTimestamp("created_date"));
		project.setModifiedDate((Date)rs.getTimestamp("modified_date"));

		return project;
	}
}
