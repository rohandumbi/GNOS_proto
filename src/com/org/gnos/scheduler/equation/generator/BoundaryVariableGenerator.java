package com.org.gnos.scheduler.equation.generator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.core.Block;
import com.org.gnos.db.model.Stockpile;
import com.org.gnos.scheduler.equation.Constraint;
import com.org.gnos.scheduler.equation.ExecutionContext;
import com.org.gnos.scheduler.equation.SPBlock;
import com.org.gnos.scheduler.equation.SlidingWindowExecutionContext;

public class BoundaryVariableGenerator extends EquationGenerator{

	
	public BoundaryVariableGenerator(ExecutionContext data) {
		super(data);
	}
	
	@Override
	public void generate() {
		try {
			buildBenchConstraintVariables();
			if(!context.isGlobalMode()) {
				buildSPReclaimVariables();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	public void buildBenchConstraintVariables() {
		Map<Integer, Block> allBlocks = context.getBlocks();
		Map<Integer, List<String>> blockVariableMapping = context.getBlockVariableMapping();
		Set<Integer> blockIds = blockVariableMapping.keySet();
		for(int blockId: blockIds){
			Constraint c = new Constraint(Constraint.BOUNDARY_VARIABLE);
			List<String> variables = blockVariableMapping.get(blockId);
			Block b = allBlocks.get(blockId);
			for(String variable: variables){
				if(!variable.startsWith("sp")){
					c.addVariable(variable, new BigDecimal(1));
				}					
			}			
			c.setEqualityType(Constraint.LESS_EQUAL);
			c.setValue(context.getScaledValue(new BigDecimal(context.getTonnesWtForBlock(b))));
			context.getConstraints().add(c);
		}
	}
	
	public void buildSPReclaimVariables() {
		SlidingWindowExecutionContext swctx = (SlidingWindowExecutionContext) context;
		Map<Integer, List<String>> spblockVariableMapping = swctx.getSPBlockVariableMapping();
		Set<Integer> spNos = spblockVariableMapping.keySet();
		for(int spNo: spNos){
			Stockpile sp = context.getStockpileFromNo(spNo);			
			if(sp == null) continue;
			Constraint c = new Constraint(Constraint.BOUNDARY_VARIABLE);
			SPBlock spb = swctx.getSPBlock(spNo);
			if(spb == null || spb.getTonnesWt() == 0) continue;
			double capacity = spb.getTonnesWt();
			List<String> variables = spblockVariableMapping.get(spNo);
			for(String variable: variables){
				c.addVariable(variable, new BigDecimal(1));
			}
			c.setEqualityType(Constraint.LESS_EQUAL);
			c.setValue(new BigDecimal(capacity));
			context.getConstraints().add(c);
		}
	}
	
}
