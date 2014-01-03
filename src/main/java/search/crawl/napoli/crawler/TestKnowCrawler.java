package search.crawl.napoli.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.ClientProtocolException;

import search.crawl.napoli.common.HtmlInfo;
import search.crawl.napoli.common.OpenKnowBodyConfig;
import search.crawl.napoli.common.OpenKnowListConfig;
import search.crawl.napoli.common.VeniceException;
import search.crawl.napoli.filter.Filter;
import search.crawl.napoli.filter.FilterUtil;
import search.crawl.napoli.filter.FilteredData;
import search.crawl.napoli.util.GrabHtml;
import search.crawl.napoli.util.OpenKnowCommons;

public class TestKnowCrawler extends Crawler {
	List<String> crawlListUrl;
	
	public TestKnowCrawler() {
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
		if (!OpenKnowBodyConfig.getInstance().read()) {
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
		Init();
		
	//	crawlListUrl.add("http://k.daum.net/qna/openknowledge/list.html?category_id=HA");
		crawlListUrl.add("http://k.daum.net/qna/view.html?qid=5EwLk");
		for(String listUrl : crawlListUrl) {
			System.out.println(listUrl);
			String filterName = "daum_computer-end";
	/*			
			if(!checkOverHit()) {
				continue;
			}*/
			Filter filter = null;
			HtmlInfo htmlInfo = null;
			FilteredData filterResult = null;
			try {
				filter = FilterUtil.getFilter(filterName);
				System.out.println(filterName);
				try {
					htmlInfo = GrabHtml.create(listUrl).crawl();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				filterResult = filter.filtering(htmlInfo);
	
			} catch (VeniceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(filterResult.getFilterState() == 1) {  
				Map<String, String> map = filterResult.getCollectionData() ;
				Set<String> set = map.keySet();
				//get textonly.
				System.out.println("********************************************************************************");
				for(String key : set) {
					System.out.println(key + " : "  + map.get(key));
				}
				System.out.println("********************************************************************************");
			} else { 
				System.out.println("Filter fail");
			}
		}
		System.exit(0);
	}

	@Override
	public boolean checkOverHit() {
		// TODO Auto-generated method stub
		return false;
	}
}
