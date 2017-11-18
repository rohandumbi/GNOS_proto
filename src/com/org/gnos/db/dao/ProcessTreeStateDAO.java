package com.org.gnos.db.dao;

import static com.org.gnos.db.dao.util.DAOUtil.prepareStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.ProcessTreeNodeState;

public class ProcessTreeStateDAO {

	private static final String SQL_LIST_ORDER_BY_ID = "select node_name, xlocation, ylocation from process_tree_state where project_id = ? ";
	private static final String SQL_INSERT = "insert into process_tree_state (project_id, node_name, xlocation, ylocation) values (?, ?, ?, ?)";
	private static final String SQL_DELETE_ALL = "delete from process_tree_state where project_id = ? ";
	private static final String SQL_DELETE = "delete from process_route_defn where project_id = ? and node_name = ? ";
	
	public List<ProcessTreeNodeState> getAll(int projectId) {

		List<ProcessTreeNodeState> processTreeNodeStateList = new ArrayList<ProcessTreeNodeState>();
		Object[] values = { projectId };
		try (
	            Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_LIST_ORDER_BY_ID, false, values);
	            ResultSet resultSet = statement.executeQuery();
	        ){
			while(resultSet.next()){
				processTreeNodeStateList.add(map(resultSet));
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		}
		
		return processTreeNodeStateList;
	}
	
	public boolean create(ProcessTreeNodeState processTreeNodeState, int projectId){

		Object[] values = {
				projectId, 
				processTreeNodeState.getNodeName(),
				processTreeNodeState.getxLoc(),
				processTreeNodeState.getyLoc()
		};
		
		try ( Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_INSERT, false, values);
			){
			statement.executeUpdate();
			
		} catch(SQLException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	

	
	public void deleteAll(int projectId){
		
		Object[] values = { projectId};

	        try (
	            Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_DELETE_ALL, false, values);
	        ) {
	            statement.executeUpdate();
	        } catch (SQLException e) {
	            //throw new DAOException(e);
	        }
	}
	
	public void deleteProcessTreeNodeState(int projectId, String nodeName){
		
		Object[] values = { 
				projectId,
				nodeName
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
	

	
	private ProcessTreeNodeState map(ResultSet rs) throws SQLException {
		ProcessTreeNodeState processTreeNodeState = new ProcessTreeNodeState();		
		processTreeNodeState.setNodeName(rs.getString("node_name"));
		processTreeNodeState.setxLoc(rs.getFloat("xlocation"));
		processTreeNodeState.setyLoc(rs.getFloat("ylocation"));		
		return processTreeNodeState;
	}
}
