package search.crawl.napoli.crawler;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import search.crawl.napoli.common.ListCrawlDataInfo;

public abstract class ListCrawler extends Crawler {
	protected List<ListCrawlDataInfo> listCrawlDataList;
	
	ListCrawler() {
		listCrawlDataList = new ArrayList<ListCrawlDataInfo>();
	}
	
	
	public abstract boolean getCrawlDataFromUrlFeeder() throws Exception ;
	
	public boolean checkOverHit() {
		return true;
	}

	
	public abstract void work() ;
}
