package com.org.gnos.services.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectImportHelper {
	
	private BufferedReader br = null;
	
	public void importProject(String fileName) {
		Map<Integer, List<String[]>> projectData = new HashMap<Integer, List<String[]>>();
		try {
			br = new BufferedReader(new FileReader(fileName));
			String line = null;
			
			while ((line = br.readLine()) != null) {
				String[] linedataArr = line.split("\\|");
				if(linedataArr.length > 1) {
					Integer ind = Integer.parseInt(linedataArr[0]);
					List<String[]> data = projectData.get(ind);
					if(data == null) {
						data = new ArrayList<String[]>();
						projectData.put(ind, data);
					}
					data.add(linedataArr);
				}
			}		
			process(projectData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void process(Map<Integer, List<String[]>> projectData) {
		
	}
	
}
