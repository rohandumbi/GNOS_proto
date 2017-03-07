package com.org.gnos.scheduler.equation.generator;

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
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Model;
import com.org.gnos.db.model.PitGroup;
import com.org.gnos.db.model.Process;
import com.org.gnos.db.model.ProcessConstraintData;
import com.org.gnos.db.model.ProcessJoin;
import com.org.gnos.db.model.Stockpile;
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
			output.flush();
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
				StringBuffer sb = new StringBuffer("");
				for(int ii=timePeriodStart; ii<=i; ii++){
					if(ii > 1){
						sb.append(" + ");
					}
					sb.append("c"+capexNumber+"i"+j+"t"+ii);
				}
				
				
				sb.append(" - "+"c"+capexNumber+"i"+(j+1)+"t"+i+" >= 0 ");
				write(sb.toString());
			}			
		}
	}

	private void buildSet3Equations(CapexData cd, int capexNumber) {
		int timePeriodStart = context.getTimePeriodStart();
		int timePeriodEnd = context.getTimePeriodEnd();
		List<CapexInstance> capexInstanceList = cd.getListOfCapexInstances();
		for(int j=1; j<= capexInstanceList.size(); j++){
			StringBuffer sb = new StringBuffer("");
			for(int i=timePeriodStart; i<= timePeriodEnd; i++){
				if(i > 1){
					sb.append(" + ");
				}
				sb.append("c"+capexNumber+"i"+j+"t"+i);			
			}
			sb.append(" <= 1 ");
			write(sb.toString());
		}
	}
	
	private void buildCapexEquationForProcesses(List<Process> processList, CapexData cd, int capexNumber, Map<Integer, Float> processConstraintData){
		List<CapexInstance> capexInstanceList = cd.getListOfCapexInstances();				
		int timePeriodStart = context.getTimePeriodStart();
		int timePeriodEnd = context.getTimePeriodEnd();
		int startyear = context.getStartYear();
		
		for(int i= timePeriodStart; i <= timePeriodEnd; i++ ){
			StringBuffer sb = new StringBuffer("");
			int count = 0;
			for(Process p: processList){
				List<Block> blocks = p.getBlocks();
				Expression exp = context.getExpressionById(p.getModel().getExpressionId());
				for(Block b: blocks){
					if(count > 0){
						sb.append(" + ");
					}
					sb.append(context.getExpressionValueforBlock(b, exp)+"p"+b.getPitNo()+"x"+b.getBlockNo()+"p"+p.getProcessNo()+"t"+i);
					count ++;
					if(context.isSpReclaimEnabled() && context.isGlobalMode() && timePeriodStart > 1) {
						int stockpileNo = getStockpileNoForReclaim(b);
						if(stockpileNo > 0) {
							if(count > 0){
								sb.append(" + ");
							}
							sb.append(context.getExpressionValueforBlock(b, exp)+"sp"+stockpileNo+"x"+b.getBlockNo()+"p"+p.getProcessNo()+"t"+i);
							count ++;
						}					
					}
					
				}
				if(context.isSpReclaimEnabled() && !context.isGlobalMode() && timePeriodStart > 1) {
					SlidingWindowExecutionContext swctx = (SlidingWindowExecutionContext) context;
					Map<Integer, SPBlock> spBlockMapping = swctx.getSpBlockMapping();
					Set<Integer> spNos = spBlockMapping.keySet();			
					for(int spNo: spNos){
						SPBlock spb = spBlockMapping.get(spNo);
						if(spb == null) continue;

						Set<Process> processes = spb.getProcesses();
						for(Process process: processes){
							if(process.getProcessNo() == p.getProcessNo()) {
								if(count > 0){
									sb.append(" + ");
								}
								sb.append(swctx.getExpressionValueforBlock(spb, exp)+"sp"+spNo+"x0p"+p.getProcessNo()+"t"+i);
								count ++;
							}
						}
					}
				}
				
			}
			int instanceNumber=0;
			for(CapexInstance ci: capexInstanceList){
				instanceNumber++;
				for(int ii=timePeriodStart; ii<=i; ii++){
					sb.append(" - "+ci.getExpansionCapacity()+"c"+capexNumber+"i"+instanceNumber+"t"+ii);
				}				
			}
			if(processConstraintData != null){
				sb.append(" <= "+processConstraintData.get(startyear + i -1));
			} else {
				sb.append(" <= 0 ");
			}
			
			write(sb.toString());
		}
	}
	
	private void buildCapexEquationForPits(Set<Integer> pitnumberList, CapexData cd, int capexNumber, Map<Integer, Float> processConstraintData){
		List<CapexInstance> capexInstanceList = cd.getListOfCapexInstances();
		List<Process> processList = context.getProcessList();
		int timePeriodStart = context.getTimePeriodStart();
		int timePeriodEnd = context.getTimePeriodEnd();
		int startyear = context.getStartYear();
		
		for(int i= timePeriodStart; i <= timePeriodEnd; i++ ){
			StringBuffer sb = new StringBuffer("");
			int count = 0;
			for( Process p: processList){
				List<Block> blocks = p.getBlocks();
				Expression exp =  context.getExpressionById(p.getModel().getExpressionId());
				for(Block b: blocks){
					if(!pitnumberList.contains(b.getPitNo())) continue;
					if(count > 0){
						sb.append(" + ");
					}
					sb.append(context.getExpressionValueforBlock(b, exp)+"p"+b.getPitNo()+"x"+b.getBlockNo()+"p"+p.getProcessNo()+"t"+i);
					count ++;
				}
			}
			for(Block b: context.getProcessBlocks()){
				if(!pitnumberList.contains(b.getPitNo())) continue;
				if(count > 0){
					sb.append(" + ");
				}
				sb.append("p"+b.getPitNo()+"x"+b.getBlockNo()+"s"+getStockpileNo(b)+"t"+i);
				count ++;
			}
			for(Block b: context.getWasteBlocks()){
				if(!pitnumberList.contains(b.getPitNo())) continue;
				List<Dump> dumps = getDump(b);
				if(dumps == null) continue;
				for(Dump dump: dumps){
					if(count > 0){
						sb.append(" + ");
					}
					sb.append("p"+b.getPitNo()+"x"+b.getBlockNo()+"w"+dump.getDumpNumber()+"t"+i);
					count ++;
				}			
				
			}
			int instanceNumber=0;
			for(CapexInstance ci: capexInstanceList){
				instanceNumber++;
				for(int ii=timePeriodStart; ii<=i; ii++){
					sb.append(" - "+ci.getExpansionCapacity()+"c"+capexNumber+"i"+instanceNumber+"t"+ii);
				}				
			}
			if(processConstraintData != null){
				sb.append(" <= "+processConstraintData.get(startyear + i -1));
			} else {
				sb.append(" <= 0 ");
			}
			
			write(sb.toString());
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
