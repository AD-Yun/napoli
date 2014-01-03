package search.crawl.napoli.filter.know;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import search.crawl.napoli.common.VeniceException;
import search.crawl.napoli.common.VeniceEnums.Errors;
import search.crawl.napoli.filter.FilterElement;
import search.crawl.napoli.util.InterruptibleCharSequence;

public class SimfileList extends KnowListFilter {

	private List<String> lstDeleteString;
	private List<FilterElement> filterInfo;
	private int currentPageNum = 0; 
	private int initPageNum = 1; 
	public SimfileList() {
		this.currentPageNum = initPageNum;
		this.filterInfo = new ArrayList<FilterElement>();
		/*
		 * pattern2 가 있다면 아래를 다시한번 반복
		 */
		/* pattern 1 */
		String prefix = "<td width=\"54\" class=\"black\">상태</td>";
		String suffix = "<table border='0' cellspacing='0' cellpadding='3'>";
		String delim = "<table width=\"537\" cellspacing=\"0\" cellpadding=\"0\">";
		// q_regexp
		String regexp = "<a href=\"([^\"]*)" ;
		List<String> fields = new ArrayList<String>(Arrays.asList("args"));
		
		Pattern pattern = Pattern.compile(regexp, Pattern.MULTILINE | Pattern.DOTALL);
		
		/*
		 * opneknow 에서는 KnowFilterElement 를 생성해야한다. 
		 * KnowFilterElement 내부에 있는 것은 마지막 자손 filter class 에서만 사용(?) 된다.
		 * a_regexp, s_regexp 기타 등등.
		 */
		KnowFilterElement fe = new KnowFilterElement(regexp, fields, pattern, prefix, suffix, delim);		
		filterInfo.add(fe);	
		/* pattern 1 완료 */
	}
	
	public int regexpPreprocessing() {
		int ret = super.regexpPreprocessing();
		if(ret != 1) {
			return ret;
		}
		
		ret = deleteStringProcessing(this.lstDeleteString);
		if(ret != 1) {
			return ret;
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
	
	@Override
	public String getNextListUrl(String url) throws VeniceException {
		if (this.currentPageNum < 2) {
			this.currentPageNum++;
			return url;
		}

		String html = getHtmlInfo().getHtmlSource();
		StringBuilder currentUrl = new StringBuilder(url + "&page=" + this.currentPageNum);
		try { 
			String pagedown;
			if (this.currentPageNum % 10 == 1) {
				pagedown = "next";
			} else {
				pagedown = "jump";
			}
	
			String prefix = "<table border='0' cellspacing='0' cellpadding='3'>";
			String suffix = "<table width=\"240\" border=\"0\" cellspacing=\"1\" cellpadding=\"4\" bgcolor=\"CACACA\" style=\"margin-bottom:7\">";
			int startInx = html.indexOf(prefix);
			int endInx = html.indexOf(suffix);
			html = html.substring(startInx, endInx);
			
			String regexp = "<a href='/list/question_all.php\\?[^&]*&vMinIdx=([^&]*)&vMaxIdx=([^&]*)&.*&numPerPage=([^&]*)&pagePerBlock=([^']*)"; 
			InterruptibleCharSequence ics = new InterruptibleCharSequence(html);
			Matcher matcher = Pattern.compile(regexp, Pattern.MULTILINE | Pattern.DOTALL).matcher(ics.toString());
			if(! matcher.find()) {
			} else {
				String nMin = matcher.group(1);
				String nMax = matcher.group(2);
				String sPage = matcher.group(3);
				String sBlock = matcher.group(4);
				
				currentUrl.append("&vMinIdx=" + nMin + "&vMaxIdx=" + nMax + "&vPageDown=" + pagedown);
				currentUrl.append("&numPerPage=" + sPage + "&pagePerBlock=" + sBlock);
			}
			
			this.currentPageNum++;
	    } catch(Exception e) {
	    	throw new VeniceException(Errors.GET_NEXT_URL_FAILURE, e);
	    }
		return currentUrl.toString();
	}
}
