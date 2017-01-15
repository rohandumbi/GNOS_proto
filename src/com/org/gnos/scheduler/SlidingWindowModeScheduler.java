package com.org.gnos.scheduler;

import com.org.gnos.core.ScenarioConfigutration;
import com.org.gnos.scheduler.equation.SlidingWindowExecutionContext;

public class SlidingWindowModeScheduler extends BaseScheduler{

	public SlidingWindowModeScheduler() {
		super();
		context = new SlidingWindowExecutionContext();
	}
	
	private void loadData() {
		
	}
	
	private void processResults() {
		
	}
	
	
	@Override
	public void execute() {
		SlidingWindowExecutionContext swcontext = (SlidingWindowExecutionContext)context ;
		short period = swcontext.getPeriod();
		short window = swcontext.getWindow();
		short stepsize = swcontext.getStepsize();
		int startYear = ScenarioConfigutration.getInstance().getStartYear();
		int timePeriod = ScenarioConfigutration.getInstance().getTimePeriod();
		for(int i=1; i<= period; i++){
			int timePeriodStart = (i -1)*stepsize + 1;
			int timePeriodEnd = (i -1)*stepsize + window;
			if(timePeriodEnd > timePeriod){
				timePeriodEnd = timePeriod;
			}
			if(timePeriodStart > timePeriod){
				break;
			}
			swcontext.setStartYear(startYear + (i -1)*stepsize);
			swcontext.setTimePeriodStart(timePeriodStart);
			swcontext.setTimePeriodEnd(timePeriodEnd);
			loadData();
			generateEquations(i);
			runSolver(timePeriodStart);
			processResults();
		}
	}

}
