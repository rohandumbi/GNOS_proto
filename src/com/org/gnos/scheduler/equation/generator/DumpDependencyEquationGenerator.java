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
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.DumpDependencyData;
import com.org.gnos.db.model.PitGroup;
import com.org.gnos.scheduler.equation.Constraint;
import com.org.gnos.scheduler.equation.ExecutionContext;

public class DumpDependencyEquationGenerator extends EquationGenerator{
	
	final Pattern lastIntPattern = Pattern.compile("[^0-9]+([0-9]+)$");
	
	public DumpDependencyEquationGenerator(ExecutionContext data) {
		super(data);
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
		List<DumpDependencyData> dumpDependencyDataList = context.getDumpDependencyDataList();
		for(DumpDependencyData dumpDependencyData:dumpDependencyDataList){
			if(!dumpDependencyData.isInUse()) continue;
			if(dumpDependencyData.getFirstPitName() != null){
				Pit pit = getPitFromPitName(dumpDependencyData.getFirstPitName());
				Dump d2 = context.getDumpfromDumpName(dumpDependencyData.getDependentDumpName());
				buildPitDumpDependencyEquation(pit, d2);
				
			} else if(dumpDependencyData.getFirstPitGroupName() != null){
				PitGroup pitGroup = context.getPitGroupfromName(dumpDependencyData.getFirstPitGroupName());
				Dump d2 = context.getDumpfromDumpName(dumpDependencyData.getDependentDumpName());
				buildPitGroupDumpDependencyEquation1(pitGroup, d2);
				buildPitGroupDumpDependencyEquation2(pitGroup, d2);
				
			} else if(dumpDependencyData.getFirstDumpName() != null){
				Dump d1 = context.getDumpfromDumpName(dumpDependencyData.getFirstDumpName());
				Dump d2 = context.getDumpfromDumpName(dumpDependencyData.getDependentDumpName());
				buildDumpDependencyEquation1(d1, d2);
				buildDumpDependencyEquation2(d1, d2);
			}
		}
	}
	
	
	private void buildDumpDependencyEquation1(Dump d1, Dump d2){
		if(!d1.isHasCapacity()) return;
		int timePeriodStart = context.getTimePeriodStart();
		int timePeriodEnd = context.getTimePeriodEnd();
		Set<Block> blocks = d1.getBlocks();
		for(int i=timePeriodStart; i<= timePeriodEnd; i++){
			Constraint c = new Constraint(Constraint.DUMP_DEPENDENCY);
			c.addVariable("d"+d1.getDumpNumber()+"t"+i, context.getScaledValue(new BigDecimal(d1.getCapacity())));
			for(Block block: blocks){
				for(int j=timePeriodStart; j<=i; j++){
					c.addVariable("p"+block.getPitNo()+"x"+block.getBlockNo()+"w"+d1.getDumpNumber()+"t"+j, new BigDecimal(1).negate());
				}
			}
			c.setEqualityType(Constraint.LESS_EQUAL);
			c.setValue(new BigDecimal(0));
			context.getConstraints().add(c);
		}
		
	}
	
	private void buildDumpDependencyEquation2(Dump d1, Dump d2){
		if(!d2.isHasCapacity()) return;
		int timePeriodStart = context.getTimePeriodStart();
		int timePeriodEnd = context.getTimePeriodEnd();
		Set<Block> blocks = d2.getBlocks();
		for(int i=timePeriodStart; i<= timePeriodEnd; i++){
			Constraint c = new Constraint(Constraint.DUMP_DEPENDENCY);
			for(Block block: blocks){
				for(int j=timePeriodStart; j<=i; j++){
					c.addVariable("p"+block.getPitNo()+"x"+block.getBlockNo()+"w"+d2.getDumpNumber()+"t"+j, new BigDecimal(1));
				}
			}
			c.addVariable("d"+d1.getDumpNumber()+"t"+i, context.getScaledValue(new BigDecimal(d2.getCapacity())).negate());
			c.setEqualityType(Constraint.LESS_EQUAL);
			c.setValue(new BigDecimal(0));
			context.getConstraints().add(c);
		}
		
	}
	
	private void buildPitDumpDependencyEquation(Pit p, Dump d2){
		if(!d2.isHasCapacity()) return;
		int timePeriodStart = context.getTimePeriodStart();
		int timePeriodEnd = context.getTimePeriodEnd();
		Bench lastBench = p.getBench(p.getBenches().size() - 1);
		Set<Block> blocks = d2.getBlocks();
		for(int i=timePeriodStart; i<= timePeriodEnd; i++){
			Constraint c = new Constraint(Constraint.DUMP_DEPENDENCY);
			for(Block block: blocks){
				for(int j=timePeriodStart; j<=i; j++){
					c.addVariable("p"+block.getPitNo()+"x"+block.getBlockNo()+"w"+d2.getDumpNumber()+"t"+j, new BigDecimal(1));
				}
			}
			c.addVariable("p"+p.getPitNo()+"b"+lastBench.getBenchNo()+"t"+i, context.getScaledValue(new BigDecimal(d2.getCapacity())).negate());
			c.setEqualityType(Constraint.LESS_EQUAL);
			c.setValue(new BigDecimal(0));
			context.getConstraints().add(c);
		}
		
	}

	private void buildPitGroupDumpDependencyEquation1(PitGroup pitGroup, Dump d2){
		
		Set<String> pits = pitGroup.getListChildPits();
		int timePeriodStart = context.getTimePeriodStart();
		int timePeriodEnd = context.getTimePeriodEnd();
		BigDecimal totalTonnage = new BigDecimal(0);
		List<Bench> benches = new ArrayList<Bench>();
		for(String pitName: pits) {
			Pit p = getPitFromPitName(pitName);
			Bench lastBench = p.getBench(p.getBenches().size() - 1);
			totalTonnage = totalTonnage.add(getBenchTonnesWt(lastBench));
			benches.add(lastBench);
		}
		for(int i=timePeriodStart; i<= timePeriodEnd; i++){
			Constraint c = new Constraint(Constraint.DUMP_DEPENDENCY);
			for(Bench bench: benches){
				List<String> variables = getAllVariablesForBench(bench);
				if(variables.size() == 0) continue;
				for(String variable: variables){
					if(variable.startsWith("sp")) continue;
					Matcher matcher = lastIntPattern.matcher(variable);
				      if (matcher.find()) {
				          String someNumberStr = matcher.group(1);
				          int year = Integer.parseInt(someNumberStr);
				          if(year > i) continue;
				          c.addVariable(variable, new BigDecimal(1).negate());
				      }						
				}
			}
			c.addVariable("pbr"+pitGroup.getPitGroupNumber()+"t"+i, totalTonnage);
			c.setEqualityType(Constraint.LESS_EQUAL);
			c.setValue(new BigDecimal(0));
			context.getConstraints().add(c);
		}
	}
	
	private void buildPitGroupDumpDependencyEquation2(PitGroup pitGroup, Dump d2){
		if(!d2.isHasCapacity()) return;
		int timePeriodStart = context.getTimePeriodStart();
		int timePeriodEnd = context.getTimePeriodEnd();
		Set<Block> blocks = d2.getBlocks();
		for(int i=timePeriodStart; i<= timePeriodEnd; i++){
			Constraint c = new Constraint(Constraint.DUMP_DEPENDENCY);
			for(Block block: blocks){
				for(int j=timePeriodStart; j<=i; j++){
					c.addVariable("p"+block.getPitNo()+"x"+block.getBlockNo()+"w"+d2.getDumpNumber()+"t"+j, new BigDecimal(1));
				}
			}
			c.addVariable("pbr"+pitGroup.getPitGroupNumber()+"t"+i, context.getScaledValue(new BigDecimal(d2.getCapacity())).negate());
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
	
	private List<String> getAllVariablesForBench(Bench bench){
		List<String> variables = new ArrayList<String>();
		List<Block> blocks= bench.getBlocks();
		for(Block block: blocks){
			List<String> variableList = context.getBlockVariableMapping().get(block.getId());
			if(variableList != null){
				//System.out.println("Block Id :"+ block.getId()+ " Variable Size:"+variableList.size());
				variables.addAll(context.getBlockVariableMapping().get(block.getId()));
			}		
		}
		
		return variables;
	}
}
