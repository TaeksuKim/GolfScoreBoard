package org.dolicoli.android.golfscoreboard.data.settings;

import org.dolicoli.android.golfscoreboard.Constants;
import org.dolicoli.android.golfscoreboard.data.RankingContainer;

public class Result implements RankingContainer {
	private int holeNumber;
	private int parNumber;

	private int[] originalScores;
	private int[] usedHandicaps;
	private int[] finalScores;

	private int[] rankings;
	private int[] sameRankingCounts;

	public Result(int holeNumber, int parNumber) {
		this.holeNumber = holeNumber;
		this.parNumber = parNumber;

		originalScores = new int[Constants.MAX_PLAYER_COUNT];
		usedHandicaps = new int[Constants.MAX_PLAYER_COUNT];
		finalScores = new int[Constants.MAX_PLAYER_COUNT];
		rankings = new int[Constants.MAX_PLAYER_COUNT];
		sameRankingCounts = new int[Constants.MAX_PLAYER_COUNT];

		for (int playerId = 0; playerId < Constants.MAX_PLAYER_COUNT; playerId++) {
			originalScores[playerId] = 0;
			usedHandicaps[playerId] = 0;
			finalScores[playerId] = 0;
			rankings[playerId] = 1;
			sameRankingCounts[playerId] = 1;
		}
	}

	public int getHoleNumber() {
		return holeNumber;
	}

	public int getParNumber() {
		return parNumber;
	}

	public int getOriginalScore(int playerId) {
		if (playerId < 0 || playerId > Constants.MAX_PLAYER_COUNT - 1)
			return 0;
		return originalScores[playerId];
	}

	public void setScore(int playerId, int score) {
		if (playerId < 0 || playerId > Constants.MAX_PLAYER_COUNT - 1)
			return;

		originalScores[playerId] = score;
	}

	public int getUsedHandicap(int playerId) {
		if (playerId < 0 || playerId > Constants.MAX_PLAYER_COUNT - 1)
			return 0;
		return usedHandicaps[playerId];
	}

	public void setUsedHandicap(int playerId, int handicap) {
		if (playerId < 0 || playerId > Constants.MAX_PLAYER_COUNT - 1)
			return;

		usedHandicaps[playerId] = handicap;
	}

	public int getFinalScore(int playerId) {
		if (playerId < 0 || playerId > Constants.MAX_PLAYER_COUNT - 1)
			return 0;
		return finalScores[playerId];
	}

	@Override
	public int getRanking(int playerId) {
		if (playerId < 0 || playerId > Constants.MAX_PLAYER_COUNT - 1)
			return 1;
		return rankings[playerId];
	}

	@Override
	public int getSameRankingCount(int playerId) {
		if (playerId < 0 || playerId > Constants.MAX_PLAYER_COUNT - 1)
			return 1;
		return sameRankingCounts[playerId];
	}

	public void clear() {
		for (int playerId = 0; playerId < Constants.MAX_PLAYER_COUNT; playerId++) {
			originalScores[playerId] = 0;
			usedHandicaps[playerId] = 0;
			finalScores[playerId] = 0;
			rankings[playerId] = 1;
			sameRankingCounts[playerId] = 1;
		}
	}

	public void calculate() {
		int finalScore = 0;

		for (int i = 0; i < Constants.MAX_PLAYER_COUNT; i++) {
			finalScore = originalScores[i] - usedHandicaps[i];
			finalScores[i] = finalScore;
			rankings[i] = 1;
			sameRankingCounts[i] = 1;
		}

		int ranking = 0;
		int sameRankingCount = 0;
		for (int i = 0; i < Constants.MAX_PLAYER_COUNT; i++) {
			finalScore = finalScores[i];
			ranking = rankings[i];
			sameRankingCount = 1;
			for (int j = 0; j < Constants.MAX_PLAYER_COUNT; j++) {
				if (i == j)
					continue;
				if (finalScores[j] < finalScore) {
					ranking++;
				} else if (finalScores[j] == finalScore) {
					sameRankingCount++;
				}
			}
			rankings[i] = ranking;
			sameRankingCounts[i] = sameRankingCount;
		}
	}
}
