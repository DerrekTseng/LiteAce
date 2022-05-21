package lite.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DateTools {

	/** yyyy/MM/dd HH:mm:ss **/
	public static final String DateTimeFormatString = "yyyy/MM/dd HH:mm:ss";
	/** yyyyMMddHHmmss **/
	public static final String DateTimeFormatNumber = "yyyyMMddHHmmss";

	/**
	 * 將 yyyy/MM/dd HH:mm:ss 格式的字串轉成 Date
	 * 
	 * @param string yyyy/MM/dd HH:mm:ss
	 * @return Date
	 * @throws ParseException
	 */
	public static Date parseDateTimeFormatString(String string) throws ParseException {
		return parseDate(DateTimeFormatString, string);
	}

	/**
	 * 將現在的時間轉成 yyyy/MM/dd HH:mm:ss 格式的字串
	 * 
	 * @return yyyy/MM/dd HH:mm:ss
	 */
	public static String getCurrentDateTimeFormatString() {
		return parseString(DateTimeFormatString, new Date());
	}

	/**
	 * 將Date轉成 yyyy/MM/dd HH:mm:ss 格式的字串
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateTimeFormatString(Date date) {
		return parseString(DateTimeFormatString, date);
	}

	/**
	 * 將 Long Milli Seconds 轉成 yyyy/MM/dd HH:mm:ss 格式的字串
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateTimeFormatString(Long date) {
		return parseString(DateTimeFormatString, new Date(date));
	}

	/**
	 * 將 yyyyMMddHHmmss 格式的字串轉成 Date
	 * 
	 * @param string yyyyMMddHHmmss
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDateTimeFormatNumber(String string) throws ParseException {
		return parseDate(DateTimeFormatNumber, string);
	}

	/**
	 * 將現在的時間轉成 yyyyMMddHHmmss 格式的字串
	 * 
	 * @return yyyyMMddHHmmss
	 */
	public static String getCurrentDateTimeFormatNumber() {
		return parseString(DateTimeFormatNumber, new Date());
	}

	/**
	 * 將現在的時間轉成 yyyyMMddHHmmss 格式的 Long
	 * 
	 * @return yyyyMMddHHmmss
	 */
	public static Long getCurrentDateTimeFormatNumberLong() {
		return Long.parseLong(parseString(DateTimeFormatNumber, new Date()));
	}

	/**
	 * 將 yyyyMMddHHmmss 格式的字串轉成 yyyy/MM/dd HH:mm:ss 格式的字串
	 * 
	 * @param dateTimeFormatNumber yyyyMMddHHmmss
	 * @return
	 */
	public static String DateTimeFormatNumberToDateTimeFormatString(String dateTimeFormatNumber) {
		try {
			return parseString(DateTimeFormatString, parseDateTimeFormatNumber(dateTimeFormatNumber));
		} catch (ParseException e) {
			return "";
		}
	}

	/**
	 * 將Date轉成 yyyyMMddHHmmss 格式的字串
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateTimeFormatNumber(Date date) {
		return parseString(DateTimeFormatNumber, date);
	}

	/**
	 * 將 Long Milli Seconds 轉成 yyyyMMddHHmmss 格式的字串
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateTimeFormatNumber(Long date) {
		return parseString(DateTimeFormatNumber, new Date(date));
	}

	/**
	 * 將String 轉換成 Date
	 * 
	 * @param format
	 * @param dateString
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDate(String format, String dateString) throws ParseException {
		return (new SimpleDateFormat(format)).parse(dateString);
	}

	/**
	 * 將Date 轉換成 String
	 * 
	 * @param format
	 * @param dateString
	 * @return
	 * @throws ParseException
	 */
	public static String parseString(String format, Date date) {
		return (new SimpleDateFormat(format)).format(date);
	}

	/**
	 * 取得當前日期
	 * 
	 * @return
	 */
	public static Date getDate(long date) {
		return new Date(date);
	}

	/**
	 * 取得當前日期
	 * 
	 * @return
	 */
	public static Date getDate() {
		return new Date();
	}

	/**
	 * 取得指定日期
	 * 
	 * @param year
	 * @param month
	 * @param date
	 * @return
	 */
	public static Date getDate(int year, int month, int date) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, date);
		return calendar.getTime();
	}

	/**
	 * 加日期 參數如果是負數則會減日期
	 * 
	 * @param d
	 * @param year
	 * @param month
	 * @param date
	 */
	public static Date addDate(Date d, int year, int month, int date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		calendar.add(Calendar.YEAR, year);
		calendar.add(Calendar.MONTH, month);
		calendar.add(Calendar.DATE, date);
		return calendar.getTime();
	}

	/**
	 * 加時間 參數如果是負數則會減時間
	 * 
	 * @param d
	 * @param hour
	 * @param minute
	 * @param second
	 */
	public static Date addTime(Date d, int hour, int minute, int second) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		calendar.add(Calendar.HOUR, hour);
		calendar.add(Calendar.MINUTE, minute);
		calendar.add(Calendar.SECOND, second);
		return calendar.getTime();
	}

	public static ZonedDateTime toZonedDateTime(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		int nanoOfSecond = calendar.get(Calendar.MILLISECOND) * 1000000;
		return ZonedDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond, ZoneId.systemDefault());
	}

	public static Date toDate(ZonedDateTime zonedDateTime) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, zonedDateTime.getYear());
		calendar.set(Calendar.MONTH, zonedDateTime.getMonthValue() - 1);
		calendar.set(Calendar.DAY_OF_MONTH, zonedDateTime.getDayOfMonth());
		calendar.set(Calendar.HOUR, zonedDateTime.getHour());
		calendar.set(Calendar.MINUTE, zonedDateTime.getMinute());
		calendar.set(Calendar.SECOND, zonedDateTime.getSecond());
		calendar.set(Calendar.MILLISECOND, zonedDateTime.getNano() / 1000000);
		return calendar.getTime();
	}

	/**
	 * 將ZonedDateTime轉成 yyyy/MM/dd HH:mm:ss 格式的字串
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateTimeFormatString(ZonedDateTime zonedDateTime) {
		return zonedDateTime.format(DateTimeFormatter.ofPattern(DateTimeFormatString));
	}

	/**
	 * 將ZonedDateTime轉成 yyyy/MM/dd HH:mm:ss 格式的字串
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateTimeFormatNumber(ZonedDateTime zonedDateTime) {
		return zonedDateTime.format(DateTimeFormatter.ofPattern(DateTimeFormatNumber));
	}

}
