package com.org.gnos.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.Field;

public class FieldDAO {

	
	public List<Field> get() {
		List<Field> fields = new ArrayList<Field>();
		String sql = "select id, name, data_type from fields where project_id = "+ ProjectConfigutration.getInstance().getProjectId();
		try (
				Connection connection = DBManager.getConnection();
	            PreparedStatement statement = connection.prepareStatement(sql);
	            ResultSet resultSet = statement.executeQuery();
			){
			Field field = null;
			while(resultSet.next()){
				field = new Field(resultSet.getInt(1), resultSet.getString(2), resultSet.getShort(3));
				fields.add(field);
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		}
		return fields;
	}
}
