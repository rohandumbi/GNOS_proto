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

import com.org.gnos.core.ScenarioConfigutration;
import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.ProcessConstraintData;

public class ProcessConstraintDAO {

	private static final String SQL_LIST_ORDER_BY_ID = "select a.id, selector_name, selector_type, coefficient_name, coefficient_type, in_use, is_max, year, value from process_constraint_defn a, process_constraint_year_mapping b " +
			" where b.process_constraint_id = a.id and scenario_id = ? order by id, year";
	private static final String SQL_INSERT = "insert into process_constraint_defn (scenario_id, selector_name, selector_type, coefficient_name, coefficient_type,  in_use, is_max) values (?, ?, ?, ?, ?, ?, ?)";
	private static final String SQL_INSERT_MAPPING = "insert into process_constraint_year_mapping (process_constraint_id, year, value) values (?, ?, ?)";
	private static final String SQL_DELETE_MAPPING = "delete from process_constraint_year_mapping where process_constraint_id = ?";
	private static final String SQL_DELETE = "delete from process_constraint_defn where id = ?";
	private static final String SQL_UPDATE = "update process_constraint_defn set selector_name = ?, selector_type = ?, coefficient_name = ?, coefficient_type = ?, in_use= ?, is_max = ? where id = ?";
	private static final String SQL_UPDATE_MAPPING = "update process_constraint_year_mapping set value = ? where process_constraint_id = ? and year = ? ";
	
	public List<ProcessConstraintData> getAll(int scenarioId) {
		
		List<ProcessConstraintData> processConstraintList = new ArrayList<ProcessConstraintData>();
		Map<Integer, ProcessConstraintData> procesConstraintMap = new HashMap<Integer, ProcessConstraintData>();
		Object[] values = {
				scenarioId, 
		};		
		try (
	            Connection connection = DBManager.getConnection();
				PreparedStatement statement = prepareStatement(connection, SQL_LIST_ORDER_BY_ID, false, values);
	            ResultSet resultSet = statement.executeQuery();
	        ){
			while(resultSet.next()){
				int id = resultSet.getInt("id");
				ProcessConstraintData pcd = procesConstraintMap.get(id);
				pcd = map(resultSet, pcd);
				procesConstraintMap.put(id, pcd);
				processConstraintList.add(pcd);
						
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		}
		
		return processConstraintList;
	}
	
	public boolean create(ProcessConstraintData pcd, int scenarioId){
		
		if (pcd.getId() != -1) {
            throw new IllegalArgumentException("ProcessConstraint is already created.");
        }
		Object[] values = {
				scenarioId, 
				pcd.getSelector_name(),
				pcd.getSelectionType(),
				pcd.getCoefficient_name(),
				pcd.getCoefficientType(),
				pcd.isInUse(),
				pcd.isMax()
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
                    pcd.setId(generatedKeys.getInt(1));                   
                } else {
                    //throw new DAOException("Creating user failed, no generated key obtained.");
                }
            }
            if(pcd.getId() > 0) {
            	Map<Integer, Float> yearValueMapping = pcd.getConstraintData();
            	for(Integer year: yearValueMapping.keySet()){
            		try {
            			Object[] yearvalues = {
            					pcd.getId(), 
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
	
	public boolean update(ProcessConstraintData pcd){
		
		if (pcd.getId() == -1) {
            throw new IllegalArgumentException("Expression is not created.");
        }
		
		Object[] values = {
				pcd.getSelector_name(),
				pcd.getSelectionType(),
				pcd.getCoefficient_name(),
				pcd.getCoefficientType(),
				pcd.isInUse(),
				pcd.isMax(),
				pcd.getId()
	   };

		try ( Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_UPDATE, true, values);
				PreparedStatement mappingstatement = prepareStatement(connection, SQL_UPDATE_MAPPING, false);
			){
			
			statement.executeUpdate();        
			if(pcd.getId() > 0) {
            	Map<Integer, Float> yearValueMapping = pcd.getConstraintData();
            	for(Integer year: yearValueMapping.keySet()){
            		try {
            			Object[] yearvalues = {
            					yearValueMapping.get(year),
            					pcd.getId(), 
            					year    					
            		   };
            			setValues(mappingstatement, yearvalues);
            			int affectedRows = mappingstatement.executeUpdate();
            			if(affectedRows == 0){
            				Object[] newValues = {
            						pcd.getId(),
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
	
	public void delete(ProcessConstraintData pcd){
		
		Object[] values = { 
				pcd.getId()
	        };

	        try (
	            Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_DELETE, false, values);
	        	PreparedStatement mappingstatement = prepareStatement(connection, SQL_DELETE_MAPPING, false, values);
	        ) {
	        	mappingstatement.executeUpdate();
	        	statement.executeUpdate();	           
	            pcd.setId(-1);
	        } catch (SQLException e) {
	            //throw new DAOException(e);
	        }
	}
	
	private ProcessConstraintData map(ResultSet rs, ProcessConstraintData pcd) throws SQLException {
		
		if(pcd == null) {
			pcd = new ProcessConstraintData();
			pcd.setId(rs.getInt("id"));
			pcd.setCoefficient_name(rs.getString("coefficient_name"));
			pcd.setCoefficientType(rs.getInt("coefficient_type"));
			pcd.setSelector_name(rs.getString("selector_name"));
			pcd.setSelectionType(rs.getInt("selector_type"));
			pcd.setInUse(rs.getBoolean("in_use"));
			pcd.setMax(rs.getBoolean("is_max"));
		}
		
		pcd.addYear(rs.getInt("year"), rs.getFloat("value"));

		return pcd;
	}
	
}
