package search.crawl.napoli.common;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;


public class OpenKnowListConfig {
	private static volatile OpenKnowListConfig uniqueInstance;
	private Configuration config;
	
	private String urlFeederIP;
	private int urlFeederPort;
	
	private String seedDBHostName;
	private String seedDBName;
	private String listDBHostName;
	private String listDBName;
	
	private int countOfCrawlSkipDuplicationForTemplate;
	private int countOfCrawlTimeout;
	
	private OpenKnowListConfig() {}
	
	public static OpenKnowListConfig getInstance() {
		if(uniqueInstance == null) {
			synchronized (OpenKnowBodyConfig.class) {
				if(uniqueInstance == null) {
					uniqueInstance = new OpenKnowListConfig();
				}
			}
		}
		return uniqueInstance;
	}
	
	public boolean read() {
		try {
			config = new XMLConfiguration("openknowlistconfig.xml");
			this.urlFeederIP = this.config.getString("listurlfeederinfo.ip");
			this.urlFeederPort = this.config.getInt("listurlfeederinfo.port");
			this.seedDBHostName = this.config.getString("seeddbinfo.hostname");
			this.seedDBName = this.config.getString("seeddbinfo.dbname");
			this.listDBHostName = this.config.getString("listdbinfo.hostname");
			this.listDBName = this.config.getString("listdbinfo.dbname");
			this.countOfCrawlSkipDuplicationForTemplate = this.config.getInt("crawlingskipfortemplateinfo.duplicatecount");
			this.countOfCrawlTimeout = this.config.getInt("timeout.maxcount");
			
			return true;
		} catch (ConfigurationException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Configuration getConfig() {
		return this.config;
	}
	
	public String getUrlFeederIP() {
		return this.urlFeederIP;
	}
	
	public int getUrlFeederPort() {
		return this.urlFeederPort;
	}
	
	public String getSeedDBHostName() {
		return this.seedDBHostName;
	}
	
	public String getSeedDBName() {
		return this.seedDBName;
	}
	
	public String getListDBHostName() {
		return this.listDBHostName;
	}
	
	public String getListDBName() {
		return this.listDBName;
	}
	
	public int getCountOfCrawlSkipDuplicationForTemplate() {
		return this.countOfCrawlSkipDuplicationForTemplate;
	}
	
	public int getCountOfCrawlTimeout() {
		return this.countOfCrawlTimeout;
	}
}
