package com.org.gnos.db.dao;

import static com.org.gnos.db.dao.util.DAOUtil.prepareStatement;
import static com.org.gnos.db.dao.util.DAOUtil.setValues;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.TruckParameterCycleTime;

public class TruckParameterCycleTimeDAO {

	private static final String SQL_LIST_ORDER_BY_ID = "select stockpile_name, process_name, value from truckparam_cycle_time where project_id = ? order by stockpile_name";
	private static final String SQL_INSERT = "insert into truckparam_cycle_time (project_id, stockpile_name, process_name, value) values (?, ?, ?, ?)";
	private static final String SQL_DELETE = "delete from truckparam_cycle_time where project_id = ?";
	private static final String SQL_UPDATE = "update truckparam_cycle_time set value = ? where project_id = ? AND stockpile_name = ? AND process_name = ?";
	
	public List<TruckParameterCycleTime> getAll(int projectId) {
		List<TruckParameterCycleTime> truckParameterCycleTimeList = new ArrayList<TruckParameterCycleTime>();
		Map<String, TruckParameterCycleTime> truckParameterCycleTimeMap = new HashMap<String, TruckParameterCycleTime>();
		Object[] values = {
				projectId
	   };
		try (
				Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_LIST_ORDER_BY_ID, false, values);
	            ResultSet resultSet = statement.executeQuery();
			){
			while(resultSet.next()){
				String stockPileName = resultSet.getString("stockpile_name");
				TruckParameterCycleTime truckParameterCycleTime = truckParameterCycleTimeMap.get(stockPileName);
				if(truckParameterCycleTime == null) {
					truckParameterCycleTime = map(resultSet,truckParameterCycleTime);
					truckParameterCycleTimeList.add(truckParameterCycleTime);
					truckParameterCycleTimeMap.put(stockPileName, truckParameterCycleTime);
				} else {
					map(resultSet,truckParameterCycleTime);
				}
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		}
		return truckParameterCycleTimeList;
	}
	
	public boolean create(TruckParameterCycleTime truckParameterCycleTime, int projectId){

		

		try ( Connection connection = DBManager.getConnection();
	            PreparedStatement statement = connection.prepareStatement(SQL_INSERT);
			){
			Map<String, BigDecimal> processdata = truckParameterCycleTime.getProcessData();
			Set<String> processNames = processdata.keySet();
			for(String processName: processNames){
				Object[] values = {
						projectId, 
						truckParameterCycleTime.getStockPileName(),
						processName,
						processdata.get(processName)
			   };
				setValues(statement, values);
				statement.executeUpdate();
			}
			
			
            
		} catch(SQLException e){
			e.printStackTrace();
		}
		return true;
	}
	
	
	public boolean update(TruckParameterCycleTime truckParameterCycleTime, int projectId){
	


		try ( Connection connection = DBManager.getConnection();
	            PreparedStatement statement = connection.prepareStatement(SQL_UPDATE);
			){
			Map<String, BigDecimal> processdata = truckParameterCycleTime.getProcessData();
			Set<String> processNames = processdata.keySet();
			for(String processName: processNames){
				Object[] values = {
						processdata.get(processName),
						projectId, 
						truckParameterCycleTime.getStockPileName(),
						processName					
			   };
				setValues(statement, values);
				statement.executeUpdate();
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
		return true;
	}
	
	public void delete(int projectId){
		
		Object[] values = { 
				projectId
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
	
	private TruckParameterCycleTime map(ResultSet rs, TruckParameterCycleTime truckParameterCycleTime) throws SQLException {
		if(truckParameterCycleTime == null) {
			truckParameterCycleTime = new TruckParameterCycleTime();
			truckParameterCycleTime.setStockPileName(rs.getString("stockpile_name"));
		}
		truckParameterCycleTime.getProcessData().put(rs.getString("process_name"), rs.getBigDecimal("value"));
		
		return truckParameterCycleTime;
	}
}
