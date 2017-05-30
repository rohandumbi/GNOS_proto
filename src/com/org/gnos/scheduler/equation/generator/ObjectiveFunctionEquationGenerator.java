package com.org.gnos.scheduler.equation.generator;

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
import com.org.gnos.core.Pit;
import com.org.gnos.core.Tree;
import com.org.gnos.db.DBManager;
import com.org.gnos.db.model.CapexData;
import com.org.gnos.db.model.CapexInstance;
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.FixedOpexCost;
import com.org.gnos.db.model.Model;
import com.org.gnos.db.model.OpexData;
import com.org.gnos.db.model.Process;
import com.org.gnos.db.model.Product;
import com.org.gnos.db.model.ProductJoin;
import com.org.gnos.db.model.Stockpile;
import com.org.gnos.db.model.TruckParameterCycleTime;
import com.org.gnos.scheduler.equation.ExecutionContext;
import com.org.gnos.scheduler.equation.SPBlock;
import com.org.gnos.scheduler.equation.SlidingWindowExecutionContext;

public class ObjectiveFunctionEquationGenerator extends EquationGenerator{
	
	private float discount_rate = 0; //this has to be made an input variable later
	
	private Set<Integer> processedBlocks;
	
	public ObjectiveFunctionEquationGenerator(ExecutionContext data) {
		super(data);
	}
	
	@Override
	public void generate() {
		processedBlocks = new HashSet<Integer>();
		try {
			discount_rate = context.getScenario().getDiscount()/100;
			System.out.println("Discount :"+discount_rate);
			buildProcessBlockVariables();
			buildStockpileVariables();
			buildWasteBlockVariables();
			buildCapexVariables();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	private void buildProcessBlockVariables() {
		List<Process> porcesses = context.getProcessList();
		Set<Block> processBlocks = new HashSet<Block>();
		for(Process process: porcesses) {
			System.out.println("Equation Generation: process name - "+process.getModel().getName());
			String condition = buildCondition(process) ;
			List<Block> blocks = findBlocks(condition);
			process.setBlocks(blocks);
			processBlocks.addAll(blocks);
			buildProcessVariables(process, blocks, process.getProcessNo());		
		}
		context.setProcessBlocks(processBlocks);

	}
	
	private void buildProcessVariables(Process process, List<Block> blocks, int processNumber) {
		List<FixedOpexCost> fixedOpexCost = context.getFixedOpexCostList();
		if(fixedOpexCost == null || fixedOpexCost.size() < 5) return;
		Map<Integer, BigDecimal> oreMiningCostMap = fixedOpexCost.get(0).getCostData();
		Map<Integer, BigDecimal> truckHourCostMap = fixedOpexCost.get(4).getCostData();
		int timePeriodStart = context.getTimePeriodStart();
		int timePeriodEnd = context.getTimePeriodEnd();
		int startYear = context.getStartYear();
		for(int i=timePeriodStart; i<= timePeriodEnd; i++ ){
			int year = startYear + i -1;
			BigDecimal miningcost = oreMiningCostMap.get(year) ;
			BigDecimal truckHourCost = truckHourCostMap.get(year);
			for(Block block: blocks) {
				if(!context.isGlobalMode()) {
					if(!context.hasRemainingTonnage(block)){
						continue;
					}
				}
				BigDecimal cost = new BigDecimal(0);
				cost = cost.add(miningcost);
				processedBlocks.add(block.getId());
				block.addProcess(process);
				int payload = context.getBlockPayloadMapping().get(block.getId());
				if(payload > 0) {
					BigDecimal ct = context.getCycleTimeDataMapping().get(block.getPitNo()+":"+block.getBenchNo()+":"+process.getModel().getName());
					if(ct != null) {
						double th_ratio =  ct.doubleValue() /(payload* 60);
						cost = cost.add(truckHourCost.multiply(new BigDecimal(th_ratio)));
					}
				}
				BigDecimal processValue = getProcessValue(block, process.getModel(), year);
				BigDecimal value = processValue.subtract(cost);
				value = (value.multiply(new BigDecimal(1 / Math.pow ((1 + discount_rate), i))));
				String variable = "p"+block.getPitNo()+"x"+block.getBlockNo()+"p"+processNumber+"t"+i; 
				context.addVariable(variable, value);
				context.addVariable(block, variable);
			}
		}
	}
	
	private void buildStockpileVariables() {
		
		List<FixedOpexCost> fixedOpexCost = context.getFixedOpexCostList();
		if(fixedOpexCost == null || fixedOpexCost.size() < 5) return;
		Map<Integer, BigDecimal> oreMiningCostMap = fixedOpexCost.get(0).getCostData();
		Map<Integer, BigDecimal> stockPilingCostMap = fixedOpexCost.get(2).getCostData();
		Map<Integer, BigDecimal> truckHourCostMap = fixedOpexCost.get(4).getCostData();
		String pitNameField = context.getPitFieldName();
		List<Stockpile> stockpileList = context.getStockpiles();
		int startYear = context.getStartYear();
		int timePeriodStart = context.getTimePeriodStart();
		int timePeriodEnd = context.getTimePeriodEnd();
		
		for(Stockpile sp: stockpileList){
			String condition = (sp.getCondition() == null) ? "" : sp.getCondition();
			boolean reclaimEnabled = context.isSpReclaimEnabled() && sp.isReclaim() && ( sp.getCapacity() > 0 );
			if(hasValue(sp.getCondition())){
				condition += " AND ";
			}
			condition +=  pitNameField + " in ( ";
			if(sp.getMappingType() == 0) {
				condition +=  "'"+sp.getMappedTo() +"'";
			} else {
				Set<Integer> pits = getPitsFromPitGroup(context.getPitGroupfromName(sp.getMappedTo()));
				for(int pitNo: pits){
					Pit pit = context.getPits().get(pitNo);
					condition +=  "'"+pit.getPitName() +"',";
				}
				condition = condition.substring(0, condition.length() -1);
			}
			condition += ")";
			System.out.format(" Stockpile No: %d  Condition %s : \n", sp.getStockpileNumber(), condition);
			List<Block> blocks = findBlocks(condition);
			for(int i=timePeriodStart; i<= timePeriodEnd; i++ ){
				int year = startYear + i -1;
				BigDecimal miningcost = stockPilingCostMap.get(year).add(oreMiningCostMap.get(year));
				BigDecimal truckHourCost = truckHourCostMap.get(year);
				for(Block block: blocks) {
					if(!context.isGlobalMode()) {
						if(!context.hasRemainingTonnage(block)){
							continue;
						}
					}
					BigDecimal cost = new BigDecimal(0);
					cost = cost.add(miningcost);
					if(!processedBlocks.contains(block.getId())) continue;
					sp.addBlock(block);
					int payload = context.getBlockPayloadMapping().get(block.getId());
					if(payload > 0) {
						BigDecimal ct = context.getCycleTimeDataMapping().get(block.getPitNo()+":"+block.getBenchNo()+":"+sp.getName());
						if(ct != null) {
							double th_ratio =  ct.doubleValue() /( payload* 60);
							cost = cost.add(truckHourCost.multiply(new BigDecimal(th_ratio)));
						}
					}
					if(sp.getStockpileNumber() == 0) continue;
					cost = (cost.multiply(new BigDecimal(1 / Math.pow ((1 + discount_rate), i))));
					String variable = "p"+block.getPitNo()+"x"+block.getBlockNo()+"s"+sp.getStockpileNumber()+"t"+i;
										
					context.addVariable(block, variable);
					context.addVariable(variable, cost.negate());
					// Build reeclaim variable
					
					if(reclaimEnabled && (year > startYear)) {
						if(context.isGlobalMode()){
							buildSPReclaimVariable(block, sp, year);
						}						
					}						
				}
				if(reclaimEnabled && (year > startYear) && !context.isGlobalMode()) {
					buildSWSPReclaimVariable(sp, year);
				}
			}
		}
		
	}
	
	private void buildWasteBlockVariables() throws IOException {
		Set<Block> wasteBlocks = new HashSet<Block>();
		List<FixedOpexCost> fixedOpexCost = context.getFixedOpexCostList();
		if(fixedOpexCost == null || fixedOpexCost.size() < 5) return;
		Map<Integer, BigDecimal> wasteMiningCostMap = fixedOpexCost.get(1).getCostData();
		Map<Integer, BigDecimal> truckHourCostMap = fixedOpexCost.get(4).getCostData();
		String pitNameField = context.getPitFieldName();
		List<Dump> dumpList = context.getDumps();
		
		int startYear = context.getStartYear();
		int timePeriodStart = context.getTimePeriodStart();
		int timePeriodEnd = context.getTimePeriodEnd();
		
		for(Dump dump: dumpList) {			
			String condition = (dump.getCondition() == null) ? "": dump.getCondition();
			if(hasValue(dump.getCondition())){
				condition += " AND " ;
			}
			condition +=  pitNameField + " in ( ";
			if(dump.getMappingType() == 0) {
				condition += "'"+dump.getMappedTo()+ "'";
			} else {
				Set<Integer> pits = getPitsFromPitGroup(context.getPitGroupfromName(dump.getMappedTo()));
				for(int pitNo: pits){
					Pit pit = context.getPits().get(pitNo);
					condition +=  "'"+ pit.getPitName() +"',";
				}
				condition = condition.substring(0, condition.length() -1);
			}
			condition += ")";
			System.out.println(" Dump Condition :"+condition);
			List<Block> blocks = findBlocks(condition);
			
			for(int i=timePeriodStart; i<= timePeriodEnd; i++ ){
				int year = startYear + i -1;
				BigDecimal wasteminingcost = wasteMiningCostMap.get(year);	
				BigDecimal truckHourCost = truckHourCostMap.get(year);
				for(Block block: blocks) {
					if(!context.isGlobalMode()) {
						if(!context.hasRemainingTonnage(block)){
							continue;
						}
					}
					BigDecimal cost = new BigDecimal(0);
					cost = cost.add(wasteminingcost);
					if(processedBlocks.contains(block.getId())) continue;
					int payload = context.getBlockPayloadMapping().get(block.getId());
					if(payload > 0) {
						BigDecimal ct = context.getCycleTimeDataMapping().get(block.getPitNo()+":"+block.getBenchNo()+":"+dump.getName());
						if(ct != null) {
							double th_ratio = ct.doubleValue() /( payload* 60);
							cost = cost.add(truckHourCost.multiply(new BigDecimal(th_ratio)));
						}
					}
					wasteBlocks.add(block);
					dump.addBlock(block);
					cost = (cost.multiply(new BigDecimal(1 / Math.pow ((1 + discount_rate), i))));
					String variable = "p"+block.getPitNo()+"x"+block.getBlockNo()+"w"+dump.getDumpNumber()+"t"+i;
					
					context.addVariable(block, variable);
					context.addVariable(variable, cost.negate());
				}
			}
		}
		context.setWasteBlocks(wasteBlocks);
		
	}
	
	private void buildCapexVariables(){
		int timePeriodStart = context.getTimePeriodStart();
		int timePeriodEnd = context.getTimePeriodEnd();
		List<CapexData> capexDataList = context.getCapexDataList();
		int capexCount = 0;
		for(CapexData cd: capexDataList) {
			capexCount++;
			List<CapexInstance> capexInstanceList = cd.getListOfCapexInstances();
			int capexInstanceCount = 0;
			for(CapexInstance ci: capexInstanceList){
				capexInstanceCount++;
				for(int i= timePeriodStart; i <= timePeriodEnd ; i++){
					String cv = "c"+capexCount+"i"+capexInstanceCount+"t"+i;
					BigDecimal value = new BigDecimal(ci.getCapexAmount() * (1 / Math.pow ((1 + discount_rate), i)));
					context.addVariable(cv, value.negate());
				}
			}
		}
	}
	
	private void buildSPReclaimVariable(Block b, Stockpile sp, int year) {
		int startYear = context.getStartYear();
		List<FixedOpexCost> fixedOpexCost = context.getFixedOpexCostList();
		if(fixedOpexCost == null || fixedOpexCost.size() < 4) return;
		Map<Integer, BigDecimal> stockPilingReclaimingCostMap = fixedOpexCost.get(3).getCostData();
		Map<Integer, BigDecimal> truckHourCostMap = fixedOpexCost.get(4).getCostData();
		
		BigDecimal cost = stockPilingReclaimingCostMap.get(year);
		BigDecimal truckHourCost = truckHourCostMap.get(year);
		
		int timeperiod = year - startYear + 1;
		
		int payload = context.getBlockPayloadMapping().get(b.getId());
		BigDecimal fixedTime = context.getFixedTime();
		if(sp.getStockpileNumber() == 0) return;
		Set<Process> processes = b.getProcesses();
		TruckParameterCycleTime cycleTime =  context.getTruckParamCycleTimeByStockpileName(sp.getName());
		
		for(Process p: processes){
			BigDecimal totalCost = new BigDecimal(0);
			BigDecimal processValue = getProcessValue(b, p.getModel(), year);
			totalCost = totalCost.add(cost);
			if(payload > 0) {
				BigDecimal ct = new BigDecimal(0);
				if(cycleTime.getProcessData() != null){
					ct = cycleTime.getProcessData().get(p.getModel().getName()).add(fixedTime);
				} 
				if(ct != null) {
					double th_ratio =  ct.doubleValue() /( payload* 60);
					totalCost = totalCost.add(truckHourCost.multiply(new BigDecimal(th_ratio)));
				}
			}
			BigDecimal value = processValue.subtract(totalCost);
			value = (value.multiply(new BigDecimal(1 / Math.pow ((1 + discount_rate), timeperiod))));
			String variable = "sp"+sp.getStockpileNumber()+"x"+b.getBlockNo()+"p"+p.getProcessNo()+"t"+timeperiod;
			context.addVariable(b, variable);
			context.addVariable(variable, value);
		}	
	}
	private void buildSWSPReclaimVariable(Stockpile sp, int year) {
		int startYear = context.getStartYear();
		List<FixedOpexCost> fixedOpexCost = context.getFixedOpexCostList();
		if(fixedOpexCost == null || fixedOpexCost.size() < 4) return;
		Map<Integer, BigDecimal> stockPilingReclaimingCostMap = fixedOpexCost.get(3).getCostData();
		Map<Integer, BigDecimal> truckHourCostMap = fixedOpexCost.get(4).getCostData();
		
		BigDecimal cost = stockPilingReclaimingCostMap.get(year);
		BigDecimal truckHourCost = truckHourCostMap.get(year);
		
		int timeperiod = year - startYear + 1;
		SPBlock spb = ((SlidingWindowExecutionContext)context).getSPBlock(sp.getStockpileNumber());
		if(spb == null || spb.getTonnesWt() == 0) return;
		int payload = spb.getPayload();
		BigDecimal fixedTime = context.getFixedTime();
		if(sp.getStockpileNumber() == 0) return;
		
		Set<Process> processes = spb.getProcesses();
		TruckParameterCycleTime cycleTime =  context.getTruckParamCycleTimeByStockpileName(sp.getName());
		
		for(Process p: processes){
			BigDecimal totalCost = new BigDecimal(0);
			BigDecimal processValue = getProcessValueForSPBlock(spb, p.getModel(), year);
			totalCost = totalCost.add(cost);
			if(payload > 0) {
				BigDecimal ct = new BigDecimal(0);
				if(cycleTime.getProcessData() != null){
					ct = cycleTime.getProcessData().get(p.getModel().getName()).add(fixedTime);
				} 
				if(ct != null) {
					double th_ratio =  ct.doubleValue() /( payload* 60);
					totalCost = totalCost.add(truckHourCost.multiply(new BigDecimal(th_ratio)));
				}
			}
			BigDecimal value = processValue.subtract(totalCost);
			value = (value.multiply(new BigDecimal(1 / Math.pow ((1 + discount_rate), timeperiod))));
			String variable = "sp"+sp.getStockpileNumber()+"x0p"+p.getProcessNo()+"t"+timeperiod;
			((SlidingWindowExecutionContext)context).addVariable(sp.getStockpileNumber(), variable);
			context.addVariable(variable, value);
		}
	}

	private String buildCondition(Process process) {
		String condition = "";

		Model model = process.getModel();
		if(hasValue(model.getCondition())){
			condition = model.getCondition();
		}
		if(model.getUnitType() == Model.UNIT_EXPRESSION) {
			Expression expr =  context.getExpressionById(model.getExpressionId());
			if(hasValue(expr.getFilter())) {
				if(hasValue(condition)) {
					condition = condition + " AND "+ expr.getFilter();
				} else {
					condition =  expr.getFilter();
				}
			}
		}
		
		boolean continueLoop = true;
		Node currNode = context.getProcessTree().getNodeByName(model.getName());
		do{
			Node parent = currNode.getParent();
			if(parent == null) {
				continueLoop = false;
			} else {
				Model pModel = parent.getData();
				if(hasValue(pModel.getCondition())){
					if(hasValue(condition)) {
						condition = condition + " AND "+  pModel.getCondition();
					} else {
						condition = pModel.getCondition();
					}
					
				}
				if(pModel.getUnitType() == Model.UNIT_EXPRESSION) {
					Expression expr1 =  context.getExpressionById(pModel.getExpressionId());
					if(expr1 != null && hasValue(expr1.getFilter())) {
						if(hasValue(condition)) {
							condition = condition + " AND "+ expr1.getFilter();
						} else {
							condition =  expr1.getFilter();
						}
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
		String sql = "select id from gnos_data_"+context.getProjectId() ;
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
				Block block = context.getBlocks().get(id);
				blocks.add(block);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return blocks;
	}
	
	private BigDecimal getProcessValue(Block b, Model model, int year) {
		BigDecimal value = new BigDecimal(0);
		if(model == null) return value;
		BigDecimal revenue = new BigDecimal(0);
		BigDecimal pcost = new BigDecimal(0);
		List<OpexData> opexDataList = context.getOpexDataList();

		for(OpexData opexData: opexDataList) {
			if(opexData.isRevenue()){
				if(opexData.getModelId() != -1 && (opexData.getModelId() != model.getId())) {
					continue;

				} else if(opexData.getProductJoinName() != null  && opexData.getProductJoinName().trim().length() > 0) {
					if(!isProcessAssociatedToProductJoin(model, opexData.getProductJoinName())){
						continue;
					}
				}
				int unitId;
				if(opexData.getUnitType() == OpexData.UNIT_FIELD) {
					unitId = opexData.getFieldId();
				} else {
					unitId = opexData.getExpressionId();
				}
				BigDecimal expr_value = context.getUnitValueforBlock(b, unitId, opexData.getUnitType());
				revenue = revenue.add(expr_value.multiply(opexData.getCostData().get(year)));
			} else {
				 if(opexData.getModelId() == model.getId() || isChild(opexData.getModelId(), model)) {
					 pcost = pcost.add(opexData.getCostData().get(year));
				 } 
				
			}

		}
		value = revenue.subtract(pcost);
		return value;
	}

	private BigDecimal getProcessValueForSPBlock(SPBlock b, Model model, int year) {
		BigDecimal value = new BigDecimal(0);
		if(model == null) return value;
		BigDecimal revenue = new BigDecimal(0);
		BigDecimal pcost = new BigDecimal(0);
		int unitId;
		if(model.getUnitType() == Model.UNIT_FIELD) {
			unitId = model.getFieldId();
		} else {
			unitId = model.getExpressionId();
		}
		List<OpexData> opexDataList = context.getOpexDataList();
		for(OpexData opexData: opexDataList) {
			if(opexData.isRevenue()){
				if(opexData.getModelId() != -1 && (opexData.getModelId() != model.getId())) {
					continue;

				} else if(opexData.getProductJoinName() != null  && opexData.getProductJoinName().trim().length() > 0) {
					if(!isProcessAssociatedToProductJoin(model, opexData.getProductJoinName())){
						continue;
					}
				}
				BigDecimal expr_value = ((SlidingWindowExecutionContext)context).getUnitValueforBlock(b, unitId, model.getUnitType());
				revenue = revenue.add(expr_value.multiply(opexData.getCostData().get(year)));
			} else {
				pcost = pcost.add(opexData.getCostData().get(year));
			}
		}
		value = revenue.subtract(pcost);
		return value;
	}
	
	
	private boolean isProcessAssociatedToProductJoin(Model model, String productJoinName) {
		ProductJoin pj = context.getProductJoinFromName(productJoinName);
		Set<String> products = pj.getProductList();
		for(String productName: products) {
			Product p = context.getProductFromName(productName);
			if(p.getModelId() == model.getId()) {
				return true;
			}
		}
		Set<String> productJoins = pj.getProductJoinList();
		for(String pjName: productJoins) {
			if(isProcessAssociatedToProductJoin(model, pjName)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isChild(int modelId, Model model) {
		Tree processTree = context.getProcessTree();
		Node processNode = processTree.getNodeByName(model.getName());
		
		return isChild(modelId, processNode);
	}
	
	private boolean isChild(int modelId, Node node) {
		Node parent = node.getParent();
		if(parent == null) return false;
		if(parent.getData().getId() == modelId) {
			return true;
		}
		
		return isChild(modelId, parent);
	}
	
	private boolean hasValue(String s) {
		return (s !=null && s.trim().length() >0);
	}
	
}
