package search.crawl.napoli.util;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;

import search.crawl.napoli.common.HtmlInfo;
import search.crawl.napoli.common.VeniceEnums;


public class Grabber {
	private String url;
	VeniceEnums.HTTP_METHOD_TYPE methodType;
	private String userAgent;
	int connectionTimeout;
	int connectionRequestTimeout;
	List<NameValuePair> argList;
	
	private static final String HTML_DEFAULT_CHARSET = "EUC-KR";
	
	private ConnectionConfig connectionConfig;
	private RequestConfig requestConfig;
	
	public Grabber(String url) {
		this.url = url;
		this.methodType = VeniceEnums.HTTP_METHOD_TYPE.GET;
		this.userAgent = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)";
		this.connectionTimeout = 5000; // default
		this.connectionRequestTimeout = 5000;
		
		this.argList = new ArrayList<NameValuePair>();
	}
	
	public Grabber setMethodType(VeniceEnums.HTTP_METHOD_TYPE methodType) {
		this.methodType = methodType;
		
		return this;
	}
	
	public Grabber setUserAgent(String userAgent) {
		this.userAgent = userAgent;
		
		return this;
	}
	
	public Grabber setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
		
		return this;
	}
	
	public Grabber setConnectionRequestTimeout(int connectionRequestTimeout) {
		this.connectionRequestTimeout = connectionRequestTimeout;
		
		return this;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public String getUserAgent() {
		return this.userAgent;
	}
	
	public Grabber putPostArgument(String key, String value) {
		this.argList.add(new BasicNameValuePair(key, value));
		
		return this;
	}
	
	public void setConfigOption() {
		this.connectionConfig = ConnectionConfig.custom()
	            .setMalformedInputAction(CodingErrorAction.IGNORE)
	            .setUnmappableInputAction(CodingErrorAction.IGNORE)
	            .setCharset(Consts.UTF_8)
	            .build();
		
		this.requestConfig = RequestConfig.custom()
                .setSocketTimeout(5000)
                .setConnectTimeout(this.connectionTimeout)
                .setConnectionRequestTimeout(this.connectionRequestTimeout)
                .build();
	}
	
	private String getHtmlContent(HttpEntity entity, String charSet) { 
		String htmlContent = "";
		//entity,getCharset 을 못 얻어오면....
		if(ContentType.get(entity).getCharset() == null) {
			int tmpBufferSize = (int)entity.getContentLength();
			if(tmpBufferSize < 0) { 
				tmpBufferSize = 4096;
			}
			if(charSet == null || "".equals(charSet)){ 
				charSet = HTML_DEFAULT_CHARSET;
			}
			CharArrayBuffer buffer = new CharArrayBuffer(tmpBufferSize);
			try(InputStream is = entity.getContent();
					Reader reader = new InputStreamReader(is, charSet);) { 
				int l;
				char[] tmp = new char[1024];
				while((l =reader.read(tmp)) != -1) {
					buffer.append(tmp, 0, l);
				}
			} catch(IOException ie) {
				ie.printStackTrace();
			}
			htmlContent = buffer.toString();
		} else {
			try {
				htmlContent = EntityUtils.toString(entity);
			} catch (ParseException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return htmlContent;
	}
	
	private HtmlInfo crawlByGetMethod(String charSet) throws Exception {
		setConfigOption();
		
		HtmlInfo htmlInfo = new HtmlInfo();
		CloseableHttpClient httpclient = HttpClients.custom()
				.setUserAgent(this.userAgent)
				.setDefaultConnectionConfig(this.connectionConfig)
				.setDefaultRequestConfig(this.requestConfig)
				.build();
		
		try {
			HttpGet httpGet = new HttpGet(this.url);
			CloseableHttpResponse response = httpclient.execute(httpGet);
			try {
				HttpEntity entity = response.getEntity();
				htmlInfo.setHeaderStatus(response.getStatusLine().getStatusCode());
				htmlInfo.setHeader(response.getAllHeaders());
				//htmlInfo.setCharset((entity.getContentType().getValue().split(";")[1].split("=")[1]));
				//htmlInfo.setHtmlSource(EntityUtils.toString(entity));
				htmlInfo.setHtmlSource(getHtmlContent(entity, charSet));
				htmlInfo.setHtmlLength(htmlInfo.getHtmlSource().getBytes().length);
			} finally {
				response.close();
				
			}
			
		} finally {
			httpclient.close();
		}
		
		return htmlInfo;
	}

	private HtmlInfo crawlByPostMethod(String charSet) throws Exception {
		setConfigOption();
		
		HtmlInfo htmlInfo = new HtmlInfo();
		CloseableHttpClient httpclient = HttpClients.custom()
				.setUserAgent(this.userAgent)
				.setDefaultConnectionConfig(this.connectionConfig)
				.setDefaultRequestConfig(this.requestConfig)
				.build();
		
		try {
			HttpPost httpPost = new HttpPost(this.url);
			
			httpPost.setEntity(new UrlEncodedFormEntity(this.argList));
			CloseableHttpResponse response = httpclient.execute(httpPost);
			
			try {
				HttpEntity entity = response.getEntity();
				htmlInfo.setHeaderStatus(response.getStatusLine().getStatusCode());
				htmlInfo.setHeader(response.getAllHeaders());
				htmlInfo.setCharset((entity.getContentType().getValue().split(";")[1].split("=")[1]));
				//htmlInfo.setHtmlSource(EntityUtils.toString(entity));
				htmlInfo.setHtmlSource(getHtmlContent(entity, charSet));
				htmlInfo.setHtmlLength(htmlInfo.getHtmlSource().getBytes().length);
			} finally {
				response.close();
			}
			
		} finally {
			httpclient.close();
		}
		
		return htmlInfo;
	}
	
	public HtmlInfo crawl() throws Exception {
		HtmlInfo htmlInfo = null;

		switch(this.methodType) {
		case GET:
			htmlInfo = crawlByGetMethod(HTML_DEFAULT_CHARSET);
			break;
		case POST:
			htmlInfo = crawlByPostMethod(HTML_DEFAULT_CHARSET);
			break;
		default : 
			break;
		}
		
		return htmlInfo;
	}
	
	public HtmlInfo crawl(String charSet) throws Exception {
		HtmlInfo htmlInfo = null;

		switch(this.methodType) {
		case GET:
			htmlInfo = crawlByGetMethod(charSet);
			break;
		case POST:
			htmlInfo = crawlByPostMethod(charSet);
			break;
		default : 
			break;
		}
		return htmlInfo;
	}
	
}
