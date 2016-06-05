package com.org.gnos.services;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.core.Block;
import com.org.gnos.core.Node;
import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.core.ScenarioConfigutration;
import com.org.gnos.core.Tree;
import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.FixedOpexCost;
import com.org.gnos.db.model.Model;
import com.org.gnos.db.model.OpexData;
import com.org.gnos.db.model.Pit;
import com.org.gnos.db.model.PitGroup;
import com.org.gnos.db.model.Process;
import com.org.gnos.db.model.Stockpile;

public class ObjectiveFunctionEquationGenerator {

	static final int BYTES_PER_LINE = 128;
	
	private BufferedOutputStream output;
	private ProjectConfigutration projectConfiguration;
	private ScenarioConfigutration scenarioConfigutration;
	private Map<Integer, List<Integer>> pitDumpMapping;
	private Map<Integer, Integer> pitStockpileMapping;
	
	private Set<Integer> processedBlocks;
	private Tree processTree;
	private int bytesWritten = 0;
	private float discount_rate = 0; //this has to be made an input variable later
	public void generate() {
		projectConfiguration = ProjectConfigutration.getInstance();
		scenarioConfigutration = ScenarioConfigutration.getInstance();
		processedBlocks = new HashSet<Integer>();
		

		int bufferSize = 8 * 1024;
		try {
			discount_rate = scenarioConfigutration.getDiscount()/100;
			System.out.println("Discount :"+discount_rate);
			output = new BufferedOutputStream(new FileOutputStream("output.txt"), bufferSize);
			bytesWritten = 0;
			buildProcessBlockVariables();
			buildWasteBlockVariables();
			output.flush();
			output.close();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	private void buildProcessBlockVariables() {
		List<Process> porcesses = projectConfiguration.getProcessList();
		processTree = projectConfiguration.getProcessTree();
		Set<Block> processBlocks = new HashSet<Block>();
		for(Process process: porcesses) {
			System.out.println("Equation Generation: process name - "+process.getModel().getName());
			String condition = buildCondition(process) ;
			List<Block> blocks = findBlocks(condition);
			process.setBlocks(blocks);
			processBlocks.addAll(blocks);
			buildProcessVariables(process, blocks, process.getProcessNo());		
		}
		buildStockpileVariables(processBlocks);
	}
	
	private void buildProcessVariables(Process process, List<Block> blocks, int processNumber) {
		FixedOpexCost[] fixedOpexCost = scenarioConfigutration.getFixedCost();
		Map<Integer, Float> oreMiningCostMap = fixedOpexCost[0].getCostData();
		Set<Integer> keys = oreMiningCostMap.keySet();
		int count = 1;
		for(int year: keys){
			float miningcost = oreMiningCostMap.get(year);
			
			for(Block block: blocks) {
				float processValue = getProcessValue(block, process.getModel(), year);
				float value = processValue - miningcost;
				value = (float) (value * (1 / Math.pow ((1 + discount_rate), count)));
				String eq = " "+value+"p"+block.getPitNo()+"x"+block.getBlockNo()+"p"+processNumber+"t"+count;
				if(value > 0){
					eq = " +"+eq;
				} 
				write(eq);
				
			}
			count ++;
		}
	}
	
	private void buildStockpileVariables(Set<Block> blocks) {
		FixedOpexCost[] fixedOpexCost = scenarioConfigutration.getFixedCost();
		if(fixedOpexCost == null || fixedOpexCost.length < 3) return;
		parseStockpileData();
		Map<Integer, Float> oreMiningCostMap = fixedOpexCost[0].getCostData();
		Map<Integer, Float> stockPilingCostMap = fixedOpexCost[2].getCostData();
		Set<Integer> keys = stockPilingCostMap.keySet();
		int count = 1;
		for(int year: keys){
			float cost = stockPilingCostMap.get(year)+oreMiningCostMap.get(year);			
			for(Block block: blocks) {
				Integer stockpileNumber = this.pitStockpileMapping.get(block.getPitNo());
				if(stockpileNumber == null) continue;
				String eq = " -"+cost+"p"+block.getPitNo()+"x"+block.getBlockNo()+"s"+stockpileNumber+"t"+count;

				write(eq);
			}
			count++;
		}
	}
	
	private void buildWasteBlockVariables() throws IOException {
		List<Block> wasteblocks = findWasteBlocks();
		FixedOpexCost[] fixedOpexCost = scenarioConfigutration.getFixedCost();
		if(fixedOpexCost == null || fixedOpexCost.length < 2) return;
		parseDumpData();
		Map<Integer, Float> wasteMiningCostMap = fixedOpexCost[1].getCostData();
		Set<Integer> keys = wasteMiningCostMap.keySet();

		int count = 1;	
		for(int year: keys){
			float cost = wasteMiningCostMap.get(year);		
			for(Block block: wasteblocks) {
				List<Integer> dumps = pitDumpMapping.get(block.getPitNo());
				if(dumps == null) continue;
				for(Integer dumpNo: dumps){
					String eq = " -"+cost+"p"+block.getPitNo()+"x"+block.getBlockNo()+"w"+dumpNo+"t"+count;
					write(eq);
				}
				
			}
			count++;
		}
	}
	
	private String buildCondition(Process process) {
		String condition = "";

		Model model = process.getModel();
		if(hasValue(model.getCondition())){
			condition = model.getCondition();
		}
		if(hasValue(model.getExpression().getFilter())) {
			if(hasValue(condition)) {
				condition = condition + " AND "+ model.getExpression().getFilter();
			} else {
				condition =  model.getExpression().getFilter();
			}
		}
		boolean continueLoop = true;
		Node currNode = processTree.getNodeByName(model.getName());
		do{
			Node parent = currNode.getParent();
			if(parent == null) {
				continueLoop = false;
			} else {
				Model pModel = parent.getData();
				if(hasValue(pModel.getCondition())){
					condition = pModel.getCondition();
				}
				if(pModel.getExpression() != null && hasValue(pModel.getExpression().getFilter())) {
					if(hasValue(condition)) {
						condition = condition + " AND "+ pModel.getExpression().getFilter();
					} else {
						condition =  pModel.getExpression().getFilter();
					}
				}
			}
			currNode = parent;
		}
		while(continueLoop);
		System.out.println("Condition :"+condition);
		return condition;
	}
	
	private List<Block> findBlocks(String condition) {
		List<Block> blocks = new ArrayList<Block>();
		String sql = "select b.* from gnos_data_"+projectConfiguration.getProjectId()+" a, gnos_computed_data_"+projectConfiguration.getProjectId()+" b where a.id = b.row_id";
		if(hasValue(condition)) {
			sql += " AND "+condition;
		}
		
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
				for(int i=1; i<=columnCount; i++){
					block.addField(md.getColumnName(i), rs.getString(i));
				}
				blocks.add(block);
				processedBlocks.add(block.getBlockNo());
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return blocks;
	}
	
	private List<Block> findWasteBlocks() {
		List<Block> blocks = new ArrayList<Block>();
		String sql = "select * from gnos_computed_data_"+projectConfiguration.getProjectId();
		
		try (
				Connection conn = DBManager.getConnection();
				Statement stmt  = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
			)
		{
			ResultSetMetaData md = rs.getMetaData();
			int columnCount = md.getColumnCount();
			while(rs.next()){
				int blockNo = rs.getInt("block_no");
				if(processedBlocks.contains(blockNo)) continue;

				Block block = new Block();
				block.setId(rs.getInt("row_id"));
				block.setBlockNo(blockNo);
				for(int i=1; i<=columnCount; i++){
					block.addField(md.getColumnName(i), rs.getString(i));
				}
				blocks.add(block);
				processedBlocks.add(block.getBlockNo());
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return blocks;
	}
	
	private float getProcessValue(Block b, Model model, int year) {
		float value = 0;
		if(model == null) return value;
		float revenue = 0;
		float pcost = 0;
		List<OpexData> opexDataList = scenarioConfigutration.getOpexDataList();
		for(OpexData opexData: opexDataList) {
			if(opexData.getModel().getId() == model.getId()){
				if(opexData.isRevenue()){
					String expressionName = opexData.getExpression().getName().replaceAll("\\s+","_");
					float expr_value = b.getRatioField(expressionName);
					revenue = revenue + expr_value * opexData.getCostData().get(year);
				} else {
					pcost = pcost + opexData.getCostData().get(year);
				}
			}
		}
		value = revenue - pcost;
		return value;
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
		 for(Pit childPit: pg.getListChildPits()){
			 pits.add(childPit.getPitNumber());
		 }
		 for(PitGroup childGroup: pg.getListChildPitGroups()) {
			 pits.addAll(flattenPitGroup(childGroup));
		 }
		 
		 return pits;
	}
	private boolean hasValue(String s) {
		return (s !=null && s.trim().length() >0);
	}
	
	private int getStockPileForPit(int pitNo){
		return pitNo;
	}
	private int getDumpForPit(int pitNo){
		return pitNo;
	}
	
	private void write(String s) {

		try {
			byte[] bytes = s.getBytes();
			if(bytes.length + bytesWritten > BYTES_PER_LINE){
				output.write("\r\n".getBytes());
				output.flush();
				bytesWritten = 0;
			}
			output.write(bytes);
			bytesWritten = bytesWritten + bytes.length;
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private class CostRevenueData {
		int year;
		float cost;
		float revenue;
		String expressionName;
	}
	
	
}
