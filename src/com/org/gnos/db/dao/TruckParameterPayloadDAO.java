package com.org.gnos.db.dao;

import static com.org.gnos.db.dao.util.DAOUtil.prepareStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.TruckParameterPayload;

public class TruckParameterPayloadDAO {

	private static final String SQL_LIST_ORDER_BY_ID = "select material_name, payload from truckparam_material_payload_mapping where project_id = ? ";
	private static final String SQL_INSERT = "insert into truckparam_material_payload_mapping (project_id, material_name, payload) values (?, ?, ?)";
	private static final String SQL_DELETE_ALL = "delete from truckparam_material_payload_mapping where project_id = ?";
	private static final String SQL_DELETE = "delete from truckparam_material_payload_mapping where project_id = ? and material_name = ? ";
	private static final String SQL_UPDATE = "update truckparam_material_payload_mapping set payload = ? where project_id = ? AND material_name = ?";
	
	public List<TruckParameterPayload> getAll(int projectId) {
		
		List<TruckParameterPayload> truckParameterPayloadList = new ArrayList<TruckParameterPayload>();
		Object[] values = {
				projectId
	   };
		try (
				Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_LIST_ORDER_BY_ID, false, values);
	            ResultSet resultSet = statement.executeQuery();
			){
			while(resultSet.next()){			
				truckParameterPayloadList.add(map(resultSet));
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		}
		return truckParameterPayloadList;
	}
	
	public boolean create(TruckParameterPayload truckParameterPayload, int projectId){

		Object[] values = {
				projectId, 
				truckParameterPayload.getMaterialName(),
				truckParameterPayload.getPayload()
	   };

		try ( Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_INSERT, false, values)
			){
			
			statement.executeUpdate();
            
		} catch(SQLException e){
			e.printStackTrace();
		}
		return true;
	}
	
	
	public boolean update(TruckParameterPayload truckParameterPayload, int projectId){
	
		Object[] values = {
				truckParameterPayload.getPayload(),
				projectId, 
				truckParameterPayload.getMaterialName()
				
	   };

		try ( Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_UPDATE, false, values)
			){
			
			statement.executeUpdate();
            
		} catch(SQLException e){
			e.printStackTrace();
		}
		return true;
	}
	
	public void deleteAll(int projectId){
		
		Object[] values = { 
				projectId
	        };

	        try (
	            Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_DELETE_ALL, false, values);
	        ) {
	            statement.executeUpdate();
	            
	        } catch (SQLException e) {
	            //throw new DAOException(e);
	        }
	}
	
	public void delete(int projectId, String materialName){
		
		Object[] values = { 
				projectId,
				materialName
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
	
	private TruckParameterPayload map(ResultSet rs) throws SQLException {
		TruckParameterPayload truckParameterPayload = new TruckParameterPayload();
		truckParameterPayload.setMaterialName(rs.getString("material_name"));
		truckParameterPayload.setPayload(rs.getInt("payload"));	
		return truckParameterPayload;
	}
}
