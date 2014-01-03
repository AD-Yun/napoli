package search.crawl.napoli.util;

public final class GrabHtml {
	private GrabHtml() { }
	public static Grabber create(String url) {
		return new Grabber(url);
	}
}
