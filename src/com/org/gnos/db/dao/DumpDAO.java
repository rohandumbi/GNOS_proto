package com.org.gnos.db.dao;

import static com.org.gnos.db.dao.util.DAOUtil.prepareStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.Dump;


public class DumpDAO {

	private static final String SQL_LIST_ORDER_BY_ID = "select id, type, name, condition_str, mapped_to, mapping_type, has_capacity, capacity from dump where project_id = ? order by id asc ";
	private static final String SQL_INSERT = "insert into dump ( project_id , type, name, condition_str, mapped_to, mapping_type, has_capacity, capacity) values ( ? , ?, ?, ?, ?, ?, ?, ?)";
	private static final String SQL_DELETE = "delete from dump where id = ?";
	private static final String SQL_UPDATE = "update dump set type = ? , name = ?, condition_str = ?, mapped_to = ?, mapping_type= ?, has_capacity = ?, capacity = ? where id = ?";
	
	public List<Dump> getAll(int projectId) {

		List<Dump> dumps = new ArrayList<Dump>();
		
		Object[] values = { projectId };

		try(
			Connection connection = DBManager.getConnection();
			PreparedStatement statement = prepareStatement(connection, SQL_LIST_ORDER_BY_ID, false, values);
			ResultSet resultSet = statement.executeQuery();
		){
			int count = 1;
			while(resultSet.next()){
				Dump dump = map(resultSet);
				dump.setDumpNumber(count);
				dumps.add(dump);
				count++;
			}

		} catch(SQLException e){
			e.printStackTrace();
		}

		return dumps;
	}

	public boolean create(Dump dump, int projectId){

		if (dump.getId() != -1) {
			throw new IllegalArgumentException("Dump is already created.");
		}

		Object[] values = {
				projectId,
				dump.getType(),
				dump.getName(),
				dump.getCondition(),
				dump.getMappedTo(),
				dump.getMappingType(),
				dump.isHasCapacity(),
				dump.getCapacity()
				
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
					dump.setId(generatedKeys.getInt(1));
				} else {
					//throw new DAOException("Creating user failed, no generated key obtained.");
				}
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
		return true;
	}


	public boolean update(Dump dump){

		if (dump.getId() == -1) {
			throw new IllegalArgumentException("Project is not created.");
		}

		Object[] values = {
				dump.getType(),
				dump.getName(),
				dump.getCondition(),
				dump.getMappedTo(),
				dump.getMappingType(),
				dump.isHasCapacity(),
				dump.getCapacity(),
				dump.getId()
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

	public void delete(Dump dump){

		Object[] values = { 
				dump.getId()
		};

		try (
				Connection connection = DBManager.getConnection();
				PreparedStatement statement = prepareStatement(connection, SQL_DELETE, false, values);
				) {
			int affectedRows = statement.executeUpdate();
			if (affectedRows == 0) {
				//throw new DAOException("Deleting user failed, no rows affected.");
			} else {
				dump.setId(-1);
			}
		} catch (SQLException e) {
			//throw new DAOException(e);
		}
	}

	private Dump map(ResultSet rs) throws SQLException {
		Dump dump = new Dump();
		dump.setId(rs.getInt("id"));
		dump.setType(rs.getInt("type"));
		dump.setName(rs.getString("name"));
		dump.setMappingType(rs.getInt("mapping_type"));
		dump.setMappedTo(rs.getString("mapped_to"));
		dump.setCondition(rs.getString("condition_str"));
		dump.setHasCapacity(rs.getBoolean("has_capacity"));
		dump.setCapacity(rs.getInt("capacity"));
		return dump;
	}
}
