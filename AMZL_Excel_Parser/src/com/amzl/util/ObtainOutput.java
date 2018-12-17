package com.amzl.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ObtainOutput {

	private static String currentUser;
	
	static {
		currentUser = System.getProperty("user.name");
	}
	
	public static OutputStream obtainOutput(String fileName) {
		
		FileOutputStream out = null;
		
		File file = new File("C:\\Users\\"+currentUser+"\\temp");
		if(!file.exists()) {
			file.mkdir();
		}
		
		try{
			out = new FileOutputStream("C:\\Users\\"+currentUser+"\\temp\\"+fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return out;
	}
	
}
