package com.org.gnos.equation;

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
import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.CycleTimeMappingData;
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.PitGroup;
import com.org.gnos.db.model.Stockpile;
import com.org.gnos.db.model.TruckParameterData;

public class InstanceData {
	
	private Set<Block> wasteBlocks = new HashSet<Block>();
	private Set<Block> processBlocks = new HashSet<Block>();
	private Map<Integer, Block> blocks = new LinkedHashMap<Integer,Block>();
	private Map<Integer, Pit> pits = new LinkedHashMap<Integer,Pit>();
	private Map<Integer, List<String>> blockVariableMapping = new HashMap<Integer, List<String>>();
	private Map<Integer, List<Dump>> pitDumpMapping;
	private Map<Integer, Stockpile> pitStockpileMapping;
	private Map<Integer, Integer> blockPayloadMapping = new HashMap<Integer, Integer>();
	private Map<String, Integer> cycleTimeDataMapping  = new HashMap<String, Integer>();
	private String pitFieldName;
	private String benchFieldName;
	
	private ProjectConfigutration projectConfiguration;
	
	public InstanceData() {
		projectConfiguration = ProjectConfigutration.getInstance();
		pitFieldName = projectConfiguration.getRequiredFieldMapping().get("pit_name");
		benchFieldName = projectConfiguration.getRequiredFieldMapping().get("bench_rl");
		loadBlocks();
		parseDumpData();
		parseStockpileData();
		loadBlockPayloadMapping();
		loadCycleTimeDataMapping();
	}

	private void loadBlocks() {
		String sql = "select a.*, b.* from gnos_data_"+projectConfiguration.getProjectId()+" a, gnos_computed_data_"+projectConfiguration.getProjectId()+" b where a.id = b.row_id";
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
	
	private void parseStockpileData() {
		this.pitStockpileMapping = new HashMap<Integer, Stockpile>();
		List<Stockpile> stockpileListData = projectConfiguration.getStockPileList();
		for(Stockpile sp: stockpileListData){
			if(sp.getMappingType() == 0) {
				this.pitStockpileMapping.put(sp.getAssociatedPit().getPitNumber(), sp);
			} else {
				Set<Integer> pits = flattenPitGroup(sp.getAssociatedPitGroup());
				for(Integer pitNo: pits) {
					this.pitStockpileMapping.put(pitNo, sp);
				}
			}
			
		}
		
	}

	
	private void parseDumpData() {
		this.pitDumpMapping = new HashMap<Integer, List<Dump>>();
		List<Dump> dumpData = projectConfiguration.getDumpList();
		for(Dump dump: dumpData){
			if(dump.getMappingType() == 0){
				int pitNo = dump.getAssociatedPit().getPitNumber();
				addDumpToPit(pitNo, dump);
			} else {
				Set<Integer> pits = flattenPitGroup(dump.getAssociatedPitGroup());
				for(Integer pitNo: pits) {
					addDumpToPit(pitNo, dump);
				}
			}
			
		}
	}
	private void addDumpToPit(int pitNo, Dump dump) {
		List<Dump> dumps = this.pitDumpMapping.get(pitNo);
		if(dumps == null){
			dumps = new ArrayList<Dump>();
			this.pitDumpMapping.put(pitNo, dumps);
		}
		dumps.add(dump);
	}
	public Set<Integer> flattenPitGroup(PitGroup pg) {
		 Set<Integer> pits = new HashSet<Integer>();
		 for(com.org.gnos.db.model.Pit childPit: pg.getListChildPits()){
			 pits.add(childPit.getPitNumber());
		 }
		 for(PitGroup childGroup: pg.getListChildPitGroups()) {
			 pits.addAll(flattenPitGroup(childGroup));
		 }
		 
		 return pits;
	}
	
	private List<Block> findBlocks(String condition) {
		List<Block> blocks = new ArrayList<Block>();
		String sql = "select id from gnos_data_"+projectConfiguration.getProjectId() ;
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
		TruckParameterData truckparamdata = projectConfiguration.getTruckParameterData();
		Map<String, Integer> payloadMap = truckparamdata.getMaterialPayloadMap();
		Set<String> exprnames = payloadMap.keySet();
		for(String exprname: exprnames){
			Expression expr = projectConfiguration.getExpressionByName(exprname);
			if(expr != null){
				String condition = expr.getFilter();
				List<Block> blocks = findBlocks(condition);
				for(Block b: blocks){
					blockPayloadMapping.put(b.getId(), payloadMap.get(exprname));
				}				
			}
		}
		
	}
	
	private void loadCycleTimeDataMapping(){
		
		CycleTimeMappingData ctd = projectConfiguration.getCycleTimeData();
		Map<String, String> fixedFields = ctd.getFixedFieldMap();
		String pitNameAlias = fixedFields.get("Pit");
		String benchAlias = fixedFields.get("Bench");
		if(pitNameAlias == null || benchAlias == null) return;
		int fixedTime = projectConfiguration.getTruckParameterData().getFixedTime();
		Map<String, String> dumpFields = ctd.getDumpFieldMap();
		Map<String, String> processFields = ctd.getChildProcessFieldMap();
		Map<String, String> stockpileFields = ctd.getStockpileFieldMap();
		Map<String, String> otherFields = new HashMap<String, String>();
		otherFields.putAll(dumpFields);
		otherFields.putAll(stockpileFields);
		otherFields.putAll(processFields);
		
		String sql = "select * from gnos_cycle_time_data_"+ projectConfiguration.getProjectId();		

		try (
				Connection conn = DBManager.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
			)
		
		{
			while (rs.next()) {
				String pitName = rs.getString(pitNameAlias);
				int benchName = rs.getInt(benchAlias);
				com.org.gnos.db.model.Pit pit = projectConfiguration.getPitfromPitName(pitName);
				if(pit == null) continue;
				int pitNo = pit.getPitNumber();
				Pit corePit = pits.get(pitNo);
				Bench b = corePit.getBench(String.valueOf(benchName));
				if(b == null) continue;
				Set<String> keys = otherFields.keySet();
				for(String key: keys){
					String columnName = otherFields.get(key);
					try{
						int data = rs.getInt(columnName);
						String dataKey = pit.getPitNumber()+":"+b.getBenchNo()+":"+key;
						cycleTimeDataMapping.put(dataKey, data + fixedTime);
					} catch(SQLException e) {
						System.err.println(e.getMessage());
					}
				}
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
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

	public Map<Integer, List<Dump>> getPitDumpMapping() {
		return pitDumpMapping;
	}

	public Map<Integer, Stockpile> getPitStockpileMapping() {
		return pitStockpileMapping;
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

	public Map<String, Integer> getCycleTimeDataMapping() {
		return cycleTimeDataMapping;
	}

	public void setCycleTimeDataMapping(Map<String, Integer> cycleTimeDataMapping) {
		this.cycleTimeDataMapping = cycleTimeDataMapping;
	}
}