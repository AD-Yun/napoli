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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import search.crawl.napoli.filter.FilterElement;
import search.crawl.napoli.filter.FilteredData;
import search.crawl.napoli.util.HtmlParser;
import search.crawl.napoli.util.InterruptibleCharSequence;

public class JobKoreaComputerEnd extends KnowFilter {

	private List<String> lstDeleteString = new ArrayList<String>(
			Arrays.asList("<HTML><BODY><SCRIPT LANGUAGE=\"JavaScript\">alert(\"삭제된 글이거나 존재하지 않는 글입니다.\");"));
	private List<FilterElement> filterInfo;
	private String key;
	
	static final Logger LOGGER = LoggerFactory.getLogger(JobKoreaComputerEnd.class);
	public JobKoreaComputerEnd() {
		this.filterInfo = new ArrayList();
		/*
		 * pattern2 가 있다면 아래를 다시한번 반복
		 */
		/* pattern 1 */
		String prefix = "<!--질문보여주기--->";
		String suffix = "<!--덧글-->";
		String delim = "<div id=\"divView_Ans";
		// q_regexp
		String regexp = "<td height=\"81\" class=\"c_000 dotum b s_16 ls_1\">(.*)" + 
				"<td width=\"160\" height=\"81\" align=\"center\" class=\"s-font\">해당 질문의 총 추천점수<br>(.*)" + 
				"(<font class='font-id'>(.*))?<font class=\"date\"> &nbsp;\\|&nbsp; ([^<]*).*" + 
				"<font class=\"s-font c_555 b\">조회</font> ([0-9]+).*" + 
				"답변</font> <font class=\"c_pink b\">([0-9]+).*<!--글내용-->(.*)<!--글내용//-->";
		List<String> fields = new ArrayList<String>(
				Arrays.asList("title", "quesrating", "-", "quesid", "quesdate", "quesreadcount", "quesanswercount", "bodyhtml"));

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
		HtmlParser.setExceptHost(new HashSet<String>(Arrays.asList("icon", "btn", "question")));
		
		/*select target */
		fe.setSectionTarget(0);
		
		/* 답변 */
		fe.setARegexp("(.*)<td height=\"81\" class=\"c_000 dotum b s_16 ls_1\">(.*)" + 
		"답변 추천하기<img src=\"/images/knowledge/icon_arrow.gif\"[^>]*>(.*)" + 
				"<td height=\"15\" valign=\"bottom\" class=\"s_11 dotum ls_1 c_gray2\">선택해 주세요(.*)(<font class='font-id'>(.*))?" + 
		"<font class=\"date\"> &nbsp;\\|&nbsp; ([^<]*).*" + 
				"<td height=\"30\" align=\"right\">(.*)<!--글내용//-->");
		List<String> aFields = new ArrayList<String>(Arrays.asList("a_answer_clue", "a_title", "a_rating", 
				"a_level_clue", "-", "a_id", "a_date", "a_bodyhtml"));
		fe.setaFields(aFields);

		fe.setSRegexp("HOME</a> > <a href=\"/Knowledge/KM_Main.asp\">(.*)<!-- 각페이지별 내용시작 -->");
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
		result = super.regexpPostprocessing(result);
		int retState = 1;
		result.put("section", "질문과답변");
		result.put("quesdate", StringUtils.replace(result.get("quesdate"), ".", "-"));
		result.put("questime", "00:00:00"); 
		result.put("s_allsection", HtmlParser.getTextOnly(result.get("s_allsection")));
		String[] sec = StringUtils.splitByWholeSeparator(result.get("s_allsection"),">");
		if(sec.length > 1) { 
			result.put("section", sec[0]);
			result.put("topcategory", sec[1]);
			String tmpSubCategory = sec[2];
			for(int inx = 3 ; inx < sec.length ; inx++) {
				tmpSubCategory = tmpSubCategory + " > " + sec[inx];
			}
			result.put("subcategory", tmpSubCategory);
		} else { 
			result.put("filterstate", "2006");
        	return result;
		}
		//답변 나누기
		for(int inx = 0 ; inx < this.answerCount ; inx++) {
			if(result.get(inx+",a_answer_clue").indexOf("table_choice_top.gif" ) > -1) {
				this.selectedAnswerCount++;
			}
			if(result.get(inx+",a_level_clue").indexOf("level_14.gif" ) > -1) {
				result.put("state", "s");
			}
		}
		result.put("filterstate", "1");
		return result;
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
