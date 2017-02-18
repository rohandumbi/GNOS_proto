package com.org.gnos.db.dao;

import static com.org.gnos.db.dao.util.DAOUtil.prepareStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.Field;

public class FieldDAO {

	private static final String SQL_LIST_ORDER_BY_ID = "select  id, name, data_type from  fields where project_id =  ? order by id";
	private static final String SQL_INSERT = "insert into fields (project_id, name, data_type) values (?, ?, ?)";
	private static final String SQL_DELETE = "delete from fields where id = ?";
	private static final String SQL_UPDATE = "update fields set data_type = ? where id = ?";
	
	
	public List<Field> getAll(int projectId) {
		List<Field> fields = new ArrayList<Field>();
		Object[] values = {
				projectId
	   };
		try (
				Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_LIST_ORDER_BY_ID, false, values);
	            ResultSet resultSet = statement.executeQuery();
			){
			while(resultSet.next()){
				fields.add(map(resultSet));
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		}
		return fields;
	}
	
	public boolean create(Field field, int projectId){
		
		if (field.getId() != -1) {
            throw new IllegalArgumentException("Expression is already created.");
        }
		Object[] values = {
				projectId, 
				field.getName(),
				field.getDataType()
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
                    field.setId(generatedKeys.getInt(1));
                } else {
                    //throw new DAOException("Creating user failed, no generated key obtained.");
                }
            }
		} catch(SQLException e){
			e.printStackTrace();
		}
		return true;
	}
	
	
	public boolean update(Field field){
		
		if (field.getId() == -1) {
            throw new IllegalArgumentException("Expression is not created.");
        }
		
		Object[] values = {
				field.getDataType(),
				field.getId()
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
	
	public void delete(Field field){
		
		Object[] values = { 
				field.getId()
	        };

	        try (
	            Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_DELETE, false, values);
	        ) {
	            int affectedRows = statement.executeUpdate();
	            if (affectedRows == 0) {
	                //throw new DAOException("Deleting user failed, no rows affected.");
	            } else {
	            	field.setId(-1);
	            }
	        } catch (SQLException e) {
	            //throw new DAOException(e);
	        }
	}
	
	private Field map(ResultSet rs) throws SQLException {
		Field field = new Field(rs.getString("name"));
		field.setId(rs.getInt("id"));
		field.setDataType(rs.getShort("data_type"));

		return field;
	}
}
