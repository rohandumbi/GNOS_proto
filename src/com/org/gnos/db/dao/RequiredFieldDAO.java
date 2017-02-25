package com.org.gnos.db.dao;

import static com.org.gnos.db.dao.util.DAOUtil.prepareStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.RequiredField;

public class RequiredFieldDAO {

	private static final String SQL_LIST_ORDER_BY_ID = "select field_name, mapped_field_name from required_field_mapping where project_id =  ? ";
	private static final String SQL_INSERT = "insert into required_field_mapping (project_id, field_name, mapped_field_name) values (?, ?, ?)";
	private static final String SQL_DELETE = "delete from required_field_mapping where project_id = ?";
	private static final String SQL_UPDATE = "update required_field_mapping set mapped_field_name = ? where project_id =? and field_name = ?";
	
	
	public List<RequiredField> getAll(int projectId) {
		List<RequiredField> requiredFields = new ArrayList<RequiredField>();
		Object[] values = {
				projectId
	   };
		try (
				Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_LIST_ORDER_BY_ID, false, values);
	            ResultSet resultSet = statement.executeQuery();
			){
			while(resultSet.next()){
				requiredFields.add(map(resultSet));
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		}
		return requiredFields;
	}
	
	public boolean create(RequiredField requiredField, int projectId){
		
		Object[] values = {
				projectId, 
				requiredField.getFieldName(),
				requiredField.getMappedFieldname()
	   };

		try ( Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_INSERT, false, values);
			){
			
			statement.executeUpdate();
		} catch(SQLException e){
			e.printStackTrace();
		}
		return true;
	}
	
	
	public boolean update(RequiredField requiredField, int projectId){
		
		
		Object[] values = {
				requiredField.getMappedFieldname(),
				projectId, 
				requiredField.getFieldName()
	   };

		try ( Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_UPDATE, true, values);
			){
			
			statement.executeUpdate();        
          
		} catch(SQLException e){
			e.printStackTrace();
		}
		return true;
	}
	
	public void delete(int projectId){
		
		Object[] values = { projectId };

	        try (
	            Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_DELETE, false, values);
	        ) {
	            int affectedRows = statement.executeUpdate();
	            if (affectedRows == 0) {
	                //throw new DAOException("Deleting user failed, no rows affected.");
	            } else {

	            }
	        } catch (SQLException e) {
	            //throw new DAOException(e);
	        }
	}
	
	private RequiredField map(ResultSet rs) throws SQLException {
		RequiredField rfield = new RequiredField();
		rfield.setFieldName(rs.getString("field_name"));
		rfield.setMappedFieldname(rs.getString("mapped_field_name"));
		
		return rfield;
	}
}
