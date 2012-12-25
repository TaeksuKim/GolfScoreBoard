package org.dolicoli.android.golfscoreboard.net;

public class HttpScraper {

	public static final String EUC_KR = "EUC-KR";
	public static final String UTF_8 = "UTF-8";

	public static String scrap(String uri, String encoding) throws Exception {
		return TimeOutHttpScraper.scrap(uri, encoding);
	}

	public static String scrap(String uri) throws Exception {
		return TimeOutHttpScraper.scrap(uri, null);
	}
}
