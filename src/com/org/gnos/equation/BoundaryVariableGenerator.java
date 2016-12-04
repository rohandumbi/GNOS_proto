package com.org.gnos.equation;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.core.Block;
import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.core.ScenarioConfigutration;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Grade;
import com.org.gnos.db.model.GradeConstraintData;
import com.org.gnos.db.model.Model;
import com.org.gnos.db.model.Pit;
import com.org.gnos.db.model.PitGroup;
import com.org.gnos.db.model.Process;
import com.org.gnos.db.model.ProcessJoin;
import com.org.gnos.db.model.Product;
import com.org.gnos.db.model.ProductJoin;

public class BoundaryVariableGenerator extends EquationGenerator{

	
	public BoundaryVariableGenerator(InstanceData data) {
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
		String tonnesWeightFieldName = projectConfiguration.getRequiredFieldMapping().get("tonnes_wt");
		Map<Integer, Block> allBlocks = serviceInstanceData.getBlocks();
		Map<Integer, List<String>> blockVariableMapping = serviceInstanceData.getBlockVariableMapping();
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
