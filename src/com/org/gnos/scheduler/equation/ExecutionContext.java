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
import com.org.gnos.core.Pit;
import com.org.gnos.core.ScenarioConfigutration;
import com.org.gnos.db.DBManager;
import com.org.gnos.db.dao.RequiredFieldDAO;
import com.org.gnos.db.dao.ScenarioDAO;
import com.org.gnos.db.model.CycleTimeMappingData;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.PitGroup;
import com.org.gnos.db.model.RequiredField;
import com.org.gnos.db.model.Scenario;
import com.org.gnos.db.model.Stockpile;
import com.org.gnos.db.model.TruckParameterData;

public class ExecutionContext {
	
	private Set<Block> wasteBlocks = new HashSet<Block>();
	private Set<Block> processBlocks = new HashSet<Block>();
	private Map<Integer, Block> blocks = new LinkedHashMap<Integer,Block>();
	private Map<Integer, Pit> pits = new LinkedHashMap<Integer,Pit>();
	private Map<Integer, List<String>> blockVariableMapping = new HashMap<Integer, List<String>>();
	private Map<Integer, Integer> blockPayloadMapping = new HashMap<Integer, Integer>();
	private Map<String, BigDecimal> cycleTimeDataMapping  = new HashMap<String, BigDecimal>();
	private String pitFieldName;
	private String benchFieldName;
	protected String tonnesWtFieldName;
	
	int projectId;
	int scenarioId;
	
	int startYear;
	int timePeriodStart;
	int timePeriodEnd;
	
	private boolean spReclaimEnabled = true;
	
	private Map<String, Boolean> equationgEnableMap;

	public ExecutionContext() {
		
		loadRequiredFields();
		loadScenario();
		loadConfiguration();
		loadBlocks();
		loadBlockPayloadMapping();
		loadCycleTimeDataMapping();
	}
	
	private void loadRequiredFields() {
		List<RequiredField> requiredFields = new RequiredFieldDAO().getAll(projectId);
		for(RequiredField requiredField: requiredFields) {
			switch(requiredField.getFieldName()) {
				case "pit_name": pitFieldName = requiredField.getMappedFieldname();
								 break;
				case "bench_rl": benchFieldName = requiredField.getMappedFieldname();
								 break;
				case "tonnes_wt": tonnesWtFieldName = requiredField.getMappedFieldname();
								 break;
			}
		}
		
	}

	private void loadScenario() {
		Scenario scenario = new ScenarioDAO().get(scenarioId);
		startYear = scenario.getStartYear();
		timePeriodStart = 1;
		timePeriodEnd = scenario.getTimePeriod();
	}

	private void loadConfiguration() {
		String sql = "select reclaim from scenario_config where scenario_id ="+ ScenarioConfigutration.getInstance().getScenarioId();
		try (
				Connection conn = DBManager.getConnection();
				Statement stmt  = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);			
			)
		{
			while(rs.next()){
				int reclaim = rs.getInt("reclaim");
				if(reclaim == 0){
					spReclaimEnabled = false;
				}
			}
			
		} catch (SQLException e) {
			System.err.println("Failed to load blocks "+e.getMessage());
		}
	}
	private void loadBlocks() {
		String sql = "select a.*, b.* from gnos_data_"+projectId+" a, gnos_computed_data_"+projectId+" b where a.id = b.row_id";
		try (
				Connection conn = DBManager.getConnection();
				Statement stmt  = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);			
			)
		{
			ResultSetMetaData md = rs.getMetaData();
			int columnCount = md.getColumnCount();
			while(rs.next()){
				
				Block block = new Block();
				block.setId(rs.getInt("row_id"));
				block.setBlockNo(rs.getInt("block_no"));
				boolean computedDataField = false;
				for(int i=1; i<= columnCount; i++){
					String columnName = md.getColumnName(i);
					if(!computedDataField && columnName.equalsIgnoreCase("row_id")){
						computedDataField = true;
					}
					if(!computedDataField) {
						block.addField(columnName, rs.getString(i));
					} else {
						block.addComputedField(columnName, rs.getString(i));
					}
				}
				Pit pit = pits.get(block.getPitNo());
				if( pit == null ){
					pit = new Pit();
					pit.setPitNo(block.getPitNo());
					pit.setPitName(block.getField(pitFieldName));
					pits.put(block.getPitNo(), pit);
				}
				Bench bench = pit.getBench(block.getBenchNo());
				if(bench == null ){
					bench = new Bench();
					bench.setBenchName(block.getField(benchFieldName));
					bench.setBenchNo(block.getBenchNo());
				}
				bench.addBlock(block);
				pit.addBench(bench);
				blocks.put(block.getId(), block);
			}
			
		} catch (SQLException e) {
			System.err.println("Failed to load blocks "+e.getMessage());
		}
	}

	public Set<String> flattenPitGroup(PitGroup pg) {
		 Set<String> pits = new HashSet<String>();
		 pits.addAll(pg.getListChildPits());
		 for(String childGroup: pg.getListChildPitGroups()) {
			 pits.addAll(flattenPitGroup(getPitGroupfromName(childGroup)));
		 }
		 
		 return pits;
	}
	
	private PitGroup getPitGroupfromName(String childGroup) {
		// TODO Auto-generated method stub
		return null;
	}

	private List<Block> findBlocks(String condition) {
		List<Block> blocks = new ArrayList<Block>();
		String sql = "select id from gnos_data_"+projectId ;
		if(hasValue(condition)) {
			sql += " where "+condition;
		}
		
		try (
				Connection conn = DBManager.getConnection();
				Statement stmt  = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);			
			)
		{
			while(rs.next()){
				int id = rs.getInt("id");
				Block block = getBlocks().get(id);
				blocks.add(block);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return blocks;
	}
	
	private void loadBlockPayloadMapping() {
		TruckParameterData truckparamdata = getTruckParameterData();
		Map<String, Integer> payloadMap = truckparamdata.getMaterialPayloadMap();
		Set<String> exprnames = payloadMap.keySet();
		for(String exprname: exprnames){
			Expression expr = getExpressionByName(exprname);
			if(expr != null){
				String condition = expr.getFilter();
				List<Block> blocks = findBlocks(condition);
				for(Block b: blocks){
					blockPayloadMapping.put(b.getId(), payloadMap.get(exprname));
				}				
			}
		}
		
	}
	
	private Expression getExpressionByName(String exprname) {

		return null;
	}

	private void loadCycleTimeDataMapping(){
		
		CycleTimeMappingData ctd = getCycleTimeData();
		Map<String, String> fixedFields = ctd.getFixedFieldMap();
		String pitNameAlias = fixedFields.get("Pit");
		String benchAlias = fixedFields.get("Bench");
		if(pitNameAlias == null || benchAlias == null) return;
		BigDecimal fixedTime = getTruckParameterData().getFixedTime();
		Map<String, String> dumpFields = ctd.getDumpFieldMap();
		Map<String, String> processFields = ctd.getChildProcessFieldMap();
		Map<String, String> stockpileFields = ctd.getStockpileFieldMap();
		Map<String, String> otherFields = new HashMap<String, String>();
		otherFields.putAll(dumpFields);
		otherFields.putAll(stockpileFields);
		otherFields.putAll(processFields);
		
		String sql = "select * from gnos_cycle_time_data_"+ projectId;		

		try (
				Connection conn = DBManager.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
			)
		
		{
			while (rs.next()) {
				String pitName = rs.getString(pitNameAlias);
				int benchName = rs.getInt(benchAlias);
				com.org.gnos.db.model.Pit pit = getPitfromPitName(pitName);
				if(pit == null) continue;
				int pitNo = pit.getPitNumber();
				Pit corePit = pits.get(pitNo);
				Bench b = corePit.getBench(String.valueOf(benchName));
				if(b == null) continue;
				Set<String> keys = otherFields.keySet();
				for(String key: keys){
					String columnName = otherFields.get(key);
					try{
						BigDecimal data = rs.getBigDecimal(columnName);
						String dataKey = pit.getPitNumber()+":"+b.getBenchNo()+":"+key;
						cycleTimeDataMapping.put(dataKey, data.add(fixedTime));
					} catch(SQLException e) {
						System.err.println(e.getMessage());
					}
				}
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private com.org.gnos.db.model.Pit getPitfromPitName(String pitName) {
		// TODO Auto-generated method stub
		return null;
	}

	private TruckParameterData getTruckParameterData() {
		// TODO Auto-generated method stub
		return null;
	}

	private CycleTimeMappingData getCycleTimeData() {
		// TODO Auto-generated method stub
		return null;
	}

	public Stockpile getStockpileFromNo(int spNo){;
		List<Stockpile> stockpiles = getStockPileList();
		for(Stockpile sp: stockpiles){
			if(sp.getStockpileNumber() == spNo) {
				return sp;
			}
		}
		return null;
	}
	
	private List<Stockpile> getStockPileList() {
		return null;
	}

	public void reset() {
		blockVariableMapping = new HashMap<Integer, List<String>>();
	}
	
	public boolean hasRemainingTonnage(Block b){
		 return true;
	}
	
	public double getTonnesWtForBlock(Block b){
		return Double.valueOf(b.getField(tonnesWtFieldName));
	}
	
	public BigDecimal getExpressionValueforBlock(Block b, Expression expr) {
		String expressionName = expr.getName().replaceAll("\\s+","_");			
		return b.getComputedField(expressionName);		
	}
	
	public BigDecimal getExpressionValueforBlock(Block b, String exprName) {
		String expressionName = exprName.replaceAll("\\s+","_");			
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
		return (s !=null && s.trim().length() >0);
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

	public void setBlocks(Map<Integer, Block> blocks) {
		this.blocks = blocks;
	}

	public Map<Integer, List<String>> getBlockVariableMapping() {
		return blockVariableMapping;
	}

	public void addVariable(Block b, String variable){
		List<String> variables = blockVariableMapping.get(b.getId());
		if(variables == null){
			variables = new ArrayList<String>();
			blockVariableMapping.put(b.getId(), variables);
		}
		variables.add(variable);
	}
	
	public void addWasteBlock(Block b){
		wasteBlocks.add(b);
	}
	public void addProcessBlock(Block b){
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
	
	public Map<String, Boolean> getEquationgEnableMap() {
		return equationgEnableMap;
	}

	public void setEquationgEnableMap(Map<String, Boolean> equationgEnableMap) {
		this.equationgEnableMap = equationgEnableMap;
	}
	
	public boolean isGlobalMode() {
		return true;
	}
}