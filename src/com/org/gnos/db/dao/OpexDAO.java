package com.org.gnos.db.dao;

import static com.org.gnos.db.dao.util.DAOUtil.prepareStatement;
import static com.org.gnos.db.dao.util.DAOUtil.setValues;

import java.math.BigDecimal;
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
import com.org.gnos.db.model.OpexData;

public class OpexDAO {


	private static final String SQL_LIST_ORDER_BY_ID = "select id, model_id, expression_id, in_use, is_revenue, year, value from opex_defn, model_year_mapping where id= opex_id and scenario_id = ? order by id, year asc ";
	private static final String SQL_INSERT_OPEX_DEFN = "insert into opex_defn (scenario_id, model_id, expression_id, in_use, is_revenue) values ( ?, ?, ?, ?, ?)";
	private static final String SQL_INSERT_OPEX_MAPPING = "insert into model_year_mapping (opex_id, year, value) values (?, ?, ?)";
	private static final String SQL_DELETE_OPEX_DEFN = "delete from opex_defn where id = ?";
	private static final String SQL_DELETE_OPEX_MAPPING = "delete from model_year_mapping where opex_id in (select id from  opex_defn where id = ? )";
	private static final String SQL_UPDATE_OPEX_DEFN = "update opex_defn set model_id = ? , expression_id = ?, in_use = ?, is_revenue = ? where id = ?";
	private static final String SQL_UPDATE_OPEX_MAPPING = "update model_year_mapping set value = ?  where opex_id = ?, year = ? ";
	
	public List<OpexData> getAll(int scenarioId) {

		List<OpexData> opexDataList = new ArrayList<OpexData>();
		Map<Integer, OpexData> opexDataMap = new HashMap<Integer, OpexData>();
		Object[] values = { scenarioId };

		try(
			Connection connection = DBManager.getConnection();
			PreparedStatement statement = prepareStatement(connection, SQL_LIST_ORDER_BY_ID, false, values);
			ResultSet resultSet = statement.executeQuery();
		){
			boolean addToList = false;
			while(resultSet.next()){
				int id = resultSet.getInt("id");
				OpexData opexData = opexDataMap.get(id);
				if(opexData == null){
					addToList = true;
				}
				opexData = map(resultSet, opexData);
				opexDataMap.put(id, opexData);
				if(addToList){
					opexDataList.add(opexData);
					addToList = false;
				}
			}

		} catch(SQLException e){
			e.printStackTrace();
		}

		return opexDataList;
	}

	public boolean create(OpexData opexData, int scenarioId){

		if (opexData.getId() != -1) {
			throw new IllegalArgumentException("OpexData is already created.");
		}

		Object[] values = {
				scenarioId,
				opexData.getModelId(),
				opexData.getExpressionId(),
				opexData.isInUse(),
				opexData.isRevenue()
				
		};

		try ( Connection connection = DBManager.getConnection();
				PreparedStatement statement = prepareStatement(connection, SQL_INSERT_OPEX_DEFN, true, values);
				PreparedStatement statement1 = connection.prepareStatement(SQL_INSERT_OPEX_MAPPING);
				){

			int affectedRows = statement.executeUpdate();
			if (affectedRows == 0) {
				//throw new DAOException("Creating user failed, no rows affected.");
			}

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					opexData.setId(generatedKeys.getInt(1));
				} else {
					//throw new DAOException("Creating user failed, no generated key obtained.");
				}
			}
			if(opexData.getId() != -1) {
				Map<Integer, BigDecimal> costData = opexData.getCostData();
				Set<Integer> costYears = costData.keySet();
				for(int costYear: costYears) {
					Object[] costValues = {
							opexData.getId(),
							costYear,
							costData.get(costYear)						
					};
					setValues(statement1, costValues);
					statement1.executeUpdate();
				}
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
		
		return true;
	}


	public boolean update(OpexData opexData){

		if (opexData.getId() == -1) {
			throw new IllegalArgumentException("OpexData is not created.");
		}

		Object[] values = {
				opexData.getModelId(),
				opexData.getExpressionId(),
				opexData.isInUse(),
				opexData.isRevenue(),
				opexData.getId()
		};

		try ( Connection connection = DBManager.getConnection();
				PreparedStatement statement = prepareStatement(connection, SQL_UPDATE_OPEX_DEFN, true, values);
				PreparedStatement statement1 = connection.prepareStatement(SQL_UPDATE_OPEX_MAPPING);
				){

			statement.executeUpdate();        

			if(opexData.getId() != -1) {
				Map<Integer, BigDecimal> costData = opexData.getCostData();
				Set<Integer> costYears = costData.keySet();
				for(int costYear: costYears) {
					Object[] costValues = {
							costData.get(costYear),
							opexData.getId(),
							costYear
					};
					setValues(statement1, costValues);
					statement1.executeUpdate();
				}
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
		return true;
	}

	public void delete(OpexData opexData){

		Object[] values = { 
				opexData.getId()
		};

		try (
				Connection connection = DBManager.getConnection();
				PreparedStatement statement = prepareStatement(connection, SQL_DELETE_OPEX_DEFN, false, values);
				PreparedStatement statement1 = prepareStatement(connection, SQL_DELETE_OPEX_MAPPING, false, values);
				) {
			statement1.executeLargeUpdate();
			int affectedRows = statement.executeUpdate();
			if (affectedRows == 0) {
				//throw new DAOException("Deleting user failed, no rows affected.");
			} else {
				opexData.setId(-1);
			}
		} catch (SQLException e) {
			//throw new DAOException(e);
		}
	}

	private OpexData map(ResultSet rs, OpexData od) throws SQLException {
		if (od == null) {
			od = new OpexData();
			od.setId(rs.getInt("id"));
			od.setModelId(rs.getInt("model_id"));
			od.setExpressionId(rs.getInt("expression_id"));
			od.setInUse(rs.getBoolean(4));
			od.setRevenue(rs.getBoolean(5));
		}
		od.addYear(rs.getInt("year"), rs.getBigDecimal("value"));
	
		return od;
	}
}
