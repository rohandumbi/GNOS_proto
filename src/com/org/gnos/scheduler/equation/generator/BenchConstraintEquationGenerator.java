package com.org.gnos.scheduler.equation.generator;

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
import com.org.gnos.scheduler.equation.Constraint;
import com.org.gnos.scheduler.equation.ExecutionContext;

public class BenchConstraintEquationGenerator extends EquationGenerator{

	private Map<Integer, List<String>> blockVariableMapping;
	final Pattern lastIntPattern = Pattern.compile("[^0-9]+([0-9]+)$");
	
	public BenchConstraintEquationGenerator(ExecutionContext data) {
		super(data);
		this.blockVariableMapping = context.getBlockVariableMapping();
	}
	
	@Override
	public void generate() {
		try {
			buildBenchConstraintVariables();
			buildBenchUserConstraintVariables();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	public void buildBenchConstraintVariables() {
		
		Map<Integer, Pit> pits = context.getPits();
		int timePeriodStart = context.getTimePeriodStart();
		int timePeriodEnd = context.getTimePeriodEnd();
		Set<Integer> pitNos = pits.keySet();
		for(int pitNo: pitNos){
			Pit pit = pits.get(pitNo);
			Set<Bench> benches = pit.getBenches();
			Bench lastBench = null;
			for(Bench bench: benches){
				List<String> variables = getAllVariablesForBench(bench);
				BigDecimal tonnesWt = getBenchTonnesWt(bench);
				if(variables.size() == 0) continue;
				for(int i=timePeriodStart; i<= timePeriodEnd; i++){
					Constraint c1 = new Constraint();
					Constraint c2 = new Constraint();
					String benchVariable = "p"+pitNo+"b"+bench.getBenchNo()+"t"+i;
					c1.addVariable(benchVariable, tonnesWt);
					for(String variable: variables){
						if(variable.startsWith("sp")) continue;
						Matcher matcher = lastIntPattern.matcher(variable);
					      if (matcher.find()) {
					          String someNumberStr = matcher.group(1);
					          int year = Integer.parseInt(someNumberStr);
					          if(year > i) continue;
					          c1.addVariable(variable, new BigDecimal(1).negate());
					          if(lastBench != null){
					        	  c2.addVariable(variable, new BigDecimal(1));
					          }
					      }						
					}
					if(lastBench != null){
						String lastBenchVariable = "p"+pitNo+"b"+lastBench.getBenchNo()+"t"+i;
						c2.addVariable(lastBenchVariable, tonnesWt.negate());
						c2.setValue(new BigDecimal(0));
						c2.setType(Constraint.LESS_EQUAL);
					}
					c1.setValue(new BigDecimal(0));
					c1.setType(Constraint.LESS_EQUAL);
					if(lastBench != null){
						//write(sb2.toString().substring(2));
					}
					context.getConstraints().add(c1);
					context.getConstraints().add(c2);
				}		
				lastBench = bench;				
			}
		}
	}
	
	private void buildBenchUserConstraintVariables() {
		List<PitBenchConstraintData> pitBenchConstraintDataList = context.getPitBenchConstraintDataList();
		int timePeriodStart = context.getTimePeriodStart();
		int timePeriodEnd = context.getTimePeriodEnd();
		int startyear = context.getStartYear();
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
			Pit pit = context.getPitNameMap().get(pitName);
			exclusionList.add(pit.getPitNo());
			for(int i=timePeriodStart; i<= timePeriodEnd; i++){
				float yearvalue = pitBenchConstraintData.getConstraintData().get(startyear+ i-1);
				buildEquationForPit(pit, i, yearvalue);
			}
			
		}
		if(hasDefaultConstraint){
			System.out.println("PitBenchConstraint: Inside default constraint");
			Map<Integer, Pit> pits = context.getPits();
			Set<Integer> pitNos = pits.keySet();
			for(Integer pitNo: pitNos){
				if(exclusionList.contains(pitNo)) continue;
				
				Pit pit = pits.get(pitNo);
				for(int i=timePeriodStart; i<= timePeriodEnd; i++){
					float yearvalue = defaultConstraint.getConstraintData().get(startyear+ i-1);
					buildEquationForPit(pit, i, yearvalue);
				}
			}
		}
	}
	
	private void buildEquationForPit(Pit pit, int timeperiod, float yearvalue){
		Set<Bench> benches = pit.getBenches();
		int timePeriodStart = context.getTimePeriodStart();
		Constraint constraint = new Constraint();
		for(Bench bench:benches){
			constraint.addVariable("p"+pit.getPitNo()+"b"+bench.getBenchNo()+"t"+timeperiod, new BigDecimal(1));
			if(timeperiod > timePeriodStart){
				constraint.addVariable("p"+pit.getPitNo()+"b"+bench.getBenchNo()+"t"+(timeperiod-1), new BigDecimal(1).negate());
			}			
		}
		float value = yearvalue == 1 ? yearvalue : yearvalue - 1;
		constraint.setType(Constraint.LESS_EQUAL);
		constraint.setValue(new BigDecimal(value));
		context.getConstraints().add(constraint);		
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
	
	private BigDecimal getBenchTonnesWt(Bench bench){
		BigDecimal tonnesWt = new BigDecimal(0);
		for(Block block:bench.getBlocks()){
			try{
				BigDecimal blockTonnesWt = new BigDecimal(context.getTonnesWtForBlock(block));
				blockTonnesWt = context.getScaledValue(blockTonnesWt);
				blockTonnesWt = formatDecimalValue(blockTonnesWt);
				tonnesWt = tonnesWt.add(blockTonnesWt);
			} catch(NumberFormatException nfe){
				System.err.println("Could not parse to float :"+nfe.getMessage());
			}
			
		}
		
		return tonnesWt;
	}
	
/*	@Override
	protected String formatDecimalValue(BigDecimal bd) {
		return bd.stripTrailingZeros().toString();
	}*/
}
