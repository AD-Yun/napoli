package search.crawl.napoli.common;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;


public final class OpenKnowBodyConfig {
	private static volatile OpenKnowBodyConfig uniqueInstance;
	private Configuration config;
	
	private String urlFeederIP;
	private int urlFeederPort;
	private String docIDServerIP;
	private int docIDServerPort;
	
	private String listRecDBHostName;
	private String listRecDBName;
	private String listArcDBHostName;
	private String listArcDBName;
	private String bodyDBHostName;
	private String bodyDBName;
	
	private String cookDeamonIP;
	private int cookDeamonPort;
	
	private String htmlBDBDaemonName;
	private String htmlBDBIP;
	private int htmlBDBPort;
	private int htmlHashingNum;
	private String thumbBDBDaemonName;
	private String thumbBDBIP;
	private int thumbBDBPort;
	private int thumbHashingNum;
	
	private int countOfCrawlSkipDuplicationForTemplate;
	private int countOfCrawlTimeout;
	
	private OpenKnowBodyConfig() {}
	
	public static OpenKnowBodyConfig getInstance() {
		if(uniqueInstance == null) {
			synchronized (OpenKnowBodyConfig.class) {
				if(uniqueInstance == null) {
					uniqueInstance = new OpenKnowBodyConfig();
				}
			}
		}
		return uniqueInstance;
	}
	
	public boolean read() {
		try {
			config = new XMLConfiguration("openknowbodyconfig.xml");
			this.urlFeederIP = this.config.getString("urlfeederinfo.ip");
			this.urlFeederPort = this.config.getInt("urlfeederinfo.port");
			this.docIDServerIP = this.config.getString("docidserverinfo.ip");
			this.docIDServerPort = this.config.getInt("docidserverinfo.port");
			this.listRecDBHostName = this.config.getString("listRecdbinfo.hostname");
			this.listRecDBName = this.config.getString("listRecdbinfo.dbname");
			this.listArcDBHostName = this.config.getString("listArcdbinfo.hostname");
			this.listArcDBName = this.config.getString("listArcdbinfo.dbname");
			this.bodyDBHostName = this.config.getString("bodydbinfo.hostname");
			this.bodyDBName = this.config.getString("bodydbinfo.dbname");
			this.countOfCrawlSkipDuplicationForTemplate = this.config.getInt("crawlingskipfortemplateinfo.duplicatecount");
			this.countOfCrawlTimeout = this.config.getInt("timeout.maxcount");
			this.cookDeamonIP = this.config.getString("cookeddeamoninfo.ip");
			this.cookDeamonPort = this.config.getInt("cookeddeamoninfo.port");
			this.htmlBDBDaemonName = this.config.getString("htmlbdbinfo.daemonname");
			this.htmlBDBIP = this.config.getString("htmlbdbinfo.ip");
			this.htmlBDBPort = this.config.getInt("htmlbdbinfo.port");
			this.htmlHashingNum = this.config.getInt("htmlbdbinfo.hashingNum"); 
			this.thumbBDBDaemonName = this.config.getString("thumbbdbinfo.daemonname");
			this.thumbBDBIP = this.config.getString("thumbbdbinfo.ip");
			this.thumbBDBPort = this.config.getInt("thumbbdbinfo.port");
			this.thumbHashingNum = this.config.getInt("thumbbdbinfo.hashingNum");
			
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
	
	public String getDocIDServerIP() {
		return this.docIDServerIP;
	}
	
	public int getDocIDServerPort() {
		return this.docIDServerPort;
	}
	
	public String getCookDeamonIP() {
		return this.cookDeamonIP;
	}
	
	public int getCookDeamonPort() {
		return this.cookDeamonPort;
	}
	
	public String getBodyDBHostName() {
		return this.bodyDBHostName;
	}
	
	public String getBodyDBName() {
		return this.bodyDBName;
	}
	
	public String getListRecDBHostName() {
		return this.listRecDBHostName;
	}
	
	public String getListRecDBName() {
		return this.listRecDBName;
	}
	
	public String getListArcDBHostName() {
		return this.listArcDBHostName;
	}
	
	public String getListArcDBName() {
		return this.listArcDBName;
	}
	
	public int getCountOfCrawlSkipDuplicationForTemplate() {
		return this.countOfCrawlSkipDuplicationForTemplate;
	}
	
	public int getCountOfCrawlTimeout() {
		return this.countOfCrawlTimeout;
	}
	
	public String getHtmlBDBDaemonName() {
		return this.htmlBDBDaemonName;
	}
	
	public String getHtmlBDBIP() {
		return this.htmlBDBIP;
	}
	
	public int getHtmlBDBPort() {
		return this.htmlBDBPort;
	}
	
	public int getHtmlHashingNum() {
		return this.htmlHashingNum;
	}
	
	public String getThumbBDBDaemonName() {
		return this.thumbBDBDaemonName;
	}
	
	public String getThumbBDBIP() {
		return this.thumbBDBIP;
	}
	
	public int getThumbBDBPort() {
		return this.thumbBDBPort;
	}
	
	public int getThumbHashingNum() {
		return this.thumbHashingNum;
	}
}
