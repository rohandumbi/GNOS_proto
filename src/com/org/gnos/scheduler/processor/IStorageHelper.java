package com.org.gnos.scheduler.processor;

import java.util.List;

import com.org.gnos.scheduler.equation.ExecutionContext;

public interface IStorageHelper {

	public void start();
	
	public void store(List<Record> records, boolean hasMore);
	
	public void stop();
	
	public void setContext(ExecutionContext context);
}
