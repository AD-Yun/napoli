package search.crawl.napoli.util;

import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

/**
 * 일반적인 사용을 위한 도움을 주는 클래스이다.
 * 
 * @author 검색본부 검색Infra개발팀 김진화 jnhwkim@sk.com
 * @date 2011.11.18
 */
public final class Commons {
	private Commons() { }
	/**
	 * 현재 날짜와 시각을 문자열로 반환한다.
	 * @return 현재 날짜와 시각을 문자열로 반환
	 */
	public static String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}
	/**
	 * 현재 날짜를 문자열로 반환한다.
	 * @return 현재 날짜를 문자열로 반환
	 */
	public static String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		return dateFormat.format(date);
	}
	/**
	 * 주어진 문자열 날짜를 <code>java.util.Date</code>객체로 반환한다.
	 * 주어진 문자열은 yyyyMMdd 또는 yyyy-MM-dd 포맷을 지원한다.
	 * @return 주어진 날짜를 <code>java.util.Date</code>객체로 반환
	 */
	public static Date getDate(String yyyymmdd) throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		return dateFormat.parse(yyyymmdd.replaceAll("-", ""));
	}
	/**
	 * 현재 시각을 문자열로 반환한다.
	 * @return 현재 시각을 표준 출력으로 반환
	 */
	public static String getTime() {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}
	/**
	 * <code>days</code>일 만큼 전의 날짜를 나타내는 문자열을 반환한다.
	 * @param yyyymmdd 기준이 되는 날짜를 나타내는 문자열
	 * @param days 몇 일 전 날짜를 알고 싶은지 나타내는 숫자
	 * @return <code>days</code>일 만큼 전의 날짜를 나타내는 문자열
	 * @throws ParseException 날짜 문자열 분석 예외 발생 시
	 */
	public static String getDaysAgo(String yyyymmdd, int days) throws ParseException {
		SimpleDateFormat yyyymmddForm = new SimpleDateFormat("yyyyMMdd");

		Calendar c = Calendar.getInstance();
		c.setTime(yyyymmddForm.parse(yyyymmdd));
		c.add(Calendar.DATE, -days);

		return yyyymmddForm.format(c.getTime());
	}
	/**
	 * <code>days</code>일 만큼 전의 날짜를 나타내는 {문자열}_{날짜} 형태의 문자열을 반환한다.
	 * @param s_yyyymmdd {문자열}_{날짜} 형태의 문자열
	 * @param days 몇 일 전 날짜를 알고 싶은지 나타내는 숫자
	 * @return <code>days</code>일 만큼 전의 날짜를 나타내는 {문자열}_{날짜} 형태의 문자열
	 * @throws ParseException 날짜 문자열 분석 예외 발생 시
	 */
	public static String getStringDaysAgo(String s_yyyymmdd, int days) throws ParseException {
		String prefix = s_yyyymmdd.split("_")[0];
		String yyyymmdd = s_yyyymmdd.split("_")[1];
		return prefix + "_" + Commons.getDaysAgo(yyyymmdd, days);
	}
	/**
	 * 시간(h), 분(m), 초(s)로 나누어 출력된 문자열을 반환한다.
	 * @param time 1970년 1월 1일 0시 0분 0초(GMT)로부터의 1/1000 초
	 * @return 시간(h), 분(m), 초(s)로 나누어 출력된 문자열
	 */
	public static String time2str(long time) {
		int mil = (int) (time % 1000);
		int sec = (int) (time / 1000);
		int minutes = sec / 60;
		int hours = minutes / 60;

		int h = hours;
		int m = minutes - hours * 60;
		int s = sec - (h * 3600 + m * 60);

		String result = "";

		if(0 != h) result += h + "h ";
		if(0 != m) result += m + "m ";

		result += s;
		result += "." + (mil > 999 ? "" : mil > 99 ? "0" : mil > 9 ? "00" : "000") + mil + "s";

		return result;
	}
	/**
	 * 한글 2-byte 기준으로 문자열의 길이를 반환한다.
	 * (자바에서는 유니코드 글자 단위로 길이를 반환한다.)
	 * @return 한글 2-byte 기준 질의어 길이
	 */
	public static int getByteLength(String s) {
		if(s == null) return -1;
		
		int len = 0;
		for(int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c  <  0xac00 || 0xd7a3 < c) len++;
			else  len += 2; // Korean letter case 
		}
		return len;
	}
	/**
	 * 경과 시간을 로그로 출력한다.
	 * @param clazz 로그를 발생시키는 클래스 개체
	 * @param base 경과 시간의 기준이 되는 시각을 나타내는 1970년 1월 1일 0시 0분 0초(GMT)로부터의 1/1000 초
	 */
	public static void logElapseTime(Class<?> clazz, long base) {
		long span = Commons.getTimeSpan(base);
		// Commons.log(clazz, Commons.time2str(span));
		// TODO : logging 
	}
	/**
	 * 1/1000 초 단위의 경과 시간을 반환한다.
	 * @param basse 경과 시간의 기준이 되는 시각을 나타내는 1970년 1월 1일 0시 0분 0초(GMT)로부터의 1/1000 초
	 * @return 1/1000 초 단위의 경과 시간
	 */
	public static long getTimeSpan(long base) {
		return System.currentTimeMillis() - base;
	}
	/**
	 * 주어진 시간 동안 같은 작업 블럭 안에서 멈춘다.
	 * @param millis 멈추고 있는 밀리초 단위 시간
	 */
	public static void sleepInLoop(long millis) {
		long base = System.currentTimeMillis();
		while(millis > Commons.getTimeSpan(base)) { 
			continue;
		}
	}
	/**
	 * 현재 작업 쓰레드를 주어진 시간 동안 멈추게 한다.
	 * @param millis 멈추고 있는 밀리초 단위 시간
	 */
	public static void sleep(long millis) {
		try {
	    	Thread.sleep(millis);
	    } catch(InterruptedException e) {
	    	e.printStackTrace();
	    	// @TODO : write logging
	    }
	}
	
	public static void sleepSeconds(int seconds) {
		try {
			long millis = (long)(seconds * 1000);
	    	Thread.sleep(millis);
	    } catch(InterruptedException e) {
	    	e.printStackTrace();
	    	// @TODO : write logging
	    }
	}
	
	/**
	 * Collection(Set, List 등)의 원소를 지정된 구분자로 합(join)하여 문자열로 만든다.
	 * @param collection 구분자로 합할 문자열 콜렉션
	 * @param delimiter 구분자
	 */
	public static String join(Collection<String> collection, String delimiter) {
		StringBuffer buffer = new StringBuffer();
		for(String e : collection) {
			if(0 != buffer.length()) buffer.append(delimiter);
			buffer.append(e);
		}
		return buffer.toString();
	}
	/**
	 * 배열의 원소를 지정된 구분자로 합(join)하여 문자열로 만든다.
	 * @param array 구분자로 합할 문자열 배열
	 * @param delimiter 구분자
	 */
	public static String join(String[] array, String delimiter) {
		StringBuffer buffer = new StringBuffer();
		for(String e : array) {
			if(0 != buffer.length()) buffer.append(delimiter);
			buffer.append(e);
		}
		return buffer.toString();
	}
	public static String replaceAll(String s, String pattern, String replacement) {
		return Commons.join(s.split(pattern), replacement);
	}
	
	public static String bytes2HexString(byte[] bytes) {
		final char [] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
		
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; ++j) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		
		return new String(hexChars);
	}
	
	public static String hexString2String(String hex, String charset) throws Exception {
		byte[] tmpBytes = new byte[hex.length() / 2];
		int hexLength = hex.length();
		int j = 0;
		
		for(int i = 0; i < hexLength; i+=2) {
			tmpBytes[j++] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
		}
		
		return new String(tmpBytes, charset);
	}
	
	public static String getMD5Hash(String orgData) throws Exception {
		byte[] tmpBytes = orgData.getBytes();
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] resultBytes = md.digest(tmpBytes);
		
		return bytes2HexString(resultBytes);
	}
	
	public static String getMD5Hash(byte[] orgData) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] resultBytes = md.digest(orgData);
		
		return bytes2HexString(resultBytes);
	}
	
	public static String rtrim(String s, char compare) {
		char[] val = s.toCharArray();
		int st = 0;
		int len = s.length();
		while(st < len && val[len -1] <= compare) {
			len--;
		}
		
		return s.substring(0, len);
	}
	
	public static String escapeQuery(String str) {
		String temp = str.replace("\'", "\\\'");
		temp = temp.replace("\"", "\\\"");
		temp = temp.replace("\\", "\\\\");
		
		return temp;
	}
	
	
}