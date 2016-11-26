package com.org.gnos.equation;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
import com.org.gnos.db.model.CapexData;
import com.org.gnos.db.model.CapexInstance;
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.FixedOpexCost;
import com.org.gnos.db.model.Model;
import com.org.gnos.db.model.OpexData;
import com.org.gnos.db.model.Pit;
import com.org.gnos.db.model.Process;
import com.org.gnos.db.model.Stockpile;
import com.org.gnos.db.model.TruckParameterCycleTime;

public class ObjectiveFunctionEquationGenerator extends EquationGenerator{
	
	private Tree processTree;
	private int bytesWritten = 0;
	private float discount_rate = 0; //this has to be made an input variable later
	
	private Set<Integer> processedBlocks;
	
	public ObjectiveFunctionEquationGenerator(InstanceData data) {
		super(data);
	}
	
	@Override
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
			buildStockpileVariables();
			buildWasteBlockVariables();
			buildCapexVariables();
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
		serviceInstanceData.setProcessBlocks(processBlocks);

	}
	
	private void buildProcessVariables(Process process, List<Block> blocks, int processNumber) {
		FixedOpexCost[] fixedOpexCost = scenarioConfigutration.getFixedCost();
		if(fixedOpexCost == null || fixedOpexCost.length < 5) return;
		Map<Integer, BigDecimal> oreMiningCostMap = fixedOpexCost[0].getCostData();
		Map<Integer, BigDecimal> truckHourCostMap = fixedOpexCost[4].getCostData();
		Set<Integer> keys = oreMiningCostMap.keySet();
		int count = 1;
		for(int year: keys){
			BigDecimal miningcost = oreMiningCostMap.get(year) ;
			BigDecimal truckHourCost = truckHourCostMap.get(year);
			for(Block block: blocks) {
				BigDecimal cost = new BigDecimal(0);
				cost = cost.add(miningcost);
				processedBlocks.add(block.getId());
				block.addProcess(process);
				int payload = serviceInstanceData.getBlockPayloadMapping().get(block.getId());
				if(payload > 0) {
					Integer ct = serviceInstanceData.getCycleTimeDataMapping().get(block.getPitNo()+":"+block.getBenchNo()+":"+process.getModel().getName());
					if(ct != null) {
						double th_ratio =  (double)ct /( payload* 60);
						cost = cost.add(truckHourCost.multiply(new BigDecimal(th_ratio)));
					}
				}
				BigDecimal processValue = getProcessValue(block, process.getModel(), year);
				BigDecimal value = processValue.subtract(cost);
				value = (value.multiply(new BigDecimal(1 / Math.pow ((1 + discount_rate), count))));
				String variable = "p"+block.getPitNo()+"x"+block.getBlockNo()+"p"+processNumber+"t"+count;
				String eq = " "+formatDecimalValue(value)+variable;
				if(value.doubleValue() > 0){
					eq = " +"+eq;
				} 
				write(eq);
				
				serviceInstanceData.addVariable(block, variable);
			}
			count ++;
		}
	}
	
	private void buildStockpileVariables() {
		
		FixedOpexCost[] fixedOpexCost = scenarioConfigutration.getFixedCost();
		if(fixedOpexCost == null || fixedOpexCost.length < 5) return;
		Map<Integer, BigDecimal> oreMiningCostMap = fixedOpexCost[0].getCostData();
		Map<Integer, BigDecimal> stockPilingCostMap = fixedOpexCost[2].getCostData();
		Map<Integer, BigDecimal> truckHourCostMap = fixedOpexCost[4].getCostData();
		Set<Integer> keys = stockPilingCostMap.keySet();
		String pitNameField = projectConfiguration.getRequiredFieldMapping().get("pit_name");
		List<Stockpile> stockpileList = projectConfiguration.getStockPileList();
		int startYear = scenarioConfigutration.getStartYear();
		for(Stockpile sp: stockpileList){
			String condition = (sp.getCondition() == null) ? "" : sp.getCondition();
			boolean reclaimEnabled = serviceInstanceData.isSpReclaimEnabled() && sp.isReclaim() && ( sp.getCapacity() > 0 );
			if(hasValue(sp.getCondition())){
				condition += " AND ";
			}
			condition +=  pitNameField + " in ( ";
			if(sp.getMappingType() == 0) {
				condition +=  "'"+sp.getAssociatedPit().getPitName() +"'";
			} else {
				Set<Integer> pits = serviceInstanceData.flattenPitGroup(sp.getAssociatedPitGroup());
				for(int pitNo: pits){
					Pit pit = projectConfiguration.getPitfromPitNumber(pitNo);
					condition +=  "'"+pit.getPitName() +"',";
				}
				condition = condition.substring(0, condition.length() -1);
			}
			condition += ")";
			System.out.println(" Stockpile Condition :"+condition);
			List<Block> blocks = findBlocks(condition);
			int count = 1;
			for(int year: keys){
				BigDecimal miningcost = stockPilingCostMap.get(year).add(oreMiningCostMap.get(year));
				BigDecimal truckHourCost = truckHourCostMap.get(year);
				for(Block block: blocks) {
					BigDecimal cost = new BigDecimal(0);
					cost = cost.add(miningcost);
					if(!processedBlocks.contains(block.getId())) continue;
					sp.addBlock(block);
					int payload = serviceInstanceData.getBlockPayloadMapping().get(block.getId());
					if(payload > 0) {
						Integer ct = serviceInstanceData.getCycleTimeDataMapping().get(block.getPitNo()+":"+block.getBenchNo()+":"+sp.getName());
						if(ct != null) {
							double th_ratio =  (double)ct /( payload* 60);
							cost = cost.add(truckHourCost.multiply(new BigDecimal(th_ratio)));
						}
					}
					if(sp.getStockpileNumber() == 0) continue;
					String variable = "p"+block.getPitNo()+"x"+block.getBlockNo()+"s"+sp.getStockpileNumber()+"t"+count;
					String eq = " -"+formatDecimalValue(cost)+ variable;
					write(eq);
					
					serviceInstanceData.addVariable(block, variable);
					
					// Build reeclaim variable
					
					if(reclaimEnabled && (year > startYear)) {
						buildSPReclaimVariable(block, sp, year);
					}
						
				}
				count++;
			}
		}
		
	}
	
	private void buildWasteBlockVariables() throws IOException {
		Set<Block> wasteBlocks = new HashSet<Block>();
		FixedOpexCost[] fixedOpexCost = scenarioConfigutration.getFixedCost();
		if(fixedOpexCost == null || fixedOpexCost.length < 5) return;
		Map<Integer, BigDecimal> wasteMiningCostMap = fixedOpexCost[1].getCostData();
		Map<Integer, BigDecimal> truckHourCostMap = fixedOpexCost[4].getCostData();
		Set<Integer> keys = wasteMiningCostMap.keySet();
		String pitNameField = projectConfiguration.getRequiredFieldMapping().get("pit_name");
		List<Dump> dumpList = projectConfiguration.getDumpList();
		
		for(Dump dump: dumpList) {			
			String condition = (dump.getCondition() == null) ? "": dump.getCondition();
			if(hasValue(dump.getCondition())){
				condition += " AND " ;
			}
			condition +=  pitNameField + " in ( ";
			if(dump.getMappingType() == 0) {
				condition += "'"+dump.getAssociatedPit().getPitName()+ "'";
			} else {
				Set<Integer> pits = serviceInstanceData.flattenPitGroup(dump.getAssociatedPitGroup());
				for(int pitNo: pits){
					Pit pit = projectConfiguration.getPitfromPitNumber(pitNo);
					condition +=  "'"+ pit.getPitName() +"',";
				}
				condition = condition.substring(0, condition.length() -1);
			}
			condition += ")";
			System.out.println(" Dump Condition :"+condition);
			List<Block> blocks = findBlocks(condition);
			
			int count = 1;	
			for(int year: keys){
				BigDecimal wasteminingcost = wasteMiningCostMap.get(year);	
				BigDecimal truckHourCost = truckHourCostMap.get(year);
				for(Block block: blocks) {
					BigDecimal cost = new BigDecimal(0);
					cost = cost.add(wasteminingcost);
					if(processedBlocks.contains(block.getId())) continue;
					int payload = serviceInstanceData.getBlockPayloadMapping().get(block.getId());
					if(payload > 0) {
						Integer ct = serviceInstanceData.getCycleTimeDataMapping().get(block.getPitNo()+":"+block.getBenchNo()+":"+dump.getName());
						if(ct != null) {
							double th_ratio = (double)ct /( payload* 60);
							cost = cost.add(truckHourCost.multiply(new BigDecimal(th_ratio)));
						}
					}
					wasteBlocks.add(block);
					dump.addBlock(block);
					String variable = "p"+block.getPitNo()+"x"+block.getBlockNo()+"w"+dump.getDumpNumber()+"t"+count;
					String eq = " -"+formatDecimalValue(cost)+variable;
					write(eq);
					
					serviceInstanceData.addVariable(block, variable);
					
				}
				count++;
			}
		}
		serviceInstanceData.setWasteBlocks(wasteBlocks);
		
	}
	
	private void buildCapexVariables(){
		int timeperiod = scenarioConfigutration.getTimePeriod();
		List<CapexData> capexDataList = scenarioConfigutration.getCapexDataList();
		int capexCount = 0;
		for(CapexData cd: capexDataList) {
			capexCount++;
			List<CapexInstance> capexInstanceList = cd.getListOfCapexInstances();
			int capexInstanceCount = 0;
			for(CapexInstance ci: capexInstanceList){
				capexInstanceCount++;
				for(int i= 1; i <= timeperiod ; i++){
					String cv = "c"+capexCount+"i"+capexInstanceCount+"t"+i;
					BigDecimal value = new BigDecimal(ci.getCapexAmount() * (1 / Math.pow ((1 + discount_rate), i)));
					write(" -"+formatDecimalValue(value)+cv);
				}
			}
		}
	}
	
	private void buildSPReclaimVariable(Block b, Stockpile sp, int year) {
		int startYear = scenarioConfigutration.getStartYear();
		FixedOpexCost[] fixedOpexCost = scenarioConfigutration.getFixedCost();
		if(fixedOpexCost == null || fixedOpexCost.length < 4) return;
		Map<Integer, BigDecimal> stockPilingReclaimingCostMap = fixedOpexCost[3].getCostData();
		Map<Integer, BigDecimal> truckHourCostMap = fixedOpexCost[4].getCostData();
		
		BigDecimal cost = stockPilingReclaimingCostMap.get(year);
		BigDecimal truckHourCost = truckHourCostMap.get(year);
		
		int timeperiod = year - startYear + 1;
		
		int payload = serviceInstanceData.getBlockPayloadMapping().get(b.getId());
		int fixedTime = projectConfiguration.getTruckParameterData().getFixedTime();
		if(sp.getStockpileNumber() == 0) return;
		Set<Process> processes = b.getProcesses();
		TruckParameterCycleTime cycleTime =  projectConfiguration.getTruckParamCycleTimeByStockpileName(sp.getName());
		
		for(Process p: processes){
			BigDecimal totalCost = new BigDecimal(0);
			totalCost = totalCost.add(cost);
			if(payload > 0) {
				Integer ct = 0;
				if(cycleTime.getProcessData() != null){
					ct = cycleTime.getProcessData().get(p.getModel().getName()) + fixedTime;
				} 
				if(ct != null) {
					double th_ratio =  (double)ct /( payload* 60);
					totalCost = totalCost.add(truckHourCost.multiply(new BigDecimal(th_ratio)));
				}
			}
			totalCost = (totalCost.multiply(new BigDecimal(1 / Math.pow ((1 + discount_rate), timeperiod))));
			String variable = "sp"+sp.getStockpileNumber()+"x"+b.getBlockNo()+"p"+p.getProcessNo()+"t"+timeperiod;
			String eq = " -"+formatDecimalValue(totalCost)+ variable;
			write(eq);
			serviceInstanceData.addVariable(b, variable);
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
				Block block = serviceInstanceData.getBlocks().get(id);
				blocks.add(block);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return blocks;
	}
	
	private Set<Block> findWasteBlocks() {
		Set<Block> blocks = new HashSet<Block>();
		Map<Integer, Block> allBlocks = serviceInstanceData.getBlocks();
		if(allBlocks != null){
			Set<Integer> blockIds = allBlocks.keySet();
			for(Integer blockId: blockIds){
				if(!processedBlocks.contains(blockId)){
					blocks.add(allBlocks.get(blockId));
				}
			}
		}
		return blocks;
	}
	
	private BigDecimal getProcessValue(Block b, Model model, int year) {
		BigDecimal value = new BigDecimal(0);
		if(model == null) return value;
		BigDecimal revenue = new BigDecimal(0);
		BigDecimal pcost = new BigDecimal(0);
		List<OpexData> opexDataList = scenarioConfigutration.getOpexDataList();
		for(OpexData opexData: opexDataList) {
			if(opexData.getModel().getId() == model.getId()){
				if(opexData.isRevenue()){
					String expressionName = opexData.getExpression().getName().replaceAll("\\s+","_");
					BigDecimal expr_value = b.getComputedField(expressionName);
					revenue = revenue.add(expr_value.multiply(opexData.getCostData().get(year)));
				} else {
					pcost = pcost.add(opexData.getCostData().get(year));
				}
			}
		}
		value = revenue.subtract(pcost);
		return value;
	}

	
	private boolean hasValue(String s) {
		return (s !=null && s.trim().length() >0);
	}
	
	@Override
	protected void write(String s) {

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
	
}
