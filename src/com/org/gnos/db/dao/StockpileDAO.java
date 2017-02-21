package com.org.gnos.db.dao;

import static com.org.gnos.db.dao.util.DAOUtil.prepareStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.Stockpile;


public class StockpileDAO {

	private static final String SQL_LIST_ORDER_BY_ID = "select id, type, name, condition_str, mapped_to, mapping_type, has_capacity, capacity, is_reclaim from stockpile where project_id = ? order by id asc";
	private static final String SQL_INSERT = "insert into stockpile ( project_id , type, name, condition_str, mapped_to, mapping_type, has_capacity, capacity, is_reclaim) values ( ? , ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String SQL_DELETE = "delete from stockpile where id = ?";
	private static final String SQL_UPDATE = "update stockpile set type = ? , name = ?, condition_str = ?, mapped_to = ?, mapping_type= ?, has_capacity = ?, capacity = ?, is_reclaim= ? where id = ?";

	public List<Stockpile> getAll(int projectId) {

		List<Stockpile> stockpiles = new ArrayList<Stockpile>();

		Object[] values = { projectId };

		try(
			Connection connection = DBManager.getConnection();
			PreparedStatement statement = prepareStatement(connection, SQL_LIST_ORDER_BY_ID, false, values);
			ResultSet resultSet = statement.executeQuery();
		){
			while(resultSet.next()){
				stockpiles.add(map(resultSet));				
			}

		} catch(SQLException e){
			e.printStackTrace();
		}

		return stockpiles;
	}

	public boolean create(Stockpile stockpile, int projectId){

		if (stockpile.getId() != -1) {
			throw new IllegalArgumentException("Scenario is already created.");
		}
		Object[] values = {
				projectId,
				stockpile.getType(),
				stockpile.getName(),
				stockpile.getCondition(),
				stockpile.getMappedTo(),
				stockpile.getMappingType(),
				stockpile.isHasCapacity(),
				stockpile.getCapacity(),
				stockpile.isReclaim()
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
					stockpile.setId(generatedKeys.getInt(1));
				} else {
					//throw new DAOException("Creating user failed, no generated key obtained.");
				}
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
		return true;
	}


	public boolean update(Stockpile stockpile){

		if (stockpile.getId() == -1) {
			throw new IllegalArgumentException("Project is not created.");
		}

		Object[] values = {
				stockpile.getType(),
				stockpile.getName(),
				stockpile.getCondition(),
				stockpile.getMappedTo(),
				stockpile.getMappingType(),
				stockpile.isHasCapacity(),
				stockpile.getCapacity(),
				stockpile.isReclaim(),
				stockpile.getId()
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

	public void delete(Stockpile stockpile){

		Object[] values = { 
				stockpile.getId()
		};

		try (
				Connection connection = DBManager.getConnection();
				PreparedStatement statement = prepareStatement(connection, SQL_DELETE, false, values);
				) {
			int affectedRows = statement.executeUpdate();
			if (affectedRows == 0) {
				//throw new DAOException("Deleting user failed, no rows affected.");
			} else {
				stockpile.setId(-1);
			}
		} catch (SQLException e) {
			//throw new DAOException(e);
		}
	}

	private Stockpile map(ResultSet rs) throws SQLException {
		Stockpile stockpile = new Stockpile();
		stockpile.setId(rs.getInt("id"));
		stockpile.setType(rs.getInt("type"));
		stockpile.setName(rs.getString("name"));
		stockpile.setMappingType(rs.getInt("mapping_type"));
		stockpile.setMappedTo(rs.getString("mapped_to"));
		stockpile.setCondition(rs.getString("condition_str"));
		stockpile.setHasCapacity(rs.getBoolean("has_capacity"));
		stockpile.setCapacity(rs.getInt("capacity"));
		stockpile.setReclaim(rs.getBoolean("is_reclaim"));
		return stockpile;
	}
}
