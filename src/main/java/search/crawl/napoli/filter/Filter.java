package search.crawl.napoli.filter;

import java.util.Map;

import search.crawl.napoli.common.HtmlInfo;
import search.crawl.napoli.common.VeniceException;

public interface Filter {
	int checkHeaderState(int headerState);
	int regexpPreprocessing();
	Map<String, String> regexpPostprocessing(Map<String, String> regexpResult);
	FilteredData filtering(HtmlInfo htmlInfo) throws VeniceException;
}
