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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.core.Block;
import com.org.gnos.core.Node;
import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.core.ScenarioConfigutration;
import com.org.gnos.core.Tree;
import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.FixedOpexCost;
import com.org.gnos.db.model.Model;
import com.org.gnos.db.model.OpexData;

public class ObjectiveFunctionEquationGenerator {

	static final int BYTES_PER_LINE = 128;
	
	private BufferedOutputStream output;
	private ProjectConfigutration projectConfiguration;
	private ScenarioConfigutration scenarioConfigutration;
	private Map<Integer, List<CostRevenueData>> modelOpexDataMapping;
	
	private Set<Integer> processedBlocks;
	private int bytesWritten = 0;
	private float discount_rate = 0; //this has to be made an input variable later
	public void generate() {
		projectConfiguration = ProjectConfigutration.getInstance();
		scenarioConfigutration = ScenarioConfigutration.getInstance();
		processedBlocks = new HashSet<Integer>();

		int bufferSize = 8 * 1024;
		try {
			discount_rate = scenarioConfigutration.getDiscount();
			output = new BufferedOutputStream(new FileOutputStream("output.txt"), bufferSize);
			bytesWritten = 0;
			//parseOpexData();
			buildProcessBlockVariables();
			buildWasteBlockVariables();
			output.flush();
			output.close();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	private void buildProcessBlockVariables() {
		Tree processtree = projectConfiguration.getProcessTree();
		List<Node> porcesses = processtree.getLeafNodes();
		Set<Block> processBlocks = new HashSet<Block>();
		int processNumber = 1;
		for(Node process: porcesses) {
			System.out.println("Equation Generation: process name - "+process.getIdentifier());
			String condition = buildCondition(process) ;
			List<Block> blocks = findBlocks(condition);
			processBlocks.addAll(blocks);
			buildProcessVariables(process, blocks, processNumber);
			processNumber++;
			
		}
		buildStockpileVariables(processBlocks);
	}
	
	private void buildProcessVariables(Node process, List<Block> blocks, int processNumber) {
		FixedOpexCost[] fixedOpexCost = scenarioConfigutration.getFixedCost();
		Map<Integer, Float> oreMiningCostMap = fixedOpexCost[0].getCostData();
		Set<Integer> keys = oreMiningCostMap.keySet();
		int count = 1;
		for(int year: keys){
			float miningcost = oreMiningCostMap.get(year);
			
			for(Block block: blocks) {
				float processValue = getProcessValue(block, process.getData(), year);
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
		Map<Integer, Float> oreMiningCostMap = fixedOpexCost[0].getCostData();
		Map<Integer, Float> stockPilingCostMap = fixedOpexCost[2].getCostData();
		Set<Integer> keys = stockPilingCostMap.keySet();
		int count = 1;
		for(int year: keys){
			float cost = stockPilingCostMap.get(year)+oreMiningCostMap.get(year);			
			for(Block block: blocks) {			
				String eq = " -"+cost+"p"+block.getPitNo()+"x"+block.getBlockNo()+"s"+getStockPileForPit(block.getPitNo())+"t"+count;

				write(eq);
			}
			count++;
		}
	}
	
	private void buildWasteBlockVariables() throws IOException {
		List<Block> wasteblocks = findWasteBlocks();
		FixedOpexCost[] fixedOpexCost = scenarioConfigutration.getFixedCost();
		Map<Integer, Float> wasteMiningCostMap = fixedOpexCost[1].getCostData();
		Set<Integer> keys = wasteMiningCostMap.keySet();

		int count = 1;	
		for(int year: keys){
			float cost = wasteMiningCostMap.get(year);		
			for(Block block: wasteblocks) {
				String eq = " -"+cost+"p"+block.getPitNo()+"x"+block.getBlockNo()+"w"+getDumpForPit(block.getPitNo())+"t"+count;
				write(eq);
			}
			count++;
		}
	}
	
	private String buildCondition(Node node) {
		String condition = "";

		Model model = node.getData();
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
		Node currNode = node;
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
	
	private boolean hasValue(String s) {
		return (s !=null && s.trim().length() >0);
	}
	
	private int getStockPileForPit(int pitNo){
		return pitNo;
	}
	private int getDumpForPit(int pitNo){
		return pitNo;
	}
	
	private void parseOpexData () {
		List<OpexData> opexDataList = scenarioConfigutration.getOpexDataList();
		modelOpexDataMapping = new LinkedHashMap<Integer, List<CostRevenueData>>();
		
		for(OpexData opexData: opexDataList) {
			if(!opexData.isInUse()) continue;
			int modelId = opexData.getModel().getId();
			List<CostRevenueData> yearCostData = modelOpexDataMapping.get(modelId);
			if(yearCostData == null) {
				yearCostData = new ArrayList<CostRevenueData>();
				modelOpexDataMapping.put(modelId, yearCostData);
					
				Map<Integer, Float> costData = opexData.getCostData();
				Set<Integer> keys = costData.keySet();
				Iterator<Integer> it = keys.iterator();
				while(it.hasNext()){
					CostRevenueData crd = new CostRevenueData();
					crd.year = it.next();
				
					if(opexData.isRevenue()) {
						crd.revenue = costData.get(crd.year);
						crd.expressionName = opexData.getExpression().getName().replaceAll("\\s+","_");
					} else {
						crd.cost = costData.get(crd.year);
					}
					yearCostData.add(crd);
				}
			} else {
				Map<Integer, Float> costData = opexData.getCostData();
				Set<Integer> keys = costData.keySet();
				Iterator<Integer> it = keys.iterator();
				while(it.hasNext()){
					int year = it.next();
					for(CostRevenueData crd: yearCostData){
						if(crd.year == year) {
							if(opexData.isRevenue()) {
								crd.revenue = costData.get(crd.year);
								crd.expressionName = opexData.getExpression().getName().replaceAll("\\s+","_");
							} else {
								crd.cost = costData.get(crd.year);
							}
							break;
						}
					}				
				}
			}
		}
		
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
