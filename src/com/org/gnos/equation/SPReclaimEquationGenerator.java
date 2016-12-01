package com.org.gnos.equation;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.core.Block;
import com.org.gnos.db.model.Stockpile;
import com.org.gnos.db.model.Process;

public class SPReclaimEquationGenerator extends EquationGenerator{
	
	private Map<Integer, List<String>> blockVariableMapping;
	
	public SPReclaimEquationGenerator(InstanceData data) {
		super(data);
		this.blockVariableMapping = serviceInstanceData.getBlockVariableMapping();
	}
	
	@Override
	public void generate() {
		
		int bufferSize = 8 * 1024;
		try {
			output = new BufferedOutputStream(new FileOutputStream("spReclaim.txt"), bufferSize);
			bytesWritten = 0;
			if(serviceInstanceData.isSpReclaimEnabled()) {
				buildStockpileEquations();
			}		
			output.flush();
			output.close();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	private void buildStockpileEquations() {
		List<Stockpile> stockpiles = projectConfiguration.getStockPileList();
		int timeperiod = scenarioConfigutration.getTimePeriod();
		for(Stockpile sp: stockpiles) {
			if(!sp.isReclaim()) return;
			Set<Block> blocks = sp.getBlocks();
			for(int i= 1; i <= timeperiod; i++) {
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
