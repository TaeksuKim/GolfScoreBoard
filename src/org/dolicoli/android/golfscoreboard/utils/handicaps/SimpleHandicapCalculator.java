package org.dolicoli.android.golfscoreboard.utils.handicaps;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

public class SimpleHandicapCalculator implements HandicapCalculator {

	private static final int AVG_COUNT_THRESHOLD = 7;
	private static final float RATIO1 = 0.8F;
	private static final float RATIO2 = 0.85F;
	private static final float RATIO3 = 1.0F;
	private static final float MAX_HANDICAP = 72F;

	private HashMap<String, PlayerScore> playerScoreMap = null;

	@Override
	public String getName(ResourceContainer context) {
		return "기본 알고리즘";
	}

	@Override
	public void calculate(String[] playerNames, Iterable<GameResultItem> items) {
		playerScoreMap = new HashMap<String, PlayerScore>();
		if (playerNames.length < 1)
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
				playerScore.increaseScore(score);
			}
		}

		for (String playerName : playerNames) {
			if (!playerScoreMap.containsKey(playerName))
				continue;

			PlayerScore playerScore = playerScoreMap.get(playerName);
			playerScore.calculateAverageScore();
		}

		Collection<PlayerScore> values = playerScoreMap.values();
		PlayerScore[] playerScores = new PlayerScore[values.size()];
		values.toArray(playerScores);

		if (playerScores.length < 1)
			return;

		Arrays.sort(playerScores);

		float minAvgScoreFloat = 100.0F;
		for (String playerName : playerNames) {
			if (!playerScoreMap.containsKey(playerName))
				continue;

			PlayerScore playerScore = playerScoreMap.get(playerName);
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
		return playerScoreMap.get(playerName).getGameCount();
	}

	private static class PlayerScore implements Comparable<PlayerScore> {
		private String playerName;
		private int gameCount;
		private int sumOfScore;
		private int maxScore, minScore;
		private float avgScore;
		private int handicap;

		public PlayerScore(String name) {
			this.playerName = name;
			this.gameCount = 0;
			this.sumOfScore = 0;
			this.avgScore = 0F;
			this.handicap = 0;
			this.maxScore = 0;
			this.minScore = 0;
		}

		public int getGameCount() {
			if (gameCount < 3)
				return gameCount;
			return gameCount - 2;
		}

		public void increaseScore(int score) {
			if (gameCount < 1) {
				this.maxScore = score;
				this.minScore = score;
			} else {
				if (minScore > score) {
					this.minScore = score;
				}

				if (maxScore < score) {
					this.maxScore = score;
				}
			}
			this.sumOfScore += score;
			this.gameCount++;
		}

		public void calculateAverageScore() {
			if (gameCount < 1) {
				avgScore = MAX_HANDICAP;
			} else if (gameCount < 3) {
				avgScore = (float) sumOfScore / (float) gameCount;
			} else {
				// 가장 높은 점수와 가장 낮은 점수를 제외함
				avgScore = (float) (sumOfScore - maxScore - minScore)
						/ (float) (gameCount - 2);
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
