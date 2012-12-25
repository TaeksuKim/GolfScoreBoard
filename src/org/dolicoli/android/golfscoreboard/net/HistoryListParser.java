package org.dolicoli.android.golfscoreboard.net;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;
import org.dolicoli.android.golfscoreboard.data.settings.PlayerSetting;
import org.dolicoli.android.golfscoreboard.data.settings.Result;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.util.Log;

public class HistoryListParser extends ResponseParser {

	private static final String TAG = "HistoryListParser";

	private ArrayList<History> historyList;

	public ArrayList<History> getHistoryList() {
		return historyList;
	}

	public boolean parse(Context context, String contents)
			throws ResponseException {
		historyList = new ArrayList<History>();

		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			ByteArrayInputStream bis = new ByteArrayInputStream(
					contents.getBytes());
			InputStream stream = bis;

			xpp.setInput(stream, "UTF-8");
			int eventType = xpp.getEventType();

			GameSetting gameSetting = null;
			PlayerSetting playerSetting = null;
			ArrayList<Result> results = null;
			Result result = null;
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
					} else if (xpp.getName().equals("game")) {
						gameSetting = new GameSetting();
						playerSetting = new PlayerSetting();
						results = new ArrayList<Result>();

						String value = xpp.getAttributeValue(null, "gameId");
						value = xpp.getAttributeValue(null, "gameDate");
						gameSetting.setDate(getLong(value));

						value = xpp.getAttributeValue(null, "holeCount");
						gameSetting.setHoleCount(getInt(value));

						value = xpp.getAttributeValue(null, "playerCount");
						gameSetting.setPlayerCount(getInt(value));

					} else if (xpp.getName().equals("fee")) {
						String value = xpp.getAttributeValue(null, "holeFee");
						gameSetting.setGameFee(getInt(value));

						value = xpp.getAttributeValue(null, "extraFee");
						gameSetting.setExtraFee(getInt(value));

						value = xpp.getAttributeValue(null, "rankingFee");
						gameSetting.setRankingFee(getInt(value));

					} else if (xpp.getName().equals("holeFee")) {
						String value = xpp.getAttributeValue(null, "ranking");
						int ranking = getInt(value);
						value = xpp.getAttributeValue(null, "fee");
						int fee = getInt(value);
						gameSetting.setHoleFeeForRanking(ranking, fee);

					} else if (xpp.getName().equals("rankingFee")) {
						String value = xpp.getAttributeValue(null, "ranking");
						int ranking = getInt(value);
						value = xpp.getAttributeValue(null, "fee");
						int fee = getInt(value);
						gameSetting.setRankingFeeForRanking(ranking, fee);

					} else if (xpp.getName().equals("player")) {
						String value = xpp.getAttributeValue(null, "id");
						int playerId = getInt(value);
						String playerName = xpp.getAttributeValue(null, "name");
						playerSetting.setPlayerName(playerId, playerName);
						value = xpp.getAttributeValue(null, "handicap");
						int handicap = getInt(value);
						playerSetting.setHandicap(playerId, handicap);

						try {
							value = xpp.getAttributeValue(null, "extraScore");
							int extraScore = getInt(value);
							playerSetting.setExtraScore(playerId, extraScore);
						} catch (Throwable t) {
						}
					} else if (xpp.getName().equals("results")) {
					} else if (xpp.getName().equals("result")) {

						String value = xpp
								.getAttributeValue(null, "holeNumber");
						int holeNumber = getInt(value);

						value = xpp.getAttributeValue(null, "parCount");
						int parCount = getInt(value);

						result = new Result(holeNumber, parCount);
					} else if (xpp.getName().equals("score")) {
						if (result != null) {
							String value = xpp.getAttributeValue(null,
									"playerId");
							int playerId = getInt(value);

							value = xpp.getAttributeValue(null, "score");
							int score = getInt(value);

							value = xpp.getAttributeValue(null, "usedHandicap");
							int usedHandicap = getInt(value);

							result.setScore(playerId, score);
							result.setUsedHandicap(playerId, usedHandicap);
						}
					}
					break;
				case XmlPullParser.END_TAG:
					if (xpp.getName().equals("result")) {
						if (result != null) {
							results.add(result);
						}
						result = null;
					} else if (xpp.getName().equals("game")) {
						historyList.add(new History(gameSetting, playerSetting,
								results));
					}
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

	public static class History {
		private GameSetting gameSetting;
		private PlayerSetting playerSetting;
		private ArrayList<Result> results;

		public History(GameSetting gameSetting, PlayerSetting playerSetting,
				ArrayList<Result> results) {
			this.gameSetting = gameSetting;
			this.playerSetting = playerSetting;
			this.results = new ArrayList<Result>();
			this.results.addAll(results);
		}

		public GameSetting getGameSetting() {
			return gameSetting;
		}

		public PlayerSetting getPlayerSetting() {
			return playerSetting;
		}

		public List<Result> getResults() {
			return results;
		}
	}
}
