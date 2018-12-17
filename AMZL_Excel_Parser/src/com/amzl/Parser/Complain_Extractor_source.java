package com.amzl.Parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.amzl.util.ObtainInput;
import com.amzl.util.ObtainOutput;

public class Complain_Extractor_source {
	
	private FileInputStream fis;
	private XSSFWorkbook wb;
	private XSSFSheet sheet;
	private XSSFRow row;
	
	Document docs = null;
	SimpleAttributeSet attrset = null;
	
	public Complain_Extractor_source() {
	}
	
	public void extractColumeInFolder(String folderName) {
		String[] fileList = ObtainInput.obtainDir(folderName);
System.out.println("��"+fileList.length+"���ļ�");
		for(String fileName: fileList) {
			//result��ͷ���ļ����ܽ��з���
			if(!fileName.startsWith("result")) {
				System.out.println("�ļ�--"+fileName+" ������");
				initInput(folderName+"\\"+fileName);
				extractColume("(������).*\\s*\\d+", "(��������|��������).*\\s*\\d+", folderName+"\\result_"+fileName);	
			}
		}
	}
	
	/**
	 * ���ָ��Excel���������������вεĹ��췽��
	 * @param fileName
	 */
	public void initInput(String fileName) {
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
	
	public void extractColume() {
		
		//������ȡ����orderId
		ArrayList<String> listStr_orderId = new ArrayList<String>();
		//������ȡ����trackingId
		ArrayList<String> listStr_trackingId = new ArrayList<String>();
		//�����ԭʼ����
		ArrayList<String> originData = new ArrayList<String>();
		
		//������ʼ��
		listStr_orderId.add("������");
		listStr_trackingId.add("��������");
		originData.add("ԭ����");
		
		//��ȡ������ʱ���ݷ���Map��
		HashMap<String, String> listMap = null;
		
		Row tempRow = null;
		Cell tempCell = null;
		String[] tempStr = null;
		String[] mapStr = null;
		
		//��ȡCell�����ݣ�����Cell��ÿ�δ���һ��Cell����һ��ΪTitle��index��1��ʼ
		for(int i=1; i <= sheet.getLastRowNum(); i++) {
			listMap = new HashMap<String, String>();
			tempRow = sheet.getRow(i);
			tempCell = tempRow.getCell(1);
			//����ԭʼ����
			originData.add(tempCell.getStringCellValue());
			//��ʼ�������ݣ����ݻ��ж����ݽ��з���
			tempStr = tempCell.getStringCellValue().split("\r\n");
			//�������ݻ��еķ���
			for(String t: tempStr) {
				if(!t.trim().equals(null)) {
					//���ݣ����з���
					mapStr = t.split(":");
					if(mapStr.length > 1) {
						listMap.put(mapStr[0], mapStr[1]);
					} else {
						listMap.put(mapStr[0], null);
					}
				}
			}
			
			if(listMap.containsKey("������")) {
				listStr_orderId.add(listMap.get("������"));
			} else {
				//�Է���δ�ɹ����ٴη���
				for(String key : listMap.keySet()) {
					if(key.startsWith("������")) {
						
					}
				}
				listStr_orderId.add("��");
			}
			if(listMap.containsKey("��������") || listMap.containsKey("��������")) {
				listStr_trackingId.add(listMap.get("��������"));
			} else {
				listStr_trackingId.add("��");
			}
			
			//δ����ȡ������
			if(!listMap.containsKey("������") || listMap.containsKey("��������")) {
				
			}
		}
		System.out.println("origin data" + originData.size());
		System.out.println("order" + listStr_orderId.size());
		System.out.println("tracking" + listStr_trackingId.size());
	}
	
	/**
	 * ����������ʽ����ȡ
	 * @param regex_orderId �����ű��ʽ
	 * @param regex_trackingId �������ű��ʽ
	 */
	public void extractColume(String regex_orderId, String regex_trackingId, String outPutFileName) {
		
		//������ȡ����orderId
		ArrayList<String> listStr_orderId = new ArrayList<String>();
		//������ȡ����trackingId
		ArrayList<String> listStr_trackingId = new ArrayList<String>();
		//�����ԭʼ����
		ArrayList<String> originData = new ArrayList<String>();
		
		//������ʼ��
		listStr_orderId.add("������");
		listStr_trackingId.add("��������");
		originData.add("ԭ����");
		
		Row tempRow = null;
		Cell tempCell = null;
		String content = null;
		String tempString = null;
		//��ʼ�������ű��ʽ
		Pattern pattern_orderId = Pattern.compile(regex_orderId);
		Matcher matcher_orderId = null;
		//��ʼ���������ű��ʽ
		Pattern pattern_trackingId = Pattern.compile(regex_trackingId);
		Matcher matcher_trackingId = null;
		//��Excel�����б���
		for(int i=1; i <= sheet.getLastRowNum(); i++) {
			tempRow = sheet.getRow(i);
			tempCell = tempRow.getCell(1);
			//���ԭ����
			content = tempCell.getStringCellValue();
			matcher_orderId = pattern_orderId.matcher(content);
			matcher_trackingId = pattern_trackingId.matcher(content);
			originData.add(content);
			//��һ��ƥ�䶩��������
			if(matcher_orderId.find()) {
				tempString = matcher_orderId.group();
				System.out.println("line is " + i +"---��һ��ƥ������"+ tempString);
				//��ȡ����
				System.out.println("line is " + i +"---��ȡ���֣�"+extractNumber(tempString));
				listStr_orderId.add(extractNumber(tempString));
			} else {
				System.out.println("line is " +i + "---is null");
				listStr_orderId.add("");
			}
			
			//��һ��ƥ��������������
			if(matcher_trackingId.find()) {
				tempString = matcher_trackingId.group();
				System.out.println("line is " + i +"---��һ��ƥ������"+ tempString);
				//��ȡ����
				System.out.println("line is " + i +"---��ȡ���֣�"+extractNumber(tempString));
				listStr_trackingId.add(extractNumber(tempString));
			} else {
				System.out.println("line is " +i + "---is null");
				listStr_trackingId.add("");
			}
			
		}
		System.out.println("origin data" + originData.size());
		System.out.println("order" + listStr_orderId.size());
		System.out.println("tracking" + listStr_trackingId.size());
		outputExcel_v1(originData, listStr_orderId, listStr_trackingId, outPutFileName);
	}
	
	
	public String extractTrackingID(String originData) {
		String[] tempStr = originData.split("\r\n");
		for(String str : tempStr) {
			if(str.startsWith("��������") || str.startsWith("��������")) {
				if(str.split(";").length > 1) return str.split(";")[1];
			}
		}
		return null;
	}
	
	/**
	 * ָ�����λ��
	 * @param originList ԭʼ����
	 * @param orderList	��ȡ�Ķ�����List
	 * @param trackingList ��ȡ����������List
	 * @param outPutFileName
	 */
	public void outputExcel_v1(List<String> originList, List<String> orderList, List<String> trackingList, String outPutFileName) {
		
		Workbook outputWb = new XSSFWorkbook();
		Sheet outputSheet = outputWb.createSheet("new sheet");
		outputSheet.setColumnWidth(0, 100*200);
		Row outputRow = null;
		Cell outputCell = null;
		for(int index=0; index<originList.size(); index++) {
			outputRow = outputSheet.createRow((short)index);
			//��һ��
			outputCell = outputRow.createCell((short)0);
			outputCell.setCellValue(originList.get(index));
			//�ڶ���
			outputCell = outputRow.createCell((short)1);
			if(orderList.get(index) == null || orderList.get(index).trim().equals(null)) {
				outputCell.setCellValue("��");
			} else {
				outputCell.setCellValue(orderList.get(index));
			}
			//������
			outputCell = outputRow.createCell((short)2);
			if(trackingList.get(index) == null || trackingList.get(index).equals(null)) {
				outputCell.setCellValue("��");
			} else {
				outputCell.setCellValue(trackingList.get(index));
			}
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
	
	public void outputExcel(List<HashMap<String, String>> tempList) {
		
		Workbook outputWb = new XSSFWorkbook();
		Sheet outputSheet = outputWb.createSheet("new sheet");
		Row outputRow = null;
		Cell outputCell = null;
		for(int index=0; index<tempList.size(); index++) {
			outputRow = outputSheet.createRow((short)index);
			HashMap<String, String> tempMap = tempList.get(index);
			outputCell = outputRow.createCell((short)1);
			outputCell.setCellValue(tempMap.get("������"));
		}
		try {
			outputWb.write(ObtainOutput.obtainOutput("result.xlsx"));
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
	 * ����������ʽ��ȡ����
	 * @param 
	 * @return
	 */
	public String extractNumber(String str) {
		String tempStr = "";
		String regex = "[\\d{2,}[\\s(\\)(;)(/)]*]";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		while(matcher.find()) {
			tempStr += matcher.group();
		}
		return tempStr;
	}
	
	/**
	 * ����������ʽ�Ƴ�����
	 * @param str
	 * @return
	 */
	public String removeChinese(String str) {
		String regex = "[\u4e00-\u9fa5]+";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		matcher.find();
		String results = matcher.replaceAll("");
		return results;
	}
	
	public static void main(String[] args) {
		//new ColumeExtracter("Extracter.xlsx").extractColume();
		new Complain_Extractor_source().extractColumeInFolder("Complaint_Extractor");
	}
}
