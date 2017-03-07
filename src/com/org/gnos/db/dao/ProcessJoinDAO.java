package com.org.gnos.db.dao;

import static com.org.gnos.db.dao.util.DAOUtil.prepareStatement;
import static com.org.gnos.db.dao.util.DAOUtil.setValues;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.ProcessJoin;

public class ProcessJoinDAO {

	private static final String SQL_LIST_ORDER_BY_ID = "select name, child_model_id from process_join_defn where project_id = ? order by name ";
	private static final String SQL_INSERT = "insert into process_join_defn (project_id, name, child_model_id) values (?, ?, ?)";
	private static final String SQL_DELETE_ALL = "delete from process_join_defn where project_id = ? and name = ? ";
	private static final String SQL_DELETE = "delete from process_join_defn where project_id = ? and name = ? and child_model_id = ? ";
	
	public List<ProcessJoin> getAll(int projectId) {
		
		List<ProcessJoin> processJoins = new ArrayList<ProcessJoin>();
		Map<String, ProcessJoin> processJoinMap = new HashMap<String, ProcessJoin>();
		Object[] values = { projectId };
		try (
	            Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_LIST_ORDER_BY_ID, false, values);
	            ResultSet resultSet = statement.executeQuery();
	        ){
			while(resultSet.next()){
				ProcessJoin processJoin = processJoinMap.get(resultSet.getString("name"));
				if(processJoin == null) {
					processJoin = map(resultSet, processJoin);
					processJoinMap.put(processJoin.getName(), processJoin);
					processJoins.add(processJoin);
				} else {
					map(resultSet, processJoin);
				}
			
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		}
		
		return processJoins;
	}
	
	public boolean create(ProcessJoin processJoin, int projectId){

		try ( Connection connection = DBManager.getConnection();
	            PreparedStatement statement = connection.prepareStatement(SQL_INSERT);
			){
			
			for(Integer processId : processJoin.getChildProcessList()) {
				try{
					Object[] values = {
							projectId, 
							processJoin.getName(),
							processId				
					};
					setValues(statement, values);
					statement.executeUpdate();			
				} catch (Exception e) {
					// Ignore exception
				}
			}
			
		} catch(SQLException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	

	
	public void deleteAll(int projectId, String name){
		
		Object[] values = { projectId, name};

	        try (
	            Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_DELETE_ALL, false, values);
	        ) {
	            statement.executeUpdate();
	        } catch (SQLException e) {
	            //throw new DAOException(e);
	        }
	}
	
	public void deleteProcess(int projectId, String name, Integer processId){
		
		Object[] values = { 
				projectId,
				name,
				processId
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
	

	
	private ProcessJoin map(ResultSet rs, ProcessJoin processJoin) throws SQLException {
		if(processJoin == null){
			processJoin = new ProcessJoin();
			processJoin.setName(rs.getString("name"));
		}
		processJoin.getChildProcessList().add(rs.getInt("child_model_id"));
		
		return processJoin;
	}
}
