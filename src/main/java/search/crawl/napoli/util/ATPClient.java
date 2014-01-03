package search.crawl.napoli.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import search.crawl.napoli.crawler.OpenKnowBodyCrawler;




public class ATPClient {
	static final Logger LOGGER = LoggerFactory.getLogger(ATPClient.class);
	/**
	 * ATP 버전
	 */
	public static final String SIGNATURE = "ATP/1.0";
	/**
	 * 줄 구분자(Line Terminator)
	 */
	public static final String LT = "\n";
	/**
	 * 필드 구분자(Field Terminator)
	 */
	public static final String FT = "\t";
	/**
	 * CONTINUE RESPONSE CODE
	 */
	public static final int CONTINUE_RESPONSE_CODE = 100;
	/**
	 * 데몬 통신 서비스 이름
	 */
	private String applName;
	/**
	 * API 코드 번호(3자리 10진수)
	 */
	private int funcCode;
	/**
	 * 데몬 통신에 사용할 소켓 객체
	 */
	private Socket socket;
	/**
	 * 응답 스트림 객체
	 */
	private DataInputStream response;
	/**
	 * 요청 스트림 객체
	 */
	private DataOutputStream request;
	/**
	 * 원격 IP 주소
	 */
	private String ip;
	/**
	 * 통신에 사용될 문자셋 이름
	 */
	private String charsetName;
	/**
	 * 원격 포트 번호
	 */
	private int port;
	/**
	 * 인스턴스 당 재초기화 최대 횟수
	 */
	private int MAX_RETRY_INIT = 20;
	/**
	 * 인스턴스 당 재요청 최대 횟수 (약 1/200,000 retry 발생) 
	 */
	private int MAX_RETRY_REQUEST = 100;
	/**
	 * 소켓 타임아웃 시간(밀리 초)
	 */
	public static final int SO_TIMEOUT = 3000;
	
	public boolean isBinaryPacket;
	public byte[] binaryPacketData;
	

	/**
	 * MS949 문자셋으로 입력 스트림을 해석하는 ATP 통신을 위한 클라이언트 객체 생성자
	 * @param appName 데몬 통신 서비스 이름
	 * @param ip 데몬 IP 주소
	 * @param port 데몬 포트 번호
	 */
	public ATPClient(String applName, String ip, int port) throws UnknownHostException, IOException {
		this.applName = applName;
		this.ip = ip;
		this.port = port;
		this.charsetName = "US-ASCII";
		this.init();
	}
	/**
	 * ATP 통신을 위한 클라이언트 객체 생성자
	 * @param appName 데몬 통신 서비스 이름
	 * @param ip 데몬 IP 주소
	 * @param port 데몬 포트 번호
	 * @param charsetName 입력 스트림을 해석할 문자셋 이름
	 */
	public ATPClient(String applName, String ip, int port, String charsetName)
	        throws UnknownHostException, IOException {
		this.applName = applName;
		this.ip = ip;
		this.port = port;
		this.charsetName = charsetName;
		this.init();
	}
	/**
	 * ATP 통신을 위한 소켓 초기화
	 */
	private void init() {
		try {
			SocketAddress socketAddress = new InetSocketAddress(this.ip, this.port);
			this.socket = new Socket();
			this.socket.connect(socketAddress, SO_TIMEOUT);
			this.socket.setSoTimeout(SO_TIMEOUT);
			this.response = new DataInputStream(this.socket.getInputStream());
			this.request = new DataOutputStream(this.socket.getOutputStream());
		} catch(UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
			System.exit(1);
		} catch(IOException e) {
			if(0 < MAX_RETRY_INIT--) {
				LOGGER.error("ATPClient attempts to retry connecting.. (" + MAX_RETRY_INIT + ") ip = " + this.ip + "port" + this.port);
				this.close();
				Commons.sleep(1000);
				this.init();
			} else {
				LOGGER.error("ATPClient failed to initalize!");
				System.exit(3);
			}
		}
	}
	/**
	 * API 기능 코드를 설정한다. API 명세서를 참조한다. 일반적으로 3자리 정수이다.
	 * @param funcCode API 기능 코드
	 */
	public void setRequestAPI(int funcCode) {
		this.funcCode = funcCode;
	}
	/**
	 * 연결된 응답 버퍼링 읽기 객체를 반환한다.
	 * @return 응답 버퍼링 읽기 객체
	 */
	public DataInputStream getReader() {
		return this.response;
	}
	/**
	 * 인자 배열을 사용하여 ATP 프로토콜 요청을 한다.
	 * @param args 인자 문자열 배열
	 * @return 응답 코드
	 */
	public void setBinary(byte[] orgString) throws Exception {
		this.isBinaryPacket = true;
		this.binaryPacketData = orgString;
	}
	
	public int request(String[] args) throws Exception {
		StringBuffer buffer = new StringBuffer();
		// request line
		buffer.append(SIGNATURE + " " + this.applName + " " + this.funcCode + LT);
		// no header
		buffer.append(LT);
		// Common Fix
		//buffer.append(0 + FT + 0 + FT + 1 + FT + 1 + FT + LT);
		// arguments
		for(String arg : args) {
			buffer.append(arg);
			if(!arg.endsWith(FT)) buffer.append(FT);
			buffer.append(LT);
		}
		// end of arguments
		buffer.append(LT);
		// no binary
		if (this.isBinaryPacket) {
			buffer.append(this.binaryPacketData.length + LT);
		} else {
			buffer.append(0 + LT);
		}

		int responseCode = 0;
		int retry = 3;
		int rereadline = 5;
		// 100 CONTINUE(NORMAL RESPONSE)
		try {
			while(0 < retry-- && (0 == responseCode || 100 != responseCode)) {
				int sendLength = buffer.length();
				if (this.isBinaryPacket) {
					sendLength += this.binaryPacketData.length;
				}
				
				String response = "";
				ByteBuffer tempPacket = ByteBuffer.allocate(sendLength);
				tempPacket.put(buffer.toString().getBytes("US-ASCII"));
				
				
				if(this.isBinaryPacket) {
					tempPacket.put(this.binaryPacketData);
				}
				
				byte[] sendPacket = new byte[sendLength];				
				sendPacket = tempPacket.array();
				
				this.request.write(sendPacket);
				
				do {
					response = this.response.readLine();
					if(null == response) throw new IOException("response is NULL!");
					rereadline--;
					if(0 > rereadline) return -1;
				} while(!response.startsWith(SIGNATURE));
				try {
					responseCode = Integer.parseInt(response.split(" ")[1]);
				} catch(NumberFormatException e) {
					throw new IOException("Invalid response code: " + response);
				}
			}
			// no header
			this.response.readLine();
		} catch(IOException e) {
			if(0 < MAX_RETRY_REQUEST--) {
				LOGGER.error("ATPClient IOException: " + e.getLocalizedMessage());
				LOGGER.error("ATPClient attemps to reset the connection... (" + MAX_RETRY_REQUEST + ")");
				this.close();
				Commons.sleep(1000);
				this.init();
				return this.request(args);
			} else {
				LOGGER.error("ATPClient exceeds MAX_RETRY_REQUEST!");
				e.printStackTrace();
				System.exit(5);
			}
		}
		if(responseCode != CONTINUE_RESPONSE_CODE)
			if(0 < MAX_RETRY_REQUEST--) {
				LOGGER.error("ATPClient attemps to reset the connection.... (" + MAX_RETRY_REQUEST + ")");
				this.close();
				Commons.sleep(1000);
				this.init();
				return this.request(args);
			} else {
				LOGGER.error("ATPClient exceeds MAX_RETRY_REQUEST!");
				System.exit(5);
			}
		return responseCode;
	}
	/**
	 * 인자 문자열을 사용하여 ATP 프로토콜 요청을 한다. 기본 구분자는 '|' 이다.
	 * @param args '|'를 구분자로 가지는 인자 문자열
	 * @return 응답 코드
	 */
	public int request(String args) throws Exception {
		return this.request(args, "\\|");
	}
	/**
	 * 인자 문자열을 사용하여 ATP 프로토콜 요청을 한다.
	 * @param args 구분자를 가지는 인자 문자열
	 * @param delimiter 구분자
	 * @return 응답 코드
	 */
	public int request(String args, String delimiter) throws Exception {
		return this.request(args.split(delimiter));
	}
	/**
	 * ATP 통신 연결을 종료한다. I/O 스트림을 닫은 뒤 소켓 연결을 끊는다.
	 */
	public void close() {
		try {
			if(null != this.request) this.request.close();
			if(null != this.response) this.response.close();
			if(null != this.socket) this.socket.close();
		} catch(Exception e) {
			LOGGER.error("ATPClient failed to close!");
			e.printStackTrace();
			System.exit(1);
		}
	}
	/**
	 * 사용 예제
	 */
	public static void main(String[] args) throws UnknownHostException, Exception {
		
		//////////////////////////형태소분석//////////////////////////
		ATPClient client = new ATPClient("KCQS", "127.0.0.1", 5400);
		client.setRequestAPI(100);
		DataInputStream in = client.getReader();

		String arguments = "";
		//client.setBinary("동해물과 백두산이");
		int responseCode = client.request(arguments);
		LOGGER.error("RESPONSE CODE: " + responseCode);

		String line = null;
		line = in.readLine();
		line = in.readLine();
		int size = Integer.parseInt(line);
		byte[] bytes = new byte[size];
		in.read(bytes, 0, size);
		
		String str1  = Commons.bytes2HexString(bytes);
		LOGGER.error(Commons.hexString2String(str1, "euc-kr"));
		//////////////////////////형태소분석//////////////////////////
		
		
		//////////////////////////html bdb////////////////////////////
		
		try {
			byte ReadByteArr[] = ATPCommons.ReadBDB("pdssd", "3", "10.30.205.92", 5400);
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		CloseableHttpClient httpClient = HttpClients.custom().build();
		try {
			HttpGet httpGet = new HttpGet("http://www.fntoday.co.kr/news/articleView.html?idxno=113650");
			CloseableHttpResponse response = httpClient.execute(httpGet);
			
			try {
				HttpEntity entity = response.getEntity();
				byte[] htmlBytes = EntityUtils.toByteArray(entity);
				
				ATPCommons.writeBDB("pdssd", "3", htmlBytes, "10.30.205.92", 5400);
				
			} finally {
				response.close();
			}
		} finally {
			httpClient.close();
		}
		//////////////////////////// html bdb////////////////////////////
		
		
	}
}
