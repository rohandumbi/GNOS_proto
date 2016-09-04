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
import com.org.gnos.db.model.CapexData;
import com.org.gnos.db.model.CapexInstance;
import com.org.gnos.db.model.PitGroup;

public class CapexEquationGenerator extends EquationGenerator{

	
	public CapexEquationGenerator(InstanceData data) {
		super(data);
	}
	
	@Override
	public void generate() {
		
		int bufferSize = 8 * 1024;
		try {
			output = new BufferedOutputStream(new FileOutputStream("capexconstraints.txt"), bufferSize);
			bytesWritten = 0;
			buildCapexEquations();
			output.flush();
			output.close();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	private void buildCapexEquations() {
		int timeperiod = scenarioConfigutration.getTimePeriod();
		List<CapexData> capexDataList = scenarioConfigutration.getCapexDataList();
		int capexCount = 0;
		for(CapexData cd: capexDataList) {
			capexCount++;
			List<CapexInstance> capexInstanceList = cd.getListOfCapexInstances();
			int capexInstanceCount = 0;
			String groupName = null;
			int groupType = -1 ;
			List<String> capexvaraibles = new ArrayList<String>();
			for(CapexInstance ci: capexInstanceList){
				capexInstanceCount++;
				groupName = ci.getGroupingName();
				groupType = ci.getGroupingType();
				String cv = ci.getExpansionCapacity()+"c"+capexCount+"i"+capexInstanceCount;
				capexvaraibles.add(cv);
				
			}
			buildSet1Equations();
			buildSet2Equations(cd, capexCount);
			buildSet3Equations(cd, capexCount);
		}
	}
	
	private void buildSet1Equations() {
		
	}
	
	private void buildSet2Equations(CapexData cd, int capexNumber) {
		int timeperiod = scenarioConfigutration.getTimePeriod();
		List<CapexInstance> capexInstanceList = cd.getListOfCapexInstances();
		int capexInstanceCount = capexInstanceList.size();
		
		for(int j=1; j<capexInstanceCount; j++){			
			for(int i=1; i<= timeperiod; i++){
				StringBuffer sb = new StringBuffer("");
				for(int ii=1; ii<=i; ii++){
					if(ii > 1){
						sb.append(" + ");
					}
					sb.append("c"+capexNumber+"i"+j+"t"+ii);
				}
				
				
				sb.append(" - "+"c"+capexNumber+"i"+(j+1)+"t"+i+" >= 0 ");
				write(sb.toString());
			}			
		}
	}

	private void buildSet3Equations(CapexData cd, int capexNumber) {
		int timeperiod = scenarioConfigutration.getTimePeriod();
		int capexInstanceNo = 0;
		List<CapexInstance> capexInstanceList = cd.getListOfCapexInstances();
		for(CapexInstance ci: capexInstanceList){
			capexInstanceNo++;
			StringBuffer sb = new StringBuffer("");
			for(int i=1; i<= timeperiod; i++){
				if(i > 1){
					sb.append(" + ");
				}
				sb.append("c"+capexNumber+"i"+capexInstanceNo+"t"+i);			
			}
			sb.append(" <= 1 ");
			write(sb.toString());
		}
	}
	
	private String getBlockVariables(String groupName, int groupType, int timeperiod) {
		String eq= "";
		if( groupType == CapexInstance.SELECTION_PIT){
			List<Block> blocks = new ArrayList<Block>();
			com.org.gnos.db.model.Pit p = projectConfiguration.getPitfromPitName(groupName);
			Pit pit = serviceInstanceData.getPits().get(p.getPitNumber());
			Set<Bench> benches = pit.getBenches();
			for(Bench b: benches){
				blocks.addAll(b.getBlocks());
			}
		} else if(groupType == CapexInstance.SELECTION_PIT_GROUP){
			List<Block> blocks = new ArrayList<Block>();
			PitGroup pg = projectConfiguration.getPitGroupfromName(groupName);
			Set<Integer> pitNumbers = getPitsFromPitGroup(pg);
			for(int pitNumber: pitNumbers){
				Pit pit = serviceInstanceData.getPits().get(pitNumber);
				Set<Bench> benches = pit.getBenches();
				for(Bench b: benches){
					blocks.addAll(b.getBlocks());
				}
			}
		}
		return eq;		
	}
	
	private void getBlocks(List<Block> blocks) {
		Map<Integer, List<String>> blockVariableMapping = serviceInstanceData.getBlockVariableMapping();
		Set<Integer> blockIds = blockVariableMapping.keySet();
		for(int blockId: blockIds){
			List<String> variables = blockVariableMapping.get(blockId);
			Block b = blocks.get(blockId);
			StringBuilder sb = new StringBuilder("");
			for(String variable: variables){
				write(variable + " >= 0" );
				sb.append(variable +"+");
			}
			//String eq = sb.toString().substring(0,sb.length() -1) +" <= "+b.getField(tonnesWeightFieldName);
			//write(eq);
		}
	}
	
}
