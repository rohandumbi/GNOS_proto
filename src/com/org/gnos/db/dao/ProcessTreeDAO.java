package com.org.gnos.db.dao;

import static com.org.gnos.db.dao.util.DAOUtil.prepareStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.ProcessTreeNode;

public class ProcessTreeDAO {

	private static final String SQL_LIST_ORDER_BY_ID = "select model_id, parent_model_id from process_route_defn where project_id = ? order by model_id ";
	private static final String SQL_INSERT = "insert into process_route_defn (project_id, model_id, parent_model_id) values (?, ?, ?)";
	private static final String SQL_DELETE_ALL = "delete from process_route_defn where project_id = ? ";
	private static final String SQL_DELETE = "delete from process_route_defn where project_id = ? and model_id = ? ";
	
	public List<ProcessTreeNode> getAll(int projectId) {

		List<ProcessTreeNode> processTreeNodeList = new ArrayList<ProcessTreeNode>();
		Object[] values = { projectId };
		try (
	            Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_LIST_ORDER_BY_ID, false, values);
	            ResultSet resultSet = statement.executeQuery();
	        ){
			while(resultSet.next()){
				processTreeNodeList.add(map(resultSet));
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		}
		
		return processTreeNodeList;
	}
	
	public boolean create(ProcessTreeNode processTreeNode, int projectId){

		Object[] values = {
				projectId, 
				processTreeNode.getModelId(),
				processTreeNode.getParentModelId()				
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
	
	public void deleteProcessTreeNode(int projectId, int modelId){
		
		Object[] values = { 
				projectId,
				modelId
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
	

	
	private ProcessTreeNode map(ResultSet rs) throws SQLException {
		ProcessTreeNode processTreeNode = new ProcessTreeNode();		
		processTreeNode.setModelId(rs.getInt("model_id"));
		processTreeNode.setParentModelId(rs.getInt("parent_model_id"));		
		return processTreeNode;
	}
}
