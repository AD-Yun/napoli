/**
 * @author n2365
 */
package search.crawl.napoli.urlfeeder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.Configuration;

public class CrawlHostMap {
	
	// 한번에 하나의 호스트에서 수집될 숫자. 
	static int maxHostCount;
	private Map<String, Integer> hostGroup;
	
	
	CrawlHostMap(Configuration config) {
		hostGroup = new HashMap<String, Integer>();
		
		@SuppressWarnings("unchecked")
		List<String> host = config.getList("hosts.name");
		Iterator<String> h = host.iterator();
		while(h.hasNext()) {
			hostGroup.put(h.next(), 0);
		}
		maxHostCount = config.getInt("hosts.hostpermax");
	}
	
	// 호스트로 수집되고 있는 갯수 반환
	public int getHostCount(String host) { 
		return hostGroup.containsKey(host) ? hostGroup.get(host) : -1;
	}
	
	public Map<String, Integer> getURLCnt() {
		Set<String> keySet = hostGroup.keySet();
		Iterator<String> it = keySet.iterator();
		Map<String, Integer> urlCnt = new HashMap<String, Integer>();
		while(it.hasNext()) {
			String key = (String) it.next();
			int remainCnt = exprGetHostCount(key);
			urlCnt.put(key, remainCnt);
		}
		return urlCnt;
	}
	// 해당 호스트로 수집가능한 숫자.
	public int exprGetHostCount(String host) {
		int nowCnt = getHostCount(host);
		
		if(nowCnt >= maxHostCount) {
			return 0;
		}
		return maxHostCount - nowCnt;
	}
	
	// 증가
	public void incrHostCount(String host) {
		incrHostCount(host, 1);
	}
	
	public void incrHostCount(String host, int cnt) {
		hostGroup.put(host, hostGroup.get(host) + cnt);
	}
	
	@Deprecated
	// 감소
	public void decrHostCount(String host) {
		decrHostCount(host, 1);
	}
	
	@Deprecated
	public void decrHostCount(String host, int cnt) {
		hostGroup.put(host, hostGroup.get(host) - cnt);
	}
}


