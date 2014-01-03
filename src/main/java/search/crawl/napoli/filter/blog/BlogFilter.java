package search.crawl.napoli.filter.blog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import search.crawl.napoli.db.CollectionData;
import search.crawl.napoli.filter.DocFilter;
import search.crawl.napoli.filter.FilteredData;

public class BlogFilter extends DocFilter {
	/**
	 * valid extract fields... - 최종 추출된 결과 field name.
	 * service/type 별로..... 다름.
	 */
	protected List<String> validExtractFields = new ArrayList<String>(Arrays.asList("SN", "HOST", "Path", "SiteID", "DocID", "Signature", "ExtrDate", "ModiDate", 
			"Title", "Category", "UserID", "UserNickname", "UserSN", "Permit2Scrap", "Hits", "RecommandCnt", "ScrappedCnt", "IsScrapped", "Date", 
			"Time", "Body", "MulmeTag", "MulmeLink", "MulmeSignature", "VisitTurn", "UpdateCnt", "UpdateDone", "Status", "DupSig", "SpamMsg",
			"GroupInfo", "GroupFlag", "Tag", "CookedTitle", "RecommendedCat", "RecommendedTag", "MapInfo", "SemanticCat", "SemanticCatDesc",
			"SemanticAttr", "SemanticAttrDesc", "Extra1", "Extra2", "Extra3", "LinkExtrDate", "ThumbPath", "ExpertCate", "CmtCnt", "TrbCnt"));
	
	private List<String> lstDeleteString;
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
	public Map<String, String> regexpPostprocessing(Map<String, String> ins)  { 
		return ins;
	}
	/*
	 * 블로그는 동일한 DB 스크마를 이용하므로 아래 method 에서 처리 db 를 위한 
	 * 데이터 처리를 하자.
	 * 혹, 하위 클래스에서 해야한다면 overriding 하도록 하자.
	 * @see search.crawl.napoli.filter.Filter#setDataObject()
	 */
	public FilteredData setDataObject(Map<String, String> regexpResult, CollectionData tmp)  {
		
		//validExtractFields 와 map 결과가 일치하는 것이 있다면 그대로 setting.
		Map<String, String> filteredResult = new HashMap<String, String>();
		for(String field : this.validExtractFields) {
			if(regexpResult.containsKey(field.toLowerCase())) {
				filteredResult.put(field, regexpResult.get(field.toLowerCase()));
			}
		}
		//validExtractFields 들에 대한 처리.
		
		//filtering 결과 이외의 데이터는 filter 이후 setting.
		FilteredData fd = new FilteredData("doc", Integer.parseInt(regexpResult.get("filterstate")), filteredResult);
		return fd;
	}
}
