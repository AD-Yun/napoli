package search.crawl.napoli.crawler;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import search.crawl.napoli.common.HtmlInfo;
import search.crawl.napoli.common.OpenKnowListConfig;
import search.crawl.napoli.common.VeniceException;
import search.crawl.napoli.common.VeniceEnums.Errors;
import search.crawl.napoli.filter.FilterUtil;
import search.crawl.napoli.filter.FilteredData;
import search.crawl.napoli.filter.ListFilter;
import search.crawl.napoli.util.GrabHtml;

public class TestKnowListCrawler extends ListCrawler {
	List<String> crawlListUrl;
	
	public TestKnowListCrawler() {
		crawlListUrl = new ArrayList<String>();
	}
	
	public void Init() {
		crawlListUrl.clear();
	}
	
	public boolean dbInit() {
		return true;
	}
	
	public void noticeCrawlingResult2UrlFeeder() throws Exception {
		return;
	}
	
	public boolean readConfig() {
		if (!OpenKnowListConfig.getInstance().read()) {
			return false;
		}
		
		return true;
	}
	
	public boolean getCrawlDataFromUrlFeeder() throws Exception {
		// TODO
		// connect and request
		// receive
		// set crawalDatalist
		String urlQuery = "http://" + OpenKnowListConfig.getInstance().getUrlFeederIP() + ":" + OpenKnowListConfig.getInstance().getUrlFeederPort();
		System.out.println(urlQuery);
		URL url = new URL(urlQuery);
		HttpURLConnection conTest = (HttpURLConnection) url.openConnection();

		ObjectInputStream in = new ObjectInputStream((InputStream) conTest.getContent());
		Map<String, Object> data2 = (Map<String, Object>) in.readObject();
		
		int robotID = (int)data2.get("id");
		List<String> urls = (List<String>)data2.get("target");
		
		for(String listUrl : urls) {
			crawlListUrl.add(listUrl);
			System.out.println(listUrl);
		}
		
		
		return true;
	}
	
	public void work() {
/*		if(!getCrawlDataFromUrlFeeder()) {
			System.out.println("get crawl failed");
		}
		*/
		//TODO : seed url.
//		crawlListUrl.add("http://k.daum.net/qna/openknowledge/list.html?category_id=HA");
		crawlListUrl.add("http://k.daum.net/qna/file/list.html?category_id=FAI");
		
		for(String listUrl : crawlListUrl) {
			//TODO : 필터명.
//			String filterName = "daum_kdic";
			String filterName = "daum_report_univ";
			System.out.println(listUrl);
			
			if(!checkOverHit()) {
				continue;
			}
			ListFilter filter = null;
			try {
				filter = FilterUtil.getListFilter(filterName);
			} catch (VeniceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			int cnt = 0;
			while(true) { 
				
				try {
					listUrl = filter.getNextListUrl(listUrl);
					if(cnt > 0) { 
						listUrl = "http://k.daum.net/qna/file/lisabcdt.html?mode=0&page=19&min=20061024164316&max=20090127163758&cp=30&category_id=FAI";
					}
					HtmlInfo htmlInfo = GrabHtml.create(listUrl).crawl();

					System.out.println("URL : " + listUrl);
					FilteredData filterResult = filter.filtering(htmlInfo);
					
					if(filterResult.getFilterState() == 1) {  
						List<Map<String, String>> lstData = filterResult.getLstCollectionData() ;
						
						for(Map<String, String> map : lstData) {
							Set<String> set = map.keySet();
							//get textonly.
							System.out.println("------------------------------------------------");
							for(String key : set) {
								System.out.println(key + ":"  + map.get(key));
							}
							
						}
					} else { 
						System.out.println("Filter fail");
						
					}
/*					if(cnt > 1) { 
					break;
					}*/
					cnt++;
						//insert2DB(htmlInfo.getHtmlSource());
					//noticeCrawlingResult2UrlFeeder();
				} catch (VeniceException ee) {
					if(ee.getError().equals(Errors.GET_NEXT_URL_FAILURE)) { 
						System.out.println("EQUALS ...........................................");
						System.out.println(ee.getError());
					} else { 
						System.out.println("NOT EQUALS ...........................................");
					}
					System.out.println(ee.getMessage());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
