package search.crawl.napoli.filter.blog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import search.crawl.napoli.common.VeniceException;
import search.crawl.napoli.filter.FilterElement;
import search.crawl.napoli.filter.FilteredData;
import search.crawl.napoli.filter.ListFilter;

public class BlogListFilter extends ListFilter {
	private List<String> lstDeleteString;
	private List<FilterElement> filterInfo;
	/**
	 * valid extract fields... - 최종 추출된 결과 field name.
	 * service/type 별로..... 다름.
	 * TODO: 수정해야함
	 */
	protected List<String> validExtractFields = new ArrayList<String>(
			Arrays.asList("section", "topcategory", "subcategory", "questitle", "quesbody", "queswriter", "quesdate",
					"questime", "queshits", "quesreplycnt", "quespoint","quesrating", "qesratingcnt", "anscount", "ansselected", "ansothers", "ansdate", "anstime",
					"state", "signature")
			);
	
	public String getNextListUrl(String url) { 
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
	/*
	 * 블로그는 동일한 DB 스크마를 이용하므로 아래 method 에서 처리 db 를 위한 
	 * 데이터 처리를 하자.
	 * 혹, 하위 클래스에서 해야한다면 overriding 하도록 하자.
	 * @see search.crawl.napoli.filter.Filter#setDataObject()
	 */
	@Override
	public String getBodyTemplateName(String url) throws VeniceException {
		// TODO Auto-generated method stub
		return null;
	}
}
