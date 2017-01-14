package com.org.gnos.equation;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.core.Block;

public class BoundaryVariableGenerator extends EquationGenerator{

	
	public BoundaryVariableGenerator(EquationContext data) {
		super(data);
	}
	
	@Override
	public void generate() {
		
		int bufferSize = 8 * 1024;
		try {
			output = new BufferedOutputStream(new FileOutputStream("boundaryVariable.txt"), bufferSize);
			bytesWritten = 0;
			buildBenchConstraintVariables();
			output.flush();
			output.close();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	public void buildBenchConstraintVariables() {
		String tonnesWeightFieldName = context.getTonnesWeightAlisName();
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
			String eq = sb.toString().substring(0,sb.length() -1) +" <= "+b.getField(tonnesWeightFieldName);
			write(eq);
		}
	}
	
}
