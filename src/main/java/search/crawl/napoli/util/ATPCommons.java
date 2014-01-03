package search.crawl.napoli.util;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;

public final class ATPCommons {
	private ATPCommons() {
	}
	public static String getCookedText(String ip, int port, String query) throws Exception {
		
		ATPClient client = new ATPClient("KCQS", ip, port);
		client.setRequestAPI(100);
		DataInputStream in = client.getReader();

		String arguments = "0|0|1|1|";
		client.setBinary(query.getBytes("euc-kr"));
		int responseCode = client.request(arguments);
		
		String line = null;
		line = in.readLine();
		line = in.readLine();
		int size = Integer.parseInt(line);
		byte[] bytes = new byte[size];
		in.read(bytes, 0, size);
		
		String str1  = Commons.bytes2HexString(bytes);
		client.close();

		
		return Commons.hexString2String(str1, "euc-kr"); 
	}
	
	public static void writeBDB(String serviceName, String key, byte[] contents, String ip, int port)  {
		ATPClient client = null;
		try {
			client = new ATPClient(serviceName, ip, port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		client.setRequestAPI(201);
		DataInputStream in = client.getReader();

		String arguments = "0|0|1|1|" + key + "|1|A|";
		try {
			client.setBinary(contents);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			int responseCode = client.request(arguments);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String line = null;
		
		try {
			line = in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			line = in.readLine();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			line = in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		client.close();
	}
	
	public static void writeBDB(String serviceName, String key, Long stripe, byte[] contents, String ip, int port) throws Exception {
		ATPClient client = new ATPClient(serviceName, ip, port);
		client.setRequestAPI(201);
		DataInputStream in = client.getReader();

		String arguments = "0|0|1|1|" + key + "|1|A|" + Long.toString(stripe);
		client.setBinary(contents);
		int responseCode = client.request(arguments);
		
		String line = null;
		
		line = in.readLine();
		line = in.readLine();
		line = in.readLine();
		
		client.close();
	}
	
	public static void writeThumbBDB(String serviceName, String key, byte[] contents, String ip, int port) throws Exception {
		ATPClient client = new ATPClient(serviceName, ip, port);
		client.setRequestAPI(201);
		DataInputStream in = client.getReader();

		String arguments = key;
		client.setBinary(contents);
		int responseCode = client.request(arguments);
		
		String line = null;
		line = in.readLine();
		//System.out.println(line);
		line = in.readLine();
		//System.out.println(line);
		line = in.readLine();
		//System.out.println(line);
		
		client.close();
	}
	
	public static byte[] ReadBDB(String serviceName, String key, String ip, int port) throws Exception {
		ATPClient client = new ATPClient(serviceName, ip, port);
		client.setRequestAPI(206);
		DataInputStream in = client.getReader();

		String arguments = "0|0|1|1|" + key + "|A|1|";
		int responseCode = client.request(arguments);
		
		String line = null;
		
		line = in.readLine(); // return code
		line = in.readLine(); // serverid
		line = in.readLine(); // delemiter
		line = in.readLine(); // packet byte size
		
		int size = Integer.parseInt(line);
		byte[] bytes = new byte[size];
		in.readFully(bytes);
		
		FileOutputStream fos = new FileOutputStream("/home/n3732/venice/test.html");
		fos.write(bytes);
		
		fos.close();
		
		return bytes;
	}
	
	public static byte[] ReadThumbBDB(String serviceName, String key, String ip, int port) throws Exception {
		ATPClient client = new ATPClient(serviceName, ip, port);
		client.setRequestAPI(202);
		DataInputStream in = client.getReader();

		String arguments = key;
		int responseCode = client.request(arguments);
		
		String line = null;
		
		line = in.readLine(); // return code
		line = in.readLine(); // serverid
		line = in.readLine(); // delemiter
		line = in.readLine(); // packet byte size
		
		int size = Integer.parseInt(line);
		System.out.println("Size = " + size);
		byte[] bytes = new byte[size];
		in.readFully(bytes);
		
		FileOutputStream fos = new FileOutputStream("/home/n3732/venice/test.jpg");
		fos.write(bytes);
		
		fos.close();
		
		return bytes;
	}
}

