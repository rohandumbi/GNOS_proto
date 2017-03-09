package com.org.gnos.scheduler.equation;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.core.Bench;
import com.org.gnos.core.Block;
import com.org.gnos.core.Node;
import com.org.gnos.core.Pit;
import com.org.gnos.core.Tree;
import com.org.gnos.db.DBManager;
import com.org.gnos.db.dao.BenchConstraintDAO;
import com.org.gnos.db.dao.CapexDAO;
import com.org.gnos.db.dao.CycleFixedTimeDAO;
import com.org.gnos.db.dao.CycleTimeFieldMappingDAO;
import com.org.gnos.db.dao.DumpDAO;
import com.org.gnos.db.dao.DumpDependencyDAO;
import com.org.gnos.db.dao.ExpressionDAO;
import com.org.gnos.db.dao.FieldDAO;
import com.org.gnos.db.dao.FixedCostDAO;
import com.org.gnos.db.dao.GradeConstraintDAO;
import com.org.gnos.db.dao.GradeDAO;
import com.org.gnos.db.dao.ModelDAO;
import com.org.gnos.db.dao.OpexDAO;
import com.org.gnos.db.dao.PitDependencyDAO;
import com.org.gnos.db.dao.PitGroupDAO;
import com.org.gnos.db.dao.ProcessConstraintDAO;
import com.org.gnos.db.dao.ProcessJoinDAO;
import com.org.gnos.db.dao.ProcessTreeDAO;
import com.org.gnos.db.dao.ProductDAO;
import com.org.gnos.db.dao.ProductJoinDAO;
import com.org.gnos.db.dao.RequiredFieldDAO;
import com.org.gnos.db.dao.ScenarioDAO;
import com.org.gnos.db.dao.StockpileDAO;
import com.org.gnos.db.dao.TruckParameterCycleTimeDAO;
import com.org.gnos.db.dao.TruckParameterPayloadDAO;
import com.org.gnos.db.model.CapexData;
import com.org.gnos.db.model.CycleTimeFieldMapping;
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.DumpDependencyData;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Field;
import com.org.gnos.db.model.FixedOpexCost;
import com.org.gnos.db.model.Grade;
import com.org.gnos.db.model.GradeConstraintData;
import com.org.gnos.db.model.Model;
import com.org.gnos.db.model.OpexData;
import com.org.gnos.db.model.PitBenchConstraintData;
import com.org.gnos.db.model.PitDependencyData;
import com.org.gnos.db.model.PitGroup;
import com.org.gnos.db.model.Process;
import com.org.gnos.db.model.ProcessConstraintData;
import com.org.gnos.db.model.ProcessJoin;
import com.org.gnos.db.model.ProcessTreeNode;
import com.org.gnos.db.model.Product;
import com.org.gnos.db.model.ProductJoin;
import com.org.gnos.db.model.RequiredField;
import com.org.gnos.db.model.Scenario;
import com.org.gnos.db.model.Stockpile;
import com.org.gnos.db.model.TruckParameterCycleTime;
import com.org.gnos.db.model.TruckParameterPayload;

public class ExecutionContext {

	private Set<Block> wasteBlocks = new HashSet<Block>();
	private Set<Block> processBlocks = new HashSet<Block>();
	private Map<Integer, Block> blocks = new LinkedHashMap<Integer, Block>();
	private Map<Integer, Pit> pits = new LinkedHashMap<Integer, Pit>();
	private Map<String, Pit> pitNameMap = new LinkedHashMap<String, Pit>();
	private Map<Integer, List<String>> blockVariableMapping = new HashMap<Integer, List<String>>();
	private Map<Integer, Integer> blockPayloadMapping = new HashMap<Integer, Integer>();
	private Map<String, BigDecimal> cycleTimeDataMapping = new HashMap<String, BigDecimal>();
	private String pitFieldName;
	private String benchFieldName;
	protected String tonnesWtFieldName;

	/* Data */

	private List<Field> fields;
	private List<PitGroup> pitGroups;
	private List<Stockpile> stockpiles;
	private List<Dump> dumps;
	private List<Expression> expressions;
	private List<Model> models;
	private Tree processTree;
	private List<Process> processList;
	private List<ProcessJoin> processJoinList;
	private List<ProductJoin> productJoinList;
	private List<Product> productList;
	private List<Grade> gradeList;
	private List<CycleTimeFieldMapping> cycleTimeFieldMappings;
	private List<TruckParameterCycleTime> truckParameterCycleTimeList;
	private BigDecimal fixedTime;

	private Scenario scenario;
	private List<OpexData> opexDataList;
	private List<FixedOpexCost> fixedOpexCostList;
	private List<ProcessConstraintData> processConstraintDataList;
	private List<GradeConstraintData> gradeConstraintDataList;
	private List<PitBenchConstraintData> pitBenchConstraintDataList;
	private List<PitDependencyData> pitDependencyDataList;
	private List<DumpDependencyData> dumpDependencyDataList;
	private List<CapexData> capexDataList;

	private int projectId;
	private int scenarioId;

	protected int startYear;
	protected int timePeriodStart;
	protected int timePeriodEnd;

	private boolean spReclaimEnabled = true;

	private Map<String, Boolean> equationEnableMap;

	public ExecutionContext(int projectId, int scenarioId) {
		this.projectId = projectId;
		this.scenarioId = scenarioId;
		loadFields();
		loadRequiredFields();
		loadBlocks();
		loadProject();
		loadScenario();
	}

	private void loadProject() {
		loadExpressions();
		loadModels();
		loadProcesses();
		loadProducts();
		loadProcessJoins();
		loadProductJoins();
		loadGrades();
		loadPitGroups();
		loadStockpiles();
		loadDumps();
		loadBlockPayloadMapping();
		loadFixedTime();
		loadTruckParamCycleTime();
		loadCycleTimeFieldMapping();
	}

	private void loadFields() {
		fields = new FieldDAO().getAll(projectId);
	}
	private void loadRequiredFields() {
		List<RequiredField> requiredFields = new RequiredFieldDAO().getAll(projectId);
		for (RequiredField requiredField : requiredFields) {
			switch (requiredField.getFieldName()) {
			case "pit_name":
				pitFieldName = requiredField.getMappedFieldname();
				break;
			case "bench_rl":
				benchFieldName = requiredField.getMappedFieldname();
				break;
			case "tonnes_wt":
				tonnesWtFieldName = requiredField.getMappedFieldname();
				break;
			}
		}

	}

	private void loadExpressions() {
		expressions = new ExpressionDAO().getAll(projectId);
	}

	private void loadModels() {
		models = new ModelDAO().getAll(projectId);
	}

	private void loadProcesses() {
		List<ProcessTreeNode> processTreeNodes = new ProcessTreeDAO().getAll(projectId);
		processList = new ArrayList<Process>();

		Map<String, Node> nodes = new HashMap<String, Node>();
		processTree = new Tree();
		for (ProcessTreeNode processTreeNode : processTreeNodes) {
			int modelId = processTreeNode.getModelId();
			int parentModelId = processTreeNode.getParentModelId();

			Model model = this.getModelById(modelId);

			Node node = nodes.get(model.getName());
			if (node == null) {
				node = new Node(model);
				nodes.put(model.getName(), node);
			}
			if (parentModelId == -1) {
				processTree.addNode(node, null);
			} else {
				Model pModel = this.getModelById(parentModelId);
				if (pModel != null) {
					Node pNode = nodes.get(pModel.getName());
					if (pNode == null) {
						pNode = new Node(pModel);
						nodes.put(pModel.getName(), pNode);
					}
					processTree.addNode(node, pNode);
					node.setParent(pNode);
				}
			}
		}
		List<Node> leafNodes = processTree.getLeafNodes();
		int count = 1;
		for (Node node : leafNodes) {
			Process process = new Process();
			process.setProcessNo(count);
			process.setModel(node.getData());
			processList.add(process);
			count++;
		}
	}

	private void loadProducts() {
		productList = new ProductDAO().getAll(projectId);
	}

	private void loadProcessJoins() {
		processJoinList = new ProcessJoinDAO().getAll(projectId);
	}

	private void loadProductJoins() {
		productJoinList = new ProductJoinDAO().getAll(projectId);
	}

	private void loadGrades() {
		gradeList = new GradeDAO().getAll(projectId);
	}

	private void loadPitGroups() {
		pitGroups = new PitGroupDAO().getAll(projectId);
	}

	private void loadStockpiles() {
		stockpiles = new StockpileDAO().getAll(projectId);
	}

	private void loadDumps() {
		dumps = new DumpDAO().getAll(projectId);
	}

	private void loadBlockPayloadMapping() {
		List<TruckParameterPayload> truckParameterPayloads = new TruckParameterPayloadDAO().getAll(projectId);
		for (TruckParameterPayload truckParameterPayload : truckParameterPayloads) {
			String exprname = truckParameterPayload.getMaterialName();
			Expression expr = getExpressionByName(exprname);
			if (expr != null) {
				String condition = expr.getFilter();
				List<Block> blocks = findBlocks(condition);
				for (Block b : blocks) {
					blockPayloadMapping.put(b.getId(), truckParameterPayload.getPayload());
				}
			}
		}

	}

	private void loadFixedTime() {
		fixedTime = new CycleFixedTimeDAO().getAll(projectId);
	}

	private void loadTruckParamCycleTime() {
		truckParameterCycleTimeList = new TruckParameterCycleTimeDAO().getAll(projectId);
	}

	private void loadCycleTimeFieldMapping() {
		cycleTimeFieldMappings = new CycleTimeFieldMappingDAO().getAll(projectId);
	}

	private void loadScenario() {
		scenario = new ScenarioDAO().get(scenarioId);
		startYear = scenario.getStartYear();
		timePeriodStart = 1;
		timePeriodEnd = scenario.getTimePeriod();

		loadOpexData();
		loadFixedCost();
		loadProcessConstraintData();
		loadGradeConstraintData();
		loadBenchConstraintData();
		loadPitDependencyData();
		loadDumpDependencyData();
		loadCapexData();
	}

	private void loadOpexData() {
		opexDataList = new OpexDAO().getAll(scenarioId);
	}

	private void loadFixedCost() {
		fixedOpexCostList = new FixedCostDAO().getAll(scenarioId);
	}

	private void loadProcessConstraintData() {
		processConstraintDataList = new ProcessConstraintDAO().getAll(scenarioId);
	}

	private void loadGradeConstraintData() {
		gradeConstraintDataList = new GradeConstraintDAO().getAll(scenarioId);
	}

	private void loadBenchConstraintData() {
		pitBenchConstraintDataList = new BenchConstraintDAO().getAll(scenarioId);
	}

	private void loadPitDependencyData() {
		pitDependencyDataList = new PitDependencyDAO().getAll(scenarioId);
	}

	private void loadDumpDependencyData() {
		dumpDependencyDataList = new DumpDependencyDAO().getAll(scenarioId);
	}

	private void loadCapexData() {
		capexDataList = new CapexDAO().getAll(scenarioId);
	}

	private void loadBlocks() {
		String sql = "select a.*, b.* from gnos_data_" + projectId + " a, gnos_computed_data_" + projectId
				+ " b where a.id = b.row_id";
		try (Connection conn = DBManager.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);) {
			ResultSetMetaData md = rs.getMetaData();
			int columnCount = md.getColumnCount();
			while (rs.next()) {

				Block block = new Block();
				block.setId(rs.getInt("row_id"));
				block.setBlockNo(rs.getInt("block_no"));
				boolean computedDataField = false;
				for (int i = 1; i <= columnCount; i++) {
					String columnName = md.getColumnName(i);
					if (!computedDataField && columnName.equalsIgnoreCase("row_id")) {
						computedDataField = true;
					}
					if (!computedDataField) {
						block.addField(columnName, rs.getString(i));
					} else {
						block.addComputedField(columnName, rs.getString(i));
					}
				}
				Pit pit = pits.get(block.getPitNo());
				if (pit == null) {
					pit = new Pit();
					pit.setPitNo(block.getPitNo());
					pit.setPitName(block.getField(pitFieldName));
					pits.put(block.getPitNo(), pit);
					pitNameMap.put(pit.getPitName(), pit);
				}
				Bench bench = pit.getBench(block.getBenchNo());
				if (bench == null) {
					bench = new Bench();
					bench.setBenchName(block.getField(benchFieldName));
					bench.setBenchNo(block.getBenchNo());
				}
				bench.addBlock(block);
				pit.addBench(bench);
				blocks.put(block.getId(), block);
			}

		} catch (SQLException e) {
			System.err.println("Failed to load blocks " + e.getMessage());
		}
	}

	public Set<String> flattenPitGroup(PitGroup pg) {
		Set<String> pits = new HashSet<String>();
		pits.addAll(pg.getListChildPits());
		for (String childGroup : pg.getListChildPitGroups()) {
			pits.addAll(flattenPitGroup(getPitGroupfromName(childGroup)));
		}

		return pits;
	}

	private List<Block> findBlocks(String condition) {
		List<Block> blocks = new ArrayList<Block>();
		String sql = "select id from gnos_data_" + projectId;
		if (hasValue(condition)) {
			sql += " where " + condition;
		}

		try (Connection conn = DBManager.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);) {
			while (rs.next()) {
				int id = rs.getInt("id");
				Block block = getBlocks().get(id);
				blocks.add(block);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return blocks;
	}

	// Helper methods

	public Field getFieldById(int id) {
		if (fields == null)
			return null;
		for (Field field : fields) {
			if (field.getId() == id) {
				return field;
			}
		}
		return null;
	}
	public Expression getExpressionByName(String name) {
		if (expressions == null || name == null)
			return null;
		for (Expression expression : expressions) {
			if (expression.getName().equals(name)) {
				return expression;
			}
		}
		return null;
	}

	
	public Expression getExpressionById(int id) {
		if (expressions == null)
			return null;
		for (Expression expression : expressions) {
			if (expression.getId() == id) {
				return expression;
			}
		}
		return null;
	}

	public Model getModelById(int id) {
		if (models == null)
			return null;
		for (Model model : models) {
			if (model.getId() == id) {
				return model;
			}
		}
		return null;
	}

	public Stockpile getStockpileFromNo(int spNo) {
		;
		for (Stockpile sp : stockpiles) {
			if (sp.getStockpileNumber() == spNo) {
				return sp;
			}
		}
		return null;
	}

	public PitGroup getPitGroupfromName(String name) {
		if (pitGroups == null || pitGroups.size() == 0 || name == null)
			return null;

		for (PitGroup pitGroup : pitGroups) {
			if (pitGroup.getName().equals(name)) {
				return pitGroup;
			}
		}
		return null;
	}

	public ProductJoin getProductJoinFromName(String name) {
		if (productJoinList == null || productJoinList.size() == 0 || name == null)
			return null;

		for (ProductJoin productJoin : productJoinList) {
			if (productJoin.getName().equals(name)) {
				return productJoin;
			}
		}
		return null;
	}

	public Product getProductFromName(String name) {
		if (productList == null || productList.size() == 0 || name == null)
			return null;

		for (Product product : productList) {
			if (product.getName().equals(name)) {
				return product;
			}
		}
		return null;
	}

	public ProcessJoin getProcessJoinByName(String name) {
		if (processJoinList == null || processJoinList.size() == 0 || name == null)
			return null;

		for (ProcessJoin processJoin : processJoinList) {
			if (processJoin.getName().equals(name)) {
				return processJoin;
			}
		}
		return null;
	}

	public Dump getDumpfromDumpName(String name) {
		for (Dump dump : dumps) {
			if (dump.getName().equals(name)) {
				return dump;
			}
		}
		return null;
	}

	public Dump getDumpfromPitName(String name) {
		for (Dump dump : dumps) {
			if (dump.getType() == 0)
				continue;
			if (dump.getName().equals(name)) {
				return dump;
			}
		}
		return null;
	}

	public List<Grade> getGradesForProduct(String productName) {
		List<Grade> grades = new ArrayList<Grade>();
		for (Grade grade : gradeList) {
			if (grade.getProductName().equals(productName)) {
				grades.add(grade);
			}
		}
		return grades;
	}

	public TruckParameterCycleTime getTruckParamCycleTimeByStockpileName(String stockpileName) {

		for (TruckParameterCycleTime tpmCycleTime : this.truckParameterCycleTimeList) {
			if (tpmCycleTime.getStockPileName().equals(stockpileName)) {
				return tpmCycleTime;
			}
		}
		return null;
	}

	public void reset() {
		blockVariableMapping = new HashMap<Integer, List<String>>();
	}

	public boolean isGlobalMode() {
		return true;
	}

	public boolean hasRemainingTonnage(Block b) {
		return true;
	}

	public double getTonnesWtForBlock(Block b) {
		return Double.valueOf(b.getField(tonnesWtFieldName));
	}
	
	public BigDecimal getUnitValueforBlock(Block b, int unitId, short unitType) {
		if(unitType == 1) { // 1- Field, 2 - Expression
			Field field = getFieldById(unitId);
			try {
				BigDecimal value = new BigDecimal(b.getField(field.getName()));
				return value;
			} catch(Exception e) {
				return null;
			}
		} else {
			Expression expression = getExpressionById(unitId);
			String expressionName = expression.getName().replaceAll("\\s+", "_");
			return b.getComputedField(expressionName);
		}
	}
	
	public BigDecimal getExpressionValueforBlock(Block b, Expression expr) {
		String expressionName = expr.getName().replaceAll("\\s+", "_");
		return b.getComputedField(expressionName);
	}

	public BigDecimal getExpressionValueforBlock(Block b, String exprName) {
		String expressionName = exprName.replaceAll("\\s+", "_");
		return b.getComputedField(expressionName);
	}

	public int getStartYear() {
		return startYear;
	}

	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}

	public int getTimePeriodStart() {
		return timePeriodStart;
	}

	public void setTimePeriodStart(int timePeriodStart) {
		this.timePeriodStart = timePeriodStart;
	}

	public int getTimePeriodEnd() {
		return timePeriodEnd;
	}

	public void setTimePeriodEnd(int timePeriodEnd) {
		this.timePeriodEnd = timePeriodEnd;
	}

	private boolean hasValue(String s) {
		return (s != null && s.trim().length() > 0);
	}

	public Set<Block> getWasteBlocks() {
		return wasteBlocks;
	}

	public void setWasteBlocks(Set<Block> wasteBlocks) {
		this.wasteBlocks = wasteBlocks;
	}

	public Set<Block> getProcessBlocks() {
		return processBlocks;
	}

	public void setProcessBlocks(Set<Block> processBlocks) {
		this.processBlocks = processBlocks;
	}

	public Map<Integer, Block> getBlocks() {
		return blocks;
	}

	public Map<Integer, Pit> getPits() {
		return pits;
	}

	public void setPits(Map<Integer, Pit> pits) {
		this.pits = pits;
	}

	public Map<String, Pit> getPitNameMap() {
		return pitNameMap;
	}

	public void setPitNameMap(Map<String, Pit> pitNameMap) {
		this.pitNameMap = pitNameMap;
	}

	public void setBlocks(Map<Integer, Block> blocks) {
		this.blocks = blocks;
	}

	public Map<Integer, List<String>> getBlockVariableMapping() {
		return blockVariableMapping;
	}

	public void addVariable(Block b, String variable) {
		List<String> variables = blockVariableMapping.get(b.getId());
		if (variables == null) {
			variables = new ArrayList<String>();
			blockVariableMapping.put(b.getId(), variables);
		}
		variables.add(variable);
	}

	public void addWasteBlock(Block b) {
		wasteBlocks.add(b);
	}

	public void addProcessBlock(Block b) {
		processBlocks.add(b);
	}

	public Map<Integer, Integer> getBlockPayloadMapping() {
		return blockPayloadMapping;
	}

	public void setBlockPayloadMapping(Map<Integer, Integer> blockPayloadMapping) {
		this.blockPayloadMapping = blockPayloadMapping;
	}

	public Map<String, BigDecimal> getCycleTimeDataMapping() {
		return cycleTimeDataMapping;
	}

	public void setCycleTimeDataMapping(Map<String, BigDecimal> cycleTimeDataMapping) {
		this.cycleTimeDataMapping = cycleTimeDataMapping;
	}

	public boolean isSpReclaimEnabled() {
		return spReclaimEnabled;
	}

	public Map<String, Boolean> getEquationEnableMap() {
		return equationEnableMap;
	}

	public void setEquationEnableMap(Map<String, Boolean> equationEnableMap) {
		this.equationEnableMap = equationEnableMap;
	}

	public List<PitGroup> getPitGroups() {
		return pitGroups;
	}

	public void setPitGroups(List<PitGroup> pitGroups) {
		this.pitGroups = pitGroups;
	}

	public List<Stockpile> getStockpiles() {
		return stockpiles;
	}

	public void setStockpiles(List<Stockpile> stockpiles) {
		this.stockpiles = stockpiles;
	}

	public List<Dump> getDumps() {
		return dumps;
	}

	public void setDumps(List<Dump> dumps) {
		this.dumps = dumps;
	}

	public List<Expression> getExpressions() {
		return expressions;
	}

	public void setExpressions(List<Expression> expressions) {
		this.expressions = expressions;
	}

	public Scenario getScenario() {
		return scenario;
	}

	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}

	public List<Model> getModels() {
		return models;
	}

	public void setModels(List<Model> models) {
		this.models = models;
	}

	public List<Process> getProcessList() {
		return processList;
	}

	public void setProcessList(List<Process> processList) {
		this.processList = processList;
	}

	public Tree getProcessTree() {
		return processTree;
	}

	public List<ProcessJoin> getProcessJoinList() {
		return processJoinList;
	}

	public void setProcessJoinList(List<ProcessJoin> processJoinList) {
		this.processJoinList = processJoinList;
	}

	public List<ProductJoin> getProductJoinList() {
		return productJoinList;
	}

	public void setProductJoinList(List<ProductJoin> productJoinList) {
		this.productJoinList = productJoinList;
	}

	public List<Product> getProductList() {
		return productList;
	}

	public void setProductList(List<Product> productList) {
		this.productList = productList;
	}

	public List<CycleTimeFieldMapping> getCycleTimeFieldMappings() {
		return cycleTimeFieldMappings;
	}

	public void setCycleTimeFieldMappings(List<CycleTimeFieldMapping> cycleTimeFieldMappings) {
		this.cycleTimeFieldMappings = cycleTimeFieldMappings;
	}

	public BigDecimal getFixedTime() {
		return fixedTime;
	}

	public List<OpexData> getOpexDataList() {
		return opexDataList;
	}

	public void setOpexDataList(List<OpexData> opexDataList) {
		this.opexDataList = opexDataList;
	}

	public List<FixedOpexCost> getFixedOpexCostList() {
		return fixedOpexCostList;
	}

	public void setFixedOpexCostList(List<FixedOpexCost> fixedOpexCostList) {
		this.fixedOpexCostList = fixedOpexCostList;
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

	public void setGradeConstraintDataList(List<GradeConstraintData> gradeConstraintDataList) {
		this.gradeConstraintDataList = gradeConstraintDataList;
	}

	public List<PitBenchConstraintData> getPitBenchConstraintDataList() {
		return pitBenchConstraintDataList;
	}

	public void setPitBenchConstraintDataList(List<PitBenchConstraintData> pitBenchConstraintDataList) {
		this.pitBenchConstraintDataList = pitBenchConstraintDataList;
	}

	public List<PitDependencyData> getPitDependencyDataList() {
		return pitDependencyDataList;
	}

	public void setPitDependencyDataList(List<PitDependencyData> pitDependencyDataList) {
		this.pitDependencyDataList = pitDependencyDataList;
	}

	public List<DumpDependencyData> getDumpDependencyDataList() {
		return dumpDependencyDataList;
	}

	public void setDumpDependencyDataList(List<DumpDependencyData> dumpDependencyDataList) {
		this.dumpDependencyDataList = dumpDependencyDataList;
	}

	public List<CapexData> getCapexDataList() {
		return capexDataList;
	}

	public void setCapexDataList(List<CapexData> capexDataList) {
		this.capexDataList = capexDataList;
	}

	public String getPitFieldName() {
		return pitFieldName;
	}

	public int getProjectId() {
		return projectId;
	}

	public int getScenarioId() {
		return scenarioId;
	}
}