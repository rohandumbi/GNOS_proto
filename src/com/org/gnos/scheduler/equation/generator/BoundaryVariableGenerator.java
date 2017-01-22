package com.org.gnos.scheduler.equation.generator;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.core.Block;
import com.org.gnos.scheduler.equation.ExecutionContext;

public class BoundaryVariableGenerator extends EquationGenerator{

	
	public BoundaryVariableGenerator(ExecutionContext data) {
		super(data);
	}
	
	@Override
	public void generate() {
		try {
			buildBenchConstraintVariables();
			output.flush();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	public void buildBenchConstraintVariables() {
		Map<Integer, Block> allBlocks = context.getBlocks();
		Map<Integer, List<String>> blockVariableMapping = context.getBlockVariableMapping();
		Set<Integer> blockIds = blockVariableMapping.keySet();
		for(int blockId: blockIds){
			List<String> variables = blockVariableMapping.get(blockId);
			Block b = allBlocks.get(blockId);
			StringBuilder sb = new StringBuilder("");
			for(String variable: variables){
				write(variable + " >= 0" );
				if(!variable.startsWith("sp")){
					sb.append(variable +"+");
				}					
			}
			String eq = sb.toString().substring(0,sb.length() -1) +" <= "+context.getTonnesWtForBlock(b);
			write(eq);
		}
	}
	
}
