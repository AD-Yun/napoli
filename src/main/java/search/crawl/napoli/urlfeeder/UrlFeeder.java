/**
 * @author n2365
 */
package search.crawl.napoli.urlfeeder;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class UrlFeeder {
	
	private UrlFeeder() {}
	
	public static String getCrawlURL(String crawlerID) {
		StringBuilder url = new StringBuilder();
		
		//OverHitController oc = OverHitController.getInstance();
		//CrawlHostMap chm = oc.getCrawlHostMap(crawlerID);
		//Map<String, Integer> urlCnt = chm.getURLCnt();
		
		//Set<String> keySet = urlCnt.keySet();
		//Iterator<String> it = keySet.iterator();
		
		//while(it.hasNext()) {
			//String hostName = it.next();
			//CrawlQueue cq =oc.getCrawlQueue(hostName);
			//int cnt = urlCnt.get(hostName);
			//cq.getIter();
			//url.append(cq.getCrawlURL(cnt) + "\n");
			//chm.incrHostCount(hostName, cnt);
		//}
		return url.toString();
	}
	
	
}
