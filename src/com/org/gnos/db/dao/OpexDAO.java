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


	private static final String SQL_LIST_ORDER_BY_ID = "select id, model_id, product_join_name, unit_type, unit_id, in_use, is_revenue, year, value from opex_defn, model_year_mapping where id= opex_id and scenario_id = ? order by id, year asc ";
	private static final String SQL_INSERT_OPEX_DEFN = "insert into opex_defn (scenario_id, model_id, product_join_name, unit_type, unit_id, in_use, is_revenue) values ( ?, ?, ?, ?, ?, ?, ?)";
	private static final String SQL_INSERT_OPEX_MAPPING = "insert into model_year_mapping (opex_id, year, value) values (?, ?, ?)";
	private static final String SQL_DELETE_OPEX_DEFN = "delete from opex_defn where id = ?";
	private static final String SQL_DELETE_OPEX_MAPPING = "delete from model_year_mapping where opex_id  = ? ";
	private static final String SQL_DELETE_OPEX_MAPPING_YEAR = "delete from model_year_mapping where opex_id  = ? and year > ?";
	private static final String SQL_DELETE_OPEX_DEFN_BY_SCENARIOID = "delete from opex_defn where scenario_id = ?";
	private static final String SQL_DELETE_OPEX_MAPPING_BY_SCENARIOID = "delete from model_year_mapping where opex_id in (select id from  opex_defn where scenario_id = ? )";
	private static final String SQL_UPDATE_OPEX_DEFN = "update opex_defn set model_id = ? , product_join_name = ? , unit_type = ? , unit_id = ?, in_use = ?, is_revenue = ? where id = ?";
	private static final String SQL_UPDATE_OPEX_MAPPING = "update model_year_mapping set value = ?  where opex_id = ? and year = ? ";
	
	public List<OpexData> getAll(int scenarioId) {

		List<OpexData> opexDataList = new ArrayList<OpexData>();
		Map<Integer, OpexData> opexDataMap = new HashMap<Integer, OpexData>();
		Object[] values = { scenarioId };

		try(
			Connection connection = DBManager.getConnection();
			PreparedStatement statement = prepareStatement(connection, SQL_LIST_ORDER_BY_ID, false, values);
			ResultSet resultSet = statement.executeQuery();
		){
			while(resultSet.next()){
				int id = resultSet.getInt("id");
				OpexData opexData = opexDataMap.get(id);
				if(opexData == null){
					opexData = map(resultSet, opexData);
					opexDataMap.put(id, opexData);
					opexDataList.add(opexData);
				} else {
					map(resultSet, opexData);
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
		int unitId;
		if(opexData.getUnitType() == OpexData.UNIT_FIELD) {
			unitId = opexData.getFieldId();
		} else {
			unitId = opexData.getExpressionId();
		}
		Object[] values = {
				scenarioId,
				opexData.getModelId(),
				opexData.getProductJoinName(),
				opexData.getUnitType(),
				unitId,
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


	public boolean addYears(int opexId, int startYear, int endYear){

		try ( Connection connection = DBManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_INSERT_OPEX_MAPPING);
				){

			if(opexId != -1) {
				for(int i=startYear; i<= endYear; i++) {
					Object[] costValues = { opexId, i, 0 };
					setValues(statement, costValues);
					statement.executeUpdate();
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
		int unitId;
		if(opexData.getUnitType() == OpexData.UNIT_FIELD) {
			unitId = opexData.getFieldId();
		} else {
			unitId = opexData.getExpressionId();
		}
		Object[] values = {
				opexData.getModelId(),
				opexData.getProductJoinName(),
				opexData.getUnitType(),
				unitId,
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

	public boolean deleteYears(int opexId, int endYear){

		Object[] values = {
				opexId,
				endYear
		};
		try ( Connection connection = DBManager.getConnection();
				PreparedStatement statement = prepareStatement(connection, SQL_DELETE_OPEX_MAPPING_YEAR, true, values);
				){

				statement.executeUpdate();
		} catch(SQLException e){
			e.printStackTrace();
		}
		
		return true;
	}
	
	public void delete(int scenarioId){

		Object[] values = { scenarioId };

		try (
				Connection connection = DBManager.getConnection();
				PreparedStatement statement = prepareStatement(connection, SQL_DELETE_OPEX_DEFN_BY_SCENARIOID, false, values);
				PreparedStatement statement1 = prepareStatement(connection, SQL_DELETE_OPEX_MAPPING_BY_SCENARIOID, false, values);
				) {
			statement1.executeLargeUpdate();
			statement.executeUpdate();
		} catch (SQLException e) {
			//throw new DAOException(e);
		}
	}
	
	private OpexData map(ResultSet rs, OpexData od) throws SQLException {
		if (od == null) {
			od = new OpexData();
			od.setId(rs.getInt("id"));
			od.setModelId(rs.getInt("model_id"));
			od.setProductJoinName(rs.getString("product_join_name"));
			od.setInUse(rs.getBoolean("in_use"));
			od.setRevenue(rs.getBoolean("is_revenue"));
			
			short unitType = rs.getShort("unit_type");
			od.setUnitType(unitType);
			if(unitType == OpexData.UNIT_FIELD) {
				od.setFieldId(rs.getInt("unit_id"));
			} else {
				od.setExpressionId(rs.getInt("unit_id"));
			}
			
		}
		od.addYear(rs.getInt("year"), rs.getBigDecimal("value"));
	
		return od;
	}
}
