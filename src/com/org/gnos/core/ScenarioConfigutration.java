package com.org.gnos.core;

import java.math.BigDecimal;
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
import com.org.gnos.db.dao.BenchConstraintDAO;
import com.org.gnos.db.dao.GradeConstraintDAO;
import com.org.gnos.db.dao.ProcessConstraintDAO;
import com.org.gnos.db.dao.ScenarioDAO;
import com.org.gnos.db.model.CapexData;
import com.org.gnos.db.model.CapexInstance;
import com.org.gnos.db.model.DumpDependencyData;
import com.org.gnos.db.model.FixedOpexCost;
import com.org.gnos.db.model.GradeConstraintData;
import com.org.gnos.db.model.OpexData;
import com.org.gnos.db.model.PitBenchConstraintData;
import com.org.gnos.db.model.PitDependencyData;
import com.org.gnos.db.model.ProcessConstraintData;
import com.org.gnos.db.model.ProcessJoin;
import com.org.gnos.db.model.Scenario;

public class ScenarioConfigutration {

	final static ScenarioConfigutration instance = new ScenarioConfigutration();

	private List<OpexData> opexDataList = new ArrayList<OpexData>();
	private FixedOpexCost[] fixedCost = new FixedOpexCost[5];// fixed opex cost has 5 fixed categories
	private List<ProcessJoin> processJoins = new ArrayList<ProcessJoin>();
	private List<ProcessConstraintData> processConstraintDataList = new ArrayList<ProcessConstraintData>();
	private List<GradeConstraintData> gradeConstraintDataList = new ArrayList<GradeConstraintData>();
	private List<PitBenchConstraintData> pitBenchConstraintDataList = new ArrayList<PitBenchConstraintData>();
	private List<PitDependencyData> pitDependencyDataList = new ArrayList<PitDependencyData>();
	private List<DumpDependencyData> dumpDependencyDataList = new ArrayList<DumpDependencyData>();
	private ArrayList<CapexData> capexDataList = new ArrayList<CapexData>();
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
		loadPitDependencyData();
		loadDumpDependencyData();
		loadCapexData();
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

				od = getOpexDataById(id);
				if (od == null) {
					od = new OpexData();					
					od.setId(id);
					od.setModelId(modelId);
					od.setInUse(rs.getBoolean(4));
					od.setRevenue(rs.getBoolean(5));
					if(od.isRevenue()){
						od.setExpressionId(expressionId);
					}

					this.opexDataList.add(od);
				}
				od.addYear(rs.getInt(6), rs.getBigDecimal(7));
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
		fixedCost = new FixedOpexCost[5];
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
				BigDecimal value = rs.getBigDecimal(3);
				FixedOpexCost fixedOpexCost = fixedCost[costHead];
				if (fixedOpexCost == null) {
					fixedOpexCost = new FixedOpexCost();
					fixedOpexCost.setCostHead(costHead);
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
		this.processConstraintDataList = new ProcessConstraintDAO().getAll(scenarioId);
	}

	public void loadGradeConstraintData() {
		this.gradeConstraintDataList = new GradeConstraintDAO().getAll(scenarioId);
	}

	public void loadBenchConstraintData() {
		this.pitBenchConstraintDataList = new BenchConstraintDAO().getAll(scenarioId);
	}
	
	public void loadPitDependencyData() {
		this.pitDependencyDataList = new ArrayList<PitDependencyData>();
		String sql = "select id, in_use, first_pit_name, first_pit_bench_name, dependent_pit_name, dependent_pit_bench_name, min_lead, max_lead from pit_dependency_defn where scenario_id =" + this.scenarioId + " order by id";
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = DBManager.getConnection();
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			PitDependencyData pdd;
			while (rs.next()) {
				int id = rs.getInt(1);

				pdd = getPitDependencyDataById(id);
				if (pdd == null) {
					pdd = new PitDependencyData();					
					pdd.setId(id);
					pdd.setInUse(rs.getBoolean(2));
					pdd.setFirstPitName(rs.getString(3));
					pdd.setFirstPitAssociatedBench(rs.getString(4));
					pdd.setDependentPitName(rs.getString(5));
					pdd.setDependentPitAssociatedBench(rs.getString(6));
					pdd.setMinLead(rs.getInt(7));
					pdd.setMaxLead(rs.getInt(8));
					this.pitDependencyDataList.add(pdd);
				}
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
	
	public void loadDumpDependencyData() {
		this.dumpDependencyDataList = new ArrayList<DumpDependencyData>();
		String sql = "select id, in_use, first_pit_name, first_dump_name, dependent_dump_name from dump_dependency_defn where scenario_id =" + this.scenarioId + " order by id";
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = DBManager.getConnection();
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			DumpDependencyData ddd;
			while (rs.next()) {
				int id = rs.getInt(1);

				ddd = getDumpDependencyDataById(id);
				if (ddd == null) {
					ddd = new DumpDependencyData();					
					ddd.setId(id);
					ddd.setInUse(rs.getBoolean(2));
					ddd.setFirstPitName(rs.getString(3));
					ddd.setFirstDumpName(rs.getString(4));
					ddd.setDependentDumpName(rs.getString(5));
					this.dumpDependencyDataList.add(ddd);
				}
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
	
	public void loadCapexData() {
		this.capexDataList = new ArrayList<CapexData>();
		String sql1 = "select a.id, a.scenario_id, a.name, b.id, b.name, b.capex_id, b.group_name, b.group_type, b.capex, b.expansion_capacity from capex_data a, capex_instance b where b.capex_id = a.id and scenario_id="
				+ this.scenarioId;
		//String sql1 = "select id, scenario_id, name from capex_data where scenario_id=" + this.scenarioId;
		
		
		Statement stmt1 = null;
		ResultSet rs1 = null;
		Connection conn = DBManager.getConnection();
		try {
			stmt1 = conn.createStatement();
			stmt1.execute(sql1);
			rs1 = stmt1.getResultSet();
			CapexData cd;
			while (rs1.next()) {
				/*int id = rs.getInt(1);

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
				gcd.addYear(rs.getInt("year"), rs.getFloat("value"));*/
				
				int capexId = rs1.getInt(1);
				cd = getCapexDataById(capexId);
				if (cd == null) {
					cd = new CapexData();
					cd.setId(capexId);
					cd.setScenarioId(rs1.getInt(2));
					cd.setName(rs1.getString(3));
					
					
					
					/*gcd.setId(id);
					gcd.setSelectorName(rs.getString(2));
					gcd.setSelectionType(rs.getInt(3));
					gcd.setSelectedGradeName(rs.getString(4));
					gcd.setProductJoinName(rs.getString(5));
					gcd.setInUse(rs.getBoolean(6));
					gcd.setMax(rs.getBoolean(7));*/

					this.capexDataList.add(cd);
				}
				CapexInstance capexInstance = new CapexInstance();
				capexInstance.setId(rs1.getInt(4));
				capexInstance.setName(rs1.getString(5));
				capexInstance.setCapexId(rs1.getInt(6));
				capexInstance.setGroupingName(rs1.getString(7));
				capexInstance.setGroupingType(rs1.getInt(8));
				capexInstance.setCapexAmount(rs1.getLong(9));
				capexInstance.setExpansionCapacity(rs1.getLong(10));
				cd.addCapexInstance(capexInstance);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt1 != null)
					stmt1.close();
				if (rs1 != null)
					rs1.close();
				if (conn != null)
					DBManager.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void save() {
		this.projectConfiguration = ProjectConfigutration.getInstance();
		saveOpexData();
		saveFixedCostData();
		saveProcessConstraintData();
		saveGradeConstraintData();
		saveBenchConstraintData();
		savePitDependencyList();
		saveDumpDependencyList();
		saveCapexData();
	}


	public void saveProcessConstraintData() {

		ProcessConstraintDAO processConstraintdao = new ProcessConstraintDAO();
		for(ProcessConstraintData pcd : this.processConstraintDataList) {			
			if (pcd.getId() > 0) {
				processConstraintdao.update(pcd);
			} else {
				processConstraintdao.create(pcd, scenarioId);
			}
		}
	}

	public void saveGradeConstraintData() {
		GradeConstraintDAO gradeConstraintdao = new GradeConstraintDAO();
		for(GradeConstraintData gcd : this.gradeConstraintDataList) {			
			if (gcd.getId() > 0) {
				gradeConstraintdao.update(gcd);
			} else {
				gradeConstraintdao.create(gcd, scenarioId);
			}
		}
	}

	public void saveBenchConstraintData() {
		
		BenchConstraintDAO benchConstraintdao = new BenchConstraintDAO();
		for(PitBenchConstraintData pcd : this.pitBenchConstraintDataList) {
			if (pcd.getId() > 0) {
				benchConstraintdao.update(pcd);
			} else {
				benchConstraintdao.create(pcd, scenarioId);
			}
		}
	}
	
	public void saveCapexData() {
		Connection conn = DBManager.getConnection();
		String insert_sql = "insert into capex_data (scenario_id, name) values (?, ?)";
		String mapping_sql = "insert into capex_instance (name, capex_id, group_name, group_type, capex, expansion_capacity) values (?, ?, ?, ?, ?, ?)";
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

			for(CapexData cd : this.capexDataList) {
				int capexDataId = cd.getId();
				if(capexDataId < 0){ // capex data not yet uploaded in DB
					pstmt.setInt(1, this.scenarioId);
					pstmt.setString(2, cd.getName());
					pstmt.executeUpdate();
					rs = pstmt.getGeneratedKeys();
					if (rs.next()){
						capexDataId = rs.getInt(1);
						cd.setId(capexDataId);
					}
				}
				for(CapexInstance ci: cd.getListOfCapexInstances()){
					if(ci.getId() < 0){// this capex instance is not in DB
						ci.setCapexId(cd.getId());
						pstmt1.setString(1, ci.getName());
						pstmt1.setInt(2, ci.getCapexId());
						pstmt1.setString(3, ci.getGroupingName());
						pstmt1.setInt(4, ci.getGroupingType());
						pstmt1.setLong(5, ci.getCapexAmount());
						pstmt1.setLong(6, ci.getExpansionCapacity());
						pstmt1.executeUpdate();
					}else{
						//update code TODO
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
	
	public void savePitDependencyList() {
		Connection conn = DBManager.getConnection();
		String insert_sql = "insert into pit_dependency_defn (scenario_id, in_use, first_pit_name, first_pit_bench_name, dependent_pit_name, dependent_pit_bench_name, min_lead, max_lead) values (?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean autoCommit = true;

		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insert_sql,
					Statement.RETURN_GENERATED_KEYS);

			for(PitDependencyData pdd : this.pitDependencyDataList) {
				if (pdd.getId() > 0)
					continue;
				//pstmt.setInt(1, this.projectConfiguration.getProjectId());
				pstmt.setInt(1, this.scenarioId);
				pstmt.setBoolean(2, pdd.isInUse());
				pstmt.setString(3, pdd.getFirstPitName());
				pstmt.setString(4, pdd.getFirstPitAssociatedBench());
				pstmt.setString(5, pdd.getDependentPitName());
				pstmt.setString(6, pdd.getDependentPitAssociatedBench());
				pstmt.setInt(7, pdd.getMinLead());
				pstmt.setInt(8, pdd.getMaxLead());
				pstmt.executeUpdate();
				rs = pstmt.getGeneratedKeys();

				if (rs.next()){
					int id = rs.getInt(1);
					pdd.setId(id);
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
	
	public void saveDumpDependencyList() {
		Connection conn = DBManager.getConnection();
		String insert_sql = "insert into dump_dependency_defn (scenario_id, in_use, first_pit_name, first_dump_name, dependent_dump_name) values (?, ?, ?, ?, ?)";
		String update_sql = " update dump_dependency_defn set in_use = ? , first_pit_name = ?, first_dump_name = ?, dependent_dump_name = ?  where id = ? and scenario_id = ?" ;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		boolean autoCommit = true;

		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insert_sql,
					Statement.RETURN_GENERATED_KEYS);
			pstmt1 = conn.prepareStatement(update_sql,
					Statement.RETURN_GENERATED_KEYS);

			for(DumpDependencyData ddd : this.dumpDependencyDataList) {
				if (ddd.getId() > 0){
					//update to come
					//pstmt1.setInt(1, this.scenarioId);
					pstmt1.setBoolean(1, ddd.isInUse());
					pstmt1.setString(2, ddd.getFirstPitName());
					pstmt1.setString(3, ddd.getFirstDumpName());
					pstmt1.setString(4, ddd.getDependentDumpName());
					pstmt1.setInt(5, ddd.getId());
					pstmt1.setInt(6, this.scenarioId);
					pstmt1.executeUpdate();
				}else{
					pstmt.setInt(1, this.scenarioId);
					pstmt.setBoolean(2, ddd.isInUse());
					pstmt.setString(3, ddd.getFirstPitName());
					pstmt.setString(4, ddd.getFirstDumpName());
					pstmt.setString(5, ddd.getDependentDumpName());
					pstmt.executeUpdate();
					rs = pstmt.getGeneratedKeys();

					if (rs.next()){
						int id = rs.getInt(1);
						ddd.setId(id);
					}
				}
				//pstmt.setInt(1, this.projectConfiguration.getProjectId());
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
				pstmt.setInt(1,scenarioId);
				pstmt.setInt(2, od.getModelId());
				pstmt.setInt(3, od.getExpressionId());
				pstmt.setBoolean(4, od.isInUse());
				pstmt.setBoolean(5, od.isRevenue());
				pstmt.executeUpdate();
				rs = pstmt.getGeneratedKeys();
				if (rs.next())
				{
					int id = rs.getInt(1);
					od.setId(id);

					Set<Integer> keys = od.getCostData().keySet();
					Iterator<Integer> it = keys.iterator();
					while (it.hasNext()) {
						int key = it.next();
						pstmt1.setInt(1, od.getId());
						pstmt1.setInt(2, key);
						pstmt1.setBigDecimal(3, od.getCostData().get(key));
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
		boolean autoCommit = true;

		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insert_sql,
					Statement.RETURN_GENERATED_KEYS);

			for (int i = 0; i < fixedCost.length; i++) {
				FixedOpexCost fixedOpexCost = fixedCost[i];
				if(fixedOpexCost == null || fixedOpexCost.getCostData() == null) continue;
				Set<Integer> keys = fixedOpexCost.getCostData().keySet();
				Iterator<Integer> it = keys.iterator();
				while (it.hasNext()) {
					int key = it.next();
					//pstmt.setInt(1, this.projectConfiguration.getProjectId());
					pstmt.setInt(1, this.getScenarioId());
					pstmt.setInt(2, i);
					pstmt.setInt(3, key);
					pstmt.setBigDecimal(4, fixedOpexCost.getCostData().get(key));
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
	
	public CapexData getCapexDataById(int id) {
		if (this.capexDataList == null)
			return null;
		for (CapexData cd : this.capexDataList) {
			if (cd.getId() == id) {
				return cd;
			}
		}
		return null;
	}

	public PitBenchConstraintData getBenchConstraintDataById(int id) {
		if (this.pitBenchConstraintDataList == null)
			return null;
		for (PitBenchConstraintData pcd : this.pitBenchConstraintDataList) {
			if (pcd.getId() == id) {
				return pcd;
			}
		}
		return null;
	}
	
	public PitDependencyData getPitDependencyDataById(int id) {
		if (this.pitDependencyDataList == null)
			return null;
		for (PitDependencyData pdd : this.pitDependencyDataList) {
			if (pdd.getId() == id) {
				return pdd;
			}
		}
		return null;
	}
	
	public DumpDependencyData getDumpDependencyDataById(int id) {
		if (this.dumpDependencyDataList == null)
			return null;
		for (DumpDependencyData ddd : this.dumpDependencyDataList) {
			if (ddd.getId() == id) {
				return ddd;
			}
		}
		return null;
	}

	public int getScenarioId() {
		return scenarioId;
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

	public List<PitBenchConstraintData> getPitBenchConstraintDataList() {
		return pitBenchConstraintDataList;
	}

	public void setPitBenchConstraintDataList(
			List<PitBenchConstraintData> pitBenchConstraintDataList) {
		this.pitBenchConstraintDataList = pitBenchConstraintDataList;
	}

	public void addProcessJoin(ProcessJoin processJoin) {
		this.processJoins.add(processJoin);
	}

	public void addProcesssConstraintData(ProcessConstraintData processConstraintData) {
		this.processConstraintDataList.add(processConstraintData);
	}
	
	public List<PitDependencyData> getPitDependencyDataList() {
		return pitDependencyDataList;
	}

	public void setPitDependencyDataList(
			List<PitDependencyData> pitDependencyDataList) {
		this.pitDependencyDataList = pitDependencyDataList;
	}
	
	public ArrayList<CapexData> getCapexDataList() {
		return capexDataList;
	}

	public void setCapexDataList(ArrayList<CapexData> capexDataList) {
		this.capexDataList = capexDataList;
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

	public List<DumpDependencyData> getDumpDependencyDataList() {
		return dumpDependencyDataList;
	}

	public void setDumpDependencyDataList(List<DumpDependencyData> dumpDependencyDataList) {
		this.dumpDependencyDataList = dumpDependencyDataList;
	}
	
	
}
