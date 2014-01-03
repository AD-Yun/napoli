package search.crawl.napoli.filter.blog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import search.crawl.napoli.filter.FilterElement;

public class ListNaver extends BlogListFilter {

	private List<String> lstDeleteString;
	private List<FilterElement> filterInfo;
	
	public ListNaver() {
		/*
		 * pattern2 가 있다면 아래를 다시한번 반복
		 */
		/* pattern 1 */
		String regexp = "(.*)<h5><a href=\"http://([^\"]*)\" target=\"_blank\".*" 
				+ "<span class=\"date\">([0-9][0-9][0-9][0-9].[0-9][0-9].[0-9][0-9]) ([0-9][0-9]:[0-9][0-9]).*" 
				+ "<input type=\"hidden\" name=\"blogId\" value=\"([^\"]*)\" class=\"vBlogId\" />.*" 
				+ "<input type=\"hidden\" name=\"logNo\" value=\"([^\"]*)\" class=\"vLogNo\" />";
		List<String> fields = new ArrayList<String>(
				Arrays.asList("checkexcep", "url", "date", "time", "user_id", "post_no"));
		String prefix = "<div class=\"list_area\">";
		String suffix = "<div class=\"paginate\">";
		String delimiter = "<h5><a href=";
		/* 
		 * regexp pattern compile setting....
		 */
		Pattern pattern = Pattern.compile(regexp, Pattern.MULTILINE | Pattern.DOTALL);
		
		FilterElement fe = new FilterElement(regexp, fields, pattern, prefix, suffix, delimiter);
		this.filterInfo = new ArrayList();
		filterInfo.add(fe);
	}
	/*
	 * 블로그 공통 정규식 후처리는 아래에서 구현하고 
	 * 사이트별 후처리는 사이트별 filter에서 처리하자
	 * @see search.crawl.napoli.filter.Filter#filterPostprocessing()
	 */
	public Map<String, String> filterPostprocessing(Map<String, String> result)  { 
		int retState = 1;
        String sHost = "blog.naver.com";
        String userid = "";
        String postno = "";
        String pUrl = "";
        //set rField($nI,pUrl)   ""

		// 정규식 처리후 데이터처리
        String regexp = "blog.naver.com/([^\\?]*)\\?Redirect=Log&logNo=(.*)";
		Pattern p = Pattern.compile(regexp, Pattern.MULTILINE | Pattern.DOTALL);
		Matcher matcher = p.matcher(result.get("url"));

		if(matcher.find()) {
			userid = matcher.group(1);
			postno = matcher.group(2);
		} else { 
			regexp = "([^\\.]+).blog.me/(.*)";
			p = Pattern.compile(regexp, Pattern.MULTILINE | Pattern.DOTALL);
			matcher = p.matcher(result.get("url"));
			if(matcher.find()) {
				userid = matcher.group(1);
				postno = matcher.group(2);
			}
		}
		
		regexp = "^([0-9]*)&.*";
		p = Pattern.compile(regexp, Pattern.MULTILINE | Pattern.DOTALL);
		matcher = p.matcher(postno);
		if(matcher.find()) {
			postno = matcher.group(1);
		}
		
		if(!userid.equals("") && !postno.equals("")) { 
			//Pattern.compile(regex).matcher(str).replaceAll(repl)
			userid = userid.replaceAll("/[0-9]+", "");
			result.put("host", sHost);
			result.put("path", "/" + userid +"/" + postno);
			
			if (result.get("checkexcep").indexOf("ico_memolog.gif") >-1) {
				result.put("filterstate", "0");
				retState = 0;
			} else if(result.get("checkexcep").indexOf("ico_lifelog.gif") >-1) { 
				result.put("filterstate", "0");
				retState = 0;
			}

		} else { 
			result.put("host", sHost);
			result.put("path", "/" + userid +"/" + postno);
            pUrl = result.get("url");
		}
		
		regexp = "^([^/]*)/";
		p = Pattern.compile(regexp, Pattern.MULTILINE | Pattern.DOTALL);
		matcher = p.matcher(pUrl);
		if(matcher.find()) {
			result.put("p_url", matcher.group(1));
		} else { 
			result.put("p_url", pUrl);
		}
		
		if (retState == 1) { 
			result.put("filterstate", "1");
		}
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
