package com.org.gnos.db.dao;

import static com.org.gnos.db.dao.util.DAOUtil.prepareStatement;
import static com.org.gnos.db.dao.util.DAOUtil.setValues;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.CapexData;
import com.org.gnos.db.model.CapexInstance;

public class CapexDAO {


	private static final String SQL_LIST_ORDER_BY_ID = "select a.id as id, a.scenario_id, a.name as name, b.id as instance_id, b.name as instance_name, b.capex_id, b.group_name, b.group_type, b.capex, b.expansion_capacity from capex_data a, capex_instance b "
			+ "where b.capex_id = a.id and scenario_id= ? ";
	private static final String SQL_INSERT = "insert into capex_data (scenario_id, name) values (?, ?)";
	private static final String SQL_INSERT_INSTANCE = "insert into capex_instance (capex_id, name, group_name, group_type, capex, expansion_capacity) values (?, ?, ?, ?, ?, ?)";
	private static final String SQL_DELETE = "delete from capex_data where id = ?";
	private static final String SQL_DELETE_INSTANCE = "delete from capex_instance where capex_id = ?";
	private static final String SQL_UPDATE = "update capex_instance set group_name = ? , group_type = ?, capex = ?, expansion_capacity = ?  where id = ?";
	
	public List<CapexData> getAll(int scenarioId) {

		List<CapexData> capexDataList = new ArrayList<CapexData>();
		Map<Integer, CapexData> capexDataMap = new HashMap<Integer, CapexData>();
		Object[] values = { scenarioId };

		try(
			Connection connection = DBManager.getConnection();
			PreparedStatement statement = prepareStatement(connection, SQL_LIST_ORDER_BY_ID, false, values);
			ResultSet resultSet = statement.executeQuery();
		){
			while(resultSet.next()){
				int id = resultSet.getInt("id");
				CapexData cd = capexDataMap.get(id);
				cd = map(resultSet, cd);
				capexDataList.add(cd);
				capexDataMap.put(id, cd);
			}

		} catch(SQLException e){
			e.printStackTrace();
		}

		return capexDataList;
	}

	public boolean create(CapexData capexData, int scenarioId){

		if (capexData.getId() != -1) {
			throw new IllegalArgumentException("Capex is already created.");
		}

		Object[] values = {
				scenarioId,
				capexData.getName()			
		};

		try ( Connection connection = DBManager.getConnection();
				PreparedStatement statement = prepareStatement(connection, SQL_INSERT, true, values);
				PreparedStatement statement1 = connection.prepareStatement(SQL_INSERT_INSTANCE, Statement.RETURN_GENERATED_KEYS)
				){

			int affectedRows = statement.executeUpdate();
			if (affectedRows == 0) {
				//throw new DAOException("Creating user failed, no rows affected.");
			}

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					capexData.setId(generatedKeys.getInt(1));
				} else {
					//throw new DAOException("Creating user failed, no generated key obtained.");
				}
			}
			for (CapexInstance capexInstance: capexData.getListOfCapexInstances()) {
				capexInstance.setCapexId(capexData.getId());
				Object[] civalues = {
						capexData.getId(),
						capexInstance.getName(),
						capexInstance.getGroupingName(),
						capexInstance.getGroupingType(),
						capexInstance.getCapexAmount(),
						capexInstance.getExpansionCapacity()
				};
				setValues(statement1, civalues);
				statement1.executeUpdate();
				try (ResultSet generatedKeys = statement1.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						capexInstance.setId(generatedKeys.getInt(1));
					} else {
						//throw new DAOException("Creating user failed, no generated key obtained.");
					}
				}
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
		return true;
	}


	public boolean update(CapexData capexData){

		if (capexData.getId() == -1) {
			throw new IllegalArgumentException("Capex data is not created.");
		}

		try ( Connection connection = DBManager.getConnection();
				PreparedStatement statement =  connection.prepareStatement(SQL_UPDATE)
				){

			for (CapexInstance capexInstance: capexData.getListOfCapexInstances()) {
				Object[] civalues = {
						
						capexInstance.getName(),
						capexInstance.getGroupingName(),
						capexInstance.getGroupingType(),
						capexInstance.getCapexAmount(),
						capexInstance.getExpansionCapacity(),
						capexInstance.getId(),
				};
				setValues(statement, civalues);
				statement.executeUpdate();
			}

		} catch(SQLException e){
			e.printStackTrace();
		}
		return true;
	}

	public void delete(CapexData capexData){

		Object[] values = { 
				capexData.getId()
		};

		try (
				Connection connection = DBManager.getConnection();
				PreparedStatement statement = prepareStatement(connection, SQL_DELETE, false, values);
				PreparedStatement statement1 = prepareStatement(connection, SQL_DELETE_INSTANCE, false, values);
				) {
			
			statement1.executeUpdate();
			statement.executeUpdate();
			capexData.setId(-1);

		} catch (SQLException e) {
			//throw new DAOException(e);
		}
	}

	private CapexData map(ResultSet rs, CapexData cd) throws SQLException {
		if(cd == null) {
			cd = new CapexData();
			cd.setId(rs.getInt("id"));
			cd.setScenarioId(rs.getInt("scenario_id"));
			cd.setName(rs.getString("name"));
		}
		CapexInstance capexInstance = new CapexInstance();
		capexInstance.setId(rs.getInt("instance_id"));
		capexInstance.setName(rs.getString("instance_name"));
		capexInstance.setCapexId(rs.getInt("capex_id"));
		capexInstance.setGroupingName(rs.getString("group_name"));
		capexInstance.setGroupingType(rs.getInt("group_type"));
		capexInstance.setCapexAmount(rs.getLong("capex"));
		capexInstance.setExpansionCapacity(rs.getLong("expansion_capacity"));
		cd.addCapexInstance(capexInstance);
		return cd;
	}
}
