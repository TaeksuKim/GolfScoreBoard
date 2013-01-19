package org.dolicoli.android.golfscoreboard.utils;

import org.dolicoli.android.golfscoreboard.data.RankingContainer;
import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;

public class FeeCalculator {

	public static int[] calculateFee(GameSetting gameSetting,
			RankingContainer container) {

		int playerCount = gameSetting.getPlayerCount();
		int[] fees = new int[playerCount];
		for (int playerId = 0; playerId < playerCount; playerId++) {
			int ranking = container.getRanking(playerId);
			int sameRankingPlayerCount = container
					.getSameRankingCount(playerId);

			int partialFee = 0;
			for (int i = ranking; i < ranking + sameRankingPlayerCount; i++) {
				partialFee += gameSetting.getHoleFeeForRanking(i);
			}

			fees[playerId] = toFee(partialFee, sameRankingPlayerCount);
		}
		return fees;
	}

	public static int toFee(int a, int b) {
		return floor(a, b);
	}

	public static int floor(int a, int b) {
		double f = ((double) a) / ((double) b);
		return (int) (Math.floor(f / 100.0) * 100.0);
	}

	public static int ceil(int a, int b) {
		double f = ((double) a) / ((double) b);
		return (int) (Math.ceil(f / 100.0) * 100.0);
	}
}
