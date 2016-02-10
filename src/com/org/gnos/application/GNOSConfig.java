package com.org.gnos.application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;


public class GNOSConfig {

	static Properties prop;

	public static void load() {		
		try {
			prop = new Properties();
			prop.load(new FileInputStream("resources/gnos.properties"));
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
