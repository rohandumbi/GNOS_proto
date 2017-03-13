package com.org.gnos.db.dao;

import static com.org.gnos.db.dao.util.DAOUtil.prepareStatement;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.org.gnos.db.DBManager;

public class CycleFixedTimeDAO {

	private static final String SQL_LIST_ORDER_BY_ID = "select fixed_time from truckparam_fixed_time where project_id =  ? ";
	private static final String SQL_INSERT = "insert into truckparam_fixed_time (project_id, fixed_time) values (?, ?)";
	private static final String SQL_DELETE = "delete from truckparam_fixed_time where project_id = ? ";
	private static final String SQL_UPDATE = "update truckparam_fixed_time set fixed_time = ?  where project_id = ?";
	
	
	public BigDecimal getAll(int projectId) {
		Object[] values = {
				projectId
	   };
		BigDecimal truckHourFixedTime = new BigDecimal(-1);
		try (
				Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_LIST_ORDER_BY_ID, false, values);
	            ResultSet resultSet = statement.executeQuery();
			){
			while(resultSet.next()){
				truckHourFixedTime = resultSet.getBigDecimal("fixed_time");
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		}
		return truckHourFixedTime;
	}
	
	public boolean create(int projectId, BigDecimal fixedTime){
		
		Object[] values = {
				projectId, 
				fixedTime
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
	
	
	public boolean update(int projectId, BigDecimal fixedTime){
		
		
		Object[] values = {
				fixedTime,
				projectId
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
	
	public boolean delete(int projectId){
		
		Object[] values = { projectId };

	        try (
	            Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_DELETE, false, values);
	        ) {
	        	statement.executeUpdate();
	           
	        } catch (SQLException e) {
	            return false;
	        }
	        
	        return true;
	}
}
