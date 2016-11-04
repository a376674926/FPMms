package com.android.mms.utils;

import java.text.SimpleDateFormat;

import android.util.Log;

/**
 * 时间格式化工具类
 * 
 * @author jackey
 * 
 */
public class DateFormat {
	
	private static final String TAG = DateFormat.class.getSimpleName() ;

	/**
	 * 一分钟的毫秒值，用于判断上次的更新时间
	 */
	public static final long ONE_MINUTE = 60 * 1000;

	/**
	 * 一小时的毫秒值，用于判断上次的更新时间
	 */
	public static final long ONE_HOUR = 60 * ONE_MINUTE;

	/**
	 * 一天的毫秒值，用于判断上次的更新时间
	 */
	public static final long ONE_DAY = 24 * ONE_HOUR;

	/**
	 * 一月的毫秒值，用于判断上次的更新时间
	 */
	public static final long ONE_MONTH = 30 * ONE_DAY;

	/**
	 * 一年的毫秒值，用于判断上次的更新时间
	 */
	public static final long ONE_YEAR = 12 * ONE_MONTH;

	public static String format(long dateTime) {
		long curTime = System.currentTimeMillis();
		long timePassed = curTime - dateTime;
		if (timePassed > ONE_DAY) { // 超过一天
			return new SimpleDateFormat("MM/dd").format(dateTime);
		} else if (timePassed > ONE_YEAR) {
			return new SimpleDateFormat("yyyy/MM/dd").format(dateTime);
		} else {// 小于一天
			return new SimpleDateFormat("kk:mm").format(dateTime);
		}

	}
}
