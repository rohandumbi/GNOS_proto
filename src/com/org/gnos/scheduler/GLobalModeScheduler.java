package com.org.gnos.scheduler;

import com.org.gnos.db.model.RunConfig;
import com.org.gnos.scheduler.equation.GlobalModeExecutionContext;
import com.org.gnos.scheduler.processor.DBStorageHelper;

public class GLobalModeScheduler extends BaseScheduler {

	
	public GLobalModeScheduler(RunConfig runConfig) {
		super();
		context = new GlobalModeExecutionContext(runConfig.getProjectId(), runConfig.getScenarioId());
		context.setTimePeriodEnd(runConfig.getPeriod());
		context.setSpReclaimEnabled(runConfig.isReclaim());
		helper = new DBStorageHelper();
		helper.setContext(context);
	}

	@Override
	public void execute() {
		helper.start();
		generateEquations(0);
		runSolver(0);
		helper.stop();
	}

}
