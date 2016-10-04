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
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.PitGroup;
import com.org.gnos.db.model.Stockpile;

public class InstanceData {
	
	private Set<Block> wasteBlocks = new HashSet<Block>();
	private Set<Block> processBlocks = new HashSet<Block>();
	private Map<Integer, Block> blocks = new LinkedHashMap<Integer,Block>();
	private Map<Integer, Pit> pits = new LinkedHashMap<Integer,Pit>();
	private Map<Integer, List<String>> blockVariableMapping = new HashMap<Integer, List<String>>();
	private Map<Integer, List<Integer>> pitDumpMapping;
	private Map<Integer, Integer> pitStockpileMapping;
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
		this.pitStockpileMapping = new HashMap<Integer, Integer>();
		List<Stockpile> stockpileListData = projectConfiguration.getStockPileList();
		for(Stockpile sp: stockpileListData){
			Set<Integer> pits = flattenPitGroup(sp.getAssociatedPitGroup());
			for(Integer pitNo: pits) {
				this.pitStockpileMapping.put(pitNo, sp.getStockpileNumber());
			}
		}
		
	}

	private void parseDumpData() {
		this.pitDumpMapping = new HashMap<Integer, List<Integer>>();
		List<Dump> dumpData = projectConfiguration.getDumpList();
		for(Dump dump: dumpData){
			Set<Integer> pits = flattenPitGroup(dump.getAssociatedPitGroup());
			for(Integer pitNo: pits) {
				List<Integer> dumps = this.pitDumpMapping.get(pitNo);
				if(dumps == null){
					dumps = new ArrayList<Integer>();
					this.pitDumpMapping.put(pitNo, dumps);
				}
				dumps.add(dump.getDumpNumber());
			}
		}
	}
	
	private Set<Integer> flattenPitGroup(PitGroup pg) {
		 Set<Integer> pits = new HashSet<Integer>();
		 for(com.org.gnos.db.model.Pit childPit: pg.getListChildPits()){
			 pits.add(childPit.getPitNumber());
		 }
		 for(PitGroup childGroup: pg.getListChildPitGroups()) {
			 pits.addAll(flattenPitGroup(childGroup));
		 }
		 
		 return pits;
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

	public Map<Integer, List<Integer>> getPitDumpMapping() {
		return pitDumpMapping;
	}

	public Map<Integer, Integer> getPitStockpileMapping() {
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
	
	
}