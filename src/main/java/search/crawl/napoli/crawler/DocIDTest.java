package search.crawl.napoli.crawler;

import java.net.URL;

import search.crawl.napoli.common.HtmlInfo;
import search.crawl.napoli.common.OpenKnowBodyConfig;
import search.crawl.napoli.common.OpenKnowListConfig;
import search.crawl.napoli.util.ATPCommons;
import search.crawl.napoli.util.Commons;
import search.crawl.napoli.util.DocIDGenerator;
import search.crawl.napoli.util.GrabHtml;
import search.crawl.napoli.util.HashFunction;
import search.crawl.napoli.util.HtmlParser;

public class DocIDTest extends Crawler {
	public DocIDTest() {
		//listCrawlDataList = new ArrayList<ListCrawlDataInfo>();

		//TODO : TEST 데이터 set.
//		CrawlDataInfo cdi = new CrawlDataInfo();
//		cdi.setFilterName("daum_computer_end");
//		cdi.setUrl("http://k.daum.net/qna/view.html?qid=5BMRm&l_cid=Q&l_st=1");
//		crawlDataList.add(cdi);
		System.out.println("body");
	}
	
	public boolean readConfig() {
		if (!OpenKnowListConfig.getInstance().read()) {
			return false;
		}
		
		return true;
	}
	
	public boolean dbInit() {
		return true;
	}
	
	/*private void setFieldData(VeniceCollectionData cd, URL url) throws Exception {
		//result.toString()
		cd.setValue("HOST", url.getHost());
		cd.setValue("Path", url.getPath());
		cd.setValue("Signature", Commons.getMD5Hash(cd.getValue("Title")));
		cd.setValue("CookedTitle", ATPCommons.getCookedText(OpenKnowBodyConfig.getInstance().getCookDeamonIP(), OpenKnowBodyConfig.getInstance().getCookDeamonPort(), cd.getValue("Title")));
	}*/
	
	public void work() {
		try { 
		//System.out.println("HASH DB : " + HashFunction.hash("1","MS949", 1));
		/*	
		System.out.println("---------------------------------------------------------------");
		System.out.println("HASH DB : " + HashFunction.hash("안녕하세요","MS949", 1));
		/*	System.out.println("HASH DB : " + HashFunction.hash("안녕하세요","MS949", 2));
		System.out.println("HASH DB : " + HashFunction.hash("안녕하세요","MS949", 3));
		System.out.println("HASH DB : " + HashFunction.hash("안녕하세요","MS949", 4));
		System.out.println("HASH DB : " + HashFunction.hash("안녕하세요","MS949", 5));
		System.out.println("HASH DB : " + HashFunction.hash("안녕하세요","MS949", 6));
		System.out.println("HASH DB : " + HashFunction.hash("안녕하세요","MS949", 7));
		System.out.println("HASH DB : " + HashFunction.hash("안녕하세요","MS949", 8));
		System.out.println("HASH DB : " + HashFunction.hash("안녕하세요","MS949", 9));
		System.out.println("HASH DB : " + HashFunction.hash("안녕하세요","MS949", 10));
		System.out.println("---------------------------------------------------------------");
	System.out.println("HASH DB : " + HashFunction.hash("1","MS949", 1));
		System.out.println("HASH DB : " + HashFunction.hash("2","MS949", 2));
		System.out.println("HASH DB : " + HashFunction.hash("3","MS949", 3));
		System.out.println("HASH DB : " + HashFunction.hash("4","MS949", 4));
		System.out.println("HASH DB : " + HashFunction.hash("5","MS949", 5));
		System.out.println("HASH DB : " + HashFunction.hash("6","MS949", 6));
		System.out.println("HASH DB : " + HashFunction.hash("7","MS949", 7));
		System.out.println("HASH DB : " + HashFunction.hash("8","MS949", 8));
		System.out.println("HASH DB : " + HashFunction.hash("9","MS949", 9));
		System.out.println("HASH DB : " + HashFunction.hash("10","MS949", 10));*/


		HtmlInfo hi = GrabHtml.create("http://k.daum.net/qna/view.html?qid=5BMRm").crawl();
		System.out.println("***********************************************************************************************");
		System.out.println(HtmlParser.getTextOnly(hi.getHtmlSource()));
		System.out.println("---------------------------------------------------------------");
		System.out.println("HASH DB : " + HashFunction.hash64("안녕하세요","MS949", 1));
				System.out.println("HASH DB : " + HashFunction.hash64("안녕하세요","MS949", 2));
		System.out.println("HASH DB : " + HashFunction.hash64("안녕하세요","MS949", 3));
		System.out.println("HASH DB : " + HashFunction.hash64("안녕하세요","MS949", 4));
		System.out.println("HASH DB : " + HashFunction.hash64("안녕하세요","MS949", 5));
		System.out.println("HASH DB : " + HashFunction.hash64("안녕하세요","MS949", 6));
		System.out.println("HASH DB : " + HashFunction.hash64("안녕하세요","MS949", 7));
		System.out.println("HASH DB : " + HashFunction.hash64("안녕하세요","MS949", 8));
		System.out.println("HASH DB : " + HashFunction.hash64("안녕하세요","MS949", 9));
		System.out.println("HASH DB : " + HashFunction.hash64("안녕하세요","MS949", 10));	
/*		System.out.println("---------------------------------------------------------------");
		System.out.println("HASH DB : " + HashFunction.hash64("1","MS949", 1));
		System.out.println("HASH DB : " + HashFunction.hash64("2","MS949", 2));
		System.out.println("HASH DB : " + HashFunction.hash64("3","MS949", 3));
		System.out.println("HASH DB : " + HashFunction.hash64("4","MS949", 4));
		System.out.println("HASH DB : " + HashFunction.hash64("5","MS949", 5));
		System.out.println("HASH DB : " + HashFunction.hash64("6","MS949", 6));
		System.out.println("HASH DB : " + HashFunction.hash64("7","MS949", 7));
		System.out.println("HASH DB : " + HashFunction.hash64("8","MS949", 8));
		System.out.println("HASH DB : " + HashFunction.hash64("9","MS949", 9));
		System.out.println("HASH DB : " + HashFunction.hash64("10","MS949", 10));*/
		
		/*
		System.out.println("------------------------------------------------------------------");
		System.out.println("HASH DB : " + HashFunction.hash("안녕하세요", 1));
		System.out.println("HASH DB : " + HashFunction.hash("안녕하세요", 2));
		System.out.println("HASH DB : " + HashFunction.hash("안녕하세요", 3));
		System.out.println("HASH DB : " + HashFunction.hash("안녕하세요", 4));
		System.out.println("HASH DB : " + HashFunction.hash("안녕하세요", 5));
		System.out.println("HASH DB : " + HashFunction.hash("안녕하세요", 6));
		System.out.println("HASH DB : " + HashFunction.hash("안녕하세요", 7));
		System.out.println("HASH DB : " + HashFunction.hash("안녕하세요", 8));
		System.out.println("HASH DB : " + HashFunction.hash("안녕하세요", 9));
		System.out.println("HASH DB : " + HashFunction.hash("안녕하세요",10));
		
		*/
		/*
		System.out.println("************************ 32bit ***********************************");
		System.out.println("HASH DB : " + HashFunction.hash32("안녕하세요","MS949", 1));
		System.out.println("HASH DB : " + HashFunction.hash32("안녕하세요","MS949", 2));
		System.out.println("HASH DB : " + HashFunction.hash32("안녕하세요","MS949", 3));
		System.out.println("HASH DB : " + HashFunction.hash32("안녕하세요","MS949", 4));
		System.out.println("HASH DB : " + HashFunction.hash32("안녕하세요","MS949", 5));
		System.out.println("HASH DB : " + HashFunction.hash32("안녕하세요","MS949", 6));
		System.out.println("HASH DB : " + HashFunction.hash32("안녕하세요","MS949", 7));
		System.out.println("HASH DB : " + HashFunction.hash32("안녕하세요","MS949", 8));
		System.out.println("HASH DB : " + HashFunction.hash32("안녕하세요","MS949", 9));
		System.out.println("HASH DB : " + HashFunction.hash32("안녕하세요","MS949", 10));
		
		
		System.out.println("------------------------------------------------------------------");
		System.out.println("HASH DB : " + HashFunction.hash32("안녕하세요", 1));
		System.out.println("HASH DB : " + HashFunction.hash32("안녕하세요", 2));
		System.out.println("HASH DB : " + HashFunction.hash32("안녕하세요", 3));
		System.out.println("HASH DB : " + HashFunction.hash32("안녕하세요", 4));
		System.out.println("HASH DB : " + HashFunction.hash32("안녕하세요", 5));
		System.out.println("HASH DB : " + HashFunction.hash32("안녕하세요", 6));
		System.out.println("HASH DB : " + HashFunction.hash32("안녕하세요", 7));
		System.out.println("HASH DB : " + HashFunction.hash32("안녕하세요", 8));
		System.out.println("HASH DB : " + HashFunction.hash32("안녕하세요", 9));
		System.out.println("HASH DB : " + HashFunction.hash32("안녕하세요",10));
		*/
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			
			int inx = 0 ;
			while (true){
				inx++;
				System.out.println("DOC ID : " + DocIDGenerator.getId(0));
				if (inx > 10) { 
					break;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean getCrawlDataFromUrlFeeder() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkOverHit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void noticeCrawlingResult2UrlFeeder() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
