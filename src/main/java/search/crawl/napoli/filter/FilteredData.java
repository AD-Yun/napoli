package search.crawl.napoli.filter;

import java.util.List;
import java.util.Map;

public class FilteredData {
	private String filterType;
	private int filterState;
	//db - data. (list)
	private List<Map<String, String>> lstFilteredResult;
	//db - data. (body)
	private Map<String, String> filteredResult;
	
	public FilteredData() {
	}
	public FilteredData(String filterType, int filterState) {
		super();
		this.filterType = filterType;
		this.filterState = filterState;
	}
	public FilteredData(String filterType, int filterState, List<Map<String, String>> lstFilteredResult) {
		super();
		this.filterType = filterType;
		this.filterState = filterState;
		this.lstFilteredResult = lstFilteredResult;
	}
	public FilteredData(String filterType, int filterState,  Map<String, String> filteredResult) {
		super();
		this.filterType = filterType;
		this.filterState = filterState;
		this.filteredResult = filteredResult;
	}
	
	public String getFilterType() {
		return filterType;
	}

	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}

	public int getFilterState() {
		return filterState;
	}

	public void setFilterState(int filterState) {
		this.filterState = filterState;
	}

	public List<Map<String, String>> getLstCollectionData() {
		return this.lstFilteredResult;
	}

	public void setLstCollectionData(List<Map<String, String>> lstFilteredResult) {
		this.lstFilteredResult = lstFilteredResult;
	}
	
	public Map<String, String> getCollectionData() {
		return this.filteredResult;
	}
	
	public void setCollectionData(Map<String, String> filteredResult) {
		this.filteredResult = filteredResult;
	}
	
	@Override
	public String toString() {
		return "FilteredData [filterType=" + filterType + ", filterState="
				+ filterState + ", lstCollectionData=" + lstFilteredResult
				+ ", collectionData=" + filteredResult.toString() + "]";
	}
		
	
}
