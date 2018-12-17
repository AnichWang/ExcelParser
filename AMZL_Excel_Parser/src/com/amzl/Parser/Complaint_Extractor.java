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
	//显示正常数据
	SimpleAttributeSet attrset = null;
	//显示错误数据
	SimpleAttributeSet erroMessage = null;
	
	private FileInputStream fis;
	private XSSFWorkbook wb;
	private XSSFSheet sheet;
	
	public Complaint_Extractor(JTextPane textPane) {
		//初始化TextPane字体
		docs = textPane.getDocument();
		attrset = new SimpleAttributeSet();
		erroMessage = new SimpleAttributeSet();
        StyleConstants.setFontSize(attrset,12);
        //普通数据为12号字体
        StyleConstants.setFontSize(attrset,12);
        //错误数据为12号红色字体
        StyleConstants.setFontSize(erroMessage,12);
        StyleConstants.setForeground(erroMessage, Color.RED);
      
	}
	
	/**
	 * 对指定文件夹下的文件遍历并进行分析
	 * @param folderName 文件夹名
	 */
	public void extractColumeInFolder(String folderName) {
		String[] fileList = ObtainInput.obtainDir(folderName);
		int count = 0;
		for(String fileName: fileList) {
			//result开头的文件不能进行分析
			if(!fileName.startsWith("result")) {
				count ++;
				initInput(folderName+"\\"+fileName);
				extractColume("(订单号).*\\s*\\d+", "(物流单号|包裹单号).*\\s*\\d+", folderName+"\\result_"+fileName);
			}
		}
		outPutMessage("共处理 "+ count +" 个文件", erroMessage);
	}
	
	/**
	 * 获得指定Excel的输入流，代替有参的构造方法
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
			//强制垃圾回收
			fis = null;
		}
	}
	
	/**
	 * 基于正则表达式的提取
	 * @param regex_orderId 订单号表达式
	 * @param regex_trackingId 物流单号表达式
	 */
	public void extractColume(String regex_orderId, String regex_trackingId, String outPutFileName) {
		
		//存入提取出的orderId
		ArrayList<String> listStr_orderId = new ArrayList<String>();
		//存入提取出的trackingId
		ArrayList<String> listStr_trackingId = new ArrayList<String>();
		//读入的原始数据
		ArrayList<String> originData = new ArrayList<String>();
		
		//列名初始化
		listStr_orderId.add("订单号");
		listStr_trackingId.add("物流单号");
		originData.add("原数据");
		
		Row tempRow = null;
		Cell tempCell = null;
		String content = null;
		String tempString = null;
		//初始化订单号表达式
		Pattern pattern_orderId = Pattern.compile(regex_orderId);
		Matcher matcher_orderId = null;
		//初始化物流单号表达式
		Pattern pattern_trackingId = Pattern.compile(regex_trackingId);
		Matcher matcher_trackingId = null;
		
		outPutMessage("开始进行数据提取", attrset);
		
		//对Excel表格进行遍历
		for(int i=1; i <= sheet.getLastRowNum(); i++) {
			int line = i;
			tempRow = sheet.getRow(i);
			tempCell = tempRow.getCell(1);
			//获得原数据
			content = tempCell.getStringCellValue();
			matcher_orderId = pattern_orderId.matcher(content);
			matcher_trackingId = pattern_trackingId.matcher(content);
			originData.add(content);
			//第一次匹配订单号数据
			if(matcher_orderId.find()) {
				tempString = matcher_orderId.group();
				outPutMessage("line  " + line +"---订单号提取完成", attrset);
				listStr_orderId.add(extractNumber(tempString));
			} else {
				listStr_orderId.add("");
			}
			
			//第一次匹配物流单号数据
			if(matcher_trackingId.find()) {
				tempString = matcher_trackingId.group();
				outPutMessage("line " + line +"---物流单号提取完成", attrset);
				listStr_trackingId.add(extractNumber(tempString));
			} else {
				listStr_trackingId.add("");
			}
		}
		outputExcel(originData, listStr_orderId, listStr_trackingId, outPutFileName);
		outPutMessage("提取完成", attrset);
	}
	
	/**
	 *输出数据到Excel 
	 * @param originList 原始数据
	 * @param orderList 提取的订单号
	 * @param trackingList 提取的物流单号
	 */
	public void outputExcel(List<String> originList, List<String> orderList, List<String> trackingList, String outPutFileName) {	
		Workbook outputWb = new XSSFWorkbook();
		Sheet outputSheet = outputWb.createSheet("new sheet");
		outputSheet.setColumnWidth(0, 100*200);
		Row outputRow = null;
		Cell outputCell = null;
		for(int index=0; index<originList.size(); index++) {
			outputRow = outputSheet.createRow((short)index);
			//第一列
			outputCell = outputRow.createCell((short)0);
			outputCell.setCellValue(originList.get(index));
			//第二列
			outputCell = outputRow.createCell((short)1);
			if(orderList.get(index) == null || orderList.get(index).trim().equals(null)) {
				outputCell.setCellValue("空");
			} else {
				outputCell.setCellValue(orderList.get(index));
			}
			//第三列
			outputCell = outputRow.createCell((short)2);
			if(trackingList.get(index) == null || trackingList.get(index).equals(null)) {
				outputCell.setCellValue("空");
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
	 * 第一次提取出的数据再次提取,提取数字
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
	 * 向页面输出信息
	 * @param message
	 * @param simpleAttributeSet 控制字体
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
