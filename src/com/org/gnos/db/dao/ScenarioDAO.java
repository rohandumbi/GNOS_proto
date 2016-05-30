package com.org.gnos.db.dao;

import static com.org.gnos.db.dao.util.DAOUtil.prepareStatement;
import static com.org.gnos.db.dao.util.DAOUtil.toSqlTimeStamp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.Scenario;


public class ScenarioDAO {

	private static final String SQL_GET_BY_ID = "select id, name, start_year, time_period, discount from  scenario where id = ? ";
	private static final String SQL_LIST_ORDER_BY_MODIFIED_DATE = "select id, name, project_id, start_year, time_period, discount from  scenario where project_id = ?";
	private static final String SQL_INSERT = "insert into scenario (project_id, name, start_year, time_period, discount) values (?, ?, ?, ?, ?)";
	private static final String SQL_DELETE = "delete from scenario where id = ?";
	private static final String SQL_UPDATE = "update scenario set start_year = ?, time_period = ?, discount = ? where id = ?";
	ProjectConfigutration projectConfiguration = null;

	public List<Scenario> getAll() {

		List<Scenario> scenarios = new ArrayList<Scenario>();
		this.projectConfiguration = ProjectConfigutration.getInstance();
		Object[] values = { 
				this.projectConfiguration.getProjectId()
		};

		try(
			Connection connection = DBManager.getConnection();
			PreparedStatement statement = prepareStatement(connection, SQL_LIST_ORDER_BY_MODIFIED_DATE, false, values);
			ResultSet resultSet = statement.executeQuery();
		){
			while(resultSet.next()){
				scenarios.add(map(resultSet));				
			}

		} catch(SQLException e){
			e.printStackTrace();
		}

		return scenarios;
	}

	public Scenario get(int id) {

		Scenario scenario = null;
		Object[] values = { 
				id
		};
		try (
				Connection connection = DBManager.getConnection();
				PreparedStatement statement = prepareStatement(connection, SQL_GET_BY_ID, false, values);
				ResultSet resultSet = statement.executeQuery();
				){
			while(resultSet.next()){
				scenario = map(resultSet);				
			}

		} catch(SQLException e){
			e.printStackTrace();
		}

		return scenario;
	}

	public boolean create(Scenario scenario){

		if (scenario.getId() != -1) {
			throw new IllegalArgumentException("Scenario is already created.");
		}
		this.projectConfiguration = ProjectConfigutration.getInstance();

		Object[] values = {
				
				this.projectConfiguration.getProjectId(),
				scenario.getName(),
				scenario.getStartYear(),
				scenario.getTimePeriod(),
				scenario.getDiscount()
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
					scenario.setId(generatedKeys.getInt(1));
				} else {
					//throw new DAOException("Creating user failed, no generated key obtained.");
				}
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
		return true;
	}


	public boolean update(Scenario scenario){

		if (scenario.getId() == -1) {
			throw new IllegalArgumentException("Project is not created.");
		}

		Object[] values = {
				scenario.getName(),
				scenario.getStartYear(),
				scenario.getTimePeriod(),
				scenario.getDiscount()
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

	public void delete(Scenario scenario){

		Object[] values = { 
				scenario.getId()
		};

		try (
				Connection connection = DBManager.getConnection();
				PreparedStatement statement = prepareStatement(connection, SQL_DELETE, false, values);
				) {
			int affectedRows = statement.executeUpdate();
			if (affectedRows == 0) {
				//throw new DAOException("Deleting user failed, no rows affected.");
			} else {
				scenario.setId(-1);
			}
		} catch (SQLException e) {
			//throw new DAOException(e);
		}
	}

	private Scenario map(ResultSet rs) throws SQLException {
		Scenario scenario = new Scenario();
		scenario.setId(rs.getInt("id"));
		scenario.setName(rs.getString("name"));
		scenario.setStartYear(rs.getInt("start_year"));
		scenario.setTimePeriod(rs.getInt("time_period"));
		scenario.setDiscount(rs.getFloat("discount"));
		return scenario;
	}
}
