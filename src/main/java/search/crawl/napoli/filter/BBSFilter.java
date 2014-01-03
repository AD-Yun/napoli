package search.crawl.napoli.filter;

import java.util.Map;

public class BBSFilter extends DocFilter {

	/*
	 * 게시판 공통 정규식 전처리는 아래에서 진행하고 
	 * 사이트별 후처리를 사이트별 filter에서 처리하자.
	 * @see search.crawl.napoli.filter.Filter#regexpPreprocessing()
	 */
	public int regexpPreprocessing() { 
		return 0;
	}
	
	
	/*
	 * 게시판 공통 정규식 후처리는 아래에서 구현하고 
	 * 사이트별 후처리는 사이트별 filter에서 처리하자
	 * @see search.crawl.napoli.filter.Filter#regexpPostprocessing()
	 */
	public Map<String, String> regexpPostprocessing(Map<String, String> ins)  { 
		int retState = 1;
		// 정규식 처리후 데이터처리..
		/*if (retState != 1) {
			
		}*/
		
		return ins;
	}
	/*
	 * 게시판 동일한 DB 스크마를 이용하므로 아래 method 에서 처리 db 를 위한 
	 * 데이터 처리를 하자.
	 * 혹, 하위 클래스에서 해야한다면 overriding 하도록 하자.
	 * @see search.crawl.napoli.filter.Filter#setDataObject()
	 */
	public FilteredData setDataObject(Map<String, String> regexpResult) {
		return null;
	}
}
