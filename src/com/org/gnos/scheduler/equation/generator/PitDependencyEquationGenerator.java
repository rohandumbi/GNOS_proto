package com.org.gnos.scheduler.equation.generator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.core.Bench;
import com.org.gnos.core.Block;
import com.org.gnos.core.Pit;
import com.org.gnos.db.model.PitBenchConstraintData;
import com.org.gnos.db.model.PitDependencyData;
import com.org.gnos.scheduler.equation.Constraint;
import com.org.gnos.scheduler.equation.ExecutionContext;

public class PitDependencyEquationGenerator extends EquationGenerator{

	private Map<Integer, List<String>> blockVariableMapping;
	
	public PitDependencyEquationGenerator(ExecutionContext data) {
		super(data);
		this.blockVariableMapping = context.getBlockVariableMapping();
	}
	
	@Override
	public void generate() {
		try {
			buildDependencyEquations();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	
	private void buildDependencyEquations() {
		List<PitDependencyData> pitDependencyDataList = context.getPitDependencyDataList();
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
		int timePeriodStart = context.getTimePeriodStart();
		int timePeriodEnd = context.getTimePeriodEnd();
		List<String> variables = getAllVariablesForBench(b2);
		BigDecimal benchTonnesWt = getBenchTonnesWt(b2);
		if(benchTonnesWt.doubleValue() <= 0 || variables.size() == 0) return;
		for(int i=timePeriodStart; i<= timePeriodEnd; i++){
			if(!checkBenchConstraint(p1, b1, b2, i)) continue;
			Constraint c = new Constraint(Constraint.PIT_DEPENDENCY);
			
			for(String variable: variables){
				if(variable.startsWith("sp")) continue;
				if(!variable.endsWith(String.valueOf("t"+i))) continue;
				c.addVariable(variable, new BigDecimal(1));
			}
			c.addVariable("p"+p1.getPitNo()+"b"+b1.getBenchNo()+"t"+i, benchTonnesWt.negate());
			c.setEqualityType(Constraint.LESS_EQUAL);
			c.setValue(new BigDecimal(0));
			context.getConstraints().add(c);
		}
		
	}
	
	private Pit getPitFromPitName(String pitname){
		Map<Integer, Pit> pits = context.getPits();
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
	
	private boolean checkBenchConstraint(Pit p1, Bench b1, Bench b2, int timePeriod) {
		int startYear = context.getStartYear();
		List<PitBenchConstraintData> pitBenchConstraintDataList = context.getPitBenchConstraintDataList();
		for(PitBenchConstraintData pitBenchConstraintData: pitBenchConstraintDataList) {
			if(!pitBenchConstraintData.isInUse()) continue;
			if(pitBenchConstraintData.getPitName().equals(p1.getPitName())) {
				
				int constraintValue = 0;
				for(int i = 0; i < timePeriod; i++) {
					constraintValue += pitBenchConstraintData.getConstraintData().get(startYear+i).intValue();
				}
				if(b1.getBenchNo() > constraintValue) {
					return false;
				} else {
					return true;
				}
				
			}
		}
		return true;
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
}
