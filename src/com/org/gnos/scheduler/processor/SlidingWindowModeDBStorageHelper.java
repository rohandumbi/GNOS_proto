package com.org.gnos.scheduler.processor;

import com.org.gnos.scheduler.equation.SlidingWindowExecutionContext;

public class SlidingWindowModeDBStorageHelper extends DBStorageHelper {

	@Override
	public void processRecord(Record record){
		SlidingWindowExecutionContext swctx = (SlidingWindowExecutionContext)context;
		if(record.getOriginType() == Record.ORIGIN_PIT) {
			swctx.addMinedTonnesWeightForBlock(record.getBlockNo(), record.getValue());
		} else {
			swctx.reclaimTonnesWeightForStockpile(record.getOriginSpNo(), record.getValue());
		}
		if(record.getDestinationType() == Record.DESTINATION_SP) {
			swctx.addTonnesWeightForStockpile(record.getDestSpNo(), record.getBlockNo(), record.getValue());
		}
	}
	
	@Override
	public void stop() {
		super.stop();
		SlidingWindowExecutionContext swctx = (SlidingWindowExecutionContext)context;
		swctx.processStockpiles();
	}
}
