package search.crawl.napoli.common;

import java.util.ArrayList;
import java.util.List;

public class ListCrawlDataInfo {
	private List<String> urls;
	private String filterName;
	private int pagingInfo;
	private int sleepInfo;
	private int status;
	private int duplCount;
	private String batchInfo;
	
	public ListCrawlDataInfo() {
		filterName = "";
		pagingInfo = 0;
		sleepInfo = 0;
		status = 0;
		duplCount = 0;
		batchInfo = "";
		urls = new ArrayList<String> ();
	}
	
	public void Init() {
		filterName = "";
		pagingInfo = 0;
		sleepInfo = 0;
		status = 0;
		duplCount = 0;
		batchInfo = "";
		urls.clear();
	}
	
	public List<String> getUrls() {
		return this.urls;
	}
	
	public String getFilterName() {
		return this.filterName;
	}
	
	public int getStatus() {
		return this.status;
	}
	
	public int getPagingInfo() {
		return this.pagingInfo;
	}
	
	public int getSleepInfo() {
		return this.sleepInfo;
	}
	
	public	int getDuplCount() {
		return this.duplCount;
	}
	
	public String getBatchInfo() {
		return this.batchInfo;
	}
	
	public void setUrl(List<String> urls) {
		this.urls = urls;
	}
	
	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public void setPaginInfo(int pageInfo) {
		this.pagingInfo = pageInfo;
	}
	
	public void setSleepInfo(int sleepInfo) {
		this.sleepInfo = sleepInfo;
	}
	
	public void setBatchInfo(String batchInfo) {
		this.batchInfo = batchInfo;
	}
	
	public void increaseDupCount() {
		this.duplCount++;
	}
}
