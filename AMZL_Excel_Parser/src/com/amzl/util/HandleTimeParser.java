package com.amzl.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HandleTimeParser {
	
	/**
	 * ����ʱ��Ϊ��9�㵽��19��
	 * @param prior ��ʼʱ�� yyyy/M/d h/m
	 * @param after	����ʱ��  yyyy/M/d h/m
	 * @return
	 */
	public int calculateTime(String prior,String after) {
		//����ʱ��
		long duringTime = 0;

		Calendar priorCalendar = Calendar.getInstance();
		Calendar afterCalendar = Calendar.getInstance();
		//priorCalendar��������Ͽ�ʼ����ʱ��
		Calendar startPriorCalendar = Calendar.getInstance();
		//priorCalendar��������Ͻ�������ʱ��
		Calendar endPriorCalendar = Calendar.getInstance();
		//afterCalendar��������Ϲ���ʱ��
		Calendar startAfterCalendar = Calendar.getInstance();
		//afterCalendar��������Ϲ���ʱ��
		Calendar endAfterCalendar = Calendar.getInstance();
		//���Ժ���
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
		//priorCanledarʱ���ʼ��,�·ݴ�0��ʼ
		if(priorList.size() == 6) {
			priorCalendar.set((int)priorList.get(0), (int)priorList.get(1)-1, (int)priorList.get(2), (int)priorList.get(3), 
					(int)priorList.get(4), (int)priorList.get(5));
			//prior�����Ϲ���ʱ��
			startPriorCalendar.set((int)priorList.get(0), (int)priorList.get(1)-1, (int)priorList.get(2), 9, 0, 0);
			//prior�����Ϲ���ʱ��
			endPriorCalendar.set((int)priorList.get(0), (int)priorList.get(1)-1, (int)priorList.get(2), 19, 0, 0);
		}
		//afterCalendarʱ���ʼ��
		if(afterList.size() == 6) {
			afterCalendar.set((int)afterList.get(0), (int)afterList.get(1)-1, (int)afterList.get(2), (int)afterList.get(3), 
					(int)afterList.get(4), (int)afterList.get(5));
			//after�����Ϲ���ʱ��
			startAfterCalendar.set((int)afterList.get(0), (int)afterList.get(1)-1, (int)afterList.get(2), 9, 0, 0);
			//after�����Ϲ���ʱ��
			endAfterCalendar.set((int)afterList.get(0), (int)afterList.get(1)-1, (int)afterList.get(2), 19, 0, 0);
		}
		//priorCalendar���ڻ����startPriorCalendarʱ��
		if(priorCalendar.compareTo(startPriorCalendar) == -1 || priorCalendar.compareTo(startPriorCalendar) == 0) {
			priorCalendar = startPriorCalendar;
			//afterCalendar�ڹ���ʱ��
			if((afterCalendar.compareTo(endAfterCalendar) == -1 || afterCalendar.compareTo(endAfterCalendar) == 0) 
					&& (afterCalendar.compareTo(startPriorCalendar) == 1 || afterCalendar.compareTo(startPriorCalendar) == 0)) {
				duringTime = afterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
			//afterCalendar����startAfterCalendar,��ʱ��Ϊǰһ�յ�endAfterCalendar
			if(afterCalendar.compareTo(startAfterCalendar) == -1) {
				afterCalendar = startAfterCalendar;
				afterCalendar.add(Calendar.HOUR_OF_DAY, -14);
				duringTime = afterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis() ;
			}
			//afterCalendar����endAfterCalendar
			if(afterCalendar.compareTo(endAfterCalendar) == 1) {
				duringTime = endAfterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
		}
		//priorCalendar����endPriorCalendarʱ��
		if(priorCalendar.compareTo(endPriorCalendar) == 1 || priorCalendar.compareTo(endPriorCalendar) == 0) {
			//������ڹ���ʱ�䣬��Ӵ���9�㿪ʼ���� 
			priorCalendar = endPriorCalendar;
			priorCalendar.add(Calendar.HOUR_OF_DAY, 14);
			//afterCalendar�ڹ���ʱ��
			if((afterCalendar.compareTo(endAfterCalendar) == -1 || afterCalendar.compareTo(endAfterCalendar) == 0) 
					&& (afterCalendar.compareTo(startPriorCalendar) == 1 || afterCalendar.compareTo(startPriorCalendar) == 0)) {
				duringTime = afterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
			//afterCalendar����startAfterCalendar,��ʱ��Ϊ���յ�startAfterCalendar
			if(afterCalendar.compareTo(startAfterCalendar) == -1) {
				afterCalendar = startAfterCalendar;
				duringTime = startAfterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
			//afterCalendar����endAfterCalendar
			if(afterCalendar.compareTo(endAfterCalendar) == 1) {
				duringTime = endAfterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
		}
		//priorCalendar�ڹ���ʱ��
		if(priorCalendar.compareTo(startPriorCalendar) == 1 && priorCalendar.compareTo(endPriorCalendar) == -1) {
			//afterCalendar�ڹ���ʱ��
			if((afterCalendar.compareTo(endAfterCalendar) == -1 || afterCalendar.compareTo(endAfterCalendar) == 0) 
					&& (afterCalendar.compareTo(startPriorCalendar) == 1 || afterCalendar.compareTo(startPriorCalendar) == 0)) {
				duringTime = afterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
			//afterCalendar����startAfterCalendar,��ʱ��Ϊǰһ�յ�endAfterCalendar
			if(afterCalendar.compareTo(startAfterCalendar) == -1) {
				afterCalendar = startAfterCalendar;
				duringTime = afterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
			//afterCalendar����endAfterCalendar
			if(afterCalendar.compareTo(endAfterCalendar) == 1) {
				duringTime = endAfterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
		}

		System.out.println("��� "+ ((double)duringTime/(1000*60*60)-(caculateDay(priorCalendar, afterCalendar))*14) +" hour");

		return 0;
	}

	/**
	 * ����ʱ��Ϊ��9�㵽��19��
	 * @param priorCalendar ��ʼʱ��
	 * @param afterCalendar ����ʱ��
	 * @return
	 */
	public double calculateTime(Calendar priorCalendar, Calendar afterCalendar) {
		//����ʱ��
		double duringTime = 0;

		//priorCalendar��������Ͽ�ʼ����ʱ��
		Calendar startPriorCalendar = Calendar.getInstance();
		startPriorCalendar.set(priorCalendar.get(Calendar.YEAR), priorCalendar.get(Calendar.MONTH), 
				priorCalendar.get(Calendar.DAY_OF_MONTH), 9, 0, 0);
		//priorCalendar��������Ͻ�������ʱ��
		Calendar endPriorCalendar = Calendar.getInstance();
		endPriorCalendar.set(priorCalendar.get(Calendar.YEAR), priorCalendar.get(Calendar.MONTH), 
				priorCalendar.get(Calendar.DAY_OF_MONTH), 19, 0, 0);
		//afterCalendar��������Ϲ���ʱ��
		Calendar startAfterCalendar = Calendar.getInstance();
		startAfterCalendar.set(afterCalendar.get(Calendar.YEAR), afterCalendar.get(Calendar.MONTH),
				afterCalendar.get(Calendar.DAY_OF_MONTH), 9, 0, 0);
		//afterCalendar��������Ϲ���ʱ��
		Calendar endAfterCalendar = Calendar.getInstance();
		endAfterCalendar.set(afterCalendar.get(Calendar.YEAR), afterCalendar.get(Calendar.MONTH),
				afterCalendar.get(Calendar.DAY_OF_MONTH), 19, 0, 0);
		//���Ժ���
		priorCalendar.set(Calendar.MILLISECOND, 0);
		afterCalendar.set(Calendar.MILLISECOND, 0);
		startPriorCalendar.set(Calendar.MILLISECOND, 0);
		endPriorCalendar.set(Calendar.MILLISECOND, 0);
		startAfterCalendar.set(Calendar.MILLISECOND, 0);
		endAfterCalendar.set(Calendar.MILLISECOND, 0);
		//priorCalendar������startPriorCalendar
		if(priorCalendar.compareTo(startPriorCalendar) == -1 || priorCalendar.compareTo(startPriorCalendar) == 0) {
			priorCalendar = startPriorCalendar;
			//afterCalendar�ڹ���ʱ��
			if((afterCalendar.compareTo(endAfterCalendar) == -1 || afterCalendar.compareTo(endAfterCalendar) == 0) 
					&& (afterCalendar.compareTo(startPriorCalendar) == 1 || afterCalendar.compareTo(startPriorCalendar) == 0)) {
				duringTime = afterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
			//afterCalendar����startAfterCalendar,��ʱ��Ϊǰһ�յ�endAfterCalendar
			if(afterCalendar.compareTo(startAfterCalendar) == -1) {
				afterCalendar = startAfterCalendar;
				afterCalendar.add(Calendar.HOUR_OF_DAY, -14);
				duringTime = afterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
			//afterCalendar����endAfterCalendar
			if(afterCalendar.compareTo(endAfterCalendar) == 1) {
				duringTime = endAfterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
		}
		//priorCalendar����endPriorCalendarʱ��
		if(priorCalendar.compareTo(endPriorCalendar) == 1 || priorCalendar.compareTo(endPriorCalendar) == 0) {
			//������ڹ���ʱ�䣬��Ӵ���9�㿪ʼ���� 
			priorCalendar = endPriorCalendar;
			priorCalendar.add(Calendar.HOUR_OF_DAY, 14);
			//afterCalendar�ڹ���ʱ��
			if((afterCalendar.compareTo(endAfterCalendar) == -1 || afterCalendar.compareTo(endAfterCalendar) == 0) 
					&& (afterCalendar.compareTo(startPriorCalendar) == 1 || afterCalendar.compareTo(startPriorCalendar) == 0)) {
				duringTime = afterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
			//afterCalendar����startAfterCalendar,��ʱ��Ϊ���յ�startAfterCalendar
			if(afterCalendar.compareTo(startAfterCalendar) == -1) {
				afterCalendar = startAfterCalendar;
				duringTime = startAfterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
			//afterCalendar����endAfterCalendar
			if(afterCalendar.compareTo(endAfterCalendar) == 1) {
				duringTime = endAfterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
		}
		//priorCalendar�ڹ���ʱ��
		if(priorCalendar.compareTo(startPriorCalendar) == 1 && priorCalendar.compareTo(endPriorCalendar) == -1) {
			//afterCalendar�ڹ���ʱ��
			if((afterCalendar.compareTo(endAfterCalendar) == -1 || afterCalendar.compareTo(endAfterCalendar) == 0) 
					&& (afterCalendar.compareTo(startPriorCalendar) == 1 || afterCalendar.compareTo(startPriorCalendar) == 0)) {
				duringTime = afterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
			//afterCalendar����startAfterCalendar,��ʱ��Ϊǰһ�յ�endAfterCalendar
			if(afterCalendar.compareTo(startAfterCalendar) == -1) {
				afterCalendar = startAfterCalendar;
				duringTime = afterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
			//afterCalendar����endAfterCalendar
			if(afterCalendar.compareTo(endAfterCalendar) == 1) {
				duringTime = endAfterCalendar.getTimeInMillis() - priorCalendar.getTimeInMillis();
			}
		}

		//System.out.println("��� "+ ((double)duringTime/(1000*60*60)-(caculateDay(priorCalendar, afterCalendar))*14) +" hour");

		return ((double)duringTime/(1000*60*60)-(caculateDay(priorCalendar, afterCalendar))*14);
	}
	
	public long caculateDay(Calendar priorCalendar, Calendar afterCalendar) {
		//���Сʱ����������
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
