package search.crawl.milano.db;

public interface CommonDBPool {
	
	public CommonDBHandler getHandler();
	public boolean returnHandler(CommonDBHandler returnHandler);
	public boolean close();
	
}
