package com.org.gnos.db.dao;

import static com.org.gnos.db.dao.util.DAOUtil.prepareStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.Expression;

public class ExpressionDAO {

	private static final String SQL_LIST_ORDER_BY_ID = "select id, name, is_grade, is_complex, expr_value, filter from  expressions order by id";
	private static final String SQL_INSERT = "insert into expressions (project_id, name, is_grade, is_complex, expr_value, filter) values (?, ?, ?, ?, ?,?)";
	private static final String SQL_DELETE = "delete from expressions where id = ?";
	private static final String SQL_UPDATE = "update expressions set is_grade = ?, is_complex = ?, expr_value = ?, filter = ? where id = ?";
	
	public List<Expression> getAll() {
		
		List<Expression> expressions = new ArrayList<Expression>();
		
		try (
	            Connection connection = DBManager.getConnection();
	            PreparedStatement statement = connection.prepareStatement(SQL_LIST_ORDER_BY_ID);
	            ResultSet resultSet = statement.executeQuery();
	        ){
			while(resultSet.next()){
				expressions.add(map(resultSet));				
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		}
		
		return expressions;
	}
	
	public boolean create(Expression expression){
		
		if (expression.getId() != -1) {
            throw new IllegalArgumentException("Expression is already created.");
        }
		int projectId = ProjectConfigutration.getInstance().getProjectId();
		Object[] values = {
				projectId, 
				expression.getName(),
				expression.isGrade(),
				expression.isComplex(),
				expression.getExprvalue(),
				expression.getFilter()
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
                    expression.setId(generatedKeys.getInt(1));
                } else {
                    //throw new DAOException("Creating user failed, no generated key obtained.");
                }
            }
		} catch(SQLException e){
			e.printStackTrace();
		}
		return true;
	}
	
	
	public boolean update(Expression expression){
		
		if (expression.getId() == -1) {
            throw new IllegalArgumentException("Expression is not created.");
        }
		
		Object[] values = {
				expression.isGrade(),
				expression.isComplex(),
				expression.getExprvalue(),
				expression.getFilter(),
				expression.getId()
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
	
	public void delete(Expression expression){
		
		Object[] values = { 
				expression.getId()
	        };

	        try (
	            Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_DELETE, false, values);
	        ) {
	            int affectedRows = statement.executeUpdate();
	            if (affectedRows == 0) {
	                //throw new DAOException("Deleting user failed, no rows affected.");
	            } else {
	            	expression.setId(-1);
	            }
	        } catch (SQLException e) {
	            //throw new DAOException(e);
	        }
	}
	
	private Expression map(ResultSet rs) throws SQLException {
		Expression expression = new Expression();
		expression.setId(rs.getInt("id"));
		expression.setName(rs.getString("name"));
		expression.setGrade(rs.getBoolean("is_grade"));
		expression.setComplex(rs.getBoolean("is_complex"));
		expression.setExprvalue(rs.getString("expr_value"));
		expression.setFilter(rs.getString("filter"));

		return expression;
	}
	
}
