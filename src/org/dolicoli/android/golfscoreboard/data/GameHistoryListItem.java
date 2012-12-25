package org.dolicoli.android.golfscoreboard.data;

import java.util.Date;

import org.dolicoli.android.golfscoreboard.Constants;

public class GameHistoryListItem {

	private String gameId;
	private int playerCount;
	private Date date;
	private String[] playerNames;

	public GameHistoryListItem(String gameId, int playerCount, long time) {
		this.gameId = gameId;
		this.playerCount = playerCount;
		this.playerNames = new String[Constants.MAX_PLAYER_COUNT];
		for (int i = 0; i < Constants.MAX_PLAYER_COUNT; i++) {
			this.playerNames[i] = "";
		}
		this.date = new Date();
		this.date.setTime(time);
	}

	public String getGameId() {
		return gameId;
	}

	public int getPlayerCount() {
		return playerCount;
	}

	public Date getDate() {
		return date;
	}

	public void setPlayerName(int playerId, String name) {
		if (playerId < 0 || playerId > Constants.MAX_PLAYER_COUNT - 1)
			return;

		playerNames[playerId] = name;
	}

	public String getPlayerName(int playerId) {
		if (playerId < 0 || playerId > Constants.MAX_PLAYER_COUNT - 1)
			return "";
		return playerNames[playerId];
	}
}
