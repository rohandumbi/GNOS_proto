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
import java.util.Set;

import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.Product;

public class ProductDAO {

	private static final String SQL_LIST_ORDER_BY_ID = "select name, associated_model_id, child_expression_id from product_defn where project_id = ? ";
	private static final String SQL_INSERT = "insert into product_defn (project_id, name, associated_model_id, child_expression_id) values (?, ?, ?, ?)";
	private static final String SQL_DELETE = "delete from product_defn where project_id = ?";
	//private static final String SQL_UPDATE = "update product_defn set associated_model_id = ? , child_expression_id = ? where project_id = ? and name = ?";
	
	public List<Product> getAll(int projectId) {

		List<Product> productList = new ArrayList<Product>();
		Map<String, Product> productMap = new HashMap<String, Product>();
		Object[] values = { projectId };

		try(
			Connection connection = DBManager.getConnection();
			PreparedStatement statement = prepareStatement(connection, SQL_LIST_ORDER_BY_ID, false, values);
			ResultSet resultSet = statement.executeQuery();
		){
			while(resultSet.next()){
				Product product = productMap.get(resultSet.getString("name"));
				if(product == null) {
					product = map(resultSet, product );
					productMap.put(product.getName(), product);
					productList.add(product);
				} else {
					map(resultSet, product );
				}
			}

		} catch(SQLException e){
			e.printStackTrace();
		}

		return productList;
	}

	public boolean create(Product product, int projectId){

		try ( Connection connection = DBManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_INSERT);
			){
			Set<Integer> expressionIdList = product.getExpressionIdList();
			for(Integer expressionId: expressionIdList) {
				
				Object[] values = {
						projectId,
						product.getName(),
						product.getModelId(),
						expressionId	
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

		Object[] values = { projectId };

		try (
			Connection connection = DBManager.getConnection();
			PreparedStatement statement = prepareStatement(connection, SQL_DELETE, false, values);
		) {
			statement.executeUpdate();
		} catch (SQLException e) {
			//throw new DAOException(e);
		}
	}

	private Product map(ResultSet rs, Product product) throws SQLException {
		if(product == null) {
			product = new Product();
			product.setName(rs.getString("name"));
			product.setModelId(rs.getInt("associated_model_id"));
			
		}
		product.getExpressionIdList().add(rs.getInt("child_expression_id"));
		return product;
	}
}
