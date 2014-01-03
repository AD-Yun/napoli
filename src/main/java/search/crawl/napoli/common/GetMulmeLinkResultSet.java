package search.crawl.napoli.common;

public class GetMulmeLinkResultSet {
	String Selected;
	String returnState;
	
	public GetMulmeLinkResultSet(String selected, String returnVal) {
		Selected = selected;
		returnState = returnVal;
	}
	
	public void setSeleted(String val) {
		this.Selected = val;
	}
	
	public void setReturnState(String val) {
		this.returnState = val;
	}
	
	public String getSelected() {
		return this.Selected;
	}
	
	public String getReturnState() {
		return this.returnState;
	}
}