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
import com.org.gnos.db.model.GradeConstraintData;
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
	private FixedOpexCost[] fixedCost = new FixedOpexCost[4];// fixed opex cost has 4 fixed categories
	private List<ProcessJoin> processJoins = new ArrayList<ProcessJoin>();
	private List<ProcessConstraintData> processConstraintDataList = new ArrayList<ProcessConstraintData>();
	private List<GradeConstraintData> gradeConstraintDataList = new ArrayList<GradeConstraintData>();
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
		loadGradeConstraintData();
		loadBenchConstraintData();
	}

	private void loadScenarioData(int scenarioId) {
		this.scenarioData = new ScenarioDAO().get(scenarioId);
	}
	
	public void loadOpexData() {
		this.opexDataList = new ArrayList<OpexData>();
		String sql = "select id, model_id, expression_id, in_use, is_revenue, year, value from opex_defn, model_year_mapping where id= opex_id and scenario_id = "
				+ this.scenarioId + " order by id, year";
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
		String sql = "select cost_head, year, value, value from fixedcost_year_mapping where scenario_id = "
				+ this.scenarioId + " order by cost_head";
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
		this.processConstraintDataList = new ArrayList<ProcessConstraintData>();
		String sql = "select id, selector_name, selector_type, coefficient_name, coefficient_type, in_use, is_max, year, value from process_constraint_defn, process_constraint_year_mapping where id = process_constraint_id and scenario_id = "
				+ this.scenarioId + " order by id, year";
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
				ProcessJoin processJoin = this.projectConfiguration.getProcessJoinByName(processJoinName);

				pcd = getProcessConstraintDataById(id);
				if (pcd == null) {
					pcd = new ProcessConstraintData();					
					pcd.setId(id);
					pcd.setSelector_name(rs.getString(2));
					pcd.setSelectionType(rs.getInt(3));
					pcd.setCoefficient_name(rs.getString(4));
					pcd.setCoefficientType(rs.getInt(5));
					pcd.setInUse(rs.getBoolean(6));
					pcd.setMax(rs.getBoolean(7));

					this.processConstraintDataList.add(pcd);
				}
				pcd.addYear(rs.getInt("year"), rs.getFloat("value"));
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

	public void loadGradeConstraintData() {
		this.gradeConstraintDataList = new ArrayList<GradeConstraintData>();
		String sql = "select id, selector_name, selector_type, grade, product_join_name, in_use, is_max, year, value from grade_constraint_defn, grade_constraint_year_mapping where id = grade_constraint_id and scenario_id = "
				+ this.scenarioId + " order by id, year";
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = DBManager.getConnection();
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			GradeConstraintData gcd;
			while (rs.next()) {
				int id = rs.getInt(1);

				gcd = getGradeConstraintDataById(id);
				if (gcd == null) {
					gcd = new GradeConstraintData();					
					gcd.setId(id);
					gcd.setSelectorName(rs.getString(2));
					gcd.setSelectionType(rs.getInt(3));
					gcd.setSelectedGradeName(rs.getString(4));
					gcd.setProductJoinName(rs.getString(5));
					gcd.setInUse(rs.getBoolean(6));
					gcd.setMax(rs.getBoolean(7));

					this.gradeConstraintDataList.add(gcd);
				}
				gcd.addYear(rs.getInt("year"), rs.getFloat("value"));
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
	
	public void loadBenchConstraintData() {
		
	}
	
	public void save() {
		this.projectConfiguration = ProjectConfigutration.getInstance();
		saveOpexData();
		saveFixedCostData();
		saveProcessConstraintData();
		saveGradeConstraintData();
		saveBenchConstraintData();
	}


	public void saveProcessConstraintData() {
		Connection conn = DBManager.getConnection();
		String insert_sql = "insert into process_constraint_defn (scenario_id, selector_name, selector_type, coefficient_name, coefficient_type,  in_use, is_max) values (?, ?, ?, ?, ?, ?, ?)";
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
				//pstmt.setInt(1, this.projectConfiguration.getProjectId());
				pstmt.setInt(1, this.scenarioId);
				pstmt.setString(2, pcd.getSelector_name());
				pstmt.setInt(3, pcd.getSelectionType());
				pstmt.setString(4, pcd.getCoefficient_name());
				pstmt.setInt(5, pcd.getCoefficientType());
				pstmt.setBoolean(6, pcd.isInUse());
				pstmt.setBoolean(7, pcd.isMax());
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
	
	public void saveGradeConstraintData() {
		Connection conn = DBManager.getConnection();
		String insert_sql = "insert into grade_constraint_defn (scenario_id, grade, product_join_name, selector_name, selector_type, in_use, is_max) values (?, ?, ?, ?, ?, ?, ?)";
		String mapping_sql = "insert into grade_constraint_year_mapping (grade_constraint_id, year, value) values (?, ?, ?)";
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
			
			for(GradeConstraintData gcd : this.gradeConstraintDataList) {
				if (gcd.getId() > 0)
					continue;
				//pstmt.setInt(1, this.projectConfiguration.getProjectId());
				pstmt.setInt(1, this.scenarioId);
				pstmt.setString(2, gcd.getSelectedGradeName());
				pstmt.setString(3, gcd.getProductJoinName());
				pstmt.setString(4, gcd.getSelectorName());
				pstmt.setInt(5, gcd.getSelectionType());
				pstmt.setBoolean(6, gcd.isInUse());
				pstmt.setBoolean(7, gcd.isMax());
				pstmt.executeUpdate();
				rs = pstmt.getGeneratedKeys();
				
				if (rs.next()){
					int id = rs.getInt(1);
					gcd.setId(id);

					Set keys = gcd.getConstraintData().keySet();
					Iterator<Integer> it = keys.iterator();
					while (it.hasNext()) {
						int key = it.next();
						pstmt1.setInt(1, gcd.getId());
						pstmt1.setInt(2, key);
						pstmt1.setFloat(3, gcd.getConstraintData().get(key));
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
	
	public void saveBenchConstraintData() {

	}
	public void saveOpexData() {
		Connection conn = DBManager.getConnection();
		String insert_sql = "insert into opex_defn (scenario_id, model_id, expression_id, in_use, is_revenue) values ( ?, ?, ?, ?, ?)";
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
				//pstmt.setInt(1, this.projectConfiguration.getProjectId());
				pstmt.setInt(1, od.getScenarioId());
				pstmt.setInt(2, od.getModel().getId());
				if(od.getExpression() != null){
					pstmt.setInt(3, od.getExpression().getId());
				}else{
					pstmt.setNull(3, java.sql.Types.INTEGER);
				}
				pstmt.setBoolean(4, od.isInUse());
				pstmt.setBoolean(5, od.isRevenue());
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
		if(fixedCost == null){
			return;
		}
		Connection conn = DBManager.getConnection();
		String insert_sql = "insert into fixedcost_year_mapping (scenario_id, cost_head, year, value) values (?, ?, ?, ?)";
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
				if(fixedOpexCost == null || fixedOpexCost.getCostData() == null) continue;
				Set keys = fixedOpexCost.getCostData().keySet();
				Iterator<Integer> it = keys.iterator();
				while (it.hasNext()) {
					int key = it.next();
					//pstmt.setInt(1, this.projectConfiguration.getProjectId());
					pstmt.setInt(1, fixedOpexCost.getScenarioId());
					pstmt.setInt(2, i);
					pstmt.setInt(3, key);
					pstmt.setFloat(4, fixedOpexCost.getCostData().get(key));
					pstmt.executeUpdate();
				}
			}
			conn.commit();

		} catch (SQLException e) {
			System.err.println("Failed saving Fixed cost."+e.getMessage());
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

	public GradeConstraintData getGradeConstraintDataById(int id) {
		if (this.gradeConstraintDataList == null)
			return null;
		for (GradeConstraintData gcd : this.gradeConstraintDataList) {
			if (gcd.getId() == id) {
				return gcd;
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
	
	public List<GradeConstraintData> getGradeConstraintDataList() {
		return gradeConstraintDataList;
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
	
	public String getName() {
		if(scenarioData == null){
			return null;
		} else {
			return this.scenarioData.getName();
		}
	}
}
