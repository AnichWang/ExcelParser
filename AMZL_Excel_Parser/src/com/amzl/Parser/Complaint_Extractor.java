package com.amzl.Parser;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.amzl.util.ObtainInput;
import com.amzl.util.ObtainOutput;

public class Complaint_Extractor implements Runnable{

	Document docs = null;
	//��ʾ��������
	SimpleAttributeSet attrset = null;
	//��ʾ��������
	SimpleAttributeSet erroMessage = null;
	
	private FileInputStream fis;
	private XSSFWorkbook wb;
	private XSSFSheet sheet;
	
	public Complaint_Extractor(JTextPane textPane) {
		//��ʼ��TextPane����
		docs = textPane.getDocument();
		attrset = new SimpleAttributeSet();
		erroMessage = new SimpleAttributeSet();
        StyleConstants.setFontSize(attrset,12);
        //��ͨ����Ϊ12������
        StyleConstants.setFontSize(attrset,12);
        //��������Ϊ12�ź�ɫ����
        StyleConstants.setFontSize(erroMessage,12);
        StyleConstants.setForeground(erroMessage, Color.RED);
      
	}
	
	/**
	 * ��ָ���ļ����µ��ļ����������з���
	 * @param folderName �ļ�����
	 */
	public void extractColumeInFolder(String folderName) {
		String[] fileList = ObtainInput.obtainDir(folderName);
		int count = 0;
		for(String fileName: fileList) {
			//result��ͷ���ļ����ܽ��з���
			if(!fileName.startsWith("result")) {
				count ++;
				initInput(folderName+"\\"+fileName);
				extractColume("(������).*\\s*\\d+", "(��������|��������).*\\s*\\d+", folderName+"\\result_"+fileName);
			}
		}
		outPutMessage("������ "+ count +" ���ļ�", erroMessage);
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
		
		outPutMessage("��ʼ����������ȡ", attrset);
		
		//��Excel�����б���
		for(int i=1; i <= sheet.getLastRowNum(); i++) {
			int line = i;
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
				outPutMessage("line  " + line +"---��������ȡ���", attrset);
				listStr_orderId.add(extractNumber(tempString));
			} else {
				listStr_orderId.add("");
			}
			
			//��һ��ƥ��������������
			if(matcher_trackingId.find()) {
				tempString = matcher_trackingId.group();
				outPutMessage("line " + line +"---����������ȡ���", attrset);
				listStr_trackingId.add(extractNumber(tempString));
			} else {
				listStr_trackingId.add("");
			}
		}
		outputExcel(originData, listStr_orderId, listStr_trackingId, outPutFileName);
		outPutMessage("��ȡ���", attrset);
	}
	
	/**
	 *������ݵ�Excel 
	 * @param originList ԭʼ����
	 * @param orderList ��ȡ�Ķ�����
	 * @param trackingList ��ȡ����������
	 */
	public void outputExcel(List<String> originList, List<String> orderList, List<String> trackingList, String outPutFileName) {	
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
	
	/**
	 * ��һ����ȡ���������ٴ���ȡ,��ȡ����
	 * @param 
	 * @return
	 */
	public String extractNumber(String str) {
		String tempStr = "";
		//String regex = "[\\d{2,}[\\s(\\)(;)(/)]*]";
		String regex = "[\\d\\s(\\)(;)(/)(\\-)]*";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		while(matcher.find()) {
			tempStr += matcher.group();
		}
		return tempStr;
	}
	
	/**
	 * ��ҳ�������Ϣ
	 * @param message
	 * @param simpleAttributeSet ��������
	 */
	public void outPutMessage(String message, SimpleAttributeSet simpleAttributeSet) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					docs.insertString(docs.getLength(), message+"\n" , simpleAttributeSet);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	@Override
	public void run() {
		extractColumeInFolder("Complaint_Extractor");
	}

}
