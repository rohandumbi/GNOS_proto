package com.org.gnos.scheduler.equation.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.core.Bench;
import com.org.gnos.core.Block;
import com.org.gnos.core.Pit;
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.PitGroup;
import com.org.gnos.scheduler.equation.ExecutionContext;

public class DumpCapacityEquationGenerator extends EquationGenerator{
	
	public DumpCapacityEquationGenerator(ExecutionContext data) {
		super(data);
	}
	
	@Override
	public void generate() {

		buildCapacityEquations();

	}

	
	private void buildCapacityEquations() {
		List<Dump> dumpList = context.getProjectConfig().getDumpList();
		int timePeriodStart = context.getTimePeriodStart();
		int timePeriodEnd = context.getTimePeriodEnd();
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
				Set<Integer> pitNos = context.flattenPitGroup(pitgroup);
				for(int pitNo: pitNos){
					pits.add(context.getPits().get(pitNo));
				}
			}			
			for(Pit pit: pits){
				Set<Bench> benches = pit.getBenches();
				for(Bench b: benches){
					List<Block> blocks = b.getBlocks();
					for(Block block: blocks){
						if(!dumpblocks.contains(block)) continue;
						for(int i= timePeriodStart; i<=timePeriodEnd; i++){
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
}
