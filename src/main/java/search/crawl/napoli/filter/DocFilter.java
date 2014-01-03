package search.crawl.napoli.filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import search.crawl.napoli.common.HtmlInfo;
import search.crawl.napoli.common.VeniceEnums;
import search.crawl.napoli.common.VeniceException;
import search.crawl.napoli.common.VeniceEnums.Errors;
import search.crawl.napoli.util.InterruptibleCharSequence;
/**
 * 본문 Filter 조상 클래스.
 * 
 * 
 * @author n2429
 * 
 */
public abstract class DocFilter implements Filter {
	static final Logger LOGGER = LoggerFactory.getLogger(DocFilter.class);
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
	 * 	Filter ff = FilterUtil.getFilter("kyobo");
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
			return new FilteredData("doc", checkValue);
		}
		// filtering 전처리
		checkValue = regexpPreprocessing();

		if (checkValue != 1) { 
			LOGGER.info("Filter Preprocessing is abnormality.");
			//checkValue 로 받은 값을 error code 값으로 보고 exception 을 발생 시키자.
			VeniceEnums.Errors er = VeniceEnums.Errors.getErrors(checkValue);
			throw new VeniceException(er, new Exception("Error occured at regexpPreprocessing"));
		}
		
		// regularExpFilter.
		Map<String, String> result = null;
		if(this.getClass().getSimpleName().equals("CommonFilter")) { 
			result = commonExtract(this.targetHtml);
		} else { 
			result = regularExpFilter(this.targetHtml);
		}

		/*
		 * if (result.get("filterstate").equals("2002")){ 
			LOGGER.error("Not matched filter regular expression");
			throw new VeniceException(VeniceEnums.Errors.FILTER_REGULAR_ERROR, new Exception("Not matched filter regular expression"));
		*/
		if(result.get("filterstate").equals("1")) {
			// filtering 후처리
			try {
				result = regexpPostprocessing(result);
				LOGGER.debug("Filtering SUCCESS");
			} catch (Exception e) {
				LOGGER.error("Regexp Postprocessing is abnormality.");
				//checkValue 로 받은 값을 error code 값으로 보고 exception 을 발생 시키자.
				throw new VeniceException(Errors.REGEXP_POSTPROCESSING_ABNORMALITY, e);
			}
		} else { 
			//filterstate 에 담긴 코드 값을 보고 error, exception 처리를 진행
			VeniceEnums.Errors er = VeniceEnums.Errors.getErrors(Integer.parseInt(result.get("filterstate")));
			LOGGER.error(er.getMessage());
			throw new VeniceException(er, new Exception("Error occured at regexpPreprocessing"));
		}
		
		//TODO : Map result 에 filterstate 의 값을 주목하자..ㅡ.ㅡ
		// data object 생성, 반환.
		FilteredData fd = setDataObject(result);
		
		// data 를 setting 하고, 데이터 객체를 리턴한다. 
		return fd;
	}
	
	public int checkHeaderState(int headerState) {
		int retState = 1;		
		
		if(headerState == 200) {
		} else { 
			// 문제가 있으니.... data 를 return. 하자...
			LOGGER.info("Header STATUS is Not 200");
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
	
	protected Map<String, String> commonExtract(String srcHtml) { 
		return new HashMap<String, String>();
	}
	/*
	 * 정규표현식을 필터마다 구현처리한다.
	 * 하위 클래스에서는 아래 클래스를 변경하지 않아요....
	 */
	public Map<String, String> regularExpFilter(String srcHtml) {
		// prefix, suffix 가 "" 이 아니면 잘라내서 그만큼만 이용하여 regexp 처리.
		int startIndex = 0;
		int endIndex = srcHtml.length()-1;
		Map<String, String> regResult = null;
		
		int matchedIndex = 0;
		for(FilterElement fe : this.getFilterInfo()) {
			regResult = new HashMap<String, String>();
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
			InterruptibleCharSequence ics = new InterruptibleCharSequence(html);
			Matcher matcher = fe.getPattern().matcher(ics.toString());
			if(matcher.find()) {
				// fields 에 있는 순서대로... 
				int i = 0; 
				for(String field : fe.getFields()) {
					regResult.put(field, matcher.group(i+1));
					i++;
				}
				regResult.put("filterstate", "1");
				this.matchedRegIndex = matchedIndex;
				break;
			} else { 
				//정규식이 맞지 않는다.
				//2번째, 혹은 3번째 정규 표현식이 있을 수 있다..ㅡ..ㅡ
				regResult.put("filterstate", "2002");
			}
			matchedIndex++;
		}
		return regResult;
	}

	/* 
	 * 정규식 처리후 데이터의 변환등의 후처리를 위한 함수
	 * Filter 클래스를 상속한 클래스는 overriding 하여 사용할 수 있다
	 * overriding 할때 super.regexpPreprocessing 의 호출은 반드시 필요하다.
	 */
	public abstract Map<String, String> regexpPostprocessing(Map<String, String> regexpResult);
	
	/*
	 * DB에 쓸수있게 데이터를 정리해서 최종 리턴 하자.
	 * mybatis 를 쓰느냐 마느냐에 따라 크게 달라질 수 있다.
	 */
	protected FilteredData setDataObject(Map<String, String> regexpResult) throws VeniceException {
		return null;
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

	public String getTargetHtml() {
		return targetHtml;
	}

	public void setTargetHtml(String targetHtml) {
		this.targetHtml = targetHtml;
	}

	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}
	
}
