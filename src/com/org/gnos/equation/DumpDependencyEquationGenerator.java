package com.org.gnos.equation;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.org.gnos.core.Bench;
import com.org.gnos.core.Block;
import com.org.gnos.core.Pit;
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.DumpDependencyData;

public class DumpDependencyEquationGenerator extends EquationGenerator{
	
	private Map<Integer, List<String>> blockVariableMapping;
	
	public DumpDependencyEquationGenerator(InstanceData data) {
		super(data);
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
		for(DumpDependencyData dumpDependencyData:dumpDependencyDataList){
			if(!dumpDependencyData.isInUse()) continue;
			if(dumpDependencyData.getFirstPitName() != null){
				Pit pit = getPitFromPitName(dumpDependencyData.getFirstPitName());
				Dump d1 = projectConfiguration.getDumpfromPitName(dumpDependencyData.getFirstPitName());
				if(d1 == null) continue;
				Dump d2 = projectConfiguration.getDumpfromDumpName(dumpDependencyData.getDependentDumpName());
				buildPitDumpDependencyEquation1(pit, d1, d2);
				buildDumpDependencyEquation2(d1, d2);
				
			} else if(dumpDependencyData.getFirstDumpName() != null){
				Dump d1 = projectConfiguration.getDumpfromDumpName(dumpDependencyData.getFirstDumpName());
				Dump d2 = projectConfiguration.getDumpfromDumpName(dumpDependencyData.getDependentDumpName());
				buildDumpDependencyEquation1(d1, d2);
				buildDumpDependencyEquation2(d1, d2);
			}


		}
	}
	
	
	private void buildDumpDependencyEquation1(Dump d1, Dump d2){
		if(!d1.isHasCapacity()) return;
		int timePeriod = scenarioConfigutration.getTimePeriod();
		Set<Block> blocks = d1.getBlocks();
		for(int i=1; i<= timePeriod; i++){
			StringBuilder sb = new StringBuilder();
			sb.append(d1.getCapacity()+"d"+d1.getDumpNumber()+"t"+i);
			for(Block block: blocks){
				for(int j=1; j<=i; j++){
					sb.append(" - p"+block.getPitNo()+"x"+block.getBlockNo()+"w"+d1.getDumpNumber()+"t"+j);
				}
			}
			sb.append(" <= 0");
			write(sb.toString());
		}
		
	}
	
	private void buildDumpDependencyEquation2(Dump d1, Dump d2){
		if(!d2.isHasCapacity()) return;
		int timePeriod = scenarioConfigutration.getTimePeriod();
		Set<Block> blocks = d2.getBlocks();
		for(int i=1; i<= timePeriod; i++){
			StringBuilder sb = new StringBuilder();
			for(Block block: blocks){
				for(int j=1; j<=i; j++){
					sb.append("+p"+block.getPitNo()+"x"+block.getBlockNo()+"w"+d2.getDumpNumber()+"t"+j);
				}
			}
			sb.append(" - "+d2.getCapacity()+"d"+d1.getDumpNumber()+"t"+i);
			sb.append(" <= 0");
			write(sb.toString());
		}
		
	}
	
	private void buildPitDumpDependencyEquation1(Pit p, Dump d1, Dump d2){
		if(!d1.isHasCapacity()) return;
		int timePeriod = scenarioConfigutration.getTimePeriod();
		Bench lastBench = p.getBench(p.getBenches().size() - 1);
		List<Block> blocks = lastBench.getBlocks();
		Set<Block> dumpBlocks = d1.getBlocks();
		for(int i=1; i<= timePeriod; i++){
			StringBuilder sb = new StringBuilder();
			sb.append(d1.getCapacity()+"p"+p.getPitNo()+"b"+lastBench.getBenchNo()+"t"+i);
			for(Block block: blocks){
				if(!dumpBlocks.contains(block)) continue;
				for(int j=1; j<=i; j++){
					sb.append(" - p"+block.getPitNo()+"x"+block.getBlockNo()+"w"+d1.getDumpNumber()+"t"+j);
				}
			}
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
}
