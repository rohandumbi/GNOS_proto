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
import com.org.gnos.db.model.PitGroup;

public class PitGroupDAO {
	private static final String SQL_LIST_ORDER_BY_ID = "select name, child_type, child from pitgroup_pit_mapping where project_id = ?";
	private static final String SQL_INSERT = "insert into pitgroup_pit_mapping ( project_id , name, child_type, child ) values ( ? , ?, ?, ?) ";
	private static final String SQL_INSERT_NEW = "insert into pitgroup_pit_mapping ( project_id , name ) values ( ? , ?) ";
	private static final String SQL_DELETE_ALL = "delete from pitgroup_pit_mapping where project_id = ?";
	private static final String SQL_DELETE = "delete from pitgroup_pit_mapping where project_id = ? and name = ? and child_type = ? and child = ? ";
	private static final String SQL_DELETE_GROUP = "delete from pitgroup_pit_mapping where project_id = ? and name = ? ";
	
	public List<PitGroup> getAll(int projectId) {
		
		List<PitGroup> pitGroups = new ArrayList<PitGroup>();
		Map<String, PitGroup> pitGroupMap = new HashMap<String, PitGroup>();
		Object[] values = { projectId };
		try (
	            Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_LIST_ORDER_BY_ID, false, values);
	            ResultSet resultSet = statement.executeQuery();
	        ){
			int count = 1;
			while(resultSet.next()){
				PitGroup pitGroup = pitGroupMap.get(resultSet.getString("name"));
				if(pitGroup == null) {
					pitGroup = map(resultSet, pitGroup);
					pitGroupMap.put(pitGroup.getName(), pitGroup);
					pitGroups.add(pitGroup);
					pitGroup.setPitGroupNumber(count);
					count++;
				} else {
					map(resultSet, pitGroup);
				}
			
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		}
		
		return pitGroups;
	}
	
	public boolean create(PitGroup pitGroup, int projectId){

		try ( Connection connection = DBManager.getConnection();
	            PreparedStatement statement = connection.prepareStatement(SQL_INSERT);
				PreparedStatement createStatement = connection.prepareStatement(SQL_INSERT_NEW);
			){
			
			if((pitGroup.getListChildPits().size() == 0) || (pitGroup.getListChildPitGroups().size() == 0)){
				try{
					Object[] values = {
							projectId, 
							pitGroup.getName()
					};
					setValues(createStatement, values);
					createStatement.executeUpdate();			
				} catch (Exception e) {
					// Ignore exception
				}
			}
			
			for(String pitName : pitGroup.getListChildPits()) {
				try{
					Object[] values = {
							projectId, 
							pitGroup.getName(),
							PitGroup.CHILD_PIT,
							pitName				
					};
					setValues(statement, values);
					statement.executeUpdate();			
				} catch (Exception e) {
					// Ignore exception
				}
			}
			for(String pitGroupName : pitGroup.getListChildPitGroups()) {
				try{
					Object[] values = {
							projectId, 
							pitGroup.getName(),
							PitGroup.CHILD_PIT_GROUP,
							pitGroupName				
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
	
	public void deletePit(int projectId, String name, String pitName){
		
		Object[] values = { 
				projectId,
				name,
				PitGroup.CHILD_PIT,
				pitName
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
	
	public void deleteChildPitGroup(int projectId, String name, String pitGroupName){
		
		Object[] values = { 
				projectId,
				name,
				PitGroup.CHILD_PIT_GROUP,
				pitGroupName
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
	
	public void deletePitGroup(int projectId, String name){
		
		Object[] values = { 
				projectId,
				name
		};

        try (
            Connection connection = DBManager.getConnection();
            PreparedStatement statement = prepareStatement(connection, SQL_DELETE_GROUP, false, values);
        ) {
            statement.executeUpdate();
        } catch (SQLException e) {
            //throw new DAOException(e);
        }
	}
	
	
	
	private PitGroup map(ResultSet rs, PitGroup pitGroup) throws SQLException {
		if(pitGroup == null){
			pitGroup = new PitGroup();
			pitGroup.setName(rs.getString("name"));
		}
		short childType = rs.getShort("child_type");
		String child = rs.getString("child");
		if(childType == PitGroup.CHILD_PIT) {
			pitGroup.addPit(child);
		} else if(childType == PitGroup.CHILD_PIT_GROUP) {
			pitGroup.addPitGroup(child);
		}
		return pitGroup;
	}
}
