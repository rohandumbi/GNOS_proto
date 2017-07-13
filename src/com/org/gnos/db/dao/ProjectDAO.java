package com.org.gnos.db.dao;

import static com.org.gnos.db.dao.util.DAOUtil.prepareStatement;
import static com.org.gnos.db.dao.util.DAOUtil.setValues;
import static com.org.gnos.db.dao.util.DAOUtil.toSqlTimeStamp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.Project;


public class ProjectDAO {

	private static final String SQL_GET_BY_ID = "select  id, name, description, created_date, modified_date, file_name from  project left join project_data_files on project_id = id where id = ? ";
	private static final String SQL_LIST_ORDER_BY_MODIFIED_DATE = "select id, name, description, created_date, modified_date, file_name from  project left join project_data_files on project_id = id order by modified_date";
	private static final String SQL_INSERT = "insert into project (name, description, created_date, modified_date) values (?, ?, ?, ?)";
	private static final String SQL_INSERT_DATA_FILE = "insert into project_data_files (project_id, file_name) values (?, ?)";
	private static final String SQL_DELETE = "delete from project where id = ?";
	private static final String SQL_DELETE_DATA_FILE = "delete from project_data_files where project_id = ?";
	private static final String SQL_UPDATE = "update project set description = ?, modified_date = ? where id = ?";
	
	public Project get(int id) {
		
		Project project = null;
		Object[] values = { id };
		try (
	            Connection connection = DBManager.getConnection();
	            PreparedStatement statement =  prepareStatement(connection, SQL_GET_BY_ID, false, values);
	            ResultSet resultSet = statement.executeQuery();
	        ){
			while(resultSet.next()){

				project = map(resultSet, project);
					
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		}
		
		return project;
	}
	
	public List<Project> getAll() {
		
		List<Project> projects = new ArrayList<Project>();
		Map<Integer, Project> projectMap = new HashMap<Integer, Project>();
		try (
	            Connection connection = DBManager.getConnection();
	            PreparedStatement statement = connection.prepareStatement(SQL_LIST_ORDER_BY_MODIFIED_DATE);
	            ResultSet resultSet = statement.executeQuery();
	        ){
			while(resultSet.next()){
				int id = resultSet.getInt("id");
				Project project = projectMap.get(id);
				if(project == null){
					project = map(resultSet, project);
					projectMap.put(id, project);
					projects.add(project);
				} else {
					map(resultSet, project);
				}			
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
	            toSqlTimeStamp(project.getCreatedDate()),
	            toSqlTimeStamp(project.getModifiedDate())
	   };

		try ( Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_INSERT, true, values);
				PreparedStatement statement1 = connection.prepareStatement(SQL_INSERT_DATA_FILE);
			){
			
			statement.executeUpdate();
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    project.setId(generatedKeys.getInt(1));
                }
            }
            if(project.getId() != -1) {
            	for(String fileName: project.getFiles()) {
					Object[] dataFiles = {
							project.getId(),
							fileName					
					};
					setValues(statement1, dataFiles);
					statement1.executeUpdate();
				}
            }
		} catch(SQLException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	public boolean update(Project project, boolean append){
		
		if (project.getId() == -1) {
            throw new IllegalArgumentException("Project is not created.");
        }
		
		Object[] values = {
				project.getDesc(),
	            toSqlTimeStamp(project.getModifiedDate()),
	            project.getId()
	   };

		try ( Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_UPDATE, true, values);
				PreparedStatement statement1 =connection.prepareStatement(SQL_DELETE_DATA_FILE);
				PreparedStatement statement2 = connection.prepareStatement(SQL_INSERT_DATA_FILE);
			){
			
			statement.executeUpdate();  
			
			if(!append) {
				Object[] deletevalues = {
						project.getId()
				};
				setValues(statement1, deletevalues);
				statement1.executeUpdate();
			}
			for(String fileName: project.getFiles()) {
				Object[] dataFiles = {
						project.getId(),
						fileName					
				};
				setValues(statement2, dataFiles);
				statement2.executeUpdate();
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
	        	PreparedStatement statement1 = prepareStatement(connection, SQL_DELETE_DATA_FILE, false, values);
	        ) {
	        	statement1.executeUpdate();
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
	
	private Project map(ResultSet rs, Project project) throws SQLException {
		if(project == null) {
			project = new Project();
			project.setId(rs.getInt("id"));
			project.setName(rs.getString("name"));
			project.setDesc(rs.getString("description"));
			project.setCreatedDate((Date)rs.getTimestamp("created_date"));
			project.setModifiedDate((Date)rs.getTimestamp("modified_date"));
		}
		project.addFile(rs.getString("file_name"));
		
		return project;
	}
}
