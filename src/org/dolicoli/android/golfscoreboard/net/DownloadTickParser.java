package org.dolicoli.android.golfscoreboard.net;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.util.Log;

public class DownloadTickParser extends ResponseParser {

	private static final String TAG = "HistoryListParser";

	private long tick;

	public long getTick() {
		return tick;
	}

	public boolean parse(Context context, String contents)
			throws ResponseException {
		tick = 0L;

		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			ByteArrayInputStream bis = new ByteArrayInputStream(
					contents.getBytes());
			InputStream stream = bis;

			xpp.setInput(stream, "UTF-8");
			int eventType = xpp.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.END_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if (xpp.getName().equals("response")) {
						String resultCode = xpp.getAttributeValue(null,
								"result");
						if (resultCode.equals("ERROR")) {
							String errorMessage = xpp.getAttributeValue(null,
									"message");
							throw new ResponseException(errorMessage);
						}

						String tickString = xpp.getAttributeValue(null, "tick");
						if (tickString != null && !tickString.isEmpty()) {
							try {
								tick = Long.parseLong(tickString);
							} catch (Throwable t) {
							}
						}
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				case XmlPullParser.TEXT:
					break;
				}
				eventType = xpp.next();
			}
			return true;
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
			return false;
		}
	}
}
