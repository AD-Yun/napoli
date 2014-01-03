package search.crawl.napoli.filter.know;

import java.util.List;
import java.util.regex.Pattern;

import search.crawl.napoli.filter.FilterElement;

public class KnowFilterElement extends FilterElement {

	private String aRegexp;
	private String sRegexp;
	
	private String answerHtml;
	private String sectionHtml;
	
	private String selectedClue = "";
	private int sectionTarget;
	private Pattern aPattern;
	private Pattern sPattern;
	
	
	private List<String> aFields;
	private List<String> sFields;
	
	public KnowFilterElement(String regexp, List<String> fields, Pattern pattern,
			String prefix, String suffix, String delimiter) {
		super(regexp, fields, pattern, prefix, suffix, delimiter);
	}
	
	public KnowFilterElement(String regexp, List<String> fields, Pattern pattern,
			String prefix, String suffix) {
		super(regexp, fields, pattern, prefix, suffix);
	}

	public String getARegexp() {
		return aRegexp;
	}

	public void setARegexp(String a_regexp) {
		this.aRegexp = a_regexp;
		Pattern pattern = Pattern.compile(a_regexp, Pattern.MULTILINE | Pattern.DOTALL);
		this.aPattern = pattern;
	}

	public String getSRegexp() {
		return sRegexp;
	}

	public void setSRegexp(String sRegexp) {
		this.sRegexp = sRegexp;
		Pattern pattern = Pattern.compile(sRegexp, Pattern.MULTILINE | Pattern.DOTALL);
		this.sPattern = pattern;
	}

	public String getaRegexp() {
		return aRegexp;
	}

	public void setaRegexp(String aRegexp) {
		this.aRegexp = aRegexp;
	}

	public String getsRegexp() {
		return sRegexp;
	}

	public void setsRegexp(String sRegexp) {
		this.sRegexp = sRegexp;
	}

	public Pattern getaPattern() {
		return aPattern;
	}

	public void setaPattern(Pattern aPattern) {
		this.aPattern = aPattern;
	}

	public String getAnswerHtml() {
		return answerHtml;
	}

	public void setAnswerHtml(String answerHtml) {
		this.answerHtml = answerHtml;
	}

	public String getSectionHtml() {
		return sectionHtml;
	}

	public void setSectionHtml(String sectionHtml) {
		this.sectionHtml = sectionHtml;
	}

	public String getSelectedClue() {
		return selectedClue;
	}

	public void setSelectedClue(String selectedClue) {
		this.selectedClue = selectedClue;
	}
	
	public Pattern getsPattern() {
		return sPattern;
	}

	public void setsPattern(Pattern sPattern) {
		this.sPattern = sPattern;
	}

	public List<String> getaFields() {
		return aFields;
	}

	public void setaFields(List<String> aFields) {
		this.aFields = aFields;
	}

	public List<String> getsFields() {
		return sFields;
	}

	public void setsFields(List<String> sFields) {
		this.sFields = sFields;
	}

	public int getSectionTarget() {
		return sectionTarget;
	}

	public void setSectionTarget(int sectionTarget) {
		this.sectionTarget = sectionTarget;
	}

}
