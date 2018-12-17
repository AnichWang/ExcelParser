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
	 * �õ�ָ��·���ļ����µ��ļ��б�
	 * @param �ļ�����
	 * @return
	 */
	public static String[] obtainDir(String folderName) {
		
		File dir = new File("C:\\Users\\"+currentUser+"\\temp\\"+folderName);
		//�ж��ļ��д��ڲ���ΪĿ¼
		if(dir.exists() && dir.isDirectory()) {
			return dir.list();
		} else {
			return null;
		}
	}
	
	/**
	 * �����ļ���
	 * @param �ļ�����
	 */
	public static void createDir(String folderName) {
		
		File file = new File("C:\\Users\\"+currentUser+"\\temp");
		if(!file.exists() ||  !file.isDirectory()) {
			file.mkdir();
		}
		
		File dir = new File("C:\\Users\\"+currentUser+"\\temp\\"+folderName);
		//�ж��ļ��д��ڲ���ΪĿ¼
		if(!dir.exists() || !dir.isDirectory()) {
			dir.mkdir();
		}
		
	}
}
