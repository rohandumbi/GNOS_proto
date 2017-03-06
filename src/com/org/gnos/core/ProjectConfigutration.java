package com.org.gnos.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.CycleTimeMappingData;
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Field;
import com.org.gnos.db.model.Model;
import com.org.gnos.db.model.Pit;
import com.org.gnos.db.model.PitGroup;
import com.org.gnos.db.model.Process;
import com.org.gnos.db.model.ProcessJoin;
import com.org.gnos.db.model.Product;
import com.org.gnos.db.model.ProductJoin;
import com.org.gnos.db.model.Stockpile;
import com.org.gnos.db.model.TruckParameterCycleTime;
import com.org.gnos.db.model.TruckParameterData;

public class ProjectConfigutration {

	final static ProjectConfigutration instance = new ProjectConfigutration();
	private CycleTimeMappingData cycleTimeMappingData = new CycleTimeMappingData();
	private TruckParameterData truckParameterData = new TruckParameterData();
	private ArrayList<TruckParameterCycleTime> truckParameterCycleTimeList = new ArrayList<TruckParameterCycleTime>();


	/* Tracking existing cycle time data for the project instance */
	private ArrayList<String> existingCycleTimeFixedFields = new ArrayList<String>();
	private ArrayList<String> existingCycleTimeDumpFields = new ArrayList<String>();
	private ArrayList<String> existingCycleTimeStockpileFields = new ArrayList<String>();
	private ArrayList<String> existingCycleTimeProcessFields = new ArrayList<String>();

	

	private int projectId = -1;

	public static ProjectConfigutration getInstance() {
		return instance;
	}

	public void load(int projectId) {

		if (projectId == -1) {
			System.err
			.println("Can not load project unless projectId is present");
			return;
		}
		this.projectId = projectId;

		// Reinitializing the structures


		loadCycleTimeMappingData();
		loadTruckParameters();
	}




	private void loadCycleTimeMappingData(){
		loadCycleTimeFixedFieldMappingData();
		loadCycleTimeDumpFieldMappingData();
		loadCycleTimeStockpileFieldMappingData();
		loadCycleTimeProcessFieldMappingData();
	}

	private void loadCycleTimeFixedFieldMappingData() {
		String sql = "select field_name, mapped_field_name from cycletime_fixed_field_mapping where project_id = "
				+ this.projectId;
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = DBManager.getConnection();
		Map<String, String> fixedFieldMap = this.cycleTimeMappingData.getFixedFieldMap();

		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			while (rs.next()) {
				existingCycleTimeFixedFields.add(rs.getString(1));
				fixedFieldMap.put(rs.getString(1), rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadCycleTimeDumpFieldMappingData() {
		String sql = "select field_name, mapped_field_name from cycletime_dump_field_mapping where project_id = "
				+ this.projectId;
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = DBManager.getConnection();
		Map<String, String> dumpFieldMap = this.cycleTimeMappingData.getDumpFieldMap();

		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			while (rs.next()) {
				existingCycleTimeDumpFields.add(rs.getString(1));
				dumpFieldMap.put(rs.getString(1), rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadCycleTimeStockpileFieldMappingData() {
		String sql = "select field_name, mapped_field_name from cycletime_stockpile_field_mapping where project_id = "
				+ this.projectId;
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = DBManager.getConnection();
		Map<String, String> stockpileFieldMap = this.cycleTimeMappingData.getStockpileFieldMap();

		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			while (rs.next()) {
				existingCycleTimeStockpileFields.add(rs.getString(1));
				stockpileFieldMap.put(rs.getString(1), rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadCycleTimeProcessFieldMappingData() {
		String sql = "select field_name, mapped_field_name from cycletime_process_field_mapping where project_id = "
				+ this.projectId;
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = DBManager.getConnection();
		Map<String, String> processFieldMap = this.cycleTimeMappingData.getChildProcessFieldMap();

		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			while (rs.next()) {
				existingCycleTimeProcessFields.add(rs.getString(1));
				processFieldMap.put(rs.getString(1), rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadTruckParameters(){
		loadTruckParamterFixedTime();
	}


	private void loadTruckParamterFixedTime(){
		String sql = "select fixed_time from truckparam_fixed_time where project_id = "
				+ this.projectId;
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = DBManager.getConnection();

		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			while (rs.next()) {
				truckParameterData.setFixedTime(rs.getBigDecimal(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void save() {
		saveTruckParameterData();
	}


	

	public void saveCycleTimeData(){
		saveCycleTimeFixedFieldData();
		saveCycleTimeDumpFieldData();
		saveCycleTimeStockpileFieldData();
		saveCycleTimeProcessFieldData();
	}

	public void saveCycleTimeFixedFieldData() {

		/*if (this.newProject) {
			new PitBenchProcessor().updatePitBenchData(projectId);
		}*/
		Connection conn = DBManager.getConnection();
		String insert_sql = " insert into cycletime_fixed_field_mapping (project_id, field_name, mapped_field_name) values (?, ?, ?)";
		String update_sql = " update cycletime_fixed_field_mapping set mapped_field_name = ? where project_id = ? AND field_name = ? ";
		PreparedStatement insertPstmt = null;
		PreparedStatement updatePstmt = null;
		boolean autoCommit = true;

		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			insertPstmt = conn.prepareStatement(insert_sql);
			updatePstmt = conn.prepareStatement(update_sql);
			Map<String, String> fixedFieldMap = this.cycleTimeMappingData.getFixedFieldMap();
			Set<String> keys = fixedFieldMap.keySet();
			Iterator<String> it = keys.iterator();
			while (it.hasNext()) {
				String key = it.next();
				if(existingCycleTimeFixedFields.contains(key)){
					updatePstmt.setString(1, fixedFieldMap.get(key));
					updatePstmt.setInt(2, projectId);
					updatePstmt.setString(3, key);
					updatePstmt.executeUpdate();
				}else{
					insertPstmt.setInt(1, projectId);
					insertPstmt.setString(2, key);
					insertPstmt.setString(3, fixedFieldMap.get(key));
					insertPstmt.executeUpdate();
				}
			}
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
				if (insertPstmt != null)
					insertPstmt.close();
				if (updatePstmt != null)
					updatePstmt.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public void saveCycleTimeDumpFieldData() {

		Connection conn = DBManager.getConnection();
		String insert_sql = " insert into cycletime_dump_field_mapping (project_id, field_name, mapped_field_name) values (?, ?, ?)";
		String update_sql = " update cycletime_dump_field_mapping set mapped_field_name = ? where project_id = ? AND field_name = ? ";
		PreparedStatement insertPstmt = null;
		PreparedStatement updatePstmt = null;
		boolean autoCommit = true;

		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			insertPstmt = conn.prepareStatement(insert_sql);
			updatePstmt = conn.prepareStatement(update_sql);
			Map<String, String> dumpFieldMap = this.cycleTimeMappingData.getDumpFieldMap();
			Set<String> keys = dumpFieldMap.keySet();
			Iterator<String> it = keys.iterator();
			while (it.hasNext()) {
				String key = it.next();
				if(existingCycleTimeDumpFields.contains(key)){
					updatePstmt.setString(1, dumpFieldMap.get(key));
					updatePstmt.setInt(2, projectId);
					updatePstmt.setString(3, key);
					updatePstmt.executeUpdate();
				}else{
					insertPstmt.setInt(1, projectId);
					insertPstmt.setString(2, key);
					insertPstmt.setString(3, dumpFieldMap.get(key));
					insertPstmt.executeUpdate();
				}
			}
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
				if (insertPstmt != null)
					insertPstmt.close();
				if (updatePstmt != null)
					updatePstmt.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public void saveCycleTimeStockpileFieldData() {

		Connection conn = DBManager.getConnection();
		String insert_sql = " insert into cycletime_stockpile_field_mapping (project_id, field_name, mapped_field_name) values (?, ?, ?)";
		String update_sql = " update cycletime_stockpile_field_mapping set mapped_field_name = ? where project_id = ? AND field_name = ? ";
		PreparedStatement insertPstmt = null;
		PreparedStatement updatePstmt = null;
		boolean autoCommit = true;

		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			insertPstmt = conn.prepareStatement(insert_sql);
			updatePstmt = conn.prepareStatement(update_sql);
			Map<String, String> stockpileFieldMap = this.cycleTimeMappingData.getStockpileFieldMap();
			Set<String> keys = stockpileFieldMap.keySet();
			Iterator<String> it = keys.iterator();
			while (it.hasNext()) {
				String key = it.next();
				if(existingCycleTimeStockpileFields.contains(key)){
					updatePstmt.setString(1, stockpileFieldMap.get(key));
					updatePstmt.setInt(2, projectId);
					updatePstmt.setString(3, key);
					updatePstmt.executeUpdate();
				}else{
					insertPstmt.setInt(1, projectId);
					insertPstmt.setString(2, key);
					insertPstmt.setString(3, stockpileFieldMap.get(key));
					insertPstmt.executeUpdate();
				}
			}
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
				if (insertPstmt != null)
					insertPstmt.close();
				if (updatePstmt != null)
					updatePstmt.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public void saveCycleTimeProcessFieldData() {

		Connection conn = DBManager.getConnection();
		String insert_sql = " insert into cycletime_process_field_mapping (project_id, field_name, mapped_field_name) values (?, ?, ?)";
		String update_sql = " update cycletime_process_field_mapping set mapped_field_name = ? where project_id = ? AND field_name = ? ";
		PreparedStatement insertPstmt = null;
		PreparedStatement updatePstmt = null;
		boolean autoCommit = true;

		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			insertPstmt = conn.prepareStatement(insert_sql);
			updatePstmt = conn.prepareStatement(update_sql);
			Map<String, String> processFieldMap = this.cycleTimeMappingData.getChildProcessFieldMap();
			Set<String> keys = processFieldMap.keySet();
			Iterator<String> it = keys.iterator();
			while (it.hasNext()) {
				String key = it.next();
				if(existingCycleTimeProcessFields.contains(key)){
					updatePstmt.setString(1, processFieldMap.get(key));
					updatePstmt.setInt(2, projectId);
					updatePstmt.setString(3, key);
					updatePstmt.executeUpdate();
				}else{
					insertPstmt.setInt(1, projectId);
					insertPstmt.setString(2, key);
					insertPstmt.setString(3, processFieldMap.get(key));
					insertPstmt.executeUpdate();
				}
			}
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
				if (insertPstmt != null)
					insertPstmt.close();
				if (updatePstmt != null)
					updatePstmt.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public void saveTruckParameterData(){
		saveTruckParameterFixedTime();
	}

	public void saveTruckParameterFixedTime(){
		String sql = "select count(*) from truckparam_fixed_time where project_id = "
				+ this.projectId;
		String insert_sql = " insert into truckparam_fixed_time (project_id, fixed_time) values (?, ?)";
		String update_sql = " update truckparam_fixed_time set fixed_time = ? where project_id = ? ";
		PreparedStatement insertPstmt = null;
		PreparedStatement updatePstmt = null;
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = DBManager.getConnection();
		boolean isFixedTimeSavedInDB = false;

		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			insertPstmt = conn.prepareStatement(insert_sql);
			updatePstmt = conn.prepareStatement(update_sql);
			while (rs.next()) {
				if(rs.getInt(1) > 0){
					isFixedTimeSavedInDB = true;
				}
			}
			if(isFixedTimeSavedInDB == true){
				updatePstmt.setBigDecimal(1, this.truckParameterData.getFixedTime());
				updatePstmt.setInt(2, projectId);
				updatePstmt.executeUpdate();
			}else{
				insertPstmt.setInt(1, projectId);
				insertPstmt.setBigDecimal(2, this.truckParameterData.getFixedTime());
				insertPstmt.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (insertPstmt != null)
					insertPstmt.close();
				if (updatePstmt != null)
					updatePstmt.close();
				if (rs != null)
					rs.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}




	
	public TruckParameterCycleTime getTruckParamCycleTimeByStockpileName(String stockpileName) {

		for(TruckParameterCycleTime tpmCycleTime : this.truckParameterCycleTimeList){
			if(tpmCycleTime.getStockPileName().equals(stockpileName)){
				return tpmCycleTime;
			}
		}
		return null;
	}
	

}
