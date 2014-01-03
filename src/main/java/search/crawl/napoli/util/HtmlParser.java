package search.crawl.napoli.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import search.crawl.napoli.common.VeniceEnums;
import search.crawl.napoli.common.VeniceException;


public final class HtmlParser {
	static final char largeMultiDelim = (char)0x03;
	static final char smallMultiDelim = (char)0x02;
	
	private static final List<String> TYPE_LIST = new ArrayList<String>(Arrays.asList("I", "D", "A", "M", "S", "C", "V", "E"));
	private static final Set<String> HTML_TYPE = new HashSet<String>(Arrays.asList(
			"html", "html", "shtml", "asp", "cgi", "tsp", "php", "php3", "php4", "jsp", "do"));
	private static final Set<String> IMAGE_TYPE = new HashSet<String>(Arrays.asList(
			"gif", "jpg", "jpeg", "bmp", "dib", "psd", "png", "pcx", "tif", "tiff", "wmf", "emf"));
	private static final Set<String> SOUND_TYPE1 = new HashSet<String>(Arrays.asList("mp2", "mp3"));
	private static final Set<String> SOUND_TYPE2 = new HashSet<String>(Arrays.asList("ram", "ra", "wav", "mid", "aud", "au", "wma"));
	private static final Set<String> VIDEO_TYPE = new HashSet<String>(Arrays.asList("avi", "mpg", "mpeg", "mpq", "mov", "xdm", "vdo", "asx", "asf", "qt"));
	private static final Set<String> DOCU_TYPE = new HashSet<String>(Arrays.asList("doc", "rtf", "txt", "ppt", "xls", "hwp", "gul", "pdf", "ps", "xlsx"));
	private static final Set<String> FLASH_TYPE = new HashSet<String>(Arrays.asList("swf"));
	private static final Set<String> COMP_TYPE = new HashSet<String>(Arrays.asList("zip", "arj", "lzh", "tar", "z", "qz", "taz", "pak", "ahx"));
	private static Map<String, Set<String>> typeMap = new HashMap<String, Set<String>>();
	static {
		typeMap.put("H", HTML_TYPE);
		typeMap.put("I", IMAGE_TYPE);
		typeMap.put("M", SOUND_TYPE1);
		typeMap.put("S", SOUND_TYPE2);
		typeMap.put("V", VIDEO_TYPE);
		typeMap.put("D", DOCU_TYPE);
		typeMap.put("A", FLASH_TYPE);
		typeMap.put("C", COMP_TYPE);
	}
	public static Set<String> exceptHost = new HashSet<String>();
	
	private HtmlParser() {
	}
	public static Map<String, String> extractLink(String html) {
		Map<String, String> result = new LinkedHashMap<String, String>();
		Document doc = Jsoup.parse(html);
		Elements linkElements = doc.select("a[href]");

		Iterator<Element> iter = linkElements.iterator();

		while(iter.hasNext()) {
			Element element = iter.next();
			if(!element.attr("abs:href").equals("")) {
				result.put(element.text(), element.attr("href"));
			}

		}
		return result;
	}
	
	public static List<String> extractImageLink(String html) {
		List<String> result = new ArrayList<String>();
		Document doc = Jsoup.parse(html);
		Elements linkElements = doc.select("img[src]");
		Iterator<Element> iter = linkElements.iterator();
		while(iter.hasNext()) {
			result.add(iter.next().attr("src"));
        }
		
		return result;
	}
	
	private static Set<String> extractImageLink4MulmeTag(String html, String title) {
		Document doc = Jsoup.parse(html);
		Elements linkElements = doc.select("img");
		
		Set<String> ret = new LinkedHashSet<String>();
		String url = "";
		String alt = "";
		ELEMENTS:
		for(Element e : linkElements) {
			url = parseProtocol(e.attr("abs:src"));
			alt = e.attr("alt");
			for(String s : HtmlParser.exceptHost) {
				if(s.length() > 0 && url.indexOf(s) > -1 ) { 
					//System.out.println("EXCEPT :" + s + "/");
					continue ELEMENTS;
				}
			}
			ret.add("I" + HtmlParser.smallMultiDelim + url + "\"" + alt + 
					HtmlParser.smallMultiDelim + title );
		}
		return ret;
	}
	
	private static String parseProtocol(String inUrl) { 
		URL url = null;
		String retUrl = inUrl;
		try {
			url = new URL(inUrl);
			retUrl = url.getHost() + url.getPath() + ((url.getQuery() == null ? "" : url.getQuery()));
			if(url.getProtocol().equals("ftp")) { 
				retUrl = ":f:" + retUrl;
			} else if(url.getProtocol().equals("mms")) { 
				retUrl = ":m:" + retUrl;
			} 
		} catch (MalformedURLException e) {
			//url type 이 아니다..ㅡ.ㅡ
			//특별히 error 로 보기는 어렵다.
		}
		return retUrl;
	}

	public static void appendExceptHost(Set<String> except) { 
		HtmlParser.exceptHost.addAll(except);
	}
	public static void setExceptHost(Set<String> except) { 
		HtmlParser.exceptHost = except;
	}
	/**
	 * except 시켜야할 host 를 새로 settging
	 * @param html
	 * @param except
	 * @return
	 */
	public static String parseMultimediaTag(String html, String title, Set<String> except) {
		HtmlParser.setExceptHost(except);
		return parseMultimediaTag(html, title);
	}
	
	private static String checkFileExtension(String src) {
		String fileType = "";
		String extension = "";
		try {
			URL url = new URL(src);
			String path = url.getPath();
			int dotInx = path.lastIndexOf(".");
			extension = (dotInx > 0 && dotInx < path.length()) ? path.substring(dotInx + 1) : "";
		} catch (MalformedURLException e) {
			// 확장자를 추출하지 못한다... 그냥 E(기타) 타입
			
		}
		Set<String> keys = HtmlParser.typeMap.keySet();
		for(String key : keys) {
			if(HtmlParser.typeMap.get(key).contains(extension)) { 
				fileType = key;
				break;
			}
		}
		return fileType;
	}
	
	private static Map<String, Set<String>> extractLink4MulmeTag(String html, String title, String tag, Map<String, Set<String>> inMap) {
		Document doc = Jsoup.parse(html);
		Elements linkElements = doc.select(tag);
		String url = "";
		String fileType = "";
		Set<String> ret = null;
		String tmp = "";
		ELEMENTS:
		for(Element e : linkElements) {
			if(tag.equals("a")) { 
				url = parseProtocol(e.attr("abs:href"));
			} else { 
				url = parseProtocol(e.attr("abs:src"));
			}

			for(String s : HtmlParser.exceptHost) {
				if(s.length() > 0 && url.indexOf(s) > -1) { 
					continue ELEMENTS;
				}
			}
			
			fileType = checkFileExtension(url);
			if(tag.equals("embed") && !fileType.equals("H") && !fileType.equals("E")) { 
				continue;
			}
			
			tmp = fileType + HtmlParser.smallMultiDelim + url + HtmlParser.smallMultiDelim + title;
			if(inMap.get(fileType) != null) {
				ret = inMap.get(fileType);
				ret.add(tmp);
			} else {
				ret = new LinkedHashSet<String>();
				ret.add(tmp);
			}
			inMap.put(fileType, ret);
		}
		return inMap;
	}
	
	public static String parseMultimediaTag(String html, String title) {
		
		//image 를 제외한 나머지는  A, FRAME, IFRAME, EMBED 태그의 는 src 의 확장자를 확인해서 처리하자.
		List<String> extrTags = new ArrayList<String>(Arrays.asList("a", "frame", "iframe", "embed"));
		
		Map<String, Set<String>> extractResult = new HashMap<String, Set<String>>();

		//image tag 추출 
		extractResult.put("I",extractImageLink4MulmeTag(html, title));
		for(String tag : extrTags) { 
			extractResult = extractLink4MulmeTag(html, title, tag, extractResult);
		}
		
		StringBuffer retData = new StringBuffer();
		for(String type : HtmlParser.TYPE_LIST) { 
			retData.append(((extractResult.get(type) == null) ? "" : 
				StringUtils.join(extractResult.get(type), HtmlParser.largeMultiDelim) + HtmlParser.largeMultiDelim));
		}
		return retData.toString();
	}
	/**
	 * grabhtml-js 를 이용해서 html로 부터 text 를 추출한다.
	 * @param html
	 * @return
	 */
	//nio.2(JAVA 7) 이용
	public static String getTextOnlyByGrabHtmlJs(String html) throws VeniceException {
		
		String defaultTmpDir = System.getProperty("java.io.tmpdir");
		
		//임시파일 생성.
		String tmpFilePrefix = "src";
		String tmpFileSuffix = ".html";
		
		Path srcHtmlFile = null;
		Charset charset = Charset.forName(System.getProperty("file.encoding"));
		try {
			srcHtmlFile = Files.createTempFile(tmpFilePrefix, tmpFileSuffix);
			BufferedWriter bw = Files.newBufferedWriter(srcHtmlFile, charset, StandardOpenOption.APPEND);
			bw.write(html);
			bw.close();
		} catch (IOException e1) {
			throw new VeniceException(VeniceEnums.Errors.UNKNOWN, e1);
		}
		
		StringBuilder resultStr = new StringBuilder();
		try { 
			//String ret = CommandExecute.build(tmpDirName).execute("cp", "src.html", "output.html") ;
			String ret = CommandExecute.build(defaultTmpDir).execute("grabhtml-js", "-nojs", "file:/" + srcHtmlFile.toString());
			
	  		//Path outputFile = defaultTmpDir.resolve("output.text");
			Path outputFile = FileSystems.getDefault().getPath(defaultTmpDir + "/output.text");
			try { 
				List<String> lines = Files.readAllLines(outputFile, charset);
				for(String line : lines) { 
					resultStr.append(line + System.getProperty("line.separator"));
				}
				
				BufferedReader br = Files.newBufferedReader(outputFile, charset);
				String line = "";
				while((line = br.readLine()) != null) {
					resultStr.append(line + System.getProperty("line.separator"));
				}
				br.close();
				
			} catch(IOException ie) { 
				//throw new RuntimeException("FILE : " + srcHtmlFile.toString() + "write error") ;
				throw new VeniceException(VeniceEnums.Errors.UNKNOWN, ie);
			}
		} catch(Exception e) { 
			//throw new RuntimeException("COMMAND EXECUTE ERROR  : " + e.toString()) ;
			throw new VeniceException(VeniceEnums.Errors.UNKNOWN, e);
		}
		
		return resultStr.toString();
	}
	
	/**
	 * 입력된 html로부터 jsoup 을 이용하여 text 를 추출한다.
	 * @param html
	 * @return
	 */
	public static String getTextOnly(String html) {
		Document doc = Jsoup.parse(html);
		String lineSerperator = System.getProperty("line.separator");
		String checkStr = lineSerperator + lineSerperator;
		String text = doc.body().textForAddNewLine();
		while(text.indexOf(checkStr) > -1) { 
			text = StringUtils.replace(text, checkStr, lineSerperator);
		}
		return text;
		//return doc.body().textForAddNewLine();
		
		/*
		String htmlText = "";
		
		// \n 을 <br> 태그로 변환하자.
		String targetHtml = html.replaceAll("\n", "<br>") ;
		System.out.println();
		Document doc = Jsoup.parse(html) ;
		//StringBuffer sbb = new StringBuffer();
		
		//body 가 존재하지 않거나?? 여러개면 어떻게 되나??? 
		//title 태그를 제거하는 방향으로 나가도록 하자.
/*		Elements elems = doc.body().getAllElements();	
		for (Element el : elems) {
            sbb.append(el.);
            sbb.append("\n");
        }*/
		
/*		// 상위 태그의 문자열 추출후, 하위 태그 문자열 추출하는 문제 있음... 
		StringBuffer sbb =new StringBuffer();
		Element bodyElement = doc.body();
		Iterator<Element> iEs2 = bodyElement.getAllElements().iterator();
		while(iEs2.hasNext()) { 
			Element el = iEs2.next();
			System.out.println("tag : " + el.tagName() + " >> " + el.text());
			tmpTag = el.tagName() ;
			//System.out.println(tmpTag);
			if(tmpTag.equals("br") || tmpTag.equals("p")) {
				sbb.append("\n") ;
			}
			sbb.append(el.text()) ;
		}		
		System.out.println(sbb.toString());
		
		System.out.println("---------------------------------------------------------------------");
		//System.out.println(bodyElement);
*/
		
/*		// 상위 태그의 문자열 추출후, 하위 태그 문자열 추출하는 문제 있음... 
		StringBuffer sbb =new StringBuffer();
		Elements elems = doc.body().getAllElements();
		System.out.println("---------------------------------------------------------------------");
		System.out.println(doc.body().toString());
		System.out.println("---------------------------------------------------------------------");
		for (Element element : elems) {
		    System.out.println(element.text() + "\n");
		    // body, title tag 제외처리.
		    if(element.tag().equals("body") || element.tag().equals("title") ) {
		    	continue ;
		    }
		    
		}*/
		/*		System.out.println(body);
		String safe = Jsoup.clean(html, Whitelist.basic());
		System.out.println(safe);*/
		/*
		Document doc = Jsoup.parse(html) ;
		doc.outputSettings().prettyPrint(true);
		Iterator<Element> iEs = doc.getAllElements().iterator();
		StringBuffer sb = new StringBuffer() ;
		String tmpTag = "";
		String br = "";
		while(iEs.hasNext()) { 
			Element el = iEs.next();
			tmpTag = el.tagName() ;
			if(tmpTag.equals("br")) {
				sb.append("\n") ;
			}
			sb.append(el.text()) ;
		}
		//System.out.println(sb.toString());
		tmpTag = "";
		br = "br";
		*/
		//return htmlText ;
		
	}

}
