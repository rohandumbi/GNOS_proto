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
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.DumpDependencyData;

public class DumpDependencyEquationGenerator extends EquationGenerator{

	private String tonnesWeightFieldName;
	private Map<Integer, List<String>> blockVariableMapping;
	
	public DumpDependencyEquationGenerator(InstanceData data) {
		super(data);
		this.tonnesWeightFieldName = projectConfiguration.getRequiredFieldMapping().get("tonnes_wt");
		this.blockVariableMapping = serviceInstanceData.getBlockVariableMapping();
	}
	
	@Override
	public void generate() {
		
		int bufferSize = 8 * 1024;
		try {
			output = new BufferedOutputStream(new FileOutputStream("dumpDependency.txt"), bufferSize);
			bytesWritten = 0;
			buildDependencyEquations();
			output.flush();
			output.close();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	
	private void buildDependencyEquations() {
		List<DumpDependencyData> dumpDependencyDataList = scenarioConfigutration.getDumpDependencyDataList();
		int timePeriod = scenarioConfigutration.getTimePeriod();
		for(DumpDependencyData dumpDependencyData:dumpDependencyDataList){
			if(!dumpDependencyData.isInUse()) continue;
			if(dumpDependencyData.getFirstPitName() != null){
				Pit pit = getPitFromPitName(dumpDependencyData.getFirstPitName());
				
			} else if(dumpDependencyData.getFirstDumpName() != null){
				
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
				System.out.println("Block Id :"+ block.getId()+ " Variable Size:"+variableList.size());
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
