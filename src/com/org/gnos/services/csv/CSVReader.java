package com.org.gnos.services.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CSVReader {
	
	private String filename = null;
	private boolean isProcessed = false;
	private BufferedReader br = null;
	private int currentRow = 0;

	
	public CSVReader(String csvFileName) throws Exception {
		this.filename = csvFileName;
		br = new BufferedReader(new FileReader(this.filename));
	}
	
	public String[] readLine() {
		
		if(this.isProcessed) return null;
		
		String rec[] =  null;
		String line = null;
		
		try {
			line = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		currentRow ++;
		if( line != null ){
			rec = line.split(",");
		} else {
			isProcessed = true;
		}			
		return rec;
	}
	
	public int getCurrentRowCount(){
		return currentRow;
	}
	

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if(br != null){
			br.close();
		}
	}
}
