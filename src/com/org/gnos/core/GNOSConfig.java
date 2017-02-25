package com.org.gnos.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;


public class GNOSConfig {

	static Properties prop;

	public static void load() {		
		try {
			prop = new Properties();
			prop.load(GNOSConfig.class.getClassLoader().getResourceAsStream("gnos.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String get(String key){
		String value = prop.getProperty(key);
			
		return value;
	}
}
