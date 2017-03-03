package com.org.gnos.services.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.Field;
import com.org.gnos.db.model.Project;
import com.org.gnos.services.csv.CycletTimeDataProcessor;

public class CycleTimeController {

	
	public Project load(String projectId, JsonObject jsonObject) throws Exception {
			String fileName = jsonObject.get("fileName").getAsString();

			loadCSVFile(fileName, Integer.parseInt(projectId));
			throw new Exception();
	}
	
	private void loadCSVFile(String fileName, int projectId ) {
		CycletTimeDataProcessor processor = CycletTimeDataProcessor.getInstance();
		processor.processCsv(fileName);
		processor.dumpToDB(projectId);
		List<Field> cycletimefields = new ArrayList<Field>();
		for(String column: processor.getHeaderColumns()){
			Field field = new Field(column);
			cycletimefields.add(field);
		}
		saveCycleTimeFields(projectId, cycletimefields);
	}
	
	public void saveCycleTimeFields(int projectId, List<Field> cycletimefields ) {
		String inset_sql = " insert into cycle_time_fields (project_id, name) values (?, ?) ";
		String delete_sql = " delete from cycle_time_fields where project_id = ? ";
		try (
				Connection connection = DBManager.getConnection();
	            PreparedStatement ps1 = connection.prepareStatement(inset_sql);
				PreparedStatement ps2 = connection.prepareStatement(delete_sql);
			){
			
			ps2.setInt(1, projectId);
			ps2.executeUpdate();
			for (Field field : cycletimefields) {
				ps1.setInt(1, projectId);
				ps1.setString(2, field.getName());
				ps1.executeUpdate();
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		}
	}
}
