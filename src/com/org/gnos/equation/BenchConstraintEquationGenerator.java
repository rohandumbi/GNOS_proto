package com.org.gnos.equation;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.org.gnos.core.Bench;
import com.org.gnos.core.Block;
import com.org.gnos.core.Pit;
import com.org.gnos.db.model.PitBenchConstraintData;

public class BenchConstraintEquationGenerator extends EquationGenerator{

	private String tonnesWeightFieldName;
	private Map<Integer, List<String>> blockVariableMapping;
	final Pattern lastIntPattern = Pattern.compile("[^0-9]+([0-9]+)$");
	
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
			buildBenchUserConstraintVariables();
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
			Set<Bench> benches = pit.getBenches();
			Bench lastBench = null;
			for(Bench bench: benches){
				List<String> variables = getAllVariablesForBench(bench);
				BigDecimal tonnesWt = getBenchTonnesWt(bench);
				
				for(int i=1; i<= timePeriod; i++){
					StringBuffer sb1= new StringBuffer();
					StringBuffer sb2= new StringBuffer();
					String benchVariable = "p"+pitNo+"b"+bench.getBenchNo()+"t"+i;
					sb1.append(formatDecimalValue(tonnesWt)+benchVariable);
					for(String variable: variables){
						Matcher matcher = lastIntPattern.matcher(variable);
					      if (matcher.find()) {
					          String someNumberStr = matcher.group(1);
					          int year = Integer.parseInt(someNumberStr);
					          if(year > i) continue;
					          sb1.append(" -"+variable);
					          if(lastBench != null){
					        	  sb2.append(" +"+variable);
					          }
					      }						
					}
					if(lastBench != null){
						//float lastBenchTonnesWeight = getBenchTonnesWt(lastBench);
						String lastBenchVariable = "p"+pitNo+"b"+lastBench.getBenchNo()+"t"+i;
						sb2.append("-"+ formatDecimalValue(tonnesWt)+lastBenchVariable +" <= 0 ");
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
	
	private void buildBenchUserConstraintVariables() {
		List<PitBenchConstraintData> pitBenchConstraintDataList = scenarioConfigutration.getPitBenchConstraintDataList();
		int timePeriod = scenarioConfigutration.getTimePeriod();
		int startyear = scenarioConfigutration.getStartYear();
		boolean hasDefaultConstraint = false;
		List<Integer> exclusionList = new ArrayList<Integer>();
		PitBenchConstraintData defaultConstraint = null;
		for(PitBenchConstraintData pitBenchConstraintData:pitBenchConstraintDataList){
			if(!pitBenchConstraintData.isInUse()) continue;
			System.out.println("PitBenchConstraint:"+ pitBenchConstraintData.getPitName());
			if(pitBenchConstraintData.getPitName().equals("Default")){
				hasDefaultConstraint = true;
				defaultConstraint = pitBenchConstraintData;
				continue;
			}
			String pitName = pitBenchConstraintData.getPitName();
			com.org.gnos.db.model.Pit pit = projectConfiguration.getPitfromPitName(pitName);
			exclusionList.add(pit.getPitNumber());
			Pit pitdata = serviceInstanceData.getPits().get(pit.getPitNumber());
			for(int i=1; i<= timePeriod; i++){
				int yearvalue = pitBenchConstraintData.getConstraintData().get(startyear+ i-1);
				buildEquationForPit(pitdata, i, yearvalue);
			}
			
		}
		if(hasDefaultConstraint){
			System.out.println("PitBenchConstraint: Inside default constraint");
			Map<Integer, Pit> pits = serviceInstanceData.getPits();
			Set<Integer> pitNos = pits.keySet();
			for(Integer pitNo: pitNos){
				if(exclusionList.contains(pitNo)) continue;
				
				Pit pit = pits.get(pitNo);
				for(int i=1; i<= timePeriod; i++){
					int yearvalue = defaultConstraint.getConstraintData().get(startyear+ i-1);
					buildEquationForPit(pit, i, yearvalue);
				}
			}
		}
	}
	
	private void buildEquationForPit(Pit pit, int timeperiod, int yearvalue){
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		Set<Bench> benches = pit.getBenches();
		
		for(Bench bench:benches){
			sb1.append("+"+"p"+pit.getPitNo()+"b"+bench.getBenchNo()+"t"+timeperiod);
			if(timeperiod > 1){
				sb2.append("-"+"p"+pit.getPitNo()+"b"+bench.getBenchNo()+"t"+(timeperiod-1));
			}			
		}
		String eq = sb1.toString().substring(1) + sb2.toString() + " <= "+ (yearvalue-1);
		write(eq);
		
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
	
	private BigDecimal getBenchTonnesWt(Bench bench){
		BigDecimal tonnesWt = new BigDecimal(0);
		for(Block block:bench.getBlocks()){
			try{
				tonnesWt = tonnesWt.add(new BigDecimal(block.getField(this.tonnesWeightFieldName)));
			} catch(NumberFormatException nfe){
				System.err.println("Could not parse to float :"+nfe.getMessage());
			}
			
		}
		
		return tonnesWt;
	}
}
