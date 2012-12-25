package org.dolicoli.android.golfscoreboard.net;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.dolicoli.android.golfscoreboard.data.GameHistoryListItem;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.util.Log;

public class GameListParser extends ResponseParser {

	private static final String TAG = "GameListParser";

	public static List<GameHistoryListItem> getGameList(Context context,
			String contents) throws ResponseException {
		List<GameHistoryListItem> gameList = new ArrayList<GameHistoryListItem>();

		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			ByteArrayInputStream bis = new ByteArrayInputStream(
					contents.getBytes());
			InputStream stream = bis;

			GameHistoryListItem gameSetting = null;
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
						String result = xpp.getAttributeValue(null, "result");
						if (result.equals("ERROR")) {
							String errorMessage = xpp.getAttributeValue(null,
									"message");
							throw new ResponseException(errorMessage);
						}
					} else if (xpp.getName().equals("game")) {
						String gameId = xpp.getAttributeValue(null, "gameId");

						String playerCountValue = xpp.getAttributeValue(null,
								"playerCount");

						String gameDateValue = xpp.getAttributeValue(null,
								"gameDate");

						gameSetting = new GameHistoryListItem(gameId,
								getInt(playerCountValue),
								getLong(gameDateValue));

					} else if (xpp.getName().equals("player")) {
						String value = xpp.getAttributeValue(null, "id");
						int playerId = getInt(value);
						String playerName = xpp.getAttributeValue(null, "name");
						gameSetting.setPlayerName(playerId, playerName);
					}
					break;
				case XmlPullParser.END_TAG:
					if (xpp.getName().equals("game")) {
						if (gameSetting != null) {
							gameList.add(gameSetting);
							gameSetting = null;
						}
					}
					break;
				case XmlPullParser.TEXT:
					break;
				}
				eventType = xpp.next();
			}

			Collections.sort(gameList, new Comparator<GameHistoryListItem>() {
				@Override
				public int compare(GameHistoryListItem lhs,
						GameHistoryListItem rhs) {
					int compare = lhs.getDate().compareTo(rhs.getDate());
					return (-1 * compare);
				}
			});
			return gameList;
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
			return null;
		}
	}
}
