package search.crawl.napoli.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nate.search.milano.common.MilanoEnums;

import search.crawl.milano.db.MysqlHandler;
import search.crawl.milano.db.MysqlPool;
import search.crawl.napoli.common.BodyCrawlDataInfo;
import search.crawl.napoli.common.GetMulmeLinkResultSet;
import search.crawl.napoli.common.HtmlInfo;
import search.crawl.napoli.common.OpenKnowBodyConfig;
import search.crawl.napoli.common.OpenKnowDBData;
import search.crawl.napoli.common.VeniceException;
import search.crawl.napoli.filter.Filter;
import search.crawl.napoli.filter.FilterUtil;
import search.crawl.napoli.filter.FilteredData;
import search.crawl.napoli.util.ATPCommons;
import search.crawl.napoli.util.Commons;
import search.crawl.napoli.util.GrabHtml;
import search.crawl.napoli.util.HashFunction;
import search.crawl.napoli.util.OpenKnowCommons;

public class OpenKnowBodyCrawler extends BodyCrawler {
	static final Logger LOGGER = LoggerFactory.getLogger(OpenKnowBodyCrawler.class);
	
	private BodyCrawlDataInfo targetDataInfo;
	MysqlPool listRecPool;
	MysqlPool bodyPool;
	
	MysqlHandler listRecHandler;
	MysqlHandler bodyHandler;
	
	public OpenKnowBodyCrawler() {
		this.crawlerID = "";
		this.targetDataInfo = new BodyCrawlDataInfo();
	}
	
	public boolean readConfig() {
		if (!OpenKnowBodyConfig.getInstance().read()) {
			return false;
		}
		
		return true;
	}
	
	public boolean dbInit() {
		listRecPool = new MysqlPool(OpenKnowBodyConfig.getInstance().getListRecDBHostName(), OpenKnowBodyConfig.getInstance().getListRecDBName());
		bodyPool = new MysqlPool(OpenKnowBodyConfig.getInstance().getBodyDBHostName(), OpenKnowBodyConfig.getInstance().getBodyDBName());
		
		return true;
	}
	
	public void initData() {
		this.targetDataInfo.Init();
		listRecHandler.close();
		bodyHandler.close();
	}
	
	public Long getDocIDFromDocIDServer() {
		Long returnVal = 0L;
		String urlQuery = "http://" + OpenKnowBodyConfig.getInstance().getDocIDServerIP() + ":" + OpenKnowBodyConfig.getInstance().getDocIDServerPort() + "?siteid=3";
		URL url;
		try {
			url = new URL(urlQuery);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			LOGGER.error("URL object create failed url = " + urlQuery);
			LOGGER.error(e.getMessage());
			return returnVal;
		}
		
		HttpURLConnection conTest;
		try {
			conTest = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Connect failed : " + e.getMessage());
			return returnVal;
		}

		ObjectInputStream in;
		try {
			in = new ObjectInputStream((InputStream) conTest.getContent());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Get Content Failed : " + e.getMessage());
			return returnVal;
		}
		
		try {
			returnVal = (Long)in.readObject();
			in.close();
		} catch (ClassNotFoundException | IOException e1) {
			// TODO Auto-generated catch block
			LOGGER.error("Get DocID Failed : " + e1.getMessage());
		}
		
		return returnVal;
	}
	
	public boolean getCrawlDataFromUrlFeeder() {
		String urlQuery = "http://" + OpenKnowBodyConfig.getInstance().getUrlFeederIP() + ":" + OpenKnowBodyConfig.getInstance().getUrlFeederPort();
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
			in.close();
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
		}
		
		return true;
	}
	
	public String getQuesTitleSig(String quesTitle) {
		Character c = new Character((char)0x09);
		String delim = c.toString();
		String cookedText = "";
		try {
			cookedText = ATPCommons.getCookedText(OpenKnowBodyConfig.getInstance().getCookDeamonIP(), OpenKnowBodyConfig.getInstance().getCookDeamonPort(), quesTitle);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error("Get cookedText failed");
			LOGGER.error(e.getMessage());
		}
		
		String[] arrCookedString = cookedText.split(" ");
		Arrays.sort(arrCookedString);
		
		String sortedCooked = "";
		
		for(int i = 0; i < arrCookedString.length; ++i) {
			sortedCooked += arrCookedString[i];
			sortedCooked += " ";
		}
		
		sortedCooked = sortedCooked.trim();
		String hashText = null;;
		try {
			hashText = Commons.getMD5Hash(sortedCooked);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error("Get Hash failed");
			LOGGER.error(e.getMessage());
		}
		
		String fullText = sortedCooked + delim + hashText;
		
		return fullText;
	}
	
	public boolean insertHtml(String docID, byte[] htmlData) {
		try {
			String atpDaemonName = OpenKnowBodyConfig.getInstance().getHtmlBDBDaemonName();
			String ip = OpenKnowBodyConfig.getInstance().getHtmlBDBIP();
			int port = OpenKnowBodyConfig.getInstance().getHtmlBDBPort();
			int htmlHashingNum = OpenKnowBodyConfig.getInstance().getHtmlHashingNum();
			Long hashResult = HashFunction.hash32(docID, "MS949", htmlHashingNum);
			
			ATPCommons.writeBDB(atpDaemonName, docID, hashResult, htmlData, ip, port);
		} catch (Exception e) {
			LOGGER.error("insertHtml Failed !! : DocID = " + docID);
			LOGGER.error(e.getMessage());
			return false;
		}
		
		return true;
	}
	
	public boolean makeTable(String tableName) {
		String query = OpenKnowCommons.getMakeTableQuery(tableName);
		MilanoEnums.DBReturnValues retVal = bodyHandler.createTable(query);
		
		if (retVal == MilanoEnums.DBReturnValues.SUCCESS)
			return true;
		else {
			LOGGER.error("Create Error(" + retVal + ") : " +  retVal.getMessage() + "\n" + "Query = " + query);
			return false;
		}
	}
	
	public boolean insert2DB(OpenKnowDBData data) {
		
		String tableNameYM = data.quesDate.substring(0, 4) + data.quesDate.substring(5, 7);
		String siteName = OpenKnowCommons.mapSiteIDName.get(Integer.parseInt(data.siteID));
		String tableName = siteName + "_" + tableNameYM;

		String insertSql = "replace into " + tableName + "(" +
		"HostName,PathName,DownloadFlag,ParsingFlag,ErrMsg,SiteID,DocID,Signature," +
		"Section,ExtrDate,TopCategory,SubCategory,QuesTitle,QuesTitleSig," +
		"QuesBody,QuesWriter,QuesDate,QuesTime,QuesHits,QuesReplyCnt,QuesPoint," +
		"AnsCount,AnsSelected,AnsOthers,AnsDate,AnsTime,MulmeLink,State," +
		"QuesRating,QuesRatingCnt,QuesMultiTag,executed,LastModified)" +
		" values('" + data.hostName + "','" + data.pathName + "','" + data.downloadFlag + "','" + data.parsingFlag + 
		"','" + data.errMsg + "'," + data.siteID + "," + data.docID + ",'" + data.signature +
		"','" + data.section + "','" + data.extrDate + "','" + StringEscapeUtils.escapeSql(data.topCategory) + "','" + StringEscapeUtils.escapeSql(data.subCategory) +
		"','" + StringEscapeUtils.escapeSql(data.quesTitle) + "','" + StringEscapeUtils.escapeSql(data.quesTitleSig) + "','" + StringEscapeUtils.escapeSql(data.quesBody) + "','" + StringEscapeUtils.escapeSql(data.quesWriter) +
		"','" + data.quesDate + "','" + data.quesTime + "'," + data.quesHits + "," + data.quesReplyCnt +
		"," + data.quesPoint + "," + data.ansCount + ",'" + StringEscapeUtils.escapeSql(data.ansSelected) + "','" + StringEscapeUtils.escapeSql(data.ansOthers) +
		"','" + data.ansDate + "','" + data.ansTime + "','" + data.mulmeLink + "','" + data.state +
		"'," + data.quesRating + "," + data.quesRatingCnt + ",'" + StringEscapeUtils.escapeSql(data.quesMultiTag) + "','" + data.executed + "', now())";
		
		//StringEscapeUtils.escapeEcmaScript(input)
		MilanoEnums.DBReturnValues returnVal = bodyHandler.insertData(insertSql);
		
		if (returnVal == MilanoEnums.DBReturnValues.SUCCESS) {
			return true;
		}
		else if (returnVal == MilanoEnums.DBReturnValues.ER_NO_SUCH_TABLE) {
			String createQuery = OpenKnowCommons.getMakeTableQuery(tableName);
			MilanoEnums.DBReturnValues createRetVal = bodyHandler.createDB(createQuery);
			
			if(createRetVal == MilanoEnums.DBReturnValues.SUCCESS) {
				MilanoEnums.DBReturnValues afterCreateReturnVal = bodyHandler.insertData(insertSql);
				if (afterCreateReturnVal == MilanoEnums.DBReturnValues.SUCCESS) {
					return true;
				} else {
					LOGGER.error("Insert Error(" + returnVal + ") : " + returnVal.getMessage() + "\n" + "Query = " + insertSql);
					return false;
				}
			} else {
				LOGGER.error("Create Error(" + returnVal + ") : " + returnVal.getMessage() + "\n" + "Query = " + createQuery);
				return false;
			}
		} else {
			LOGGER.error("Insert Error(" + returnVal + ") : " + returnVal.getMessage() + "\n" + "Query = " + insertSql);
			return false;
		}
	}
	
	public boolean updateListRecDB(String path, String crawlState, String quesWriteDateTime) {
		String updateSql = "update KNOW_LIST_RECENT set RunningStep = '200', crawlstate='" + crawlState + "',WriteDateTime = '" + quesWriteDateTime + "',ExtrDatetime = now()" + " where Path = '" + path + "'";
		MilanoEnums.DBReturnValues returnVal = this.listRecHandler.updateData(updateSql);
		
		if (returnVal == MilanoEnums.DBReturnValues.SUCCESS ) {
			return true;
		} else {
			LOGGER.error("Insert Error(" + returnVal + ") : " + returnVal.getMessage() + "\n" + "Query = " + updateSql);
			return false;
		}
	}
	
	public void work() {
		this.listRecHandler = listRecPool.getHandler();
		this.bodyHandler = bodyPool.getHandler();
		
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
		
		List<String> crawlData = this.targetDataInfo.getUrls();
		String filterName = this.targetDataInfo.getFilterName();
		String crawlState = "2011";
		
		for(String listUrl : crawlData) {
			LOGGER.info("Current path = " + listUrl);
			boolean crawlResult = true;
			OpenKnowDBData data = new OpenKnowDBData();
			GetMulmeLinkResultSet returnSet;
			
			URL temp = null;
			String host = "";
			String path = "";
			String errorMsg = "";
			Filter filter = null;
			Long tempDocID = this.getDocIDFromDocIDServer();
			String docID = tempDocID.toString();
			String quesWriteDateTime = "";
			if (tempDocID <= 0) {
				crawlResult = false;
				crawlState = "5311";
				errorMsg = "DocID get Failed : URL = " + listUrl;
				LOGGER.error("DocID get Failed : URL = " + listUrl);
			} 
			
			
			if(crawlResult) {
				try {
					temp = new URL("http://" + listUrl);
					host = temp.getHost();
					path = temp.getPath() + "?" + temp.getQuery();
				} catch (MalformedURLException e) {
					crawlResult = false;
					crawlState = "1011";
					errorMsg = "Url object create faild : " + e.getMessage();
					LOGGER.error("Url object create faild : " + e.getMessage());
				}
			}
			
			if(crawlResult) {
				try {
					filter = FilterUtil.getFilter(filterName);
				} catch (VeniceException e1) {
					crawlResult = false;
					crawlState = "5012";
					errorMsg = "Get filter failed : filtername = " + filterName;
					LOGGER.error("Get filter failed : filtername = " + filterName);
				}
			}
			
			if(crawlResult) {
				if(!checkOverHit()) {
					LOGGER.error("Over hit. can't crawl!!");
					this.initData();
					return;
				}
			}
			
			HtmlInfo htmlInfo = null;
			FilteredData filterResult = null;
			
			if(crawlResult) {
				for(int i = 0; i < OpenKnowBodyConfig.getInstance().getCountOfCrawlTimeout(); ++i) {
					try {
						htmlInfo = GrabHtml.create(temp.toString()).crawl();
						break;
					} catch (Exception e) {
						if ((e.getMessage() == "Read timed out") && (i < OpenKnowBodyConfig.getInstance().getCountOfCrawlTimeout() - 1)) {
							LOGGER.error("TIMTOUT!! count = " + i);
							continue;
						}
						crawlResult = false;
						crawlState = "1011";
						errorMsg = "Crawling failed(url = " + listUrl + ") : " + e.getMessage();
						LOGGER.error("Crawling failed(url = " + listUrl + ") : " + e.getMessage());
					}
				}
			}
			
			if(crawlResult) {
				data.hostName = host; data.pathName = path; data.downloadFlag = "1"; data.parsingFlag = "1";
				data.docID = docID;
				
				try {
					filterResult = filter.filtering(htmlInfo);
					if(filterResult.getFilterState() == 1) {  
						Map<String, String> filterResultMap = filterResult.getCollectionData();

						data.section = filterResultMap.get("section");data.topCategory = filterResultMap.get("topcategory");
						data.subCategory = filterResultMap.get("subcategory");data.quesTitle = filterResultMap.get("questitle");
						data.quesBody = filterResultMap.get("quesbody");data.quesWriter = filterResultMap.get("queswriter");
						data.quesDate = filterResultMap.get("quesdate");data.quesTime = filterResultMap.get("questime");
						data.quesHits = filterResultMap.get("queshits");data.quesReplyCnt = filterResultMap.get("quesreplycnt");
						data.quesPoint = filterResultMap.get("quespoint");data.quesRating = filterResultMap.get("quesrating");
						data.quesRatingCnt = filterResultMap.get("quesratingcnt");data.ansCount = filterResultMap.get("anscount");
						data.ansSelected = filterResultMap.get("ansselected");data.ansOthers = filterResultMap.get("ansothers");
						data.ansDate = filterResultMap.get("ansdate");data.ansTime = filterResultMap.get("anstime");
						data.quesMultiTag = filterResultMap.get("allmultitag");data.state = filterResultMap.get("state");
						data.signature = filterResultMap.get("signature");	data.siteID = filterResultMap.get("siteid");
						data.executed = filterResultMap.get("executed"); data.extrDate = Commons.getDateTime();

						if (!(data.quesMultiTag == "")) {
							returnSet = OpenKnowCommons.GetMulmeLink(data.quesMultiTag, docID);
							data.mulmeLink = returnSet.getSelected();
							crawlState = returnSet.getReturnState();
						}
						data.quesTitleSig = this.getQuesTitleSig(data.quesTitle);
					} else { 
						crawlResult = false;
						crawlState = "5012";
						errorMsg = "Filter failed(" + filterName + ") : getFilterState = " + filterResult.getFilterState();
						LOGGER.error("Filter failed(" + filterName + ") : getFilterState = " + filterResult.getFilterState());
					}
				} catch (VeniceException e) {
					crawlResult = false;
					crawlState = String.valueOf(e.getError_code());
					errorMsg = "Filtering failed(filtername = " + filterName + ", url = " + listUrl + " " + e.getMessage();
					LOGGER.error("Filtering failed(filtername = " + filterName + ", url = " + listUrl + " " + e.getMessage());
				}
			}
			
			if(crawlResult) {
				if(!this.insertHtml(docID, htmlInfo.getHtmlSource().getBytes())) {
					crawlResult = false;
					crawlState = "5312";
					errorMsg = "BDB Insert Failed";
					LOGGER.error("BDB Insert Failed : path = " + data.pathName);
				}
			}
			
			data.errMsg = errorMsg;
			if(crawlResult) {
				LOGGER.info("All Success : path = " + data.pathName);
				if(!this.insert2DB(data)) {
					crawlState = "5111";
					LOGGER.error("DB insert Failed : path = " + data.pathName);
				}
			}
			
			quesWriteDateTime = data.quesDate + " " + data.quesTime;
			
			if (!updateListRecDB(path, crawlState, quesWriteDateTime)) {
				LOGGER.error("update error");
			}
		}
		
		this.initData();
	}
}
