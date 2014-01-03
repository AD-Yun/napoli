package search.crawl.napoli.filter.know;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import search.crawl.napoli.common.VeniceEnums;
import search.crawl.napoli.common.VeniceException;
import search.crawl.napoli.common.VeniceEnums.Errors;
import search.crawl.napoli.filter.DocFilter;
import search.crawl.napoli.filter.FilterElement;
import search.crawl.napoli.filter.FilteredData;
import search.crawl.napoli.util.Commons;
import search.crawl.napoli.util.HtmlParser;
import search.crawl.napoli.util.InterruptibleCharSequence;



public class KnowFilter extends DocFilter {
	static final Logger LOGGER = LoggerFactory.getLogger(KnowFilter.class);
	
	private final char largeAnswerDelim = (char)0x11;
	private final char smallAnswerDelim = (char)0x09;
	private final char largeMultiDelim = (char)0x03;
	private final char smallMultiDelim = (char)0x02;
	/**
	 * openknow 에서 parsing 에 이용되는 html blocks.
	 */
	List<String> htmlBlocks = null;
	protected int selectedAnswerCount=0;
	protected int answerCount=0;
	/**
	 * openknow 에서 공통으로 쓰이는 delete string list 로 셋팅.
	 */
	private List<String> lstDeleteString = null;
	
	protected List<String> answerElements = new ArrayList<String>(
			Arrays.asList("a_title", "a_rating", "a_ratingcount", "a_id", "a_date", "a_time", "a_bodyhtml" ));
	 
	/**
	 * valid extract fields... - 최종 추출된 결과 field name.
	 * service/type 별로..... 다름.
	 */
	protected List<String> validExtractFields = new ArrayList<String>(
			Arrays.asList("section", "topcategory", "subcategory", "questitle", "quesbody", "queswriter", "quesdate",
					"questime", "queshits", "quesreplycnt", "quespoint","quesrating", "quesratingcnt", "anscount", "ansselected", 
					"ansothers", "ansdate", "anstime",
					"state", "allmultitag", "signature", "siteid", "executed")
			);
	
	protected List<String> validDigitFields = new ArrayList<String>(Arrays.asList("queshits", "quesreplycnt", "quespoint","quesrating",
			"quesratingcnt", "anscount"));
	/**
	 * 공통 정규식 전처리는 아래에서 진행하고 
	 * 사이트별 후처리를 사이트별 filter에서 처리하자.
	 * 일종의 hook 와 같다. 구현을 하지 않아도 상관 없는....
	 * 기본적으로 DocFilter.deleteStringProcessing 은 호출하는것이 좋겠다.
	 * @see search.crawl.napoli.filter.Filter#regexpPreprocessing()
	 */
	public int regexpPreprocessing() { 
		int ret = deleteStringProcessing(this.lstDeleteString);
		if(ret != 1) { return ret; } 
		String orignalHtml = getHtmlInfo().getHtmlSource();
		
		this.htmlBlocks = new ArrayList<String>();
		KnowFilterElement kfe = null;
		try { 
			for(FilterElement fe : this.getFilterInfo()) {
				kfe = (KnowFilterElement) fe;
				this.htmlBlocks = cutterBlock(orignalHtml, kfe);
				if(this.htmlBlocks != null && this.htmlBlocks.size() > 0) { 
					//임시로 MatchedRegIndex 를 setting. 어쩔수 없음..
					super.setMatchedRegIndex(this.getFilterInfo().indexOf(fe));
					break;
				}
			}
		} catch(VeniceException e) { 
			return VeniceEnums.Errors.KNOW_EXTRACT_BLOCK_FAIL.getErrorCode();
		}
		if(this.htmlBlocks.size() == 0) {
			return VeniceEnums.Errors.KNOW_EXTRACT_BLOCK_FAIL.getErrorCode();
		}
		super.setTargetHtml(this.htmlBlocks.get(1));
		return ret;
	}

	/**
	 * ok 의 prifix, suffix, delim 을 이용해서 KnowFilterElement.answerHtml, KnowFilterElement.sectionHtml 을 구하여 setting.
	 * 메인 regexp 는 preifx, suffix 는 
	 * @param html
	 * @param fe
	 * @return
	 */
	private List<String> cutterBlock(String html, KnowFilterElement kfe) throws VeniceException {		
		
		List<String> blocks = new ArrayList<String>();
		//답변에 들어갈 구분자 제거
		html = html.replaceAll(Character.toString(this.largeAnswerDelim), " ");
		html = html.replaceAll(Character.toString(this.smallAnswerDelim), " ");

		//채택된 답변의 개수를 찾는다.
		String selectedClue = kfe.getSelectedClue();
	    if(selectedClue.equals("")) {
	    	this.selectedAnswerCount = 0;
	    } else { 
	    	Pattern p = Pattern.compile(selectedClue, Pattern.MULTILINE | Pattern.DOTALL);
	    	Matcher m = p.matcher(html);
	    	this.selectedAnswerCount = m.groupCount();
	    }
	    //suffix, prefix 가 없다면 error
	    if(html.indexOf(kfe.getSuffix()) == -1 || html.indexOf(kfe.getPrefix()) == -1) {
	    	LOGGER.error("suffix or prefix is not exsist.");
	    	throw new VeniceException(2007, "suffix or prefix is not exsist.", new Exception("suffix or prefix is not exsist."));
	    }
	    
	    String firstBlock = html.substring(0,html.indexOf(kfe.getPrefix())-1);
	    String secondBlock = "";
	    String thirdBlock = "";
	    html = html.substring(html.indexOf(kfe.getPrefix()));
	    if(html.indexOf(kfe.getDelimiter()) == -1 || kfe.getDelimiter().length() == 0) { 
	    	//delim이 하나도 없을 경우 질문 부분 처리
	    	secondBlock = html.substring(0,html.indexOf(kfe.getSuffix())-1);
	    	
	    } else { 
	    	//delim이 하나라도 있을경우 -> 답변이 달린 질문이다.
	    	secondBlock = html.substring(0,html.indexOf(kfe.getDelimiter())-1); //질문부가 들어가는 html
	    	thirdBlock = html.substring(html.indexOf(kfe.getDelimiter()), html.indexOf(kfe.getSuffix())); //답변부가 들어가는 html
	    }
	    String fourthBlock = html.substring(html.indexOf(kfe.getSuffix()));

	    blocks.add(firstBlock);
	    blocks.add(secondBlock);
	    blocks.add(thirdBlock);
	    blocks.add(fourthBlock);
		return blocks;
	}
	
	/**
	 * 공통 정규식 후처리는 아래에서 구현하고 
	 * 사이트별 후처리는 사이트별 filter에서 처리하자
	 * 일종의 hook 와 같다. 구현을 하지 않아도 상관 없는....
	 * @see search.crawl.napoli.filter.Filter#regexpPostprocessing()
	 */
	public Map<String, String> regexpPostprocessing(Map<String, String> ins) {
		KnowFilterElement kfe = (KnowFilterElement)this.getFilterInfo().get(this.getMatchedRegIndex());
		//답변부분 regexp 구성 , 먼저 delim로 답변 부분을 나눈 다음 각각의 본문에 대해 regexp를 돌린다.
		// 블럭별로 답변부분 regexp 실행, a_fields setting.
		InterruptibleCharSequence ics = null;
		Matcher matcher = null;
		String answersHtml = this.htmlBlocks.get(2);
		List<String> answersList = new ArrayList<String>(); //역순 답변
		String oneAnswer = "";
		while (true) {
			if(answersHtml.indexOf(kfe.getDelimiter()) == -1 || kfe.getDelimiter().length() == 0 ) { 
				break;
			}
			oneAnswer = answersHtml.substring(answersHtml.lastIndexOf(kfe.getDelimiter()));
			answersList.add(oneAnswer);
			answersHtml = answersHtml.substring(0, answersHtml.lastIndexOf(kfe.getDelimiter()));
		}
		
		/*List<String> answersList  = new ArrayList<String>(Arrays.asList(StringUtils.splitByWholeSeparator(answersHtml, kfe.getDelimiter())));	*/	
		this.answerCount = answersList.size();
		if(this.answerCount == 1 && answersList.get(0).length() < 5) { 
			this.answerCount = 0;
		} else { 
			int answerInx = 0;
			String answerString = "";
			for(int i = this.answerCount-1 ; i >= 0 ; i--) {
			//for(String answerString : answersList) {
				answerString = answersList.get(i);
				ics = new InterruptibleCharSequence(answerString);
				//LOGGER.info(ics.toString());
				matcher = kfe.getaPattern().matcher(ics.toString());
				if(matcher.find()) {
					// a_fields 에 있는 순서대로... 
					int j = 0; 
					for(String field : kfe.getaFields()) {
						ins.put(answerInx + "," + field, matcher.group(j+1));
						j++;
					}
				} else { 
					//TODO: a_regexp 가 맞지 않는다... 
				}
				answerInx += 1;
			}
		}
		//섹션부분 regexp 구성, s_fields setting.
		String sectionHtml = htmlBlocks.get(kfe.getSectionTarget());
		ics = new InterruptibleCharSequence(sectionHtml);
		matcher = kfe.getsPattern().matcher(ics.toString());
		if(matcher.find()) {
			int i = 0; 
			for(String field : kfe.getsFields()) {
				ins.put(field, matcher.group(i+1));
				i++;
			}			
		} else { 
			//TODO: s_regexp 맞지 않아~
		}

		return ins;
	}
	
	protected int getSiteID() {
		int siteID = 0;
		if(this.getFilterName().indexOf("daum") > -1) { 
			siteID = 6;
		} else if(this.getFilterName().indexOf("simfile") > -1) {
			siteID = 13;
		} else if(this.getFilterName().indexOf("jobkorea") > -1) {
			siteID = 17;
		} else if(this.getFilterName().indexOf("jisiklog") > -1) {
			siteID = 18;
		} else if(this.getFilterName().indexOf("kita") > -1) {
			siteID = 15;
		}
		return siteID;
	}
	/*
	 * 동일한 DB 스크마를 이용하므로 아래 method 에서 처리 db 를 위한 
	 * 데이터 처리를 하자.
	 * 혹, 하위 클래스에서 해야한다면 overriding 하도록 하자.
	 * @see search.crawl.napoli.filter.Filter#setDataObject()
	 */
	public FilteredData setDataObject(Map<String, String> regexpResult)  throws VeniceException {
		//validExtractFields 와 map 결과가 일치하는 것이 있다면 그대로 setting.
		Map<String, String> filteredResult = new HashMap<String, String>();
		//TODO : 아래부부분은 모두 추가하자...
		filteredResult = setAnswerText(regexpResult, filteredResult);
		
		//multimediatag 처리..  무조건 추가 title, bodyhtml 주의
		filteredResult.put("quesmultitag", HtmlParser.parseMultimediaTag(regexpResult.get("bodyhtml"), HtmlParser.getTextOnly(regexpResult.get("title"))));
		filteredResult = setMulmeTag(filteredResult);
		//signature
		try {
			filteredResult = setSignature(filteredResult);
		} catch (Exception e) {
			LOGGER.error("Signature creation is failure.");
			throw new VeniceException(VeniceEnums.Errors.SIGNATURE_CREATE_FAIL, e);
		}
		
		for(String field : this.validExtractFields) {
			if(regexpResult.containsKey(field.toLowerCase())) {
				filteredResult.put(field, HtmlParser.getTextOnly(regexpResult.get(field.toLowerCase())));
			}
		}
		filteredResult.put("quesdate" , refineDate(regexpResult.get("quesdate")));
		if(regexpResult.containsKey("bodyhtml")) {
			filteredResult.put("quesbody", HtmlParser.getTextOnly(regexpResult.get("bodyhtml")));
		}
		if(regexpResult.containsKey("title")) {
			filteredResult.put("questitle", HtmlParser.getTextOnly(regexpResult.get("title")));
		}
		
		filteredResult = setSiteID(filteredResult);
		
		//validExtractFields 가 없다면... "" 로 setting. robot 요청사항..ㅡ..ㅡ
		for(String field : this.validExtractFields) {
			if(! filteredResult.containsKey(field)) {
				filteredResult.put(field, "");
			}			
			if(validDigitFields.contains(field) && filteredResult.get(field).equals("")) { 
				filteredResult.put(field, "0");
			}
		}
		//filtering 결과 이외의 데이터는 filter 이후 setting.
		FilteredData fd = new FilteredData("doc", Integer.parseInt(regexpResult.get("filterstate")), filteredResult);
		return fd;
	}
	
	protected Map<String, String> setSiteID(Map<String, String> map) throws VeniceException { 
		Map<String, String> out = map;
		int siteId = getSiteID();
		//siteId 가 0 이면 문제가 있는 것이다.
		if(siteId == 0) {
	    	LOGGER.error("siteid setting failure.....");
	    	throw new VeniceException(Errors.SITEID_SETTING_FAILURE, new Exception("siteid setting failure....."));
		}
		out.put("siteid", String.valueOf(siteId));
		if(siteId == 6 && (map.containsKey("subcategory") && map.get("subcategory").indexOf("국민신문고") >= -1)) {
			out.put("executed", "7");
		} else if(siteId == 14 && map.get("section").indexOf("노하우") >= -1) {
			out.put("executed", "7");
		} else { 
			out.put("executed", "");
		}
		return out;
	}
	
	protected Map<String, String> setAnswerText(Map<String, String> map, Map<String, String> retMap) {
		int ansCount = this.answerCount;
		String indexName = "";
		StringBuilder ansMultiTag = new StringBuilder();
		String tmp = "";
		StringBuilder selectedAns = new StringBuilder();
		StringBuilder otherAns = new StringBuilder();
		for(int i = 0 ; i < ansCount ; i++) {
			for(String ele :answerElements) { 
				indexName = i + "," + ele;
				if(map.get(indexName) == null) {
					map.put(indexName, "");
				}
			}
			tmp = HtmlParser.parseMultimediaTag(map.get(i+",a_bodyhtml"), HtmlParser.getTextOnly(map.get(i+",a_title")));
			ansMultiTag.append(tmp);
			
			List<String> tmpList = new ArrayList<String>();
			//Arrays.asList("a_title", "a_rating", "a_ratingcount", "", "", "a_time", "" ));
			if(i < selectedAnswerCount) { 
				//선택답변
				tmpList.add(HtmlParser.getTextOnly(map.get(i+",a_title")));
				tmpList.add(HtmlParser.getTextOnly(map.get(i+",a_rating")));
				tmpList.add(HtmlParser.getTextOnly(map.get(i+",a_ratingcount")));
				tmpList.add(HtmlParser.getTextOnly(map.get(i+",a_id")));
				tmpList.add(HtmlParser.getTextOnly(map.get(i+",a_date")));
				tmpList.add(HtmlParser.getTextOnly(map.get(i+",a_time")));
				tmpList.add(HtmlParser.getTextOnly(map.get(i+",a_bodyhtml")));
				tmpList.add(tmp);				
				selectedAns.append(StringUtils.join(tmpList, smallAnswerDelim) + largeAnswerDelim);
			} else { 
				//선택 안된 답변
				tmpList.add(HtmlParser.getTextOnly(map.get(i+",a_title")));
				tmpList.add(HtmlParser.getTextOnly(map.get(i+",a_rating")));
				tmpList.add(HtmlParser.getTextOnly(map.get(i+",a_ratingcount")));
				tmpList.add(HtmlParser.getTextOnly(map.get(i+",a_id")));
				tmpList.add(HtmlParser.getTextOnly(map.get(i+",a_date")));
				tmpList.add(HtmlParser.getTextOnly(map.get(i+",a_time")));
				tmpList.add(HtmlParser.getTextOnly(map.get(i+",a_bodyhtml")));
				tmpList.add(tmp);				
				otherAns.append(StringUtils.join(tmpList, smallAnswerDelim) + largeAnswerDelim);
			}
		}

		retMap.put("ansselected", selectedAns.toString());
		retMap.put("ansothers", otherAns.toString());
		if(selectedAnswerCount > 0) {
			retMap.put("ansdate",  map.get(0+",a_date"));
			retMap.put("anstime",  map.get(0+",a_time"));
		} else {
				retMap.put("ansdate","0000-00-00");
				retMap.put("anstime", "00:00:00");
		}
		retMap.put("ansmultitag", ansMultiTag.toString());
		return retMap;
	}
	
	protected Map<String, String> setSignature(Map <String, String> map) throws Exception {
		String signaureStr = map.get("section") + map.get("topcategory") + map.get("subcategory") + map.get("questitle") + map.get("quesbody") +
				map.get("queswriter") + map.get("quesmultitag") + map.get("ansselected") + map.get("ansothers") + map.get("allmultitag");
		signaureStr = signaureStr.replaceAll("[0-9]", "");
		map.put("signature", Commons.getMD5Hash(signaureStr));
		return map;
	}
	
	protected Map<String, String> setMulmeTag(Map<String, String> map) {
		map.put("allmultitag", map.get("ansmultitag") + map.get("quesmultitag"));
		return map;
	}

	/**
	 * daum에서만 쓰고 있음, daum중 날짜가 string과 숫자의 조합으로 된 경우 이proc에 넣어야한다.
	 * @param date
	 * @return
	 */
	protected List<String> translateDate(String date) {
		List<String> retList = new ArrayList<String>();

		String tmpRegexp = "([0-9]+-[0-9]+-[0-9]+) ([0-9]+:[0-9]+)";
		Matcher dMatcher = Pattern.compile(tmpRegexp, Pattern.MULTILINE | Pattern.DOTALL).matcher(date);
		if(dMatcher.find()) { 
			retList.add(dMatcher.group(1));
			retList.add(dMatcher.group(2));
		} else { 
			String[] arrDate = date.split(" ");
			String sADate = arrDate[0];
			String sAType = arrDate[1];
			tmpRegexp = "([0-9]+)([^0-9]*)";
			Pattern pattern = Pattern.compile(tmpRegexp, Pattern.MULTILINE | Pattern.DOTALL);
			Matcher matcher = pattern.matcher(sADate);
			int nNumber = 0;
			String sYMS = "";
			int sEngYMS = Calendar.SECOND;
			if(matcher.find()) { 
				nNumber = Integer.parseInt(matcher.group(1));
				sYMS = matcher.group(2);
				switch(sYMS) { 
				case "주" : 
					sEngYMS = Calendar.DATE;
					nNumber = nNumber * 7;
					break;
				case "일" : 
					sEngYMS = Calendar.DATE;
					break;
				case "시간" :
					sEngYMS = Calendar.HOUR;
					break;
				case "분" : 
					sEngYMS = Calendar.MINUTE;
					break;
				case "초" :
					sEngYMS = Calendar.SECOND;
					break;
				default :
					;
				}
			} else { 
				switch(sADate) { 
				case "이틀" :
					sEngYMS = Calendar.DATE;
					nNumber = 2;
					break;
				case "하루" :
					sEngYMS = Calendar.DATE;
					nNumber = 1;
					break;
				case "방금" :
					sEngYMS = Calendar.DATE;
					nNumber = 0;
					sAType = "전";
					break;
				default :
					;
				}
			}
			int engType = -1;
			switch(sAType) { 
			case "전" :
				engType = -1;
				break;
			case "후" :
				engType = 1;
				break;
			default :
				;
			}
			int timeDiff = engType * nNumber;
			Calendar c = Calendar.getInstance();
			c.add(sEngYMS, timeDiff); 
			/* c.add(Calendar.DATE, timeDiff); //1일전 */
			Date ddate = c.getTime();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String returnDate =  dateFormat.format(ddate);	
			dateFormat = new SimpleDateFormat("HH:mm:ss");
			String returnTime = dateFormat.format(ddate);		
			retList.add(returnDate);
			retList.add(returnTime);
		}
		return retList;
	}
	
	private String getQuesDate(String yyyymmdd, String style) throws ParseException {
		return yyyymmdd.replaceAll(style, "-");
	}
	protected String refineDate(String ymd) {
		// yyyy.mm.dd, yyyy-mm-dd, yyyy.m.d, yyyy/mm/dd, yy/m/d, yy-m-d 를 yyyy-mm-dd 로 변경.
		try { 
			if (ymd.matches("[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]")) {
			} else if(ymd.matches("[0-9][0-9][0-9][0-9].[^0-9]*[0-9]+.[^0-9]*[0-9]+")) {
				String regexp = "([0-9][0-9][0-9][0-9]).[^0-9]*([0-9])+.[^0-9]*([0-9]+)";
				Matcher dMatcher = Pattern.compile(regexp, Pattern.MULTILINE | Pattern.DOTALL).matcher(ymd);
				if(dMatcher.find()) { 
					String mm = dMatcher.group(2);
					if(mm.length() == 1) { 
						mm = "0" + mm;
					}
					String dd = dMatcher.group(3);
					if(dd.length() == 1) { 
						dd = "0" + dd;
					}
					String yy = dMatcher.group(1);
					ymd = getQuesDate(yy + "." + mm + "." + dd, "\\.");
				} else { 
					throw new Exception("QuesDate Parse Exception");
				}
			} else if(ymd.matches("[0-9][0-9][0-9][0-9]/[0-9][0-9]/[0-9][0-9]")) { 
				ymd = getQuesDate(ymd, "/");
			} else { 
				LOGGER.error("Ques Date Parsing Error" + ymd);
			}
		} catch(Exception e) { 
			LOGGER.error("Ques Date Parsing Exception" + ymd);
		}
		return ymd;
	}
}




















