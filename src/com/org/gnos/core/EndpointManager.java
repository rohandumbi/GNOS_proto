package com.org.gnos.core;

import com.org.gnos.licensing.GNOSLicense;
import com.org.gnos.services.endpoints.BenchConstraintEndpoint;
import com.org.gnos.services.endpoints.CapexEndpoint;
import com.org.gnos.services.endpoints.CycleTimeEndpoint;
import com.org.gnos.services.endpoints.CycleTimeMappingEndpoint;
import com.org.gnos.services.endpoints.DumpDependencyEndpoint;
import com.org.gnos.services.endpoints.DumpEndpoint;
import com.org.gnos.services.endpoints.ExpressionEndpoint;
import com.org.gnos.services.endpoints.FieldEndpoint;
import com.org.gnos.services.endpoints.FixedCostEndpoint;
import com.org.gnos.services.endpoints.GradeConstraintEndpoint;
import com.org.gnos.services.endpoints.ModelEndpoint;
import com.org.gnos.services.endpoints.OpexEndpoint;
import com.org.gnos.services.endpoints.PitDependencyEndpoint;
import com.org.gnos.services.endpoints.PitEndpoint;
import com.org.gnos.services.endpoints.PitGroupEndpoint;
import com.org.gnos.services.endpoints.ProcessConstraintEndpoint;
import com.org.gnos.services.endpoints.ProcessEndpoint;
import com.org.gnos.services.endpoints.ProcessJoinEndpoint;
import com.org.gnos.services.endpoints.ProcessTreeEndpoint;
import com.org.gnos.services.endpoints.ProductEndpoint;
import com.org.gnos.services.endpoints.ProductJoinEndpoint;
import com.org.gnos.services.endpoints.ProjectEndpoint;
import com.org.gnos.services.endpoints.ReportEndpoint;
import com.org.gnos.services.endpoints.RequiredFieldEndpoint;
import com.org.gnos.services.endpoints.ScenarioEndpoint;
import com.org.gnos.services.endpoints.SchedulerEndpoint;
import com.org.gnos.services.endpoints.StockpileEndpoint;
import com.org.gnos.services.endpoints.TruckParameterCycleTimeEndpoint;
import com.org.gnos.services.endpoints.TruckParameterPayloadEndpoint;

import static spark.Spark.before;
import static spark.Spark.halt;

public class EndpointManager {

	public static final boolean DEV_MODE = false;
	
	public static void start() {
		new ProjectEndpoint();
		new FieldEndpoint();
		new RequiredFieldEndpoint();
		new PitEndpoint();
		new PitGroupEndpoint();
		new ExpressionEndpoint();
		//new GradeEndpoint();
		new ModelEndpoint();
		new ProcessEndpoint();
		new ProcessTreeEndpoint();
		new ProcessJoinEndpoint();
		new ProductEndpoint();
		new ProductJoinEndpoint();
		new DumpEndpoint();
		new StockpileEndpoint();
		new TruckParameterCycleTimeEndpoint();
		new CycleTimeMappingEndpoint();
		new CycleTimeEndpoint();
		new ScenarioEndpoint();
		new OpexEndpoint();
		new FixedCostEndpoint();;
		new ProcessConstraintEndpoint();
		new BenchConstraintEndpoint();
		new GradeConstraintEndpoint();
		new PitDependencyEndpoint();
		new DumpDependencyEndpoint();
		new CapexEndpoint();
		new SchedulerEndpoint();
		new RequiredFieldEndpoint();
		new TruckParameterPayloadEndpoint();
		new ReportEndpoint();
		
		before((request, response) -> {
			if(DEV_MODE) return;
		    try {
		    	boolean isValid = GNOSLicense.isValid();
		    	if(!isValid) {
		    		halt(401, "License has expired");
		    	}
		    } catch(Exception e) {
		    	halt(401, e.getMessage());
		    }
		});
	}
}
