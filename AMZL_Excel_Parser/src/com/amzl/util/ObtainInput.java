package com.amzl.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ObtainInput {
	
	static final String currentUser;
	
	static {
		currentUser = System.getProperty("user.name");
	}
	
	public static FileInputStream obtainInput(String fileName) {
		FileInputStream in = null;
		
		File file = new File("C:\\Users\\"+currentUser+"\\temp");
		if(!file.exists()) {
			file.mkdir();
		}
		
		try {
			in = new FileInputStream("C:\\Users\\"+currentUser+"\\temp\\"+fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return in;
	}
	
	/**
	 * 得到指定路径文件夹下的文件列表
	 * @param 文件夹名
	 * @return
	 */
	public static String[] obtainDir(String folderName) {
		
		File dir = new File("C:\\Users\\"+currentUser+"\\temp\\"+folderName);
		//判断文件夹存在并且为目录
		if(dir.exists() && dir.isDirectory()) {
			return dir.list();
		} else {
			return null;
		}
	}
	
	/**
	 * 创建文件夹
	 * @param 文件夹名
	 */
	public static void createDir(String folderName) {
		
		File file = new File("C:\\Users\\"+currentUser+"\\temp");
		if(!file.exists() ||  !file.isDirectory()) {
			file.mkdir();
		}
		
		File dir = new File("C:\\Users\\"+currentUser+"\\temp\\"+folderName);
		//判断文件夹存在并且为目录
		if(!dir.exists() || !dir.isDirectory()) {
			dir.mkdir();
		}
		
	}
}
