package search.crawl.napoli.crawler;

public abstract class Crawler {
	protected String crawlerID;
	
	public abstract boolean getCrawlDataFromUrlFeeder() throws Exception;
	public abstract boolean checkOverHit();
	public abstract void noticeCrawlingResult2UrlFeeder() throws Exception;
	public abstract boolean readConfig();
	public abstract boolean dbInit();
	
	public abstract void work();
}
