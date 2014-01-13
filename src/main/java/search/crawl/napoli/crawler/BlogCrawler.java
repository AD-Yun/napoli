package search.crawl.napoli.crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import search.crawl.napoli.common.BodyCrawlDataInfo;
import search.crawl.napoli.common.HtmlInfo;
import search.crawl.napoli.common.OpenKnowBodyConfig;
import search.crawl.napoli.common.OpenKnowListConfig;
import search.crawl.napoli.filter.Filter;
import search.crawl.napoli.filter.FilterUtil;
import search.crawl.napoli.filter.FilteredData;
import search.crawl.napoli.util.ATPCommons;
import search.crawl.napoli.util.Commons;
import search.crawl.napoli.util.GrabHtml;

public class BlogCrawler extends BodyCrawler {
	
	BlogCrawler() {
		
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
	
	public void work()  {
//		if(!getCrawlDataFromUrlFeeder()) {
//			System.out.println("get crawl failed");
//		}
//		
//		for(BodyCrawlDataInfo ci : crawlDataList) {
//			URL url = null;
//			try {
//				url = new URL(ci.getUrl());
//			} catch (MalformedURLException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			System.out.println("URL : " + url.toString());
//			String filterName = ci.getFilterName();
//			filterName = "naver";
//			
//			if(!checkOverHit()) {
//				continue;
//			}
//			
//			try {
//				//VeniceCollectionData setting.
//				ArrayList<String> arrPk = new ArrayList<String>(Arrays.asList("hostname", "pathname"));
//				VeniceCollectionData cd = new VeniceCollectionData();
//				cd.setPK(arrPk);
//				cd.resetValue();
//				
//				Filter filter = FilterUtil.getFilter(filterName);
//				HtmlInfo htmlInfo = GrabHtml.create(url.toString()).crawl();
//				
//				FilteredData filterResult = filter.filtering(htmlInfo);			
//				
//				//filter type.
//				String type = filterResult.getFilterType();
//				cd = (VeniceCollectionData) filterResult.getCollectionData();
//				setFieldData(cd, url);
//				System.out.println(cd.toString());
//				// TODO
//				//insertHtml2BDB(htmlInfo.getHtmlSource());
//				//insert2DB(htmlInfo.getHtmlSource());
//				noticeCrawlingResult2UrlFeeder();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	}
}
