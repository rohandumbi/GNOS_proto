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
import com.org.gnos.db.model.FixedOpexCost;

public class FixedCostDAO {

	private static final String SQL_LIST_ORDER_BY_ID = "select cost_head, year, value from fixedcost_year_mapping where scenario_id = ? order by cost_head asc";
	private static final String SQL_INSERT = "insert into fixedcost_year_mapping (scenario_id, cost_head, year, value) values (?, ?, ?, ?)";
	private static final String SQL_DELETE = "delete from fixedcost_year_mapping where scenario_id = ?";
	private static final String SQL_UPDATE = "update fixedcost_year_mapping set value = ? where scenario_id = ? and cost_head = ? and year = ? ";
	
	public List<FixedOpexCost> getAll(int scenarioId) {

		List<FixedOpexCost> fixedCosts = new ArrayList<FixedOpexCost>();
		Map<Integer, FixedOpexCost> fixedCostMap = new HashMap<Integer, FixedOpexCost>();
		
		Object[] values = { scenarioId };

		try(
			Connection connection = DBManager.getConnection();
			PreparedStatement statement = prepareStatement(connection, SQL_LIST_ORDER_BY_ID, false, values);
			ResultSet resultSet = statement.executeQuery();
		){
			while(resultSet.next()){
				int costHead = resultSet.getInt("cost_head");
				FixedOpexCost fixedOpexCost = fixedCostMap.get(costHead);
				if(fixedOpexCost == null){
					fixedOpexCost = map(resultSet, fixedOpexCost);
					fixedCostMap.put(costHead, fixedOpexCost);
					fixedCosts.add(fixedOpexCost);
				} else {
					map(resultSet, fixedOpexCost);
				}
			}

		} catch(SQLException e){
			e.printStackTrace();
		}

		return fixedCosts;
	}

	public boolean create(FixedOpexCost fixedOpexCost, int scenarioId){


		try ( Connection connection = DBManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_INSERT);
				){

			Map<Integer, BigDecimal> costData = fixedOpexCost.getCostData();
			Set<Integer> costYears = costData.keySet();
			for(int costYear: costYears) {
				Object[] values = {
						scenarioId,
						fixedOpexCost.getCostHead(),
						costYear,
						costData.get(costYear)
				};
				setValues(statement, values);
				statement.executeUpdate();
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
		return true;
	}


	public boolean update(FixedOpexCost fixedOpexCost, int scenarioId){

		try ( Connection connection = DBManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_UPDATE);
				){
			Map<Integer, BigDecimal> costData = fixedOpexCost.getCostData();
			Set<Integer> costYears = costData.keySet();
			for(int costYear: costYears) {
				Object[] values = {
						costData.get(costYear),
						scenarioId,
						fixedOpexCost.getCostHead(),
						costYear
				};
				setValues(statement, values);
				statement.executeUpdate();
			}
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
				PreparedStatement statement = prepareStatement(connection, SQL_DELETE, false, values);
				) {
			statement.executeUpdate();
			
		} catch (SQLException e) {
			//throw new DAOException(e);
		}
	}

	private FixedOpexCost map(ResultSet rs, FixedOpexCost fixedOpexCost) throws SQLException {
		if (fixedOpexCost == null) {
			fixedOpexCost = new FixedOpexCost();
			fixedOpexCost.setCostHead(rs.getInt("cost_head"));
		}
		fixedOpexCost.addCostData(rs.getInt("year"), rs.getBigDecimal("value"));
		
		return fixedOpexCost;
	}
}
