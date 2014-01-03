package search.crawl.napoli.util;

import static java.lang.Math.pow;
import static java.lang.Math.round;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.Format;

import org.apache.commons.lang.StringUtils;


public final class DocIDGenerator {

	public static final int serverBits = 10; 
	public static final int sequenceBits = 12; 
	public static final short serverIdUpperBound = (short) (round(pow(2, serverBits)) - 1); 
	public static final short sequenceUpperBound = (short) (round(pow(2, sequenceBits)) - 1); 

	public static final int timestampLShift = 22; 
	public static final int serverLShift = sequenceBits; 
	public static final int sequenceLShift = 0; 

	private static long serverId  = 0; 
	private static long pauseMs = 0L;
	private static short sequenceNum = 0; 
	private static long lastTimestamp = -1L; 
	private final static Object mutex = new Object();
	static { 
		/**
		 * serverid create. 
		 * IP(.을 제외한 뒤의 6자리) + PID(7자리) + thread id(2) = 15자리
		 * 205203 + 0012345 + 01 = 205203001234501
		 */
		String fullIP = "127.0.0.1";
/*		try {
			fullIP = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
		}
		String ips = String.format("%03d", Integer.parseInt(StringUtils.split(fullIP, ".")[2])) + 
				String.format("%03d", Integer.parseInt(StringUtils.split(fullIP, ".")[3]));
		String threadID = String.format("%02d", Thread.currentThread().getId());
		//String pid = String.format("%07d", StringUtils.split(ManagementFactory.getRuntimeMXBean().getName(), "@")[0]);
		//serverId = Long.parseLong(pid + ips + threadID);
*/	}
	public DocIDGenerator() {
	}
	private static String pid = "0";
	private static String ips = "0";
	public static void setServerId(String iips){
		pid = String.format("%07d", 
				Integer.parseInt(StringUtils.split(ManagementFactory.getRuntimeMXBean().getName(), "@")[0]));
		//serverId = Long.parseLong(ips + threadID + pid ) % 1023;
		ips=iips;
		serverId = Integer.parseInt(ips) + Integer.parseInt(pid); 
	}
	public void setPauseMs(int pause) { 
		this.pauseMs = pause; 
	} 

	public static long getId(int svrId) throws Exception { 
		//long now = System.nanoTime(); 
		long now = System.currentTimeMillis() ;
		long seq;
		synchronized(mutex) { 
			maybePause();
			serverId = svrId;
			if(serverId < 0 || serverId > serverIdUpperBound) {
				System.out.println("serverIdUpperBound OVER~~~~~");
				throw new Exception(); 
			}
				
			if (now < lastTimestamp) { 
				throw new Exception(); 
			} else if (now > lastTimestamp) { 
				sequenceNum = 0; 
			} else { 
				if (sequenceNum < DocIDGenerator.sequenceUpperBound) { 
					sequenceNum++; 
				} else {
					System.out.println("sequenceUpperBound OVER~~~~~");
					throw new Exception(); 
				} 
			} 
			seq = sequenceNum; 
			lastTimestamp = now; 
		}
/*		System.out.println("-------------------------------------------------------------------------");
		//System.out.println(String.format("%11s","NOW : ") + String.format("%20s", now) + ", SHIFT : " +  String.format("%20s",(now << timestampLShift)));
		System.out.println("IPS : "+ ips);
		System.out.println("PID : " + pid);
		System.out.println(String.format("%11s","NOW : ") + String.format("%20s", now) + ", SHIFT : " + (now << timestampLShift));
		System.out.println(String.format("%11s","SERVERID : ") + String.format("%20s",serverId ) + ", SHIFT : " + (serverId << serverLShift));
		System.out.println(String.format("%11s","SEQ : ") + String.format("%20s",seq) + ", SHIFT : " + (seq << sequenceLShift));*/
		//return (now << timestampLShift) | (serverId << machineLShift) | (seq << sequenceLShift);
		return now << timestampLShift | serverId << serverLShift | seq << sequenceLShift;
	} 

	private static void maybePause() { 
		if (pauseMs > 0) { 
			try { 
				Thread.sleep(pauseMs); 
			} catch (InterruptedException ie) { 
			} 
		} 
	} 
}