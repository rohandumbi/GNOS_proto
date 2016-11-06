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
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.PitGroup;

public class DumpCapacityEquationGenerator extends EquationGenerator{
	
	private Map<Integer, List<String>> blockVariableMapping;
	
	public DumpCapacityEquationGenerator(InstanceData data) {
		super(data);
		this.blockVariableMapping = serviceInstanceData.getBlockVariableMapping();
	}
	
	@Override
	public void generate() {
		
		int bufferSize = 8 * 1024;
		try {
			output = new BufferedOutputStream(new FileOutputStream("dumpCapacity.txt"), bufferSize);
			bytesWritten = 0;
			buildCapacityEquations();
			output.flush();
			output.close();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	
	private void buildCapacityEquations() {
		List<Dump> dumpList = projectConfiguration.getDumpList();
		int timeperiod = scenarioConfigutration.getTimePeriod();
		for(Dump d:dumpList){
			if(!d.isHasCapacity()) continue;
			StringBuilder sb= new StringBuilder("");
			Set<Block> dumpblocks = d.getBlocks();
			List<Pit> pits = new ArrayList<Pit>();
			if(d.getMappingType() == 0){
				Pit pit = getPitFromPitName(d.getAssociatedPit().getPitName());
				pits.add(pit);
			} else {
				PitGroup pitgroup = d.getAssociatedPitGroup();
				Set<Integer> pitNos = serviceInstanceData.flattenPitGroup(pitgroup);
				for(int pitNo: pitNos){
					pits.add(serviceInstanceData.getPits().get(pitNo));
				}
			}			
			for(Pit pit: pits){
				Set<Bench> benches = pit.getBenches();
				for(Bench b: benches){
					List<Block> blocks = b.getBlocks();
					for(Block block: blocks){
						if(!dumpblocks.contains(block)) continue;
						for(int i= 1; i<=timeperiod; i++){
							sb.append(" +p"+block.getPitNo()+"x"+block.getBlockNo()+"w"+d.getDumpNumber()+"t"+i);
						}
					}
				}
			}
			if(sb.length() > 0){
				sb.append(" <=  "+d.getCapacity());
				write(sb.toString());
			}
			
		}
	}
	
	
	private Pit getPitFromPitName(String pitname){
		Map<Integer, Pit> pits = serviceInstanceData.getPits();
		Set<Integer> pitNos = pits.keySet();
		for(int pitNo: pitNos){
			Pit pit = pits.get(pitNo);
			if(pit.getPitName().equals(pitname)){
				return pit;
			}
		}
		return null;
	}
}
