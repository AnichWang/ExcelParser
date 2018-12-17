package com.amzl.Parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.amzl.util.ObtainInput;
import com.amzl.util.ObtainOutput;

public class CK_Extractor_source {
	
	private FileInputStream fis;
	private XSSFWorkbook wb;
	private XSSFSheet sheet;
	private XSSFRow row;
	
	ArrayList<String> listString = null;
	
	private String[] fileList;
	
	/**
	 * 直接初始化读取Excel流的构造器，此构造方法用于单文件的操作
	 * @param Excel文件名
	 */
	public CK_Extractor_source(String fileName) {
		this.fis = ObtainInput.obtainInput(fileName);
		try {
			this.wb = new XSSFWorkbook(fis);
			this.sheet = wb.getSheetAt(0);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 获得指定Excel的输入流，代替有参的构造方法
	 * @param fileName
	 */
	public void obtainInput(String fileName) {
		this.fis = ObtainInput.obtainInput(fileName);
		try {
			this.wb = new XSSFWorkbook(fis);
			this.sheet = wb.getSheetAt(0);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//强制垃圾回收
			fis = null;
		}
	}
	
	public CK_Extractor_source() {
	}
	
	/**
	 * Obtain the excel first row data
	 * @param fileName 文件名，包含文件类型
	 * @return 列名数组
	 */
	public String[] readExcelColumeTitle() {
		
		row = sheet.getRow(0);
		
		int colNum = row.getPhysicalNumberOfCells();
		
		String[] columeTitle = new String[colNum];
		for(int i=0; i<colNum; i++) {
			columeTitle[i] = row.getCell(i).getStringCellValue();
		}
		return columeTitle;
	}	
	public void extractColume(String columeName, String startStr, int strLong) {
		ArrayList<String> listString = new ArrayList<String>();
		//第一行为列名
		listString.add("result");
		for(int i=1; i <= sheet.getLastRowNum(); i++) {
			Row tempRow = sheet.getRow(i);
			Cell tempCell = tempRow.getCell(1);
			String tempStr = (tempCell.getStringCellValue().split("\\+"))[1].trim();
			//RegExp
			if(tempStr.startsWith("CK")) {
				System.out.println(tempStr+"--------"+ i);
				listString.add(tempStr);
			} else {
				System.out.println("deep parse line " + i +"----");
				String regex = "(CK)\\d+";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(tempCell.getStringCellValue());
				System.out.println("deep parse line " + i +matcher.group());
				listString.add(matcher.group());
			}
		}
		outputExcel(listString);
	}
	
	/** 通过正则表达式分析
	 * @param regex 正则表达式
	 */
	public void extractColume(String regex) {
		this.listString = new ArrayList<String>();
		//第一行为列名
		listString.add("result");
		for(int i=1; i <= sheet.getLastRowNum(); i++) {
			Row tempRow = sheet.getRow(i);
			Cell tempCell = tempRow.getCell(1);
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(tempCell.getStringCellValue().toString());
			if(matcher.find()) {
				listString.add(matcher.group());
			} else {
				listString.add("parse failed");
			}
		}
		outputExcel(listString);
	}
	
	/** 通过正则表达式分析, 指定输出位置
	 * @param regex 正则表达式
	 * @param outPutFileName 输出文件名字 CK_Extractor\\‘outPutFileName’
	 */
	public void extractColume(String regex, String outPutFileName) {
		this.listString = new ArrayList<String>();
		//第一行为列名
		listString.add("result");
		for(int i=1; i <= sheet.getLastRowNum(); i++) {
			Row tempRow = sheet.getRow(i);
			Cell tempCell = tempRow.getCell(1);
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(tempCell.getStringCellValue().toString());
			if(matcher.find()) {
				listString.add(matcher.group());
			} else {
				listString.add("parse failed");
			}
		}
		outputExcel(listString, outPutFileName);
		//强制垃圾回收
		this.listString = null;
	}
	
	/**
	 * 默认输出位置，tmep/CK_Extracter_result.xlsx
	 * @param listString
	 */
	
	public void outputExcel(ArrayList<String> listString) {
		Workbook outputWb = new XSSFWorkbook();
		Sheet outputSheet = outputWb.createSheet("new sheet");
		Row row = null;
		Cell cell = null;
		for(int index=0; index<listString.size(); index++) {
			row = outputSheet.createRow(index);
			cell = row.createCell(0);
			cell.setCellValue(listString.get(index));
		}
		try {
			outputWb.write(ObtainOutput.obtainOutput("CK_Extracter_result.xlsx"));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				outputWb.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 指定输出位置
	 * @param listString
	 * @param 输出位置，CK_Extractor\\‘outPutFileName’
	 */
	public void outputExcel(ArrayList<String> listString, String outPutFileName) {
		Workbook outputWb = new XSSFWorkbook();
		Sheet outputSheet = outputWb.createSheet("new sheet");
		Row row = null;
		Cell cell = null;
		for(int index=0; index<listString.size(); index++) {
			row = outputSheet.createRow(index);
			cell = row.createCell(0);
			cell.setCellValue(listString.get(index));
		}
		try {
			outputWb.write(ObtainOutput.obtainOutput(outPutFileName));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				outputWb.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void extractColumeInFolder(String folderName) {
		fileList = ObtainInput.obtainDir(folderName);
System.out.println("共"+fileList.length+"个文件");
		for(String fileName: fileList) {
			//result开头的文件不能进行分析
			if(!fileName.startsWith("result")) {
				System.out.println("文件--"+fileName+" 处理中");
				obtainInput(folderName+"\\"+fileName);
				extractColume("(CK)\\d+", folderName+"\\result_"+fileName);	
			}
		}
	}
	
	public static void main(String[]args) {
		//CK_Extractor_source parser = new CK_Extractor_source("CK_Extracter.xlsx");
		//parser.extractColume("(CK)\\d+");
		
		CK_Extractor_source parser = new CK_Extractor_source();
		parser.extractColumeInFolder("CK_Extractor");
		
	}
}
