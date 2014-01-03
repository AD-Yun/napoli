package search.crawl.napoli.common;

import java.util.ArrayList;
import java.util.List;

public class BodyCrawlDataInfo {
	private List<String> urls;
	String filterName;
	
	public BodyCrawlDataInfo() {
		urls = new ArrayList<String> ();
		filterName = "";
	}
	
	public void Init() {
		filterName = "";
		urls.clear();
	}
	
	public List<String> getUrls() {
		return this.urls;
	}
	
	public String getFilterName() {
		return this.filterName;
	}
	
	public void setUrl(List<String> urls) {
		this.urls = urls;
	}
	
	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}
}
