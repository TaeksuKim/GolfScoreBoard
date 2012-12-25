package org.dolicoli.android.golfscoreboard.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;
import org.dolicoli.android.golfscoreboard.data.settings.PlayerSetting;
import org.dolicoli.android.golfscoreboard.data.settings.Result;
import org.dolicoli.android.golfscoreboard.utils.FeeCalculator;
import org.dolicoli.android.golfscoreboard.utils.PlayerUIUtil;
import org.dolicoli.android.golfscoreboard.utils.handicaps.HandicapCalculator.GameResultItem;

import android.util.SparseArray;

public class GameAndResult implements Comparable<GameAndResult>, GameResultItem {

	private GameSetting gameSetting;
	private PlayerSetting playerSetting;
	private ArrayList<Result> results;
	private UsedHandicap usedHandicap;
	private int holeCount;
	private int currentHole;
	private SparseArray<PlayerScore> playerIdScoreMap;
	private HashMap<String, PlayerScore> playerScoreMap;

	public GameAndResult(GameSetting gameSetting, PlayerSetting playerSetting,
			ArrayList<Result> results) {
		this.gameSetting = gameSetting;
		this.playerSetting = playerSetting;
		this.results = results;

		Collections.sort(results, new Comparator<Result>() {
			@Override
			public int compare(Result lhs, Result rhs) {
				if (lhs.getHoleNumber() < rhs.getHoleNumber())
					return -1;
				if (lhs.getHoleNumber() > rhs.getHoleNumber())
					return 1;
				return 0;
			}
		});

		calculateRanking();

	}

	private void calculateRanking() {
		holeCount = gameSetting.getHoleCount();

		int playerCount = gameSetting.getPlayerCount();
		playerIdScoreMap = new SparseArray<PlayerScore>();
		playerScoreMap = new HashMap<String, PlayerScore>();

		ArrayList<PlayerScore> list = new ArrayList<PlayerScore>();
		for (int playerId = 0; playerId < playerCount; playerId++) {
			PlayerScore playerScore = null;
			if (usedHandicap == null) {
				playerScore = new PlayerScore(playerId,
						playerSetting.getPlayerName(playerId),
						playerSetting.getHandicap(playerId));
			} else {
				playerScore = new PlayerScore(playerId,
						playerSetting.getPlayerName(playerId),
						playerSetting.getHandicap(playerId)
								- usedHandicap.getUsedHandicap(playerId));
			}
			playerIdScoreMap.put(playerId, playerScore);
			list.add(playerId, playerScore);
		}

		currentHole = 0;
		for (Result result : results) {
			if (currentHole < result.getHoleNumber())
				currentHole = result.getHoleNumber();

			int[] fees = FeeCalculator.calculateFee(gameSetting, result);

			for (int playerId = 0; playerId < playerCount; playerId++) {
				PlayerScore playerScore = list.get(playerId);

				playerScore.increaseScore(result.getFinalScore(playerId));
				playerScore.increaseOriginalScore(result
						.getOriginalScore(playerId));
				playerScore.increaseOriginalHoleFee(fees[playerId]);
				playerScore
						.setAdjustedHoleFee(playerScore.getOriginalHoleFee());

				playerScore.addHoleRanking(result.getRanking(playerId));
			}
		}

		for (int playerId = 0; playerId < playerCount; playerId++) {
			PlayerScore playerScore = list.get(playerId);
			playerScore.setExtraScore(playerSetting.getExtraScore(playerId));
		}
		Collections.sort(list);
		calculateRanking(list);
		calculateRankingFee(list, gameSetting);
		calculateAvgRanking(list);
		calculateOverPar(list);

		adjustFee(list, gameSetting, currentHole >= gameSetting.getHoleCount());
		Collections.sort(list); // Sort again after adjustFee has been set

		for (int i = 0; i < playerCount; i++) {
			String playerName = playerSetting.getPlayerName(i);
			playerName = PlayerUIUtil.toCanonicalName(playerName);
			playerScoreMap.put(playerName, playerIdScoreMap.get(i));
		}
	}

	private void calculateRanking(ArrayList<PlayerScore> list) {
		PlayerScore prevPlayerScore = null;
		int size = list.size();

		int ranking = 1;
		for (PlayerScore playerScore : list) {
			if (prevPlayerScore != null
					&& prevPlayerScore.getScore() == playerScore.getScore()) {
				playerScore.setRanking(prevPlayerScore.getRanking());
			} else {
				playerScore.setRanking(ranking);
			}
			ranking++;
			prevPlayerScore = playerScore;
		}

		for (int i = 0; i < size; i++) {
			PlayerScore playerScore = list.get(i);
			playerScore.setSameRankingCount(0);
			for (int j = 0; j < size; j++) {
				PlayerScore compare = list.get(j);
				if (compare.getRanking() == playerScore.getRanking()) {
					playerScore.increaseSameRankingCount(1);
				}
			}
		}

		for (int i = 0; i < size; i++) {
			PlayerScore playerScore = list.get(i);
			if (playerScore.getRanking() + playerScore.getSameRankingCount()
					- 1 > size - 1) {
				playerScore.setLastStanding(true);
			} else {
				playerScore.setLastStanding(false);
			}
		}
	}

	private void calculateRankingFee(ArrayList<PlayerScore> list,
			GameSetting gameSetting) {
		for (PlayerScore playerScore : list) {
			int sum = 0;
			int sameRankingCount = playerScore.getSameRankingCount();

			for (int i = 0; i < sameRankingCount; i++) {
				sum += gameSetting.getRankingFeeForRanking(playerScore
						.getRanking() + i);
			}
			playerScore.setOriginalRankingFee(FeeCalculator.ceil(sum,
					sameRankingCount));
			playerScore.setAdjustedRankingFee(playerScore
					.getOriginalRankingFee());
		}
	}

	private void calculateAvgRanking(ArrayList<PlayerScore> list) {
		for (PlayerScore playerScore : list) {
			if (currentHole < 1) {
				playerScore.setAvgRanking(0F);
			} else {
				playerScore.setAvgRanking((float) playerScore
						.getHoleRankingSum() / (float) currentHole);
			}
		}
	}

	private void calculateOverPar(ArrayList<PlayerScore> list) {
		for (PlayerScore playerScore : list) {
			if (currentHole < 1) {
				playerScore.setAvgOverPar(0F);
			} else {
				playerScore.setAvgOverPar((float) playerScore.getScore()
						/ (float) currentHole);
			}
		}
	}

	private void adjustFee(ArrayList<PlayerScore> list,
			GameSetting gameSetting, boolean isGameFinished) {
		int sumOfHoleFee = 0;
		int sumOfRankingFee = 0;
		for (PlayerScore playerScore : list) {
			sumOfHoleFee += playerScore.getOriginalHoleFee();
			sumOfRankingFee += playerScore.getOriginalRankingFee();

			playerScore.setOriginalTotalFee(playerScore.getOriginalHoleFee()
					+ playerScore.getOriginalRankingFee());
		}

		int rankingFee = gameSetting.getRankingFee();
		int holeFee = gameSetting.getHoleFee();

		int remainOfHoleFee = 0;
		if (sumOfHoleFee < holeFee && isGameFinished) {
			int additionalHoleFeePerPlayer = FeeCalculator.ceil(holeFee
					- sumOfHoleFee, gameSetting.getPlayerCount());

			int sum = 0;
			for (PlayerScore playerScore : list) {
				playerScore.increaseAdjustedHoleFee(additionalHoleFeePerPlayer);
				sum += playerScore.getAdjustedHoleFee();
			}
			remainOfHoleFee = sum - holeFee;
		} else if (sumOfHoleFee > holeFee) {
			remainOfHoleFee = sumOfHoleFee - holeFee;
		}

		int remainOfRankingFee = 0;
		if (sumOfRankingFee < rankingFee) {
			int additionalRankingFeePerPlayer = FeeCalculator.ceil(rankingFee
					- sumOfRankingFee, gameSetting.getPlayerCount());

			int sum = 0;
			for (PlayerScore playerScore : list) {
				playerScore
						.increaseAdjustedRankingFee(additionalRankingFeePerPlayer);
				sum += playerScore.getAdjustedRankingFee();
			}
			remainOfRankingFee = sum - rankingFee;
		} else if (sumOfRankingFee > rankingFee) {
			remainOfRankingFee = sumOfRankingFee - rankingFee;
		}

		PlayerScore lastSinglePlayerScore = null;
		for (PlayerScore playerScore : list) {
			if (playerScore.getSameRankingCount() <= 1) {
				lastSinglePlayerScore = playerScore;
			}
		}
		if (remainOfHoleFee > 0 || remainOfRankingFee > 0) {
			if (lastSinglePlayerScore != null) {
				lastSinglePlayerScore
						.decreaseAdjustedRankingFee(remainOfHoleFee
								+ remainOfRankingFee);
			} else {
				// 이 경우 어떻게 해야 할까요?
			}
		}

		sumOfHoleFee = 0;
		sumOfRankingFee = 0;
		for (PlayerScore playerScore : list) {
			sumOfHoleFee += playerScore.getAdjustedHoleFee();
			sumOfRankingFee += playerScore.getAdjustedRankingFee();

			playerScore.setAdjustedTotalFee(playerScore.getAdjustedHoleFee()
					+ playerScore.getAdjustedRankingFee());
		}
	}

	public int getHoleCount() {
		return holeCount;
	}

	@Override
	public int compareTo(GameAndResult compare) {
		Date date1 = gameSetting.getDate();
		Date date2 = compare.gameSetting.getDate();
		return date2.compareTo(date1);
	}

	public GameSetting getGameSetting() {
		return gameSetting;
	}

	public PlayerSetting getPlayerSetting() {
		return playerSetting;
	}

	public boolean containsPlayerScore(String playerName) {
		String canonicalName = PlayerUIUtil.toCanonicalName(playerName);
		return playerScoreMap.containsKey(canonicalName);
	}

	public PlayerScore getPlayerScore(String playerName) {
		String canonicalName = PlayerUIUtil.toCanonicalName(playerName);
		if (!playerScoreMap.containsKey(canonicalName))
			return null;
		return playerScoreMap.get(canonicalName);
	}

	@Override
	public int getPlayerCount() {
		if (gameSetting == null)
			return 0;

		return gameSetting.getPlayerCount();
	}

	@Override
	public boolean containsPlayer(String playerName) {
		if (gameSetting == null || playerSetting == null)
			return false;

		String name1, name2;
		name1 = PlayerUIUtil.toCanonicalName(playerName);
		int count = getPlayerCount();
		for (int playerId = 0; playerId < count; playerId++) {
			name2 = playerSetting.getPlayerName(playerId);
			name2 = PlayerUIUtil.toCanonicalName(name2);
			if (name1.equalsIgnoreCase(name2))
				return true;
		}

		return false;
	}

	@Override
	public int getRanking(String playerName) {
		if (!playerScoreMap.containsKey(playerName))
			return 0;

		PlayerScore playerScore = playerScoreMap.get(playerName);
		return playerScore.getRanking();
	}

	@Override
	public int getEighteenHoleScore(String playerName) {
		if (!playerScoreMap.containsKey(playerName))
			return 0;

		holeCount = getHoleCount();
		PlayerScore playerScore = playerScoreMap.get(playerName);
		int score = playerScore.getOriginalScore();
		if (holeCount != 18)
			score *= 2;
		return score;
	}

	@Override
	public int getEighteenHoleFee(String playerName) {
		if (!playerScoreMap.containsKey(playerName))
			return 0;

		PlayerScore playerScore = playerScoreMap.get(playerName);
		int fee = playerScore.getAdjustedTotalFee();
		if (holeCount != 18)
			fee *= 2;
		return fee;
	}

	@Override
	public Date getDate() {
		return gameSetting.getDate();
	}
}
