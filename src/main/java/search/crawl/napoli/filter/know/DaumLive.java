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

public class DaumLive extends KnowFilter {

	private List<String> lstDeleteString = new ArrayList<String>(
			Arrays.asList("<dd class=\"title\"><b class=\"bB14\">삭제 또는 존재 하지 않는 게시글 입니다.</b></dd>", 
					"이 정보내용은 청소년 유해매체물로서 정보통신망 이용촉진 및<br>정보보호등에 관한 법률 및 청소년보호법의 규정에 의하여<br>19세 미만의 청소이 이용할 수 없습니다.",
					"<span class=\"bOr14\">삭제되었거나 존재하지 않는</span>",
					"<b class=\"bB14\">오류가 발생하였습니다",
					"<dt class=\"bBl14_2\">권리침해신고 접수로 인해 <span class=\"bOr14\">임시 접근금지</span> 조치된 글입니다",
					"이 정보는 청소년 유해매체물 또는 청소년에게 제공하기 부적합한 <br />",
					"<dt class=\"fw_b\">삭제되었거나 존재하지 않는 게시글 입니다.</dt>",
					"<dt class=\"fw_b line2\">권리침해신고 접수로 인해<br>임시 접근금지 조치된 글입니다.",
					"<p class=\"title\">오류가 발생하였습니다.<br /></p>"));
	private List<FilterElement> filterInfo;
	
	static final Logger LOGGER = LoggerFactory.getLogger(DaumLive.class);
	public DaumLive() {
		this.filterInfo = new ArrayList();
		/*
		 * pattern2 가 있다면 아래를 다시한번 반복
		 */
		
		/* pattern 1 */
		String prefix = "<div class=\"btnArea\">";
		String suffix = "<!-- // qnaReadContent -->";
		String delim = "<div id=\"answer_";
		
		// q_regexp
		String regexp = "<span class=\"tit\">(.*)<span class=\"toAge\">([^<]*).*<span class=\"writer\">.*class=\"bG11\" >([^<]*)";
		// q_fields
		List<String> fields = new ArrayList<String>(Arrays.asList("q_title", "q_date_time_clue", "q_id"));
		
		Pattern pattern = Pattern.compile(regexp, Pattern.MULTILINE | Pattern.DOTALL);
		
		/*
		 * opneknow 에서는 KnowFilterElement 를 생성해야한다. 
		 * KnowFilterElement 내부에 있는 것은 마지막 자손 filter class 에서만 사용(?) 된다.
		 * a_regexp, s_regexp 기타 등등.
		 */
		KnowFilterElement fe = new KnowFilterElement(regexp, fields, pattern, prefix, suffix, delim);

		// daum_live 필터에는 ::grBodyTempl(excepthosts)가 null임
		//HtmlParser.setExceptHost(new HashSet<String>(Arrays.asList("image.hanmail.net/hanmail/", "k.daum.net/font")));
		
		/*select target */
		fe.setSectionTarget(0);
		
		/* 답변 */
		fe.setARegexp("<!-- 메달 시작 테그 -->(.*)<p class=\"title\">(.*)<p class=\"infos\".*<span class=\"toAge\">([^<]*).*<span class=\"writer\">.*class=\"bG11\"[ ]*>([^<]*)");
		List<String> aFields = new ArrayList<String>(Arrays.asList("a_selected_clue", "a_title", "a_date_time_clue", "a_id"));
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
		//TODO : 반드시 KnowFilter regexpPostprocessing 를 호출
		result = super.regexpPostprocessing(result);
		
		/*
		 * valid extract fields... - 최종 추출된 결과 field name.
		 * service/type 별로..... 다름.
		protected List<String> validExtractFields = new ArrayList<String>(Arrays.asList(
			"section", "topcategory", "subcategory", "questitle", "quesbody", "queswriter", "quesdate",	"questime", "queshits", 
			"quesreplycnt", "quespoint","quesrating", "qesratingcnt", "anscount", "ansselected", "ansothers", "ansdate", "anstime",
			"state", "allmultitag", "signature")
 		
 		protected List<String> answerElements = new ArrayList<String>(
			Arrays.asList("a_title", "a_rating", "a_ratingcount", "a_id", "a_date", "a_time", "a_bodyhtml" ));
			
 		List<String> fields = new ArrayList<String>(Arrays.asList("q_title", "q_date_time_clue", "q_id"));
		List<String> aFields = new ArrayList<String>(Arrays.asList("a_selected_clue", "a_title", "a_date_time_clue", "a_id"));
		List<String> sFields = new ArrayList<String>(Arrays.asList("s_allsection"));
		);*/
		
		// questime 강제 입력, 시간 정보가 없는 듯 -> questime 입력
		result.put("questime", "00:00");
		
		// 날짜 정보 파싱 - 매칭 패턴 하나 남기고 제거 -> quesdate 입력
		//System.out.println("q_date_time_clue" + result.get("q_date_time_clue"));
		InterruptibleCharSequence ics = new InterruptibleCharSequence(result.get("q_date_time_clue"));
		Matcher matcher = Pattern.compile("([0-9]+.[ 0-9]+.[ 0-9]+)", Pattern.MULTILINE | Pattern.DOTALL).matcher(ics.toString());
		if(matcher.find()) {
			result.put("quesdate", matcher.group(1));
		} else {
			matcher = Pattern.compile("([^<]* 전)", Pattern.MULTILINE | Pattern.DOTALL).matcher(ics.toString());
			String strDate = "";
			if(matcher.find()) {
				strDate = matcher.group(1);
			} else {
				strDate = result.get("q_date_time_clue");
				if(! strDate.trim().equals("방금")) {
					result.put("filterstate", "2004");
					return result;
				}
			}
			List<String> lsDateTime = translateDate(strDate);
			if(lsDateTime.size() == 0) { 
				result.put("filterstate", "2004");
				return result;
			} else { 
				result.put("quesdate", lsDateTime.get(0));
				result.put("questime", lsDateTime.get(1));
			}
		}
		
		// 답변 부분
		for(int inx = 0 ; inx < answerCount ; inx++) {
			// 답변도 마찬가지로 타이틀이 곧 바디 -> a_bodyhtml 입력
			result.put(inx + ",a_bodyhtml", result.get(inx + ",a_title"));
			
			//선택된 답변 카운팅
			if(result.get(inx + ",a_selected_clue").indexOf("<div class=\"medalQ\">") >= 1) {
	        	this.selectedAnswerCount += 1;
	        }
			
			// a_time 강제 입력, 시간 정보가 없는 듯 -> a_time 입력
			result.put(inx + ",a_time", "00:00");
			
			// 날짜 정보 파싱 - 매칭 패턴 하나 남기고 제거 -> a_date 입력
			ics = new InterruptibleCharSequence(result.get(inx + ",a_date_time_clue"));
			matcher = Pattern.compile("([0-9]+.[ 0-9]+.[ 0-9]+)", Pattern.MULTILINE | Pattern.DOTALL).matcher(ics.toString());
			if(matcher.find()) {
				result.put(inx + ",a_date", matcher.group(1));
			} else {
				matcher = Pattern.compile("([^<]* 전)", Pattern.MULTILINE | Pattern.DOTALL).matcher(ics.toString());
				String strDate = "";
				if(matcher.find()) {
					strDate = matcher.group(1);
				} else {
					strDate = result.get(inx + ",a_date_time_clue");
					if(! strDate.trim().equals("방금")) {
						result.put("filterstate", "2003");
						return result;
					}
				}
				List<String> lsDateTime = translateDate(strDate);
				if(lsDateTime.size() == 0) { 
					result.put("filterstate", "2003");
					return result;
				} else { 
					result.put(inx + ",a_date", lsDateTime.get(0));
					result.put(inx + ",a_time", lsDateTime.get(1));
				}
			}			
		}

		// 질문 작성자 gabage 제거 -> queswriter 입력
		String id = result.get("q_id");
		id = id.replaceAll("작성자:", "");
		result.put("queswriter", id);
		
		// 섹션 및 카테고리 정보 추출 -> section, topcategory, subcategory 입력
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

//		Set<String> set = result.keySet();
//		for(String key : set) { 
//			System.out.println("KEY : " + key);
//			System.out.println("VALUEs : " + result.get(key));
//		}
		
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
		
		// validExtractFields 들에 대한 처리.
		/*
		 이하 매핑 항목:	questitle, quesbody
		 기본 수집 항목:	a_title, a_id
		 후처리 항목:		quesdate, questime, queswriter, a_bodyhtml, a_date, a_time, section, topcategory, subcategory
		 수집 불가 항목:	queshits, quesreplycnt, anscount, "quespoint","quesrating", "qesratingcnt", "anscount", "ansselected", "ansothers", 
							"ansdate", "anstime", "state", "allmultitag", "signature"
		*/
		filteredResult.put("quesdate" , refineDate(regexpResult.get("quesdate")));
		filteredResult.put("questitle", HtmlParser.getTextOnly(regexpResult.get("q_title")));
		filteredResult.put("quesbody", HtmlParser.getTextOnly(regexpResult.get("q_title")));
		
		//TODO : 아래부부분은 모두 추가하자...
		filteredResult = setAnswerText(regexpResult, filteredResult);
		filteredResult = setSiteID(filteredResult);
		//multimediatag 처리..  무조건 추가 title, bodyhtml 주의
		filteredResult.put("quesmultitag", HtmlParser.parseMultimediaTag(regexpResult.get("q_title"), HtmlParser.getTextOnly(regexpResult.get("q_title"))));
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
