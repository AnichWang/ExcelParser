package com.amzl.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HandleTimeParser {
	
	/**
	 * 工作时间为早9点到晚19点
	 * @param prior 开始时间 yyyy/M/d h/m
	 * @param after	结束时间  yyyy/M/d h/m
	 * @return
	 */
	public int calculateTime(String prior,String after) {
		//工作时长
		long duringTime = 0;

		Calendar priorCalendar = Calendar.getInstance();
		Calendar afterCalendar = Calendar.getInstance();
		//priorCalendar当天的早上开始工作时间
		Calendar startPriorCalendar = Calendar.getInstance();
		//priorCalendar当天的晚上结束工作时间
		Calendar endPriorCalendar = Calendar.getInstance();
		//afterCalendar当天的早上工作时间
		Calendar startAfterCalendar = Calendar.getInstance();
		//afterCalendar当天的晚上工作时间
		Calendar endAfterCalendar = Calendar.getInstance();
		//忽略毫秒
		priorCalendar.set(Calendar.MILLISECOND, 0);
		afterCalendar.set(Calendar.MILLISECOND, 0);
		startPriorCalendar.set(Calendar.MILLISECOND, 0);
		endPriorCalendar.set(Calendar.MILLISECOND, 0);
		startAfterCalendar.set(Calendar.MILLISECOND, 0);
		endAfterCalendar.set(Calendar.MILLISECOND, 0);
		
		ArrayList<Integer> priorList = getNumbers(prior);	
		if(priorList.size() < 6) {
			for(int i=priorList.size()-1; i<6; i++) {
				priorList.add(new Integer(0));
			}
		}
		
		ArrayList<Integer> afterList = getNumbers(after);
		if(priorList.size() < 6) {
			for(int i=priorList.size()-1; i<6; i++) {
				priorList.add(new Integer(0));
			}
		}
		//priorCanledar时间初始化,月份从0开始
		if(priorList.size() == 6) {
			priorCalendar.set((int)priorList.get(0), (int)priorList.get(1)-1, (int)priorList.get(2), (int)priorList.get(3), 
					(int)priorList.get(4), (int)priorList.get(5));
			//prior的早上工作时间
			startPriorCalendar.set((int)priorList.get(0), (int)priorList.get(1)-1, (int)priorList.get(2), 9, 0, 0);
			//prior的晚上工作时间
			endPriorCalendar.set((int)priorList.get(0), (int)priorList.get(1)-1, (int)priorList.get(2), 19, 0, 0);
		}
		//afterCalendar时间初始化
		if(afterList.size() == 6) {
			afterCalendar.set((int)afterList.get(0), (int)afterList.get(1)-1, (int)afterList.get(2), (int)afterList.get(3), 
					(int)afterList.get(4), (int)afterList.get(5));
			//after的早上工作时间
			startAfterCalendar.set((int)afterList.get(0), (int)afterList.get(1)-1, (int)afterList.get(2), 9, 0, 0);
			//after的晚上工作时间
			endAfterCalendar.set((int)afterList.get(0), (int)afterList.get(1)-1, (int)afterList.get(2), 19, 0, 0);
		}
		//priorCalendar早于或等于startPriorCalendar时间
		if(priorCalendar.compareTo(startPriorCalendar) == -1 || priorCalendar.compareTo(startPriorCalendar) == 0) {
			priorCalendar = startPriorCalendar;
			//afterCalendar在工作时间
			if((afterCalendar.compareTo(endAfterCalendar) == -1 || afterCalendar.compareTo(endAfterCalendar) == 0) 
					&& (afterCalendar.compareTo(startPriorCalendar) == 1 || afterCalendar.compareTo(startPriorCalendar) == 0)) {
				duringTime = afterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
			//afterCalendar早于startAfterCalendar,则时间为前一日的endAfterCalendar
			if(afterCalendar.compareTo(startAfterCalendar) == -1) {
				afterCalendar = startAfterCalendar;
				afterCalendar.add(Calendar.HOUR_OF_DAY, -14);
				duringTime = afterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis() ;
			}
			//afterCalendar晚于endAfterCalendar
			if(afterCalendar.compareTo(endAfterCalendar) == 1) {
				duringTime = endAfterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
		}
		//priorCalendar晚于endPriorCalendar时间
		if(priorCalendar.compareTo(endPriorCalendar) == 1 || priorCalendar.compareTo(endPriorCalendar) == 0) {
			//如果晚于工作时间，则从次日9点开始工作 
			priorCalendar = endPriorCalendar;
			priorCalendar.add(Calendar.HOUR_OF_DAY, 14);
			//afterCalendar在工作时间
			if((afterCalendar.compareTo(endAfterCalendar) == -1 || afterCalendar.compareTo(endAfterCalendar) == 0) 
					&& (afterCalendar.compareTo(startPriorCalendar) == 1 || afterCalendar.compareTo(startPriorCalendar) == 0)) {
				duringTime = afterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
			//afterCalendar早于startAfterCalendar,则时间为当日的startAfterCalendar
			if(afterCalendar.compareTo(startAfterCalendar) == -1) {
				afterCalendar = startAfterCalendar;
				duringTime = startAfterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
			//afterCalendar晚于endAfterCalendar
			if(afterCalendar.compareTo(endAfterCalendar) == 1) {
				duringTime = endAfterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
		}
		//priorCalendar在工作时间
		if(priorCalendar.compareTo(startPriorCalendar) == 1 && priorCalendar.compareTo(endPriorCalendar) == -1) {
			//afterCalendar在工作时间
			if((afterCalendar.compareTo(endAfterCalendar) == -1 || afterCalendar.compareTo(endAfterCalendar) == 0) 
					&& (afterCalendar.compareTo(startPriorCalendar) == 1 || afterCalendar.compareTo(startPriorCalendar) == 0)) {
				duringTime = afterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
			//afterCalendar早于startAfterCalendar,则时间为前一日的endAfterCalendar
			if(afterCalendar.compareTo(startAfterCalendar) == -1) {
				afterCalendar = startAfterCalendar;
				duringTime = afterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
			//afterCalendar晚于endAfterCalendar
			if(afterCalendar.compareTo(endAfterCalendar) == 1) {
				duringTime = endAfterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
		}

		System.out.println("相差 "+ ((double)duringTime/(1000*60*60)-(caculateDay(priorCalendar, afterCalendar))*14) +" hour");

		return 0;
	}

	/**
	 * 工作时间为早9点到晚19点
	 * @param priorCalendar 开始时间
	 * @param afterCalendar 结束时间
	 * @return
	 */
	public double calculateTime(Calendar priorCalendar, Calendar afterCalendar) {
		//工作时长
		double duringTime = 0;

		//priorCalendar当天的早上开始工作时间
		Calendar startPriorCalendar = Calendar.getInstance();
		startPriorCalendar.set(priorCalendar.get(Calendar.YEAR), priorCalendar.get(Calendar.MONTH), 
				priorCalendar.get(Calendar.DAY_OF_MONTH), 9, 0, 0);
		//priorCalendar当天的晚上结束工作时间
		Calendar endPriorCalendar = Calendar.getInstance();
		endPriorCalendar.set(priorCalendar.get(Calendar.YEAR), priorCalendar.get(Calendar.MONTH), 
				priorCalendar.get(Calendar.DAY_OF_MONTH), 19, 0, 0);
		//afterCalendar当天的早上工作时间
		Calendar startAfterCalendar = Calendar.getInstance();
		startAfterCalendar.set(afterCalendar.get(Calendar.YEAR), afterCalendar.get(Calendar.MONTH),
				afterCalendar.get(Calendar.DAY_OF_MONTH), 9, 0, 0);
		//afterCalendar当天的晚上工作时间
		Calendar endAfterCalendar = Calendar.getInstance();
		endAfterCalendar.set(afterCalendar.get(Calendar.YEAR), afterCalendar.get(Calendar.MONTH),
				afterCalendar.get(Calendar.DAY_OF_MONTH), 19, 0, 0);
		//忽略毫秒
		priorCalendar.set(Calendar.MILLISECOND, 0);
		afterCalendar.set(Calendar.MILLISECOND, 0);
		startPriorCalendar.set(Calendar.MILLISECOND, 0);
		endPriorCalendar.set(Calendar.MILLISECOND, 0);
		startAfterCalendar.set(Calendar.MILLISECOND, 0);
		endAfterCalendar.set(Calendar.MILLISECOND, 0);
		//priorCalendar在早于startPriorCalendar
		if(priorCalendar.compareTo(startPriorCalendar) == -1 || priorCalendar.compareTo(startPriorCalendar) == 0) {
			priorCalendar = startPriorCalendar;
			//afterCalendar在工作时间
			if((afterCalendar.compareTo(endAfterCalendar) == -1 || afterCalendar.compareTo(endAfterCalendar) == 0) 
					&& (afterCalendar.compareTo(startPriorCalendar) == 1 || afterCalendar.compareTo(startPriorCalendar) == 0)) {
				duringTime = afterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
			//afterCalendar早于startAfterCalendar,则时间为前一日的endAfterCalendar
			if(afterCalendar.compareTo(startAfterCalendar) == -1) {
				afterCalendar = startAfterCalendar;
				afterCalendar.add(Calendar.HOUR_OF_DAY, -14);
				duringTime = afterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
			//afterCalendar晚于endAfterCalendar
			if(afterCalendar.compareTo(endAfterCalendar) == 1) {
				duringTime = endAfterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
		}
		//priorCalendar晚于endPriorCalendar时间
		if(priorCalendar.compareTo(endPriorCalendar) == 1 || priorCalendar.compareTo(endPriorCalendar) == 0) {
			//如果晚于工作时间，则从次日9点开始工作 
			priorCalendar = endPriorCalendar;
			priorCalendar.add(Calendar.HOUR_OF_DAY, 14);
			//afterCalendar在工作时间
			if((afterCalendar.compareTo(endAfterCalendar) == -1 || afterCalendar.compareTo(endAfterCalendar) == 0) 
					&& (afterCalendar.compareTo(startPriorCalendar) == 1 || afterCalendar.compareTo(startPriorCalendar) == 0)) {
				duringTime = afterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
			//afterCalendar早于startAfterCalendar,则时间为当日的startAfterCalendar
			if(afterCalendar.compareTo(startAfterCalendar) == -1) {
				afterCalendar = startAfterCalendar;
				duringTime = startAfterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
			//afterCalendar晚于endAfterCalendar
			if(afterCalendar.compareTo(endAfterCalendar) == 1) {
				duringTime = endAfterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
		}
		//priorCalendar在工作时间
		if(priorCalendar.compareTo(startPriorCalendar) == 1 && priorCalendar.compareTo(endPriorCalendar) == -1) {
			//afterCalendar在工作时间
			if((afterCalendar.compareTo(endAfterCalendar) == -1 || afterCalendar.compareTo(endAfterCalendar) == 0) 
					&& (afterCalendar.compareTo(startPriorCalendar) == 1 || afterCalendar.compareTo(startPriorCalendar) == 0)) {
				duringTime = afterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
			//afterCalendar早于startAfterCalendar,则时间为前一日的endAfterCalendar
			if(afterCalendar.compareTo(startAfterCalendar) == -1) {
				afterCalendar = startAfterCalendar;
				duringTime = afterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
			//afterCalendar晚于endAfterCalendar
			if(afterCalendar.compareTo(endAfterCalendar) == 1) {
				duringTime = endAfterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
		}

		//System.out.println("相差 "+ ((double)duringTime/(1000*60*60)-(caculateDay(priorCalendar, afterCalendar))*14) +" hour");

		return ((double)duringTime/(1000*60*60)-(caculateDay(priorCalendar, afterCalendar))*14);
	}
	
	public long caculateDay(Calendar priorCalendar, Calendar afterCalendar) {
		//清除小时，分钟与秒
		priorCalendar.set(Calendar.SECOND, 0);
		priorCalendar.set(Calendar.MINUTE, 0);
		priorCalendar.set(Calendar.HOUR_OF_DAY, 0);
		afterCalendar.set(Calendar.SECOND, 0);
		afterCalendar.set(Calendar.MINUTE, 0);
		afterCalendar.set(Calendar.HOUR_OF_DAY, 0);
		
		return (afterCalendar.getTimeInMillis()-priorCalendar.getTimeInMillis())/(1000*60*60*24);
	}
	
	public ArrayList<Integer> getNumbers(String str) {
		ArrayList<Integer> tempList = new ArrayList<Integer>();
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(str);
		while(matcher.find()) {
			tempList.add(Integer.parseInt(matcher.group()));
		}
		return tempList;
	}
	
	public static void main(String[] args) {
		HandleTimeParser handleTimeParser = new HandleTimeParser();
		handleTimeParser.calculateTime("2017/1/7  18:30", "2017/1/8  20:00");
	}
}
