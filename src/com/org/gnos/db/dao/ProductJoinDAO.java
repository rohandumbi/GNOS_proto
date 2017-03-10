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
import com.org.gnos.db.model.ProductJoin;

public class ProductJoinDAO {

	private static final String SQL_LIST_ORDER_BY_ID = "select name, child_type, child from product_join_defn where project_id = ?";
	private static final String SQL_INSERT = "insert into product_join_defn ( project_id , name, child_type, child ) values ( ? , ?, ?, ?) ";
	private static final String SQL_INSERT_EMPTY = "insert into product_join_defn ( project_id , name) values ( ? , ?) ";
	private static final String SQL_DELETE_ALL = "delete from product_join_defn where project_id = ?";
	private static final String SQL_DELETE = "delete from product_join_defn where project_id = ? and name = ? and child_type = ? and child = ? ";
	
	public List<ProductJoin> getAll(int projectId) {
		
		List<ProductJoin> productJoins = new ArrayList<ProductJoin>();
		Map<String, ProductJoin> productJoinMap = new HashMap<String, ProductJoin>();
		Object[] values = { projectId };
		try (
	            Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_LIST_ORDER_BY_ID, false, values);
	            ResultSet resultSet = statement.executeQuery();
	        ){
			while(resultSet.next()){
				ProductJoin productJoin = productJoinMap.get(resultSet.getString("name"));
				if(productJoin == null) {
					productJoin = map(resultSet, productJoin);
					productJoinMap.put(productJoin.getName(), productJoin);
					productJoins.add(productJoin);
				} else {
					map(resultSet, productJoin);
				}
			
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		}
		
		return productJoins;
	}
	
	public boolean create(ProductJoin productJoin, int projectId){

		try ( Connection connection = DBManager.getConnection();
	            PreparedStatement statement = connection.prepareStatement(SQL_INSERT);
				PreparedStatement statement1 = connection.prepareStatement(SQL_INSERT_EMPTY);
			){
			
			if(productJoin.getProductJoinList().size()==0 && productJoin.getProductList().size()==0){
				try{
					Object[] values = {
							projectId, 
							productJoin.getName()			
					};
					setValues(statement1, values);
					statement1.executeUpdate();			
				} catch (Exception e) {
					// Ignore exception
				}
			}else{
				for(String pitName : productJoin.getProductList()) {
					try{
						Object[] values = {
								projectId, 
								productJoin.getName(),
								ProductJoin.CHILD_PRODUCT,
								pitName				
						};
						setValues(statement, values);
						statement.executeUpdate();			
					} catch (Exception e) {
						// Ignore exception
					}
				}
				for(String pitGroupName : productJoin.getProductJoinList()) {
					try{
						Object[] values = {
								projectId, 
								productJoin.getName(),
								ProductJoin.CHILD_PRODUCT_JOIN,
								pitGroupName				
						};
						setValues(statement, values);
						statement.executeUpdate();
						
					} catch (Exception e) {
						// Ignore exception
					}
				}
			}
			
		} catch(SQLException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	

	
	public void deleteAll(int projectId, String productJoinName){
		
		Object[] values = { 
				projectId,
				productJoinName
		};

	        try (
	            Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_DELETE_ALL, false, values);
	        ) {
	            statement.executeUpdate();
	        } catch (SQLException e) {
	            //throw new DAOException(e);
	        }
	}
	
	public void deleteProduct(int projectId, String name, String productName){
		
		Object[] values = { 
				projectId,
				name,
				ProductJoin.CHILD_PRODUCT,
				productName
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
	
	public void deleteProductJoin(int projectId, String name, String productJoinName){
		
		Object[] values = { 
				projectId,
				name,
				ProductJoin.CHILD_PRODUCT_JOIN,
				productJoinName
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
	
	private ProductJoin map(ResultSet rs, ProductJoin productJoin) throws SQLException {
		if(productJoin == null){
			productJoin = new ProductJoin();
			productJoin.setName(rs.getString("name"));
		}
		short childType = rs.getShort("child_type");
		String child = rs.getString("child");
		if(childType == ProductJoin.CHILD_PRODUCT) {
			productJoin.getProductList().add(child);
		} else if(childType == ProductJoin.CHILD_PRODUCT_JOIN) {
			productJoin.getProductJoinList().add(child);
		}
		return productJoin;
	}
}
