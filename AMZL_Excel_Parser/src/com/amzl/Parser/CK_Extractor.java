package com.amzl.Parser;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
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

public class CK_Extractor implements Runnable{
	
	Document docs = null;
	//��ʾ��������
	SimpleAttributeSet attrset = null;
	//��ʾ��������
	SimpleAttributeSet erroMessage = null;
	
	private FileInputStream fis;
	private XSSFWorkbook wb;
	private XSSFSheet sheet;
	
	public CK_Extractor(JTextPane textPane) {
	
		//��ʼ��TextPane����
		docs = textPane.getDocument();
		attrset = new SimpleAttributeSet();
		erroMessage = new SimpleAttributeSet();
		//��ͨ����Ϊ12������
        StyleConstants.setFontSize(attrset,12);
        //��������Ϊ12�ź�ɫ����
        StyleConstants.setFontSize(erroMessage,12);
        StyleConstants.setForeground(erroMessage, Color.RED);
	}
	
	public CK_Extractor() {
	}
	
	@Override
	public void run() {
		extractColumeInFolder("CK_Extractor");
	}
	
	/**
	 * ��ָ���ļ����µ��ļ����������з���
	 * @param folderName �ļ�����
	 */
	public void extractColumeInFolder(String folderName) {
		String[] fileList = ObtainInput.obtainDir(folderName);
		int count = 0;
		for(String fileName: fileList) {
			//result��ͷ���ļ������з���
			if(!fileName.startsWith("result")) {
				count ++;
				initInput(folderName+"\\"+fileName);
				outPutMessage("�ļ�--"+fileName+" ������", attrset);
				extractColume("(CK)\\d+", folderName+"\\result_"+fileName);	
			}
		}
		outPutMessage("������ "+ count +" ���ļ�", erroMessage);
	}
	
	/**
	 * ���ָ��Excel��������
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
	 * ͨ��������ʽ����, ָ�����λ��
	 * @param regex ������ʽ
	 * @param outPutFileName ����ļ����� CK_Extractor\\��outPutFileName��
	 */
	public void extractColume(String regex, String outPutFileName) {
		ArrayList<String> listString = new ArrayList<String>();
		//��һ��Ϊ����
		listString.add("result");
		for(int i=1; i <= sheet.getLastRowNum(); i++) {
			Row tempRow = sheet.getRow(i);
			Cell tempCell = tempRow.getCell(1);
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(tempCell.getStringCellValue().toString());
			if(matcher.find()) {
				listString.add(matcher.group());
				outPutMessage(matcher.group(), attrset);
			} else {
				int erroLine = i;
				listString.add("parse failed");
				//���ʧ����Ϣ
				outPutMessage("parse failed, line is" + erroLine, erroMessage);
			}
		}
		outPutMessage("д��������.................." , attrset);
		outputExcel(listString, outPutFileName);
		outPutMessage("���,����ȡ "+listString.size()+" ������" , erroMessage);
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
			outPutMessage("��ȡʧ��", erroMessage);
		} finally {
			try {
				//�ر�Excel�Ķ�ȡ�������
				wb.close();
				outputWb.close();
			} catch (IOException e) {
				outPutMessage("��ȡʧ��", erroMessage);
			}
		}
	}
	
	/**
	 * ������д������ϣ�ָ�������ʽ
	 * @param message �������
	 * @param simpleattributeSet ָ����ʽ
	 */
	public void outPutMessage(String message, SimpleAttributeSet simpleAttributeSet) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					docs.insertString(docs.getLength(), message+"\n", simpleAttributeSet);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
}
