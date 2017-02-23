package com.org.gnos.db.dao;

import static com.org.gnos.db.dao.util.DAOUtil.prepareStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.PitDependencyData;


public class PitDependencyDAO {

	private static final String SQL_LIST_ORDER_BY_ID = "select id, in_use, first_pit_name, first_pit_bench_name, dependent_pit_name, dependent_pit_bench_name, min_lead, max_lead from pit_dependency_defn where scenario_id = ? order by id asc ";
	private static final String SQL_INSERT = "insert into pit_dependency_defn ( scenario_id, first_pit_name, first_pit_bench_name, dependent_pit_name, dependent_pit_bench_name, min_lead, max_lead, in_use) values ( ? , ?, ?, ?, ?, ?, ?, ?)";
	private static final String SQL_DELETE = "delete from pit_dependency_defn where id = ?";
	private static final String SQL_UPDATE = "update pit_dependency_defn set first_pit_name = ? , first_pit_bench_name = ?, dependent_pit_name = ?, dependent_pit_bench_name = ?, min_lead= ?, max_lead = ?, in_use = ? where id = ?";
	
	public List<PitDependencyData> getAll(int scenarioId) {

		List<PitDependencyData> pitDependencyDataList = new ArrayList<PitDependencyData>();
		
		Object[] values = { scenarioId };

		try(
			Connection connection = DBManager.getConnection();
			PreparedStatement statement = prepareStatement(connection, SQL_LIST_ORDER_BY_ID, false, values);
			ResultSet resultSet = statement.executeQuery();
		){
			while(resultSet.next()){
				pitDependencyDataList.add(map(resultSet));
			}

		} catch(SQLException e){
			e.printStackTrace();
		}

		return pitDependencyDataList;
	}

	public boolean create(PitDependencyData pitDependencyData, int scenarioId){

		if (pitDependencyData.getId() != -1) {
			throw new IllegalArgumentException("Dump is already created.");
		}

		Object[] values = {
				scenarioId,
				pitDependencyData.getFirstPitName(),
				pitDependencyData.getFirstPitAssociatedBench(),
				pitDependencyData.getDependentPitName(),
				pitDependencyData.getDependentPitAssociatedBench(),
				pitDependencyData.getMinLead(),
				pitDependencyData.getMaxLead(),
				pitDependencyData.isInUse()
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
					pitDependencyData.setId(generatedKeys.getInt(1));
				} else {
					//throw new DAOException("Creating user failed, no generated key obtained.");
				}
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
		return true;
	}


	public boolean update(PitDependencyData pitDependencyData){

		if (pitDependencyData.getId() == -1) {
			throw new IllegalArgumentException("Project is not created.");
		}

		Object[] values = {
				pitDependencyData.getFirstPitName(),
				pitDependencyData.getFirstPitAssociatedBench(),
				pitDependencyData.getDependentPitName(),
				pitDependencyData.getDependentPitAssociatedBench(),
				pitDependencyData.getMinLead(),
				pitDependencyData.getMaxLead(),
				pitDependencyData.isInUse(),
				pitDependencyData.getId()
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

	public void delete(PitDependencyData pitDependencyData){

		Object[] values = { 
				pitDependencyData.getId()
		};

		try (
				Connection connection = DBManager.getConnection();
				PreparedStatement statement = prepareStatement(connection, SQL_DELETE, false, values);
				) {
			int affectedRows = statement.executeUpdate();
			if (affectedRows == 0) {
				//throw new DAOException("Deleting user failed, no rows affected.");
			} else {
				pitDependencyData.setId(-1);
			}
		} catch (SQLException e) {
			//throw new DAOException(e);
		}
	}

	private PitDependencyData map(ResultSet rs) throws SQLException {
		PitDependencyData pitDependencyData = new PitDependencyData();
		pitDependencyData.setId(rs.getInt("id"));
		pitDependencyData.setFirstPitName(rs.getString("first_pit_name"));
		pitDependencyData.setFirstPitAssociatedBench(rs.getString("first_pit_bench_name"));
		pitDependencyData.setDependentPitName(rs.getString("dependent_pit_name"));
		pitDependencyData.setDependentPitAssociatedBench(rs.getString("dependent_pit_bench_name"));
		pitDependencyData.setMaxLead(rs.getInt("max_lead"));
		pitDependencyData.setMinLead(rs.getInt("min_lead"));
		pitDependencyData.setInUse(rs.getBoolean("in_use"));

		return pitDependencyData;
	}
}
