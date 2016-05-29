package com.org.gnos.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.org.gnos.db.DBManager;
import com.org.gnos.db.dao.ScenarioDAO;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.FixedOpexCost;
import com.org.gnos.db.model.Model;
import com.org.gnos.db.model.OpexData;
import com.org.gnos.db.model.OreMiningCost;
import com.org.gnos.db.model.ProcessConstraintData;
import com.org.gnos.db.model.ProcessJoin;
import com.org.gnos.db.model.Scenario;
import com.org.gnos.db.model.StockpileReclaimingCost;
import com.org.gnos.db.model.StockpilingCost;
import com.org.gnos.db.model.WasteMiningCost;

public class ScenarioConfigutration {

	final static ScenarioConfigutration instance = new ScenarioConfigutration();

	private List<OpexData> opexDataList = new ArrayList<OpexData>();
	private FixedOpexCost[] fixedCost;
	private List<ProcessJoin> processJoins = new ArrayList<ProcessJoin>();
	private List<ProcessConstraintData> processConstraintDataList = new ArrayList<ProcessConstraintData>();
	private Scenario scenarioData ;

	private int scenarioId = -1;
	ProjectConfigutration projectConfiguration = null;

	public static ScenarioConfigutration getInstance() {
		return instance;
	}

	public void load(int scenarioId) {

		if (scenarioId == -1) {
			System.err.println("Can not load scenario unless scenarioId is present");
			return;
		}
		this.projectConfiguration = ProjectConfigutration.getInstance();
		this.scenarioId = scenarioId;

		loadScenarioData(this.scenarioId);
		opexDataList = new ArrayList<OpexData>();
		
		loadOpexData();
		loadFixedCost();
		loadProcessConstraintData();
	}

	private void loadScenarioData(int scenarioId) {
		this.scenarioData = new ScenarioDAO().get(scenarioId);
	}
	
	public void loadOpexData() {
		String sql = "select id, model_id, expression_id, in_use, is_revenue, year, value from opex_defn, model_year_mapping where id= opex_id and scneario_id = "
				+ this.projectConfiguration.getProjectId() + " order by id, year";
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = DBManager.getConnection();
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			OpexData od;
			while (rs.next()) {
				int id = rs.getInt(1);
				int modelId = rs.getInt(2);
				int expressionId = rs.getInt(3);
				Model model = this.projectConfiguration.getModelById(modelId);

				od = getOpexDataById(id);
				if (od == null) {
					od = new OpexData(model);					
					od.setId(id);
					od.setInUse(rs.getBoolean(4));
					od.setRevenue(rs.getBoolean(5));
					if(od.isRevenue()){
						Expression expression = this.projectConfiguration.getExpressionById(expressionId);
						od.setExpression(expression);
					}

					this.opexDataList.add(od);
				}
				od.addYear(rs.getInt(6), rs.getFloat(7));
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

	public void loadFixedCost() {
		String sql = "select cost_head, year, value, value from fixedcost_year_mapping where project_id = "
				+ this.projectConfiguration.getProjectId() + " order by cost_head";
		fixedCost = new FixedOpexCost[4];
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = DBManager.getConnection();
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			while (rs.next()) {
				int costHead = rs.getInt(1);
				int year = rs.getInt(2);
				float value = rs.getFloat(3);
				FixedOpexCost fixedOpexCost = fixedCost[costHead];
				if (fixedOpexCost == null) {
					if (costHead == 0) {
						fixedOpexCost = new OreMiningCost();
					} else if (costHead == 1) {
						fixedOpexCost = new WasteMiningCost();
					} else if (costHead == 2) {
						fixedOpexCost = new StockpilingCost();
					} else if (costHead == 3) {
						fixedOpexCost = new StockpileReclaimingCost();
					}
					fixedCost[costHead] = fixedOpexCost;
				}

				fixedOpexCost.getCostData().put(year, value);
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
	
	public void loadProcessConstraintData() {
		String sql = "select id, process_join_name, expression_id, in_use, is_max, year, value from process_constraint_defn, process_constraint_year_mapping where id= process_constraint_id and project_id = "
				+ this.projectConfiguration.getProjectId() + " order by id, year";
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = DBManager.getConnection();
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			ProcessConstraintData pcd;
			while (rs.next()) {
				int id = rs.getInt(1);
				String processJoinName = rs.getString(2);
				int expressionId = rs.getInt(3);
				//Model model = this.getModelById(modelId);
				ProcessJoin processJoin = this.getProcessJoinByName(processJoinName);

				pcd = getProcessConstraintDataById(id);
				if (pcd == null) {
					pcd = new ProcessConstraintData();					
					pcd.setId(id);
					pcd.setInUse(rs.getBoolean(4));
					pcd.setMax(rs.getBoolean(5));
					pcd.setExpression(this.projectConfiguration.getExpressionById(expressionId));
					pcd.setProcessJoin(processJoin);

					this.processConstraintDataList.add(pcd);
				}
				pcd.addYear(rs.getInt(6), rs.getFloat(7));
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
		saveOpexData();
		saveFixedCostData();
		saveProcessConstraintData();
	}


	public void saveProcessConstraintData() {
		Connection conn = DBManager.getConnection();
		String insert_sql = "insert into process_constraint_defn (project_id, scenario_id, process_join_name, expression_id, in_use, is_max) values (?, ?, ?, ?, ?, ?)";
		String mapping_sql = "insert into process_constraint_year_mapping (process_constraint_id, year, value) values (?, ?, ?)";
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		boolean autoCommit = true;

		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insert_sql,
					Statement.RETURN_GENERATED_KEYS);
			pstmt1 = conn.prepareStatement(mapping_sql);
			
			for(ProcessConstraintData pcd : this.processConstraintDataList) {
				if (pcd.getId() > 0)
					continue;
				pstmt.setInt(1, this.projectConfiguration.getProjectId());
				pstmt.setInt(2, 1);
				pstmt.setString(3, pcd.getProcessJoin().getName());
				if(pcd.getExpression() != null){
					pstmt.setInt(4, pcd.getExpression().getId());
				}else{
					pstmt.setNull(4, java.sql.Types.INTEGER);
				}
				pstmt.setBoolean(5, pcd.isInUse());
				pstmt.setBoolean(6, pcd.isMax());
				pstmt.executeUpdate();
				rs = pstmt.getGeneratedKeys();
				
				if (rs.next()){
					int id = rs.getInt(1);
					pcd.setId(id);

					Set keys = pcd.getConstraintData().keySet();
					Iterator<Integer> it = keys.iterator();
					while (it.hasNext()) {
						int key = it.next();
						pstmt1.setInt(1, pcd.getId());
						pstmt1.setInt(2, key);
						pstmt1.setFloat(3, pcd.getConstraintData().get(key));
						pstmt1.executeUpdate();
					}
				}
			}

			conn.commit();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void saveOpexData() {
		Connection conn = DBManager.getConnection();
		String insert_sql = "insert into opex_defn (project_id, scenario_id, model_id, expression_id, in_use, is_revenue) values (?, ?, ?, ?, ?, ?)";
		String mapping_sql = "insert into model_year_mapping (opex_id, year, value) values (?, ?, ?)";
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		boolean autoCommit = true;

		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insert_sql,
					Statement.RETURN_GENERATED_KEYS);
			pstmt1 = conn.prepareStatement(mapping_sql);

			for (OpexData od : this.opexDataList) {
				if (od.getId() > 0)
					continue;
				pstmt.setInt(1, this.projectConfiguration.getProjectId());
				pstmt.setInt(2, 1);
				pstmt.setInt(3, od.getModel().getId());
				if(od.getExpression() != null){
					pstmt.setInt(4, od.getExpression().getId());
				}else{
					pstmt.setNull(4, java.sql.Types.INTEGER);
				}
				pstmt.setBoolean(5, od.isInUse());
				pstmt.setBoolean(6, od.isRevenue());
				pstmt.executeUpdate();
				rs = pstmt.getGeneratedKeys();
				if (rs.next())
				{
					int id = rs.getInt(1);
					od.setId(id);

					Set keys = od.getCostData().keySet();
					Iterator<Integer> it = keys.iterator();
					while (it.hasNext()) {
						int key = it.next();
						pstmt1.setInt(1, od.getId());
						pstmt1.setInt(2, key);
						pstmt1.setFloat(3, od.getCostData().get(key));
						pstmt1.executeUpdate();
					}
				}
			}
			conn.commit();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void saveFixedCostData() {
		Connection conn = DBManager.getConnection();
		String insert_sql = "insert into fixedcost_year_mapping (project_id, scenario_id, cost_head, year, value) values (?, ?, ?, ?, ?)";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean autoCommit = true;

		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insert_sql,
					Statement.RETURN_GENERATED_KEYS);

			for (int i = 0; i < fixedCost.length; i++) {
				FixedOpexCost fixedOpexCost = fixedCost[i];
				Set keys = fixedOpexCost.getCostData().keySet();
				Iterator<Integer> it = keys.iterator();
				while (it.hasNext()) {
					int key = it.next();
					pstmt.setInt(1, this.projectConfiguration.getProjectId());
					pstmt.setInt(2, 1);
					pstmt.setInt(3, i);
					pstmt.setInt(4, key);
					pstmt.setFloat(5, fixedOpexCost.getCostData().get(key));
					pstmt.executeUpdate();
				}
			}
			conn.commit();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}



	public ProcessJoin getProcessJoinByName(String name) {
		if (name == null)
			return null;
		for (ProcessJoin processJoin : processJoins) {
			if (processJoin.getName().equals(name)) {
				return processJoin;
			}
		}
		return null;
	}

	public OpexData getOpexDataById(int id) {
		if (this.opexDataList == null)
			return null;
		for (OpexData od : this.opexDataList) {
			if (od.getId() == id) {
				return od;
			}
		}
		return null;
	}
	
	public ProcessConstraintData getProcessConstraintDataById(int id) {
		if (this.processConstraintDataList == null)
			return null;
		for (ProcessConstraintData pcd : this.processConstraintDataList) {
			if (pcd.getId() == id) {
				return pcd;
			}
		}
		return null;
	}


	public List<OpexData> getOpexDataList() {
		return opexDataList;
	}

	public FixedOpexCost[] getFixedCost() {
		return fixedCost;
	}

	public void setFixedCost(FixedOpexCost[] fixedCost) {
		this.fixedCost = fixedCost;
	}

	public void setOpexDataList(List<OpexData> opexDataList) {
		this.opexDataList = opexDataList;
	}

	public List<ProcessJoin> getProcessJoins() {
		return processJoins;
	}

	public void setProcessJoins(List<ProcessJoin> processJoins) {
		this.processJoins = processJoins;
	}


	public List<ProcessConstraintData> getProcessConstraintDataList() {
		return processConstraintDataList;
	}

	public void setProcessConstraintDataList(List<ProcessConstraintData> processConstraintDataList) {
		this.processConstraintDataList = processConstraintDataList;
	}

	public void addProcessJoin(ProcessJoin processJoin) {
		this.processJoins.add(processJoin);
	}
	
	public void addProcesssConstraintData(ProcessConstraintData processConstraintData) {
		this.processConstraintDataList.add(processConstraintData);
	}

	public int getStartYear() {
		if(scenarioData == null){
			return -1;
		} else {
			return this.scenarioData.getStartYear();
		}
	}
	
	public int getTimePeriod() {
		if(scenarioData == null){
			return -1;
		} else {
			return this.scenarioData.getTimePeriod();
		}
	}
	
	public float getDiscount() {
		if(scenarioData == null){
			return -1;
		} else {
			return this.scenarioData.getDiscount();
		}
	}
}
