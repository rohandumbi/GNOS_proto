package com.org.gnos.db.dao;

import static com.org.gnos.db.dao.util.DAOUtil.prepareStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.DumpDependencyData;

public class DumpDependencyDAO {


	private static final String SQL_LIST_ORDER_BY_ID = "select id, in_use, first_pit_name, first_dump_name, dependent_dump_name from dump_dependency_defn where scenario_id = ? order by id asc ";
	private static final String SQL_INSERT = "insert into dump_dependency_defn ( scenario_id , first_pit_name, first_dump_name, dependent_dump_name, in_use) values (?, ?, ?, ?, ?)";
	private static final String SQL_DELETE = "delete from dump_dependency_defn where id = ?";
	private static final String SQL_UPDATE = "update dump_dependency_defn set first_pit_name = ? , first_dump_name = ?, dependent_dump_name = ?, in_use = ? where id = ?";
	
	public List<DumpDependencyData> getAll(int scenarioId) {

		List<DumpDependencyData> dumpDependencyDataList = new ArrayList<DumpDependencyData>();
		
		Object[] values = { scenarioId };

		try(
			Connection connection = DBManager.getConnection();
			PreparedStatement statement = prepareStatement(connection, SQL_LIST_ORDER_BY_ID, false, values);
			ResultSet resultSet = statement.executeQuery();
		){
			while(resultSet.next()){
				dumpDependencyDataList.add(map(resultSet));
			}

		} catch(SQLException e){
			e.printStackTrace();
		}

		return dumpDependencyDataList;
	}

	public boolean create(DumpDependencyData dumpDependencyData, int scenarioId){

		if (dumpDependencyData.getId() != -1) {
			throw new IllegalArgumentException("dumpDependency is already created.");
		}

		Object[] values = {
				scenarioId,
				dumpDependencyData.getFirstPitName(),
				dumpDependencyData.getFirstDumpName(),
				dumpDependencyData.getDependentDumpName(),
				dumpDependencyData.isInUse()
				
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
					dumpDependencyData.setId(generatedKeys.getInt(1));
				} else {
					//throw new DAOException("Creating user failed, no generated key obtained.");
				}
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
		return true;
	}


	public boolean update(DumpDependencyData dumpDependencyData){

		if (dumpDependencyData.getId() == -1) {
			throw new IllegalArgumentException("Project is not created.");
		}

		Object[] values = {
				dumpDependencyData.getFirstPitName(),
				dumpDependencyData.getFirstDumpName(),
				dumpDependencyData.getDependentDumpName(),
				dumpDependencyData.isInUse(),
				dumpDependencyData.getId()
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

	public void delete(DumpDependencyData dumpDependencyData){

		Object[] values = { 
				dumpDependencyData.getId()
		};

		try (
				Connection connection = DBManager.getConnection();
				PreparedStatement statement = prepareStatement(connection, SQL_DELETE, false, values);
				) {
			int affectedRows = statement.executeUpdate();
			if (affectedRows == 0) {
				//throw new DAOException("Deleting user failed, no rows affected.");
			} else {
				dumpDependencyData.setId(-1);
			}
		} catch (SQLException e) {
			//throw new DAOException(e);
		}
	}

	private DumpDependencyData map(ResultSet rs) throws SQLException {
		DumpDependencyData dumpDependencyData = new DumpDependencyData();
		dumpDependencyData.setId(rs.getInt("id"));
		dumpDependencyData.setFirstPitName(rs.getString("first_pit_name"));
		dumpDependencyData.setFirstDumpName(rs.getString("first_dump_name"));
		dumpDependencyData.setDependentDumpName(rs.getString("dependent_dump_name"));
		dumpDependencyData.setInUse(rs.getBoolean("in_use"));

		return dumpDependencyData;
	}
}
