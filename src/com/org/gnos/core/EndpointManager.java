package com.org.gnos.core;

import com.org.gnos.services.endpoints.BenchConstraintEndpoint;
import com.org.gnos.services.endpoints.CapexEndpoint;
import com.org.gnos.services.endpoints.DumpDependencyEndpoint;
import com.org.gnos.services.endpoints.DumpEndpoint;
import com.org.gnos.services.endpoints.ExpressionEndpoint;
import com.org.gnos.services.endpoints.FieldEndpoint;
import com.org.gnos.services.endpoints.FixedCostEndpoint;
import com.org.gnos.services.endpoints.GradeConstraintEndpoint;
import com.org.gnos.services.endpoints.ModelEndpoint;
import com.org.gnos.services.endpoints.OpexEndpoint;
import com.org.gnos.services.endpoints.PitDependencyEndpoint;
import com.org.gnos.services.endpoints.ProcessConstraintEndpoint;
import com.org.gnos.services.endpoints.ProjectEndpoint;
import com.org.gnos.services.endpoints.ScenarioEndpoint;
import com.org.gnos.services.endpoints.StockpileEndpoint;

public class EndpointManager {

	public static void start() {
		new ProjectEndpoint();
		new FieldEndpoint();
		new ExpressionEndpoint();
		new ModelEndpoint();
		new DumpEndpoint();
		new StockpileEndpoint();
		new ScenarioEndpoint();
		new OpexEndpoint();
		new FixedCostEndpoint();
		new ProcessConstraintEndpoint();
		new BenchConstraintEndpoint();
		new GradeConstraintEndpoint();
		new PitDependencyEndpoint();
		new DumpDependencyEndpoint();
		new CapexEndpoint();

	}
}
