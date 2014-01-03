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

public class SimfileComputerEnd extends KnowFilter {

	private List<String> lstDeleteString = new ArrayList<String>(
			Arrays.asList("alert('게시글이 삭제되었거나 존재 하지 않습니다');", 
					"Fatal error</b>:  Call to a member function eof() on a non-object in <b>/svc/addon/drsim_new/wwwhome/qna/_inc/view.html</b>"));
	private List<FilterElement> filterInfo;
	private String key;
	
	static final Logger LOGGER = LoggerFactory.getLogger(SimfileComputerEnd.class);
	public SimfileComputerEnd() {
		this.filterInfo = new ArrayList();
		/*
		 * pattern2 가 있다면 아래를 다시한번 반복
		 */
		/* pattern 1 */
		String prefix = "<!--  새로운 질문시작-->";
		String suffix = "<!-- cpca 광고 시작 -->";
		String delim = "<td style=\"padding-bottom:10px;\">";
		// q_regexp
		String regexp = "<td class=\"tx14_black2\"[^>]*>(.*)<td class=\"tx11_black\"><b>ID :</b> ([^&]*)&nbsp; 분류" +
				"[^:]*:(.*)</b-->&nbsp;&nbsp;   조회[^:]*:(.*)&nbsp; &nbsp;  답변 : <b>([^<]*).*<!-- 질문제목 끝 -->[^<]*" +
				"<table width=\"537\" cellspacing=\"0\" cellpadding=\"0\">[^<]*<tr>[^<]*" +
				"<td class=\"tx11_black\" style=\"padding:0 0 0 10\"><b>등록일 : </b>([^ ]*) ([^ ]*).*" +
				"<td class=\"tx13_black\" style=\"padding:11px;line-height:22px;word-break:break-all;\">(.*)" ;
		List<String> fields = new ArrayList<String>(
				Arrays.asList("title", "queswriter", "topcategory", "queshits", "anscount", "quesdate", "questime", "bodyhtml"));

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
		HtmlParser.setExceptHost(new HashSet<String>(Arrays.asList("thumb", "icon", "btn", "topleft.gif", "topright.gif", "botleft.gif", 
				"botright.gif", "point_lv", "USERIMG")));
		
		/*select target */
		fe.setSectionTarget(1);
		
		/* 답변 */
		fe.setARegexp("(.*)");
		List<String> aFields = new ArrayList<String>(Arrays.asList("a_answerlist"));
		fe.setaFields(aFields);

		fe.setSRegexp("");
		List<String> sFields = new ArrayList<String>();
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

		//답변 나누기
		String[] ansList = StringUtils.splitByWholeSeparator(result.get("0,a_answerlist"), "<!-- 답변 1시작 -->");
		//selectedAnswerCount=0;
		this.answerCount = ansList.length;
		int tmpInx = 0;
		if(this.answerCount > 0) { 
			String selectedString = "<span class=\"tx14_black2\"[^>]*>(.*)<td class=\"tx11_black\"[^>]*>.*" +
					"등록일 : </b>([^ ]*) ([^ ]*) <font color=\"#7A8AAC\">\\|</font><b> id : </b>([^<]*).*" +
							"<td class=\"tx13_black\"[^>]*>(.*)<td style=\"padding-bottom:10px;\">";

			InterruptibleCharSequence ics = new InterruptibleCharSequence(ansList[0]);
			Matcher matcher = Pattern.compile(selectedString, Pattern.MULTILINE | Pattern.DOTALL).matcher(ics.toString());
			if(matcher.find()) {
				result.put("0,a_title", matcher.group(1));
				result.put("0,a_date", matcher.group(2));
				result.put("0,a_time", matcher.group(2));
				result.put("0,a_id", matcher.group(2));
				result.put("0,a_bodyhtml", matcher.group(2));
				result.put("0,a_date", StringUtils.replace(result.get("0,a_date"), ".", "-"));
				this.selectedAnswerCount++;
				tmpInx++;
			} else { 
				this.answerCount--;
			}
		}
		
		String a_regexp = "<td height=\"26\" bgcolor=\"F6F6F6\" class=\"tx14_black3\"[^>]*>(.*)<td width=\"5\" height=\"5\"[^>]*>.*" +
				"등록일 : </b>([^ ]*) ([^ ]*) <font color=\"#7A8AAC\">\\|</font><b> id : </b>([^<]*).*" +
				"<td class=\"tx13_black\"[^>]*>(.*)<td style=\"padding-bottom:10px;\">";
		InterruptibleCharSequence ics = null;
		Matcher matcher = null;
		for(int i = tmpInx ; i <this.answerCount ; i++) { 
			ics = new InterruptibleCharSequence(ansList[i]);
			matcher = Pattern.compile(a_regexp, Pattern.MULTILINE | Pattern.DOTALL).matcher(ics.toString());
			
			if(matcher.find()) {
				result.put(i+",a_title", matcher.group(1));
				result.put(i+",a_date",StringUtils.replace(matcher.group(2), ".", "-"));
				result.put(i+",a_time", matcher.group(3));
				result.put(i+",a_id", matcher.group(4));
				result.put(i+",a_bodyhtml", matcher.group(5));
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
