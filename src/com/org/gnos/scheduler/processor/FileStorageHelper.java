package com.org.gnos.scheduler.processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import com.org.gnos.scheduler.equation.ExecutionContext;

public class FileStorageHelper implements IStorageHelper {

	private String fileName = "merged1.csv";
	private PrintWriter writer;
	
	public FileStorageHelper() {
		File file = new File(fileName);
        try {
			writer = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void store(List<Record> records) {
		for(Record record: records){
			writer.write(record.toString());
			writer.flush();
		}
		writer.close();
	}
	@Override
	public void setContext(ExecutionContext context) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

}
