package com.amzl.Parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.amzl.util.HandleTimeParser;
import com.amzl.util.ObtainInput;
import com.amzl.util.ObtainOutput;

public class Time_Extractor_source {

	private FileInputStream fis;
	private XSSFWorkbook wb;
	private XSSFSheet sheet;
	
	public Time_Extractor_source() {
	}
	
	/**
	 * ��ָ���ļ����µ��ļ����������з���
	 * @param folderName �ļ�����
	 */
	public void extractColumeInFolder(String folderName) {
		String[] fileList = ObtainInput.obtainDir(folderName);
		for(String fileName: fileList) {
			//result��ͷ���ļ������з���
			if(!fileName.startsWith("result")) {
System.out.println("��ʼ��"+fileName+"���з���");
				initInput(folderName+"\\"+fileName);
				timeParse(folderName+"\\result_"+fileName);
			}
		}
	}
	
	/**
	 * ���ָ��Excel��������
	 * @param fileName ���ļ�������ʼ
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
	
	@SuppressWarnings("deprecation")
	public void timeParse(String outPutFileName) {
		
		XSSFRow row;
		
		HandleTimeParser handleTimeParser = new HandleTimeParser();
		
		//���ڴ����
		List<String> numList = new ArrayList<String>();
		numList.add(("���"));
		//���ڴ�ԭʼʱ��
		List<String> originList = new ArrayList<String>();
		originList.add("�״β�ѯʱ��");
		//���ڴ��һ������ʱ��
		List<String> firstList = new ArrayList<String>();
		firstList.add("��һ������ʱ��");
		//���ڴ�ڶ�������ʱ��
		List<String> secondList = new ArrayList<String>();
		secondList.add("�ڶ�������ʱ��");
		//���ڴ��һ����������ʱ��
		List<String> firstDurationList = new ArrayList<String>();
		firstDurationList.add("��һ���������ʱ��");
		//���ڴ�ڶ�����������ʱ��
		List<String> secondDurationList = new ArrayList<String>();
		secondDurationList.add("�ڶ����������ʱ��");
		
		SimpleDateFormat time=new SimpleDateFormat("yyyy MM dd HH mm ss"); 
		
		//һ����������ʱ��
		String firstDuration = "";
		//������������ʱ��
		String secondDuration = "";
		
		//�״β�ѯʱ��
		Calendar originTime = null;
		//һ������ʱ��
		Calendar firstTime = null;
		//��������ʱ��
		Calendar secondTime = null;
		
		for(int index=1; index<=sheet.getLastRowNum(); index++) {
			row = sheet.getRow(index);
			//�õ����к�
			numList.add(String.valueOf(row.getRowNum()));
			originTime = Calendar.getInstance();
			//�õ��״β�ѯʱ��
			try {
				originTime.setTime(row.getCell(3).getDateCellValue());
				originList.add(time.format(originTime.getTime()));
			} catch (Exception e) {
				//�����쳣˵��δ�õ�����
				System.out.println("origin can't parser,line is: "+index);
				originList.add("��");
			}
			
			//�����һ������ʱ�䲻Ϊ�գ�����е�һ������ʱ�����
			if(!(row.getCell(4) == null) && !(row.getCell(4).getCellType() == XSSFCell.CELL_TYPE_BLANK)) {
				firstTime = Calendar.getInstance();
				//���cell��Ϊ���ڸ�ʽ��ֱ�ӽ��д���
				try {
					firstTime.setTime(row.getCell(4).getDateCellValue());
					firstList.add(time.format(firstTime.getTime()));
					firstDuration = String.format("%.2f", handleTimeParser.calculateTime(originTime, firstTime));
					firstDurationList.add(firstDuration);
				} catch(Exception e) {
					//�����쳣˵��δ�õ�����
					System.out.println("first exception,line is: "+index);
					firstList.add("");
					firstDurationList.add("");
				}
				
				//�ڵ�һ��������Ϊ�յĻ����ϣ�����ڶ�������ʱ�䲻Ϊ�գ�����еڶ�������ʱ�����
				if(!(row.getCell(5) == null) && !(row.getCell(5).getCellType() == XSSFCell.CELL_TYPE_BLANK)) {
					secondTime = Calendar.getInstance();
					//���cell��Ϊ���ڸ�ʽ��ֱ�ӽ��д���
					try {
						secondTime.setTime(row.getCell(5).getDateCellValue());
						secondList.add(time.format(secondTime.getTime()));
						secondDuration = String.format("%.2f", handleTimeParser.calculateTime(firstTime, secondTime));
						secondDurationList.add(secondDuration);
					} catch(Exception e) {
						//�����쳣˵��δ�õ�����
						System.out.println("second exception,line is: "+index);
						secondList.add("");
						secondDurationList.add("");
					}
				} else {
					secondList.add("");
					secondDurationList.add("");
					//�����쳣˵��δ�õ�����
					System.out.println("second can't parser,line is: "+index);
				}
				
			} else {
				firstList.add("");
				secondList.add("");
				firstDurationList.add("");
				secondDurationList.add("");
				//�����쳣˵��δ�õ�����
				System.out.println("first can't parser,line is: "+index);
			}
		}
		
		outputExcel(numList, originList, firstList, secondList, firstDurationList, secondDurationList, outPutFileName);
	}
	
	public void outputExcel(List<String> numList, List<String> originList, List<String> firstList, List<String> secondList, List<String> firstDurationList, List<String> secondDurationList, String outPutFileName) {
		
		Workbook outputWb = new XSSFWorkbook();
		Sheet outputSheet = outputWb.createSheet("new sheet");
		CellStyle style = outputWb.createCellStyle();
		style.setFillForegroundColor(IndexedColors.AQUA.getIndex());
		
		outputSheet.setColumnWidth(0, 100*20);
		outputSheet.setColumnWidth(1, 100*60);
		outputSheet.setColumnWidth(2, 100*60);
		outputSheet.setColumnWidth(3, 100*60);
		outputSheet.setColumnWidth(4, 100*60);
		outputSheet.setColumnWidth(4, 100*60);
		Row outputRow = null;
		Cell outputCell = null;
		for(int index=0; index<originList.size(); index++) {
			outputRow = outputSheet.createRow((short)index);
			//��1��
			outputCell = outputRow.createCell((short)0);
			outputCell.setCellValue(numList.get(index));
			//��2��
			outputCell = outputRow.createCell((short)1);
			outputCell.setCellValue(originList.get(index));
			//��3��
			outputCell = outputRow.createCell((short)2);
			outputCell.setCellValue(firstList.get(index));
			//��4��
			outputCell = outputRow.createCell((short)3);
			outputCell.setCellValue(secondList.get(index));
			//��5��
			outputCell = outputRow.createCell((short)4);
			outputCell.setCellValue(firstDurationList.get(index));
			//��6��
			outputCell = outputRow.createCell((short)5);
			outputCell.setCellValue(secondDurationList.get(index));
		}
		try {
			outputWb.write(ObtainOutput.obtainOutput(outPutFileName));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				outputWb.close();
				wb.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[]args) {
		new Time_Extractor_source().extractColumeInFolder("Time_Extractor");
	}
}
