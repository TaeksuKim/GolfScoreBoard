package org.dolicoli.android.golfscoreboard.data.settings;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.dolicoli.android.golfscoreboard.Constants;

public class GameSetting {

	private String playDate;
	private int holeCount;
	private int playerCount;
	private int gameFee;
	private int extraFee;
	private int rankingFee;
	private int[] holeFees;
	private int[] rankingFees;
	private Date date;

	public GameSetting() {
		this.holeCount = 9;
		this.playerCount = 4;
		this.gameFee = 60000;
		this.extraFee = 0;
		this.rankingFee = 0;
		this.holeFees = new int[Constants.MAX_PLAYER_COUNT];
		this.holeFees[0] = 0;
		this.holeFees[1] = 1000;
		this.holeFees[2] = 2000;
		this.holeFees[3] = 3000;
		this.holeFees[4] = 4000;
		this.holeFees[5] = 5000;
		this.rankingFees = new int[Constants.MAX_PLAYER_COUNT];
		this.rankingFees[0] = 0;
		this.rankingFees[1] = 0;
		this.rankingFees[2] = 0;
		this.rankingFees[3] = 0;
		this.rankingFees[4] = 0;
		this.rankingFees[5] = 0;
		this.date = new Date();
	}

	public String getPlayDate() {
		return playDate;
	}

	public void setPlayDate(String playDate) {
		this.playDate = playDate;
	}

	public int getHoleCount() {
		return holeCount;
	}

	public void setHoleCount(int holeCount) {
		this.holeCount = holeCount;
	}

	public int getPlayerCount() {
		return playerCount;
	}

	public void setPlayerCount(int playerCount) {
		this.playerCount = playerCount;
	}

	public int getGameFee() {
		return gameFee;
	}

	public void setGameFee(int gameFee) {
		this.gameFee = gameFee;
	}

	public int getExtraFee() {
		return extraFee;
	}

	public void setExtraFee(int extraFee) {
		this.extraFee = extraFee;
	}

	public int getHoleFee() {
		return getTotalFee() - rankingFee;
	}

	public int getTotalFee() {
		return gameFee + extraFee;
	}

	public int getRankingFee() {
		return rankingFee;
	}

	public void setRankingFee(int fee) {
		this.rankingFee = fee;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(long time) {
		this.date.setTime(time);
	}

	public void setHoleFeeForRanking(int ranking, int fee) {
		if (ranking < 1 || ranking > Constants.MAX_PLAYER_COUNT)
			return;

		holeFees[ranking - 1] = fee;
	}

	public int getHoleFeeForRanking(int ranking) {
		if (ranking < 1 || ranking > Constants.MAX_PLAYER_COUNT)
			return 0;
		return holeFees[ranking - 1];
	}

	public void setRankingFeeForRanking(int ranking, int fee) {
		if (ranking < 1 || ranking > Constants.MAX_PLAYER_COUNT)
			return;

		rankingFees[ranking - 1] = fee;
	}

	public int getRankingFeeForRanking(int ranking) {
		if (ranking < 1 || ranking > Constants.MAX_PLAYER_COUNT)
			return 0;
		return rankingFees[ranking - 1];
	}

	public void copy(GameSetting setting) {
		playDate = setting.playDate;
		holeCount = setting.holeCount;
		playerCount = setting.playerCount;
		gameFee = setting.gameFee;
		extraFee = setting.extraFee;
		rankingFee = setting.rankingFee;
		for (int i = 0; i < Constants.MAX_PLAYER_COUNT; i++) {
			holeFees[i] = setting.holeFees[i];
			rankingFees[i] = setting.rankingFees[i];
		}
		date = setting.date;
	}

	private static final SimpleDateFormat GAME_ID_FORMAT = new SimpleDateFormat(
			"yyyyMMddHHmm", Locale.getDefault());

	public static String toGameIdFormat(Date date) {
		return GAME_ID_FORMAT.format(date);
	}
}
