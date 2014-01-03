/**
 * @author n2365
 */
package search.crawl.napoli.urlfeeder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class CrawlQueue {
	private LinkedList<String> cq;
	private Iterator<String> it;

	CrawlQueue() {
		cq = new LinkedList<String>();
	}

	public void addQueue(String url) {
		cq.add(url);
	}

	public int getQueueSize() {
		return cq.size();
	}

	@Deprecated
	public void getIter() {
		it = cq.iterator();
	}

	public ArrayList<String> getCrawlURL(int cnt) {
		ArrayList<String> crawlUrl = new ArrayList<String>();
		for (int i = 0; i < cnt; i++) {
			crawlUrl.add(getCrawlURL());
		}
		return crawlUrl;
	}

	public String getCrawlURL() {
		String url = null;
		if (it.hasNext()) {
			url = it.next();
			it.remove();
		}
		return url;
		// return it.hasNext() ? it.next() : new String("");
	}
}
