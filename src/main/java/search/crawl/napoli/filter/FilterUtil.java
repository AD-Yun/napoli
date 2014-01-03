package search.crawl.napoli.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import search.crawl.napoli.common.VeniceEnums;
import search.crawl.napoli.common.VeniceException;

public final class FilterUtil {
	static final Logger LOGGER = LoggerFactory.getLogger(FilterUtil.class);
	private FilterUtil() { }

	private static ClassLoader filterClassLoader = ClassLoader.getSystemClassLoader();
	private static Map<String, String> filterNameMap = new HashMap<String, String>();
	//초기화 블럭. filter.xml 로부터 filtername = classname 을 읽어옴. filterNameMap 에 setting.
	static { 
		StringBuilder data = new StringBuilder();
		try { 
			BufferedReader br = new BufferedReader(
					new InputStreamReader(filterClassLoader.getResourceAsStream("filter.xml")));
			String line = "";
			while((line = br.readLine()) != null) {
				data.append(line + System.getProperty("line.separator"));
			}
			br.close();
		} catch (IOException e) {
		}
		String html = data.toString();
		Document doc = Jsoup.parse(html, "", Parser.xmlParser());
		
		//test
		String filterType = "";
		String filterName = "";
		Elements fff = doc.getElementsByTag("filter");
		for(Element e: fff) { 
			filterType = e.attr("type");
			filterName = e.attr("name");
			filterNameMap.put(filterType + ","  + filterName, e.getElementsByTag("classname").text().trim());
		}
	}
/*	private static DocFilter getDocFilterInstance(String className, String regexp, 
			List<String> lstFields, String prefix, String suffix ) throws VeniceException { */
	@SuppressWarnings("unchecked")
	private static DocFilter getDocFilterInstance(String filterName, String className) throws VeniceException {
		DocFilter instance = null;
		try { 
			Class<? extends DocFilter> filterClass = 
					(Class<? extends DocFilter>) filterClassLoader.loadClass("search.crawl.napoli.filter." + className);
			instance = filterClass.newInstance();
			instance.setFilterName(filterName);
			/*
			instance.setRegexp(regexp);
			instance.setFields(lstFields);
			instance.setPattern(Pattern.compile(regexp,Pattern.MULTILINE | Pattern.DOTALL));
			instance.setPrefix(prefix);
			instance.setSuffix(suffix);
			 */
		} catch (ClassNotFoundException e) { 
			LOGGER.error("FILTER_CLASS_NOT_FOUNDED : " + filterName );
			throw new VeniceException(VeniceEnums.Errors.FILTER_CLASS_NOT_FOUNDED, e);
		} catch (InstantiationException e) { 
			LOGGER.error("Can not create an instance of filter class : " + filterName );
			throw new VeniceException(VeniceEnums.Errors.FILTER_CLASS_INSTANTIATIONEXCEPTION, e);
		} catch (IllegalAccessException e) { 
			LOGGER.error("IllegalAccessException - filter class : " + filterName );
			throw new VeniceException(VeniceEnums.Errors.FILTER_CLASS_ILLEGALACCESSException, e);
		}
		return instance;
	}
/*	private static ListFilter getListFilterInstance(String className, String regexp, 
			List<String> lstFields, String prefix, String suffix ) throws VeniceException { */
	@SuppressWarnings("unchecked")
	private static ListFilter getListFilterInstance(String filterName, String className) throws VeniceException { 	
		ListFilter instance = null;
		try { 
			Class<? extends ListFilter> filterClass = 
					(Class<? extends ListFilter>) filterClassLoader.loadClass("search.crawl.napoli.filter." + className);
			instance = filterClass.newInstance();
			instance.setFilterName(filterName);
			/*
			instance.setRegexp(regexp);
			instance.setFields(lstFields);
			instance.setPattern(Pattern.compile(regexp,Pattern.MULTILINE | Pattern.DOTALL));
			instance.setPrefix(prefix);
			instance.setSuffix(suffix);
			 */
		} catch (ClassNotFoundException e) { 
			LOGGER.error("FILTER_CLASS_NOT_FOUNDED : " + filterName );
			throw new VeniceException(VeniceEnums.Errors.FILTER_CLASS_NOT_FOUNDED, e);
		} catch (InstantiationException e) { 
			LOGGER.error("Can not create an instance of filter class : " + filterName );
			throw new VeniceException(VeniceEnums.Errors.FILTER_CLASS_INSTANTIATIONEXCEPTION, e);
		} catch (IllegalAccessException e) { 
			LOGGER.error("IllegalAccessException - filter class : " + filterName );
			throw new VeniceException(VeniceEnums.Errors.FILTER_CLASS_ILLEGALACCESSException, e);
		}
		return instance;
	}
	
	public static Filter getFilter(String name) throws VeniceException {
		return getFilter(name, "doc");
	}
	public static ListFilter getListFilter(String name) throws VeniceException {
		return (ListFilter)getFilter(name, "list");
	}
	public static Filter getFilter(String name, String type) throws VeniceException {
		String className = filterNameMap.get(type + "," + name);
		if (className == null)  { 
			if(type.equals("doc")) { 
				className = "CommonFilter";
			} else { 
				//TODO List Common Filter
			}
		}
		Filter instance = null;
		if (type.equals("list")) { 
			instance = getListFilterInstance(name, className);
		} else { 
			instance = getDocFilterInstance(name, className);
		}
		return instance;	
	}
	/*
	 * filter.xml 을 이용하던 것을 각 사이트별 필터.java 포함 시키도록 변경...
	 * 
	 * public static Filter getFilter_old(String name, String type) throws VeniceException {
		StringBuilder data = new StringBuilder();
		try { 
			BufferedReader br = new BufferedReader(
					new InputStreamReader(filterClassLoader.getResourceAsStream("filter.xml")));
			String line = "" ;
			while((line = br.readLine()) != null) {
				data.append(line + System.getProperty("line.separator"));
			}
			br.close();
		} catch (IOException e) { 
			System.out.println("IOException" );
			e.printStackTrace();
			throw new VeniceException(VeniceEnums.Errors.UNKNOWN, e);
		}
		
		String html = data.toString();
		Document doc = Jsoup.parse(html, "", Parser.xmlParser());
		Elements filtersEls = doc.getElementsByAttributeValue("name", name) ;
		
		Elements els = null;
		for (Element e : filtersEls) { 
			els = e.getElementsByAttributeValue("type", type);
			if (! els.isEmpty()) { 
				break;
			}
		}
			
		if (els.isEmpty()) { 
			//TODO : 해당 필터가 없다. general filter 를 가동하자~
		}
		
		String className = els.select("classname").text();
		String regexp = els.select("regexp").text().trim();
		String fields = els.select("fields").text();
		
		String prefix = "";
		String suffix = "";
		Elements tmpEls = els.select("prefix");
		if (! tmpEls.isEmpty()) { 
			prefix = tmpEls.text();
		}
		tmpEls = els.select("suffix");
		if (! tmpEls.isEmpty()) { 
			suffix = tmpEls.text();
		}

		String[] arrFields = fields.split(",");
		List<String> lstFields = new ArrayList<String>();
		for(String field : arrFields) { 
			lstFields.add(field.trim());
		}
		Filter instance = null;
		if (type.equals("list")) { 
			instance = getListFilterInstance(className, regexp, lstFields, prefix, suffix);
		} else { 
			instance = getDocFilterInstance(className, regexp, lstFields, prefix, suffix);
		}

		return instance;
	}*/
	/* 
	 * dom4j 를 Jsoup 으로 대체 했음
	 
	 public static Filter getFilter(String name) throws VeniceException {
		Filter instance = null;
		SAXReader reader = new SAXReader();
		try { 
			Document doc = reader.read(new File("D:\\workspace\\venice\\target\\classes\\filter.xml"));
		
			Node node = doc.selectSingleNode("//filters/filter[@name='" + name + "']");
			Element el = (Element)node;
			String className = el.elementText("classname");
			String regexp = el.elementText("regexp").trim();
			String fields = el.elementText("fields");
			String[] arrFields = fields.split(",");
			List<String> lstFields = new ArrayList();
			for(String field : arrFields) { 
				lstFields.add(field.trim());
			}
			ClassLoader filterClassLoader = ClassLoader.getSystemClassLoader();
			Class<? extends Filter> filterClass = 
					(Class<? extends Filter>) filterClassLoader.loadClass("search.crawl.napoli.filter." + className);
			
			instance = filterClass.newInstance();
			
			instance.setRegexp(regexp);
			instance.setFields(lstFields);
			instance.setPattern(Pattern.compile(regexp,Pattern.MULTILINE | Pattern.DOTALL));
			
		} catch (DocumentException de) { 
			System.out.println("DocumentException" );
			de.printStackTrace();
			throw new VeniceException(VeniceEnums.Errors.UNKNOWN, de);
		} catch (ClassNotFoundException e) { 
			System.out.println("ClassNotFoundException" );
			e.printStackTrace();
			throw new VeniceException(VeniceEnums.Errors.UNKNOWN, e);
		} catch (InstantiationException e) { 
			System.out.println("ClassNotFoundException" );
			e.printStackTrace();
			throw new VeniceException(VeniceEnums.Errors.UNKNOWN, e);
		} catch (IllegalAccessException e) { 
			System.out.println("ClassNotFoundException" );
			e.printStackTrace();
			throw new VeniceException(VeniceEnums.Errors.UNKNOWN, e);
		}
		return instance;
	}
	*/
}
