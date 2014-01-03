package search.crawl.napoli.filter.blog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import search.crawl.napoli.filter.FilterElement;

public class Naver extends BlogFilter {

	private List<String> lstDeleteString;
	private List<FilterElement> filterInfo;
	
	public Naver() {
		this.filterInfo = new ArrayList();
		/*
		 * pattern2 가 있다면 아래를 다시한번 반복
		 */
		/* pattern 1 */
		String prefix = "<head>";
		String suffix = "<div class=\"content3\">";
		
		String regexp = "(.*)<span class=\"pcol1 itemSubjectBoldfont\">(.*)</span>[^<]*(.*)<span class=\"cate pcol2\">" 
				+ "(.*)<p class=\"date fil5 pcol2 _postAddDate\">([0-9][0-9][0-9][0-9]/[0-9]+/[0-9]+) ([0-9]+:[0-9]+)</p>[^<].*<div class=\"clear blank5\"></div>" 
				+ "[^<]*<p class=\"post_option\">([^<]*<a href[^>]+>[^<]*<span class=\"[^\"]+\"> " 
				+ "\\(<em>[^<]*</em>\\)</span></a>)?(.*)<input type=text class=\"tag_inp\" title=\"태그를 입력해 주세요\" value=\"\" />.*";
		
		List<String> fields = new ArrayList<String>(
				Arrays.asList("metasearch", "title", "searchallow", "category", "date", "time", "-", "body"));

		//regexp pattern compile setting....
		Pattern pattern = Pattern.compile(regexp, Pattern.MULTILINE | Pattern.DOTALL);
		
		FilterElement fe = new FilterElement(regexp, fields, pattern, prefix, suffix);
		filterInfo.add(fe);
		
		/* pattern 2 */
		prefix = "";
		suffix = "<p class=\"postre\">";

		regexp = "(.*)var guideMessage = (.*)"
				+ "//<!-- 제목 -->[^<]+<table class=\"post-top\"><tr><td valign=\"bottom\">[^>]+"
				+ "<div class=\"htitle\">[^<]*"
				+ "(<img src='http://blogimgs.naver.net/imgs/head/[^']+gif' border='0'/>|<img src='http://blogimgs.naver.net/imgs/head/[0-9]+.gif' border='0'/>)?.*"
				+ "//?"
				+ "(<a href='http://mobile.naver.com/phonenaver/blog.nhn' target='_blank'><img src='http://blogimgs.naver.net/imgs/head/p[0-9_a-z]+.gif' border='0'/></a>[^<]+)?"
				//isscraped title"
				+ "<span class=\"pcol1 itemSubjectBoldfont\">(<img src=\"http://blogimgs.naver.net/nblog/ico_scrap[0-9]+.gif\" class=\"i_scrap\" width=\"50\" height=\"15\" alt=\"[^\"]+\" />)?([^<]*)</span>[^<]*"
				+ "//?"
				+ "(<img src=\"http://blogimgs.naver.net/nblog/00ico_lock_p_2.gif\" class=\"key\" width=\"9\" height=\"11\" alt=\"비공개\" />[^<]+)?"
				//category -"
				+ "<span class=\"cate pcol2\">[^<]*"
				+ "<img src=\"http://blogimgs.naver.net/imgs/nblog/spc.gif\" class=\"pcol2b fil3\" width=\"1\" height=\"11\"[^>]+>[^<]*"
				+ "(.*)"
				+ "<img src=\"http://blogimgs.naver.net/imgs/nblog/spc.gif\" width=\"105\" height=\"1\"[^>]+>[^<]*</span>[^<]+"
				+ "</div>[^<]+"
				//datetime"
				+ "<p class=\"date fil5 pcol2\">([0-9][0-9][0-9][0-9]/[0-9][0-9]/[0-9][0-9] [0-9][0-9]:[0-9][0-9])?</p>[^<]+"
				+ "<p class=\"fil3 dline\"></p>[^<]*"
				+ "<p class=\"url\".*"
				+ "<a href=\"[^\"]+\" target=\"_top\" class=\"fil5 pcol2\">[^<]+</a>[^<]*</p>.*"
				+ "<p class=\"post_option\">.*"
				//filebody"
				+ "(<img src=\"http://blogimgs.naver.net/imgs/nblog/spc.gif\" class=\"fil3 pcol2b\" width=\"1\" height=\"11\" alt=\"\" id=\"sep_[0-9]+\"/>[^<]+"
				+ "<a href=\"#\" onclick=\"showFileLayer\\( this, '1' \\);return false;\" class=\"pcol2\">첨부파일 <span class=\"pcol3\">\\(<em>[0-9]*</em>\\)</span></a>[^<]+)?"
				+ "</p>[^<]*"
				+ "<div class=\"post-sub ptr\" id=\"[a-zA-Z0-9_-]+\" style=\"display:none;\">[^<]*"
				+ "<p><span class=\"pcol2\" id=\"[a-zA-Z0-9_-]+\"></span></p>[^<]*"
				+ "</div>.*"
				//isscraped2"
				+ "(<!-- delete something \\| [0-9]+ AjaxUI -->[^<]+"
				+ "<div class=\"blank clear\"></div><div class=\"post-sub ptl\"><img src=\"http://blogimgs.naver.net/blog20/blog/ico_origin.gif\" width=\"32\" height=\"17\" alt=\"출처\"> .+</div>[^<]+|<img src=\"http://blogimgs.naver.net/blog20/blog/ico_origin.gif\" width=\"32\" height=\"17\" alt=\"출처\"> <b class='s_link'>비정상적으로 스크랩되어 출처가 누락되었습니다.</b><br>[^<]+)?"
				+ "<div id=\"post-view[0-9]*\" class=\"post-view pcol2\">"
				//body
				+ "(.+)"
				+ "</div>[^<]*"
				+ "<div class=\"post_footer_contents\">.*"
				+ "<div class=\"post-btn\">[^<]*"
				+ "<p class=\"postedit\">[^<]*"
				//scrapallow
				+ "(<span id=\"post-scrap-text_[0-9]*\" class=\"move\">.*"
				+ "이 포스트를..</a>[^<]*)?";
		
		fields = new ArrayList<String>(
				Arrays.asList("metasearch", "innerfile", "-", "-", "isscraped", "title", "-", "category", "datetime", "filebody", "isscraped2", "body", "scrapallow"));
		//regexp pattern compile setting....
		pattern = Pattern.compile(regexp, Pattern.MULTILINE | Pattern.DOTALL);
		fe = new FilterElement(regexp, fields, pattern, prefix, suffix);
		filterInfo.add(fe);
	}
	
	public int regexpPreprocessing() {
		int ret = super.regexpPreprocessing();
		if(ret != 1) {
			return ret;
		}
		String html = super.getHtmlInfo().getHtmlSource();
		if(html.length() < 500 && html.indexOf("top.location.href = '") > -1) { 
			// return 값에 대한 문제는..??? 어떻게 해야하나????
			return 2;
		}
		if(html.indexOf("네이버 블로그 서비스 긴급 점검 중입니다.") > -1) { 
			return 312;
		}
		return 1;
    }
	/*
	 * 블로그 공통 정규식 후처리는 아래에서 구현하고 
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
		int matchedRegIndex = super.getMatchedRegIndex();
		if(matchedRegIndex == 0) {
			result =  firstPostprocessing(result);
		} else { 
			result =  secondPostprocessing(result);
		}
		return result;
	 }
	private Map<String, String> firstPostprocessing(Map<String, String> result)  { 
		int retState = 1;
		
		// 정규식 처리후 데이터처리.		
		String regexp = "(.*)&lt;INPUT class=\"tag_inp\" title=\"태그를 입력해 주세요\" value=\"\" /> .*";
		Pattern p = Pattern.compile(regexp, Pattern.MULTILINE | Pattern.DOTALL);
		Matcher matcher = p.matcher(result.get("body"));
		String bodyHtml = result.get("body");
		if(matcher.find()) {
			result.put("body", bodyHtml);
			bodyHtml = matcher.group(1);
		}

		// event.
		if(bodyHtml.indexOf("http://blogimgs.naver.net/imgs/icon_hit_event.gif") > -1 
				|| bodyHtml.indexOf("http://blogimgs.naver.net/imgs/icon_birth.gif") > -1
				|| bodyHtml.indexOf("http://blogimgs.naver.net/nblog/ico_mrblog.gif") > -1
				|| bodyHtml.indexOf("http://blogimgs.naver.net/imgs/happybean/ico_happybean.gif") > -1) {
			if(bodyHtml.indexOf("생일") > -1 || bodyHtml.indexOf("이벤트") > -1 
					|| bodyHtml.indexOf("Mr.Blog") > -1)  {
				result.put("event", "1");
			} else { 
				result.put("event", "1");
			}
		} else { 
			result.put("event", "0");
		}
		
		//search allow
		if(result.get("searchallow").indexOf("http://blogimgs.naver.net/nblog/ico_sopen2.gif") > -1) {
			result.put("searchallow", "0");
		} else if(result.get("metasearch").indexOf("<meta name=\"robots\" content=\"noindex,nofollow\"/>") > -1) { 
			result.put("searchallow", "0");
		} else {
			result.put("searchallow", "1");
		}
		
		regexp = "^([0-9]+)/([0-9]+)/([0-9]+)$";
		p = Pattern.compile(regexp, Pattern.MULTILINE | Pattern.DOTALL);
		matcher = p.matcher(result.get("date"));
		String tmpDate = "";
		if(matcher.find()) {
			tmpDate = String.format("%s-%s-%s ", matcher.group(1), matcher.group(2), matcher.group(3));
			result.put("date",  tmpDate);
		}
		
		
		regexp = "^([0-9]+):([0-9]+)$";
		p = Pattern.compile(regexp, Pattern.MULTILINE | Pattern.DOTALL);
		matcher = p.matcher(result.get("time"));
		
		if(matcher.find()) {
			tmpDate = String.format("%s:%s:00", matcher.group(1), matcher.group(2));
			result.put("time",  tmpDate);
		} else { 
			result.put("time",  "00:00:00");
		}

		result.put("filterstate", "1");
		return result;
	}
	
	private Map<String, String> secondPostprocessing(Map<String, String> result)  { 
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
