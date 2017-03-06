package com.org.gnos.db.dao;

import static com.org.gnos.db.dao.util.DAOUtil.prepareStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.CycleTimeFieldMapping;

public class CycleTimeFieldMappingDAO {

	private static final String SQL_LIST_ORDER_BY_ID = "select  field_name, mapping_type, mapped_field_name from  cycletime_field_mapping where project_id =  ? order by id";
	private static final String SQL_INSERT = "insert into cycletime_field_mapping (project_id, field_name, mapping_type, mapped_field_name) values (?, ?, ?, ?)";
	private static final String SQL_DELETE_ALL = "delete from cycletime_field_mapping where project_id = ?";
	private static final String SQL_DELETE = "delete from cycletime_field_mapping where project_id = ? and field_name ? and mapping_type = ? ";
	private static final String SQL_UPDATE = "update cycletime_field_mapping set mapped_field_name = ?  where project_id = ? and field_name ? and mapping_type = ? ";
	
	
	public List<CycleTimeFieldMapping> getAll(int projectId) {
		List<CycleTimeFieldMapping> cycleTimeFieldMappingList = new ArrayList<CycleTimeFieldMapping>();
		Object[] values = {
				projectId
	   };
		try (
				Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_LIST_ORDER_BY_ID, false, values);
	            ResultSet resultSet = statement.executeQuery();
			){
			while(resultSet.next()){
				cycleTimeFieldMappingList.add(map(resultSet));
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		}
		return cycleTimeFieldMappingList;
	}
	
	public boolean create(CycleTimeFieldMapping cycleTimeFieldMapping, int projectId){
		
		Object[] values = {
				projectId, 
				cycleTimeFieldMapping.getFieldName(),
				cycleTimeFieldMapping.getMappingType(),
				cycleTimeFieldMapping.getMappedFieldName()
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
	
	
	public boolean update(CycleTimeFieldMapping cycleTimeFieldMapping, int projectId){
		
		
		Object[] values = {
				cycleTimeFieldMapping.getMappedFieldName(),
				projectId, 
				cycleTimeFieldMapping.getFieldName(),
				cycleTimeFieldMapping.getMappingType()
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
	
	public void deleteAll(int projectId){
		
		Object[] values = { projectId };

	        try (
	            Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_DELETE_ALL, false, values);
	        ) {
	        	statement.executeUpdate();
	           
	        } catch (SQLException e) {
	            //throw new DAOException(e);
	        }
	}
	
	public void delete(CycleTimeFieldMapping cycleTimeFieldMapping, int projectId){
		
		Object[] values = { 
				projectId,
				cycleTimeFieldMapping.getFieldName(),
				cycleTimeFieldMapping.getMappingType()
		};

	        try (
	            Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_DELETE, false, values);
	        ) {
	        	statement.executeUpdate();
	           
	        } catch (SQLException e) {
	            //throw new DAOException(e);
	        }
	}

	private CycleTimeFieldMapping map(ResultSet rs) throws SQLException {
		CycleTimeFieldMapping cycleTimeFieldMapping = new CycleTimeFieldMapping();
		cycleTimeFieldMapping.setFieldName(rs.getString("field_name"));
		cycleTimeFieldMapping.setMappingType(rs.getShort("mapping_type"));
		cycleTimeFieldMapping.setMappedFieldName(rs.getString("mapped_field_name"));
		
		return cycleTimeFieldMapping;
	}
}
