package search.crawl.napoli.common;

import org.apache.http.Header;


public class HtmlInfo {
	private String url;
	private String htmlSource;
	private Header[] header;
	private int headerStatus;
	private String encodingSet;
	private int htmlLength;
	private byte[] htmlData;
	
	public HtmlInfo() {
		url = "";
		this.htmlSource = null;
		this.headerStatus = 0;
		this.encodingSet = null;
		this.htmlLength = 0;
		this.htmlData = null;
	}

	
	public String getHtmlSource() {
		return this.htmlSource;
	}

	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public void setHtmlSource(String htmlSource) {
		this.htmlSource = htmlSource;
	}
	
	public int getHtmlLength() {
		return this.htmlLength;
	}
	
	public void setHtmlLength(int htmlLength) {
		this.htmlLength = htmlLength;
	}
	
	public int getHeaderStatus() {
		return this.headerStatus;
	}

	public void setHeaderStatus(int headerStatus) {
		this.headerStatus = headerStatus;
	}
	
	public Header[] getHeader() {
		return this.header;
	}

	public void setHeader(Header[] header) {
		this.header = header;
	}

	public String getCharset() {
		return this.encodingSet;
	}
	
	public void setCharset(String encodingSet) {
		this.encodingSet = encodingSet;
	}
	
	public void setHtmlData(byte[] data) {
		this.htmlData = data;
	}
	
	public byte[] getHtmlData() {
		return this.htmlData;
	}
}
