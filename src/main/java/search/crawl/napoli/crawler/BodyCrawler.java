package search.crawl.napoli.crawler;

import java.util.ArrayList;
import java.util.List;

import search.crawl.napoli.common.BodyCrawlDataInfo;

public abstract class BodyCrawler extends Crawler {
	protected List<BodyCrawlDataInfo> crawlDataList;
	
	BodyCrawler() {
		crawlDataList = new ArrayList<BodyCrawlDataInfo>();
	}
	
	public boolean getCrawlDataFromUrlFeeder() {
		// TODO
		// connect and request
		// receive
		// set crawalDatalist
		
		return true;
	}
	
	public boolean checkOverHit() {
		return true;
	}
	
	public void noticeCrawlingResult2UrlFeeder() {
		return;
	}
	
	public abstract void work() ;
}
