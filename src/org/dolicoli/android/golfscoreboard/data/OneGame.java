package org.dolicoli.android.golfscoreboard.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

public class OneGame implements GameResultItem, Comparable<OneGame> {

	private GameSetting gameSetting;
	private PlayerSetting playerSetting;
	private ArrayList<Result> results;

	private SparseArray<HashMap<String, OneHolePlayerScore>> holePlayerScores;
	private HashMap<String, PlayerScore> totalPlayerScoreMap;

	private int currentHole;

	public OneGame(GameSetting gs, PlayerSetting ps) {
		this(gs, ps, null);
	}

	public OneGame(GameSetting gs, PlayerSetting ps, Iterable<Result> rs) {
		this.holePlayerScores = new SparseArray<HashMap<String, OneHolePlayerScore>>();
		this.totalPlayerScoreMap = new HashMap<String, PlayerScore>();
		this.gameSetting = gs;
		this.playerSetting = ps;
		this.results = new ArrayList<Result>();

		initializePlayerScoreMap();

		if (rs != null) {
			for (Result r : rs) {
				results.add(r);
			}
			results.trimToSize();

			Collections.sort(results, new Comparator<Result>() {
				@Override
				public int compare(Result lhs, Result rhs) {
					int leftHoleNumber = lhs.getHoleNumber();
					int rightHoleNumber = rhs.getHoleNumber();

					if (leftHoleNumber < rightHoleNumber)
						return -1;
					if (leftHoleNumber > rightHoleNumber)
						return 1;
					return 0;
				}
			});

			calculateHoleFee();
			calculateTotalRanking();
		}
	}

	private void initializePlayerScoreMap() {
		int playerCount = gameSetting.getPlayerCount();
		for (int playerId = 0; playerId < playerCount; playerId++) {
			String playerName = playerSetting.getPlayerName(playerId);
			String canonicalPlayerName = PlayerUIUtil
					.toCanonicalName(playerName);
			PlayerScore playerScore = new PlayerScore(playerId,
					playerSetting.getPlayerName(playerId),
					playerSetting.getHandicap(playerId),
					playerSetting.getExtraScore(playerId));
			totalPlayerScoreMap.put(canonicalPlayerName, playerScore);
		}
	}

	@Override
	public int compareTo(OneGame another) {
		Date date1 = gameSetting.getDate();
		Date date2 = another.gameSetting.getDate();
		return date2.compareTo(date1);
	}

	@Override
	public boolean containsPlayer(String playerName) {
		String canonicalName = PlayerUIUtil.toCanonicalName(playerName);
		return totalPlayerScoreMap.containsKey(canonicalName);
	}

	@Override
	public int getRanking(String playerName) {
		String canonicalName = PlayerUIUtil.toCanonicalName(playerName);
		if (!totalPlayerScoreMap.containsKey(canonicalName))
			return 0;

		PlayerScore playerScore = totalPlayerScoreMap.get(canonicalName);
		return playerScore.getRanking();
	}

	@Override
	public int getEighteenHoleScore(String playerName) {
		String canonicalName = PlayerUIUtil.toCanonicalName(playerName);
		if (!totalPlayerScoreMap.containsKey(canonicalName))
			return 0;

		int holeCount = getHoleCount();
		PlayerScore playerScore = totalPlayerScoreMap.get(canonicalName);
		int score = playerScore.getOriginalScore();
		return score * 18 / holeCount;
	}

	@Override
	public int getEighteenHoleFee(String playerName) {
		String canonicalName = PlayerUIUtil.toCanonicalName(playerName);
		if (!totalPlayerScoreMap.containsKey(canonicalName))
			return 0;

		int holeCount = getHoleCount();
		PlayerScore playerScore = totalPlayerScoreMap.get(canonicalName);
		int fee = playerScore.getAdjustedTotalFee();
		return fee * 18 / holeCount;
	}

	@Override
	public int getHoleCount() {
		if (gameSetting == null)
			return 0;
		return gameSetting.getHoleCount();
	}

	@Override
	public int getPlayerCount() {
		if (gameSetting == null)
			return 0;
		return gameSetting.getPlayerCount();
	}

	@Override
	public Date getDate() {
		if (gameSetting == null)
			return null;
		return gameSetting.getDate();
	}

	public String getPlayDate() {
		if (gameSetting == null)
			return null;
		return gameSetting.getPlayDate();
	}

	public String getPlayerName(int playerId) {
		return playerSetting.getPlayerName(playerId);
	}

	public String getCanonicalPlayerName(int playerId) {
		String playerName = getPlayerName(playerId);
		return PlayerUIUtil.toCanonicalName(playerName);
	}

	// TODO: Maybe remove it !!!
	public Iterable<Result> getResults() {
		return results;
	}

	public int getRankingFee() {
		return gameSetting.getRankingFee();
	}

	public int getHoleFee() {
		return gameSetting.getHoleFee();
	}

	public int getTotalFee() {
		return gameSetting.getTotalFee();
	}

	public int getRankingFeeForRanking(int ranking) {
		return gameSetting.getRankingFeeForRanking(ranking);
	}

	public int getHoleFeeForRanking(int ranking) {
		return gameSetting.getHoleFeeForRanking(ranking);
	}

	@Override
	public String toString() {
		if (gameSetting == null)
			return "OneGame @ UNKNOWN";

		Date date = gameSetting.getDate();
		return "OneGame @ " + date.toString();
	}

	public int getCurrentHole() {
		return currentHole;
	}

	public boolean containsPlayerScore(String playerName) {
		return containsPlayer(playerName);
	}

	public PlayerScore getPlayerScore(int playerId) {
		return getPlayerScore(getPlayerName(playerId));
	}

	public PlayerScore getPlayerScore(String playerName) {
		String canonicalName = PlayerUIUtil.toCanonicalName(playerName);
		if (!totalPlayerScoreMap.containsKey(canonicalName))
			return null;
		return totalPlayerScoreMap.get(canonicalName);
	}

	public OneHolePlayerScore getHolePlayerScore(int hole, int playerId) {
		HashMap<String, OneHolePlayerScore> map = holePlayerScores.get(hole,
				null);
		if (map == null)
			return null;

		return map.get(getCanonicalPlayerName(playerId));
	}

	private void calculateHoleFee() {
		if (gameSetting == null || playerSetting == null || results == null
				|| results.size() < 1)
			return;

		int playerCount = gameSetting.getPlayerCount();
		currentHole = 0;
		for (Result result : results) {
			int holeNumber = result.getHoleNumber();
			if (currentHole < holeNumber)
				currentHole = holeNumber;

			HashMap<String, OneHolePlayerScore> holePlayerScoreMap = holePlayerScores
					.get(holeNumber, null);
			if (holePlayerScoreMap == null) {
				holePlayerScoreMap = new HashMap<String, OneHolePlayerScore>();
				holePlayerScores.put(holeNumber, holePlayerScoreMap);
			}

			int[] fees = FeeCalculator.calculateFee(gameSetting, result);

			for (int playerId = 0; playerId < playerCount; playerId++) {
				String playerName = playerSetting.getPlayerName(playerId);
				String canonicalPlayerName = PlayerUIUtil
						.toCanonicalName(playerName);

				OneHolePlayerScore oneHolePlayerScore = new OneHolePlayerScore(
						holeNumber, result.getParNumber(), playerId,
						playerName, result.getOriginalScore(playerId),
						result.getUsedHandicap(playerId),
						result.getRanking(playerId),
						result.getSameRankingCount(playerId), fees[playerId]);
				holePlayerScoreMap.put(canonicalPlayerName, oneHolePlayerScore);
			}
		}
	}

	private void calculateTotalRanking() {
		if (gameSetting == null || playerSetting == null || results == null
				|| results.size() < 1)
			return;

		int playerCount = gameSetting.getPlayerCount();

		for (int holeNumber = 1; holeNumber <= currentHole; holeNumber++) {
			HashMap<String, OneHolePlayerScore> holePlayerScoreMap = holePlayerScores
					.get(holeNumber);

			for (int playerId = 0; playerId < playerCount; playerId++) {
				String playerName = playerSetting.getPlayerName(playerId);
				String canonicalPlayerName = PlayerUIUtil
						.toCanonicalName(playerName);
				PlayerScore playerScore = totalPlayerScoreMap
						.get(canonicalPlayerName);

				OneHolePlayerScore holePlayerScore = holePlayerScoreMap
						.get(canonicalPlayerName);

				playerScore.increaseHoleCount();
				playerScore.increaseScore(holePlayerScore.getOriginalScore(),
						holePlayerScore.getUsedHandicap());
				playerScore.increaseOriginalHoleFee(holePlayerScore.getFee());
				playerScore
						.setAdjustedHoleFee(playerScore.getOriginalHoleFee());
				playerScore.addHoleRanking(playerScore.getRanking());

			}
		}

		Collection<PlayerScore> playerScores = totalPlayerScoreMap.values();
		PlayerScore[] playerScoreArray = new PlayerScore[playerScores.size()];
		playerScores.toArray(playerScoreArray);

		Arrays.sort(playerScoreArray);
		calculateRanking(playerScoreArray);
		calculateRankingFee(playerScoreArray, gameSetting);
		for (PlayerScore playerScore : playerScoreArray) {
			playerScore.calculateAvgRanking(currentHole);
			playerScore.calculateOverPar(currentHole);
		}

		adjustFee(playerScoreArray, gameSetting,
				currentHole >= gameSetting.getHoleCount());

		Arrays.sort(playerScoreArray); // Sort again after adjustFee has been
										// set
	}

	private void calculateRanking(PlayerScore[] array) {
		int size = array.length;

		int ranking = 1;
		PlayerScore prevPlayerScore = null;
		for (PlayerScore playerScore : array) {
			if (prevPlayerScore != null
					&& prevPlayerScore.getFinalScore() == playerScore
							.getFinalScore()) {
				playerScore.setRanking(prevPlayerScore.getRanking());
			} else {
				playerScore.setRanking(ranking);
			}
			ranking++;
			prevPlayerScore = playerScore;
		}

		for (int i = 0; i < size; i++) {
			PlayerScore playerScore = array[i];
			playerScore.setSameRankingCount(0);
			for (int j = 0; j < size; j++) {
				PlayerScore compare = array[j];
				if (compare.getRanking() == playerScore.getRanking()) {
					playerScore.increaseSameRankingCount(1);
				}
			}
		}

		for (int i = 0; i < size; i++) {
			PlayerScore playerScore = array[i];
			if (playerScore.getRanking() + playerScore.getSameRankingCount()
					- 1 > size - 1) {
				playerScore.setLastStanding(true);
			} else {
				playerScore.setLastStanding(false);
			}
		}
	}

	private void calculateRankingFee(PlayerScore[] array,
			GameSetting gameSetting) {
		for (PlayerScore playerScore : array) {
			int sum = 0;
			int ranking = playerScore.getRanking();
			int sameRankingCount = playerScore.getSameRankingCount();

			for (int i = 0; i < sameRankingCount; i++) {
				sum += gameSetting.getRankingFeeForRanking(ranking + i);
			}
			playerScore.setOriginalRankingFee(FeeCalculator.ceil(sum,
					sameRankingCount));
			playerScore.setAdjustedRankingFee(playerScore
					.getOriginalRankingFee());
		}
	}

	private void adjustFee(PlayerScore[] array, GameSetting gameSetting,
			boolean isGameFinished) {
		int sumOfHoleFee = 0;
		int sumOfRankingFee = 0;
		for (PlayerScore playerScore : array) {
			sumOfHoleFee += playerScore.getOriginalHoleFee();
			sumOfRankingFee += playerScore.getOriginalRankingFee();
		}

		int rankingFee = gameSetting.getRankingFee();
		int holeFee = gameSetting.getHoleFee();

		int remainOfHoleFee = 0;
		if (sumOfHoleFee < holeFee && isGameFinished) {
			int additionalHoleFeePerPlayer = FeeCalculator.ceil(holeFee
					- sumOfHoleFee, gameSetting.getPlayerCount());

			int sum = 0;
			for (PlayerScore playerScore : array) {
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
			for (PlayerScore playerScore : array) {
				playerScore
						.increaseAdjustedRankingFee(additionalRankingFeePerPlayer);
				sum += playerScore.getAdjustedRankingFee();
			}
			remainOfRankingFee = sum - rankingFee;
		} else if (sumOfRankingFee > rankingFee) {
			remainOfRankingFee = sumOfRankingFee - rankingFee;
		}

		PlayerScore lastSinglePlayerScore = null;
		for (PlayerScore playerScore : array) {
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
		for (PlayerScore playerScore : array) {
			sumOfHoleFee += playerScore.getAdjustedHoleFee();
			sumOfRankingFee += playerScore.getAdjustedRankingFee();

			playerScore.setAdjustedTotalFee(playerScore.getAdjustedHoleFee()
					+ playerScore.getAdjustedRankingFee());
		}
	}
}
