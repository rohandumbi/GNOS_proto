package com.org.gnos.scheduler.equation.generator;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.core.Block;
import com.org.gnos.db.model.Process;
import com.org.gnos.db.model.Stockpile;
import com.org.gnos.scheduler.equation.ExecutionContext;
import com.org.gnos.scheduler.equation.SPBlock;
import com.org.gnos.scheduler.equation.SlidingWindowExecutionContext;

public class SPReclaimEquationGenerator extends EquationGenerator{
	

	public SPReclaimEquationGenerator(ExecutionContext data) {
		super(data);
	}
	
	@Override
	public void generate() {

		try {
			if(context.isSpReclaimEnabled() && context.isGlobalMode()) {
				buildStockpileEquations();
			} else {
				buildSWStockpileEquations();
			}
			output.flush();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	private void buildStockpileEquations() {
		List<Stockpile> stockpiles = context.getStockpiles();
		int timePeriodStart = context.getTimePeriodStart();
		int timePeriodEnd = context.getTimePeriodEnd();
		for(Stockpile sp: stockpiles) {
			if(!sp.isReclaim()) continue;
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
	
	private void buildSWStockpileEquations() {
		
		SlidingWindowExecutionContext swctx = (SlidingWindowExecutionContext) context;
		int timePeriodStart = context.getTimePeriodStart();
		int timePeriodEnd = context.getTimePeriodEnd();
		Map<Integer, SPBlock> spBlockMapping = swctx.getSpBlockMapping();
		Set<Integer> spNos = spBlockMapping.keySet();			
		for(int spNo: spNos){
			Stockpile sp = context.getStockpileFromNo(spNo);
			if(!sp.isReclaim() && !(sp.getCapacity() > 0)) continue;
			SPBlock spb = spBlockMapping.get(spNo);
			if(spb == null || spb.getTonnesWt() == 0) continue;
			Set<Block> blocks = sp.getBlocks();
			double capacity = sp.getCapacity();
			if(spb.getTonnesWt() > 0) {
				capacity = capacity - spb.getTonnesWt();
			}
			for(int i= timePeriodStart; i <= timePeriodEnd; i++) {
				StringBuilder sbc_sp = new StringBuilder("");
				StringBuilder sbc_spr = new StringBuilder("");
				for(Block b: blocks){
					StringBuilder sb_sp = new StringBuilder("");					
					for(int j=timePeriodStart; j<=i; j++){
						sb_sp.append(" + p"+b.getPitNo()+"x"+b.getBlockNo()+"s"+sp.getStockpileNumber()+"t"+j);						
					}
					sbc_sp.append(sb_sp);
				}
				Set<Process> processes = spb.getProcesses();
				for(int j=timePeriodStart; j<=i; j++){
					if(j == 1) continue;
					for(Process p: processes) {
						sbc_spr.append(" - sp"+sp.getStockpileNumber()+"x0p"+p.getProcessNo()+"t"+j);
					}
				}
				 
				if(capacity > 0 && (sbc_sp.length() > 0 || sbc_spr.length() > 0)){
					write(sbc_sp.toString()+sbc_spr.toString()+" <= "+capacity);
				}
			}
		}
	
	}
}
