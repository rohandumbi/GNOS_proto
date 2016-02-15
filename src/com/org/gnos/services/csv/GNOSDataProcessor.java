package com.org.gnos.services.csv;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import com.org.gnos.db.DBHelper;

public class GNOSDataProcessor extends SwingWorker<Void, Void>{
	
	private CSVReader reader = null;
	private String fileName = null;
	
	private List<ColumnHeader> headers = null;
	

	public GNOSDataProcessor(String fileName) {
		super();
		this.fileName = fileName;
	}

	public List<ColumnHeader> getHeaderColumns() {
				
		return headers;
	}

	private void processHeaderRow(String[] headerRow){
		headers = new ArrayList<ColumnHeader>();
		for(int i=0; i < headerRow.length; i++ ){			
			ColumnHeader header = new ColumnHeader(headerRow[i]);
			headers.add(header);
		}
		
		dropTable();
		createTable();
	}
	
	private void dropTable() {
		
		String  sql = "DROP TABLE IF EXISTS gnos_data;";
		DBHelper.updateDB(sql);
	}

	private void createTable() {
		
		String  sql = "CREATE TABLE gnos_data ( ";
		
		for(int i =0; i< headers.size(); i++){
			String columnName = headers.get(i).getName().replaceAll("\\s+","_").toLowerCase();
			sql += columnName +" VARCHAR(40)";
			if(i<headers.size()-1) sql += ", ";
		}
		sql += ");";
		DBHelper.updateDB(sql);

	}
	
	private void processRecord(String[] row){
		String  sql = "insert into gnos_data values (";
		
		for(int i =0; i< row.length; i++){
			if(row[i] != null){
				sql += "'"+row[i]+ "'";
			}			
			if(i<row.length-1) sql += ", ";
		}
		sql += ");";
		
		DBHelper.updateDB(sql);
		
	}
	
	@Override
	public Void doInBackground() throws Exception {
		reader = new CSVReader(this.fileName);
		String[] row = reader.readLine();;
		while (row != null) {
			if(reader.getCurrentRowCount() == 1) {
				processHeaderRow(row);
				break;
			} else {
				processRecord(row);
			}
			row = reader.readLine();
			
		}
		
		return null;
	}
}
