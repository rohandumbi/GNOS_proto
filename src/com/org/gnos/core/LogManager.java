package com.org.gnos.core;

import static java.nio.file.StandardOpenOption.*;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LogManager {

	private static OutputStream os1 = null;
	private static OutputStream os2 = null;
	
	public static void initialize() {
		String l1 = GNOSConfig.get("scheduler.log");
		String l2 = GNOSConfig.get("application.log");
		Path p1 = Paths.get(l1);
		Path p2 = Paths.get(l2);
		
		 try {
			 os1 = new BufferedOutputStream(Files.newOutputStream(p1, CREATE, APPEND));
			 os2 = new BufferedOutputStream(Files.newOutputStream(p2, CREATE, APPEND));
		 } catch (IOException ioe) {
			 System.err.println(ioe.getMessage());
		 }
	}
	
	public static void logSchedule(String msg) {
		if(os1 != null) {
			byte data[] = (msg+"\n").getBytes();
			try {
				os1.write(data, 0, data.length);
				os1.flush();
			} catch (IOException ioe) {
				System.err.println(ioe.getMessage());
			}
		}
	}
	
	public static void log(String msg) {
		if(os2 != null) {
			byte data[] = (msg+"\n").getBytes();
			try {
				os2.write(data, 0, data.length);
				os2.flush();
			} catch (IOException ioe) {
				System.err.println(ioe.getMessage());
			}
		}
	}
}
