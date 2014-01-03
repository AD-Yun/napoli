/**
 * @author n2365
 */
package search.crawl.napoli.urlfeeder;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import search.crawl.milano.db.MysqlHandler;
import search.crawl.milano.db.MysqlPool;
import search.crawl.napoli.crawler.OpenKnowListCrawler;

public class FeedServer {
	
//	static int MaxSerialNumber = 256;
	static String hostname = "10.30.205.59";
	static MysqlPool mp = new MysqlPool(hostname);
	static int TotalCountReceive = 0;
	
	static LinkedList<Object> SerialId = new LinkedList<Object>();
//	static Map<String, List<String>> UpdateId = new HashMap<String, List<String>>();	
	private final int port;

	public FeedServer(int port) {
		this.port = port;
	}

	public void run(String caseFeeder) {
		// Configure the server.
		ServerBootstrap bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new FeedServerPipelineFactory(caseFeeder));

		// Bind and start to accept incoming connections.
		bootstrap.bind(new InetSocketAddress(port));
	}

//	public static void InitializeSerial(int MaxSerialNumber) {
//		for (int i =0; i < MaxSerialNumber; i++) {
//			Map<String, Object> defaultMap = new HashMap<String, Object>();
//			defaultMap.put("id", i);
//			List<String> urllist = new ArrayList<String>();
//			defaultMap.put("target", urllist);
//			defaultMap.put("paging", 0);
//			defaultMap.put("template", null);
//			SerialId.add(defaultMap);
//		}
//	}

	public static void main(String[] args) {
		
//		InitializeSerial(MaxSerialNumber);
		
		int port;
		String caseFeeder;
		if (args.length > 0) {
			caseFeeder = args[0];
			if (caseFeeder.contentEquals("List")) {
				port = 8082;
			} else if (caseFeeder.contentEquals("Body")) {
				port = 8081;
			} else if (caseFeeder.contentEquals("Docid")) {
				port = 8088;
			} else {
				port = 0;
				System.out
						.println("Usage: FeedServer run List|Body|Docid None|DefualtPort");
				System.exit(1);
			}
			if (args.length > 1) {
				port = Integer.parseInt(args[1]);
			}
			new FeedServer(port).run(caseFeeder);
		} else {
			System.out
					.println("Usage: FeedServer run List|Body|Docid None|DefualtPort");
			System.exit(1);
		}
	}
}