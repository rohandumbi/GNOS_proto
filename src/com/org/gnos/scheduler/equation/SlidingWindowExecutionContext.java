package com.org.gnos.scheduler.equation;

import com.org.gnos.core.ScenarioConfigutration;

public class SlidingWindowExecutionContext extends ExecutionContext {

	private short period;
	private short window;
	private short stepsize;
	private short currPeriod;
	
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
	public short getStepsize() {
		return stepsize;
	}
	public void setStepsize(short stepsize) {
		this.stepsize = stepsize;
	}
	public short getCurrPeriod() {
		return currPeriod;
	}
	public void setCurrPeriod(short currPeriod) {
		this.currPeriod = currPeriod;
	}
	
	public int getStartYear() {
		return ScenarioConfigutration.getInstance().getStartYear();
	}
	
	public int getTimePeriod() {
		return this.window;
	}
}
