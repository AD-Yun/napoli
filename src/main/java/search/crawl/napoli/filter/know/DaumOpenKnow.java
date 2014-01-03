package search.crawl.napoli.filter.know;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

public class DaumOpenKnow extends KnowFilter {

	private List<String> lstDeleteString = new ArrayList<String>(
			Arrays.asList("<dd class=\"title\"><b class=\"bB14\">삭제 또는 존재 하지 않는 게시글 입니다.</b></dd>",
					"이 정보내용은 청소년 유해매체물로서 정보통신망 이용촉진 및<br>정보보호등에 관한 법률 및 소년보호법의 규정에 의하여<br>19세 미만의 청소년이 이용할 수 없습니다.",
					"<span class=\"bOr14\">삭제되었거나 존재하지 않는</span>",
					"<b class=\"bB14\">오류가 발생하였습니다",
					"<dt class=\"bBl14_2\">권리침해신고 접수로 인해 <span class=\"bOr14\">임시 접근금지</span> 조된 글입니다",
					"이 정보는 청소년 유해매체물 또는 청소년에게 제공하기 부적합한 <br />",
					"<dt class=\"fw_b\">삭제되었거나 존재하지 않는 게시글 입니다.</dt>",
					"<dd class=\"txtArea\">이글은 본인 또는 Daum 지식 운영자에 의해 삭제 되었거나, Daum지식에 <br> 존재하지 않는 게시글입니다.<br>관련하여 문의사항 있으시면 언제든지 고객센터로 문의해주시길 바랍니다.</dd>",
					"<dt class=\"fw_b line2\">권리침해신고 접수로 인해<br>임시 접근금지 조치된 글입니다."));

	private List<FilterElement> filterInfo;
	
	static final Logger LOGGER = LoggerFactory.getLogger(DaumOpenKnow.class);
	public DaumOpenKnow() {
		this.filterInfo = new ArrayList();
		/*
		 * pattern2 가 있다면 아래를 다시한번 반복
		 */
		/* pattern 1 */
		String prefix = "<div id=\"content8\">";
		String suffix = "<dd class=\"qBtm clearfix\">";
		String delim = "<dd class=\"qContent\">";
		
		/* 질문 */
		String regexp = "<p class=\"title\">(.*)<p class=\"etc\">(.*)";
		List<String> fields = new ArrayList<String>(
				Arrays.asList("title", "id_readcount_datetime"));
		
		Pattern pattern = Pattern.compile(regexp, Pattern.MULTILINE | Pattern.DOTALL);
		
		/*
		 * opneknow 에서는 KnowFilterElement 를 생성해야한다. 
		 * KnowFilterElement 내부에 있는 것은 마지막 자손 filter class 에서만 사용(?) 된다.
		 * a_regexp, s_regexp 기타 등등.
		 */
		KnowFilterElement fe = new KnowFilterElement(regexp, fields, pattern, prefix, suffix, delim);
		
		HtmlParser.setExceptHost(new HashSet<String>(Arrays.asList("")));
				
		/*select target */
		fe.setSectionTarget(1);
		
		/* 답변 */
		fe.setARegexp("<td class=\"tx-content-container\">(.*)<!-- 첨부파일 -->");
		List<String> aFields = new ArrayList<String>(Arrays.asList("a_bodyhtml"));
		fe.setaFields(aFields);

		/* 섹션 */
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
		//String bodyhtml = result.get("1,a_bodyhtml");
		//bodyhtml = Pattern.compile("x-SCRIPT|scr-ipt|XSCRIPT", Pattern.CASE_INSENSITIVE).matcher(bodyhtml).replaceAll("script");
		//result.put("bodyhtml", bodyhtml);

		// id_readcount_datetime
		String[] bodyInfos = result.get("id_readcount_datetime").split("<span class=\"bar\">\\|</span>");
		String readcount = "";
		String bodyhtml = "";
		
		/* bodyhtml */
		result.put("bodyhtml",bodyhtml);
		
		/* 작성자 */
		String tmpID = bodyInfos[0];
		tmpID = tmpID.replaceAll("작성자:","");
		tmpID = tmpID.replaceAll("엑스퍼트</b> :","");
		result.put("id", tmpID);

	    /* date time */
	    String qDateTime = bodyInfos[1];
    
	    InterruptibleCharSequence ics = new InterruptibleCharSequence(qDateTime);
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
        
		
		/* readcount */
		matcher = Pattern.compile("<span id=\"viewCount\">([0-9]+)", Pattern.MULTILINE | Pattern.DOTALL).matcher(bodyInfos[2]);
	    if(! matcher.find()) {
	    	LOGGER.error("readcount regexp 실패");
	    } else { 
	    	readcount = matcher.group(1);
			result.put("readcount", readcount);
	    }

		
		// 답변
		String a_bodyhtml = "";
		String q_date = result.get("date");
		String q_time = result.get("time");
		int answerLength = 0;
		
		for(int inx = 0 ; inx < answerCount ; inx++) {
			this.selectedAnswerCount += 1;
			
			ics = new InterruptibleCharSequence(result.get(inx + ",a_bodyhtml"));
			a_bodyhtml = ics.toString();
			a_bodyhtml = Pattern.compile("x-SCRIPT|scr-ipt|XSCRIPT", Pattern.CASE_INSENSITIVE).matcher(a_bodyhtml).replaceAll("script");
			
			//if (inx == 0) {
			//	result.put("bodyhtml", a_bodyhtml);
			//}
	        if(a_bodyhtml.indexOf("블라인드된 답변입니다") > -1 && a_bodyhtml.length() < 350) { 
	        	a_bodyhtml = "";
	        }
	        
	        answerLength = answerLength + a_bodyhtml.length();
	        int tempInx = a_bodyhtml.indexOf("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"NKtContF\">");
	        if( tempInx > -1) { 
	        	a_bodyhtml = a_bodyhtml.substring(0,tempInx - 1);
	        }
	        result.put(inx + ",a_bodyhtml", a_bodyhtml);
	        
	        result.put(inx + ",a_date", q_date);
	        result.put(inx + ",a_time", q_time);
 
		}
		
		/* 답변 모두가 블라인드인 경우 답변수 0개로 해서 검색에서 제외 */
		if(answerLength == 0) {
			this.answerCount = 0;
		}
				
	    // 섹션 정보
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
		filteredResult.put("questitle", HtmlParser.getTextOnly(regexpResult.get("title")));
		filteredResult.put("queswriter",HtmlParser.getTextOnly(regexpResult.get("id")));
		filteredResult.put("quesbody", HtmlParser.getTextOnly(regexpResult.get("bodyhtml")));
		filteredResult.put("quesdate", regexpResult.get("date"));
		filteredResult.put("questime", regexpResult.get("time"));
		filteredResult.put("queshits", HtmlParser.getTextOnly(regexpResult.get("readcount")));
		filteredResult.put("quesreplycnt", "1");
		filteredResult.put("anscount", "1");
		
		//filteredResult.put("quesreplycnt", HtmlParser.getTextOnly(regexpResult.get("answercount")));
		//filteredResult.put("anscount", HtmlParser.getTextOnly(regexpResult.get("answercount")));
		
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
			if(! filteredResult.containsKey(field)) {
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
