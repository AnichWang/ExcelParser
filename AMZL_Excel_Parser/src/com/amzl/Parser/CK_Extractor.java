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
	//显示正常数据
	SimpleAttributeSet attrset = null;
	//显示错误数据
	SimpleAttributeSet erroMessage = null;
	
	private FileInputStream fis;
	private XSSFWorkbook wb;
	private XSSFSheet sheet;
	
	public CK_Extractor(JTextPane textPane) {
	
		//初始化TextPane字体
		docs = textPane.getDocument();
		attrset = new SimpleAttributeSet();
		erroMessage = new SimpleAttributeSet();
		//普通数据为12号字体
        StyleConstants.setFontSize(attrset,12);
        //错误数据为12号红色字体
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
	 * 对指定文件夹下的文件遍历并进行分析
	 * @param folderName 文件夹名
	 */
	public void extractColumeInFolder(String folderName) {
		String[] fileList = ObtainInput.obtainDir(folderName);
		int count = 0;
		for(String fileName: fileList) {
			//result开头的文件不进行分析
			if(!fileName.startsWith("result")) {
				count ++;
				initInput(folderName+"\\"+fileName);
				outPutMessage("文件--"+fileName+" 处理中", attrset);
				extractColume("(CK)\\d+", folderName+"\\result_"+fileName);	
			}
		}
		outPutMessage("共处理 "+ count +" 个文件", erroMessage);
	}
	
	/**
	 * 获得指定Excel的输入流
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
	 * 通过正则表达式分析, 指定输出位置
	 * @param regex 正则表达式
	 * @param outPutFileName 输出文件名字 CK_Extractor\\‘outPutFileName’
	 */
	public void extractColume(String regex, String outPutFileName) {
		ArrayList<String> listString = new ArrayList<String>();
		//第一列为列名
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
				//输出失败信息
				outPutMessage("parse failed, line is" + erroLine, erroMessage);
			}
		}
		outPutMessage("写入数据中.................." , attrset);
		outputExcel(listString, outPutFileName);
		outPutMessage("完成,共提取 "+listString.size()+" 行数据" , erroMessage);
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
			outPutMessage("提取失败", erroMessage);
		} finally {
			try {
				//关闭Excel的读取与输出流
				wb.close();
				outputWb.close();
			} catch (IOException e) {
				outPutMessage("提取失败", erroMessage);
			}
		}
	}
	
	/**
	 * 将数据写到面板上，指定字体格式
	 * @param message 输出数据
	 * @param simpleattributeSet 指定格式
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
