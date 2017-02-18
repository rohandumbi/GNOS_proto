package com.org.gnos.db.dao;

import static com.org.gnos.db.dao.util.DAOUtil.prepareStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Model;

public class ModelDAO {

	private static final String SQL_LIST_ORDER_BY_ID = "select a.id as id, a.name, a.expr_id, a.filter_str, b.id as eid, b.name as ename from models a, expressions b where a.project_id = ? AND b.id = a.expr_id order by a.id";
	private static final String SQL_INSERT = "insert into models (project_id, name, expr_id, filter_str) values (?, ?, ?, ?)";
	private static final String SQL_DELETE = "delete from models where id = ?";
	private static final String SQL_UPDATE = "update models set expr_id= ? , filter_str = ? where id = ?";
	
	public List<Model> getAll(int projectId) {
		
		List<Model> models = new ArrayList<Model>();
		Object[] values = {
				projectId, 
	   };
		
		try (
	            Connection connection = DBManager.getConnection();
				PreparedStatement statement = prepareStatement(connection, SQL_LIST_ORDER_BY_ID, false, values);
	            ResultSet resultSet = statement.executeQuery();
	        ){
			while(resultSet.next()){
				models.add(map(resultSet));				
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		}
		
		return models;
	}
	
	public boolean create(Model model, int projectId){
		
		if (model.getId() != -1) {
            throw new IllegalArgumentException("Model is already created.");
        }
		Object[] values = {
				projectId, 
				model.getName(),
				model.getExpression().getId(),
				model.getCondition()
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
                    model.setId(generatedKeys.getInt(1));
                } else {
                    //throw new DAOException("Creating user failed, no generated key obtained.");
                }
            }
		} catch(SQLException e){
			e.printStackTrace();
		}
		return true;
	}
	
	
	public boolean update(Model model){
		
		if (model.getId() == -1) {
            throw new IllegalArgumentException("Expression is not created.");
        }
		
		Object[] values = {
				model.getExpression().getId(),
				model.getCondition(),
				model.getId()
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
	
	public void delete(Model model){
		
		Object[] values = { 
				model.getId()
	        };

	        try (
	            Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_DELETE, false, values);
	        ) {
	            int affectedRows = statement.executeUpdate();
	            if (affectedRows == 0) {
	                //throw new DAOException("Deleting user failed, no rows affected.");
	            } else {
	            	model.setId(-1);
	            }
	        } catch (SQLException e) {
	            //throw new DAOException(e);
	        }
	}
	
	private Model map(ResultSet rs) throws SQLException {
		Model model = new Model();
		model.setId(rs.getInt("id"));
		model.setName(rs.getString("name"));
		model.setCondition(rs.getString("filter_str"));
		
		Expression expression = new Expression();
		expression.setId(rs.getInt("eid"));
		expression.setName(rs.getString("ename"));
		model.setExpression(expression);
		return model;
	}
	
}
