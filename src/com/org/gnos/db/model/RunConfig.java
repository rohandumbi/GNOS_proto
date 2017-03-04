package com.org.gnos.db.model;

import java.util.HashMap;
import java.util.Map;

public class RunConfig {

	public static final short GLOBAL_MODE = 1;
	public static final short SLIDING_WINDOW_MODE = 2;
	
	private short mode;
	private Map<String, Boolean> eqnenablestate;
	
	public RunConfig() {
		this.setMode(GLOBAL_MODE);
		this.setEqnenablestate(new HashMap<String, Boolean>());

		eqnenablestate.put("PROCESS_CONSTRAINT", true);
		eqnenablestate.put("BENCH_CONSTRAINT", true);
		eqnenablestate.put("GRADE_CONSTRAINT", true);
		eqnenablestate.put("BENCH_PROPORTION", true);
		eqnenablestate.put("DUMP_CAPACITIES", true);
		eqnenablestate.put("CAPEX", true);
		eqnenablestate.put("DUMP_DEPENDENCY", true);
		eqnenablestate.put("PIT_DEPENDENCY", true);
		eqnenablestate.put("BOUNDARY_VARIABLES", true);
	}

	public Map<String, Boolean> getEqnenablestate() {
		return eqnenablestate;
	}

	public void setEqnenablestate(Map<String, Boolean> eqnenablestate) {
		this.eqnenablestate = eqnenablestate;
	}
	
	public void addEqnenablestate(String eqnName, Boolean state) {
		this.eqnenablestate.put(eqnName, state);
	}

	public short getMode() {
		return mode;
	}

	public void setMode(short mode) {
		this.mode = mode;
	}
}
