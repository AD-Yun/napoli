package search.crawl.napoli.filter.know;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import search.crawl.napoli.filter.ListFilter;

public class KnowListFilter extends ListFilter {
	static final Logger LOGGER = LoggerFactory.getLogger(KnowListFilter.class);
	private List<String> lstDeleteString;
	private List<FilterElement> filterInfo;
	/**
	 * valid extract fields... - 최종 추출된 결과 field name.
	 * service/type 별로..... 다름.
	 */
	/*protected List<String> validExtractFields = new ArrayList<String>(
			Arrays.asList("args", "eid", "title", "writer", "regdate"));
*/	protected List<String> validExtractFields = new ArrayList<String>(Arrays.asList("host", "path", "siteid", "sectionid", "bodyfilter"));

	public String getNextListUrl(String url) throws VeniceException { 
		return "";
	}
	/*
	 * 블로그 공통 정규식 전처리는 아래에서 진행하고 
	 * 사이트별 후처리를 사이트별 filter에서 처리하자.
	 * 일종의 hook 와 같다. 구현을 하지 않아도 상관 없는....
	 * @see search.crawl.napoli.filter.Filter#regexpPreprocessing()
	 */
	public int regexpPreprocessing() { 
		int ret = super.regexpPreprocessing();
		return ret;
	}
	
	/*
	 * 블로그 공통 정규식 후처리는 아래에서 구현하고 
	 * 사이트별 후처리는 사이트별 filter에서 처리하자
	 * 일종의 hook 와 같다. 구현을 하지 않아도 상관 없는....
	 * @see search.crawl.napoli.filter.Filter#regexpPostprocessing()
	 */
	public Map<String, String> regexpPostprocessing(Map<String, String> ins) { 
		return ins;
	}
	
	private Map<String, String> getUrlInfo(String sArg) {
		String path = "";
		String host = "";
		String siteId = "";
		String sectionID = "";
		Map<String, String> resultValue = new HashMap<String, String>();
		switch(getFilterName()) { 
			case "paran_ques" : {
				path = "/sknow/queview.php?que=" + sArg;
				host = "ksea.paran.com";
				siteId = "4";
				break;
			}
			case "paran_worry" : {
				path = "/sworry/queview.php?que=" + sArg;
				host = "ksea.paran.com";
				siteId = "4";
				break;
			}
			case "paran_wisdom" : {
				path = "/swisdom/queview.php?que=" + sArg;
				host = "ksea.paran.com";
				siteId = "4";
				break;
			}
			case "paran_extraordin" : {
				path = "/snobrain/queview.php?que=" + sArg;
				host = "ksea.paran.com";
				siteId = "4";
				break;
			}
			case "hmall" : {
				path = "/pf/top/qa_detail?eid=" + sArg;
				host = "ks.hmall.com";
				siteId = "9";
				break;
			}
			case "gseshop" : {
				path = "/know/qna/view.gs?bbs_id=qna&message_id=" + sArg;
				host = "www.gseshop.co.kr";
				siteId = "10";
				break;
			}
			case "interpark" : {
				path = "/shoptalk/review/reviewContent.do?" + sArg;
				host = "knowhow.interpark.com";
				siteId = "11";
				break;
			}
			case "yahoo_wikidic" : {
				path = sArg;
				host = "kr.ks.yahoo.com";
				siteId = "1";
				break;
			} 
			case "empas_kdic" : {
				path = "/kdic/" + sArg;
				host = "kdaq.empas.com";
				siteId = "2";
				break;
			}
			case "empas_kpart" : {
				path = "/kpart/" + sArg;
				host = "kdaq.empas.com";
				siteId = "2";
				break;
			}
			case "freechal" : {
				path = "/Qna/ViewArticle.asp?docid=" + sArg;
				host = "iq.freechal.com";
				siteId = "5";
				break;
			}
			case "jobkorea_newqna_ques" : { 
				path = "/Knowledge/QA_View.asp?Q_Idx=" + sArg;
				host = "www.jobkorea.co.kr";
				siteId = "17";
				break;
			}
			case "kita_ques" : 
			case "kita" : { 
				String kita0 = "";
				String kita1 = "";
				String kita2 = "";
				String kita3 = "";
				String regexp = "<td class=\"title\">[^<]*<a href=\"([^\"]*)";
				Matcher matcher = Pattern.compile(regexp, Pattern.MULTILINE | Pattern.DOTALL).matcher(sArg);
				if(! matcher.find()) {
					//TODO error 처리..ㅡ.ㅡ
				} else {
					String tempBody = matcher.group(1);
					regexp = "([^\\?]*)\\?n_index=([^&]*)&cmd_id=([^&]*).*catename=([^&]*)&n_dirid=";
					Matcher matcher2 = Pattern.compile(regexp, Pattern.MULTILINE | Pattern.DOTALL).matcher(tempBody);
					if(! matcher2.find()) {
						regexp = "<td class=\"title\">.*<td class=\"title\">[^<]*<a href=\"([^\"]*)";
						Matcher matcher3 = Pattern.compile(regexp, Pattern.MULTILINE | Pattern.DOTALL).matcher(tempBody);
						if(! matcher3.find()) {
							//TODO error 처리..ㅡ.ㅡ
						} else { 
							regexp = "([^\\?]*)\\?n_index=([^&]*)&cmd_id=([^&]*).*catename=([^&]*)&n_dirid=";
							Matcher matcher4 = Pattern.compile(regexp, Pattern.MULTILINE | Pattern.DOTALL).matcher(tempBody);
							if(! matcher4.find()) {
								//TODO error 처리..ㅡ.ㅡ
							} else { 
								kita0 = matcher4.group(1);
								kita1 = matcher4.group(2);
								kita2 = matcher4.group(3);
								kita3 = matcher4.group(4);
							}
						}
					} else { 
						kita0 = matcher2.group(1);
						kita1 = matcher2.group(2);
						kita2 = matcher2.group(3);
						kita3 = matcher2.group(4);						
					}
				}
				path = "/jsp/wiki/" + kita0 + "?n_index=" + kita1 + "&cmd_id=" + kita2 + "&catename=" + kita3;
				host = "www.kita.net";
				siteId = "15";
				break;
            }
			case "jisiklog" :
				path = sArg ;
				host = "www.jobkorea.co.kr";
				siteId = "18";
				break;
			case "daum" :
			case "daum_kdic" :
			case "daum_report_univ" :
			case "daum_report_biz" :
			case "daum_report_normal" :	
			case "daum_ques" :
			case "daum_sindong" :
			case "daum_poll" :
				path = sArg ;
				host = "k.daum.net";
				siteId = "6";
				sectionID = "1";
				break;
			case "simfile" :
			case "simfile_ques" :
			case "simfile_unsolve" :
				path = sArg ;
				host = "dr.simfile.chol.com";
				siteId = "13";
				sectionID = "1";
				break;
			default :
				path = sArg ;
		}
		resultValue.put("host", host);
		resultValue.put("path", path);
		resultValue.put("siteid", siteId);
		resultValue.put("sectionid", sectionID);
		return resultValue;
	}
	/*
	 * 블로그는 동일한 DB 스크마를 이용하므로 아래 method 에서 처리 db 를 위한 
	 * 데이터 처리를 하자.
	 * 혹, 하위 클래스에서 해야한다면 overriding 하도록 하자.
	 * @see search.crawl.napoli.filter.Filter#setDataObject()
	 */
	public FilteredData setDataObject(List<Map<String, String>> regexpResult) throws VeniceException {
		List<Map<String, String>> lstData = new ArrayList<Map<String, String>>();
		
		//db field 와 map 결과가 일치하는 것이 있다면 그대로 setting.
		String path = "";
		String host = "";
		Map<String, String> urlInfo = new HashMap<String, String>();
		for(Map<String, String> map : regexpResult) { 
			Map<String, String> resultMap = new HashMap<String, String>();
			urlInfo = getUrlInfo(map.get("args"));
			for(String field : this.validExtractFields) {
				resultMap.put(field, urlInfo.get(field.toLowerCase()));
				if(map.containsKey(field.toLowerCase())) {
					resultMap.put(field, map.get(field.toLowerCase()));
				}
			}
			resultMap.put("bodyfilter", getBodyTemplateName("http://" + resultMap.get("host") + resultMap.get("path")));
			lstData.add(resultMap);
		}
		//filtering 결과 이외의 데이터는 filter 이후 setting.
		FilteredData fd = new FilteredData("list", 1, lstData);
		return fd;
	}
	
	@Override
	public String getBodyTemplateName(String inUrl) throws VeniceException {
		String templName = "";
		URL url = null;
		try {
			url = new URL(inUrl);
		} catch (MalformedURLException e) {
			LOGGER.error("MalformedURLException : URL" + inUrl);
			throw new VeniceException(VeniceEnums.Errors.EXTRACTURL_IS_MALFORMEDURL, e);
		}
		String host = url.getHost();
		String path = url.getPath() + "?" + url.getQuery();
		
		if(host.matches(".*simfile.*")) {
			templName = "simfile_computer-end";
		} else if(host.matches(".*jobkorea.*")) {
			templName = "jobkorea_computer-end";
		} else { 
			//daum...
			if (path.matches(".*/qna/view.html.*")) { 
				templName = "daum_computer-end";
			} else if(path.matches(".*/qna/openknowledge/view.html.*")) {
				templName = "daum_open-know";
			} else if(path.matches(".*/qna/file/view.html.*")) {
				templName = "daum_report";
			} else if(path.matches(".*/qna/sindongkin/view.html.*")) {
				templName = "daum_sindong";
			} else if(path.matches(".*/qna/poll/view.html.*")) {
					templName = "daum_poll";
			} else if(path.matches(".*/qna/mini/view.html.*")) {
				templName = "daum_mini";
			} else if(path.matches(".*/qna/live/view.html.*")) {
				templName = "daum_live";
			} else { 
				templName = "daum_computer-end";
			}
		}
		return templName;
	}
}
