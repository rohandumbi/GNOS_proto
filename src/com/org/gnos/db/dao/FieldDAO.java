package com.org.gnos.db.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.Field;

public class FieldDAO {

	
	public List<Field> get() {
		List<Field> fields = new ArrayList<Field>();
		String sql = "select id, name, data_type from fields where project_id = "+ ProjectConfigutration.getInstance().getProjectId();
		Statement stmt = null;
		ResultSet rs = null; 
		Connection conn = DBManager.getConnection();
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			Field field = null;
			while(rs.next()){
				field = new Field(rs.getInt(1), rs.getString(2), rs.getShort(3));
				fields.add(field);
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			try {
				if(stmt != null) stmt.close();
				if(rs != null) rs.close();
				if(conn != null) DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return fields;
	}
}
