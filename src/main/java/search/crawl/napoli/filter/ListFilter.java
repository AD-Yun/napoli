package search.crawl.napoli.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import search.crawl.napoli.common.HtmlInfo;
import search.crawl.napoli.common.VeniceEnums;
import search.crawl.napoli.common.VeniceException;
import search.crawl.napoli.util.HtmlParser;
import search.crawl.napoli.util.InterruptibleCharSequence;
/**
 * List Filter 조상 클래스.
 * 
 * 
 * @author n2429
 * 
 */
public abstract class ListFilter implements Filter {
	static final Logger LOGGER = LoggerFactory.getLogger(ListFilter.class);
	private String filterName = "";
	private int matchedRegIndex = 0;
	private HtmlInfo htmlInfo;
	private String targetHtml;
	private List<String> lstDeleteString;
	private List<FilterElement> filterInfo;


	/*  
	 * filter main method.
	 * WebCrawler 에서는 이 method 만 호출.
	 * example : 
	 * 	Filter ff = FilterUtil.getFilter("kyobo", "list");
	 * 	ff.veniceFilter(htmlInfo);
	 * 
	 * return 은 서비스별(혹은 사이트별) Data Object.
	 * Data Object 는 filtering 결과상태값 , DB 입력 가능한 데이터 를 모두 포함하고 있어야 함.
	 */
	public final FilteredData filtering(HtmlInfo htmlInfo) throws VeniceException {
		this.htmlInfo = htmlInfo;
		this.targetHtml = htmlInfo.getHtmlSource();
		int checkValue = checkHeaderState(this.htmlInfo.getHeaderStatus());
		if (checkValue != 1) { 
			return new FilteredData("list", checkValue);
		}
		LOGGER.info("after checkHeaderState = " + checkValue + ", URL : " + htmlInfo.getUrl());
		// filtering 전처리
		checkValue = regexpPreprocessing();
		
		if (checkValue != 1) { 
			// 문제가 있으니.... data 를 return. 하자...
			LOGGER.info("Filter Preprocessing is abnormality.");
			//checkValue 로 받은 값을 error code 값으로 보고 exception 을 발생 시키자.
			VeniceEnums.Errors er = VeniceEnums.Errors.getErrors(checkValue);
			throw new VeniceException(er, new Exception("Error occured at regexpPreprocessing"));
		}
		
		List<Map<String, String>> result = null;
		try { 
			// regularExpFilter.
			result = regularExpFilter(this.htmlInfo.getHtmlSource());
		} catch(VeniceException ve) {
			LOGGER.error("Not matched filter regular expression");
			throw new VeniceException(VeniceEnums.Errors.FILTER_REGULAR_ERROR, ve);
		}
		// filtering 후처리
		result = regexpListPostprocessing(result);
		
		List<Map<String, String>> fResult = new ArrayList<Map<String, String>>();
		for(Map<String, String> m : result) { 
			if (!m.get("filterstate").equals("1")) {
				LOGGER.error("ERROR --> Not Available......");
				//filterstate 에 담긴 코드 값을 보고 error, exception 처리를 진행
				//TODO : 1 개라도 오류가 있으면 우선 에러 처리.
				VeniceEnums.Errors er = VeniceEnums.Errors.getErrors(Integer.parseInt(m.get("filterstate")));
				LOGGER.error(er.getMessage());
				throw new VeniceException(er, new Exception("Error occured at regexpPreprocessing"));
			} else { 	
				//TODO : TEST용 logging.
				//LOGGER.debug("Filtering SUCCESS");				
				Set<String> set = m.keySet();
				//get textonly.
				for(String key : set) {
					if(m.get(key) != null) { 
						m.put(key, HtmlParser.getTextOnly(m.get(key)));
					}
				}
				fResult.add(m);
			}
		}
		
		// data object 생성, 반환.
		FilteredData fld = setDataObject(fResult);
		// data 를 setting 하고, 데이터 객체를 리턴한다. 
		return fld;
	}
	
	public int checkHeaderState(int headerState) {
		int retState = 1;		
		
		if(headerState == 200) {
		} else { 
			// 문제가 있으니.... data 를 return. 하자...
			LOGGER.info("Header STATUS is Not 200 : value = " + headerState);
			retState = headerState;
		}
		
		return retState;
	}
	
	/* 
	 * 정규식 처리를 하기전 해야할 일들을 처리한다.
	 * Filter 클래스를 상속한 클래스는 overriding 하여 사용할 수 있다
	 * overriding 할때 super.regexpPreprocessing 의 호출은 반드시 필요하다.
	 */
	public int regexpPreprocessing() { 
		//삭제문구를 확인하여 삭제문구가 있는 경우 삭제로 returun.
		int ret = deleteStringProcessing(this.lstDeleteString);
	
		//그외 정규식을 진행하기전 처리 작업.
		return ret;
	}

	public int deleteStringProcessing(List<String> lstDeleteString) {
		//삭제 문구가 html 안에 포함되어 있다면, 삭제 상태로 return.
		int retState = 1;
		if(lstDeleteString != null && lstDeleteString.size() != 0) {
			for(String delString : lstDeleteString) { 
				if (this.htmlInfo.getHtmlSource().indexOf(delString) > -1) {
					retState = 400;
					break;
				}
			}
		}
		return retState;
	}
	
	public int deleteStringProcessing() {
		//삭제 문구가 html 안에 포함되어 있다면, 삭제 상태로 return.
		int retState = 1;
		if(this.lstDeleteString != null && this.lstDeleteString.size() != 0) {
			for(String delString : this.lstDeleteString) { 
				if (this.htmlInfo.getHtmlSource().indexOf(delString) > -1) {
					retState = 400;
					break;
				}
			}
		}
		return retState;
	}
	
	/*
	 * 정규표현식을 필터마다 구현처리한다.
	 * 목록수집에서는 에서는 List<Map> 을 이용해서 return 한다.
	 */
	public List<Map<String, String>> regularExpFilter(String srcHtml) throws VeniceException {
	
		// prefix, suffix 가 "" 이 아니면 잘라내서 그만큼만 이용하여 regexp 처리.
		int startIndex = 0;
		int endIndex = srcHtml.length()-1;
		List<Map<String, String>> ret = null;

		int bFilterFlag = 0;
		int matchedIndex = 0;
		for(FilterElement fe : this.getFilterInfo()) {
			ret = new ArrayList<Map<String, String>>();
			int tmpInx = 0;
			if (!fe.getPrefix().equals("") && srcHtml.indexOf(fe.getPrefix()) != -1) {
				tmpInx = srcHtml.indexOf(fe.getPrefix());
				startIndex = tmpInx;
			}
			if (!fe.getSuffix().equals("") && srcHtml.indexOf(fe.getSuffix()) != -1) {
				tmpInx = srcHtml.indexOf(fe.getSuffix());
				endIndex = tmpInx;
			}

			String html = srcHtml.substring(startIndex, endIndex);
			String[] arrHtml = html.split(fe.getDelimiter());
			InterruptibleCharSequence ics = null;
			for(String elemString : arrHtml) { 
				ics = new InterruptibleCharSequence(fe.getDelimiter() + elemString);
				Map<String, String> regResult = new HashMap<String, String>();
				Matcher matcher = fe.getPattern().matcher(ics.toString());
				
				if(matcher.find()) {
					// fields 에 있는 순서대로... 
					int i = 0;
					for(String field : fe.getFields()) {
						regResult.put(field, matcher.group(i+1));
						i++;
					}
					regResult.put("filterstate", "1");
					ret.add(regResult);
				} else { 
					//정규식이 맞지 녀석들이 있다... 어쩔거나?????
					bFilterFlag++;
				}
			};
			if (bFilterFlag == arrHtml.length) { 
				// 모두 맞지 않는다... 이럴때는 오류다 ~~!!!
				throw new VeniceException(VeniceEnums.Errors.FILTER_REGULAR_ERROR, new Exception("Not matched filter regular expression"));
			}
			this.matchedRegIndex = matchedIndex;
		}
		return ret;
	}


	/* 
	 * 정규식 처리후 데이터의 변환등의 후처리를 위한 함수
	 * filterPostprocessing 를 1개 단위로 재처리 하기 위해 loop .
	 */
	public List<Map<String, String>> regexpListPostprocessing(List<Map<String, String>> regexpResult) {
		List<Map<String, String>> ret = new ArrayList<Map<String, String>>();
		for(Map<String, String> m : regexpResult) { 
			ret.add(regexpPostprocessing(m));
		}
		return ret;
	}
	
	/*
	 * 정규식 수행후 추출된 각각의 element들에 대해서 후처리 하기위 한 작업..
	 */
	public abstract Map<String, String> regexpPostprocessing(Map<String, String> regexpResult);
	
	public abstract String getNextListUrl(String url) throws VeniceException;
	
	public abstract String getBodyTemplateName(String url) throws VeniceException ;
	/*
	 * DB에 쓸수있게 데이터를 정리해서 최종 리턴 하자.
	 * mybatis 를 쓰느냐 마느냐에 따라 크게 달라질 수 있다.
	 */
	protected FilteredData setDataObject(List<Map<String, String>> regexpResult) throws VeniceException {
		return null;
	}
	
	/*
	 * Getter......
	 */
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

	public HtmlInfo getHtmlInfo() {
		return htmlInfo;
	}

	public void setHtmlInfo(HtmlInfo htmlInfo) {
		this.htmlInfo = htmlInfo;
	}

	public int getMatchedRegIndex() {
		return matchedRegIndex;
	}

	public void setMatchedRegIndex(int matchedRegIndex) {
		this.matchedRegIndex = matchedRegIndex;
	}

	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

}
