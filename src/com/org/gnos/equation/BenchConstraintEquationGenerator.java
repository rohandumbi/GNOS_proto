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

public class BenchConstraintEquationGenerator extends EquationGenerator{

	private String tonnesWeightFieldName;
	private Map<Integer, List<String>> blockVariableMapping;
	
	public BenchConstraintEquationGenerator(InstanceData data) {
		super(data);
		this.tonnesWeightFieldName = projectConfiguration.getRequiredFieldMapping().get("tonnes_wt");
		this.blockVariableMapping = serviceInstanceData.getBlockVariableMapping();
	}
	
	@Override
	public void generate() {
		
		int bufferSize = 8 * 1024;
		try {
			output = new BufferedOutputStream(new FileOutputStream("pitBenchConstraint.txt"), bufferSize);
			bytesWritten = 0;
			buildBenchConstraintVariables();
			output.flush();
			output.close();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	public void buildBenchConstraintVariables() {
		Map<Integer, Pit> pits = serviceInstanceData.getPits();
		int timePeriod = scenarioConfigutration.getTimePeriod();
		Set<Integer> pitNos = pits.keySet();
		for(int pitNo: pitNos){
			Pit pit = pits.get(pitNo);
			List<Bench> benches = pit.getBenches();
			Bench lastBench = null;
			for(Bench bench: benches){
				List<String> variables = getAllVariablesForBench(bench);
				float tonnesWt = getBenchTonnesWt(bench);
				
				for(int i=1; i<= timePeriod; i++){
					StringBuffer sb1= new StringBuffer();
					StringBuffer sb2= new StringBuffer();
					String benchVariable = "p"+pitNo+"b"+bench.getBenchNo()+"t"+i;
					sb1.append(tonnesWt+benchVariable);
					for(String variable: variables){
						String lastChar= variable.substring(variable.length() - 1);
						int year = Integer.parseInt(lastChar);
						if(year > i) break;
						sb1.append(" -"+variable);
						if(lastBench != null){
							sb2.append(" +"+variable);
						}
						
					}
					if(lastBench != null){
						float lastBenchTonnesWeight = getBenchTonnesWt(lastBench);
						
						sb2.append("-"+ lastBenchTonnesWeight+benchVariable +" <= 0 ");
					}
					sb1.append(" <= 0 ");
					write(sb1.toString());
					if(lastBench != null){
						write(sb2.toString().substring(2));
					}
				}
				

				
				lastBench = bench;				
			}
		}
	}
	private List<String> getAllVariablesForBench(Bench bench){
		List<String> variables = new ArrayList<String>();
		List<Block> blocks= bench.getBlocks();
		for(Block block: blocks){
			variables.addAll(this.blockVariableMapping.get(block.getId()));
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
