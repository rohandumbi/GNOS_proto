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
import com.org.gnos.db.model.PitBenchConstraintData;

public class BenchConstraintDAO {

	private static final String SQL_LIST_ORDER_BY_ID = "select a.id, pit_name, in_use, year, value from bench_constraint_defn a, bench_constraint_year_mapping b where b.bench_constraint_id = a.id and scenario_id = ? order by id, year";
	private static final String SQL_INSERT = "insert into bench_constraint_defn (scenario_id, pit_name, in_use ) values (?, ?, ?)";
	private static final String SQL_INSERT_MAPPING = "insert into bench_constraint_year_mapping (bench_constraint_id, year, value) values (?, ?, ?)";
	private static final String SQL_DELETE_MAPPING = "delete from bench_constraint_year_mapping where bench_constraint_id = ?";
	private static final String SQL_DELETE = "delete from bench_constraint_defn where id = ?";
	private static final String SQL_DELETE_MAPPING_BY_SCENARIONID = "delete from bench_constraint_year_mapping where bench_constraint_id in ( select id from bench_constraint_defn where scenario_id = ? )";
	private static final String SQL_DELETE_BY_SCENARIONID = "delete from bench_constraint_defn where scenario_id = ?";
	private static final String SQL_UPDATE = "update bench_constraint_defn set pit_name = ?, in_use= ? where id = ?";
	private static final String SQL_UPDATE_MAPPING = "update bench_constraint_year_mapping set value = ? where bench_constraint_id = ? and year = ? ";
	
	public List<PitBenchConstraintData> getAll(int scenarioId) {
		
		List<PitBenchConstraintData> benchConstraintList = new ArrayList<PitBenchConstraintData>();
		Map<Integer, PitBenchConstraintData> benchConstraintMap = new HashMap<Integer, PitBenchConstraintData>();
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
				PitBenchConstraintData pcd = benchConstraintMap.get(id);
				if(pcd == null) {
					pcd = map(resultSet, pcd);
					benchConstraintMap.put(id, pcd);
					benchConstraintList.add(pcd);
				} else {
					pcd = map(resultSet, pcd);
				}				
			}			
		} catch(SQLException e){
			e.printStackTrace();
		}
		
		return benchConstraintList;
	}
	
	public boolean create(PitBenchConstraintData pcd, int scenarioId){
		
		if (pcd.getId() != -1) {
            throw new IllegalArgumentException("ProcessConstraint is already created.");
        }

		Object[] values = {
				scenarioId, 
				pcd.getPitName(),
				pcd.isInUse()
	   };
		Connection connection =  DBManager.getConnection();

		try (
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
			DBManager.releaseConnection(connection);
		}
		return true;
	}
	
	
	public boolean update(PitBenchConstraintData pcd){
		
		if (pcd.getId() == -1) {
            throw new IllegalArgumentException("Expression is not created.");
        }
		
		Object[] values = {
				pcd.getPitName(),
				pcd.isInUse(),
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
	
	public void delete(PitBenchConstraintData pcd){
		
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
	
	
	public void delete(int scenarioId){
		
		Object[] values = { scenarioId };

	        try (
	            Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_DELETE_BY_SCENARIONID, false, values);
	        	PreparedStatement mappingstatement = prepareStatement(connection, SQL_DELETE_MAPPING_BY_SCENARIONID, false, values);
	        ) {
	        	mappingstatement.executeUpdate();
	        	statement.executeUpdate();	           
	        } catch (SQLException e) {
	            //throw new DAOException(e);
	        }
	}
	
	private PitBenchConstraintData map(ResultSet rs, PitBenchConstraintData pcd) throws SQLException {
		
		if(pcd == null){
			pcd = new PitBenchConstraintData();
			pcd.setId(rs.getInt("id"));
			pcd.setPitName(rs.getString("pit_name"));
			pcd.setInUse(rs.getBoolean("in_use"));			
		}	
		pcd.addYear(rs.getInt("year"), rs.getFloat("value"));	
		
		return pcd;
	}
	
}
