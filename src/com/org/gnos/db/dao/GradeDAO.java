package com.org.gnos.db.dao;

import static com.org.gnos.db.dao.util.DAOUtil.prepareStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.Grade;

public class GradeDAO {

	private static final String SQL_LIST_ORDER_BY_ID = "select id, name, product_name, type, mapped_name, value from grade where project_id = ? ";
	private static final String SQL_INSERT = "insert into grade (project_id, name, product_name, type, mapped_name, value) values (?, ?, ?, ?, ?, ?)";
	private static final String SQL_DELETE = "delete from grade where id  = ?";

	public List<Grade> getAll(int projectId) {

		List<Grade> gradeList = new ArrayList<Grade>();

		Object[] values = { projectId };

		try(
			Connection connection = DBManager.getConnection();
			PreparedStatement statement = prepareStatement(connection, SQL_LIST_ORDER_BY_ID, false, values);
			ResultSet resultSet = statement.executeQuery();
		){
			while(resultSet.next()){
				gradeList.add(map(resultSet));
			}

		} catch(SQLException e){
			e.printStackTrace();
		}

		return gradeList;
	}

	public boolean create(Grade grade, int projectId){

		Object[] values = {
				projectId,
				grade.getName(),
				grade.getProductName(),
				grade.getType(),
				grade.getMappedName()
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
                    grade.setId(generatedKeys.getInt(1));
                } else {
                    //throw new DAOException("Creating user failed, no generated key obtained.");
                }
            }
			

			
		} catch(SQLException e){
			e.printStackTrace();
		}
		return true;
	}

	public void delete(int id){

		Object[] values = { id };

		try (
			Connection connection = DBManager.getConnection();
			PreparedStatement statement = prepareStatement(connection, SQL_DELETE, false, values);
		) {
			statement.executeUpdate();
		} catch (SQLException e) {
			//throw new DAOException(e);
		}
	}

	private Grade map(ResultSet rs) throws SQLException {
		Grade grade = new Grade();
		grade.setId(rs.getInt("id"));
		grade.setName(rs.getString("name"));
		grade.setProductName(rs.getString("product_name"));
		grade.setType(rs.getShort("type"));
		grade.setMappedName(rs.getString("mapped_name"));
		return grade;
	}
}
