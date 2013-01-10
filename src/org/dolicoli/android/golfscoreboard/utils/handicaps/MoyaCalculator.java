package org.dolicoli.android.golfscoreboard.utils.handicaps;

import java.util.ArrayList;
import java.util.HashMap;

import org.dolicoli.android.golfscoreboard.R;

public class MoyaCalculator implements HandicapCalculator {

	private static final int AVG_COUNT_THRESHOLD = 6;
	private static final float RATIO1 = 0.8F;
	private static final float RATIO2 = 0.9F;
	private static final float RATIO3 = 1.0F;
	private static final float MAX_HANDICAP = 72F;

	private HashMap<String, PlayerScore> playerScoreMap;

	@Override
	public String getName(ResourceContainer context) {
		if (context == null)
			return "";
		return context.getString(R.string.moya_calculator_name);
	}

	@Override
	public void calculate(String[] playerNames, Iterable<GameResultItem> items) {
		playerScoreMap = new HashMap<String, PlayerScore>();

		int playerCount = playerNames.length;
		if (playerCount < 1)
			return;

		for (String playerName : playerNames) {
			playerScoreMap.put(playerName, new PlayerScore(playerName));
		}

		for (GameResultItem item : items) {
			for (String playerName : playerNames) {
				if (!item.containsPlayer(playerName))
					continue;

				if (!playerScoreMap.containsKey(playerName))
					continue;

				PlayerScore playerScore = playerScoreMap.get(playerName);
				if (playerScore.gameCount >= AVG_COUNT_THRESHOLD)
					continue;

				int score = item.getEighteenHoleScore(playerName);
				playerScore.increaseScore(playerCount, score,
						item.getRanking(playerName),
						item.getEighteenHoleFee(playerName));
			}
		}

		for (String playerName : playerNames) {
			if (!playerScoreMap.containsKey(playerName))
				continue;

			PlayerScore playerScore = playerScoreMap.get(playerName);
			playerScore.calculateAverageScore();
		}

		float minAvgScoreFloat = 72F;
		for (String playerName : playerNames) {
			if (!playerScoreMap.containsKey(playerName))
				continue;

			PlayerScore playerScore = playerScoreMap.get(playerName);
			if (playerScore.gameCount < 1)
				continue;

			float avgScore = playerScore.avgScore;
			if (minAvgScoreFloat > avgScore) {
				minAvgScoreFloat = avgScore;
			}
		}

		int minAvgScore = (int) Math.floor(minAvgScoreFloat);

		for (String playerName : playerNames) {
			if (!playerScoreMap.containsKey(playerName))
				continue;

			PlayerScore playerScore = playerScoreMap.get(playerName);

			if (playerScore.gameCount < 1)
				playerScore.avgScore = MAX_HANDICAP;

			int diff = (int) Math.floor(playerScore.avgScore) - minAvgScore;

			if (diff < 10) {
				playerScore.handicap = (int) (diff * RATIO1);
			} else if (diff < 20) {
				playerScore.handicap = (int) (diff * RATIO2);
			} else {
				playerScore.handicap = (int) (diff * RATIO3);
			}
		}
	}

	@Override
	public float getAvgScore(String playerName) {
		if (!playerScoreMap.containsKey(playerName))
			return 0F;
		return playerScoreMap.get(playerName).avgScore;
	}

	@Override
	public int getHandicap(String playerName) {
		if (!playerScoreMap.containsKey(playerName))
			return 0;
		return playerScoreMap.get(playerName).handicap;
	}

	@Override
	public int getGameCount(String playerName) {
		if (!playerScoreMap.containsKey(playerName))
			return 0;
		return playerScoreMap.get(playerName).gameCount;
	}

	private static class PlayerScore implements Comparable<PlayerScore> {
		private String playerName;
		private int gameCount;
		private ArrayList<Integer> scores;
		private float avgScore;
		private int handicap;

		public PlayerScore(String name) {
			this.playerName = name;
			this.gameCount = 0;
			this.scores = new ArrayList<Integer>();
			this.avgScore = 0F;
			this.handicap = 0;
		}

		public void increaseScore(int playerCount, int score, int ranking,
				int fee) {
			if (gameCount < 1) {
				scores.add(score);
				scores.add(score);
				scores.add(score);
				scores.add(score);
			} else if (gameCount < 3) {
				scores.add(score);
				scores.add(score);
				scores.add(score);
			} else if (gameCount < 5) {
				scores.add(score);
				scores.add(score);
			} else {
				scores.add(score);
			}

			if (playerCount > 2) {
				for (int i = 0; i < playerCount; i++) {
					// 등수 portion
					int portion = playerCount - ranking;

					for (int j = 0; j < portion; j++) {
						scores.add(score);
					}
				}

				// ranking 및 금액 portion 적용
				if (ranking == 1 && fee < 15000) {
					scores.add(score);
				}
			}

			this.gameCount++;
		}

		public void calculateAverageScore() {
			int count = scores.size();
			if (count < 1) {
				avgScore = MAX_HANDICAP;
			} else {
				int sumOfScore = 0;
				for (int score : scores)
					sumOfScore += score;
				avgScore = (float) sumOfScore / (float) count;
			}
		}

		@Override
		public int compareTo(PlayerScore another) {
			if (gameCount <= 0 && another.gameCount > 0)
				return 1;
			if (gameCount > 0 && another.gameCount <= 0)
				return -1;
			if (avgScore < another.avgScore)
				return -1;
			if (avgScore > another.avgScore)
				return 1;
			return 0;
		}

		@Override
		public String toString() {
			return playerName + "-" + avgScore;
		}
	}
}
