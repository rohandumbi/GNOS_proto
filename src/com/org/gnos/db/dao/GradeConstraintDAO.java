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
import com.org.gnos.db.model.GradeConstraintData;

public class GradeConstraintDAO {

	private static final String SQL_LIST_ORDER_BY_ID = "select a.id, selector_name, selector_type, grade, product_join_name, in_use, is_max, year, value from grade_constraint_defn a, grade_constraint_year_mapping b " +
			" where b.grade_constraint_id = a.id and scenario_id = ? order by id, year";
	private static final String SQL_INSERT = "insert into grade_constraint_defn (scenario_id, selector_name, selector_type, grade, product_join_name,  in_use, is_max) values (?, ?, ?, ?, ?, ?, ?)";
	private static final String SQL_INSERT_MAPPING = "insert into grade_constraint_year_mapping (grade_constraint_id, year, value) values (?, ?, ?)";
	private static final String SQL_DELETE_MAPPING = "delete from grade_constraint_year_mapping where grade_constraint_id = ?";
	private static final String SQL_DELETE = "delete from grade_constraint_defn where id = ?";
	private static final String SQL_UPDATE = "update grade_constraint_defn set selector_name = ?, selector_type = ?, grade = ?, product_join_name = ?, in_use= ?, is_max = ? where id = ?";
	private static final String SQL_UPDATE_MAPPING = "update grade_constraint_year_mapping set value = ? where grade_constraint_id = ? and year = ? ";
	
	public List<GradeConstraintData> getAll(int scenarioId ) {
		
		List<GradeConstraintData> gradeConstraintList = new ArrayList<GradeConstraintData>();
		Map<Integer, GradeConstraintData> gradeConstraintMap = new HashMap<Integer, GradeConstraintData>();
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
				GradeConstraintData gcd = gradeConstraintMap.get(id);
				if(gcd == null) {
					gcd = map(resultSet, gcd);
					gradeConstraintMap.put(id, gcd);
					gradeConstraintList.add(gcd);
				} else {
					map(resultSet, gcd);
				}
				
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		}
		
		return gradeConstraintList;
	}
	
	public boolean create(GradeConstraintData gcd, int scenarioId){
		
		if (gcd.getId() != -1) {
            throw new IllegalArgumentException("ProcessConstraint is already created.");
        }
		Object[] values = {
				scenarioId, 
				gcd.getSelectorName(),
				gcd.getSelectionType(),
				gcd.getSelectedGradeName(),
				gcd.getProductJoinName(),
				gcd.isInUse(),
				gcd.isMax()
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
                    gcd.setId(generatedKeys.getInt(1));                   
                } else {
                    //throw new DAOException("Creating user failed, no generated key obtained.");
                }
            }
            if(gcd.getId() > 0) {
            	Map<Integer, Float> yearValueMapping = gcd.getConstraintData();
            	for(Integer year: yearValueMapping.keySet()){
            		try {
            			Object[] yearvalues = {
            					gcd.getId(), 
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
	
	
	public boolean update(GradeConstraintData gcd){
		
		if (gcd.getId() == -1) {
            throw new IllegalArgumentException("Expression is not created.");
        }
		
		Object[] values = {
				gcd.getSelectorName(),
				gcd.getSelectionType(),
				gcd.getSelectedGradeName(),
				gcd.getProductJoinName(),
				gcd.isInUse(),
				gcd.isMax(),
				gcd.getId()
	   };

		try ( Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_UPDATE, true, values);
				PreparedStatement mappingstatement = prepareStatement(connection, SQL_UPDATE_MAPPING, false);
			){
			
			statement.executeUpdate();        
			if(gcd.getId() > 0) {
            	Map<Integer, Float> yearValueMapping = gcd.getConstraintData();
            	for(Integer year: yearValueMapping.keySet()){
            		try {
            			Object[] yearvalues = {
            					yearValueMapping.get(year),
            					gcd.getId(), 
            					year    					
            		   };
            			setValues(mappingstatement, yearvalues);
            			int affectedRows = mappingstatement.executeUpdate();
            			if(affectedRows == 0){
            				Object[] newValues = {
            						gcd.getId(),
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
	
	public void delete(GradeConstraintData gcd){
		
		Object[] values = { 
				gcd.getId()
	        };

	        try (
	            Connection connection = DBManager.getConnection();
	            PreparedStatement statement = prepareStatement(connection, SQL_DELETE, false, values);
	        	PreparedStatement mappingstatement = prepareStatement(connection, SQL_DELETE_MAPPING, false, values);
	        ) {
	        	mappingstatement.executeUpdate();
	        	statement.executeUpdate();	           
	        	gcd.setId(-1);
	        } catch (SQLException e) {
	            //throw new DAOException(e);
	        }
	}
	
	private GradeConstraintData map(ResultSet rs, GradeConstraintData gcd) throws SQLException {
		
		if(gcd == null){
			gcd = new GradeConstraintData();
			gcd.setId(rs.getInt("id"));
			gcd.setProductJoinName(rs.getString("product_join_name"));
			gcd.setSelectedGradeName(rs.getString("grade"));
			gcd.setSelectorName(rs.getString("selector_name"));
			gcd.setSelectionType(rs.getInt("selector_type"));
			gcd.setInUse(rs.getBoolean("in_use"));
			gcd.setMax(rs.getBoolean("is_max"));
		}
		
		gcd.addYear(rs.getInt("year"), rs.getFloat("value"));


		return gcd;
	}
	
}
