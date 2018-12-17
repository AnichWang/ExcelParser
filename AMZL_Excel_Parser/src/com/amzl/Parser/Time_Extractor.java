package com.amzl.Parser;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.amzl.util.HandleTimeParser;
import com.amzl.util.ObtainInput;
import com.amzl.util.ObtainOutput;

public class Time_Extractor implements Runnable{
	
	Document docs = null;
	SimpleAttributeSet attrset = null;
	SimpleAttributeSet erroMessage = null;
	
	private FileInputStream fis;
	private XSSFWorkbook wb;
	private XSSFSheet sheet;
	
	public Time_Extractor(JTextPane textPane) {
		// 初始化TextPane字体
		docs = textPane.getDocument();
		attrset = new SimpleAttributeSet();
		erroMessage = new SimpleAttributeSet();
		// 普通数据为12号字体
		StyleConstants.setFontSize(attrset, 12);
		// 错误数据为12号红色字体
		StyleConstants.setFontSize(erroMessage, 12);
		StyleConstants.setForeground(erroMessage, Color.RED);
	}
	
	/**
	 * 获得指定Excel的输入流
	 * @param fileName 从文件夹名开始
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
	 * 对指定文件夹下的文件遍历并进行分析
	 * @param folderName 文件夹名
	 */
	public void extractColumeInFolder(String folderName) {
		String[] fileList = ObtainInput.obtainDir(folderName);
		for(String fileName: fileList) {
			//result开头的文件不进行分析
			if(!fileName.startsWith("result")) {
				outPutMessage("开始对"+fileName+"进行分析", attrset);
				initInput(folderName+"\\"+fileName);
				timeParse(folderName+"\\result_"+fileName);
			}
		}
	}
	
	/**
	 * 
	 * @param outPutFileName 输出文件名
	 */
	@SuppressWarnings("deprecation")
	public void timeParse(String outPutFileName) {
		
		XSSFRow row;
		
		HandleTimeParser handleTimeParser = new HandleTimeParser();
		
		//用于存序号
		List<String> numList = new ArrayList<String>();
		numList.add("序号");
		//用于存原始时间
		List<String> originList = new ArrayList<String>();
		originList.add("首次查询时间");
		//用于存第一次升级时间
		List<String> firstList = new ArrayList<String>();
		firstList.add("第一次升级时间");
		//用于存第二次升级时间
		List<String> secondList = new ArrayList<String>();
		secondList.add("第二次升级时间");
		//用于存第一次升级处理时间
		List<String> firstDurationList = new ArrayList<String>();
		firstDurationList.add("第一次升级解决时间");
		//用于存第二次升级处理时间
		List<String> secondDurationList = new ArrayList<String>();
		secondDurationList.add("第二次升级解决时间");
		
		SimpleDateFormat time=new SimpleDateFormat("yyyy MM dd HH mm ss");
		
		//一次升级处理时常
		String firstDuration = "";
		//二次升级处理时常
		String secondDuration = "";
		
		//首次查询时间
		Calendar originTime = null;
		//一次升级时间
		Calendar firstTime = null;
		//二次升级时间
		Calendar secondTime = null;
		
		outPutMessage("开始进行分析", attrset);
		
		for(int index=1; index<=sheet.getLastRowNum(); index++) {
			int line = index;
			outPutMessage("line "+line+":分析结束", attrset);
			
			row = sheet.getRow(index);
			originTime = Calendar.getInstance();
			
			//得到序列号
			numList.add(String.valueOf(row.getRowNum()));
			originTime = Calendar.getInstance();
			//得到首次查询时间
			try {
				originTime.setTime(row.getCell(3).getDateCellValue());
				originList.add(time.format(originTime.getTime()));
			} catch (Exception e) {
				//出现异常说明未得到数据
				//System.out.println("origin can't parser,line is: "+index);
				originList.add("空");
			}
			
			//如果第一次升级时间不为空，则进行第一次升级时间分析
			if(!(row.getCell(4) == null) && !(row.getCell(4).getCellType() == XSSFCell.CELL_TYPE_BLANK)) {
				firstTime = Calendar.getInstance();
				//如果cell中为日期格式则直接进行处理
				try {
					firstTime.setTime(row.getCell(4).getDateCellValue());
					firstList.add(time.format(firstTime.getTime()));
					firstDuration = String.format("%.2f", handleTimeParser.calculateTime(originTime, firstTime));
					firstDurationList.add(firstDuration);
				} catch(Exception e) {
					//出现异常说明未得到数据
					System.out.println("first exception,line is: "+index);
					firstList.add("");
					firstDurationList.add("");
				}
				
				//在第一次升级不为空的基础上，如果第二次升级时间不为空，则进行第一次升级时间分析
				if(!(row.getCell(5) == null) && !(row.getCell(5).getCellType() == XSSFCell.CELL_TYPE_BLANK)) {
					secondTime = Calendar.getInstance();
					//如果cell中为日期格式则直接进行处理
					try {
						secondTime.setTime(row.getCell(5).getDateCellValue());
						secondList.add(time.format(secondTime.getTime()));
						secondDuration = String.format("%.2f", handleTimeParser.calculateTime(firstTime, secondTime));
						secondDurationList.add(secondDuration);
					} catch(Exception e) {
						//出现异常说明未得到数据
						System.out.println("second exception,line is: "+index);
						secondList.add("");
						secondDurationList.add("");
					}
				} else {
					secondList.add("");
					secondDurationList.add("");
					//出现异常说明未得到数据
					//System.out.println("second can't parser,line is: "+index);
				}
				
			} else {
				firstList.add("");
				secondList.add("");
				firstDurationList.add("");
				secondDurationList.add("");
				//出现异常说明未得到数据
				//System.out.println("first can't parser,line is: "+index);
			}
			
		}
		outPutMessage("开始提取数据", attrset);
		outputExcel(numList, originList, firstList, secondList, firstDurationList, secondDurationList, outPutFileName);
		outPutMessage("提取完成", attrset);
	}

	public void outputExcel(List<String> numList, List<String> originList, List<String> firstList, List<String> secondList, 
			List<String> firstDurationList, List<String> secondDurationList, String outPutFileName) {
		
		Workbook outputWb = new XSSFWorkbook();
		Sheet outputSheet = outputWb.createSheet("new sheet");
		outputSheet.setColumnWidth(0, 100*10);
		outputSheet.setColumnWidth(1, 100*60);
		outputSheet.setColumnWidth(2, 100*60);
		outputSheet.setColumnWidth(3, 100*60);
		outputSheet.setColumnWidth(4, 100*60);
		outputSheet.setColumnWidth(5, 100*60);
		Row outputRow = null;
		Cell outputCell = null;
		for(int index=0; index<originList.size(); index++) {
			outputRow = outputSheet.createRow((short)index);
			//第1列
			outputCell = outputRow.createCell((short)0);
			outputCell.setCellValue(numList.get(index));
			//第2列
			outputCell = outputRow.createCell((short)1);
			outputCell.setCellValue(originList.get(index));
			//第3列
			outputCell = outputRow.createCell((short)2);
			outputCell.setCellValue(firstList.get(index));
			//第4列
			outputCell = outputRow.createCell((short)3);
			outputCell.setCellValue(secondList.get(index));
			//第5列
			outputCell = outputRow.createCell((short)4);
			outputCell.setCellValue(firstDurationList.get(index));
			//第6列
			outputCell = outputRow.createCell((short)5);
			outputCell.setCellValue(secondDurationList.get(index));
		}
		try {
			outputWb.write(ObtainOutput.obtainOutput(outPutFileName));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				wb.close();
				outputWb.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
		extractColumeInFolder("Time_Extractor");
	}
}
