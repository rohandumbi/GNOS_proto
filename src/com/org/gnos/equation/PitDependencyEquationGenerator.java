package com.org.gnos.equation;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.core.Bench;
import com.org.gnos.core.Block;
import com.org.gnos.core.Pit;
import com.org.gnos.db.model.PitDependencyData;

public class PitDependencyEquationGenerator extends EquationGenerator{

	private String tonnesWeightFieldName;
	private Map<Integer, List<String>> blockVariableMapping;
	
	public PitDependencyEquationGenerator(InstanceData data) {
		super(data);
		this.tonnesWeightFieldName = projectConfiguration.getRequiredFieldMapping().get("tonnes_wt");
		this.blockVariableMapping = serviceInstanceData.getBlockVariableMapping();
	}
	
	@Override
	public void generate() {
		
		int bufferSize = 8 * 1024;
		try {
			output = new BufferedOutputStream(new FileOutputStream("pitDependency.txt"), bufferSize);
			bytesWritten = 0;
			buildDependencyEquations();
			output.flush();
			output.close();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	
	private void buildDependencyEquations() {
		List<PitDependencyData> pitDependencyDataList = scenarioConfigutration.getPitDependencyDataList();
		int timePeriod = scenarioConfigutration.getTimePeriod();
		for(PitDependencyData pitDependencyData:pitDependencyDataList){
			if(!pitDependencyData.isInUse()) continue;
			Pit firstpit = getPitFromPitName(pitDependencyData.getFirstPitName());
			Pit dependentpit = getPitFromPitName(pitDependencyData.getDependentPitName());
			if(firstpit == null || dependentpit == null) continue;
			
			Bench firstPitbench = getBenchFromBenchNumber(firstpit, pitDependencyData.getFirstPitAssociatedBench());
			Bench dependentPitbench = getBenchFromBenchNumber(dependentpit, pitDependencyData.getDependentPitAssociatedBench());
			
			int firstPitBenchCount = firstpit.getBenches().size();
			int dependentPitbenchCount = dependentpit.getBenches().size();
			
			int minlead = pitDependencyData.getMinLead();
			int maxlead = pitDependencyData.getMaxLead();
			
			if(minlead <=0 && maxlead <= 0) {
				int firstPitBenchNo = firstPitBenchCount; // Last bench of first pit
				int dependentPitBenchNo = 1; // first bench of dependent pit
				if(firstPitbench == null){
					firstPitbench = firstpit.getBench(firstPitBenchNo);
				}
				if(dependentPitbench == null){
					dependentPitbench = dependentpit.getBench(dependentPitBenchNo);
				}
				buildPitDependencyEquation(firstpit, firstPitbench, dependentPitbench);
			} else {
				int dependentBenchStart = 1;
				
				if(firstPitbench != null){
					firstPitBenchCount = firstPitbench.getBenchNo();
				}
				if(dependentPitbench != null){
					dependentBenchStart = dependentPitbench.getBenchNo();
					dependentPitbenchCount = dependentPitbenchCount +1 - dependentBenchStart;
				}
				
				if(minlead > 0){
					
					int benchLoopCount = dependentPitbenchCount;
					
					if((firstPitBenchCount - minlead) < benchLoopCount){
						benchLoopCount = firstPitBenchCount - minlead;
					}
					for(int i=0; i< benchLoopCount; i++) {
						Bench firstpitBench = firstpit.getBench(minlead + 1 + i);
						Bench dpitBench = dependentpit.getBench(dependentBenchStart + i);
						buildPitDependencyEquation(firstpit, firstpitBench, dpitBench);
					}
				} 
				if(maxlead > 0) {
					
					int benchLoopCount = dependentPitbenchCount;
					
					if((firstPitBenchCount - maxlead) < benchLoopCount){
						benchLoopCount = firstPitBenchCount - maxlead;
					}
					for(int i=0; i< benchLoopCount; i++) {
						Bench firstpitBench = firstpit.getBench(maxlead + 1 + i);
						Bench dpitBench = dependentpit.getBench(dependentBenchStart + i);
						buildPitDependencyEquation(dependentpit, dpitBench, firstpitBench);
					}
				}
			}
		}
	}
	
	private void buildPitDependencyEquation(Pit p1, Bench b1, Bench b2){
		int timePeriod = scenarioConfigutration.getTimePeriod();
		List<String> variables = getAllVariablesForBench(b2);
		float benchTonnesWt = getBenchTonnesWt(b2);
		for(int i=1; i<= timePeriod; i++){
			StringBuilder sb = new StringBuilder();
			int count = 0;
			
			for(String variable: variables){
				if(variable.startsWith("sp")) continue;
				if(!variable.endsWith(String.valueOf(i))) continue;
				if(count > 0) sb.append(" + ");						
				sb.append(variable);
				count++;
			}
			sb.append(" - ");
			sb.append(benchTonnesWt+"p"+p1.getPitNo()+"b"+b1.getBenchNo()+"t"+i);
			sb.append(" <= 0");
			write(sb.toString());
		}
		
	}
	
	private Pit getPitFromPitName(String pitname){
		Map<Integer, Pit> pits = serviceInstanceData.getPits();
		Set<Integer> pitNos = pits.keySet();
		for(int pitNo: pitNos){
			Pit pit = pits.get(pitNo);
			if(pit.getPitName().equals(pitname)){
				return pit;
			}
		}
		return null;
	}
	
	private Bench getBenchFromBenchNumber(Pit pit, String benchNumber){
		if(benchNumber == null || benchNumber.trim().length() == 0) return null;
		Set<Bench> benches = pit.getBenches();
		for(Bench b: benches){
			if(b.getBenchName().equals(benchNumber)){
				return b;
			}
		}
		
		return null;
	}
	private List<String> getAllVariablesForBench(Bench bench){
		List<String> variables = new ArrayList<String>();
		List<Block> blocks= bench.getBlocks();
		for(Block block: blocks){
			List<String> variableList = this.blockVariableMapping.get(block.getId());
			if(variableList != null){
				//System.out.println("Block Id :"+ block.getId()+ " Variable Size:"+variableList.size());
				variables.addAll(this.blockVariableMapping.get(block.getId()));
			}		
		}
		
		return variables;
	}
	
	private float getBenchTonnesWt(Bench bench){
		float tonnesWt = 0;
		for(Block block:bench.getBlocks()){
			try{
				tonnesWt += Float.parseFloat(block.getField(this.tonnesWeightFieldName));
			} catch(NumberFormatException nfe){
				System.err.println("Could not parse to float :"+nfe.getMessage());
			}
			
		}
		
		return tonnesWt;
	}
}
