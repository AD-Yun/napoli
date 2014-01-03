package search.crawl.napoli.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import search.crawl.napoli.db.CollectionData;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;

/**
 * common filter. 
 * filter 가 존재 하지 않는 경우 loading 되는 filter.
 * boilerpipe 를 이용해서 body 영역을 추출하고 title 태그를 통해서 title 을 추출한다.
 *  
 * 그외 작업은 TODO.
 * @author N2429
 *
 */
public class CommonFilter extends DocFilter {
	/**
	 * valid extract fields... - 최종 추출된 결과 field name.
	 * service/type 별로..... 다름.
	 */
	protected List<String> validExtractFields = new ArrayList<String>(Arrays.asList("title", "bodyhtml"));
	public CommonFilter() {
	}
	
	@Override
	protected Map<String, String> commonExtract(String srcHtml) { 
		Map<String, String> result = new HashMap<String, String>();
		
		//boilerpipe. extract body text.
		try {
			String boilerText = ArticleExtractor.INSTANCE.getText(srcHtml);
			result.put("bodyhtml", boilerText);
		} catch (BoilerpipeProcessingException e) {
			// TODO Auto-generated catch block
			
		}
		
		//extract title text from <title> tag
		Document doc = Jsoup.parse(srcHtml);
		result.put("title", doc.title());
		result.put("filterstate", "1");
		return result;
	}
	
	@Override
	public int regexpPreprocessing() { 
		int ret = super.regexpPreprocessing();
		return ret;
	}
	
	
	@Override
	public Map<String, String> regexpPostprocessing(
			Map<String, String> regexpResult) {
		return regexpResult;
	}
	
	// 임시. test 용.
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
