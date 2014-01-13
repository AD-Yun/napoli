package search.crawl.napoli.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import search.crawl.milano.common.MilanoEnums;

import search.crawl.milano.db.MysqlHandler;
import search.crawl.milano.db.MysqlPool;
import search.crawl.napoli.common.HtmlInfo;
import search.crawl.napoli.common.ListCrawlDataInfo;
import search.crawl.napoli.common.OpenKnowListConfig;
import search.crawl.napoli.common.VeniceException;
import search.crawl.napoli.common.VeniceEnums.Errors;
import search.crawl.napoli.filter.FilterUtil;
import search.crawl.napoli.filter.FilteredData;
import search.crawl.napoli.filter.ListFilter;
import search.crawl.napoli.util.Commons;
import search.crawl.napoli.util.GrabHtml;

public class OpenKnowListCrawler extends ListCrawler {
	static final Logger LOGGER = LoggerFactory.getLogger(OpenKnowListCrawler.class);
	static final int stopCntOfFilteringErrorOnBatchCrawl = 5;
	
	private ListCrawlDataInfo targetDataInfo;
	static MysqlPool seedPool;
	static MysqlPool listPool;
	
	MysqlHandler seedHandler;
	MysqlHandler listHandler;
	
	public OpenKnowListCrawler() {
		this.crawlerID = "";
		this.targetDataInfo = new ListCrawlDataInfo();
	}
	
	public boolean readConfig() {
		if (!OpenKnowListConfig.getInstance().read()) {
			return false;
		}
		
		return true;
	}
	
	public boolean dbInit() {
		seedPool = new MysqlPool(OpenKnowListConfig.getInstance().getSeedDBHostName(), OpenKnowListConfig.getInstance().getSeedDBName());
		listPool = new MysqlPool(OpenKnowListConfig.getInstance().getListDBHostName(), OpenKnowListConfig.getInstance().getListDBName());
		
		return true;
	}
	
	public void initData() {
		this.targetDataInfo.Init();
		seedHandler.close();
		listHandler.close();
	}
	
	public void noticeCrawlingResult2UrlFeeder() throws Exception {
//		String urlQuery = "http://" + OpenKnowListConfig.getInstance().getUrlFeederIP() + ":" + OpenKnowListConfig.getInstance().getUrlFeederPort() + "?doneCrawlId=" + this.crawlerID;
//		URL url = new URL(urlQuery);
//		HttpURLConnection conTest = (HttpURLConnection) url.openConnection();
		
		return;
	}
	
	public boolean getCrawlDataFromUrlFeeder() {
		String urlQuery = "http://" + OpenKnowListConfig.getInstance().getUrlFeederIP() + ":" + OpenKnowListConfig.getInstance().getUrlFeederPort();
		URL url;
		try {
			url = new URL(urlQuery);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			LOGGER.error("URL object create failed url = " + urlQuery);
			LOGGER.error(e.getMessage());
			return false;
		}
		
		HttpURLConnection conTest;
		try {
			conTest = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Connect failed : " + e.getMessage());
			return false;
		}

		ObjectInputStream in;
		try {
			in = new ObjectInputStream((InputStream) conTest.getContent());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Get Content Failed : " + e.getMessage());
			return false;
		}
		
		Map<String, Object> recvData;
		try {
			recvData = (HashMap<String, Object>) in.readObject();
			if (recvData == null) {
				LOGGER.error("There is No RecvData");
				return false;
			}
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Received data read failed : " + e.getMessage());
			return false;
		}
		
		if (recvData.size() <= 0) {
			Commons.sleepSeconds(1);
			return true;
		} else {
			this.crawlerID = (String)recvData.get("crawlid");
			this.targetDataInfo.setUrl((List<String>)recvData.get("target"));
			this.targetDataInfo.setFilterName((String)recvData.get("templates"));
			this.targetDataInfo.setPaginInfo(Integer.parseInt((String)recvData.get("paging")));
			this.targetDataInfo.setSleepInfo(Integer.parseInt((String)recvData.get("sleepinfo")));
		}
		
		return true;
	}
	
	public boolean insert2DB(Map<String, String> map) {
		String firstInsertCrawlState = "1010";
		String firstInsertRunningStep = "000";
		
		String recentsql = "insert into KNOW_LIST_RECENT(Host,Path,SiteID,SectionID,crawlstate,runningstep,Flag,bodyfilter) " + "values('" + map.get("host") + "','" + map.get("path") + "'," + Integer.parseInt(map.get("siteid")) + ",'" + map.get("sectionid") + "','" + firstInsertCrawlState + "','" + firstInsertRunningStep + "','','" + map.get("bodyfilter") + "')";
		MilanoEnums.DBReturnValues recentReturnVal = listHandler.insertData(recentsql);
		
		if (recentReturnVal == MilanoEnums.DBReturnValues.SUCCESS) {
			return true;
		} else if(recentReturnVal == MilanoEnums.DBReturnValues.ER_DUP_ENTRY) {
			this.targetDataInfo.increaseDupCount();
			return true;
		} else {
			LOGGER.error("Update Error(" + recentReturnVal + ") : ", recentReturnVal.getMessage() + "\n" + "Query = " + recentsql);
			return false;
		}
	}
	
	public boolean updateSeedDB(String path, String crawlState ) {
		String updateSql = "update KNOW_SEEDURL set RunningStep = '200', crawlstate='" + crawlState + "', LastModidate = now() where Path = '" + path + "'";
		MilanoEnums.DBReturnValues returnVal = seedHandler.updateData(updateSql);
		
		if (returnVal == MilanoEnums.DBReturnValues.SUCCESS ) {
			return true;
		} else {
			LOGGER.error("Update Error(" + returnVal + ") : ", returnVal.getMessage() + "\n" + "Query = " + updateSql);
			return false;
		}
	}
	
	public void work() {
		
		this.seedHandler = seedPool.getHandler();
		this.listHandler = listPool.getHandler();
		
		if(!getCrawlDataFromUrlFeeder()) {
			LOGGER.error("get crawl failed");
			this.initData();
			return;
		}
		
		if (this.targetDataInfo.getUrls().size() <= 0) {
			LOGGER.info("No data");
			this.initData();
			return;
		}
		// 리스트 크롤러는 받는url이 하나다.
		String listUrl = this.targetDataInfo.getUrls().get(0);
		URL temp;
		try {
			temp = new URL("http://" + listUrl);
		} catch (MalformedURLException e) {
			this.initData();
			// TODO Auto-generated catch block
			LOGGER.error("Url object create faild : " + e.getMessage());
			return;
		}
		
		String path = (temp.getPath() + "?" + temp.getQuery());
		
		String filterName = this.targetDataInfo.getFilterName();
		int sleepInfo = this.targetDataInfo.getSleepInfo();
		int pagingInfo = this.targetDataInfo.getPagingInfo();
		ListFilter filter = null;
		
		try {
			filter = FilterUtil.getListFilter(filterName);
		} catch (VeniceException e1) {
			// TODO Auto-generated catch block
			LOGGER.error("Get filter failed : filtername = " + filterName);
			if (updateSeedDB(path, String.valueOf(e1.getError_code()))) {
				this.initData();
				return;
			} else {
				LOGGER.error("update error");
				this.initData();
				return;
			}
		}
		
		
		if(!checkOverHit()) {
			LOGGER.error("Over hit. can't crawl!!");
			this.initData();
			return;
		}
		
		int nCurPage = 0;
		int nCurFilteringFailCnt = 0;
		String crawlState = "";
		
		while (true) {
			nCurPage++;
			if (!listUrl.contains("http://")) {
				listUrl = "http://" + listUrl;
			}
			
			try {
				listUrl = filter.getNextListUrl(listUrl);
			} catch (VeniceException e1) {
				if(e1.getError().equals(Errors.GET_NEXT_URL_FAILURE)) {
					LOGGER.error("Nexturl get Failed : Before Url = " + listUrl);
					break;
				}
			}
			HtmlInfo htmlInfo = null;
			
			for(int i = 0; i < OpenKnowListConfig.getInstance().getCountOfCrawlTimeout(); ++i) {
				try {
					htmlInfo = GrabHtml.create(listUrl).crawl();
					htmlInfo.setUrl(listUrl);
					break;
				} catch (Exception e) {
					if ((e.getMessage() == "Read timed out") && (i < OpenKnowListConfig.getInstance().getCountOfCrawlTimeout() - 1)) {
						LOGGER.error("TIMTOUT!! count = " + i);
						continue;
					}
					
					LOGGER.error("Crawling failed(url = " + listUrl + ") : " + e.getMessage());
					break;
				}
			}
			
			if(htmlInfo == null) {
				crawlState = "1011";
				break;
			}
			
			FilteredData filterResult;
			
			try {
				filterResult = filter.filtering(htmlInfo);
				if(filterResult.getFilterState() == 1) {  
					List<Map<String, String>> lstData = filterResult.getLstCollectionData() ;
					
					for(Map<String, String> filterResultMap : lstData) {
						if (!insert2DB(filterResultMap)) {
							crawlState = "5111";
							LOGGER.error("InsertFail");
							break;
						}
					}
				} else {
					if (targetDataInfo.getBatchInfo() == "N") {
						LOGGER.error("Filter failed(" + filterName + ") : getFilterState = " + filterResult.getFilterState());
						crawlState = "5012";
						break;
					} else {
						nCurFilteringFailCnt++;
						if (nCurFilteringFailCnt >= stopCntOfFilteringErrorOnBatchCrawl) {
							LOGGER.error("On batch crawl, error occured 5 times over.(CurPage = " + nCurPage);
							LOGGER.error("Filter failed(" + filterName + ") : getFilterState = " + filterResult.getFilterState());
							crawlState = "5012";
							break;
						}
						
						continue;
					}
				}
			} catch (VeniceException e) {
				// TODO Auto-generated catch block
				LOGGER.error("Filtering failed(filtername = " + filterName + ", url = " + listUrl + "Current Page = " + nCurPage + " : " + e.getMessage());
				crawlState = String.valueOf(e.getError_code());
				break;
			}
			
			crawlState = "2011";
			
			if ((nCurPage == pagingInfo) && (targetDataInfo.getBatchInfo() == "N")) {
				LOGGER.info("Max page reached : Maxpage = " + pagingInfo);
				break;
			}
			
			if (this.targetDataInfo.getDuplCount() >= OpenKnowListConfig.getInstance().getCountOfCrawlSkipDuplicationForTemplate()) {
				LOGGER.info("skip by duplicate");
				break;
			}
			
			Commons.sleepSeconds(sleepInfo);
		}
		
		if (!updateSeedDB(path, crawlState)) {
			LOGGER.error("update error");
		}
		
		this.initData();
	}
}
