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
import com.org.gnos.scheduler.equation.Constraint;
import com.org.gnos.scheduler.equation.ExecutionContext;

public class BenchProportionEquationGenerator extends EquationGenerator{

	private Map<Integer, List<String>> blockVariableMapping;
	final Pattern lastIntPattern = Pattern.compile("[^0-9]+([0-9]+)$");
	
	public BenchProportionEquationGenerator(ExecutionContext data) {
		super(data);
		this.blockVariableMapping = context.getBlockVariableMapping();
	}
	
	@Override
	public void generate() {
		try {
			buildBenchProportionEquations();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	public void buildBenchProportionEquations() {
		Map<Integer, Pit> pits = context.getPits();
		int timePeriodStart = context.getTimePeriodStart();
		int timePeriodEnd = context.getTimePeriodEnd();
		Set<Integer> pitNos = pits.keySet();
		for(int pitNo: pitNos){
			Pit pit = pits.get(pitNo);
			Set<Bench> benches = pit.getBenches();
			for(Bench bench: benches){
				List<Block> blocks = bench.getBlocks();
				Block lastBlock = null;
				for(Block block: blocks) {
					if(lastBlock != null) {
						List<String> blockvariables1 = this.blockVariableMapping.get(lastBlock.getId());
						List<String> blockvariables2 = this.blockVariableMapping.get(block.getId());
						blockvariables1 = blockvariables1 == null ? new ArrayList<String>() : blockvariables1;
						blockvariables2 = blockvariables2 == null ? new ArrayList<String>() : blockvariables2;
						double tonnage1 = context.getTonnesWtForBlock(lastBlock);
						double tonnage2 = context.getTonnesWtForBlock(block);
						for(int i=timePeriodStart; i<= timePeriodEnd; i++){
							Constraint constraint = new Constraint();
							for(String variable : blockvariables1) {
								if(variable.startsWith("sp")) continue;
								Matcher matcher = lastIntPattern.matcher(variable);
								if (matcher.find()) {
								      String yearStr = matcher.group(1);
								      int year = Integer.parseInt(yearStr);
								      if(year != i ) continue;
									  constraint.addVariable(variable, context.getScaledValue(new BigDecimal(tonnage2)));
								}								
							}
							for(String variable : blockvariables2) {
								if(variable.startsWith("sp")) continue;
								Matcher matcher = lastIntPattern.matcher(variable);
								if (matcher.find()) {
								      String yearStr = matcher.group(1);
								      int year = Integer.parseInt(yearStr);
								      if(year != i ) continue;
								      constraint.addVariable(variable, context.getScaledValue(new BigDecimal(tonnage1)).negate());
								}
							}
							if(constraint.getVariables().size() > 0){
								constraint.setType(Constraint.EQUAL);
								constraint.setValue(new BigDecimal(0));
								context.getConstraints().add(constraint);
							}
						}
					}
					lastBlock = block;
				}
			}
		}
	}
	
	@Override
	protected String formatDecimalValue(BigDecimal bd) {
		return bd.stripTrailingZeros().toString();
	}
}
