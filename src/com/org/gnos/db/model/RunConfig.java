package com.org.gnos.db.model;

import java.util.HashMap;
import java.util.Map;

public class RunConfig {

	public static final short GLOBAL_MODE = 1;
	public static final short SLIDING_WINDOW_MODE = 2;
	
	private short mode;
	private Map<String, Boolean> eqnenablestate;
	
	private int projectId;
	private int scenarioId;
	private boolean isReclaim;
	private short period;
	private short window;
	private short stepSize;
	
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

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public int getScenarioId() {
		return scenarioId;
	}

	public void setScenarioId(int scenarioId) {
		this.scenarioId = scenarioId;
	}

	public boolean isReclaim() {
		return isReclaim;
	}

	public void setReclaim(boolean isReclaim) {
		this.isReclaim = isReclaim;
	}

	public short getPeriod() {
		return period;
	}

	public void setPeriod(short period) {
		this.period = period;
	}
	
	public short getWindow() {
		return window;
	}

	public void setWindow(short window) {
		this.window = window;
	}

	public short getStepSize() {
		return stepSize;
	}

	public void setStepSize(short stepSize) {
		this.stepSize = stepSize;
	}

	@Override
	public String toString() {
		return "RunConfig [mode=" + mode + ", projectId=" + projectId
				+ ", scenarioId=" + scenarioId + ", isReclaim=" + isReclaim + ", period=" + period + ", window="
				+ window + ", stepSize=" + stepSize + "]";
	}

	
}
