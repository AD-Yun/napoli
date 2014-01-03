package search.crawl.napoli;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import search.crawl.napoli.crawler.OpenKnowListCrawler;

final class OpenKnowListRobot {
	
	/* logger 생성 */
	static final Logger LOGGER = LoggerFactory.getLogger("OpenKnowListRobot");
	
	private OpenKnowListRobot() { 
		
	}
	
	public static void main(String[] args) throws Exception {
//		String url = args[0];
//		//test URL "http://blog.naver.com/PostView.nhn?blogId=hinfl23&logNo=40191682153" 
//		HtmlInfo htmlInfo = GrabHtml.create(url).crawl();
//		System.out.println("HTML SIZE ==>" + htmlInfo.getHtmlLength());
//		//HtmlInfo htmlInfo = GrabHtml.create("http://www.nate.com").crawl();
///*		if (htmlInfo != null) {
//			String str = HtmlParser.getTextOnly(htmlInfo.getHtmlSource());
//			System.out.println(str);
//			
//			Map<String, String> linkData = HtmlParser.extractLink(htmlInfo.getHtmlSource());
//			List<String> imageLinkData = HtmlParser.extractImageLink(htmlInfo.getHtmlSource());
//			
//			Set<String> set = linkData.keySet();
//			Iterator<String> iter = set.iterator();
//			
//			while(iter.hasNext()) {
//				String v_key= iter.next();
//				System.out.println(v_key + "===>" + linkData.get(v_key));
//	        }
//			
//			Iterator<String> imageIter = imageLinkData.iterator();
//			
//			while(imageIter.hasNext()) {
//				System.out.println(imageIter.next());
//	        }
//		}
//		*/
///*		htmlInfo = GrabHtml.create("https://itunes.apple.com/kr/rss/toppaidapplications/limit=25/genre=6013/xml").crawl();
//		System.out.println(htmlInfo.getHtmlSource());*/
//		/* log... 생성 */
//		LOGGER.trace("한글은 ABCDE");
//		LOGGER.debug("한글은 ABCDE");
//		LOGGER.info("한글은 ABCDE");
//		LOGGER.warn("한글은 ABCDE");
//		LOGGER.error("한글은 ABCDE");
////		
////		System.out.println("한글" + System.getProperty("file.encoding"));
////		//filter test.
////		Filter ff = FilterUtil.getFilter("kyobo", "list");
////		ff.filtering(htmlInfo);
////
////		Filter ff2 = FilterUtil.getFilter("kyobo");
////		ff2.filtering(htmlInfo);
//		
//		System.out.println("한글" + System.getProperty("file.encoding"));
//
//		Filter ff2 = FilterUtil.getFilter("naver");
//		ff2.filtering(htmlInfo);
//	
//		/*
//		Filter ff2 = FilterUtil.getFilter("naver", "list");
//		ff2.filtering(htmlInfo);
//		*/
///*		<filters>
//		<name>kyobo</name>
//		<classname>Kyobo</<classname>>
//		<regexp><![CDATA[
//			<div class="titleinfo">(.*)</a></h2>.*<span class="date">([^<]*)</span>(.*)
//		]]></regexp>
//		<field>title, body</field>
//	</filters>*/
//		
//	
//		System.out.println(ATPCommons.getCookedText("127.0.0.1", 5400, "동해물s과 백두산이"));
//		byte[] data = ImageController.crawlImage("http://main.nateimg.co.kr//img/cms/content_pool/2013/06/01(7).jpg");
//		ImageController.saveImage(data, "abc.jpg");
//		byte[] data2 = ImageController.getThumbnailData("http://main.nateimg.co.kr//img/cms/content_pool/2013/06/01(7).jpg", 130, 100);
//		ImageController.saveImage(data2, "thumb.jpg");
//		
//		Config.getInstance().read();
//		List<String> host = Config.getInstance().getConfig().getList("urlfeederinfo.ip");
//		System.out.println(Config.getInstance().toString());
//		System.out.println(Config.getInstance().toString());
//		System.out.println(Config.getInstance().toString());
//		System.out.println(Config.getInstance().toString());
//		
//		Iterator<String> h = host.iterator();
//		while(h.hasNext()) {
//			String hostName = h.next();
//			System.out.println(hostName);
//		}

		
//		RobotMain main = new RobotMain(new OpenKnowListCrawler());
//		if(!main.startRobot()) {
//			return;
//		}
//		
//		main.run();
	}
}

