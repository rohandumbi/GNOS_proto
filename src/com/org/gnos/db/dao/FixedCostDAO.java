package com.org.gnos.db.dao;

import static com.org.gnos.db.dao.util.DAOUtil.prepareStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.FixedOpexCost;

public class FixedCostDAO {

	private static final String SQL_LIST_ORDER_BY_ID = "select cost_head, year, value, value from fixedcost_year_mapping where scenario_id = ? order by cost_head";
	private static final String SQL_INSERT = "insert into fixedcost_year_mapping (scenario_id, cost_head, year, value) values (?, ?, ?, ?)";
	private static final String SQL_DELETE = "delete from fixedcost_year_mapping where scenario_id = ?";
	private static final String SQL_UPDATE = "update fixedcost_year_mapping set value = ? where scenario_id = ?, cost_head = ?, year = ? ";
	
	public List<FixedOpexCost> getAll(int scenarioId) {

		List<FixedOpexCost> fixedCosts = new ArrayList<FixedOpexCost>();
		
		Object[] values = { scenarioId };

		try(
			Connection connection = DBManager.getConnection();
			PreparedStatement statement = prepareStatement(connection, SQL_LIST_ORDER_BY_ID, false, values);
			ResultSet resultSet = statement.executeQuery();
		){
			while(resultSet.next()){
				fixedCosts.add(map(resultSet));
			}

		} catch(SQLException e){
			e.printStackTrace();
		}

		return fixedCosts;
	}

	public boolean create(FixedOpexCost dump, int scenarioId){

		if (dump.getId() != -1) {
			throw new IllegalArgumentException("Dump is already created.");
		}

		Object[] values = {
				projectId,
				dump.getType(),
				dump.getName(),
				dump.getCondition(),
				dump.getMappedTo(),
				dump.getMappingType(),
				dump.isHasCapacity(),
				dump.getCapacity()
				
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
					dump.setId(generatedKeys.getInt(1));
				} else {
					//throw new DAOException("Creating user failed, no generated key obtained.");
				}
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
		return true;
	}


	public boolean update(Dump dump){

		if (dump.getId() == -1) {
			throw new IllegalArgumentException("Project is not created.");
		}

		Object[] values = {
				dump.getType(),
				dump.getName(),
				dump.getCondition(),
				dump.getMappedTo(),
				dump.getMappingType(),
				dump.isHasCapacity(),
				dump.getCapacity(),
				dump.getId()
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

	public void delete(Dump dump){

		Object[] values = { 
				dump.getId()
		};

		try (
				Connection connection = DBManager.getConnection();
				PreparedStatement statement = prepareStatement(connection, SQL_DELETE, false, values);
				) {
			int affectedRows = statement.executeUpdate();
			if (affectedRows == 0) {
				//throw new DAOException("Deleting user failed, no rows affected.");
			} else {
				dump.setId(-1);
			}
		} catch (SQLException e) {
			//throw new DAOException(e);
		}
	}

	private FixedOpexCost map(ResultSet rs) throws SQLException {
		FixedOpexCost fixedOpexCost = new FixedOpexCost();
		dump.setId(rs.getInt("id"));
		dump.setType(rs.getInt("type"));
		dump.setName(rs.getString("name"));
		dump.setMappingType(rs.getInt("mapping_type"));
		dump.setMappedTo(rs.getString("mapped_to"));
		dump.setCondition(rs.getString("condition_str"));
		dump.setHasCapacity(rs.getBoolean("has_capacity"));
		dump.setCapacity(rs.getInt("capacity"));
		return dump;
	}
}
