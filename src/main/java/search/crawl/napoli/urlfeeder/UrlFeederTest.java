/**
 * @author n2365
 */
package search.crawl.napoli.urlfeeder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import org.jboss.netty.handler.codec.http.HttpRequest;

import search.crawl.milano.db.MysqlHandler;
import search.crawl.milano.db.MysqlPool;

public class UrlFeederTest {

	private static String host;
	private static String temp;
	private static int port;
	private static HttpRequest request;
	

	public static void main(String[] args) throws Throwable {
		
		URL url = new URL("http://127.0.0.1:8080?doneCrawlId=33");
		HttpURLConnection conTest = (HttpURLConnection) url.openConnection();
		//System.out.println(conTest.getContent());
				
//		ByteArrayInputStream byteIn = new ByteArrayInputStream((ByteArrayOutputStream)conTest.getContent());
		ObjectInputStream in = new ObjectInputStream((InputStream) conTest.getContent());
		Map<Integer, String> data2 = (Map<Integer, String>) in.readObject();
		//System.out.println(data2.toString());
		
		System.exit(1);

		
		/**
		 * System.out.println("==done==="); for(int i=1;i<=10; i++) { String
		 * crawlerID = String.format("%09d", i); System.out.println("======== "
		 * + crawlerID + " start ========");
		 * System.out.println(UrlFeeder.getCrawlURL(crawlerID)); }
		 * 
		 * for(int i=1;i<=10; i++) { String crawlerID = String.format("%09d",
		 * i); System.out.println("======== " + crawlerID + " start ========");
		 * System.out.println(UrlFeeder.getCrawlURL(crawlerID)); }
		 **/

	}
}
