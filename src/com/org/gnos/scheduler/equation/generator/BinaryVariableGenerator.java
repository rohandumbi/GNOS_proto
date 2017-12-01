package com.org.gnos.scheduler.equation.generator;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.core.Bench;
import com.org.gnos.core.Pit;
import com.org.gnos.db.model.CapexData;
import com.org.gnos.db.model.CapexInstance;
import com.org.gnos.scheduler.equation.ExecutionContext;

public class BinaryVariableGenerator extends EquationGenerator{

	public BinaryVariableGenerator(ExecutionContext data) {
		super(data);
	}
	
	@Override
	public void generate() {
		buildBinaryVariables();
		buildCapexBinaryVariables();
	}
	
	public void buildBinaryVariables() {
		
		int timePeriodStart = context.getTimePeriodStart();
		int timePeriodEnd = context.getTimePeriodEnd();
		Map<Integer, Pit> pits = context.getPits();
		Set<Integer> pitNos = pits.keySet();
		for(Integer pitNo:pitNos){
			Pit pit = pits.get(pitNo);
			Set<Bench> benches = pit.getBenches();
			for(Bench b: benches){
				for(int i=timePeriodStart; i<= timePeriodEnd;i++){
					context.getBinaries().add("p"+pitNo+"b"+b.getBenchNo()+"t"+i);
				}
			}
		}
		
	}
	
	public void buildCapexBinaryVariables() {
		int timePeriodStart = context.getTimePeriodStart();
		int timePeriodEnd = context.getTimePeriodEnd();
		List<CapexData> capexDataList = context.getCapexDataList();
		int capexCount = 0;
		for(CapexData cd: capexDataList) {
			capexCount++;
			List<CapexInstance> capexInstanceList = cd.getListOfCapexInstances();
			for (int j = 1; j <= capexInstanceList.size(); j++) {
				CapexInstance ci = capexInstanceList.get(j);
				if(!ci.isInUse()) continue;
				for(int i= timePeriodStart; i <= timePeriodEnd ; i++){
					context.getBinaries().add("c"+capexCount+"i"+j+"t"+i);
				}
			}
		}
	}
}
