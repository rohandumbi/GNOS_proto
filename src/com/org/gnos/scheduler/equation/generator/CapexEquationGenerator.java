package com.org.gnos.scheduler.equation.generator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.core.Block;
import com.org.gnos.core.Pit;
import com.org.gnos.db.model.CapexData;
import com.org.gnos.db.model.CapexInstance;
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.Model;
import com.org.gnos.db.model.PitGroup;
import com.org.gnos.db.model.Process;
import com.org.gnos.db.model.ProcessConstraintData;
import com.org.gnos.db.model.ProcessJoin;
import com.org.gnos.db.model.Stockpile;
import com.org.gnos.scheduler.equation.Constraint;
import com.org.gnos.scheduler.equation.ExecutionContext;
import com.org.gnos.scheduler.equation.SPBlock;
import com.org.gnos.scheduler.equation.SlidingWindowExecutionContext;

public class CapexEquationGenerator extends EquationGenerator{

	
	public CapexEquationGenerator(ExecutionContext data) {
		super(data);
	}
	
	@Override
	public void generate() {

		try {
			buildCapexEquations();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	private void buildCapexEquations() {
		List<CapexData> capexDataList = context.getCapexDataList();
		int capexCount = 0;
		for(CapexData cd: capexDataList) {
			capexCount++;
			buildSet1Equations(cd, capexCount);
			buildSet2Equations(cd, capexCount);
			buildSet3Equations(cd, capexCount);
		}
	}
	
	private void buildSet1Equations(CapexData cd, int capexNumber) {
		List<Process> processList = context.getProcessList();
		List<CapexInstance> capexInstanceList = cd.getListOfCapexInstances();
		int capexInstanceCount = capexInstanceList.size();
		if(capexInstanceCount < 1) return;
		String groupName = capexInstanceList.get(0).getGroupingName();
		int groupType = capexInstanceList.get(0).getGroupingType();
		Map<Integer, Float> processConstraintData = getProcessConstraintData(groupName, groupType);
		if( groupType == CapexInstance.SELECTION_PROCESS){
			for( Process p: processList){
				if(p.getModel().getName().equals(groupName)){
					List<Process> pList = new ArrayList<Process>();
					pList.add(p);
					buildCapexEquationForProcesses(pList, cd, capexNumber, processConstraintData);
					break;
				}
			}
		} else if(groupType == CapexInstance.SELECTION_PROCESS_JOIN){
			ProcessJoin processJoin = context.getProcessJoinByName(groupName);
			List<Process> pList = new ArrayList<Process>();
			if(processJoin != null) {
				for(Integer modelId: processJoin.getChildProcessList()){
					Model model = context.getModelById(modelId);
					if(model == null) continue;
					for( Process p: processList){
						if(p.getModel().getName().equals(model.getName())){
							pList.add(p);
						}
					}
				}
			}
			buildCapexEquationForProcesses(pList, cd, capexNumber,processConstraintData);
		}  else if(groupType == CapexInstance.SELECTION_PIT){
			Set<Integer> pList = new HashSet<Integer>();
			Pit pit = context.getPitNameMap().get(groupName);
			pList.add(pit.getPitNo());
			buildCapexEquationForPits(pList, cd, capexNumber, processConstraintData);
		} else if(groupType == CapexInstance.SELECTION_PIT_GROUP){
			Set<Integer> pList = new HashSet<Integer>();
			PitGroup pg = context.getPitGroupfromName(groupName);
			pList.addAll(getPitsFromPitGroup(pg));
			buildCapexEquationForPits(pList, cd, capexNumber, processConstraintData);
		} 
	}
	
	private void buildSet2Equations(CapexData cd, int capexNumber) {
		int timePeriodStart = context.getTimePeriodStart();
		int timePeriodEnd = context.getTimePeriodEnd();
		List<CapexInstance> capexInstanceList = cd.getListOfCapexInstances();
		int capexInstanceCount = capexInstanceList.size();
		
		for(int j=1; j<capexInstanceCount; j++){			
			for(int i=timePeriodStart; i<= timePeriodEnd; i++){
				Constraint c = new Constraint();
				StringBuffer sb = new StringBuffer("");
				for(int ii=timePeriodStart; ii<=i; ii++){
					if(ii > 1){
						sb.append(" + ");
					}
					sb.append("c"+capexNumber+"i"+j+"t"+ii);
					c.addVariable("c"+capexNumber+"i"+j+"t"+ii, new BigDecimal(1));
				}
				
				
				sb.append(" - "+"c"+capexNumber+"i"+(j+1)+"t"+i+" >= 0 ");
				c.addVariable("c"+capexNumber+"i"+(j+1)+"t"+i, new BigDecimal(1).negate());
				c.setType(Constraint.GREATER_EQUAL);
				c.setValue(new BigDecimal(0));
				context.getConstraints().add(c);
			}			
		}
	}

	private void buildSet3Equations(CapexData cd, int capexNumber) {
		int timePeriodStart = context.getTimePeriodStart();
		int timePeriodEnd = context.getTimePeriodEnd();
		List<CapexInstance> capexInstanceList = cd.getListOfCapexInstances();
		for(int j=1; j<= capexInstanceList.size(); j++){
			StringBuffer sb = new StringBuffer("");
			Constraint c = new Constraint();
			for(int i=timePeriodStart; i<= timePeriodEnd; i++){
				if(i > 1){
					sb.append(" + ");
				}
				sb.append("c"+capexNumber+"i"+j+"t"+i);	
				c.addVariable("c"+capexNumber+"i"+j+"t"+i, new BigDecimal(1));			
			}
			sb.append(" <= 1 ");
			c.setType(Constraint.LESS_EQUAL);
			c.setValue(new BigDecimal(1));
			context.getConstraints().add(c);
		}
	}
	
	private void buildCapexEquationForProcesses(List<Process> processList, CapexData cd, int capexNumber, Map<Integer, Float> processConstraintData){
		List<CapexInstance> capexInstanceList = cd.getListOfCapexInstances();				
		int timePeriodStart = context.getTimePeriodStart();
		int timePeriodEnd = context.getTimePeriodEnd();
		int startyear = context.getStartYear();
		
		for(int i= timePeriodStart; i <= timePeriodEnd; i++ ){
			Constraint c = new Constraint();
			for(Process p: processList){
				List<Block> blocks = p.getBlocks();
				int unitId;
				if(p.getModel().getUnitType() == Model.UNIT_FIELD) {
					unitId = p.getModel().getFieldId();
				} else {
					unitId = p.getModel().getExpressionId();
				}
				for(Block b: blocks){
					c.addVariable("p"+b.getPitNo()+"x"+b.getBlockNo()+"p"+p.getProcessNo()+"t"+i, context.getUnitValueforBlock(b, unitId, p.getModel().getUnitType()));
					if(context.isSpReclaimEnabled() && context.isGlobalMode() && i > timePeriodStart) {
						int stockpileNo = getStockpileNoForReclaim(b);
						if(stockpileNo > 0) {
							c.addVariable("sp"+stockpileNo+"x"+b.getBlockNo()+"p"+p.getProcessNo()+"t"+i, context.getUnitValueforBlock(b, unitId, p.getModel().getUnitType()));
						}					
					}
					
				}
				if(context.isSpReclaimEnabled() && !context.isGlobalMode() &&   i > timePeriodStart) {
					SlidingWindowExecutionContext swctx = (SlidingWindowExecutionContext) context;
					Map<Integer, SPBlock> spBlockMapping = swctx.getSpBlockMapping();
					Set<Integer> spNos = spBlockMapping.keySet();			
					for(int spNo: spNos){
						SPBlock spb = spBlockMapping.get(spNo);
						if(spb == null) continue;

						Set<Process> processes = spb.getProcesses();
						for(Process process: processes){
							if(process.getProcessNo() == p.getProcessNo()) {
								c.addVariable("sp"+spNo+"x0p"+p.getProcessNo()+"t"+i, swctx.getUnitValueforBlock(spb, unitId, p.getModel().getUnitType()));
							}
						}
					}
				}
				
			}
			int instanceNumber=0;
			for(CapexInstance ci: capexInstanceList){
				instanceNumber++;
				for(int ii=timePeriodStart; ii<=i; ii++){
					c.addVariable("c"+capexNumber+"i"+instanceNumber+"t"+ii, context.getScaledValue(new BigDecimal(ci.getExpansionCapacity())).negate());
				}				
			}
			if(processConstraintData != null){
				c.setValue(context.getScaledValue(new BigDecimal(processConstraintData.get(startyear + i -1))));
			} else {
				c.setValue(new BigDecimal(0));
			}
			c.setType(Constraint.LESS_EQUAL);
			context.getConstraints().add(c);
		}
	}
	
	private void buildCapexEquationForPits(Set<Integer> pitnumberList, CapexData cd, int capexNumber, Map<Integer, Float> processConstraintData){
		List<CapexInstance> capexInstanceList = cd.getListOfCapexInstances();
		List<Process> processList = context.getProcessList();
		int timePeriodStart = context.getTimePeriodStart();
		int timePeriodEnd = context.getTimePeriodEnd();
		int startyear = context.getStartYear();
		
		for(int i= timePeriodStart; i <= timePeriodEnd; i++ ){
			Constraint c = new Constraint();
			for( Process p: processList){
				List<Block> blocks = p.getBlocks();
				int unitId;
				if(p.getModel().getUnitType() == Model.UNIT_FIELD) {
					unitId = p.getModel().getFieldId();
				} else {
					unitId = p.getModel().getExpressionId();
				}
				for(Block b: blocks){
					if(!pitnumberList.contains(b.getPitNo())) continue;
					c.addVariable("x"+b.getBlockNo()+"p"+p.getProcessNo()+"t"+i, context.getUnitValueforBlock(b, unitId, p.getModel().getUnitType()));
					if(context.isSpReclaimEnabled() && context.isGlobalMode() &&  i > timePeriodStart) {
						int stockpileNo = getStockpileNoForReclaim(b);
						if(stockpileNo > 0) {
							c.addVariable("sp"+stockpileNo+"x"+b.getBlockNo()+"p"+p.getProcessNo()+"t"+i, context.getUnitValueforBlock(b, unitId, p.getModel().getUnitType()));
						}					
					}
				}
				if(context.isSpReclaimEnabled() && !context.isGlobalMode() &&  i > timePeriodStart ) {
					SlidingWindowExecutionContext swctx = (SlidingWindowExecutionContext) context;
					Map<Integer, SPBlock> spBlockMapping = swctx.getSpBlockMapping();
					Set<Integer> spNos = spBlockMapping.keySet();			
					for(int spNo: spNos){
						SPBlock spb = spBlockMapping.get(spNo);
						if(spb == null) continue;

						Set<Process> processes = spb.getProcesses();
						for(Process process: processes){
							if(process.getProcessNo() == p.getProcessNo()) {
								c.addVariable("sp"+spNo+"x0p"+p.getProcessNo()+"t"+i, swctx.getUnitValueforBlock(spb, unitId, p.getModel().getUnitType()));
							}
						}
					}
				}
			}
			
			for(Block b: context.getProcessBlocks()){
				if(!pitnumberList.contains(b.getPitNo())) continue;
				c.addVariable("p"+b.getPitNo()+"x"+b.getBlockNo()+"s"+getStockpileNo(b)+"t"+i, new BigDecimal(1));
			}
			for(Block b: context.getWasteBlocks()){
				if(!pitnumberList.contains(b.getPitNo())) continue;
				List<Dump> dumps = getDump(b);
				if(dumps == null) continue;
				for(Dump dump: dumps){
					c.addVariable("p"+b.getPitNo()+"x"+b.getBlockNo()+"w"+dump.getDumpNumber()+"t"+i, new BigDecimal(1));
				}			
				
			}
			int instanceNumber=0;
			for(CapexInstance ci: capexInstanceList){
				instanceNumber++;
				for(int ii=timePeriodStart; ii<=i; ii++){
					c.addVariable("c"+capexNumber+"i"+instanceNumber+"t"+ii, context.getScaledValue(new BigDecimal(ci.getExpansionCapacity())).negate());
				}				
			}
			if(processConstraintData != null){
				c.setValue(context.getScaledValue(new BigDecimal(processConstraintData.get(startyear + i -1))));
			} else {
				c.setValue(new BigDecimal(0));
			}
			c.setType(Constraint.LESS_EQUAL);
			context.getConstraints().add(c);
		}
	}
	
	private Map<Integer, Float> getProcessConstraintData(String name, int type){
		List<ProcessConstraintData> processConstraintDataList = context.getProcessConstraintDataList();
		for(ProcessConstraintData pcd: processConstraintDataList){
			if(!pcd.isInUse()) continue;
			if(pcd.getSelectionType() == type && pcd.getCoefficientType() == ProcessConstraintData.COEFFICIENT_NONE){
				if(pcd.getSelector_name().equals(name)){
					return pcd.getConstraintData();
				}
			}
		}
		return null;
	}
	
	private int getStockpileNo(Block b){
		List<Stockpile> stockpiles = context.getStockpiles();
		
		for(Stockpile sp: stockpiles){
			Set<Block> blocks = sp.getBlocks();
			for(Block block: blocks){
				if(block.getBlockNo() == b.getBlockNo()){
					return sp.getStockpileNumber();
				}
			}
		}
		
		return -1;
	}
	
	private int getStockpileNoForReclaim(Block b){
		List<Stockpile> stockpiles = context.getStockpiles();
		
		for(Stockpile sp: stockpiles){
			if(!sp.isReclaim()) continue;
			Set<Block> blocks = sp.getBlocks();
			for(Block block: blocks){
				if(block.getBlockNo() == b.getBlockNo()){
					return sp.getStockpileNumber();
				}
			}
		}
		
		return -1;
	}
	
	private List<Dump> getDump(Block b){
		List<Dump> alldumps = context.getDumps();
		List<Dump> dumps = new ArrayList<Dump>();
		for(Dump dump: alldumps){
			Set<Block> blocks = dump.getBlocks();
			for(Block block: blocks){
				if(block.getBlockNo() == b.getBlockNo()){
					dumps.add(dump);
					continue;
				}
			}
		}
		
		return dumps;
	}
}
