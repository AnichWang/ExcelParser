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
	 * ֱ�ӳ�ʼ����ȡExcel���Ĺ��������˹��췽�����ڵ��ļ��Ĳ���
	 * @param Excel�ļ���
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
	 * ���ָ��Excel���������������вεĹ��췽��
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
			//ǿ����������
			fis = null;
		}
	}
	
	public CK_Extractor_source() {
	}
	
	/**
	 * Obtain the excel first row data
	 * @param fileName �ļ����������ļ�����
	 * @return ��������
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
		//��һ��Ϊ����
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
	
	/** ͨ��������ʽ����
	 * @param regex ������ʽ
	 */
	public void extractColume(String regex) {
		this.listString = new ArrayList<String>();
		//��һ��Ϊ����
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
	
	/** ͨ��������ʽ����, ָ�����λ��
	 * @param regex ������ʽ
	 * @param outPutFileName ����ļ����� CK_Extractor\\��outPutFileName��
	 */
	public void extractColume(String regex, String outPutFileName) {
		this.listString = new ArrayList<String>();
		//��һ��Ϊ����
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
		//ǿ����������
		this.listString = null;
	}
	
	/**
	 * Ĭ�����λ�ã�tmep/CK_Extracter_result.xlsx
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
	 * ָ�����λ��
	 * @param listString
	 * @param ���λ�ã�CK_Extractor\\��outPutFileName��
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
System.out.println("��"+fileList.length+"���ļ�");
		for(String fileName: fileList) {
			//result��ͷ���ļ����ܽ��з���
			if(!fileName.startsWith("result")) {
				System.out.println("�ļ�--"+fileName+" ������");
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
