package search.crawl.napoli.common;

public class OpenKnowDBData {
	public String siteID;
	public String hostName;
	public String pathName;
	public String downloadFlag;
	public String parsingFlag;
	public String errMsg;
	public String docID;
	public String signature;
	public String section;
	public String extrDate;
	public String topCategory;
	public String subCategory;
	public String quesTitle;
	public String quesTitleSig;
	public String quesBody;
	public String quesWriter;
	public String quesDate;
	public String quesTime;
	public String quesHits;
	public String quesReplyCnt;
	public String quesPoint;
	public String ansCount;
	public String ansSelected;
	public String ansOthers;
	public String ansDate;
	public String ansTime;
	public String mulmeLink;
	public String state;
	public String quesRating;
	public String quesRatingCnt;
	public String quesMultiTag;
	public String executed;
	
	public OpenKnowDBData() {
		this.quesDate = "0000-00-00";
		this.quesTime = "00:00:00";
	}
}
