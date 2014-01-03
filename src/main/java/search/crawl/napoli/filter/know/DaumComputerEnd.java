package search.crawl.napoli.filter.know;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import search.crawl.napoli.common.VeniceEnums;
import search.crawl.napoli.common.VeniceException;
import search.crawl.napoli.filter.FilterElement;
import search.crawl.napoli.filter.FilteredData;
import search.crawl.napoli.util.HtmlParser;
import search.crawl.napoli.util.InterruptibleCharSequence;

public class DaumComputerEnd extends KnowFilter {

	private List<String> lstDeleteString = new ArrayList<String>(
			Arrays.asList("<dd class=\"title\"><b class=\"bB14\">삭제 또는 존재 하지 않는 게시글 입니다.</b></dd>", 
					"<span class=\"bOr14\">삭제되었거나 존재하지 않는</span>",
					"이 정보내용은 청소년 유해매체물로서 정보통신망 이용촉진 및<br>정보보호등에 관한 법률 및 청소년보호법의 규정에 의하여<br>",
					"<b class=\"bB14\">오류가 발생하였습니다",
					"<dt class=\"bBl14_2\">권리침해신고 접수로 인해 <span class=\"bOr14\">임시 접근금지</span> 조치된 글입니다",
					"이 정보는 청소년 유해매체물 또는 청소년에게 제공하기 부적합한 <br />",
					"<dt class=\"fw_b\">삭제되었거나 존재하지 않는 게시글 입니다.</dt>",
					"<dt class=\"fw_b line2\">권리침해신고 접수로 인해<br>임시 접근금지 조치된 글입니다."));
	private List<FilterElement> filterInfo;
	private String key;
	
	static final Logger LOGGER = LoggerFactory.getLogger(DaumComputerEnd.class);
	public DaumComputerEnd() {
		this.filterInfo = new ArrayList();
		/*
		 * pattern2 가 있다면 아래를 다시한번 반복
		 */
		/* pattern 1 */
		String prefix = "<div id=\"body_wrap\">";
		String suffix = "<!-- //블로그 답변 -->";
		String delim = "<dt class=\"aTop clearfix\"";
		// q_regexp
		String regexp = "<!-- 질문 -->.*<p class=\"title\"  id=\"q_title\">(.*)<p class=\"etc\">([^<]*<a href=\"http://[^>]*>)?" +
				"\n?([^\n]*)[^<]*<span class=\"bar\">\\|</span>([^<]*).*조회 <span id=\"viewCount\">([0-9]*)</span>" +
				"[^<]*<span class=\"bar\">\\|</span>[^<]*답변 <strong>([0-9]+)</strong>.*" +
				"<div class=\"content\">(.*)<!-- 링크 -->";
		List<String> fields = new ArrayList<String>(
				Arrays.asList("title", "temp", "id", "date_time_clue", "readcount", "answercount", "bodyhtml"));
		
		Pattern pattern = Pattern.compile(regexp, Pattern.MULTILINE | Pattern.DOTALL);
		
		/*
		 * opneknow 에서는 KnowFilterElement 를 생성해야한다. 
		 * KnowFilterElement 내부에 있는 것은 마지막 자손 filter class 에서만 사용(?) 된다.
		 * a_regexp, s_regexp 기타 등등.
		 */
		KnowFilterElement fe = new KnowFilterElement(regexp, fields, pattern, prefix, suffix, delim);
		/*
		fe.setExceptHosts(new HashSet<String>(Arrays.asList("image.hanmail.net/hanmail/", "k.daum.net/font")));
		HtmlParser.setExceptHost(fe.getExceptHosts());
		*/
		HtmlParser.setExceptHost(new HashSet<String>(Arrays.asList("image.hanmail.net/hanmail/", "k.daum.net/font")));
		
		/*select target */
		fe.setSectionTarget(1);
		
		/* 답변 */
		fe.setARegexp("(.*)<dd class=\"aContent[^\"]*\">(.*)<!-- //상세 내용 -->(.+)");
		List<String> aFields = new ArrayList<String>(Arrays.asList("a_selected_id_clue", "a_bodyhtml", "a_date_time_clue"));
		fe.setaFields(aFields);

		fe.setSRegexp("<div class=\"catePath listBtnArea clearfix\">(.+)<!--같은분류 카테고리 레이어 -->");
		List<String> sFields = new ArrayList<String>(Arrays.asList("s_allsection"));
		fe.setsFields(sFields);
		
		filterInfo.add(fe);	
		/* pattern 1 완료 */
	}
	
	public int regexpPreprocessing() {
		String html = this.getHtmlInfo().getHtmlSource();
		int ret = super.regexpPreprocessing();
		if(ret != 1) {
			return ret;
		}
		
		ret = deleteStringProcessing(this.lstDeleteString);
		if(ret != 1) {
			return ret;
		}
		
		if(html.length() < 500 && html.indexOf("top.location.href = '") > -1) { 
			// return 값에 대한 문제는..??? 어떻게 해야하나????
			return 2;
		}

		return 1;
    }
	/*
	 * 공통 정규식 후처리는 아래에서 구현하고 
	 * 사이트별 후처리는 사이트별 filter에서 처리하자
	 * 정규식이 2가지가 될 수 있기에 if 문으로 나누어서 후처리 한다.
	 * 정규식이 1가지만으로 처리 된다면 filterPostprocessing 에 직접 로직을 구현하자.
	 * @see search.crawl.napoli.filter.Filter#filterPostprocessing()
	 */
	public Map<String, String> regexpPostprocessing(Map<String, String> result)  {
		/* 
		 * 방안 reflection 을 이용하는 방안. 일단 비추~ 
			Method exec_method = null;
			try {
				exec_method = this.getClass().getMethod("firstPostprocessing", Map.class);
			} catch (NoSuchMethodException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				result = (Map<String, String>) exec_method.invoke(this, result);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		****/
		//TODO : 반드시 KnowFilter regexpPostprocessing 를 호출
		result = super.regexpPostprocessing(result);
		
		/*protected List<String> answerElements = new ArrayList<String>(
				Arrays.asList("a_title", "a_rating", "a_ratingcount", "a_id", "a_date", "a_time", "a_bodyhtml" ));
		 
		*//**
		 * valid extract fields... - 최종 추출된 결과 field name.
		 * service/type 별로..... 다름.
		 *//*
		protected List<String> validExtractFields = new ArrayList<String>(
				Arrays.asList("section", "topcategory", "subcategory", "questitle", "quesbody", "queswriter", "quesdate",
						"questime", "queshits", "quesreplycnt", "quespoint","quesrating", "qesratingcnt", "anscount", "ansselected", "ansothers", "ansdate", "anstime",
						"state", "allmultitag", "signature")
				
				Arrays.asList("title", "temp", "id", "date_time_clue", "readcount", "answercount", "bodyhtml"));
				);*/
		
		int retState = 1;
		//본문에서 출처 삭제
		String bodyhtml = result.get("bodyhtml");
		bodyhtml = Pattern.compile("x-SCRIPT|scr-ipt|XSCRIPT", Pattern.CASE_INSENSITIVE).matcher(bodyhtml).replaceAll("script");

		//본문내의 자동생성 문구 제거  ex) Daum 영화 > 주지훈
		int inInx = bodyhtml.indexOf("<p class=\"kboardCate fs_13_g fc_g3\">") ;
		if(inInx > -1) {
			int inInx2 = bodyhtml.indexOf("</p>");
			if(inInx2 > -1) { 
				bodyhtml = bodyhtml.substring(0, inInx-1) + bodyhtml.substring(inInx+4);
			}	
		}
		result.put("bodyhtml", bodyhtml);
		
		InterruptibleCharSequence ics = new InterruptibleCharSequence(result.get("date_time_clue"));
		String regexp = "([0-9]+-[0-9]+-[0-9]+) ([0-9]+:[0-9]+)";
		Matcher matcher = Pattern.compile(regexp, Pattern.MULTILINE | Pattern.DOTALL).matcher(ics.toString());
		if(matcher.find()) {
			result.put("date", matcher.group(1));
			result.put("time", matcher.group(2));		
		} else { 
			matcher = Pattern.compile("([^<]* 전)", Pattern.MULTILINE | Pattern.DOTALL).matcher(ics.toString());
			if(! matcher.find()) {
				result.put("filterstate", "2004");
	        	return result;
			} else { 
				String strDate = matcher.group(1);
				List<String> lsDateTime = translateDate(strDate);
				if(lsDateTime.size() == 0) { 
					result.put("filterstate", "2004");
		        	return result;
				} else { 
					result.put("date", lsDateTime.get(0));
					result.put("time", lsDateTime.get(1));
				}
			}
		}
		
		String id = result.get("id");
		id = id.replaceAll("작성자:", "");
		result.put("id", id);
		
		//답변부분 출처 정보 제거
		String inxName = "";
		String a_bodyhtml = "";
		String aIdClue = "";
		String aSelectedClue = "";
		String aDateTimeClue = "";
		int answerLength = 0;
		for(int inx = 0 ; inx < answerCount ; inx++) {
			ics = new InterruptibleCharSequence(result.get(inx + ",a_bodyhtml"));
			a_bodyhtml = ics.toString();
			a_bodyhtml = Pattern.compile("x-SCRIPT|scr-ipt|XSCRIPT", Pattern.CASE_INSENSITIVE).matcher(a_bodyhtml).replaceAll("script");
			a_bodyhtml = Pattern.compile("&#xD;", Pattern.CASE_INSENSITIVE).matcher(a_bodyhtml).replaceAll("");

	        inxName = inx + ",a_selected_id_clue";
	        
	        matcher = Pattern.compile("(.*)class=\"nick\"[^>]*>(.*)</.*", Pattern.MULTILINE | Pattern.DOTALL).matcher(result.get(inxName));
	        if(! matcher.find()) { 
	        	aSelectedClue = "";
	        	aIdClue = result.get(inxName);
	        } else {
	        	aSelectedClue = matcher.group(1);
	        	aIdClue = matcher.group(2);
	        }
	        matcher = Pattern.compile("([^<]*)", Pattern.MULTILINE | Pattern.DOTALL).matcher(aIdClue);
	        if(! matcher.find()) { 
	        	result.put("filterstate", "2003");
	        	return result;
	        } else { 
	        	result.put(inx + ",a_id", matcher.group(1)); 
	        }
	        aIdClue = aIdClue.replaceAll("엑스퍼트</b> :", "");
	        // 답변에 질문자 평가
	        if(aSelectedClue.indexOf("<div class=\"userImg\">") >= 1 || aSelectedClue.indexOf("<div class=\"chooseMedal\"") > -1) {
	        	this.selectedAnswerCount += 1;
	        }
	        
	        //답변 작성자 gabage 제거
	        aIdClue = aIdClue.replaceAll("작성자:", "");
	        if(a_bodyhtml.indexOf("블라인드된 답변입니다") > -1 && a_bodyhtml.length() < 350) { 
	        	a_bodyhtml = "";
	        }
	        
	        answerLength = answerLength + a_bodyhtml.length();
	        int tempInx = a_bodyhtml.indexOf("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"NKtContF\">");
	        if( tempInx > -1) { 
	        	a_bodyhtml = a_bodyhtml.substring(0,tempInx - 1);
	        }
	        
	        //전문가 답변인 경우 date,time 이 존재 안함
	        aDateTimeClue = result.get(inx + ",a_date_time_clue");
	        if(aDateTimeClue.indexOf("전문가 배너") > -1) { 
	        	LOGGER.info(" 전문가 배너 aDateTimeClue : " + aDateTimeClue);
	        	result.put(inx + ",a_date", "0000-00-00");
	        	result.put(inx + ",a_time", "00:00");
	        } else if(aIdClue.indexOf("카페이름 : <a href=\"http://cafe.daum.net/") > -1) {
	        	//# 카페 답변인 경우
	        	matcher = Pattern.compile("(.+)<span class=\"fc_g3 cDate\">([0-9]+-[0-9]+-[0-9]+) ([0-9]+:[0-9]+)</span>", 
	        			Pattern.MULTILINE | Pattern.DOTALL).matcher(a_bodyhtml);
	        	if(matcher.find()) { 
	        		a_bodyhtml = matcher.group(1);
	        		result.put(inx + ",a_date", matcher.group(2));
	        		result.put(inx + ",a_time", matcher.group(3));
	        	} else { 
	        		result.put("filterstate", "2005");
		        	return result;
	        	}
	        } else if(aIdClue.indexOf("target=\"new\">블로그 바로가기</a>") > -1) {
	        	//블로그 답변인 경우
	        	matcher = Pattern.compile("(.+)<span class=\"fc_g3 cDate\">([0-9]+-[0-9]+-[0-9]+) ([0-9]+:[0-9]+)</span>", Pattern.MULTILINE | Pattern.DOTALL).matcher(a_bodyhtml);
	        	if(matcher.find()) { 
	        		a_bodyhtml = matcher.group(1);
	        		result.put(inx + ",a_date", matcher.group(2));
	        		result.put(inx + ",a_time", matcher.group(3));
	        	} else { 
	        		//TODO : ERROR, 
	        		result.put("filterstate", "2005");
		        	return result;
	        	}
	        } else {
		        matcher = Pattern.compile("([0-9]+-[0-9]+-[0-9]+) ([0-9]+:[0-9]+)",Pattern.MULTILINE | Pattern.DOTALL).matcher(aDateTimeClue);
		        if(! matcher.find()) {
		        	matcher = Pattern.compile(">([^<]* 전)", Pattern.MULTILINE | Pattern.DOTALL).matcher(aDateTimeClue);
					if(! matcher.find()) {
						//TODO: 날짜를 못 뽑은...error.
						result.put("filterstate", "2005");
			        	return result;
					} else {
						String strDate = matcher.group(1);
						List<String> lsDateTime = translateDate(strDate);
						if(lsDateTime.size() == 0) { 
							result.put("filterstate", "2005");
				        	return result;
						} else { 
							result.put(inx + ",a_date", lsDateTime.get(0));
							result.put(inx + ",a_time", lsDateTime.get(1));
						}
					}
		        } else { 
		        	result.put(inx + ",a_date", matcher.group(1));
					result.put(inx + ",a_time", matcher.group(2));
		        }
	        }
	        result.put(inx + ",a_bodyhtml", a_bodyhtml);
		}
		
		if(answerLength == 0) {
			this.answerCount = 0;
		}
		String[] sectionInfos = result.get("s_allsection").split("<span class=\"bG12\">&gt;</span>");
		if(sectionInfos.length > 1) { 
			result.put("section", sectionInfos[0]);
			result.put("topcategory", sectionInfos[1]);
			if (sectionInfos.length > 2) { 
				String tmpSubCategory = sectionInfos[2];
				for(int inx = 3 ; inx < sectionInfos.length ; inx++) {
					tmpSubCategory = tmpSubCategory + sectionInfos[inx];
				}
				result.put("subcategory", tmpSubCategory);
			}
		} else { 
			result.put("filterstate", "2006");
        	return result;
		}
		
		result.put("filterstate", "1");
		return result;
	}
	
	public FilteredData setDataObject(Map<String, String> regexpResult) throws VeniceException {
		
		//validExtractFields 와 map 결과가 일치하는 것이 있다면 그대로 setting.
		Map<String, String> filteredResult = new HashMap<String, String>();
		for(String field : this.validExtractFields) {
			if(regexpResult.containsKey(field.toLowerCase())) {
				filteredResult.put(field, HtmlParser.getTextOnly(regexpResult.get(field.toLowerCase())));
			}
		}
		
		//validExtractFields 들에 대한 처리.
		List<String> datetime = translateDate(regexpResult.get("date_time_clue"));
		filteredResult.put("questitle", HtmlParser.getTextOnly(regexpResult.get("title")));
		filteredResult.put("queswriter",HtmlParser.getTextOnly(regexpResult.get("id")));
		filteredResult.put("quesbody", HtmlParser.getTextOnly(regexpResult.get("bodyhtml")));
		filteredResult.put("quesdate", refineDate(datetime.get(0)));
		filteredResult.put("questime", datetime.get(1));
		filteredResult.put("queshits", HtmlParser.getTextOnly(regexpResult.get("readcount")));
		filteredResult.put("quesreplycnt", HtmlParser.getTextOnly(regexpResult.get("answercount")));
		filteredResult.put("anscount", HtmlParser.getTextOnly(regexpResult.get("answercount")));
		
		//TODO : 아래부부분은 모두 추가하자...
		filteredResult = setAnswerText(regexpResult, filteredResult);
		filteredResult = setSiteID(filteredResult);
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
		//validExtractFields 가 없다면... "" 로 setting. robot 요청사항..ㅡ..ㅡ
		for(String field : this.validExtractFields) {
			if(!filteredResult.containsKey(field)) {
				filteredResult.put(field, "");
			}
			if(validDigitFields.contains(field) && filteredResult.get(field).equals("")) { 
				filteredResult.put(field, "0");
			}
		}
		FilteredData fd = new FilteredData("doc", Integer.parseInt(regexpResult.get("filterstate")), filteredResult);
		return fd;
	}

	public List<String> getLstDeleteString() {
		return lstDeleteString;
	}

	public void setLstDeleteString(List<String> lstDeleteString) {
		this.lstDeleteString = lstDeleteString;
	}

	public List<FilterElement> getFilterInfo() {
		return filterInfo;
	}

	public void setFilterInfo(List<FilterElement> filterInfo) {
		this.filterInfo = filterInfo;
	}

}
