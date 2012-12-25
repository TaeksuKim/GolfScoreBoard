package org.dolicoli.android.golfscoreboard.data.settings;

import org.dolicoli.android.golfscoreboard.Constants;

public class PlayerSetting {

	private String playDate;
	private String[] playerNames;
	private int[] handicaps;
	private int[] extraScores;

	public PlayerSetting() {
		playerNames = new String[Constants.MAX_PLAYER_COUNT];
		handicaps = new int[Constants.MAX_PLAYER_COUNT];
		extraScores = new int[Constants.MAX_PLAYER_COUNT];

		for (int playerId = 0; playerId < Constants.MAX_PLAYER_COUNT; playerId++) {
			playerNames[playerId] = "Player " + (playerId + 1);
			handicaps[playerId] = 0;
			extraScores[playerId] = 0;
		}
	}

	public String getPlayDate() {
		return playDate;
	}

	public void setPlayDate(String playDate) {
		this.playDate = playDate;
	}

	public String getPlayerName(int playerId) {
		if (playerId < 0 || playerId > Constants.MAX_PLAYER_COUNT - 1)
			return "";
		return playerNames[playerId];
	}

	public void setPlayerName(int playerId, String name) {
		if (playerId < 0 || playerId > Constants.MAX_PLAYER_COUNT - 1)
			return;

		playerNames[playerId] = name;
	}

	public int getHandicap(int playerId) {
		if (playerId < 0 || playerId > Constants.MAX_PLAYER_COUNT - 1)
			return 0;
		return handicaps[playerId];
	}

	public void setHandicap(int playerId, int handicap) {
		if (playerId < 0 || playerId > Constants.MAX_PLAYER_COUNT - 1)
			return;

		handicaps[playerId] = handicap;
	}

	public int getExtraScore(int playerId) {
		if (playerId < 0 || playerId > Constants.MAX_PLAYER_COUNT - 1)
			return 0;
		return extraScores[playerId];
	}

	public void setExtraScore(int playerId, int extraScore) {
		if (playerId < 0 || playerId > Constants.MAX_PLAYER_COUNT - 1)
			return;

		extraScores[playerId] = extraScore;
	}

	public void copy(PlayerSetting setting) {
		this.playDate = setting.playDate;
		for (int playerId = 0; playerId < Constants.MAX_PLAYER_COUNT; playerId++) {
			this.playerNames[playerId] = setting.playerNames[playerId];
			this.handicaps[playerId] = setting.handicaps[playerId];
			this.extraScores[playerId] = setting.extraScores[playerId];
		}
	}
}
