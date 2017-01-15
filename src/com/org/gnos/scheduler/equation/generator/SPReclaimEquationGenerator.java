package com.org.gnos.scheduler.equation.generator;

import java.util.List;
import java.util.Set;

import com.org.gnos.core.Block;
import com.org.gnos.db.model.Process;
import com.org.gnos.db.model.Stockpile;
import com.org.gnos.scheduler.equation.ExecutionContext;

public class SPReclaimEquationGenerator extends EquationGenerator{
	

	public SPReclaimEquationGenerator(ExecutionContext data) {
		super(data);
	}
	
	@Override
	public void generate() {

		try {
			if(context.isSpReclaimEnabled()) {
				buildStockpileEquations();
			}		
			output.flush();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	private void buildStockpileEquations() {
		List<Stockpile> stockpiles = context.getProjectConfig().getStockPileList();
		int timePeriodStart = context.getTimePeriodStart();
		int timePeriodEnd = context.getTimePeriodEnd();
		for(Stockpile sp: stockpiles) {
			if(!sp.isReclaim()) return;
			Set<Block> blocks = sp.getBlocks();
			for(int i= timePeriodStart; i <= timePeriodEnd; i++) {
				StringBuilder sbc_sp = new StringBuilder("");
				StringBuilder sbc_spr = new StringBuilder("");
				for(Block b: blocks){
					StringBuilder sb_sp = new StringBuilder("");
					StringBuilder sb_spr = new StringBuilder("");
					sbc_sp.append(" + p"+b.getPitNo()+"x"+b.getBlockNo()+"s"+sp.getStockpileNumber()+"t1");
					for(int j=2; j<=i; j++){
						sb_sp.append(" + p"+b.getPitNo()+"x"+b.getBlockNo()+"s"+sp.getStockpileNumber()+"t"+(j-1));
						Set<Process> processes = b.getProcesses();
						for(Process p: processes) {
							sb_spr.append(" - sp"+sp.getStockpileNumber()+"x"+b.getBlockNo()+"p"+p.getProcessNo()+"t"+j);
						}						
					}
					if(sb_sp.length() > 0 || sb_spr.length() > 0){
						write(sb_sp.toString()+sb_spr.toString()+" >= 0");
					}
					sbc_sp.append(sb_sp);
					sbc_spr.append(sb_spr);
				}
				if(sp.getCapacity() > 0 && (sbc_sp.length() > 0 || sbc_spr.length() > 0)){
					write(sbc_sp.toString()+sbc_spr.toString()+" <= "+sp.getCapacity());
				}
			}
			
		}
	}
}
