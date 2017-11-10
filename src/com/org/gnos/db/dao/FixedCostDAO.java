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

import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.FixedOpexCost;

public class FixedCostDAO {

	private static final String SQL_LIST_ORDER_BY_ID = "select a.id, cost_type, selector_name, selector_type, in_use, is_default, year, value from fixedcost_defn a left join fixedcost_year_mapping b " +
			" on b.fixedcost_id = a.id where scenario_id = ? order by id, year";
	private static final String SQL_INSERT = "insert into fixedcost_defn (scenario_id, cost_type, selector_name, selector_type, in_use, is_default) values (?, ?, ?, ?, ?, ?)";
	private static final String SQL_INSERT_MAPPING = "insert into fixedcost_year_mapping (fixedcost_id, year, value) values (?, ?, ?)";
	private static final String SQL_DELETE_BY_ID = "delete from fixedcost_defn where id = ?";
	private static final String SQL_DELETE_MAPPING_BY_ID = "delete from fixedcost_year_mapping where fixedcost_id = ?";
	private static final String SQL_DELETE_MAPPING_BY_ID_YEAR = "delete from fixedcost_year_mapping where fixedcost_id = ? and year > ? ";
	private static final String SQL_DELETE_BY_SCENARIOID = "delete from fixedcost_defn where scenario_id = ?";
	private static final String SQL_DELETE_MAPPING_BY_SCENARIOID = "delete from fixedcost_year_mapping where fixedcost_id in (select id from fixedcost_defn where scenario_id = ? )";
	private static final String SQL_UPDATE = "update fixedcost_defn set cost_type= ?, selector_name = ?, selector_type = ?, in_use= ?, is_default = ? where id = ?";
	private static final String SQL_UPDATE_MAPPING = "update fixedcost_year_mapping set value = ? where fixedcost_id = ? and year = ? ";
	
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
				int costType = resultSet.getInt("cost_type");
				FixedOpexCost fixedOpexCost = fixedCostMap.get(costType);
				if(fixedOpexCost == null){
					fixedOpexCost = map(resultSet, fixedOpexCost);
					fixedCostMap.put(costType, fixedOpexCost);
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

	public boolean create(FixedOpexCost foc, int scenarioId){


		if (foc.getId() != -1) {
            throw new IllegalArgumentException("Fixed Cost is already created.");
        }
		Object[] values = {
				scenarioId, 
				foc.getCostType(),
				foc.getSelectorName(),
				foc.getSelectionType(),
				foc.isInUse(),
				foc.isDefault()
	   };

		try ( Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_INSERT, true, values);
				PreparedStatement mappingstatement = prepareStatement(connection, SQL_INSERT_MAPPING, false);
			){
			
			int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                //throw new DAOException("Creating user failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    foc.setId(generatedKeys.getInt(1));                   
                } else {
                    //throw new DAOException("Creating user failed, no generated key obtained.");
                }
            }
            if(foc.getId() > 0) {
            	Map<Integer, BigDecimal> yearValueMapping = foc.getCostData();
            	for(Integer year: yearValueMapping.keySet()){
            		try {
            			Object[] yearvalues = {
            					foc.getId(), 
            					year,
            					yearValueMapping.get(year)
            		   };
            			setValues(mappingstatement, yearvalues);
            			mappingstatement.executeUpdate();
            		} catch(SQLException e){
            			e.printStackTrace();
            		}
            	}
            }
		} catch(SQLException e){
			e.printStackTrace();
		}
		
		return true;
	}

	public boolean addYears(int id, int startYear, int endYear){

		try ( Connection connection = DBManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_INSERT_MAPPING);
				){

			if(id != -1) {
				for(int i=startYear; i<= endYear; i++) {
					Object[] costValues = { id, i, 0 };
					setValues(statement, costValues);
					statement.executeUpdate();
				}
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
		return true;
	}

	public boolean update(FixedOpexCost foc, int scenarioId){

		if (foc.getId() == -1) {
            throw new IllegalArgumentException("Expression is not created.");
        }
		
		Object[] values = {
				foc.getCostType(),
				foc.getSelectorName(),
				foc.getSelectionType(),
				foc.isInUse(),
				foc.isDefault(),
				foc.getId()
	   };

		try ( Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_UPDATE, true, values);
				PreparedStatement mappingstatement = prepareStatement(connection, SQL_UPDATE_MAPPING, false);
			){
			
			statement.executeUpdate();        
			if(foc.getId() > 0) {
            	Map<Integer, BigDecimal> yearValueMapping = foc.getCostData();
            	for(Integer year: yearValueMapping.keySet()){
            		try {
            			Object[] yearvalues = {
            					yearValueMapping.get(year),
            					foc.getId(), 
            					year    					
            		   };
            			setValues(mappingstatement, yearvalues);
            			int affectedRows = mappingstatement.executeUpdate();
            			if(affectedRows == 0){
            				Object[] newValues = {
            						foc.getId(),
            						year,
            						yearValueMapping.get(year)
            			   };
            				PreparedStatement pstmt = prepareStatement(connection, SQL_INSERT_MAPPING, false, newValues);
            				pstmt.executeUpdate();
            				pstmt.close();
            			}
            		} catch(SQLException e){
            			e.printStackTrace();
            		}
            	}
            }
		} catch(SQLException e){
			e.printStackTrace();
		}
		return true;
	}

	public void delete(FixedOpexCost foc){
		
		Object[] values = { 
				foc.getId()
	        };

	        try (
	            Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_DELETE_BY_ID, false, values);
	        	PreparedStatement mappingstatement = prepareStatement(connection, SQL_DELETE_MAPPING_BY_ID, false, values);
	        ) {
	        	mappingstatement.executeUpdate();
	        	statement.executeUpdate();	           
	            foc.setId(-1);
	        } catch (SQLException e) {
	            //throw new DAOException(e);
	        }
	}
	
	public void delete(int scenarioId){

		Object[] values = { scenarioId };

		try (
				Connection connection = DBManager.getConnection();
				PreparedStatement statement = prepareStatement(connection, SQL_DELETE_BY_SCENARIOID, false, values);
				PreparedStatement mappingstatement = prepareStatement(connection, SQL_DELETE_MAPPING_BY_SCENARIOID, false, values);
				) {
			mappingstatement.executeUpdate();
			statement.executeUpdate();
			
		} catch (SQLException e) {
			//throw new DAOException(e);
		}
	}

	public boolean deleteYears(int scenarioId, int endYear){

		Object[] values = {
				scenarioId,
				endYear
		};
		try ( Connection connection = DBManager.getConnection();
				PreparedStatement statement = prepareStatement(connection, SQL_DELETE_MAPPING_BY_ID_YEAR, true, values);
				){

				statement.executeUpdate();
		} catch(SQLException e){
			e.printStackTrace();
		}
		
		return true;
	}
	
	private FixedOpexCost map(ResultSet rs, FixedOpexCost foc) throws SQLException {
		if (foc == null) {
			foc = new FixedOpexCost();
			foc.setCostType(rs.getInt("cost_type"));
			foc.setSelectorName(rs.getString("selector_name"));
			foc.setSelectionType(rs.getInt("selector_type"));
			foc.setInUse(rs.getBoolean("in_use"));
			foc.setDefault(rs.getBoolean("is_default"));
		}
		foc.addCostData(rs.getInt("year"), rs.getBigDecimal("value"));
		
		return foc;
	}
}
