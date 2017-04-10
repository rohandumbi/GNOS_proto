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
import com.org.gnos.db.model.Product;

public class ProductDAO {

	private static final String SQL_LIST_ORDER_BY_ID = "select name, associated_model_id, child_unit_type, child_unit_id from product_defn where project_id = ? ";
	private static final String SQL_GET_PRODUCT_BY_NAME = "select name, associated_model_id, child_unit_type, child_unit_id from product_defn where project_id = ? and name = ?";
	private static final String SQL_INSERT = "insert into product_defn (project_id, name, associated_model_id, child_unit_type, child_unit_id) values (?, ?, ?, ?, ?)";
	private static final String SQL_DELETE = "delete from product_defn where project_id = ?";
	private static final String SQL_DELETE_PRODUCT = "delete from product_defn where project_id = ? and name = ?";
	private static final String SQL_DELETE_UNIT = "delete from product_defn where project_id = ? and name = ? and child_unit_type = ? , child_unit_id = ?";
	
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

	public Product get(int projectId, String productName) {

		Product product = null;
		Object[] values = { projectId, productName };

		try(
			Connection connection = DBManager.getConnection();
			PreparedStatement statement = prepareStatement(connection, SQL_GET_PRODUCT_BY_NAME, false, values);
			ResultSet resultSet = statement.executeQuery();
		){
			while(resultSet.next()){
				if(product == null) {
					product = map(resultSet, product );
				} else {
					map(resultSet, product );
				}
			}

		} catch(SQLException e){
			e.printStackTrace();
		}

		return product;
	}
	
	public boolean create(Product product, int projectId){

		try ( Connection connection = DBManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_INSERT);
			){
			for(Integer expressionId: product.getExpressionIdList()) {
				
				Object[] values = {
						projectId,
						product.getName(),
						product.getModelId(),
						Product.UNIT_EXPRESSION,
						expressionId	
				};
				setValues(statement, values);
				statement.executeUpdate();
			}
			for(Integer fieldId: product.getFieldIdList()) {
				
				Object[] values = {
						projectId,
						product.getName(),
						product.getModelId(),
						Product.UNIT_FIELD,
						fieldId	
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

	public void deleteProduct(int projectId, String productName){

		Object[] values = { 
				projectId,
				productName
		};

		try (
			Connection connection = DBManager.getConnection();
			PreparedStatement statement = prepareStatement(connection, SQL_DELETE_PRODUCT, false, values);
		) {
			statement.executeUpdate();
		} catch (SQLException e) {
			//throw new DAOException(e);
		}
	}
	
	public void deleteUnit(int projectId, String productName, short unitType, int unitId){

		Object[] values = { 
				projectId,
				productName,
				unitType,
				unitId
		};

		try (
			Connection connection = DBManager.getConnection();
			PreparedStatement statement = prepareStatement(connection, SQL_DELETE_UNIT, false, values);
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
		short unitType = rs.getShort("child_unit_type");
		int childUnitId = rs.getInt("child_unit_id");
		if(unitType == Product.UNIT_FIELD) {
			product.getFieldIdList().add(childUnitId);
		} else {
			product.getExpressionIdList().add(childUnitId);
		}
		
		return product;
	}
}
