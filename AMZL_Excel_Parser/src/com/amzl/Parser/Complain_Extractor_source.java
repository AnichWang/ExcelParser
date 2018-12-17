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
System.out.println("共"+fileList.length+"个文件");
		for(String fileName: fileList) {
			//result开头的文件不能进行分析
			if(!fileName.startsWith("result")) {
				System.out.println("文件--"+fileName+" 处理中");
				initInput(folderName+"\\"+fileName);
				extractColume("(订单号).*\\s*\\d+", "(物流单号|包裹单号).*\\s*\\d+", folderName+"\\result_"+fileName);	
			}
		}
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
	
	public void extractColume() {
		
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
		
		//提取出的临时数据放入Map中
		HashMap<String, String> listMap = null;
		
		Row tempRow = null;
		Cell tempCell = null;
		String[] tempStr = null;
		String[] mapStr = null;
		
		//提取Cell中数据，遍历Cell，每次处理一个Cell，第一行为Title，index从1开始
		for(int i=1; i <= sheet.getLastRowNum(); i++) {
			listMap = new HashMap<String, String>();
			tempRow = sheet.getRow(i);
			tempCell = tempRow.getCell(1);
			//存入原始数据
			originData.add(tempCell.getStringCellValue());
			//开始分析数据，根据换行对数据进行分组
			tempStr = tempCell.getStringCellValue().split("\r\n");
			//遍历根据换行的分组
			for(String t: tempStr) {
				if(!t.trim().equals(null)) {
					//根据：进行分组
					mapStr = t.split(":");
					if(mapStr.length > 1) {
						listMap.put(mapStr[0], mapStr[1]);
					} else {
						listMap.put(mapStr[0], null);
					}
				}
			}
			
			if(listMap.containsKey("订单号")) {
				listStr_orderId.add(listMap.get("订单号"));
			} else {
				//对分组未成功的再次分析
				for(String key : listMap.keySet()) {
					if(key.startsWith("订单号")) {
						
					}
				}
				listStr_orderId.add("空");
			}
			if(listMap.containsKey("物流单号") || listMap.containsKey("包裹单号")) {
				listStr_trackingId.add(listMap.get("物流单号"));
			} else {
				listStr_trackingId.add("空");
			}
			
			//未正常取出数据
			if(!listMap.containsKey("订单号") || listMap.containsKey("物流单号")) {
				
			}
		}
		System.out.println("origin data" + originData.size());
		System.out.println("order" + listStr_orderId.size());
		System.out.println("tracking" + listStr_trackingId.size());
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
		//对Excel表格进行遍历
		for(int i=1; i <= sheet.getLastRowNum(); i++) {
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
				System.out.println("line is " + i +"---第一次匹配结果："+ tempString);
				//提取数字
				System.out.println("line is " + i +"---提取数字："+extractNumber(tempString));
				listStr_orderId.add(extractNumber(tempString));
			} else {
				System.out.println("line is " +i + "---is null");
				listStr_orderId.add("");
			}
			
			//第一次匹配物流单号数据
			if(matcher_trackingId.find()) {
				tempString = matcher_trackingId.group();
				System.out.println("line is " + i +"---第一次匹配结果："+ tempString);
				//提取数字
				System.out.println("line is " + i +"---提取数字："+extractNumber(tempString));
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
			if(str.startsWith("包裹单号") || str.startsWith("物流单号")) {
				if(str.split(";").length > 1) return str.split(";")[1];
			}
		}
		return null;
	}
	
	/**
	 * 指定输出位置
	 * @param originList 原始数据
	 * @param orderList	提取的订单号List
	 * @param trackingList 提取的物流单号List
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
	
	public void outputExcel(List<HashMap<String, String>> tempList) {
		
		Workbook outputWb = new XSSFWorkbook();
		Sheet outputSheet = outputWb.createSheet("new sheet");
		Row outputRow = null;
		Cell outputCell = null;
		for(int index=0; index<tempList.size(); index++) {
			outputRow = outputSheet.createRow((short)index);
			HashMap<String, String> tempMap = tempList.get(index);
			outputCell = outputRow.createCell((short)1);
			outputCell.setCellValue(tempMap.get("订单号"));
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
	 * 基于正则表达式提取数据
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
	 * 基于正则表达式移除汉字
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
