/**
 * @author n2365
 */
package search.crawl.napoli.urlfeeder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;


public final class OverHitController {
	
	private static Map<String, CrawlHostMap> crawlerIDMap;
	private static Map<String, CrawlQueue> crawlURLMap;
	private static Configuration config;
	private static volatile OverHitController uniqueInstance;
	
	private OverHitController() {
		crawlerIDMap = new HashMap<String, CrawlHostMap>();
		crawlURLMap = new HashMap<String, CrawlQueue>();
		try {
			config = new XMLConfiguration("config.xml");
			
			@SuppressWarnings("unchecked")
			List<String> host = config.getList("hosts.name");
			Iterator<String> h = host.iterator();
			while(h.hasNext()) {
				String hostName = h.next();
				crawlURLMap.put(hostName, new CrawlQueue());
			}
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	// singleton
	public static OverHitController getInstance() {
		if(uniqueInstance == null) {
			synchronized (OverHitController.class) {
				if(uniqueInstance == null) {
					uniqueInstance = new OverHitController();
				}
			}
		}
		return uniqueInstance;
	}
	
	public CrawlHostMap getCrawlHostMap(String crawlerID) {
		return crawlerIDMap.containsKey(crawlerID) ? crawlerIDMap.get(crawlerID) : setCrawlHostMap(crawlerID);
	}
	
	public CrawlHostMap setCrawlHostMap(String crawlerID) {
		crawlerIDMap.put(crawlerID, new CrawlHostMap(config));
		return crawlerIDMap.get(crawlerID);
	}
	
	public CrawlQueue getCrawlQueue(String hostName) {
		return crawlURLMap.get(hostName);
	}
}