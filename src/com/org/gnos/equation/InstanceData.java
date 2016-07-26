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

public class InstanceData {
	
	private Set<Block> wasteBlocks = new HashSet<Block>();
	private Set<Block> processBlocks = new HashSet<Block>();
	private Map<Integer, Block> blocks = new LinkedHashMap<Integer,Block>();
	private Map<Integer, Pit> pits = new LinkedHashMap<Integer,Pit>();
	private Map<Integer, List<String>> blockVariableMapping = new HashMap<Integer, List<String>>();
	
	private ProjectConfigutration projectConfiguration;
	
	public InstanceData() {
		projectConfiguration = ProjectConfigutration.getInstance();
		loadBlocks();
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
					pits.put(block.getPitNo(), pit);
				}
				Bench bench = pit.getBench(block.getBenchNo());
				if(bench == null ){
					bench = new Bench();
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
	
	
}