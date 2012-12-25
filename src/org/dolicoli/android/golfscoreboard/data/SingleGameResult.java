package org.dolicoli.android.golfscoreboard.data;

import java.util.ArrayList;
import java.util.Date;

import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;
import org.dolicoli.android.golfscoreboard.data.settings.PlayerSetting;
import org.dolicoli.android.golfscoreboard.data.settings.Result;

public class SingleGameResult {
	private GameSetting gameSetting;
	private PlayerSetting playerSetting;
	private ArrayList<Result> results;
	private UsedHandicap usedHandicap;

	public SingleGameResult() {
		gameSetting = null;
		playerSetting = null;
		results = new ArrayList<Result>();
		usedHandicap = null;
	}

	public void setGameSetting(GameSetting gameSetting) {
		this.gameSetting = gameSetting;
	}

	public void setPlayerSetting(PlayerSetting playerSetting) {
		this.playerSetting = playerSetting;
	}

	public void setResults(Iterable<Result> results) {
		this.results.clear();
		for (Result result : results) {
			this.results.add(result);
		}
	}

	public void setUsedHandicap(UsedHandicap usedHandicap) {
		this.usedHandicap = usedHandicap;
	}

	public String getPlayDate() {
		return gameSetting.getPlayDate();
	}

	public Date getDate() {
		return gameSetting.getDate();
	}

	@Override
	public String toString() {
		if (gameSetting == null)
			return "GAME @ UNKNOWN";

		Date date = gameSetting.getDate();
		return "GAME @ " + date.toString();
	}

	public int getHoleCount() {
		return gameSetting.getHoleCount();
	}

	public int getPlayerCount() {
		return gameSetting.getPlayerCount();
	}

	public int getTotalFee() {
		return gameSetting.getTotalFee();
	}

	public int getRankingFee() {
		return gameSetting.getRankingFee();
	}

	public int getHoleFee() {
		return gameSetting.getHoleFee();
	}

	public int getHoleFeeForRanking(int ranking) {
		return gameSetting.getHoleFeeForRanking(ranking);
	}

	public int getRankingFeeForRanking(int ranking) {
		return gameSetting.getRankingFeeForRanking(ranking);
	}

	public String getPlayerName(int playerId) {
		return playerSetting.getPlayerName(playerId);
	}

	public int getHandicap(int playerId) {
		return playerSetting.getHandicap(playerId);
	}

	public int getExtraScore(int playerId) {
		return playerSetting.getExtraScore(playerId);
	}

	public int getUsedHandicap(int playerId) {
		return usedHandicap.getUsedHandicap(playerId);
	}

	public Iterable<Result> getResults() {
		return results;
	}
}
