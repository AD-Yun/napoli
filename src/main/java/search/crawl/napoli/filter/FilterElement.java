package search.crawl.napoli.filter;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class FilterElement {
	private String regexp;
	private List<String> fields;
	private Pattern pattern;
	private String prefix;
	private String suffix;
	private String delimiter;
	private Set exceptHosts ;
	public FilterElement() { 
		
	}
	public FilterElement(String regexp, List<String> fields, Pattern pattern,
			String prefix, String suffix, String delimiter) {
		super();
		this.regexp = regexp;
		this.fields = fields;
		this.pattern = pattern;
		this.prefix = prefix;
		this.suffix = suffix;
		this.delimiter = delimiter;
	}
	public FilterElement(String regexp, List<String> fields, Pattern pattern,
			String prefix, String suffix) {
		super();
		this.regexp = regexp;
		this.fields = fields;
		this.pattern = pattern;
		this.prefix = prefix;
		this.suffix = suffix;
		this.delimiter = "";
	}
	
	public String getRegexp() {
		return regexp;
	}
	public void setRegexp(String regexp) {
		this.regexp = regexp;
	}
	public List<String> getFields() {
		return fields;
	}
	public void setFields(List<String> fields) {
		this.fields = fields;
	}
	public Pattern getPattern() {
		return pattern;
	}
	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	public String getDelimiter() {
		return delimiter;
	}
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
	public Set getExceptHosts() {
		return exceptHosts;
	}
	public void setExceptHosts(Set exceptHosts) {
		this.exceptHosts = exceptHosts;
	}
	
}
