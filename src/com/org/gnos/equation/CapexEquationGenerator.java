package com.org.gnos.equation;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.org.gnos.core.Block;
import com.org.gnos.db.model.CapexData;
import com.org.gnos.db.model.CapexInstance;
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Model;
import com.org.gnos.db.model.Pit;
import com.org.gnos.db.model.PitGroup;
import com.org.gnos.db.model.Process;
import com.org.gnos.db.model.ProcessConstraintData;
import com.org.gnos.db.model.ProcessJoin;

public class CapexEquationGenerator extends EquationGenerator{

	
	public CapexEquationGenerator(InstanceData data) {
		super(data);
	}
	
	@Override
	public void generate() {
		
		int bufferSize = 8 * 1024;
		try {
			output = new BufferedOutputStream(new FileOutputStream("capexconstraints.txt"), bufferSize);
			bytesWritten = 0;
			buildCapexEquations();
			output.flush();
			output.close();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	private void buildCapexEquations() {
		int timeperiod = scenarioConfigutration.getTimePeriod();
		List<CapexData> capexDataList = scenarioConfigutration.getCapexDataList();
		int capexCount = 0;
		for(CapexData cd: capexDataList) {
			capexCount++;
			buildSet1Equations(cd, capexCount);
			buildSet2Equations(cd, capexCount);
			buildSet3Equations(cd, capexCount);
		}
	}
	
	private void buildSet1Equations(CapexData cd, int capexNumber) {
		List<Process> processList = projectConfiguration.getProcessList();
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
			ProcessJoin processJoin = projectConfiguration.getProcessJoinByName(groupName);
			List<Process> pList = new ArrayList<Process>();
			if(processJoin != null) {
				for(Model model: processJoin.getlistChildProcesses()){
					for( Process p: processList){
						if(p.getModel().getName().equals(model.getName())){
							pList.add(p);
						}
					}
				}
			}
			buildCapexEquationForProcesses(pList, cd, capexNumber,processConstraintData);
		}  else if(groupType == CapexInstance.SELECTION_PIT){
			List<Integer> pList = new ArrayList<Integer>();
			Pit pit = projectConfiguration.getPitfromPitName(groupName);
			pList.add(pit.getPitNumber());
			buildCapexEquationForPits(pList, cd, capexNumber, processConstraintData);
		} else if(groupType == CapexInstance.SELECTION_PIT_GROUP){
			List<Integer> pList = new ArrayList<Integer>();
			PitGroup pg = projectConfiguration.getPitGroupfromName(groupName);
			pList.addAll(getPitsFromPitGroup(pg));
			buildCapexEquationForPits(pList, cd, capexNumber, processConstraintData);
		} 
	}
	
	private void buildSet2Equations(CapexData cd, int capexNumber) {
		int timeperiod = scenarioConfigutration.getTimePeriod();
		List<CapexInstance> capexInstanceList = cd.getListOfCapexInstances();
		int capexInstanceCount = capexInstanceList.size();
		
		for(int j=1; j<capexInstanceCount; j++){			
			for(int i=1; i<= timeperiod; i++){
				StringBuffer sb = new StringBuffer("");
				for(int ii=1; ii<=i; ii++){
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
		int timeperiod = scenarioConfigutration.getTimePeriod();
		int capexInstanceNo = 0;
		List<CapexInstance> capexInstanceList = cd.getListOfCapexInstances();
		for(CapexInstance ci: capexInstanceList){
			capexInstanceNo++;
			StringBuffer sb = new StringBuffer("");
			for(int i=1; i<= timeperiod; i++){
				if(i > 1){
					sb.append(" + ");
				}
				sb.append("c"+capexNumber+"i"+capexInstanceNo+"t"+i);			
			}
			sb.append(" <= 1 ");
			write(sb.toString());
		}
	}
	
	private void buildCapexEquationForProcesses(List<Process> processList, CapexData cd, int capexNumber, Map<Integer, Float> processConstraintData){
		List<CapexInstance> capexInstanceList = cd.getListOfCapexInstances();				
		int timeperiod = scenarioConfigutration.getTimePeriod();
		int startyear = scenarioConfigutration.getStartYear();
		
		for(int i= 1; i <= timeperiod; i++ ){
			StringBuffer sb = new StringBuffer("");
			int count = 0;
			for(Process p: processList){
				List<Block> blocks = p.getBlocks();
				Expression exp = p.getModel().getExpression();
				String expressionName = exp.getName().replaceAll("\\s+","_");
				for(Block b: blocks){
					if(count > 0){
						sb.append(" + ");
					}
					sb.append(b.getComputedField(expressionName)+"p"+b.getPitNo()+"x"+b.getBlockNo()+"p"+p.getProcessNo()+"t"+i);
					count ++;
				}
			}
			int instanceNumber=0;
			for(CapexInstance ci: capexInstanceList){
				instanceNumber++;
				for(int ii=1; ii<=i; ii++){
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
	
	private void buildCapexEquationForPits(List<Integer> pitnumberList, CapexData cd, int capexNumber, Map<Integer, Float> processConstraintData){
		List<CapexInstance> capexInstanceList = cd.getListOfCapexInstances();
		List<Process> processList = projectConfiguration.getProcessList();
		int timeperiod = scenarioConfigutration.getTimePeriod();
		int startyear = scenarioConfigutration.getStartYear();
		
		for(int i= 1; i <= timeperiod; i++ ){
			StringBuffer sb = new StringBuffer("");
			int count = 0;
			for( Process p: processList){
				List<Block> blocks = p.getBlocks();
				Expression exp = p.getModel().getExpression();
				String expressionName = exp.getName().replaceAll("\\s+","_");
				for(Block b: blocks){
					if(!pitnumberList.contains(b.getPitNo())) continue;
					if(count > 0){
						sb.append(" + ");
					}
					sb.append(b.getComputedField(expressionName)+"p"+b.getPitNo()+"x"+b.getBlockNo()+"p"+p.getProcessNo()+"t"+i);
					count ++;
				}
			}
			for(Block b: serviceInstanceData.getProcessBlocks()){
				if(!pitnumberList.contains(b.getPitNo())) continue;
				if(count > 0){
					sb.append(" + ");
				}
				sb.append("p"+b.getPitNo()+"x"+b.getBlockNo()+"s"+this.serviceInstanceData.getPitStockpileMapping().get(b.getPitNo())+"t"+i);
				count ++;
			}
			for(Block b: serviceInstanceData.getWasteBlocks()){
				if(!pitnumberList.contains(b.getPitNo())) continue;
				if(count > 0){
					sb.append(" + ");
				}
				List<Dump> dumps = this.serviceInstanceData.getPitDumpMapping().get(b.getPitNo());
				if(dumps == null) continue;
				for(Dump dump: dumps){
					sb.append("p"+b.getPitNo()+"x"+b.getBlockNo()+"w"+dump.getDumpNumber()+"t"+i);
				}			
				count ++;
			}
			int instanceNumber=0;
			for(CapexInstance ci: capexInstanceList){
				instanceNumber++;
				for(int ii=1; ii<=i; ii++){
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
		List<ProcessConstraintData> processConstraintDataList = scenarioConfigutration.getProcessConstraintDataList();
		for(ProcessConstraintData pcd: processConstraintDataList){
			if(!pcd.isInUse()) continue;
			if(pcd.getSelectionType() == type && pcd.getCoefficientType() == pcd.COEFFICIENT_NONE){
				if(pcd.getSelector_name().equals(name)){
					return pcd.getConstraintData();
				}
			}
		}
		return null;
	}
	

	
}
