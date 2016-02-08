package com.org.gnos.services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVProcessor {
	
	private String filename = null;
	private boolean isProcessed = false;
	private BufferedReader br = null;
	private int currentLine = 1;
	private List<ColumnHeader> headers = null;
	
	public CSVProcessor(String csvFileName) throws Exception{
		this.filename = csvFileName;
		br = new BufferedReader(new FileReader(this.filename));
	}
	
	public void process() throws IOException {
		if(currentLine == 1){
			this.parseHeader();
		}
	}
	
	public List<ColumnHeader> getHeaderColumns() throws IOException {
		if(headers == null){
			this.parseHeader();			
		} 
		return headers;
	}
	
	private void parseHeader() throws IOException {
		String line = br.readLine();
		currentLine ++;
		String rec[] = line.split(",");
		headers = new ArrayList<ColumnHeader>();
		for(int i =0; i< rec.length; i++){
			ColumnHeader header = new ColumnHeader(rec[i]);
			headers.add(header);
		}
	}
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if(br != null){
			br.close();
		}
	}
}
